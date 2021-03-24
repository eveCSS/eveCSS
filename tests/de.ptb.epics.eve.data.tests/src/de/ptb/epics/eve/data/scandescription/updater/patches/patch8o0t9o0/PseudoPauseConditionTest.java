package de.ptb.epics.eve.data.scandescription.updater.patches.patch8o0t9o0;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.ptb.epics.eve.data.ComparisonTypes;
import de.ptb.epics.eve.data.DataTypes;

/**
 * @author Marcus Michalsky
 * @since 1.36
 */
public class PseudoPauseConditionTest {

	@Test
	public void testEqualsReflexive() {
		PseudoPauseCondition pauseCondition = new PseudoPauseCondition();
		pauseCondition.setDeviceId("deviceId");
		pauseCondition.setType(DataTypes.INT);
		pauseCondition.setOperator(ComparisonTypes.EQ);
		pauseCondition.setPauseLimit("1");
		pauseCondition.setContinueLimit(null);
		assertEquals(pauseCondition, pauseCondition);
	}
	
	@Test
	public void testEqualsSymmetric() {
		PseudoPauseCondition pauseConditionA = new PseudoPauseCondition();
		pauseConditionA.setDeviceId("deviceId");
		pauseConditionA.setType(DataTypes.INT);
		pauseConditionA.setOperator(ComparisonTypes.EQ);
		pauseConditionA.setPauseLimit("1");
		pauseConditionA.setContinueLimit(null);
		
		PseudoPauseCondition pauseConditionB = new PseudoPauseCondition();
		pauseConditionB.setDeviceId("deviceId");
		pauseConditionB.setType(DataTypes.INT);
		pauseConditionB.setOperator(ComparisonTypes.EQ);
		pauseConditionB.setPauseLimit("1");
		pauseConditionB.setContinueLimit(null);
		
		assertTrue(pauseConditionA.equals(pauseConditionB));
		assertTrue(pauseConditionB.equals(pauseConditionA));
	}
	
	@Test
	public void testEqualsTransitive() {
		PseudoPauseCondition pauseConditionA = new PseudoPauseCondition();
		pauseConditionA.setDeviceId("deviceId");
		pauseConditionA.setType(DataTypes.INT);
		pauseConditionA.setOperator(ComparisonTypes.EQ);
		pauseConditionA.setPauseLimit("1");
		pauseConditionA.setContinueLimit(null);
		
		PseudoPauseCondition pauseConditionB = new PseudoPauseCondition();
		pauseConditionB.setDeviceId("deviceId");
		pauseConditionB.setType(DataTypes.INT);
		pauseConditionB.setOperator(ComparisonTypes.EQ);
		pauseConditionB.setPauseLimit("1");
		pauseConditionB.setContinueLimit(null);
		
		PseudoPauseCondition pauseConditionC = new PseudoPauseCondition();
		pauseConditionC.setDeviceId("deviceId");
		pauseConditionC.setType(DataTypes.INT);
		pauseConditionC.setOperator(ComparisonTypes.EQ);
		pauseConditionC.setPauseLimit("1");
		pauseConditionC.setContinueLimit(null);
		
		assertTrue(pauseConditionA.equals(pauseConditionB));
		assertTrue(pauseConditionB.equals(pauseConditionC));
		assertTrue(pauseConditionA.equals(pauseConditionC));
	}
	
	@Test
	public void testEqualsConsistent() {
		PseudoPauseCondition pauseConditionA = new PseudoPauseCondition();
		pauseConditionA.setDeviceId("deviceId");
		pauseConditionA.setType(DataTypes.INT);
		pauseConditionA.setOperator(ComparisonTypes.EQ);
		pauseConditionA.setPauseLimit("1");
		pauseConditionA.setContinueLimit(null);
		
		PseudoPauseCondition pauseConditionB = new PseudoPauseCondition();
		pauseConditionB.setDeviceId("deviceId");
		pauseConditionB.setType(DataTypes.INT);
		pauseConditionB.setOperator(ComparisonTypes.EQ);
		pauseConditionB.setPauseLimit("1");
		pauseConditionB.setContinueLimit(null);
		
		assertEquals(pauseConditionA.equals(pauseConditionB), 
				pauseConditionA.equals(pauseConditionB));
	}
	
	@Test
	public void testEqualsNull() {
		PseudoPauseCondition pauseCondition = new PseudoPauseCondition();
		pauseCondition.setDeviceId("deviceId");
		pauseCondition.setType(DataTypes.INT);
		pauseCondition.setOperator(ComparisonTypes.EQ);
		pauseCondition.setPauseLimit("1");
		pauseCondition.setContinueLimit(null);
		assertFalse(pauseCondition.equals(null));
	}
	
	@Test
	public void testEqualsDifferentDevice() {
		PseudoPauseCondition pauseConditionA = new PseudoPauseCondition();
		pauseConditionA.setDeviceId("deviceId");
		pauseConditionA.setType(DataTypes.INT);
		pauseConditionA.setOperator(ComparisonTypes.EQ);
		pauseConditionA.setPauseLimit("1");
		pauseConditionA.setContinueLimit(null);
		
		PseudoPauseCondition pauseConditionB = new PseudoPauseCondition();
		pauseConditionB.setDeviceId("OtherDeviceId");
		pauseConditionB.setType(DataTypes.INT);
		pauseConditionB.setOperator(ComparisonTypes.EQ);
		pauseConditionB.setPauseLimit("1");
		pauseConditionB.setContinueLimit(null);
		
		assertFalse(pauseConditionA.equals(pauseConditionB));
	}
	
	@Test
	public void testEqualsDifferentType() {
		PseudoPauseCondition pauseConditionA = new PseudoPauseCondition();
		pauseConditionA.setDeviceId("deviceId");
		pauseConditionA.setType(DataTypes.INT);
		pauseConditionA.setOperator(ComparisonTypes.EQ);
		pauseConditionA.setPauseLimit("1");
		pauseConditionA.setContinueLimit(null);
		
		PseudoPauseCondition pauseConditionB = new PseudoPauseCondition();
		pauseConditionB.setDeviceId("deviceId");
		pauseConditionB.setType(DataTypes.DOUBLE);
		pauseConditionB.setOperator(ComparisonTypes.EQ);
		pauseConditionB.setPauseLimit("1");
		pauseConditionB.setContinueLimit(null);
		
		assertFalse(pauseConditionA.equals(pauseConditionB));
	}
	
	@Test
	public void testEqualsDifferentOperator() {
		PseudoPauseCondition pauseConditionA = new PseudoPauseCondition();
		pauseConditionA.setDeviceId("deviceId");
		pauseConditionA.setType(DataTypes.INT);
		pauseConditionA.setOperator(ComparisonTypes.EQ);
		pauseConditionA.setPauseLimit("1");
		pauseConditionA.setContinueLimit(null);
		
		PseudoPauseCondition pauseConditionB = new PseudoPauseCondition();
		pauseConditionB.setDeviceId("deviceId");
		pauseConditionB.setType(DataTypes.INT);
		pauseConditionB.setOperator(ComparisonTypes.LT);
		pauseConditionB.setPauseLimit("1");
		pauseConditionB.setContinueLimit(null);
		
		assertFalse(pauseConditionA.equals(pauseConditionB));
	}
	
	@Test
	public void testEqualsDifferentPauseLimit() {
		PseudoPauseCondition pauseConditionA = new PseudoPauseCondition();
		pauseConditionA.setDeviceId("deviceId");
		pauseConditionA.setType(DataTypes.INT);
		pauseConditionA.setOperator(ComparisonTypes.EQ);
		pauseConditionA.setPauseLimit("1");
		pauseConditionA.setContinueLimit(null);
		
		PseudoPauseCondition pauseConditionB = new PseudoPauseCondition();
		pauseConditionB.setDeviceId("deviceId");
		pauseConditionB.setType(DataTypes.INT);
		pauseConditionB.setOperator(ComparisonTypes.EQ);
		pauseConditionB.setPauseLimit("2");
		pauseConditionB.setContinueLimit(null);
		
		assertFalse(pauseConditionA.equals(pauseConditionB));
	}
	
	@Test
	public void testEqualsDifferentContinueLimit() {
		PseudoPauseCondition pauseConditionA = new PseudoPauseCondition();
		pauseConditionA.setDeviceId("deviceId");
		pauseConditionA.setType(DataTypes.INT);
		pauseConditionA.setOperator(ComparisonTypes.EQ);
		pauseConditionA.setPauseLimit("1");
		pauseConditionA.setContinueLimit(null);
		
		PseudoPauseCondition pauseConditionB = new PseudoPauseCondition();
		pauseConditionB.setDeviceId("deviceId");
		pauseConditionB.setType(DataTypes.INT);
		pauseConditionB.setOperator(ComparisonTypes.EQ);
		pauseConditionB.setPauseLimit("1");
		pauseConditionB.setContinueLimit("2");
		
		assertFalse(pauseConditionA.equals(pauseConditionB));
	}
	
	@Test
	public void testHashCodeConsistent() {
		PseudoPauseCondition pauseCondition = new PseudoPauseCondition();
		pauseCondition.setDeviceId("deviceId");
		pauseCondition.setType(DataTypes.INT);
		pauseCondition.setOperator(ComparisonTypes.EQ);
		pauseCondition.setPauseLimit("1");
		pauseCondition.setContinueLimit(null);
		
		assertEquals(pauseCondition.hashCode(), pauseCondition.hashCode());
	}
	
	@Test
	public void testHashCodeEquality() {
		PseudoPauseCondition pauseConditionA = new PseudoPauseCondition();
		pauseConditionA.setDeviceId("deviceId");
		pauseConditionA.setType(DataTypes.INT);
		pauseConditionA.setOperator(ComparisonTypes.EQ);
		pauseConditionA.setPauseLimit("1");
		pauseConditionA.setContinueLimit(null);
		
		PseudoPauseCondition pauseConditionB = new PseudoPauseCondition();
		pauseConditionB.setDeviceId("deviceId");
		pauseConditionB.setType(DataTypes.INT);
		pauseConditionB.setOperator(ComparisonTypes.EQ);
		pauseConditionB.setPauseLimit("1");
		pauseConditionB.setContinueLimit(null);
		
		assertEquals(pauseConditionA, pauseConditionB);
		assertEquals(pauseConditionA.hashCode(), pauseConditionB.hashCode());
	}
}
