package net.java.openjdk.cacio.servlet;

import java.util.*;

import net.java.openjdk.awt.peer.web.*;

public class SimpleImagePacker {
    
    int curWidth = 0, curHeight = 0;
    
    public SimpleImagePacker() {
	
    }
    
    public SimpleImagePacker(List<DamageRect> regionList) {
	this();
	insertDamagedRegionList(regionList);
    }
    
    public void insertDamagedRegionList(List<DamageRect> regionList) {
	for(DamageRect rect : regionList) {
	    insert(rect);
	}
    }
    
    public void insert(DamageRect rect) {
	curHeight += rect.getHeight();
	curWidth = Math.max(curWidth, rect.getWidth());
    }
    
    public void insert(BlitScreenUpdate update) {
	update.setSrcY(curHeight);
	update.setSrcX(0);
	
	insert(update.getUpdateArea());
    }
   
    public DamageRect getBoundingBox() {
	return new DamageRect(0, 0, curWidth, curHeight);
    }
    
    public boolean isPackingEfficient(DamageRect boundingBox, DamageRect unionRect) {
	return true;
    }
}
