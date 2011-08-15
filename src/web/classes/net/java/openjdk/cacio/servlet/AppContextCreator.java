package net.java.openjdk.cacio.servlet;

import java.lang.reflect.*;
import java.util.*;

import net.java.openjdk.awt.peer.web.*;
import sun.awt.*;

public class AppContextCreator {

    
    protected void startAppInNewAppContext(final WebSessionState sessionState) {
	
	ThreadGroup appGroup = new ThreadGroup("AppThreadGroup "+String.valueOf(new Random().nextLong()));
	Thread t = new Thread(appGroup, "AppInitThread") {
	    public void run() {
		AppContext appContext = SunToolkit.createNewAppContext();
		
		try {
		    sessionState.lockSession();
		    WebSessionManager.getInstance().registerAppContext(appContext, sessionState);
		    
		    ClassLoader loader = getClass().getClassLoader();
		    Class<?> cls = loader.loadClass(sessionState.getMainClsName());
		    Method mainMethod = cls.getMethod("main", String[].class);
		    mainMethod.setAccessible(true);
		    mainMethod.invoke(cls, (Object) sessionState.getCmdLineParams());
		} catch (Exception ex) {
		    ex.printStackTrace();
		} finally {
		    sessionState.unlockSession();
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
    }
}
