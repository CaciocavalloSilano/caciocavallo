package net.java.openjdk.cacio.servlet;

import java.util.*;
import net.java.openjdk.awt.peer.web.*;

public class CopyAreaScreenUpdate extends ScreenUpdate {

    int dx, dy;
    
    public CopyAreaScreenUpdate(int dstX, int dstY, int srcX, int srcY, int w, int h) {
	super(new DamageRect(dstX, dstY, w, h));
    }

    @Override
    public void writeCmdStream(List<Integer> cmdList) {
    }


}
