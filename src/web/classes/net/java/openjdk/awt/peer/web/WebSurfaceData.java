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

import javax.imageio.*;
import javax.servlet.http.*;

import biz.source_code.base64Coder.*;

import com.keypoint.*;

import net.java.openjdk.cacio.servlet.*;

import sun.awt.image.*;
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
    SurfaceData imgBufferSD;
    Graphics2D bufferGraphics;

    private Rectangle bounds;
    private GraphicsConfiguration configuration;
    private Object destination;
    int[] data;

    GridDamageTracker damageTracker;
    WebScreen screen;

    List<ScreenUpdate> surfaceUpdateList;

    protected WebSurfaceData(WebScreen screen, SurfaceType surfaceType, ColorModel cm, Rectangle b, GraphicsConfiguration gc, Object dest) {

	super(surfaceType, cm);

	this.screen = screen;
	bounds = b;
	configuration = gc;
	destination = dest;

	surfaceUpdateList = new ArrayList<ScreenUpdate>();
	damageTracker = new GridDamageTracker(b.width, b.height);
	imgBuffer = new BufferedImage(b.width, b.height, BufferedImage.TYPE_INT_RGB);
	bufferGraphics = (Graphics2D) imgBuffer.getGraphics();
	bufferGraphics.setColor(Color.WHITE);
	bufferGraphics.fillRect(0, 0, b.width, b.height);
	data = ((DataBufferInt) imgBuffer.getRaster().getDataBuffer()).getData();

        imgBufferSD = SurfaceManager.getManager(imgBuffer).getPrimarySurfaceData();
	
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

    public final void lockSurface() {
	screen.lockScreen();
    }

    public final void unlockSurface() {
	screen.unlockScreen();
    }

    public void addDirtyRectAndUnlock(int x1, int x2, int y1, int y2) {
	DamageRect rect = new DamageRect(x1, y1, x2, y2);
	damageTracker.trackDamageRect(rect);
	unlockSurface();
    }

    protected void evacuateDamagedAreas() {
	for (ScreenUpdate update : surfaceUpdateList) {
	    if (update instanceof BlitScreenUpdate) {
		((BlitScreenUpdate) update).evacuate();
	    }
	}
    }

    protected void addPendingUpdates(List<ScreenUpdate> updates) {
	if (updates != null) {
	    surfaceUpdateList.addAll(updates);
	}
    }

    public List<ScreenUpdate> fetchPendingSurfaceUpdates() {
	boolean forcePacking = surfaceUpdateList.size() > 0;
	addPendingUpdates(damageTracker.persistDamagedAreas(imgBuffer, forcePacking));
	
	if (surfaceUpdateList.size() > 0) {
	    List<ScreenUpdate> pendingUpdateList = surfaceUpdateList;
	    surfaceUpdateList = new ArrayList<ScreenUpdate>();
	    return pendingUpdateList;
	}

	return null;
    }

    int cnt = 0;

    @Override
    public boolean copyArea(SunGraphics2D sg2d, int x, int y, int w, int h, int dx, int dy) {
	Region clipRect = sg2d.getCompClip();
	CompositeType comptype = sg2d.imageComp;

	if (clipRect.isRectangular() && sg2d.transformState < sg2d.TRANSFORM_TRANSLATESCALE
		&& (CompositeType.SrcOverNoEa.equals(comptype) || CompositeType.SrcNoEa.equals(comptype))) {

	    try {
		lockSurface();

		x += sg2d.transX;
		y += sg2d.transY;

		addPendingUpdates(damageTracker.persistDamagedAreas(imgBuffer, true));
		evacuateDamagedAreas();

		bufferGraphics.setComposite(sg2d.composite);
		bufferGraphics.setClip(clipRect.getLoX(), clipRect.getLoY(), clipRect.getWidth(), clipRect.getHeight());
		bufferGraphics.copyArea(x, y, w, h, dx, dy);

		surfaceUpdateList.add(new CopyAreaScreenUpdate(x, y, x + w, y + h, dx, dy, clipRect));
	    } finally {
		unlockSurface();
	    }

	    return true;
	}

	return false;
    }
}
