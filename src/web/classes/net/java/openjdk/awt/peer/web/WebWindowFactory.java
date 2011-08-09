package net.java.openjdk.awt.peer.web;

import java.awt.*;
import sun.awt.peer.cacio.*;
import sun.awt.peer.cacio.managed.*;

public class WebWindowFactory extends FullScreenWindowFactory {

    public WebWindowFactory() {
	super(new SessionScreenSelector(), null);
    }

    @Override
    public CacioEventPump<?> createEventPump() {
	return new WebDummyEventPump();
    }

    public void repaintScreen(PlatformScreen screen) {
	ScreenManagedWindowContainer smwc = getScreenManagedWindowContainer(screen);
	smwc.repaint(0, 0, screen.getBounds().width, screen.getBounds().height);
    }

    private static final class SessionScreenSelector implements PlatformScreenSelector {
	@Override
	public PlatformScreen getPlatformScreen(GraphicsConfiguration config) {
	    return ((WebGraphicsConfiguration) config).getScreen();
	}
    }
}

class WebDummyEventPump extends CacioEventPump<EventData> {
    // Do not start an event-pump thread at all
    protected void start() {
    }

    @Override
    protected EventData fetchNativeEvent() {
	return null;
    }

    @Override
    protected void dispatchNativeEvent(EventData nativeEvent) {
    }
}