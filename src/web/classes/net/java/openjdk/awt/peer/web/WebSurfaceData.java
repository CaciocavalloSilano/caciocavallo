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

import java.awt.*;

import java.awt.image.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.locks.*;

import javax.servlet.http.*;

import biz.source_code.base64Coder.*;

import com.keypoint.*;

import net.java.openjdk.cacio.servlet.*;

import sun.java2d.*;
import sun.java2d.loops.*;
import sun.java2d.pipe.*;

/**
 * SurfaceData implementation based on libSDL.
 * 
 * @author Mario Torre <neugens.limasoftware@gmail.com>
 */
public class WebSurfaceData extends SurfaceData {

    static SurfaceType typeDefault = SurfaceType.IntRgb.deriveSubType("Cacio Web default");

    static {
	initIDs();
    }

    public BufferedImage imgBuffer;
    Graphics bufferGraphics;

    private Rectangle bounds;
    private GraphicsConfiguration configuration;
    private Object destination;
    int[] data;

    ReentrantLock surfaceLock = new ReentrantLock();

    ArrayList<ScreenUpdate> pendingUpdateList = new ArrayList<ScreenUpdate>();
    GridDamageTracker damageTracker;
    
    CmdStreamEncoder encoder;

    protected WebSurfaceData(SurfaceType surfaceType, ColorModel cm, Rectangle b, GraphicsConfiguration gc, Object dest) {

	super(surfaceType, cm);

	bounds = b;
	configuration = gc;
	destination = dest;

	encoder = new BinaryRLEStreamEncoder();
	damageTracker = new GridDamageTracker(b.width, b.height);
	imgBuffer = new BufferedImage(b.width, b.height, BufferedImage.TYPE_INT_RGB);
	bufferGraphics = imgBuffer.getGraphics();
	bufferGraphics.setColor(Color.WHITE);
	bufferGraphics.fillRect(0, 0, b.width, b.height);
	data = ((DataBufferInt) imgBuffer.getRaster().getDataBuffer()).getData();

	initOps(data, b.width, b.height, 0);
    }

    @Override
    public SurfaceData getReplacement() {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public GraphicsConfiguration getDeviceConfiguration() {
	return configuration;
    }

    @Override
    public Raster getRaster(int arg0, int arg1, int arg2, int arg3) {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Rectangle getBounds() {
	return new Rectangle(bounds);
    }

    @Override
    public Object getDestination() {
	return destination;
    }

    private native final void initOps(int[] data, int width, int height, int stride);

    private static final native void initIDs();

    int cnt = 0;

    public void lockSurface() {
	surfaceLock.lock();
	cnt++;
    }

    public void unlockSurface() {
	surfaceLock.unlock();
    }

    public void addDirtyRectAndUnlock(int x1, int x2, int y1, int y2) {
	DamageRect rect = new DamageRect(x1, y1, x2, y2);
	damageTracker.trackDamageRect(rect);
	surfaceLock.unlock();
    }

    // @Override
    // public boolean copyArea(SunGraphics2D sg2d, int x, int y, int w, int h,
    // int dx, int dy) {
    // Region clipRect = sg2d.getCompClip();
    // CompositeType comptype = sg2d.imageComp;
    //
    // if (clipRect.isRectangular() && sg2d.transformState <
    // sg2d.TRANSFORM_TRANSLATESCALE
    // && (CompositeType.SrcOverNoEa.equals(comptype) ||
    // CompositeType.SrcNoEa.equals(comptype))) {
    
//    evacuateDamagedAreas(); 
    
    //
    // x += sg2d.transX;
    // y += sg2d.transY;
    //
    // // TODO: For now we use a temporary Buffer, improve it
    // BufferedImage tmpImg = new BufferedImage(w, h,
    // BufferedImage.TYPE_INT_RGB);
    // Graphics tmpG = tmpImg.getGraphics();
    // tmpG.drawImage(imgBuffer, 0, 0, w, h, x, y, x + w, y + h, null);
    // Graphics g = imgBuffer.getGraphics();
    // if (clipRect != null) {
    // g.setClip(clipRect.getLoX(), clipRect.getLoY(), clipRect.getWidth(),
    // clipRect.getHeight());
    // }
    //
    // try {
    // lockSurface();
    // int xdx = x + dx;
    // int ydy = y + dy;
    //
    // g.drawImage(tmpImg, xdx, ydy, null);
    // // persistDamagedAreas();
    // // pendingUpdateList.add(new CopyAreaScreenUpdate(xdx, ydy, w,
    // // h, dx, dy));
    // } finally {
    // unlockSurface();
    // }
    //
    // return true;
    // }
    //
    // return false;
    // }
    
    protected void persistDamagedAreas() {
	try {
	    lockSurface();

	    DamageRect unionRect = damageTracker.getUnionRectangle();
	    if (unionRect != null) {
		List<DamageRect> regionList = damageTracker.createDamagedRegionList(3);
		
		TreeImagePacker packer = new TreeImagePacker(regionList);
		DamageRect packedRegionBox = packer.getBoundingBox();

		if (unionRect != null && unionRect.getWidth() > 0 && unionRect.getHeight() > 0 && packedRegionBox != null) {

		    if (false || !packer.isPackingEfficient(packedRegionBox, unionRect)) {
			pendingUpdateList.add(new BlitScreenUpdate(unionRect.getX1(), unionRect.getY1(), unionRect.getX1(), unionRect.getY1(),
				unionRect.getWidth(), unionRect.getHeight(), imgBuffer));
		    } else {
			for (DamageRect dRect : regionList) {
			    pendingUpdateList.add(new BlitScreenUpdate(dRect.getX1(), dRect.getY1(), dRect.getX1(), dRect.getY1(), dRect.getWidth(),
				    dRect.getHeight(), imgBuffer));
			}
		    }
		}

		damageTracker.reset();
	    }
	} finally {
	    unlockSurface();
	}
    }
    
    protected void evacuateDamagedAreas() {
	for (ScreenUpdate update : pendingUpdateList) {
	    if (update instanceof BlitScreenUpdate) {
		((BlitScreenUpdate) update).evacuate();
	    }
	}
    }
    
    public boolean pollForScreenUpdates(HttpServletResponse response, int timeout, int pollPause) throws IOException {
	
	int pollCnt = timeout / pollPause; 
	boolean updatesWritten = false;

	try {
		Thread.sleep(25);
		
        	while (pollCnt >= 0 && !updatesWritten) {
        	    updatesWritten = writeScreenUpdates(response);
        	    if(!updatesWritten) {
        		    cnt++;
        		    Thread.sleep(pollPause);
        	    }
        	}
	    } catch (InterruptedException e) {
		e.printStackTrace();
	    }
	
	if(!updatesWritten) {
	    encoder.writeEmptyData(response);
	}
	
	return updatesWritten;
    }

    public boolean writeScreenUpdates(HttpServletResponse response) throws IOException {
	long start = System.currentTimeMillis();
	
	// Merge all ScreenUpdates into one texture & encode command stream
	try {
	    lockSurface();
	    persistDamagedAreas();
	  
	    if (pendingUpdateList.size() > 0) {
		ArrayList<Integer> cmdList = new ArrayList<Integer>(pendingUpdateList.size()*7);

		//Refactor
		TreeImagePacker packer = new TreeImagePacker();
		for (ScreenUpdate update : pendingUpdateList) {
		    if (update instanceof BlitScreenUpdate) {
			BlitScreenUpdate bsUpdate = (BlitScreenUpdate) update;
			packer.insert(bsUpdate);
		    }

		    update.writeCmdStream(cmdList);
		}

		encoder.writeEnocdedData(response, pendingUpdateList, packer, cmdList);
		//Write updates here
		    
		pendingUpdateList.clear();

		long end = System.currentTimeMillis();
		System.out.println("Total Took: "+(end-start));
		System.out.println();
		
		return true;
	    }
	} finally {
	    unlockSurface();
	}

	return false;
    }
}


//try {
////    FileOutputStream fos = new FileOutputStream("/home/ce/imgFiles/" + cnt + ".base64");
////    fos.write(bData);
////    fos.close();
//    
//    FileOutputStream dos = new FileOutputStream("/home/ce/imgFiles/" + cnt + ".bin");
//    dos.write(binData);
//    dos.close();
//
////    FileOutputStream bos = new FileOutputStream("/home/ce/imgFiles/" + cnt + ".bmp");
////    bos.write(imgData);
////    bos.close();
//    
//    return true;
