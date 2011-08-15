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
 * 
 * @author Clemens Eisserer <linuxhippy@gmail.com>
 */
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
