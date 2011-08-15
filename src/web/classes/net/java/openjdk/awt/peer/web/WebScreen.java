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
import java.util.concurrent.locks.*;

import javax.servlet.http.*;

import net.java.openjdk.cacio.servlet.*;
import net.java.openjdk.cacio.servlet.transport.*;
import sun.awt.*;
import sun.awt.peer.cacio.*;
import sun.awt.peer.cacio.managed.*;
import sun.java2d.*;

/**
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
	height = state.getInitialScreenDimension().height;

	screenLock = new ReentrantLock();
	screenCondition = screenLock.newCondition();
	pendingUpdateList = new ArrayList<ScreenUpdate>();
	encoder = state.getBackend();
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

    public void addEvent(EventData data) {
	try {
	    SunToolkit.awtLock();

	    WebToolkit toolkit = ((WebToolkit) WebToolkit.getDefaultToolkit());
	    WebWindowFactory factory = (WebWindowFactory) toolkit.getPlatformWindowFactory();
	    ScreenManagedWindowContainer windowContainer = factory.getScreenManagedWindowContainer(this);
	    data.setSource(windowContainer);
	    windowContainer.dispatchEvent(data);
	} finally {
	    SunToolkit.awtUnlock();
	}

    }

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

    public final void lockScreen() {
	screenLock.lock();
    }

    public final void unlockScreen() {
	screenLock.unlock();
    }

    public final void signalScreen() {
	screenCondition.signal();
    }

    protected void addPendingUpdate(ScreenUpdate update) {
	pendingUpdateList.add(update);
    }

    public void pollForScreenUpdates(HttpServletResponse response, int timeout) throws IOException {
	response.setContentType(encoder.getContentType());
	OutputStream os = response.getOutputStream();

	boolean updatesWritten = false;

	try {
	    lockScreen();
	    updatesWritten = writeScreenUpdates(os);

	    if (!updatesWritten) {
		try {
		    boolean signalled = screenCondition.await(timeout, TimeUnit.MILLISECONDS);
		    
		    /*
		     * If we had to wait, we quite likely have been waked by the first operation.
		     * Usually (e.g. Swing) many draw-commands are executed closely together,
		     * so we wait just a little longer, so we can send a larger batch down.
		     */
		    if(signalled) {
			Thread.sleep(20);
		    }
		} catch (InterruptedException e) {
		    e.printStackTrace();
		}

		updatesWritten = writeScreenUpdates(os);
	    }
	} finally {
	    unlockScreen();
	}
	
	if(!updatesWritten) {
	    encoder.writeEmptyData(os);
	}
    }

    int cnt = 0;

    public boolean writeScreenUpdates(OutputStream os) throws IOException {
	if (surfaceData == null) {
	    return false;
	}

	long start = System.currentTimeMillis();

	try {
	    lockScreen();
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
		    update.writeCmdStream(cmdList);
		}

		try {
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
		    cnt++;
		    //
		    // Thread.sleep(50);
		    if (false)
			throw new IOException();
		} catch (Exception ex) {
		    ex.printStackTrace();
		}

		System.out.println("cmdlist: " + cmdList.size());
		encoder.writeEncodedData(os, pendingUpdateList, packer, cmdList);
		// Write updates here

		pendingUpdateList.clear();

		long end = System.currentTimeMillis();
		System.out.println("Total Took: " + (end - start));
		// System.out.println();

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
