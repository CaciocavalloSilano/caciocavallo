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

package net.java.openjdk.cacio.servlet.benchmark;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

/**
 * 
 * @author Clemens Eisserer <linuxhippy@gmail.com>
 */
public class ImgBenchServlet extends HttpServlet {

    byte[] imgData;
    
    public ImgBenchServlet() {
	imgData = loadFileInArray("150.rle");
    }
    
    protected byte[] loadFileInArray(String fileName) {
	try {
	    FileInputStream fin = new FileInputStream(fileName);
	    ByteArrayOutputStream bos = new ByteArrayOutputStream();
	    
	    int read = 0;
	    while((read = fin.read()) != -1) {
		bos.write(read);
	    }
	    
	    fin.close();
	    return bos.toByteArray();
	}catch(IOException ex) {
	    ex.printStackTrace();
	}
	
	return null;
    }
    
    int counter = 0;
    long lastTime = 0;
    @Override
    public synchronized void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	disableCaching(resp);
	resp.setContentType("image/png");
	
	if(counter % 1000 == 0) {
	    if(counter != 0) {
		System.out.println("Time for 1000 images: "+(System.currentTimeMillis() - lastTime));
	    }
	    lastTime = System.currentTimeMillis();
	}
	
//	try {
//	    Thread.sleep(35);
//	} catch (InterruptedException e) {
//	    e.printStackTrace();
//	}
	
	resp.getOutputStream().write(imgData);
	
	counter++;
    }

    protected void disableCaching(HttpServletResponse response) {
	response.setHeader("Expires", "Sat, 1 May 2000 12:00:00 GMT");
	response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
	response.addHeader("Cache-Control", "post-check=0, pre-check=0");
	response.setHeader("Pragma", "no-cache");
    }
}
