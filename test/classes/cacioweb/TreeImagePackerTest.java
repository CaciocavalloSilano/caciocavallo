package cacioweb;

import java.util.*;

import net.java.openjdk.awt.peer.web.*;

import org.junit.*;
import static org.junit.Assert.*;

public class TreeImagePackerTest {
    
    @Test
    public void testInitialBoundingBox() {
	TreeImagePacker packer = new TreeImagePacker();
	assertEquals(new WebRect(0, 0, 0, 0), packer.getBoundingBox());
    }
    
    @Test
    public void testEfficientPacking() {
	TreeImagePacker packer = new TreeImagePacker();
	
	List<ScreenUpdate> updateList = new ArrayList<ScreenUpdate>();
	BlitScreenUpdate bs1 = new BlitScreenUpdate(0, 0, 0, 0, 20, 20, null);
	BlitScreenUpdate bs2 = new BlitScreenUpdate(200, 200, 0, 0, 20, 20, null);
	
	updateList.add(bs1);
	updateList.add(bs2);
	
	packer.insertScreenUpdateList(updateList);
	
	WebRect packedBoundingBox = packer.getBoundingBox();
	assertTrue(packedBoundingBox.getWidth() <= 40);
	assertTrue(packedBoundingBox.getHeight() <= 40);
	assertTrue(bs1.getPackedX() <= 40);
	assertTrue(bs1.getPackedY() <= 40);
	assertTrue(bs2.getPackedY() <= 40);
	assertTrue(bs2.getPackedY() <= 40);
    }
}
