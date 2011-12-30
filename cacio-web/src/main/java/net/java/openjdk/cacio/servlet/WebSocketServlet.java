package net.java.openjdk.cacio.servlet;

import javax.servlet.http.*;

import net.java.openjdk.awt.peer.web.*;

import org.eclipse.jetty.websocket.*;

public class WebSocketServlet extends org.eclipse.jetty.websocket.WebSocketServlet {

    @Override
    public WebSocket doWebSocketConnect(HttpServletRequest request, String protocol) {
	WebSessionState state = WebSessionManager.getInstance().getSessionState(request);
	return new WebSocketStreamThread(request.getSession(), state);
    }
}
