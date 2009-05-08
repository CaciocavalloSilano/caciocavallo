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

package sun.awt.peer.cacio;

import java.awt.Button;
import java.awt.Canvas;
import java.awt.Checkbox;
import java.awt.CheckboxMenuItem;
import java.awt.Choice;
import java.awt.Dialog;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.KeyboardFocusManager;
import java.awt.Label;
import java.awt.List;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Panel;
import java.awt.PopupMenu;
import java.awt.ScrollPane;
import java.awt.Scrollbar;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.Window;
import java.awt.peer.ButtonPeer;
import java.awt.peer.CanvasPeer;
import java.awt.peer.CheckboxMenuItemPeer;
import java.awt.peer.CheckboxPeer;
import java.awt.peer.ChoicePeer;
import java.awt.peer.DialogPeer;
import java.awt.peer.FileDialogPeer;
import java.awt.peer.FramePeer;
import java.awt.peer.KeyboardFocusManagerPeer;
import java.awt.peer.LabelPeer;
import java.awt.peer.ListPeer;
import java.awt.peer.MenuBarPeer;
import java.awt.peer.MenuItemPeer;
import java.awt.peer.MenuPeer;
import java.awt.peer.PanelPeer;
import java.awt.peer.PopupMenuPeer;
import java.awt.peer.ScrollPanePeer;
import java.awt.peer.ScrollbarPeer;
import java.awt.peer.TextAreaPeer;
import java.awt.peer.TextFieldPeer;
import java.awt.peer.WindowPeer;

import sun.awt.SunToolkit;

public abstract class CacioToolkit extends SunToolkit {

    public CacioToolkit() {
        CacioEventSource source = getPlatformWindowFactory().createEventSource();
        new CacioEventPump(source);
    }

    @Override
    public ButtonPeer createButton(Button target) throws HeadlessException {
	CacioButtonPeer peer = new CacioButtonPeer(target,
						   getPlatformWindowFactory());
	SunToolkit.targetCreatedPeer(target, peer);
        return peer;
    }

    @Override
    public CanvasPeer createCanvas(Canvas target) {
	CacioPanelPeer peer = new CacioPanelPeer(target,
					  getPlatformWindowFactory());
	SunToolkit.targetCreatedPeer(target, peer);
        return peer;
    }
   
    @Override
    public CheckboxPeer createCheckbox(Checkbox target)
            throws HeadlessException {
	CacioCheckboxPeer peer = new CacioCheckboxPeer(target,
						   getPlatformWindowFactory());
	SunToolkit.targetCreatedPeer(target, peer);
        return peer;
    }

    @Override
    public CheckboxMenuItemPeer createCheckboxMenuItem(CheckboxMenuItem target)
            throws HeadlessException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ChoicePeer createChoice(Choice target) throws HeadlessException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DialogPeer createDialog(Dialog target) throws HeadlessException {
	CacioDialogPeer peer = new CacioDialogPeer(target,
						 getPlatformWindowFactory());
	SunToolkit.targetCreatedPeer(target, peer);
        return peer;
    }

    @Override
    public FileDialogPeer createFileDialog(FileDialog target)
            throws HeadlessException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public FramePeer createFrame(Frame target) throws HeadlessException {
	CacioFramePeer peer = new CacioFramePeer(target,
						 getPlatformWindowFactory());
	SunToolkit.targetCreatedPeer(target, peer);
        return peer;
    }

    @Override
    public LabelPeer createLabel(Label target) throws HeadlessException {
	CacioLabelPeer peer = new CacioLabelPeer(target,
						 getPlatformWindowFactory());
	SunToolkit.targetCreatedPeer(target, peer);
        return peer;
    }

    @Override
    public ListPeer createList(List target) throws HeadlessException {
        CacioListPeer peer = new CacioListPeer(target,
                                               getPlatformWindowFactory());
        SunToolkit.targetCreatedPeer(target, peer);
        return peer;
    }

    @Override
    public MenuPeer createMenu(Menu target) throws HeadlessException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public MenuBarPeer createMenuBar(MenuBar target) throws HeadlessException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public MenuItemPeer createMenuItem(MenuItem target)
            throws HeadlessException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public PanelPeer createPanel(Panel target) {
	CacioPanelPeer peer = new CacioPanelPeer(target,
						 getPlatformWindowFactory());
	SunToolkit.targetCreatedPeer(target, peer);
        return peer;
    }

    @Override
    public PopupMenuPeer createPopupMenu(PopupMenu target)
            throws HeadlessException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ScrollPanePeer createScrollPane(ScrollPane target)
            throws HeadlessException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ScrollbarPeer createScrollbar(Scrollbar target)
            throws HeadlessException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public TextAreaPeer createTextArea(TextArea target)
            throws HeadlessException {
        CacioTextAreaPeer peer = new CacioTextAreaPeer(target,
                                                   getPlatformWindowFactory());
        SunToolkit.targetCreatedPeer(target, peer);
        return peer;
    }

    @Override
    public TextFieldPeer createTextField(TextField target)
            throws HeadlessException {
        
	CacioTextFieldPeer peer = new CacioTextFieldPeer(target,
			                           getPlatformWindowFactory());
	SunToolkit.targetCreatedPeer(target, peer);
        return peer;
    }

    @Override
    public WindowPeer createWindow(Window target) throws HeadlessException {

	CacioWindowPeer peer = new CacioWindowPeer(target,
						   getPlatformWindowFactory());
	SunToolkit.targetCreatedPeer(target, peer);
        return peer;

    }

    public KeyboardFocusManagerPeer createKeyboardFocusManagerPeer(KeyboardFocusManager manager) throws HeadlessException {
        return CacioKeyboardFocusManagerPeer.getInstance();
    }

    static void disposePeer(Object target, Object peer) {
	SunToolkit.targetDisposedPeer(target, peer);
    }

    public abstract PlatformWindowFactory getPlatformWindowFactory();

}
