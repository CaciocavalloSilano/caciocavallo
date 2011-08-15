package net.java.openjdk.awt.peer.web;

import java.util.*;
import sun.java2d.pipe.*;

public class CopyAreaScreenUpdate extends ScreenUpdate {

    int dx, dy;
    Region clip;
    
    public CopyAreaScreenUpdate(int x1, int y1, int x2, int y2, int dx, int dy, Region clip) {
	super(new WebRect(x1, y1, x2, y2));
	this.dx = dx;
	this.dy = dy;
	this.clip = clip;
    }

    @Override
    public void writeCmdStream(List<Integer> cmdList) {
	cmdList.add(1);
	cmdList.add(updateArea.getX1());
	cmdList.add(updateArea.getY1());
	cmdList.add(updateArea.getX2());
	cmdList.add(updateArea.getY2());
	cmdList.add(dx);
	cmdList.add(dy);
	cmdList.add(clip.getLoX());
	cmdList.add(clip.getLoY());
	cmdList.add(clip.getWidth());
	cmdList.add(clip.getHeight());
    }

    @Override
    public String toString() {
	return "CopyAreaScreenUpdate [dx=" + dx + ", dy=" + dy + ", updateArea=" + updateArea + "]";
    }
    
    
}
