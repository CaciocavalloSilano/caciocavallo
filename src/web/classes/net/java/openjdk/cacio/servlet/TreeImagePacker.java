package net.java.openjdk.cacio.servlet;

import java.util.*;

import net.java.openjdk.awt.peer.web.*;

public class TreeImagePacker {

    PackNode rootNode;
    int width, height;

    public TreeImagePacker() {

    }

    public void insertScreenUpdateList(List<ScreenUpdate> updateList) {
	ArrayList<BlitScreenUpdate> bltUpdateList = new ArrayList<BlitScreenUpdate>(updateList.size());
	for (ScreenUpdate update : updateList) {
	    if (update instanceof BlitScreenUpdate) {
		bltUpdateList.add((BlitScreenUpdate) update);
	    }
	}

	Collections.sort(bltUpdateList, new ScreenUpdateComperator());

	int maxWidth = getMaxWidth(bltUpdateList);
	int maxHeight = getMaxHeight(bltUpdateList);

	boolean packingSuccessful;
	do {
	    rootNode = new PackNode();
	    rootNode.setRect(new WebRect(0, 0, maxWidth, maxHeight));
	    width = height = 0;
	    
	    packingSuccessful = true;
	    for(int i=0; packingSuccessful && i < bltUpdateList.size(); i++){
		packingSuccessful &= insert(bltUpdateList.get(i));
	    }
	    
	    maxWidth = (int) (maxWidth * 1.5 + 1);
	    maxHeight = (int) (maxHeight * 1.5 + 1);
	} while (!packingSuccessful);
    }

    protected int getMaxWidth(ArrayList<BlitScreenUpdate> updateList) {
	int maxWidth = 0;
	for (BlitScreenUpdate update : updateList) {
	    maxWidth = Math.max(maxWidth, update.getUpdateArea().getWidth());
	}
	return maxWidth;
    }

    protected int getMaxHeight(ArrayList<BlitScreenUpdate> updateList) {
	int maxHeight = 0;
	for (BlitScreenUpdate update : updateList) {
	    maxHeight = Math.max(maxHeight, update.getUpdateArea().getHeight());
	}
	return maxHeight;
    }

    private boolean insert(BlitScreenUpdate update) {
	PackNode insertNode =rootNode.insert(update.getUpdateArea());
	if (insertNode != null) {
	    update.setPackedX(insertNode.rect.getX1());
	    update.setPackedY(insertNode.rect.getY1());
	    
	    width = Math.max(width, insertNode.rect.getX2());
	    height = Math.max(height, insertNode.rect.getY2());
	    
	    return true;
	} else {
	    return false;
	}
    }

    public WebRect getBoundingBox() {
	return new WebRect(0, 0, width, height);
    }

    public boolean isPackingEfficient(WebRect boundingBox, WebRect unionRect) {
	int packedArea = boundingBox.getWidth() * boundingBox.getHeight();
	int unionArea = (unionRect.getWidth() * unionRect.getHeight());
	return (packedArea * 100) / unionArea <= 80;
    }
}

class PackNode {
    PackNode leftNode;
    PackNode rightNode;
    WebRect rect;
    WebRect imgRect;

    public PackNode insert(WebRect newRect) {
	// If the current node is no leaf, pass the insert down the tree
	if (leftNode != null) {
	    PackNode insertNode = leftNode.insert(newRect);
	    if (insertNode == null) {
		insertNode = rightNode.insert(newRect);
	    }
	    return insertNode;
	} else {
	    if (imgRect != null) {
		return null;
	    }

	    // Area to small
	    if (rect.getWidth() < newRect.getWidth() || rect.getHeight() < newRect.getHeight()) {
		return null;
	    }

	    // Perfect fit
	    if (rect.getWidth() == newRect.getWidth() && rect.getHeight() == newRect.getHeight()) {
		imgRect = newRect;
		return this;
	    }

	    // Split node
	    leftNode = new PackNode();
	    rightNode = new PackNode();

	    int dw = rect.getWidth() - newRect.getWidth();
	    int dh = rect.getHeight() - newRect.getHeight();

	    if (dw > dh) {
		leftNode.setRect(new WebRect(rect.getX1(), rect.getY1(), rect.getX1() + newRect.getWidth(), rect.getY2()));
		rightNode.setRect(new WebRect(rect.getX1() + newRect.getWidth(), rect.getY1(), rect.getX2(), rect.getY2()));
	    } else {
		leftNode.setRect(new WebRect(rect.getX1(), rect.getY1(), rect.getX2(), rect.getY1() + newRect.getHeight()));
		rightNode.setRect(new WebRect(rect.getX1(), rect.getY1() + newRect.getHeight(), rect.getX2(), rect.getY2()));
	    }

	    return leftNode.insert(newRect);
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

    public WebRect getRect() {
	return rect;
    }

    public void setRect(WebRect rect) {
	this.rect = rect;
    }

    public WebRect getImgRect() {
	return imgRect;
    }

    public void setImgRect(WebRect imgRect) {
	this.imgRect = imgRect;
    }
}

class ScreenUpdateComperator implements Comparator<ScreenUpdate> {
    @Override
    public int compare(ScreenUpdate s1, ScreenUpdate s2) {
	WebRect u1 = s1.getUpdateArea();
	WebRect u2 = s2.getUpdateArea();

	return (u2.getWidth() * u2.getHeight()) - (u1.getWidth() * u1.getHeight());
    }

}
