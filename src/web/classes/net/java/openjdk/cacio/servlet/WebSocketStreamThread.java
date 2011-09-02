package net.java.openjdk.cacio.servlet;

import java.io.*;

import javax.servlet.http.*;

import org.eclipse.jetty.websocket.*;
import net.java.openjdk.awt.peer.web.*;
import net.java.openjdk.cacio.servlet.transport.*;

public class WebSocketStreamThread extends Thread implements WebSocket.OnTextMessage {
    final WebSessionState state;
    final HttpSession session;
    final WebSessionManager sessionManager;
    
    Connection con;
    volatile boolean close = false;

    public WebSocketStreamThread(HttpSession session, WebSessionState state) {
	this.state = state;
	this.session = session;
	this.sessionManager = WebSessionManager.getInstance();
    }

    @Override
    public void onClose(int arg0, String arg1) {
	close = true;
    }

    @Override
    public void onOpen(Connection c) {
	this.con = c;
	start();
    }

    public void run() {
	try {
	   sessionManager.registerSessionAtCurrentThread(state);

	    while (!close) {
		Transport transport = state.getScreen().pollForScreenUpdates(15000);

		if (!close) {
		    con.sendMessage(transport.asString());
		}
	    }
	} catch (IOException e) {
	    e.printStackTrace();
	    close = true;
	}
	
	sessionManager.disposeSessionState(session, state);
    }

    @Override
    public void onMessage(String evDataStr) {
	WebSessionManager.getInstance().registerSessionAtCurrentThread(state);
	if(state.getEventManager() != null) {
	    state.getEventManager().parseEventData(-1, evDataStr);
	}
    }
}
