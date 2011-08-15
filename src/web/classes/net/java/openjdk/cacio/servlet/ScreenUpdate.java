package net.java.openjdk.cacio.servlet;

import java.util.*;

import net.java.openjdk.awt.peer.web.*;

public abstract class ScreenUpdate {
    WebRect updateArea;

    public ScreenUpdate(WebRect updateArea) {
	this.updateArea = updateArea;
    }

    public WebRect getUpdateArea() {
        return updateArea;
    }

    public void setUpdateArea(WebRect updateArea) {
        this.updateArea = updateArea;
    }
    
    public abstract void writeCmdStream(List<Integer> cmdList);
}
