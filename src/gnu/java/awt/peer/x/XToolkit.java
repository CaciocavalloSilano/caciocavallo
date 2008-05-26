/* XToolkit.java -- The central AWT Toolkit for the X peers
   Copyright (C) 2006 Free Software Foundation, Inc.

This file is part of GNU Classpath.

GNU Classpath is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2, or (at your option)
any later version.

GNU Classpath is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
General Public License for more details.

You should have received a copy of the GNU General Public License
along with GNU Classpath; see the file COPYING.  If not, write to the
Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
02110-1301 USA.

Linking this library statically or dynamically with other modules is
making a combined work based on this library.  Thus, the terms and
conditions of the GNU General Public License cover the whole
combination.

As a special exception, the copyright holders of this library give you
permission to link this library with independent modules to produce an
executable, regardless of the license terms of these independent
modules, and to copy and distribute the resulting executable under
terms of your choice, provided that you also meet, for each linked
independent module, the terms and conditions of the license of that
module.  An independent module is a module which is not derived from
or based on this library.  If you modify this library, you may extend
this exception to your version of the library, but you are not
obligated to do so.  If you do not wish to do so, delete this
exception statement from your version. */


package gnu.java.awt.peer.x;

import java.awt.AWTException;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.CheckboxMenuItem;
import java.awt.Choice;
import java.awt.Desktop;
import java.awt.Dialog;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.KeyboardFocusManager;
import java.awt.Label;
import java.awt.List;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.PrintJob;
import java.awt.Robot;
import java.awt.ScrollPane;
import java.awt.Scrollbar;
import java.awt.SystemTray;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.TrayIcon;
import java.awt.Window;
import java.awt.Dialog.ModalExclusionType;
import java.awt.Dialog.ModalityType;
import java.awt.datatransfer.Clipboard;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.peer.DragSourceContextPeer;
import java.awt.font.TextAttribute;
import java.awt.im.InputMethodHighlight;
import java.awt.im.spi.InputMethodDescriptor;
import java.awt.image.ColorModel;
import java.awt.peer.ButtonPeer;
import java.awt.peer.CheckboxMenuItemPeer;
import java.awt.peer.CheckboxPeer;
import java.awt.peer.ChoicePeer;
import java.awt.peer.DesktopPeer;
import java.awt.peer.DialogPeer;
import java.awt.peer.FileDialogPeer;
import java.awt.peer.FontPeer;
import java.awt.peer.FramePeer;
import java.awt.peer.KeyboardFocusManagerPeer;
import java.awt.peer.LabelPeer;
import java.awt.peer.ListPeer;
import java.awt.peer.MenuBarPeer;
import java.awt.peer.MenuItemPeer;
import java.awt.peer.MenuPeer;
import java.awt.peer.PopupMenuPeer;
import java.awt.peer.RobotPeer;
import java.awt.peer.ScrollPanePeer;
import java.awt.peer.ScrollbarPeer;
import java.awt.peer.SystemTrayPeer;
import java.awt.peer.TextAreaPeer;
import java.awt.peer.TextFieldPeer;
import java.awt.peer.TrayIconPeer;
import java.awt.peer.WindowPeer;
import java.util.Map;
import java.util.Properties;

import sun.awt.KeyboardFocusManagerPeerProvider;
import sun.awt.SunToolkit;

public class XToolkit
  extends SunToolkit implements KeyboardFocusManagerPeerProvider
{

  static final boolean DEBUG = false;

  @Override
  public ButtonPeer createButton(Button target)
  {
    // TODO: Implement this.
    throw new UnsupportedOperationException("Not yet implemented.");
  }

  @Override
  public TextFieldPeer createTextField(TextField target)
  {
    // TODO: Implement this.
    throw new UnsupportedOperationException("Not yet implemented.");
  }

  @Override
  public LabelPeer createLabel(Label target)
  {
    // TODO: Implement this.
    throw new UnsupportedOperationException("Not yet implemented.");
  }

  @Override
  public ListPeer createList(List target)
  {
    // TODO: Implement this.
    throw new UnsupportedOperationException("Not yet implemented.");
  }

  @Override
  public CheckboxPeer createCheckbox(Checkbox target)
  {
    // TODO: Implement this.
    throw new UnsupportedOperationException("Not yet implemented.");
  }

  @Override
  public ScrollbarPeer createScrollbar(Scrollbar target)
  {
    // TODO: Implement this.
    throw new UnsupportedOperationException("Not yet implemented.");
  }

  @Override
  public ScrollPanePeer createScrollPane(ScrollPane target)
  {
    // TODO: Implement this.
    throw new UnsupportedOperationException("Not yet implemented.");
  }

  @Override
  public TextAreaPeer createTextArea(TextArea target)
  {
    // TODO: Implement this.
    throw new UnsupportedOperationException("Not yet implemented.");
  }

  @Override
  public ChoicePeer createChoice(Choice target)
  {
    // TODO: Implement this.
    throw new UnsupportedOperationException("Not yet implemented.");
  }

  @Override
  public FramePeer createFrame(Frame target)
  {
    XFramePeer frame = new XFramePeer(target);
    return frame;
  }

  @Override
  public WindowPeer createWindow(Window target)
  {
    return new XWindowPeer(target);
  }

  @Override
  public DialogPeer createDialog(Dialog target)
  {
    return new XDialogPeer(target);
  }

  @Override
  public MenuBarPeer createMenuBar(MenuBar target)
  {
    // TODO: Implement this.
    throw new UnsupportedOperationException("Not yet implemented.");
  }

  @Override
  public MenuPeer createMenu(Menu target)
  {
    // TODO: Implement this.
    throw new UnsupportedOperationException("Not yet implemented.");
  }

  @Override
  public PopupMenuPeer createPopupMenu(PopupMenu target)
  {
    // TODO: Implement this.
    throw new UnsupportedOperationException("Not yet implemented.");
  }

  @Override
  public MenuItemPeer createMenuItem(MenuItem target)
  {
    // TODO: Implement this.
    throw new UnsupportedOperationException("Not yet implemented.");
  }

  @Override
  public FileDialogPeer createFileDialog(FileDialog target)
  {
    // TODO: Implement this.
    throw new UnsupportedOperationException("Not yet implemented.");
  }

  @Override
  public CheckboxMenuItemPeer createCheckboxMenuItem(CheckboxMenuItem target)
  {
    // TODO: Implement this.
    throw new UnsupportedOperationException("Not yet implemented.");
  }

  @Override
  public FontPeer getFontPeer(String name, int style)
  {
    // TODO: Implement this.
    throw new UnsupportedOperationException("Not yet implemented.");
  }

  @Override
  public int getScreenResolution()
  {
    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    GraphicsDevice gd = ge.getDefaultScreenDevice();
    GraphicsConfiguration gc = gd.getDefaultConfiguration();
    XGraphicsConfiguration xgc = (XGraphicsConfiguration) gc;

    return xgc.getResolution();
  }

  /**
   * Returns the color model used by this toolkit.
   *
   * @return the color model used by this toolkit
   */
  @Override
  public ColorModel getColorModel()
  {
    // TODO: I assume 24 bit depth here, we can do this better.
    return ColorModel.getRGBdefault();
  }

  @Override
  public void sync()
  {
    // TODO: Implement this.
    throw new UnsupportedOperationException("Not yet implemented.");
  }

  @Override
  public PrintJob getPrintJob(Frame frame, String title, Properties props)
  {
    // TODO: Implement this.
    throw new UnsupportedOperationException("Not yet implemented.");
  }

  @Override
  public void beep()
  {
    // TODO: Implement this.
    throw new UnsupportedOperationException("Not yet implemented.");
  }

  @Override
  public Clipboard getSystemClipboard()
  {
    // TODO: Implement this.
    throw new UnsupportedOperationException("Not yet implemented.");
  }

  @Override
  public DragSourceContextPeer createDragSourceContextPeer(DragGestureEvent e)
  {
    // TODO: Implement this.
    throw new UnsupportedOperationException("Not yet implemented.");
  }

  @Override
  public Map<TextAttribute,?> mapInputMethodHighlight(InputMethodHighlight highlight)
  {
    // TODO: Implement this.
    throw new UnsupportedOperationException("Not yet implemented.");
  }

  @Override
  public boolean isModalExclusionTypeSupported(ModalExclusionType modalExclusionType)
  {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean isModalityTypeSupported(ModalityType modalityType)
  {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  protected DesktopPeer createDesktopPeer(Desktop target)
    throws HeadlessException
  {
	// TODO Auto-generated method stub
	return null;
  }

  @Override
  public KeyboardFocusManagerPeer createKeyboardFocusManagerPeer(KeyboardFocusManager manager)
  {
    // TODO Auto-generated method stub
    System.err.println("XToolkit::createKeyboardFocusManagerPeer: !!!IMPLEMENT ME!!!");
    return null;
  }

  @Override
  public RobotPeer createRobot(Robot arg0, GraphicsDevice arg1) throws AWTException
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public SystemTrayPeer createSystemTray(SystemTray arg0)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public TrayIconPeer createTrayIcon(TrayIcon arg0) throws HeadlessException, AWTException
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  protected int getScreenHeight()
  {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  protected int getScreenWidth()
  {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public void grab(Window arg0)
  {
    // TODO Auto-generated method stub
    
  }

  @Override
  public boolean isDesktopSupported()
  {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean isTraySupported()
  {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  protected boolean syncNativeQueue(long arg0)
  {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public void ungrab(Window arg0)
  {
    // TODO Auto-generated method stub
    
  }

  public InputMethodDescriptor getInputMethodAdapterDescriptor() throws AWTException
  {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * Helper method to quickly fetch the default device (X Display).
   *
   * @return the default XGraphicsDevice
   */
  static XGraphicsDevice getDefaultDevice()
  {
    XGraphicsEnvironment env = (XGraphicsEnvironment)
      XGraphicsEnvironment.getLocalGraphicsEnvironment();
    return (XGraphicsDevice) env.getDefaultScreenDevice();
  }

}
