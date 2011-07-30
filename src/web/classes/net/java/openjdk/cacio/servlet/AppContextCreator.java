package net.java.openjdk.cacio.servlet;

import java.lang.reflect.*;
import java.util.*;

import javax.servlet.http.*;

import sun.awt.*;
import net.java.openjdk.awt.peer.web.*;

public class AppContextCreator {
    private WebSessionState sessionState;
    
    protected WebSessionState startAppInNewAppContext(final HttpSession session, final String className, final String[] parameters) {
	
	ThreadGroup appGroup = new ThreadGroup("AppThreadGroup "+String.valueOf(new Random().nextLong()));
	Thread t = new Thread(appGroup, "AppInitThread") {
	    public void run() {
		AppContext appContext = SunToolkit.createNewAppContext();
		
		WebSessionState state = WebSessionManager.getInstance().register(session);
		sessionState = state;
		try {
		    state.lockSession();
		    state.setAppContext(appContext);

		    ClassLoader loader = getClass().getClassLoader();
		    Class cls = loader.loadClass(className);
		    Method mainMethod = cls.getMethod("main", String[].class);
		    mainMethod.setAccessible(true);
		    mainMethod.invoke(cls, (Object) parameters);
		} catch (Exception ex) {
		    ex.printStackTrace();
		} finally {
		    state.unlockSession();
		}
	    }
	};
	
	try {
	    t.start();
	    
	    //Wait for initialization to finish
	    t.join();
	} catch (InterruptedException e) {
	    e.printStackTrace();
	}
	
	return sessionState;
    }
}
