/*
 * Copyright 2008 Sun Microsystems, Inc.  All Rights Reserved.
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

package gnu.java.awt.peer.x;

import java.awt.AWTException;
import java.awt.Desktop;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
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
import sun.awt.peer.cacio.PlatformWindowFactory;

public class EscherToolkit extends CacioToolkit {

    static final boolean DEBUG = false; 

    private PlatformWindowFactory platformWindowFactory;

    @Override
    public synchronized PlatformWindowFactory getPlatformWindowFactory() {
        if (platformWindowFactory == null) {
            platformWindowFactory = new EscherPlatformWindowFactory();
        }
        return platformWindowFactory;
    }

    @Override
    public DragSourceContextPeer createDragSourceContextPeer(
            DragGestureEvent dge) throws InvalidDnDOperationException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public RobotPeer createRobot(Robot target, GraphicsDevice screen)
            throws AWTException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SystemTrayPeer createSystemTray(SystemTray target) {
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
    public FontPeer getFontPeer(String name, int style) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected int getScreenHeight() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    protected int getScreenWidth() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void grab(Window w) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isDesktopSupported() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isTraySupported() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    protected boolean syncNativeQueue(long timeout) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void ungrab(Window w) {
        // TODO Auto-generated method stub

    }

    @Override
    public void beep() {
        // TODO Auto-generated method stub

    }

    @Override
    protected DesktopPeer createDesktopPeer(Desktop target)
            throws HeadlessException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ColorModel getColorModel() throws HeadlessException {
        // TODO: I assume 24 bit depth here, we can do this better.
        return ColorModel.getRGBdefault();
    }

    @Override
    public PrintJob getPrintJob(Frame frame, String jobtitle, Properties props) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getScreenResolution() throws HeadlessException {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        GraphicsConfiguration gc = gd.getDefaultConfiguration();
        XGraphicsConfiguration xgc = (XGraphicsConfiguration) gc;

        return xgc.getResolution();
    }

    @Override
    public Clipboard getSystemClipboard() throws HeadlessException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<TextAttribute, ?> mapInputMethodHighlight(
            InputMethodHighlight highlight) throws HeadlessException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void sync() {
        // TODO Auto-generated method stub

    }

    @Override
    public InputMethodDescriptor getInputMethodAdapterDescriptor()
            throws AWTException {
        // TODO Auto-generated method stub
        return null;
    }

    public static XGraphicsDevice getDefaultDevice() {
        XGraphicsEnvironment env = (XGraphicsEnvironment)
                            XGraphicsEnvironment.getLocalGraphicsEnvironment();
        return (XGraphicsDevice) env.getDefaultScreenDevice();
    }

}
