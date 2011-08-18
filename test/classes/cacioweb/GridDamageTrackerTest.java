package cacioweb;

import java.util.*;


import net.java.openjdk.awt.peer.web.*;

import org.junit.*;
import static org.junit.Assert.*;

public class GridDamageTrackerTest {

    @Test
    public void testStateResetAfterGrouping() {
	GridDamageTracker tracker = new GridDamageTracker(1000, 1000);
	tracker.trackDamageRect(new WebRect(20, 20, 40, 40));

	List<ScreenUpdate> updateList = tracker.groupDamagedAreas(null, false);
	assertEquals(1, updateList.size());
	assertEquals(BlitScreenUpdate.class, updateList.get(0).getClass());

	List<ScreenUpdate> updatesAfterReset = tracker.groupDamagedAreas(null, false);
	assertEquals(null, updatesAfterReset);
    }

    /**
     * Tests cell merging when the damage rectangle has been split to fit the
     * damage grid by the tracker itself
     */
    @Test
    public void testImplicitDamageCellMerging() {
	GridDamageTracker tracker = new GridDamageTracker(1000, 1000);
	tracker.trackDamageRect(new WebRect(10, 10, 100, 20));

	List<ScreenUpdate> updateList = tracker.groupDamagedAreas(null, false);
	assertEquals(1, updateList.size());
    }

    @Test
    public void testExplicitDamageCellMergingHorizontal() {
	GridDamageTracker tracker = new GridDamageTracker(1000, 1000);
	tracker.trackDamageRect(new WebRect(10, 10, 100, 20));
	tracker.trackDamageRect(new WebRect(100, 10, 200, 20));

	List<ScreenUpdate> updateList = tracker.groupDamagedAreas(null, false);
	assertEquals(1, updateList.size());
    }

    @Test
    public void testExplicitDamageCellMergingVertical() {
	GridDamageTracker tracker = new GridDamageTracker(1000, 1000);
	tracker.trackDamageRect(new WebRect(10, 10, 100, 20));
	tracker.trackDamageRect(new WebRect(10, 20, 100, 30));

	List<ScreenUpdate> updateList = tracker.groupDamagedAreas(null, false);
	assertEquals(1, updateList.size());
    }

}
