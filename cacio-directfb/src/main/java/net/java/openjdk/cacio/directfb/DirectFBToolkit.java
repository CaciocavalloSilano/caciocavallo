package net.java.openjdk.cacio.directfb;

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

public class DirectFBToolkit extends CacioToolkit {

    public DirectFBToolkit() {
        throw new InternalError("NYI");
    }

    @Override
    public PlatformWindowFactory getPlatformWindowFactory() {
        throw new InternalError("NYI");
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
        throw new InternalError("NYI");
    }

    @Override
    protected int getScreenHeight() {
        throw new InternalError("NYI");
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
    public InputMethodDescriptor getInputMethodAdapterDescriptor()
            throws AWTException {
        throw new InternalError("NYI");
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
