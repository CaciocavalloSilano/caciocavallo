package net.java.openjdk.awt.peer.web;

import sun.awt.peer.cacio.*;
import sun.awt.peer.cacio.managed.*;

public class WebEventSource implements CacioEventSource {

    static WebEventSource instance;
    
    @Override
    public EventData getNextEvent() {
	return null;
    }

    public synchronized static WebEventSource getInstance() {
	if(instance == null) {
	    instance = new WebEventSource();
	}
	return instance;
    }

}
