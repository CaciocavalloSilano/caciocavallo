/*
 * Copyright (c) 2012, Oracle and/or its affiliates. All rights reserved.
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

package com.github.caciocavallosilano.cacio.ctc;

import static org.junit.Assert.assertTrue;

import java.awt.Frame;
import java.awt.Window;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import com.github.caciocavallosilano.cacio.ctc.junit.CacioTestRunner;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(CacioTestRunner.class)
public class WindowLeakTest {

    private ReferenceQueue<Window> windowQueue;
    private List<WeakReference<Window>> weakRefs;

    @Before
    public void setUp() {
        windowQueue = new ReferenceQueue<>();
        weakRefs = new ArrayList<>();
    }

    @After
    public void tearDown() {
        weakRefs = null;
        windowQueue = null;
    }

    @Test
    public void test() throws InterruptedException {

        for (int i = 0; i < 1000; i++) {
            createDisposeWindow();
        }

        for (int i = 0; i < 1000; i++) {
            System.gc();
            System.runFinalization();
        }
        
        int numFinalizedWindows = 0;
        while (true) {
            Reference<? extends Window> ref = windowQueue.poll();
            if (ref == null) {
                break;
            }
            numFinalizedWindows++;
        }
        System.out.println("Number of finalized windows: " + numFinalizedWindows);
        assertTrue(numFinalizedWindows > 0);

    }

    private void createDisposeWindow() {
        Window w = new Frame();
        w.setSize(100, 100);
        w.setVisible(true);
        w.dispose();
        weakRefs.add(new WeakReference<>(w, windowQueue));
    }

}
