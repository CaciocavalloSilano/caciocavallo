/*
 * Copyright 2009 Sun Microsystems, Inc.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the LICENSE file that accompanied this code.
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
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * CA 95054 USA or visit www.sun.com if you need additional information or
 * have any questions.
 */
package net.java.openjdk.awt.peer.web;

import java.awt.Color;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.awt.image.ColorModel;
import java.io.*;
import java.util.*;
import java.util.concurrent.locks.*;

import javax.servlet.http.*;

import net.java.openjdk.cacio.servlet.*;

import sun.awt.*;
import sun.awt.peer.cacio.WindowClippedGraphics;
import sun.awt.peer.cacio.managed.*;
import sun.java2d.SunGraphics2D;

/**
 * 
 * @author Mario Torre <neugens@limasoftware.net>
 */
public class WebScreen implements PlatformScreen {

    private  int width;
    private  int height;

    WebGraphicsConfiguration config;

    ReentrantLock screenLock;
    ArrayList<ScreenUpdate> pendingUpdateList;
    CmdStreamEncoder encoder;

    private EventData eventData;
    private WebSurfaceData surfaceData;

    protected WebScreen(WebGraphicsConfiguration config) {
	this.config = config;

	//TODO: Replace with bounds determined by initial size-detection page and stored in AppContext
	Dimension dim = FullScreenWindowFactory.getScreenDimension();
	width = dim.width;
	height = dim.height;
	
	screenLock = new ReentrantLock();
	pendingUpdateList = new ArrayList<ScreenUpdate>();
	encoder = new BinaryRLEStreamEncoder();
//	encoder = new BinaryPngStreamEncoder();
//	encoder = new ImageCmdStreamEncoder();
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
	}finally {
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
    
    protected void addPendingUpdate(ScreenUpdate update) {
	    pendingUpdateList.add(update);
    }

    public boolean pollForScreenUpdates(HttpServletResponse response, int timeout, int pollPause) throws IOException {

	int pollCnt = timeout / pollPause;
	boolean updatesWritten = false;

	response.setContentType(encoder.getContentType());
	OutputStream os = response.getOutputStream();

	try {
	    Thread.sleep(10);

	    while (pollCnt >= 0 && !updatesWritten) {
		updatesWritten = writeScreenUpdates(os);
		if (!updatesWritten) {
		    pollCnt++;
		    Thread.sleep(pollPause);
		}
	    }
	} catch (InterruptedException e) {
	    e.printStackTrace();
	}

	if (!updatesWritten) {
	    encoder.writeEmptyData(os);
	}

	return updatesWritten;
    }
    
    int cnt = 0;
    public boolean writeScreenUpdates(OutputStream os) throws IOException {
	if (surfaceData == null) {
	    return false;
	}

	long start = System.currentTimeMillis();

	// Merge all ScreenUpdates into one texture & encode command stream

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
		//    System.out.println(update);
		    update.writeCmdStream(cmdList);
		}

		try {
//		     BinaryRLEStreamEncoder rleEncoder = new
//		     BinaryRLEStreamEncoder();
//		     FileOutputStream fos = new
//		     FileOutputStream("/home/ce/imgFiles/" + cnt + ".rle");
//		     rleEncoder.writeEnocdedData(fos, pendingUpdateList,
//		     packer, cmdList);
//		     fos.close();
		    //
		    // BinaryCmdStreamEncoder binEncoder = new
		    // BinaryPngStreamEncoder();
		    // FileOutputStream dos = new
		    // FileOutputStream("/home/ce/imgFiles/" + cnt + ".bin");
		    // binEncoder.writeEnocdedData(dos, pendingUpdateList,
		    // packer, cmdList);
		    // dos.close();
		    //
//		     Base64CmdStreamEncoder baseCoder = new
//		     Base64CmdStreamEncoder();
//		     FileOutputStream dbos = new
//		     FileOutputStream("/home/ce/imgFiles/" + cnt + ".base64");
//		     baseCoder.writeEnocdedData(dbos, pendingUpdateList,
//		     packer, cmdList);
//		     dbos.close();
		    //
//		     ImageCmdStreamEncoder imgEncoder = new
//		     ImageCmdStreamEncoder();
//		     FileOutputStream bos = new
//		     FileOutputStream("/home/ce/imgFiles/" + cnt+
//		     ".png");
//		     imgEncoder.writeEnocdedData(bos, pendingUpdateList,
//		     packer, cmdList);
//		     bos.close();
//		    //
		    cnt++;
//		    
//		    Thread.sleep(50);
		    if (false)
			throw new IOException();
		} catch (Exception ex) {
		    ex.printStackTrace();
		}

		System.out.println("cmdlist: "+cmdList.size());
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
