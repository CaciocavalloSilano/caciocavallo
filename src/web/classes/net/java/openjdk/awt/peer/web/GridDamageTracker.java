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

import java.awt.image.*;
import java.util.*;

import net.java.openjdk.cacio.servlet.*;

public class GridDamageTracker {

    public static int GRID_SIZE = 16;

    DamageGridElement[][] grid;
    BufferedImage combinedAreas;
    ArrayList<WebRect> rectList;

    public GridDamageTracker(int width, int height) {
	rectList = new ArrayList<WebRect>();

	int cellsX = (int) Math.ceil(((double) width) / GRID_SIZE);
	int cellsY = (int) Math.ceil(((double) height) / GRID_SIZE);
	grid = new DamageGridElement[cellsY][cellsX];

	for (int y = 0; y < cellsY; y++) {
	    for (int x = 0; x < cellsX; x++) {
		grid[y][x] = new DamageGridElement(x * GRID_SIZE, y * GRID_SIZE);
	    }
	}
    }

    protected void trackDamageRect(WebRect rect) {
	int x1Cell = rect.getX1() / GRID_SIZE;
	int y1Cell = rect.getY1() / GRID_SIZE;
	int x2Cell = Math.min(rect.getX2() / GRID_SIZE, grid[0].length - 1);
	int y2Cell = Math.min(rect.getY2() / GRID_SIZE, grid.length - 1);

	for (int y = y1Cell; y <= y2Cell; y++) {
	    for (int x = x1Cell; x <= x2Cell; x++) {
		grid[y][x].addDamageRect(rect);
	    }
	}

	rectList.add(rect);
    }
    
    protected List<ScreenUpdate> persistDamagedAreas(BufferedImage imgBuffer, boolean forcePacking) {
	    WebRect unionRect = getUnionRectangle();
	    if (unionRect != null) {
		ArrayList<ScreenUpdate> screenUpdateList = new ArrayList<ScreenUpdate>();
		
		List<WebRect> regionList = createDamagedRegionList(5);
		if (unionRect != null && unionRect.getWidth() > 0 && unionRect.getHeight() > 0) {
		    
		    if (!isPackingEfficient(regionList, unionRect) && !forcePacking) {
			screenUpdateList.add(new BlitScreenUpdate(unionRect.getX1(), unionRect.getY1(), unionRect.getX1(), unionRect.getY1(),
				unionRect.getWidth(), unionRect.getHeight(), imgBuffer));
		    } else {
			for (WebRect dRect : regionList) {
			    screenUpdateList.add(new BlitScreenUpdate(dRect.getX1(), dRect.getY1(), dRect.getX1(), dRect.getY1(), dRect.getWidth(),
				    dRect.getHeight(), imgBuffer));
			}
		    }
		}

		reset();
		return screenUpdateList;
	    }
	    
	    return null;
    }

    private WebRect getUnionRectangle() {
	if (rectList.size() == 0) {
	    return null;
	}

	return new WebRect().union(rectList);
    }
    

    private void reset() {
	for (int y = 0; y < grid.length; y++) {
	    for (int x = 0; x < grid[0].length; x++) {
		grid[y][x].reset();
	    }
	}

	rectList.clear();
    }

    private boolean isPackingEfficient(List<WebRect> regionList, WebRect unionRect) {
	int regionSize = 0;
	for (WebRect rect : regionList) {
	    regionSize += rect.getWidth() * rect.getHeight();
	}

	int unionSize = unionRect.getWidth() * unionRect.getHeight();

	// Packing makes only sence if more than half of the area would be
	// "wasted" otherwise
	return regionSize * 2 < unionSize;
    }

    private List<WebRect> createDamagedRegionList(int mergeLimit) {
	WebRect[][] unions = new WebRect[grid.length][grid[0].length];
	List<WebRect> rectList = new ArrayList<WebRect>();

	/* Calculate damaged region for each Cell */
	for (int y = 0; y < grid.length; y++) {
	    for (int x = 0; x < grid[0].length; x++) {
		DamageGridElement elem = grid[y][x];
		unions[y][x] = elem.calculateDamageUnion();
	    }
	}

	// System.out.println("Count before merging: " + countUnions(unions));

	mergeCellsHorizontal(unions, mergeLimit);
	mergeCellsVertical(unions, mergeLimit);

	// System.out.println("Count after merging: " + countUnions(unions));

	for (int y = 0; y < grid.length; y++) {
	    for (int x = 0; x < grid[0].length; x++) {
		if (unions[y][x] != null) {
		    rectList.add(unions[y][x]);
		}
	    }
	}

	return rectList;
    }

    private int countUnions(WebRect[][] unions) {
	int unionCnt = 0;
	for (int y = 0; y < grid.length; y++) {
	    for (int x = 0; x < grid[0].length; x++) {
		if (unions[y][x] != null) {
		    unionCnt++;
		}
	    }
	}
	return unionCnt;
    }

    private void mergeCellsHorizontal(WebRect[][] unions, int mergeLimit) {
	// Try to reduce regions by extending horizontally
	for (int y = 0; y < unions.length; y++) {
	    for (int x = 0; x < unions[0].length - 1; x++) {
		WebRect cellRect = unions[y][x];

		int firstedMerged = x;
		for (x++; x < unions[0].length && cellRect != null && (x - firstedMerged) < mergeLimit; x++) {
		    WebRect extensionRect = unions[y][x];

		    if (extensionRect != null && extensionRect.y1 == cellRect.y1 && extensionRect.getHeight() == cellRect.getHeight()
			    && extensionRect.x1 == cellRect.x2) {
			cellRect.x2 = extensionRect.x2;
			unions[y][x] = null;
		    }
		}
	    }
	}
    }

    private void mergeCellsVertical(WebRect[][] unions, int mergeLimit) {
	// Try to reduce regions by extending horizontally
	for (int x = 0; x < unions[0].length; x++) {
	    for (int y = 0; y < unions.length - 1; y++) {
		WebRect cellRect = unions[y][x];

		int firstMergedCell = y;
		for (y++; y < unions.length && cellRect != null && (y - firstMergedCell) < mergeLimit; y++) {
		    WebRect extensionRect = unions[y][x];

		    if (extensionRect != null && extensionRect.x1 == cellRect.x1 && extensionRect.getWidth() == cellRect.getWidth()
			    && extensionRect.y1 == cellRect.y2) {
			cellRect.y2 = extensionRect.y2;
			unions[y][x] = null;
		    }
		}
	    }
	}
    }

}

class DamageGridElement {
    int x, y;
    ArrayList<WebRect> rectangles = null;

    boolean tracked = false;

    public DamageGridElement(int x, int y) {
	this.x = x;
	this.y = y;
    }

    public void addDamageRect(WebRect rect) {
	if (rectangles == null) {
	    rectangles = new ArrayList<WebRect>();
	}
	rectangles.add(rect);
    }

    public WebRect calculateDamageUnion() {
	if (rectangles == null || rectangles.size() == 0) {
	    return null;
	}

	WebRect damageRect = new WebRect(rectangles.get(0));
	for (WebRect rect : rectangles) {
	    damageRect.union(rect);
	}

	int x2 = x + GridDamageTracker.GRID_SIZE;
	int y2 = y + GridDamageTracker.GRID_SIZE;
	damageRect.restrictToArea(x, y, x2, y2);

	if (damageRect.getWidth() == 0 || damageRect.getHeight() == 0) {
	    return null;
	}

	return damageRect;
    }

    public void reset() {
	rectangles = null;
	tracked = false;
    }

    public boolean isTracked() {
	return tracked;
    }

    public void setTracked(boolean tracked) {
	this.tracked = tracked;
    }
}
