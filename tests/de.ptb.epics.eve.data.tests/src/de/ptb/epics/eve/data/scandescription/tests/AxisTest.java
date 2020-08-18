package de.ptb.epics.eve.data.scandescription.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.data.scandescription.Stepfunctions;
import de.ptb.epics.eve.data.tests.mothers.measuringstation.MotorAxisMother;

/**
 * @author Marcus Michalsky
 * @since 1.34.1
 */
public class AxisTest {
	private boolean stepfunction = false;
	private Axis axis;
	
	@Test
	public void testStepfunctionPropertyChange() {
		final Stepfunctions oldValue = axis.getStepfunction();
		assertEquals(Stepfunctions.ADD, axis.getStepfunction());
		axis.addPropertyChangeListener(Axis.STEPFUNCTION_PROP, 
				new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals(Axis.STEPFUNCTION_PROP)) {
					assertEquals("old stepfunction", 
							oldValue, evt.getOldValue());
					assertEquals("new stepfunction", 
							Stepfunctions.FILE, evt.getNewValue());
					stepfunction = true;
				}
			}
		});
		axis.setStepfunction(Stepfunctions.FILE);
		assertEquals(Stepfunctions.FILE, axis.getStepfunction());
		assertTrue(stepfunction);
	}
	
	@Before
	public void beforeTest() {
		ScanModule scanModule = new ScanModule(1);
		axis = new Axis(scanModule);
		axis.setMotorAxis(MotorAxisMother.createNewDoubleTypeMotorAxis());
	}
	
	@After
	public void afterTest() {
		axis = null;
	}
}
