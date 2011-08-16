package net.java.openjdk.awt.peer.web;

import net.java.openjdk.cacio.servlet.*;
import sun.awt.*;
import sun.awt.peer.cacio.managed.*;

public class WebFocusManager extends FocusManager {

    private final static String APPCTX_KEY = "WebFocusManager";
    
    @Override
    protected FocusManager getContextInstance() {
	AppContext context = WebSessionManager.getInstance().getCurrentState().getAppContext();
	
	WebFocusManager mgr;
	if((mgr =(WebFocusManager) context.get(APPCTX_KEY))  == null) {
	    mgr = new WebFocusManager();
	    context.put(APPCTX_KEY, mgr);
	}
	
	return mgr;
    }

}
