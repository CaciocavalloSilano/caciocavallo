package net.java.openjdk.cacio.servlet;

import java.util.*;

import net.java.openjdk.awt.peer.web.*;

public class TreeImagePacker {

    PackNode rootNode;
    
    public TreeImagePacker() {
	rootNode = new PackNode();
	rootNode.setRect(new DamageRect(0, 0, Integer.MAX_VALUE, Integer.MAX_VALUE));
    }
    
    public TreeImagePacker(List<DamageRect> regionList) {
	this();
	insertDamagedRegionList(regionList);
    }
    
    public void insertDamagedRegionList(List<DamageRect> regionList) {
	for(DamageRect rect : regionList) {
	    insert(rect);
	}
    }
    
    public PackNode insert(DamageRect rect) {
	return rootNode.insert(rect);
    }
    
    public void insert(BlitScreenUpdate update) {	
	PackNode insertNode = insert(update.getUpdateArea());
	update.setPackedX(insertNode.rect.getX1());
	update.setPackedY(insertNode.rect.getY1());
    }
   
    public DamageRect getBoundingBox() {
	return rootNode.getBoundingBox();
    }
    
    public boolean isPackingEfficient(DamageRect boundingBox, DamageRect unionRect) {
	int packedArea = boundingBox.getWidth() * boundingBox.getHeight();
	int unionArea = (unionRect.getWidth() * unionRect.getHeight());
	return (packedArea*100) / unionArea <= 80;
    }
}

class PackNode {
    PackNode leftNode;
    PackNode rightNode;
    DamageRect rect;
    DamageRect imgRect;

    public DamageRect getBoundingBox() {
	DamageRect leftBox= null;
	DamageRect rightBox = null;
	DamageRect thisBox = null;
	
	if(leftNode != null) {
	    leftBox = leftNode.getBoundingBox();
	    rightBox = rightNode.getBoundingBox();
	}
	
	if(imgRect != null) {
	    thisBox = new DamageRect(rect.getX1(), rect.getY1(), rect.getX1() + imgRect.getWidth(),  rect.getY1() + imgRect.getHeight());
	}
	
	DamageRect unionRect = leftBox != null ? leftBox : rightBox != null ? rightBox : thisBox != null ? thisBox : null;
	
	if(unionRect != null) {
	    if(thisBox != null) {
		unionRect.union(thisBox);
	    }
	    
	    if(leftBox != null) {
		unionRect.union(leftBox);
	    }
	    
	    if(rightBox != null) {
		unionRect.union(rightBox);
	    }
	}
	
	return unionRect;
    }
   
    
    public PackNode insert(DamageRect newRect) {
	// If the current node is no leaf, pass the insert down the tree
	if (leftNode != null) {
	    PackNode insertNode = leftNode.insert(newRect);
	    if (insertNode == null) {
		insertNode = rightNode.insert(newRect);
	    }
	    return insertNode;
	} else {
	    if(imgRect != null) {
		return null;
	    }
	    
	    //Area to small
	    if(rect.getWidth() < newRect.getWidth() || rect.getHeight() < newRect.getHeight()) {
		return null;
	    }
	    
	    //Perfect fit
	    if(rect.getWidth() == newRect.getWidth() && rect.getHeight() == newRect.getHeight()) {
		return this;
	    }
	    
	    //Split node
	    leftNode = new PackNode();
	    rightNode = new PackNode();
	    
	    int dw = rect.getWidth() - newRect.getWidth();
	    int dh = rect.getHeight() - newRect.getHeight();
	    
	    if(dw > dh) {
		leftNode.setRect(new DamageRect(rect.getX1(), rect.getY1(), rect.getX1() + newRect.getWidth() - 1, rect.getY2()));
		rightNode.setRect(new DamageRect(rect.getX1() + newRect.getWidth(), rect.getY1(), rect.getX2(), rect.getY2()));
	    } else {
		leftNode.setRect(new DamageRect(rect.getX1(), rect.getY1(), rect.getX2(), rect.getY1() + newRect.getHeight() - 1));
		rightNode.setRect(new DamageRect(rect.getX1(), rect.getY1() + newRect.getHeight(), rect.getX2(), rect.getY2()));
	    }
	    
	   //Insert into first child we created
	    leftNode.setImgRect(newRect);
	    return leftNode;
	}
    }
    
    public PackNode getLeftNode() {
	return leftNode;
    }

    public PackNode getRightNode() {
	return rightNode;
    }


    public void setLeftNode(PackNode leftNode) {
	this.leftNode = leftNode;
    }

    public void setRightNode(PackNode rightNode) {
	this.rightNode = rightNode;
    }


    public DamageRect getRect() {
        return rect;
    }


    public void setRect(DamageRect rect) {
        this.rect = rect;
    }

    public DamageRect getImgRect() {
        return imgRect;
    }

    public void setImgRect(DamageRect imgRect) {
        this.imgRect = imgRect;
    }
}
