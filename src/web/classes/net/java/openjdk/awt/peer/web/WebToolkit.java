package net.java.openjdk.awt.peer.web;

import java.awt.AWTException;
import java.awt.Desktop;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
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

import java.util.logging.Level;
import java.util.logging.Logger;

import sun.awt.peer.cacio.CacioToolkit;
import sun.awt.peer.cacio.PlatformWindowFactory;

public class WebToolkit extends CacioToolkit {

    private static Logger logger =
        Logger.getLogger("net.java.openjdk.awt.peer.web.CacioToolkit");

    private PlatformWindowFactory platformWindow;

    static {
        System.loadLibrary("cacio-web");
    }

    public WebToolkit() {
        super();
        setDecorateWindows(true);
    }

    @Override
    public synchronized PlatformWindowFactory getPlatformWindowFactory() {

        if (platformWindow == null) {
            platformWindow = new WebWindowFactory();
        }
        return platformWindow;
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
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isTraySupported() {
        throw new UnsupportedOperationException("Not supported yet.");
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
        /* not needed for SDL */
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


}
