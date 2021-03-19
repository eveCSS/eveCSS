package de.ptb.epics.eve.data.scandescription.updater.patches.patch8o0t9o0;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import de.ptb.epics.eve.data.ComparisonTypes;
import de.ptb.epics.eve.data.DataTypes;
import de.ptb.epics.eve.data.scandescription.Limit;

/**
 * @author Marcus Michalsky
 * @since 1.36
 */
public class Patch8o0T9o0HelperTest {
	private Patch8o0T9o0Helper helper;
	
	@Test
	public void testFindHysteresis() {
		Limit limitA = new Limit(DataTypes.INT, ComparisonTypes.LT);
		limitA.setValue("3");
		PauseEvent eventA = new PauseEvent("deviceA", limitA, EventAction.OFF);
		PauseEvent eventB = new PauseEvent("deviceB",
				new Limit(DataTypes.INT, ComparisonTypes.EQ), EventAction.ONOFF);
		Limit limitC = new Limit(DataTypes.INT, ComparisonTypes.GT);
		limitC.setValue("5");
		PauseEvent eventC = new PauseEvent("deviceA", limitC, EventAction.ON);
		List<PauseEvent> pauseEvents = new ArrayList<>();
		pauseEvents.add(eventA);
		pauseEvents.add(eventB);
		pauseEvents.add(eventC);
		Set<PauseEvent> usedSet = new HashSet<>();
		
		List<PseudoPauseCondition> pauseConditions = helper.findHysteresis(
				pauseEvents, usedSet);
		
		assertTrue("eventA should be in usedSet", usedSet.contains(eventA));
		assertFalse("eventB should not be in usedSet", usedSet.contains(eventB));
		assertTrue("eventC should be in usedSet", usedSet.contains(eventC));
		// one combined pause event should have be created
		assertTrue(pauseConditions.size() == 1);
		PseudoPauseCondition pauseCondition = pauseConditions.get(0);
		assertEquals("id", eventA.getId(), pauseCondition.getDeviceId());
		assertEquals("type", eventA.getLimit().getType(), 
				pauseCondition.getType());
		assertEquals("operator should be that of eventC", 
				eventC.getLimit().getComparison(), pauseCondition.getOperator());
		assertEquals("pause limit should be value of eventC", 
				eventC.getLimit().getValue(), pauseCondition.getPauseLimit());
		assertEquals("continue limit should be value of eventA", 
				eventA.getLimit().getValue(), pauseCondition.getContinueLimit());
	}
	
	@Test
	public void testIsOperatorCompatibleHysteresis() {
		PauseEvent eventA = new PauseEvent("deviceA", 
			new Limit(DataTypes.INT, ComparisonTypes.EQ), EventAction.ONOFF);
		PauseEvent eventB = new PauseEvent("deviceA",
			new Limit(DataTypes.INT, ComparisonTypes.EQ), EventAction.ONOFF);
		assertFalse("eq/eq should be false", 
				helper.isOperatorCompatibleHysteresis(eventA, eventB));
		eventA = new PauseEvent("deviceA", 
			new Limit(DataTypes.INT, ComparisonTypes.EQ), EventAction.ONOFF);
		eventB = new PauseEvent("deviceA",
			new Limit(DataTypes.INT, ComparisonTypes.NE), EventAction.ONOFF);
		assertFalse("eq/ne should be false", 
				helper.isOperatorCompatibleHysteresis(eventA, eventB));
		eventA = new PauseEvent("deviceA", 
			new Limit(DataTypes.INT, ComparisonTypes.EQ), EventAction.ONOFF);
		eventB = new PauseEvent("deviceA",
			new Limit(DataTypes.INT, ComparisonTypes.LT), EventAction.ONOFF);
		assertFalse("eq/lt should be false", 
				helper.isOperatorCompatibleHysteresis(eventA, eventB));
		eventA = new PauseEvent("deviceA", 
			new Limit(DataTypes.INT, ComparisonTypes.EQ), EventAction.ONOFF);
		eventB = new PauseEvent("deviceA",
			new Limit(DataTypes.INT, ComparisonTypes.GT), EventAction.ONOFF);
		assertFalse("eq/gt should be false", 
				helper.isOperatorCompatibleHysteresis(eventA, eventB));
		
		eventA = new PauseEvent("deviceA", 
			new Limit(DataTypes.INT, ComparisonTypes.NE), EventAction.ONOFF);
		eventB = new PauseEvent("deviceA",
			new Limit(DataTypes.INT, ComparisonTypes.EQ), EventAction.ONOFF);
		assertFalse("ne/eq should be false", 
				helper.isOperatorCompatibleHysteresis(eventA, eventB));
		eventA = new PauseEvent("deviceA", 
			new Limit(DataTypes.INT, ComparisonTypes.NE), EventAction.ONOFF);
		eventB = new PauseEvent("deviceA",
			new Limit(DataTypes.INT, ComparisonTypes.NE), EventAction.ONOFF);
		assertFalse("ne/ne should be false", 
				helper.isOperatorCompatibleHysteresis(eventA, eventB));
		eventA = new PauseEvent("deviceA", 
			new Limit(DataTypes.INT, ComparisonTypes.NE), EventAction.ONOFF);
		eventB = new PauseEvent("deviceA",
			new Limit(DataTypes.INT, ComparisonTypes.LT), EventAction.ONOFF);
		assertFalse("ne/lt should be false", 
				helper.isOperatorCompatibleHysteresis(eventA, eventB));
		eventA = new PauseEvent("deviceA", 
			new Limit(DataTypes.INT, ComparisonTypes.NE), EventAction.ONOFF);
		eventB = new PauseEvent("deviceA",
			new Limit(DataTypes.INT, ComparisonTypes.GT), EventAction.ONOFF);
		assertFalse("ne/gt should be false", 
				helper.isOperatorCompatibleHysteresis(eventA, eventB));
		
		eventA = new PauseEvent("deviceA", 
			new Limit(DataTypes.INT, ComparisonTypes.LT), EventAction.ONOFF);
		eventB = new PauseEvent("deviceA",
			new Limit(DataTypes.INT, ComparisonTypes.EQ), EventAction.ONOFF);
		assertFalse("lt/eq should be false", 
				helper.isOperatorCompatibleHysteresis(eventA, eventB));
		eventA = new PauseEvent("deviceA", 
				new Limit(DataTypes.INT, ComparisonTypes.LT), EventAction.ONOFF);
		eventB = new PauseEvent("deviceA",
			new Limit(DataTypes.INT, ComparisonTypes.NE), EventAction.ONOFF);
		assertFalse("lt/ne should be false", 
				helper.isOperatorCompatibleHysteresis(eventA, eventB));
		eventA = new PauseEvent("deviceA", 
			new Limit(DataTypes.INT, ComparisonTypes.LT), EventAction.ONOFF);
		eventB = new PauseEvent("deviceA",
			new Limit(DataTypes.INT, ComparisonTypes.LT), EventAction.ONOFF);
		assertFalse("lt/lt should be false", 
				helper.isOperatorCompatibleHysteresis(eventA, eventB));
		eventA = new PauseEvent("deviceA", 
				new Limit(DataTypes.INT, ComparisonTypes.LT), EventAction.ONOFF);
		eventB = new PauseEvent("deviceA",
			new Limit(DataTypes.INT, ComparisonTypes.GT), EventAction.ONOFF);
		assertTrue("lt/gt should be true", 
				helper.isOperatorCompatibleHysteresis(eventA, eventB));
		
		eventA = new PauseEvent("deviceA", 
			new Limit(DataTypes.INT, ComparisonTypes.GT), EventAction.ONOFF);
		eventB = new PauseEvent("deviceA",
			new Limit(DataTypes.INT, ComparisonTypes.EQ), EventAction.ONOFF);
		assertFalse("gt/eq should be false", 
				helper.isOperatorCompatibleHysteresis(eventA, eventB));
		eventA = new PauseEvent("deviceA", 
			new Limit(DataTypes.INT, ComparisonTypes.GT), EventAction.ONOFF);
		eventB = new PauseEvent("deviceA",
			new Limit(DataTypes.INT, ComparisonTypes.NE), EventAction.ONOFF);
		assertFalse("gt/ne should be false", 
				helper.isOperatorCompatibleHysteresis(eventA, eventB));
		eventA = new PauseEvent("deviceA", 
			new Limit(DataTypes.INT, ComparisonTypes.GT), EventAction.ONOFF);
		eventB = new PauseEvent("deviceA",
			new Limit(DataTypes.INT, ComparisonTypes.LT), EventAction.ONOFF);
		assertTrue("gt/lt should be true", 
				helper.isOperatorCompatibleHysteresis(eventA, eventB));
		eventA = new PauseEvent("deviceA", 
			new Limit(DataTypes.INT, ComparisonTypes.GT), EventAction.ONOFF);
		eventB = new PauseEvent("deviceA",
			new Limit(DataTypes.INT, ComparisonTypes.GT), EventAction.ONOFF);
		assertFalse("gt/gt should be false", 
				helper.isOperatorCompatibleHysteresis(eventA, eventB));
	}
	
	@Test
	public void testFindSubsets() {
		Limit limitA = new Limit(DataTypes.INT, ComparisonTypes.LT);
		limitA.setValue("3");
		PauseEvent eventA = new PauseEvent("deviceA", limitA, EventAction.ONOFF);
		PauseEvent eventB = new PauseEvent("deviceB",
				new Limit(DataTypes.INT, ComparisonTypes.EQ), EventAction.ONOFF);
		Limit limitC = new Limit(DataTypes.INT, ComparisonTypes.LT);
		limitC.setValue("5");
		PauseEvent eventC = new PauseEvent("deviceA", limitC, EventAction.ONOFF);
		List<PauseEvent> pauseEvents = new ArrayList<>();
		pauseEvents.add(eventA);
		pauseEvents.add(eventB);
		pauseEvents.add(eventC);
		Set<PauseEvent> usedSet = new HashSet<>();
		
		List<PseudoPauseCondition> pauseConditions = helper.findSubsets(
				pauseEvents, usedSet);
		
		assertTrue("eventA should be in usedSet", usedSet.contains(eventA));
		assertFalse("eventB should not be in usedSet", usedSet.contains(eventB));
		assertTrue("eventC should be in usedSet", usedSet.contains(eventC));
		// one combined pause event should have be created
		assertTrue(pauseConditions.size() == 1);
		PseudoPauseCondition pauseCondition = pauseConditions.get(0);
		assertEquals("id", eventA.getId(), pauseCondition.getDeviceId());
		assertEquals("type", eventA.getLimit().getType(), 
				pauseCondition.getType());
		assertEquals("operator", eventA.getLimit().getComparison(), 
				pauseCondition.getOperator());
		assertEquals("pause limit should be value of eventA", 
				eventA.getLimit().getValue(), pauseCondition.getPauseLimit());
		assertNull("continue limit should not be set", 
				pauseCondition.getContinueLimit());
	}
	
	@Test
	public void testIsOperatorCompatibleSubset() {
		PauseEvent eventA = new PauseEvent("deviceA", 
			new Limit(DataTypes.INT, ComparisonTypes.EQ), EventAction.ONOFF);
		PauseEvent eventB = new PauseEvent("deviceA",
			new Limit(DataTypes.INT, ComparisonTypes.EQ), EventAction.ONOFF);
		assertFalse("eq/eq should be false", 
				helper.isOperatorCompatibleSubset(eventA, eventB));
		eventA = new PauseEvent("deviceA", 
			new Limit(DataTypes.INT, ComparisonTypes.EQ), EventAction.ONOFF);
		eventB = new PauseEvent("deviceA",
			new Limit(DataTypes.INT, ComparisonTypes.NE), EventAction.ONOFF);
		assertFalse("eq/ne should be false", 
				helper.isOperatorCompatibleSubset(eventA, eventB));
		eventA = new PauseEvent("deviceA", 
			new Limit(DataTypes.INT, ComparisonTypes.EQ), EventAction.ONOFF);
		eventB = new PauseEvent("deviceA",
			new Limit(DataTypes.INT, ComparisonTypes.LT), EventAction.ONOFF);
		assertFalse("eq/lt should be false", 
				helper.isOperatorCompatibleSubset(eventA, eventB));
		eventA = new PauseEvent("deviceA", 
			new Limit(DataTypes.INT, ComparisonTypes.EQ), EventAction.ONOFF);
		eventB = new PauseEvent("deviceA",
			new Limit(DataTypes.INT, ComparisonTypes.GT), EventAction.ONOFF);
		assertFalse("eq/gt should be false", 
				helper.isOperatorCompatibleSubset(eventA, eventB));
		
		eventA = new PauseEvent("deviceA", 
			new Limit(DataTypes.INT, ComparisonTypes.NE), EventAction.ONOFF);
		eventB = new PauseEvent("deviceA",
			new Limit(DataTypes.INT, ComparisonTypes.EQ), EventAction.ONOFF);
		assertFalse("ne/eq should be false", 
				helper.isOperatorCompatibleSubset(eventA, eventB));
		eventA = new PauseEvent("deviceA", 
			new Limit(DataTypes.INT, ComparisonTypes.NE), EventAction.ONOFF);
		eventB = new PauseEvent("deviceA",
			new Limit(DataTypes.INT, ComparisonTypes.NE), EventAction.ONOFF);
		assertFalse("ne/ne should be false", 
				helper.isOperatorCompatibleSubset(eventA, eventB));
		eventA = new PauseEvent("deviceA", 
			new Limit(DataTypes.INT, ComparisonTypes.NE), EventAction.ONOFF);
		eventB = new PauseEvent("deviceA",
			new Limit(DataTypes.INT, ComparisonTypes.LT), EventAction.ONOFF);
		assertFalse("ne/lt should be false", 
				helper.isOperatorCompatibleSubset(eventA, eventB));
		eventA = new PauseEvent("deviceA", 
			new Limit(DataTypes.INT, ComparisonTypes.NE), EventAction.ONOFF);
		eventB = new PauseEvent("deviceA",
			new Limit(DataTypes.INT, ComparisonTypes.GT), EventAction.ONOFF);
		assertFalse("ne/gt should be false", 
				helper.isOperatorCompatibleSubset(eventA, eventB));
		
		eventA = new PauseEvent("deviceA", 
			new Limit(DataTypes.INT, ComparisonTypes.LT), EventAction.ONOFF);
		eventB = new PauseEvent("deviceA",
			new Limit(DataTypes.INT, ComparisonTypes.EQ), EventAction.ONOFF);
		assertFalse("lt/eq should be false", 
				helper.isOperatorCompatibleSubset(eventA, eventB));
		eventA = new PauseEvent("deviceA", 
				new Limit(DataTypes.INT, ComparisonTypes.LT), EventAction.ONOFF);
		eventB = new PauseEvent("deviceA",
			new Limit(DataTypes.INT, ComparisonTypes.NE), EventAction.ONOFF);
		assertFalse("lt/ne should be false", 
				helper.isOperatorCompatibleSubset(eventA, eventB));
		eventA = new PauseEvent("deviceA", 
			new Limit(DataTypes.INT, ComparisonTypes.LT), EventAction.ONOFF);
		eventB = new PauseEvent("deviceA",
			new Limit(DataTypes.INT, ComparisonTypes.LT), EventAction.ONOFF);
		assertTrue("lt/lt should be true", 
				helper.isOperatorCompatibleSubset(eventA, eventB));
		eventA = new PauseEvent("deviceA", 
				new Limit(DataTypes.INT, ComparisonTypes.LT), EventAction.ONOFF);
		eventB = new PauseEvent("deviceA",
			new Limit(DataTypes.INT, ComparisonTypes.GT), EventAction.ONOFF);
		assertFalse("lt/gt should be false", 
				helper.isOperatorCompatibleSubset(eventA, eventB));
		
		eventA = new PauseEvent("deviceA", 
			new Limit(DataTypes.INT, ComparisonTypes.GT), EventAction.ONOFF);
		eventB = new PauseEvent("deviceA",
			new Limit(DataTypes.INT, ComparisonTypes.EQ), EventAction.ONOFF);
		assertFalse("gt/eq should be false", 
				helper.isOperatorCompatibleSubset(eventA, eventB));
		eventA = new PauseEvent("deviceA", 
			new Limit(DataTypes.INT, ComparisonTypes.GT), EventAction.ONOFF);
		eventB = new PauseEvent("deviceA",
			new Limit(DataTypes.INT, ComparisonTypes.NE), EventAction.ONOFF);
		assertFalse("gt/ne should be false", 
				helper.isOperatorCompatibleSubset(eventA, eventB));
		eventA = new PauseEvent("deviceA", 
			new Limit(DataTypes.INT, ComparisonTypes.GT), EventAction.ONOFF);
		eventB = new PauseEvent("deviceA",
			new Limit(DataTypes.INT, ComparisonTypes.LT), EventAction.ONOFF);
		assertFalse("gt/lt should be false", 
				helper.isOperatorCompatibleSubset(eventA, eventB));
		eventA = new PauseEvent("deviceA", 
			new Limit(DataTypes.INT, ComparisonTypes.GT), EventAction.ONOFF);
		eventB = new PauseEvent("deviceA",
			new Limit(DataTypes.INT, ComparisonTypes.GT), EventAction.ONOFF);
		assertTrue("gt/gt should be true", 
				helper.isOperatorCompatibleSubset(eventA, eventB));
	}
	
	@Test
	public void testIsActionCompatibleSubset() {
		PauseEvent eventA = new PauseEvent("deviceA", 
			new Limit(DataTypes.INT, ComparisonTypes.EQ), EventAction.ON);
		PauseEvent eventB = new PauseEvent("deviceA",
			new Limit(DataTypes.INT, ComparisonTypes.EQ), EventAction.ON);
		assertTrue("on/on should be true",
				helper.isActionCompatibleSubset(eventA, eventB));
		eventA = new PauseEvent("deviceA", 
			new Limit(DataTypes.INT, ComparisonTypes.EQ), EventAction.ON);
		eventB = new PauseEvent("deviceA",
			new Limit(DataTypes.INT, ComparisonTypes.EQ), EventAction.OFF);
		assertFalse("on/off should be false",
				helper.isActionCompatibleSubset(eventA, eventB));
		eventA = new PauseEvent("deviceA", 
			new Limit(DataTypes.INT, ComparisonTypes.EQ), EventAction.ON);
		eventB = new PauseEvent("deviceA",
			new Limit(DataTypes.INT, ComparisonTypes.EQ), EventAction.ONOFF);
		assertFalse("on/onoff should be false",
				helper.isActionCompatibleSubset(eventA, eventB));
		
		eventA = new PauseEvent("deviceA", 
			new Limit(DataTypes.INT, ComparisonTypes.EQ), EventAction.OFF);
		eventB = new PauseEvent("deviceA",
			new Limit(DataTypes.INT, ComparisonTypes.EQ), EventAction.ON);
		assertFalse("off/on should be false",
				helper.isActionCompatibleSubset(eventA, eventB));
		eventA = new PauseEvent("deviceA", 
			new Limit(DataTypes.INT, ComparisonTypes.EQ), EventAction.OFF);
		eventB = new PauseEvent("deviceA",
			new Limit(DataTypes.INT, ComparisonTypes.EQ), EventAction.OFF);
		assertFalse("off/off should be false",
				helper.isActionCompatibleSubset(eventA, eventB));
		eventA = new PauseEvent("deviceA", 
			new Limit(DataTypes.INT, ComparisonTypes.EQ), EventAction.OFF);
		eventB = new PauseEvent("deviceA",
			new Limit(DataTypes.INT, ComparisonTypes.EQ), EventAction.ONOFF);
		assertFalse("off/onoff should be false",
				helper.isActionCompatibleSubset(eventA, eventB));
		
		eventA = new PauseEvent("deviceA", 
			new Limit(DataTypes.INT, ComparisonTypes.EQ), EventAction.ONOFF);
		eventB = new PauseEvent("deviceA",
			new Limit(DataTypes.INT, ComparisonTypes.EQ), EventAction.ON);
		assertFalse("onoff/on should be false",
				helper.isActionCompatibleSubset(eventA, eventB));
		eventA = new PauseEvent("deviceA", 
			new Limit(DataTypes.INT, ComparisonTypes.EQ), EventAction.ONOFF);
		eventB = new PauseEvent("deviceA",
			new Limit(DataTypes.INT, ComparisonTypes.EQ), EventAction.OFF);
		assertFalse("onoff/off should be false",
				helper.isActionCompatibleSubset(eventA, eventB));
		eventA = new PauseEvent("deviceA", 
			new Limit(DataTypes.INT, ComparisonTypes.EQ), EventAction.ONOFF);
		eventB = new PauseEvent("deviceA",
			new Limit(DataTypes.INT, ComparisonTypes.EQ), EventAction.ONOFF);
		assertTrue("onoff/onoff should be true",
				helper.isActionCompatibleSubset(eventA, eventB));
	}
	
	@Test
	public void testCombineEventsCheckPrecondition() {
		PauseEvent eventA = new PauseEvent("deviceA", 
			new Limit(DataTypes.INT, ComparisonTypes.EQ), EventAction.ON);
		PauseEvent eventB = new PauseEvent("deviceA",
			new Limit(DataTypes.INT, ComparisonTypes.EQ), EventAction.ON);
		assertNull(helper.combineEvents(eventA, eventB));
		eventA = new PauseEvent("deviceA", 
			new Limit(DataTypes.INT, ComparisonTypes.EQ), EventAction.ON);
		eventB = new PauseEvent("deviceA",
			new Limit(DataTypes.INT, ComparisonTypes.EQ), EventAction.ON);
		assertNull(helper.combineEvents(eventA, eventB));
		
		// TODO check 3x3x4x4 = 144 states ?! 
		// ActionA (3 states) x ActionB (3 states) x 
		// operatorA (4 states) x operatorB (4 states)
	}
	
	@Test
	public void testCombineEventsSubsetDatetime() {
		Limit limit = new Limit(DataTypes.DATETIME, ComparisonTypes.LT);
		limit.setValue("1970-00-00");
		PauseEvent eventA = new PauseEvent("deviceA", limit, EventAction.ONOFF);
		PauseEvent eventB = new PauseEvent("deviceA", limit, EventAction.ONOFF);
		PseudoPauseCondition pauseCondition = helper.combineEvents(eventA, eventB);
		assertEquals("id should be that of eventA", 
				eventA.getId(), pauseCondition.getDeviceId());
		assertEquals("operator should be that of eventA",
				eventA.getLimit().getComparison(), pauseCondition.getOperator());
		assertEquals("type should be that of eventA",
				eventA.getLimit().getType(), pauseCondition.getType());
		assertEquals("pause limit should be limit value of eventA",
				eventA.getLimit().getValue(), pauseCondition.getPauseLimit());
	}
	
	@Test
	public void testCombineEventsSubsetDoubleLT() {
		Limit limitA = new Limit(DataTypes.DOUBLE, ComparisonTypes.LT);
		limitA.setValue("2.2");
		Limit limitB = new Limit(DataTypes.DOUBLE, ComparisonTypes.LT);
		limitB.setValue("5.5");
		PauseEvent eventA = new PauseEvent("deviceA", limitA, EventAction.ONOFF);
		PauseEvent eventB = new PauseEvent("deviceA", limitB, EventAction.ONOFF);
		PseudoPauseCondition pauseCondition = helper.combineEvents(eventA, eventB);
		assertEquals("id", eventA.getId(), pauseCondition.getDeviceId());
		assertEquals("operator", eventA.getLimit().getComparison(), 
				pauseCondition.getOperator());
		assertEquals("type should be that of eventA",
				eventA.getLimit().getType(), pauseCondition.getType());
		assertEquals("limitA value (is lower than B)", 
				eventA.getLimit().getValue(), pauseCondition.getPauseLimit());
	}
	
	@Test
	public void testCombineEventsSubsetDoubleGT() {
		Limit limitA = new Limit(DataTypes.DOUBLE, ComparisonTypes.GT);
		limitA.setValue("2.2");
		Limit limitB = new Limit(DataTypes.DOUBLE, ComparisonTypes.GT);
		limitB.setValue("5.5");
		PauseEvent eventA = new PauseEvent("deviceA", limitA, EventAction.ONOFF);
		PauseEvent eventB = new PauseEvent("deviceA", limitB, EventAction.ONOFF);
		PseudoPauseCondition pauseCondition = helper.combineEvents(eventA, eventB);
		assertEquals("id", eventA.getId(), pauseCondition.getDeviceId());
		assertEquals("operator", eventA.getLimit().getComparison(), 
				pauseCondition.getOperator());
		assertEquals("type should be that of eventA",
				eventA.getLimit().getType(), pauseCondition.getType());
		assertEquals("limitB value (is higher than A)", 
				eventB.getLimit().getValue(), pauseCondition.getPauseLimit());
	}
	
	@Test
	public void testCombineEventsSubsetIntLT() {
		Limit limitA = new Limit(DataTypes.INT, ComparisonTypes.LT);
		limitA.setValue("2");
		Limit limitB = new Limit(DataTypes.INT, ComparisonTypes.LT);
		limitB.setValue("5");
		PauseEvent eventA = new PauseEvent("deviceA", limitA, EventAction.ONOFF);
		PauseEvent eventB = new PauseEvent("deviceA", limitB, EventAction.ONOFF);
		PseudoPauseCondition pauseCondition = helper.combineEvents(eventA, eventB);
		assertEquals("id", eventA.getId(), pauseCondition.getDeviceId());
		assertEquals("operator", eventA.getLimit().getComparison(), 
				pauseCondition.getOperator());
		assertEquals("type should be that of eventA",
				eventA.getLimit().getType(), pauseCondition.getType());
		assertEquals("limitA value (is lower than B)", 
				eventA.getLimit().getValue(), pauseCondition.getPauseLimit());
	}

	@Test
	public void testCombineEventsSubsetIntGT() {
		Limit limitA = new Limit(DataTypes.INT, ComparisonTypes.GT);
		limitA.setValue("2");
		Limit limitB = new Limit(DataTypes.INT, ComparisonTypes.GT);
		limitB.setValue("5");
		PauseEvent eventA = new PauseEvent("deviceA", limitA, EventAction.ONOFF);
		PauseEvent eventB = new PauseEvent("deviceA", limitB, EventAction.ONOFF);
		PseudoPauseCondition pauseCondition = helper.combineEvents(eventA, eventB);
		assertEquals("id", eventA.getId(), pauseCondition.getDeviceId());
		assertEquals("operator", eventA.getLimit().getComparison(), 
				pauseCondition.getOperator());
		assertEquals("type should be that of eventA",
				eventA.getLimit().getType(), pauseCondition.getType());
		assertEquals("limitB value (is higher than A)", 
				eventB.getLimit().getValue(), pauseCondition.getPauseLimit());
	}
	
	@Test
	public void testCombineEventsSubsetString() {
		Limit limit = new Limit(DataTypes.STRING, ComparisonTypes.LT);
		limit.setValue("foo");
		PauseEvent eventA = new PauseEvent("deviceA", limit, EventAction.ONOFF);
		PauseEvent eventB = new PauseEvent("deviceA", limit, EventAction.ONOFF);
		PseudoPauseCondition pauseCondition = helper.combineEvents(eventA, eventB);
		assertEquals("id should be that of eventA", 
				eventA.getId(), pauseCondition.getDeviceId());
		assertEquals("operator should be that of eventA",
				eventA.getLimit().getComparison(), pauseCondition.getOperator());
		assertEquals("type should be that of eventA",
				eventA.getLimit().getType(), pauseCondition.getType());
		assertEquals("pause limit should be limit value of eventA",
				eventA.getLimit().getValue(), pauseCondition.getPauseLimit());
	}
	
	@Test
	public void testCombineEventsHysteresis() {
		Limit limitA = new Limit(DataTypes.INT, ComparisonTypes.LT);
		limitA.setValue("2");
		Limit limitB = new Limit(DataTypes.INT, ComparisonTypes.GT);
		limitB.setValue("5");
		PauseEvent eventA = new PauseEvent("deviceA", limitA, EventAction.ONOFF);
		PauseEvent eventB = new PauseEvent("deviceA", limitB, EventAction.ONOFF);
		PseudoPauseCondition pauseCondition = helper.combineEvents(eventA, eventB);
		assertEquals("id", eventA.getId(), pauseCondition.getDeviceId());
		assertEquals("operator should be that of eventA",
				eventA.getLimit().getComparison(), pauseCondition.getOperator());
		assertEquals("pause limit should be value of eventA",
				eventA.getLimit().getValue(), pauseCondition.getPauseLimit());
		assertEquals("type should be that of eventA",
				eventA.getLimit().getType(), pauseCondition.getType());
		assertEquals("continue limit should be value of eventB",
				eventB.getLimit().getValue(), pauseCondition.getContinueLimit());
	}
	
	@Test
	public void testConvert() {
		Limit limit = new Limit(DataTypes.INT, ComparisonTypes.LT);
		limit.setValue("2");
		PauseEvent event = new PauseEvent("deviceA", limit, EventAction.ONOFF);
		PseudoPauseCondition pauseCondition = helper.convert(event);
		assertEquals("id", event.getId(), pauseCondition.getDeviceId());
		assertEquals("operator",
				event.getLimit().getComparison(), pauseCondition.getOperator());
		assertEquals("type", event.getLimit().getType(), pauseCondition.getType());
		assertEquals("pause limit",
				event.getLimit().getValue(), pauseCondition.getPauseLimit());
		assertNull("continue limit", pauseCondition.getContinueLimit());
	}
	
	@Before
	public void before() {
		this.helper = new Patch8o0T9o0Helper();
	}
}
