/*
 * Copyright (c) 2014, Oracle and/or its affiliates. All rights reserved.
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

package net.java.openjdk.cacio.directfb;

import java.awt.AWTException;
import java.awt.Desktop;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
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
import java.awt.peer.DesktopPeer;
import java.awt.peer.FontPeer;
import java.awt.peer.FramePeer;
import java.awt.peer.RobotPeer;
import java.awt.peer.SystemTrayPeer;
import java.awt.peer.TrayIconPeer;
import java.util.Map;
import java.util.Properties;

import sun.awt.LightweightFrame;
import sun.awt.datatransfer.DataTransferer;
import sun.awt.peer.cacio.CacioToolkit;
import sun.awt.peer.cacio.PlatformWindowFactory;
import sun.awt.peer.cacio.managed.FullScreenWindowFactory;

public class DirectFBToolkit extends CacioToolkit {

    static {
        System.setProperty("sun.font.fontmanager", "sun.awt.FcFontManager");
    }

    private PlatformWindowFactory windowFactory;

    public DirectFBToolkit() {
        // Nothing to do here.
    }

    @Override
    public PlatformWindowFactory getPlatformWindowFactory() {
        if (windowFactory == null) {
            DirectFBScreen screen = new DirectFBScreen();
            DirectFBEventSource eventSource = new DirectFBEventSource(screen);
            windowFactory = new FullScreenWindowFactory(screen, eventSource);
        }
        return windowFactory;
    }

    @Override
    public boolean isDesktopSupported() {
        return false;
    }

    @Override
    public void grab(Window w) {
        throw new InternalError("NYI");
    }

    @Override
    public void ungrab(Window w) {
        throw new InternalError("NYI");
    }

    @Override
    protected boolean syncNativeQueue(long timeout) {
        throw new InternalError("NYI");
    }

    @Override
    protected int getScreenWidth() {
        DirectFBGraphicsConfiguration conf = DirectFBGraphicsConfiguration.getDefaultConfiguration();
        return conf.getBounds().width;
    }

    @Override
    protected int getScreenHeight() {
        DirectFBGraphicsConfiguration conf = DirectFBGraphicsConfiguration.getDefaultConfiguration();
        return conf.getBounds().height;
    }

    @Override
    public FontPeer getFontPeer(String name, int style) {
        throw new InternalError("NYI");
    }

    @Override
    public boolean isTraySupported() {
        return false;
    }

    @Override
    public SystemTrayPeer createSystemTray(SystemTray target) {
        throw new InternalError("NYI");
    }

    @Override
    public TrayIconPeer createTrayIcon(TrayIcon target)
            throws HeadlessException, AWTException {

        throw new InternalError("NYI");

    }

    @Override
    public FramePeer createLightweightFrame(LightweightFrame target)
            throws HeadlessException {

        throw new InternalError("NYI");
    }

    @Override
    public void beep() {
        throw new InternalError("NYI");
    }

    @Override
    public InputMethodDescriptor getInputMethodAdapterDescriptor() throws AWTException {
        return null;
    }

    @Override
    public DragSourceContextPeer createDragSourceContextPeer(
            DragGestureEvent arg0) throws InvalidDnDOperationException {
        throw new InternalError("NYI");
    }

    @Override
    public RobotPeer createRobot(Robot arg0, GraphicsDevice arg1)
            throws AWTException {
        throw new InternalError("NYI");
    }

    @Override
    protected DesktopPeer createDesktopPeer(Desktop arg0)
            throws HeadlessException {
        throw new InternalError("NYI");
    }

    @Override
    public ColorModel getColorModel() throws HeadlessException {
        throw new InternalError("NYI");
    }

    @Override
    public PrintJob getPrintJob(Frame arg0, String arg1, Properties arg2) {
        throw new InternalError("NYI");
    }

    @Override
    public Map<TextAttribute, ?> mapInputMethodHighlight(
            InputMethodHighlight arg0) throws HeadlessException {
        throw new InternalError("NYI");
    }

    @Override
    public void sync() {
        throw new InternalError("NYI");
    }

    @Override
    public DataTransferer getDataTransferer() {
        throw new InternalError("NYI");
    }
}
