package net.java.openjdk.cacio.server;

import javax.servlet.http.*;

import net.java.openjdk.cacio.servlet.*;

public class CacioSessionListener implements HttpSessionListener {
    @Override
    public void sessionDestroyed(HttpSessionEvent ev) {
	WebSessionManager.getInstance().disposeSession(ev.getSession());
    }
    
    @Override
    public void sessionCreated(HttpSessionEvent ev) {
    }
}
