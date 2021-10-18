package de.ptb.epics.eve.data.scandescription.updater.patches.patch8o0t9o0;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.ptb.epics.eve.data.ComparisonTypes;
import de.ptb.epics.eve.data.DataTypes;
import de.ptb.epics.eve.data.scandescription.Limit;

/**
 * @author Marcus Michalsky
 * @since 1.36
 */
public class PauseEventTest {
	
	@Test
	public void testEqualsReflexive() {
		Limit limit = new Limit(DataTypes.INT, ComparisonTypes.EQ);
		PauseEvent pauseEvent = new PauseEvent("pauseId", limit, EventAction.OFF);
		assertEquals(pauseEvent, pauseEvent);
	}
	
	@Test
	public void testEqualsSymmetric() {
		Limit limit = new Limit(DataTypes.INT, ComparisonTypes.EQ);
		PauseEvent pauseEventA = new PauseEvent("pauseId", limit, EventAction.OFF);
		PauseEvent pauseEventB = new PauseEvent("pauseId", limit, EventAction.OFF);
		assertTrue("symmetry: A equals B", pauseEventA.equals(pauseEventB));
		assertTrue("symmetry: B equals A", pauseEventB.equals(pauseEventA));
	}
	
	@Test
	public void testEqualsTransitive() {
		Limit limit = new Limit(DataTypes.INT, ComparisonTypes.EQ);
		PauseEvent pauseEventA = new PauseEvent("pauseId", limit, EventAction.OFF);
		PauseEvent pauseEventB = new PauseEvent("pauseId", limit, EventAction.OFF);
		PauseEvent pauseEventC = new PauseEvent("pauseId", limit, EventAction.OFF);
		assertTrue("transitive: A equals B", pauseEventA.equals(pauseEventB));
		assertTrue("transitive: B equals C", pauseEventB.equals(pauseEventC));
		assertTrue("transitive: A equals C", pauseEventA.equals(pauseEventC));
	}
	
	@Test
	public void testEqualsConsistent() {
		Limit limit = new Limit(DataTypes.INT, ComparisonTypes.EQ);
		PauseEvent pauseEventA = new PauseEvent("pauseId", limit, EventAction.OFF);
		PauseEvent pauseEventB = new PauseEvent("pauseId", limit, EventAction.OFF);
		assertEquals(pauseEventA.equals(pauseEventB), pauseEventB.equals(pauseEventA));
	}
	
	@Test
	public void testEqualsNull() {
		Limit limit = new Limit(DataTypes.INT, ComparisonTypes.EQ);
		PauseEvent pauseEvent = new PauseEvent("pauseId", limit, EventAction.OFF);
		assertFalse(pauseEvent.equals(null));
	}
	
	@Test
	public void testEqualsDifferentId() {
		Limit limit = new Limit(DataTypes.INT, ComparisonTypes.EQ);
		PauseEvent pauseEventA = new PauseEvent("pauseId", limit, EventAction.OFF);
		PauseEvent pauseEventB = new PauseEvent("pauseId2", limit, EventAction.OFF);
		assertFalse(pauseEventA.equals(pauseEventB));
	}
	
	@Test
	public void testEqualsDifferentLimit() {
		Limit limitA = new Limit(DataTypes.INT, ComparisonTypes.EQ);
		Limit limitB = new Limit(DataTypes.DOUBLE, ComparisonTypes.EQ);
		PauseEvent pauseEventA = new PauseEvent("pauseId", limitA, EventAction.OFF);
		PauseEvent pauseEventB = new PauseEvent("pauseId", limitB, EventAction.OFF);
		assertFalse(pauseEventA.equals(pauseEventB));
	}
	
	@Test
	public void testEqualsDifferentAction() {
		Limit limit = new Limit(DataTypes.INT, ComparisonTypes.EQ);
		PauseEvent pauseEventA = new PauseEvent("pauseId", limit, EventAction.OFF);
		PauseEvent pauseEventB = new PauseEvent("pauseId", limit, EventAction.ON);
		assertFalse(pauseEventA.equals(pauseEventB));
	}
	
	@Test
	public void testEquals() {
		Limit limit = new Limit(DataTypes.INT, ComparisonTypes.EQ);
		PauseEvent pauseEventA = new PauseEvent("pauseId", limit, EventAction.OFF);
		PauseEvent pauseEventB = new PauseEvent("pauseId", limit, EventAction.OFF);
		assertTrue(pauseEventA.equals(pauseEventB));
	}
	
	@Test
	public void testHashCodeConsistent() {
		Limit limit = new Limit(DataTypes.INT, ComparisonTypes.EQ);
		PauseEvent pauseEvent = new PauseEvent("pauseId", limit, EventAction.OFF);
		assertEquals(pauseEvent.hashCode(), pauseEvent.hashCode());
	}
	
	@Test
	public void testHashCodeEquality() {
		Limit limit = new Limit(DataTypes.INT, ComparisonTypes.EQ);
		PauseEvent pauseEventA = new PauseEvent("pauseId", limit, EventAction.OFF);
		PauseEvent pauseEventB = new PauseEvent("pauseId", limit, EventAction.OFF);
		assertEquals(pauseEventA, pauseEventB);
		assertEquals(pauseEventA.hashCode(), pauseEventB.hashCode());
	}
}
