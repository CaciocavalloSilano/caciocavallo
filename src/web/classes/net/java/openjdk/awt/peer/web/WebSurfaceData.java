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
import java.awt.image.*;
import java.util.*;
import java.util.List;

import sun.awt.image.*;
import sun.java2d.*;
import sun.java2d.loops.*;
import sun.java2d.pipe.*;

/**
 * SurfaceData implementation for Caciocavallo-Web.
 * 
 * One of the core components of caciocavallo-web is WebSurfaceData, an
 * implementation of OpenJDK's SurfaceData interface - which is basically a
 * surface Java2D can render to. Whenever a rendering operation is executed,
 * Java2D's software rendering implementation "locks" the target WebSurfaceData,
 * and supplies cacio-web the area it plans to modify, which are passed to the
 * GridDamageTracker.
 * 
 * @author Clemens Eisserer <linuxhippy@gmail.com>
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

	int w = b.width, h = b.height;
	surfaceUpdateList = new ArrayList<ScreenUpdate>();
	damageTracker = new GridDamageTracker(w, h);
	imgBuffer = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
	bufferGraphics = (Graphics2D) imgBuffer.getGraphics();
	bufferGraphics.setColor(Color.WHITE);
	bufferGraphics.fillRect(0, 0, w, h);
	data = ((DataBufferInt) imgBuffer.getRaster().getDataBuffer()).getData();

	imgBufferSD = SurfaceManager.getManager(imgBuffer).getPrimarySurfaceData();

	int imgStride = ((SinglePixelPackedSampleModel) imgBuffer.getSampleModel()).getScanlineStride() * 4;
	initOps(data, w, h, imgStride);
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

    /**
     * Locks the surface, by locking the screen-lock
     */
    public final void lockSurface() {
	screen.lockScreen();
    }

    /**
     * Unlocks the surface, by releasing the screen-lock.
     */
    public final void unlockSurface() {
	screen.unlockScreen();
    }

    /**
     * Adds a modified area to the list of tracked rectangles, signals the
     * screen for changes and releases the surface-lock.
     * 
     * Intended to be called by native code through JNI. May only be invoked
     * when the surface is locked.
     * 
     * @param x1
     * @param x2
     * @param y1
     * @param y2
     */
    public final void addDirtyRectAndUnlock(int x1, int x2, int y1, int y2) {
	try {
	    x1 = (x1 > 0) ? x1 : 0;
	    y1 = (y1 > 0) ? y1 : 0;
	    x1 = (x1 < bounds.width) ? x1 : bounds.width;
	    y1 = (y1 < bounds.height) ? y1 : bounds.height;

	    x2 = (x2 > x1) ? x2 : x1;
	    y2 = (y2 > y1) ? y2 : y1;
	    x2 = (x2 < bounds.width) ? x2 : bounds.width;
	    y2 = (y2 < bounds.height) ? y2 : bounds.height;

	    WebRect rect = new WebRect(x1, y1, x2, y2);
	    damageTracker.trackDamageRect(rect);

	    screen.signalScreen();
	} finally {
	    unlockSurface();
	}
    }

    /**
     * "Evacuate" all pending BlitScreenUpdates.
     * 
     * @see BlitScreenUpdate for more information.
     */
    protected void evacuateBlitScreenUpdates() {
	for (ScreenUpdate update : surfaceUpdateList) {
	    if (update instanceof BlitScreenUpdate) {
		((BlitScreenUpdate) update).evacuate();
	    }
	}
    }

    /**
     * Add a list of updates, to the list of pending updates.
     * 
     * @param updates
     */
    protected void addPendingUpdates(List<ScreenUpdate> updates) {
	if (updates != null) {
	    surfaceUpdateList.addAll(updates);
	}
    }

    /**
     * If there are multiple BlitScreenUpdates caused by ScreenUpdates which
     * require ordering (e.g. CopyAreaScreenUpdate), check if the total
     * Image-Size of those BlitScreenUpdates is really less than a single one
     * spanning the whole changed area.
     */
    protected void mergeMultipleScreenUpdates() {
	if (surfaceUpdateList.size() >= 2) {
	    WebRect singleUpdateBoundingBox = ScreenUpdate.getScreenUpdateBoundingBox(surfaceUpdateList);
	    int multiUpdateSize = BlitScreenUpdate.getPendingBlitScreenUpdateSize(surfaceUpdateList);
	    int singleUpdateSize = singleUpdateBoundingBox.getWidth() * singleUpdateBoundingBox.getHeight();

	    if (multiUpdateSize >= singleUpdateSize / 2) {
		surfaceUpdateList.clear();
		surfaceUpdateList
			.add(new BlitScreenUpdate(singleUpdateBoundingBox.getX1(), singleUpdateBoundingBox.getY1(), singleUpdateBoundingBox.getX1(),
				singleUpdateBoundingBox.getY1(), singleUpdateBoundingBox.getWidth(), singleUpdateBoundingBox.getHeight(), imgBuffer));
	    }
	}
    }

    /**
     * @return A list with all ScreenUpdates, or null iff there are none.
     */
    public List<ScreenUpdate> fetchPendingSurfaceUpdates() {
	addPendingUpdates(damageTracker.groupDamagedAreas(imgBuffer));

	if (surfaceUpdateList.size() > 0) {
	    mergeMultipleScreenUpdates();

	    List<ScreenUpdate> pendingUpdateList = surfaceUpdateList;
	    surfaceUpdateList = new ArrayList<ScreenUpdate>();
	    return pendingUpdateList;
	}

	return null;
    }

    /**
     * Checks wether generating an order requiring ScreenUpdate is efficient at
     * all, by investigating the amount of image-data that would have to be
     * effacuated. If this method returns false, its often better to fall back to
     * software routines and let the DamageTracking do its job.
     * 
     * Although ScreenUpdates are merged if profitable by
     * mergeMultipleScreenUpdates(), evacuating huge amounts of image-data, just
     * to throw it away later, is very inefficient.
     * 
     * @return
     */
    private boolean isOrderedScreenUpdateEfficient() {
	return BlitScreenUpdate.getPendingBlitScreenUpdateSize(surfaceUpdateList) * 2 < (bounds.width * bounds.height);
    }

    /**
     * Acceleration hook for handling copyArea operations.
     * 
     * Scrolling as well as Window-Movement are implemented using copyArea, and
     * can be sped up by properly implementing copyArea. Instead of the whole
     * modified area a copyArea commend is sent to the browser, and only the
     * "invisible" area has to be sent down as image-data.
     * 
     * CopyArea needs to "evacuate" BlitScreenUpdates preceeding it, because its
     * order dependent.
     * 
     */
    @Override
    public boolean copyArea(SunGraphics2D sg2d, int x, int y, int w, int h, int dx, int dy) {
	Region clipRect = sg2d.getCompClip();
	CompositeType comptype = sg2d.imageComp;

	if (clipRect.isRectangular() && sg2d.transformState < SunGraphics2D.TRANSFORM_TRANSLATESCALE
		&& (CompositeType.SrcOverNoEa.equals(comptype) || CompositeType.SrcNoEa.equals(comptype))) {

	    try {
		lockSurface();
		if (!isOrderedScreenUpdateEfficient()) {
		    return false;
		}

		x += sg2d.transX;
		y += sg2d.transY;

		// Materialize pending updates, and "evacuate" those changes.
		addPendingUpdates(damageTracker.groupDamagedAreas(imgBuffer));
		evacuateBlitScreenUpdates();

		// Execute copyArea on the BufferedImage backing the
		// WebSurfaceData, to get the copyArea-effect there too.
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
