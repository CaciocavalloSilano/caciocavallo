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
import java.awt.datatransfer.Clipboard;
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

import java.util.logging.Level;
import java.util.logging.Logger;

import net.java.openjdk.cacio.servlet.*;

import sun.awt.*;
import sun.awt.datatransfer.DataTransferer;
import sun.awt.peer.cacio.CacioToolkit;
import sun.awt.peer.cacio.PlatformWindowFactory;

/**
 * Toolkit implementation for Caciocavallo-Web
 * @author Clemens Eisserer <linuxhippy@gmail.com>
 * @author Mario Torre <neugens.limasoftware@gmail.com>
 */
public class WebToolkit extends CacioToolkit {

    private static Logger logger =
        Logger.getLogger("net.java.openjdk.awt.peer.web.CacioToolkit");

    private PlatformWindowFactory platformWindow;

    static {
        LibraryLoader.loadLibs();
    }

    public WebToolkit() {
        super();
        setDecorateWindows(true);
    }

    @Override
    public synchronized PlatformWindowFactory getPlatformWindowFactory() {
	WebSessionState state = WebSessionManager.getInstance().getCurrentState();
        
	WebWindowFactory factory;
	if ((factory = state.getWindowFactory()) == null) {
	    factory = new WebWindowFactory();
	   state.setWindowFactory(factory);
        }
        return factory;
    }

    @Override
    public DragSourceContextPeer createDragSourceContextPeer(DragGestureEvent dge) throws InvalidDnDOperationException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public TrayIconPeer createTrayIcon(TrayIcon target) throws HeadlessException, AWTException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public SystemTrayPeer createSystemTray(SystemTray target) {
	return new WebSystemTrayPeer();
    }

    @Override
    public boolean isTraySupported() {
	return false;
    }

    @Override
    public FontPeer getFontPeer(String name, int style) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public RobotPeer createRobot(Robot target, GraphicsDevice screen) throws AWTException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected int getScreenWidth() {
        
        GraphicsConfiguration config =
                WebGraphicsConfiguration.getDefaultConfiguration();
        return config.getBounds().width;
    }

    @Override
    protected int getScreenHeight() {
        
        GraphicsConfiguration config =
                WebGraphicsConfiguration.getDefaultConfiguration();
        return config.getBounds().height;
    }

    @Override
    protected boolean syncNativeQueue(long timeout) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void grab(Window w) {
	System.out.println("Grabbing not implemented: grab");
    }

    @Override
    public void ungrab(Window w) {
	System.out.println("Grabbing not implemented: ungrab");
    }

    @Override
    public boolean isDesktopSupported() {
        return false;
    }

    @Override
    protected DesktopPeer createDesktopPeer(Desktop target)
            throws HeadlessException {

        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getScreenResolution() throws HeadlessException {
        return 72;
    }

    @Override
    public ColorModel getColorModel() throws HeadlessException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void sync() {
        /* not needed for Web */
    }

    @Override
    public PrintJob getPrintJob(Frame frame, String jobtitle,
                                Properties props) {
        
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void beep() {
        logger.log(Level.FINE, "BEEP");
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
}
