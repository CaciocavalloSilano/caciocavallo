package net.java.openjdk.cacio.servlet;

import java.io.*;
import java.util.*;

import net.java.openjdk.awt.peer.web.*;

public abstract class ScreenUpdate {
    DamageRect updateArea;

    public ScreenUpdate(DamageRect updateArea) {
	this.updateArea = updateArea;
    }

    public DamageRect getUpdateArea() {
        return updateArea;
    }

    public void setUpdateArea(DamageRect updateArea) {
        this.updateArea = updateArea;
    }
    
    public abstract void writeCmdStream(List<Integer> cmdList);
}
