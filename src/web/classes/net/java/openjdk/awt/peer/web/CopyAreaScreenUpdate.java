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
import sun.java2d.pipe.*;

/**
 * ScreenUpdate generated when state between browser and server is synchronized
 * by sending a copyArea.
 * 
 * The browser also needs to honor the currently set clip.
 * 
 * @see ScreenUpdate
 * @see BlitScreenUpdate
 * @author Clemens Eisserer <linuxhippy@gmail.com>
 */
public class CopyAreaScreenUpdate extends ScreenUpdate {

    int dx, dy;
    Region clip;
    
    /**
     * 
     * @param x1 x1 coordinate of src area
     * @param y1 
     * @param x2 
     * @param y2
     * @param dx - x difference between src and dst pos
     * @param dy
     * @param clip - clip, if one has been applied.
     */
    public CopyAreaScreenUpdate(int x1, int y1, int x2, int y2, int dx, int dy, Region clip) {
	super(new WebRect(x1, y1, x2, y2));
	this.dx = dx;
	this.dy = dy;
	this.clip = clip;
    }

    @Override
    public void writeToCmdStream(List<Integer> cmdList) {
	cmdList.add(1);
	cmdList.add(updateArea.getX1());
	cmdList.add(updateArea.getY1());
	cmdList.add(updateArea.getX2());
	cmdList.add(updateArea.getY2());
	cmdList.add(dx);
	cmdList.add(dy);
	cmdList.add(clip.getLoX());
	cmdList.add(clip.getLoY());
	cmdList.add(clip.getWidth());
	cmdList.add(clip.getHeight());
    }

    @Override
    public String toString() {
	return "CopyAreaScreenUpdate [dx=" + dx + ", dy=" + dy + ", updateArea=" + updateArea + "]";
    }
}
