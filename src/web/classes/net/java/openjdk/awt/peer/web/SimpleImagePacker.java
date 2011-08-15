package net.java.openjdk.awt.peer.web;

import java.util.*;

import net.java.openjdk.awt.peer.web.*;

public class SimpleImagePacker {
    
    int curWidth = 0, curHeight = 0;
    
    public SimpleImagePacker() {
	
    }
    
    public SimpleImagePacker(List<WebRect> regionList) {
	this();
	insertDamagedRegionList(regionList);
    }
    
    public void insertDamagedRegionList(List<WebRect> regionList) {
	for(WebRect rect : regionList) {
	    insert(rect);
	}
    }
    
    public void insert(WebRect rect) {
	curHeight += rect.getHeight();
	curWidth = Math.max(curWidth, rect.getWidth());
    }
    
    public void insert(BlitScreenUpdate update) {
	update.setPackedY(curHeight);
	update.setPackedX(0);
	
	insert(update.getUpdateArea());
    }
   
    public WebRect getBoundingBox() {
	return new WebRect(0, 0, curWidth, curHeight);
    }
    
    public boolean isPackingEfficient(WebRect boundingBox, WebRect unionRect) {
	return true;
    }
}
