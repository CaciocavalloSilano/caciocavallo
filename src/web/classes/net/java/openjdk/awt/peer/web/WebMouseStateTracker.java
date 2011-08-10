package net.java.openjdk.awt.peer.web;

import java.awt.event.*;
import java.util.*;

import sun.awt.peer.cacio.managed.*;

public class WebMouseStateTracker {
    WebScreen screen;

    EventData lastMouseEvent = null;
    EventData lastClickedEvent = null;
    HashMap<Integer, Boolean> pressedMap = new HashMap<Integer, Boolean>();

    public WebMouseStateTracker(WebScreen screen) {
	this.screen = screen;
    }
    
    public void trackMouseWheelEvent(boolean up, int x, int y) {
	EventData data = new EventData();
	data.setX(x);
	data.setY(y);
	data.setId(MouseEvent.MOUSE_WHEEL);
	data.setButton(up ? 4 : 5);
	
	screen.addEvent(data);
    }

    public void trackMouseMotionEvent(int x, int y) {
	EventData data = new EventData();
	data.setX(x);
	data.setY(y);

	//TODO: Generalize for many buttons
	Boolean pressedResult = pressedMap.get(MouseEvent.BUTTON1);
	if (pressedResult != null && pressedResult) {
	    data.setModifiers(lastMouseEvent.getModifiers());
	    data.setId(MouseEvent.MOUSE_DRAGGED);
	} else {
	    data.setId(MouseEvent.MOUSE_MOVED);
	}

	screen.addEvent(data);
    }

    public void trackMouseEvent(boolean down, int buttonMask, int x, int y) {
	EventData data = new EventData();
	data.setSource(screen);
	data.setX(x);
	data.setY(y);
	data.setId(down ? MouseEvent.MOUSE_PRESSED : MouseEvent.MOUSE_RELEASED);
	data.setButton(jsButtonMaskToJava(buttonMask));
	data.setModifiers(jsButtonMaskToJavaMask(buttonMask));
	data.setTime(System.currentTimeMillis());

//	if (lastClickedEvent != null && (System.currentTimeMillis() - lastClickedEvent.getTime()) <= 200) {
//	    data.setClickCount(2);
//	} else {
//	    data.setClickCount(1);
//	}

	screen.addEvent(data);

	switch (data.getId()) {
	case MouseEvent.MOUSE_PRESSED:
	    pressedMap.put(data.getButton(), Boolean.TRUE);
	    break;

	case MouseEvent.MOUSE_RELEASED:
	    pressedMap.put(data.getButton(), Boolean.FALSE);
	    generateClickedEvent(data);
	    break;
	}

	this.lastMouseEvent = data;
    }

    protected int jsButtonMaskToJava(int jsButtonMask) {
	switch (jsButtonMask) {
	case 0:
	    return MouseEvent.BUTTON1;
	case 1:
	    return MouseEvent.BUTTON2;
	case 2:
	case 3:
	    return MouseEvent.BUTTON3;
	}

	throw new RuntimeException("Should not reach");
    }

    protected int jsButtonMaskToJavaMask(int jsButtonMask) {
	switch (jsButtonMask) {
	case 0:
	    return MouseEvent.BUTTON1_MASK;
	case 1:
	    return MouseEvent.BUTTON2_MASK;
	case 2:
	case 3:
	    return MouseEvent.BUTTON3_MASK;
	}

	throw new RuntimeException("Should not reach");
    }

    protected void generateClickedEvent(EventData origData) {
	EventData synthData = new EventData();
	synthData.setX(origData.getX());
	synthData.setY(origData.getY());
	synthData.setId(MouseEvent.MOUSE_CLICKED);
	synthData.setModifiers(MouseEvent.BUTTON1);
	synthData.setButton(MouseEvent.BUTTON1);
	synthData.setSource(origData.getSource());
	synthData.setTime(System.currentTimeMillis());
	synthData.setClickCount(1);

	if (lastClickedEvent != null && (System.currentTimeMillis() - lastClickedEvent.getTime()) <= 200) {
	    synthData.setClickCount(2);
	}
	screen.addEvent(synthData);

	lastClickedEvent = synthData;
    }
}
