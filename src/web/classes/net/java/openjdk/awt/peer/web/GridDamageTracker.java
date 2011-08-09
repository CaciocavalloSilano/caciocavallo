package net.java.openjdk.awt.peer.web;

import java.awt.image.*;
import java.util.*;

import net.java.openjdk.cacio.servlet.*;

public class GridDamageTracker {

    public static int GRID_SIZE = 16;

    DamageGridElement[][] grid;
    BufferedImage combinedAreas;
    ArrayList<DamageRect> rectList;

    public GridDamageTracker(int width, int height) {
	rectList = new ArrayList<DamageRect>();

	int cellsX = (int) Math.ceil(((double) width) / GRID_SIZE);
	int cellsY = (int) Math.ceil(((double) height) / GRID_SIZE);
	grid = new DamageGridElement[cellsY][cellsX];

	for (int y = 0; y < cellsY; y++) {
	    for (int x = 0; x < cellsX; x++) {
		grid[y][x] = new DamageGridElement(x * GRID_SIZE, y * GRID_SIZE);
	    }
	}
    }

    protected void trackDamageRect(DamageRect rect) {
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
	    DamageRect unionRect = getUnionRectangle();
	    if (unionRect != null) {
		ArrayList<ScreenUpdate> screenUpdateList = new ArrayList<ScreenUpdate>();
		
		List<DamageRect> regionList = createDamagedRegionList(5);
		if (unionRect != null && unionRect.getWidth() > 0 && unionRect.getHeight() > 0) {
		    
		    if (!isPackingEfficient(regionList, unionRect) && !forcePacking) {
			screenUpdateList.add(new BlitScreenUpdate(unionRect.getX1(), unionRect.getY1(), unionRect.getX1(), unionRect.getY1(),
				unionRect.getWidth(), unionRect.getHeight(), imgBuffer));
		    } else {
			for (DamageRect dRect : regionList) {
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

    private DamageRect getUnionRectangle() {
	if (rectList.size() == 0) {
	    return null;
	}

	return new DamageRect().union(rectList);
    }
    

    private void reset() {
	for (int y = 0; y < grid.length; y++) {
	    for (int x = 0; x < grid[0].length; x++) {
		grid[y][x].reset();
	    }
	}

	rectList.clear();
    }

    private boolean isPackingEfficient(List<DamageRect> regionList, DamageRect unionRect) {
	int regionSize = 0;
	for (DamageRect rect : regionList) {
	    regionSize += rect.getWidth() * rect.getHeight();
	}

	int unionSize = unionRect.getWidth() * unionRect.getHeight();

	// Packing makes only sence if more than half of the area would be
	// "wasted" otherwise
	return regionSize * 2 < unionSize;
    }

    private List<DamageRect> createDamagedRegionList(int mergeLimit) {
	DamageRect[][] unions = new DamageRect[grid.length][grid[0].length];
	List<DamageRect> rectList = new ArrayList<DamageRect>();

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

    private int countUnions(DamageRect[][] unions) {
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

    private void mergeCellsHorizontal(DamageRect[][] unions, int mergeLimit) {
	// Try to reduce regions by extending horizontally
	for (int y = 0; y < unions.length; y++) {
	    for (int x = 0; x < unions[0].length - 1; x++) {
		DamageRect cellRect = unions[y][x];

		int firstedMerged = x;
		for (x++; x < unions[0].length && cellRect != null && (x - firstedMerged) < mergeLimit; x++) {
		    DamageRect extensionRect = unions[y][x];

		    if (extensionRect != null && extensionRect.y1 == cellRect.y1 && extensionRect.getHeight() == cellRect.getHeight()
			    && extensionRect.x1 == cellRect.x2) {
			cellRect.x2 = extensionRect.x2;
			unions[y][x] = null;
		    }
		}
	    }
	}
    }

    private void mergeCellsVertical(DamageRect[][] unions, int mergeLimit) {
	// Try to reduce regions by extending horizontally
	for (int x = 0; x < unions[0].length; x++) {
	    for (int y = 0; y < unions.length - 1; y++) {
		DamageRect cellRect = unions[y][x];

		int firstMergedCell = y;
		for (y++; y < unions.length && cellRect != null && (y - firstMergedCell) < mergeLimit; y++) {
		    DamageRect extensionRect = unions[y][x];

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
    ArrayList<DamageRect> rectangles = null;

    boolean tracked = false;

    public DamageGridElement(int x, int y) {
	this.x = x;
	this.y = y;
    }

    public void addDamageRect(DamageRect rect) {
	if (rectangles == null) {
	    rectangles = new ArrayList<DamageRect>();
	}
	rectangles.add(rect);
    }

    public DamageRect calculateDamageUnion() {
	if (rectangles == null || rectangles.size() == 0) {
	    return null;
	}

	DamageRect damageRect = new DamageRect(rectangles.get(0));
	for (DamageRect rect : rectangles) {
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
