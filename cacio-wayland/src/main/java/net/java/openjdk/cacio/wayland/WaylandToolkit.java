/*
 * Copyright (c) 2016, Oracle and/or its affiliates. All rights reserved.
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

package net.java.openjdk.cacio.wayland;

import java.awt.*;
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
import sun.awt.peer.cacio.*;
import sun.awt.peer.cacio.managed.FullScreenWindowFactory;

public class WaylandToolkit extends CacioToolkit {

    static {
        System.setProperty("sun.font.fontmanager", "sun.awt.FcFontManager");
    }

    static WaylandScreenSelector screenSelector = new WaylandScreenSelector();

    private PlatformWindowFactory windowFactory;
    public WaylandToolkit() {
    }

    @Override
    public PlatformWindowFactory getPlatformWindowFactory() {
        if (windowFactory == null) {
            WaylandEventSource eventSource = new WaylandEventSource(screenSelector);
            windowFactory = new FullScreenWindowFactory(screenSelector, eventSource);
        }
        return windowFactory;
    }

    @Override
    public FramePeer createLightweightFrame(LightweightFrame lightweightFrame) throws HeadlessException {
        return null;
    }
    @Override
    public DragSourceContextPeer createDragSourceContextPeer(DragGestureEvent dragGestureEvent) throws InvalidDnDOperationException {
        return null;
    }

    @Override
    public Map<TextAttribute, ?> mapInputMethodHighlight(InputMethodHighlight inputMethodHighlight) throws HeadlessException {
        return null;
    }

    @Override
    public TrayIconPeer createTrayIcon(TrayIcon trayIcon) throws HeadlessException, AWTException {
        return null;
    }

    @Override
    public SystemTrayPeer createSystemTray(SystemTray systemTray) {
        return null;
    }

    @Override
    public boolean isTraySupported() {
        return false;
    }

    @Override
    protected DesktopPeer createDesktopPeer(Desktop desktop) throws HeadlessException {
        return null;
    }

    @Override
    public FontPeer getFontPeer(String s, int i) {
        return null;
    }

    @Override
    public ColorModel getColorModel() throws HeadlessException {
        return null;
    }

    @Override
    public void sync() {

    }

    @Override
    public PrintJob getPrintJob(Frame frame, String s, Properties properties) {
        return null;
    }

    @Override
    public void beep() {

    }

    @Override
    public RobotPeer createRobot(Robot robot, GraphicsDevice graphicsDevice) throws AWTException {
        return null;
    }

    @Override
    public DataTransferer getDataTransferer() {
        return null;
    }

    @Override
    protected int getScreenWidth() {
        return WaylandGraphicsConfiguration.getDefaultConfiguration().getBounds().width;
    }

    @Override
    protected int getScreenHeight() {
        return WaylandGraphicsConfiguration.getDefaultConfiguration().getBounds().height;
    }

    @Override
    protected boolean syncNativeQueue(long l) {
        return false;
    }

    @Override
    public void grab(Window window) {

    }

    @Override
    public void ungrab(Window window) {

    }

    @Override
    public boolean isDesktopSupported() {
        return false;
    }

    @Override
    public InputMethodDescriptor getInputMethodAdapterDescriptor() throws AWTException {
        return null;
    }
}
