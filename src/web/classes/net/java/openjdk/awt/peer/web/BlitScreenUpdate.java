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
import java.util.List;

/**
 * 
 * @author Clemens Eisserer <linuxhippy@gmail.com>
 */
public class BlitScreenUpdate extends ScreenUpdate {

    int packedX, packedY;
    int srcX, srcY;

    BufferedImage image;
    boolean isEvacuated;

    public BlitScreenUpdate(int dstX, int dstY, int srcX, int srcY, int w, int h, BufferedImage src) {
	super(new WebRect(dstX, dstY, dstX + w, dstY + h));

	this.srcX = srcX;
	this.srcY = srcY;
	this.isEvacuated = false;

	this.image = src;
    }

    public void evacuate() {
	if (!isEvacuated) {
	    BufferedImage src = image;

	    int w = getUpdateArea().getWidth();
	    int h = getUpdateArea().getHeight();

	    image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
	    Graphics g = image.getGraphics();
	    g.drawImage(src, 0, 0, w, h, srcX, srcY, srcX + w, srcY + h, null);
	    setSrcX(0);
	    setSrcY(0);
	    
	    isEvacuated = true;
	}
    }

    public void writeCmdStream(List<Integer> cmdList) {
	cmdList.add(0);
	cmdList.add(updateArea.getX1());
	cmdList.add(updateArea.getY1());
	cmdList.add(updateArea.getX2());
	cmdList.add(updateArea.getY2());
	cmdList.add(packedX);
	cmdList.add(packedY);
    }

    public BufferedImage getImage() {
	return image;
    }

    public int getPackedX() {
	return packedX;
    }

    public int getPackedY() {
	return packedY;
    }

    public void setPackedX(int packedX) {
	this.packedX = packedX;
    }

    public void setPackedY(int packedY) {
	this.packedY = packedY;
    }

    public int getSrcX() {
	return srcX;
    }

    public int getSrcY() {
	return srcY;
    }

    public void setSrcX(int srcX) {
	this.srcX = srcX;
    }

    public void setSrcY(int srcY) {
	this.srcY = srcY;
    }
}
