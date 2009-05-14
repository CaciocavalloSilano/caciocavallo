/*
 * Copyright 2009 Sun Microsystems, Inc.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the LICENSE file that accompanied this code.
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
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * CA 95054 USA or visit www.sun.com if you need additional information or
 * have any questions.
 */

package sun.awt.peer.x11;

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
import java.awt.datatransfer.Clipboard;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.InvalidDnDOperationException;
import java.awt.dnd.peer.DragSourceContextPeer;
import java.awt.font.TextAttribute;
import java.awt.im.InputMethodHighlight;
import java.awt.im.spi.InputMethodDescriptor;
import java.awt.image.ColorModel;
import java.awt.peer.DesktopPeer;
import java.awt.peer.FontPeer;
import java.awt.peer.RobotPeer;
import java.awt.peer.SystemTrayPeer;
import java.awt.peer.TrayIconPeer;
import java.util.Map;
import java.util.Properties;
import sun.awt.peer.cacio.CacioToolkit;
import sun.awt.peer.cacio.FullScreenWindowFactory;
import sun.awt.peer.cacio.PlatformWindowFactory;

public class X11Toolkit extends CacioToolkit {

    static {
        System.loadLibrary("cacio-x11");
    }

    private PlatformWindowFactory platformWindowFactory;

    @Override
    public PlatformWindowFactory getPlatformWindowFactory() {
        if (platformWindowFactory == null) {
            platformWindowFactory = createPlatformWindowFactory();
        }
        return platformWindowFactory;
    }

    private PlatformWindowFactory createPlatformWindowFactory() {
        X11PlatformScreen screen = new X11PlatformScreen();
        PlatformWindowFactory factory =
                new FullScreenWindowFactory(screen, screen);
        return factory;
    }

    @Override
    public DragSourceContextPeer createDragSourceContextPeer(DragGestureEvent arg0) throws InvalidDnDOperationException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public TrayIconPeer createTrayIcon(TrayIcon arg0) throws HeadlessException, AWTException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public SystemTrayPeer createSystemTray(SystemTray arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isTraySupported() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public FontPeer getFontPeer(String arg0, int arg1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public RobotPeer createRobot(Robot arg0, GraphicsDevice arg1) throws AWTException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected int getScreenWidth() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected int getScreenHeight() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected boolean syncNativeQueue(long arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void grab(Window arg0) {
        System.err.println("IMPLEMENT ME: X11Toolkit.grab()");
    }

    @Override
    public void ungrab(Window arg0) {
        System.err.println("IMPLEMENT ME: X11Toolkit.ungrab()");
    }

    @Override
    public boolean isDesktopSupported() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected DesktopPeer createDesktopPeer(Desktop target) throws HeadlessException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getScreenResolution() throws HeadlessException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ColorModel getColorModel() throws HeadlessException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void sync() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public PrintJob getPrintJob(Frame frame, String jobtitle, Properties props) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void beep() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Clipboard getSystemClipboard() throws HeadlessException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Map<TextAttribute, ?> mapInputMethodHighlight(InputMethodHighlight highlight) throws HeadlessException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public InputMethodDescriptor getInputMethodAdapterDescriptor() throws AWTException {
        // TODO: Implement.
        return null;
    }

}
