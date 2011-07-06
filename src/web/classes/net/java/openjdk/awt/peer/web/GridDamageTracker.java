package net.java.openjdk.awt.peer.web;

import java.util.*;

public class GridDamageTracker {

    public static int GRID_SIZE = 16;

    DamageGridElement[][] grid;

    public GridDamageTracker(int width, int height) {
	int cellsX = (int) Math.ceil(((double) width) / GRID_SIZE);
	int cellsY = (int) Math.ceil(((double) height) / GRID_SIZE);

	grid = new DamageGridElement[cellsY][cellsX];
	for (int y = 0; y < cellsY; y++) {
	    for (int x = 0; x < cellsX; x++) {
		grid[y][x] = new DamageGridElement(x * GRID_SIZE, y * GRID_SIZE);
	    }
	}
    }

    public void trackDamageRect(DamageRect rect) {
	int x1Cell = rect.getX1() / GRID_SIZE;
	int y1Cell = rect.getY1() / GRID_SIZE;
	int x2Cell = rect.getX2() / GRID_SIZE;
	int y2Cell = rect.getY2() / GRID_SIZE;

	for (int y = y1Cell; y <= y2Cell; y++) {
	    for (int x = x1Cell; x <= x2Cell; x++) {
		grid[y][x].addDamageRect(rect);
	    }
	}
    }

    public void calculateDamagedAreas() {
	DamageRect[][] unions = new DamageRect[grid.length][grid[0].length];

	long start = System.currentTimeMillis();
	
	/* Calculate damaged region for each Cell */
	for (int y = 0; y < grid.length; y++) {
	    for (int x = 0; x < grid[0].length; x++) {
		DamageGridElement elem = grid[y][x];
		unions[y][x] = elem.calculateDamageUnion();
		elem.reset();
	    }
	}

	System.out.println("Count before merging: " + countUnions(unions));

	mergeCellsHorizontal(unions);
	mergeCellsVertical(unions);

	System.out.println("Count after merging: " + countUnions(unions));
	
	long end = System.currentTimeMillis();
	System.out.println("Took: "+(end-start));
	
//	for (int y = 0; y < grid.length; y++) {
//	    for (int x = 0; x < grid[0].length; x++) {
//		if (unions[y][x] != null) {
//		    System.out.println(unions[y][x]);
//		}
//	    }
//	}
//	    
	System.out.println();
    }

    protected int countUnions(DamageRect[][] unions) {
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

    protected void mergeCellsHorizontal(DamageRect[][] unions) {
	// Try to reduce regions by extending horizontally
	for (int y = 0; y < unions.length; y++) {
	    for (int x = 0; x < unions[0].length - 1; x++) {
		DamageRect cellRect = unions[y][x];

		for (x++; x < unions[0].length && cellRect != null; x++) {
		    DamageRect extensionRect = unions[y][x];

		    if (extensionRect != null && extensionRect.y1 == cellRect.y1 && extensionRect.getHeight() == cellRect.getHeight()
			    && extensionRect.x1 == cellRect.x2 + 1) {
			cellRect.x2 = extensionRect.x2;
			unions[y][x] = null;
		    }
		}
	    }
	}
    }

    protected void mergeCellsVertical(DamageRect[][] unions) {
	// Try to reduce regions by extending horizontally
	for (int x = 0; x < unions[0].length; x++) {
	    for (int y = 0; y < unions.length - 1; y++) {
		DamageRect cellRect = unions[y][x];

		for (y++; y < unions.length && cellRect != null; y++) {
		    DamageRect extensionRect = unions[y][x];

		    if (extensionRect != null && extensionRect.x1 == cellRect.x1 && extensionRect.getWidth() == cellRect.getWidth()
			    && extensionRect.y1 == cellRect.y2 + 1) {
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

	int x2 = x + GridDamageTracker.GRID_SIZE - 1;
	int y2 = y + GridDamageTracker.GRID_SIZE - 1;
	damageRect.restrictToArea(x, y, x2, y2);

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
