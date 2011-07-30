package net.java.openjdk.awt.peer.web;

import java.awt.*;
import java.awt.peer.*;

public class WebSystemTrayPeer implements SystemTrayPeer {

    @Override
    public Dimension getTrayIconSize() {
	return new Dimension(0, 0);
    }

}
