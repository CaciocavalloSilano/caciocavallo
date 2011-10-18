/*
 * Copyright (c) 2011, Clemens Eisserer, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package net.java.openjdk.awt.peer.web;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.concurrent.locks.*;

import javax.servlet.http.*;

import net.java.openjdk.cacio.servlet.*;
import net.java.openjdk.cacio.servlet.transport.*;
import sun.awt.*;
import sun.awt.peer.cacio.*;
import sun.awt.peer.cacio.managed.*;
import sun.java2d.*;

/**
 * Screen Implementation for Caciocavallo-Web.
 * 
 * WebScreen is basically the "hub" where all things come together. Each
 * Caciocavallo-Web session has a single screen, which itself consists of a
 * WebSurfaceData.
 * 
 * @author Clemens Eisserer <linuxhippy@gmail.com>
 */
public class WebScreen implements PlatformScreen {

    private int width;
    private int height;

    WebGraphicsConfiguration config;

    ReentrantLock screenLock;
    Condition screenCondition;

    ArrayList<ScreenUpdate> pendingUpdateList;
    Transport encoder;

    private EventData eventData;
    private WebSurfaceData surfaceData;

    protected WebScreen(WebGraphicsConfiguration config) {
	this.config = config;

	WebSessionState state = WebSessionManager.getInstance().getCurrentStateAWT();
	width = state.getInitialScreenDimension().width;
	width = width > 0 ? width : 1;
	height = height > 0 ? height : 1;

	height = state.getInitialScreenDimension().height;

	screenLock = new ReentrantLock();
	screenCondition = screenLock.newCondition();
	pendingUpdateList = new ArrayList<ScreenUpdate>();
    }

    public Graphics2D getClippedGraphics(Color fg, Color bg, Font font, List<Rectangle> clipRects) {

	WebSurfaceData data = getSurfaceData();
	Graphics2D g2d = new SunGraphics2D(data, fg, bg, font);
	if (clipRects != null && clipRects.size() > 0) {
	    Area a = new Area(getBounds());
	    for (Rectangle clip : clipRects) {
		a.subtract(new Area(clip));
	    }
	    g2d = new WindowClippedGraphics(g2d, a);
	}
	return g2d;
    }

    public ColorModel getColorModel() {
	return getGraphicsConfiguration().getColorModel();
    }

    public GraphicsConfiguration getGraphicsConfiguration() {
	return config;
    }

    public Rectangle getBounds() {
	return new Rectangle(0, 0, width, height);
    }

    /**
     * Dispatch an event received by a servlet.
     * 
     * @param data
     */
    public void dispatchEvent(EventData data) {
	try {
	    SunToolkit.awtLock();

	    WebToolkit toolkit = ((WebToolkit) WebToolkit.getDefaultToolkit());
	    WebWindowFactory factory = (WebWindowFactory) toolkit.getPlatformWindowFactory();
	    ScreenManagedWindowContainer windowContainer = factory.getScreenManagedWindowContainer(this);
	    if (windowContainer != null) {
		data.setSource(windowContainer);
		windowContainer.dispatchEvent(data);
	    }
	} finally {
	    SunToolkit.awtUnlock();
	}

    }

    /**
     * Get the WebSurfaceData, or initialize one if it doesn't yet exist.
     * 
     * @return
     */
    public WebSurfaceData getSurfaceData() {
	if (surfaceData == null) {
	    surfaceData = new WebSurfaceData(this, WebSurfaceData.typeDefault, getColorModel(), getBounds(), getGraphicsConfiguration(), this);
	}
	return surfaceData;
    }

    private native final long nativeInitScreen(int width, int height);

    public WebGraphicsConfiguration getConfig() {
	return config;
    }

    /**
     * Resizes the screen by discarding the current WebSurfaceData inculding all
     * pending ScreenUpdates, initializing a WebSurfaceData with the requested
     * size, and issuing a repaint command for all Windows.
     * 
     * @param width
     * @param height
     */
    public void resizeScreen(int width, int height) {
	try {
	    SunToolkit.awtLock();
	    lockScreen();
	    this.width = width;
	    this.height = height;

	    surfaceData = new WebSurfaceData(this, WebSurfaceData.typeDefault, getColorModel(), getBounds(), getGraphicsConfiguration(), this);
	    ((WebWindowFactory) ((WebToolkit) WebToolkit.getDefaultToolkit()).getPlatformWindowFactory()).repaintScreen(this);
	} finally {
	    SunToolkit.awtUnlock();
	    unlockScreen();
	}
    }

    /**
     * Grabs the screen-lock
     */
    public final void lockScreen() {
	screenLock.lock();
    }

    /**
     * Releases the screen-lock
     */
    public final void unlockScreen() {
	screenLock.unlock();
    }

    /**
     * Signall a possible waiting thread
     */
    public final void signalScreen() {
	screenCondition.signal();
    }

    /**
     * Polls the WebScreen for pending updates. - Returns immediatly if pending
     * updates are available - Waits up to timeout seconds, of no updates are
     * available.
     * 
     * @param response
     *            - the HttpServletResponse the updates will be written to
     * @param timeout
     * @throws IOException
     */
    public Transport pollForScreenUpdates(int timeout) throws IOException {
	
	boolean updatesWritten = false;
	try {
	    lockScreen();
	    updatesWritten = prepareScreenUpdates();

	    if (!updatesWritten) {
		try {
		    boolean signalled = screenCondition.await(timeout, TimeUnit.MILLISECONDS);

		    /*
		     * If we had to wait, we quite likely have been waked by the
		     * first operation. Usually (e.g. Swing) many draw-commands
		     * are executed closely together, so we wait just a little
		     * longer, so we can send a larger batch down.
		     * In order to allow the rendering thread to do its job, we have
		     * to unlock the screen however.
		     */
		    if (signalled) {
			unlockScreen();
			Thread.sleep(10);
			lockScreen();
		    }
		} catch (InterruptedException e) {
		    e.printStackTrace();
		}

		updatesWritten = prepareScreenUpdates();
	    }
	} finally {
	    unlockScreen();
	}

	return encoder;
    }


    /**
     * If ScreenUpdates are pending, preserve/encode pending them,
     * so the ScreenLock can be released and rendering can continue.
     * 
     * @param os
     *            the OUtputStream the pending updates are written to
     * @return true if updates have been written, false if no updates were
     *         pending.
     * @throws IOException
     */
    protected boolean prepareScreenUpdates() {
	if (surfaceData == null) {
	    return false;
	}

	try {
	    lockScreen();
	    
	    encoder = WebSessionManager.getInstance().getCurrentState().getBackend();
	    
	    List<ScreenUpdate> screenUpdates = surfaceData.fetchPendingSurfaceUpdates();
	    if (screenUpdates != null) {
		pendingUpdateList.addAll(screenUpdates);
	    }

	    if (pendingUpdateList.size() > 0) {
		ArrayList<Integer> cmdList = new ArrayList<Integer>(pendingUpdateList.size() * 7);

		// Refactor
		TreeImagePacker packer = new TreeImagePacker();
		packer.insertScreenUpdateList(pendingUpdateList);
		for (ScreenUpdate update : pendingUpdateList) {
		    // System.out.println(update);
		    update.writeToCmdStream(cmdList);
		}

		// Write updates to us
		encoder.prepareUpdate(pendingUpdateList, packer, cmdList);
		pendingUpdateList.clear();

		return true;
	    }
	} finally {
	    unlockScreen();
	}

	return false;
    }

    public ArrayList<ScreenUpdate> getPendingUpdateList() {
	return pendingUpdateList;
    }
}

// try {
// BinaryRLEStreamEncoder rleEncoder = new
// BinaryRLEStreamEncoder();
// FileOutputStream fos = new
// FileOutputStream("/home/ce/imgFiles/" + cnt + ".rle");
// rleEncoder.writeEnocdedData(fos, pendingUpdateList,
// packer, cmdList);
// fos.close();
//
// BinaryCmdStreamEncoder binEncoder = new
// BinaryPngStreamEncoder();
// FileOutputStream dos = new
// FileOutputStream("/home/ce/imgFiles/" + cnt + ".bin");
// binEncoder.writeEnocdedData(dos, pendingUpdateList,
// packer, cmdList);
// dos.close();
//
// Base64CmdStreamEncoder baseCoder = new
// Base64CmdStreamEncoder();
// FileOutputStream dbos = new
// FileOutputStream("/home/ce/imgFiles/" + cnt + ".base64");
// baseCoder.writeEnocdedData(dbos, pendingUpdateList,
// packer, cmdList);
// dbos.close();
//
// ImageCmdStreamEncoder imgEncoder = new
// ImageCmdStreamEncoder();
// FileOutputStream bos = new
// FileOutputStream("/home/ce/imgFiles/" + cnt+
// ".png");
// imgEncoder.writeEnocdedData(bos, pendingUpdateList,
// packer, cmdList);
// bos.close();
// //
// cnt++;
//
// Thread.sleep(50);
// if (false)
// throw new IOException();
// } catch (Exception ex) {
// ex.printStackTrace();
// }
