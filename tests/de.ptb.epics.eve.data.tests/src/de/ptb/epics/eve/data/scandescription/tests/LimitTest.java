package de.ptb.epics.eve.data.scandescription.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.ptb.epics.eve.data.ComparisonTypes;
import de.ptb.epics.eve.data.DataTypes;
import de.ptb.epics.eve.data.scandescription.Limit;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener;
import de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent;

/**
 * @author Marcus Michalsky
 * @since 1.36
 */
public class LimitTest {
	
	@Test
	public void testEqualsReflexive() {
		Limit limit = new Limit(DataTypes.INT, ComparisonTypes.EQ);
		limit.setValue("1");
		assertEquals("reflexive int test", limit, limit);
		
		limit = new Limit(DataTypes.DOUBLE, ComparisonTypes.GT);
		limit.setValue("1.1");
		assertEquals("reflexive double test", limit, limit);
		
		limit = new Limit(DataTypes.STRING, ComparisonTypes.LT);
		limit.setValue("foo");
		assertEquals("reflexive string test", limit, limit);
	}
	
	@Test
	public void testEqualsSymmetric() {
		Limit limitA = new Limit(DataTypes.INT, ComparisonTypes.EQ);
		limitA.setValue("1");
		Limit limitB = new Limit(DataTypes.INT, ComparisonTypes.EQ);
		limitB.setValue("1");
		assertTrue("symmetry: A equals B", limitA.equals(limitB));
		assertTrue("symmetry: B equals A", limitB.equals(limitA));
	}
	
	@Test
	public void testEqualsTransitive() {
		Limit limitA = new Limit(DataTypes.INT, ComparisonTypes.EQ);
		limitA.setValue("1");
		Limit limitB = new Limit(DataTypes.INT, ComparisonTypes.EQ);
		limitB.setValue("1");
		Limit limitC = new Limit(DataTypes.INT, ComparisonTypes.EQ);
		limitC.setValue("1");
		assertTrue("transitive: A equals B", limitA.equals(limitB));
		assertTrue("transitive: B equals C", limitB.equals(limitC));
		assertTrue("transitive: A equals C", limitA.equals(limitC));
	}
	
	@Test
	public void testEqualsConsistent() {
		Limit limitA = new Limit(DataTypes.INT, ComparisonTypes.EQ);
		limitA.setValue("1");
		Limit limitB = new Limit(DataTypes.INT, ComparisonTypes.EQ);
		limitB.setValue("1");
		assertEquals(limitA.equals(limitB), limitA.equals(limitB));
	}
	
	@Test
	public void testEqualsNull() {
		Limit limitA = new Limit(DataTypes.INT, ComparisonTypes.EQ);
		limitA.setValue("1");
		assertFalse(limitA.equals(null));
	}
	
	@Test
	public void testEqualsDifferentType() {
		Limit limitA = new Limit(DataTypes.INT, ComparisonTypes.EQ);
		limitA.setValue("1");
		Limit limitB = new Limit(DataTypes.DOUBLE, ComparisonTypes.EQ);
		limitB.setValue("1");
		assertFalse(limitA.equals(limitB));
	}
	
	@Test
	public void testEqualsDifferentOperator() {
		Limit limitA = new Limit(DataTypes.INT, ComparisonTypes.EQ);
		limitA.setValue("1");
		Limit limitB = new Limit(DataTypes.INT, ComparisonTypes.NE);
		limitB.setValue("1");
		assertFalse(limitA.equals(limitB));
	}
	
	@Test
	public void testEqualsDifferentValue() {
		Limit limitA = new Limit(DataTypes.INT, ComparisonTypes.EQ);
		limitA.setValue("1");
		Limit limitB = new Limit(DataTypes.INT, ComparisonTypes.EQ);
		limitB.setValue("2");
		assertFalse(limitA.equals(limitB));
	}
	
	@Test
	public void testEquals() {
		Limit limitA = new Limit(DataTypes.INT, ComparisonTypes.EQ);
		limitA.setValue("1");
		Limit limitB = new Limit(DataTypes.INT, ComparisonTypes.EQ);
		limitB.setValue("1");
		assertTrue(limitA.equals(limitB));
	}
	
	@Test
	public void testHashCodeConsistent() {
		Limit limit = new Limit(DataTypes.INT, ComparisonTypes.EQ);
		limit.setValue("1");
		assertEquals(limit.hashCode(), limit.hashCode());
	}
	
	@Test
	public void testHashCodeEquality() {
		Limit limitA = new Limit(DataTypes.INT, ComparisonTypes.EQ);
		limitA.setValue("1");
		Limit limitB = new Limit(DataTypes.INT, ComparisonTypes.EQ);
		limitB.setValue("1");
		assertEquals(limitA, limitB);
		assertEquals(limitA.hashCode(), limitB.hashCode());
	}
	
	private boolean typeFired;
	@Test
	public void testUpdateEventType() {
		this.typeFired = false;
		Limit limit = new Limit(DataTypes.INT, ComparisonTypes.EQ);
		limit.setValue("1");
		limit.addModelUpdateListener(new IModelUpdateListener() {
			@Override
			public void updateEvent(ModelUpdateEvent modelUpdateEvent) {
				typeFired = true;
			}
		});
		limit.setType(DataTypes.DOUBLE);
		assertTrue(this.typeFired);
	}
	
	private boolean comparisonFired;
	@Test
	public void testUpdateEventOperator() {
		this.comparisonFired = false;
		Limit limit = new Limit(DataTypes.INT, ComparisonTypes.EQ);
		limit.setValue("1");
		limit.addModelUpdateListener(new IModelUpdateListener() {
			@Override
			public void updateEvent(ModelUpdateEvent modelUpdateEvent) {
				comparisonFired = true;
			}
		});
		limit.setComparison(ComparisonTypes.LT);
		assertTrue(this.comparisonFired);
	}
	
	private boolean valueFired;
	@Test
	public void testUpdateEventValue() {
		this.valueFired = false;
		Limit limit = new Limit(DataTypes.INT, ComparisonTypes.EQ);
		limit.setValue("1");
		limit.addModelUpdateListener(new IModelUpdateListener() {
			@Override
			public void updateEvent(ModelUpdateEvent modelUpdateEvent) {
				valueFired = true;
			}
		});
		limit.setValue("2");
		assertTrue(this.valueFired);
	}
}
