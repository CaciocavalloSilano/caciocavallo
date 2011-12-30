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

import java.util.*;

/**
 * Image packer, which packs multiple differently shaped images into a larger
 * one. This functionality is required when sending multiple BlitScreenUpdates
 * to the browser in one-go, as only one resulting image will be loaded.
 * 
 * @author Clemens Eisserer <linuxhippy@gmail.com>
 */
public class TreeImagePacker {
    PackNode rootNode;
    int width, height;

    public TreeImagePacker() {

    }

    /**
     * Inserts the BlitScreenUpdates contained in the updateList into
     * packedImage. The tree-packing alrorithm requires a iterative size-growth
     * for acceptable results.
     * It starts with an image as large as the largest width/height, and tries to fit all updates into it.
     * If it doesn't succeed it tries again, with an image 1.5* larger in each direction.
     * 
     * @param updateList
     */
    public void insertScreenUpdateList(List<ScreenUpdate> updateList) {
	List<BlitScreenUpdate> bltUpdateList = filterBlitScreenUpdates(updateList);
	Collections.sort(bltUpdateList, new ScreenUpdateComperator());

	int maxWidth = getMaxWidth(bltUpdateList);
	int maxHeight = getMaxHeight(bltUpdateList);

	boolean packingSuccessful;
	do {
	    rootNode = new PackNode();
	    rootNode.setRect(new WebRect(0, 0, maxWidth, maxHeight));
	    width = height = 0;

	    packingSuccessful = true;
	    for (int i = 0; packingSuccessful && i < bltUpdateList.size(); i++) {
		packingSuccessful &= insert(bltUpdateList.get(i));
	    }

	    maxWidth = (int) (maxWidth * 1.5 + 1);
	    maxHeight = (int) (maxHeight * 1.5 + 1);
	} while (!packingSuccessful);
    }

    /**
     * Filters out the BlitScreenUpdates contained in updateList.
     * 
     * @param updateList
     * @return List with the BlitScreenUpdates contained in updateList
     */
    protected List<BlitScreenUpdate> filterBlitScreenUpdates(List<ScreenUpdate> updateList) {
	ArrayList<BlitScreenUpdate> bltUpdateList = new ArrayList<BlitScreenUpdate>(updateList.size());
	for (ScreenUpdate update : updateList) {
	    if (update instanceof BlitScreenUpdate) {
		bltUpdateList.add((BlitScreenUpdate) update);
	    }
	}
	return bltUpdateList;
    }

    /**
     * @param updateList
     * @return width of the widest BlitScreenUpdate
     */
    protected int getMaxWidth(List<BlitScreenUpdate> updateList) {
	int maxWidth = 0;
	for (BlitScreenUpdate update : updateList) {
	    maxWidth = Math.max(maxWidth, update.getUpdateArea().getWidth());
	}
	return maxWidth;
    }

    /**
     * @param updateList
     * @return height of the widest BlitScreenUpdate
     */
    protected int getMaxHeight(List<BlitScreenUpdate> updateList) {
	int maxHeight = 0;
	for (BlitScreenUpdate update : updateList) {
	    maxHeight = Math.max(maxHeight, update.getUpdateArea().getHeight());
	}
	return maxHeight;
    }

    /**
     * Inserts a BlitScreenUpdate into the packing-structure, determines its
     * position on success, which is stored in packedX/Y variables.
     * 
     * @param update
     * @return wether the insert operation was successful
     */
    private boolean insert(BlitScreenUpdate update) {
	PackNode insertNode = rootNode.insert(update.getUpdateArea());
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

    /**
     * @return boundingbox containing all image-data in the packed image.
     */
    public WebRect getBoundingBox() {
	return new WebRect(0, 0, width, height);
    }

    /**
     * Determines if packing makes sence at all. If packing the grouped changes
     * results in a larger image than simply spanning a rectangle arround all
     * changes, packing is not efficient.
     * 
     * @param boundingBox
     *            - the bounding-box arround the packed image data
     * @param unionRect
     *            - the bounding-box of all changes without packing.
     * @return true iff packing images is more efficient than simply spanning a
     *         bounding-box arround all changes.
     */
    public boolean isPackingEfficient(WebRect boundingBox, WebRect unionRect) {
	int packedArea = boundingBox.getWidth() * boundingBox.getHeight();
	int unionArea = (unionRect.getWidth() * unionRect.getHeight());
	return (packedArea * 100) / unionArea <= 80;
    }
}

/**
 * Internal Binary-Tree data structure used for packing.
 * 
 * @author Clemens Eisserer <linuxhippy@gmail.com>
 */
class PackNode {
    PackNode leftNode;
    PackNode rightNode;
    WebRect rect;
    WebRect imgRect;

    public PackNode insert(WebRect newRect) {
	// If the current node is no leaf, pass the insert down the tree
	if (leftNode != null) {
	    
	    //Insert into the left node
	    PackNode insertNode = leftNode.insert(newRect);
	    if (insertNode == null) {
		//Try the right one, if left insert was not successful
		insertNode = rightNode.insert(newRect);
	    }
	    return insertNode;
	} else {
	    //Node already occupied
	    if (imgRect != null) {
		return null;
	    }

	    // Area to small, won't fit
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
		//Split current node horizontally
		leftNode.setRect(new WebRect(rect.getX1(), rect.getY1(), rect.getX1() + newRect.getWidth(), rect.getY2()));
		rightNode.setRect(new WebRect(rect.getX1() + newRect.getWidth(), rect.getY1(), rect.getX2(), rect.getY2()));
	    } else {
		//Spilt current node vertically
		leftNode.setRect(new WebRect(rect.getX1(), rect.getY1(), rect.getX2(), rect.getY1() + newRect.getHeight()));
		rightNode.setRect(new WebRect(rect.getX1(), rect.getY1() + newRect.getHeight(), rect.getX2(), rect.getY2()));
	    }

	    //Insert into the newly created, left node.
	    return leftNode.insert(newRect);
	}
    }

    protected void setRect(WebRect rect) {
	this.rect = rect;
    }
}

/**
 * Comperator used for sorting ScreenUpdates by size.
 * 
 * @author Clemens Eisserer <linuxhippy@gmail.com>
 */
class ScreenUpdateComperator implements Comparator<ScreenUpdate> {
    @Override
    public int compare(ScreenUpdate s1, ScreenUpdate s2) {
	WebRect u1 = s1.getUpdateArea();
	WebRect u2 = s2.getUpdateArea();

	return (u2.getWidth() * u2.getHeight()) - (u1.getWidth() * u1.getHeight());
    }
}
