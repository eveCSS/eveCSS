package de.ptb.epics.eve.data.scandescription.axismode.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.junit.Before;
import org.junit.Test;

import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.data.scandescription.axismode.PositionlistMode;
import de.ptb.epics.eve.data.tests.mothers.measuringstation.MotorAxisMother;
import de.ptb.epics.eve.data.tests.mothers.scandescription.ScanModuleMother;

public class PositionlistModeTest {
	private boolean eventFired;
	private PositionlistMode positionlistMode;
	
	@Test
	public void testPropertyChangeSupport() {
		eventFired = false;
		final String initialValue = "1,2,3";
		final String newValue = "4,5,6";
		this.positionlistMode.setPositionList(initialValue);
		this.positionlistMode.addPropertyChangeListener(
				PositionlistMode.POSITIONLIST_PROP, 
				new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				assertEquals(initialValue, evt.getOldValue());
				assertEquals(newValue, evt.getNewValue());
				eventFired = true;
			}
		});
		this.positionlistMode.setPositionList(newValue);
		assertEquals(newValue, this.positionlistMode.getPositionList());
		assertTrue(eventFired);
	}
	
	@Before
	public void beforeTest() {
		Axis axis = new Axis(ScanModuleMother.createNewScanModule(), 
				MotorAxisMother.createNewDoubleTypeMotorAxis());
		this.positionlistMode = new PositionlistMode(axis);
	}
}
