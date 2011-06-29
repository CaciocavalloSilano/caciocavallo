package net.java.openjdk.awt.peer.web;

import java.awt.*;
import java.util.*;

import sun.awt.peer.cacio.*;
import sun.awt.peer.cacio.managed.*;

public class WebWindowFactory extends FullScreenWindowFactory {

    public WebWindowFactory() {
	super(new SessionScreenSelector(), null);
    }

    @Override
    public CacioEventPump<?> createEventPump() {
	SDLFullScreenEventSource s = new SDLFullScreenEventSource();
        return new FullScreenEventPump(s);
    }
    
    EventData unsetEventData = new EventData();
    private class SDLFullScreenEventSource implements CacioEventSource {
        public EventData getNextEvent() {
        
            Set<PlatformScreen> screenSet = getScreenMap().keySet();
            
            for(PlatformScreen screen : screenSet) {
        	if(screen instanceof WebScreen) {
        	    WebScreen sdlScreen = (WebScreen) screen;
        	    EventData d = sdlScreen.getNextEvent();
        	    if(d != null) {
        		d.setSource(getScreenMap().get(sdlScreen));
        		return d;
        	    }
        	}
            }
           
            //Wait 10ms for *all* eventsources together
            try {
        	Thread.sleep(10);
            }catch(InterruptedException ex) {
            }
            
            return unsetEventData;
        }
    }
   

    private static final class SessionScreenSelector implements PlatformScreenSelector {
	
        SessionScreenSelector() {
        }

        @Override
        public PlatformScreen getPlatformScreen(GraphicsConfiguration config) {            
            return ((WebGraphicsConfiguration) config).getScreen();
        }
    }
}
