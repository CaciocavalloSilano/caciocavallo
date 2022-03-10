/*
 * Copyright (c) 2011, Oracle and/or its affiliates. All rights reserved.
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

import java.awt.AWTException;
import java.awt.Desktop;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.HeadlessException;
import java.awt.PrintJob;
import java.awt.Robot;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.Window;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.InvalidDnDOperationException;
import java.awt.dnd.peer.DragSourceContextPeer;
import java.awt.font.TextAttribute;
import java.awt.im.InputMethodHighlight;
import java.awt.im.spi.InputMethodDescriptor;
import java.awt.image.ColorModel;
import java.awt.peer.*;
import java.util.Map;
import java.util.Properties;

import sun.awt.LightweightFrame;
import sun.awt.datatransfer.DataTransferer;
import com.github.caciocavallosilano.cacio.peer.CacioToolkit;
import com.github.caciocavallosilano.cacio.peer.PlatformWindowFactory;
import com.github.caciocavallosilano.cacio.peer.managed.FullScreenWindowFactory;

public class CTCToolkit extends CacioToolkit {

    private PlatformWindowFactory platformWindowFactory;

    public CTCToolkit() {
        setDecorateWindows(true);
        System.setProperty("swing.defaultlaf", "javax.swing.plaf.metal.MetalLookAndFeel");
    }

    @Override
    public PlatformWindowFactory getPlatformWindowFactory() {
        if (platformWindowFactory == null) {
          CTCScreen screen = CTCScreen.getInstance();
          CTCEventSource eventSource = CTCEventSource.getInstance();
          platformWindowFactory = new FullScreenWindowFactory(screen, eventSource);
        }
        return platformWindowFactory;
    }

    @Override
    public InputMethodDescriptor getInputMethodAdapterDescriptor()
            throws AWTException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DragSourceContextPeer createDragSourceContextPeer(
            DragGestureEvent dge) throws InvalidDnDOperationException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public TrayIconPeer createTrayIcon(TrayIcon target)
            throws HeadlessException, AWTException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SystemTrayPeer createSystemTray(SystemTray target) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isTraySupported() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public FontPeer getFontPeer(String name, int style) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public RobotPeer createRobot(GraphicsDevice screen) throws AWTException {
        return new CTCRobotPeer();
    }

    protected int getScreenWidth() {
        return FullScreenWindowFactory.getScreenDimension().width;
    }

    protected int getScreenHeight() {
        return FullScreenWindowFactory.getScreenDimension().height;
    }

    @Override
    protected boolean syncNativeQueue(long timeout) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void grab(Window w) {
        // TODO Auto-generated method stub

    }

    @Override
    public void ungrab(Window w) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isDesktopSupported() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public DesktopPeer createDesktopPeer(Desktop target)
            throws HeadlessException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ColorModel getColorModel() throws HeadlessException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void sync() {
        // TODO Auto-generated method stub

    }

    @Override
    public PrintJob getPrintJob(Frame frame, String jobtitle, Properties props) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void beep() {
        // TODO Auto-generated method stub

    }

    @Override
    public Map<TextAttribute, ?> mapInputMethodHighlight(
            InputMethodHighlight highlight) throws HeadlessException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public FramePeer createLightweightFrame(LightweightFrame lightweightFrame) throws HeadlessException {
        return null;
    }

    @Override
    public DataTransferer getDataTransferer() {
        return null;
    }

    @Override
    public boolean isTaskbarSupported() {
        return true;
    }
}
