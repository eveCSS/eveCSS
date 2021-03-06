package de.ptb.epics.eve.data.scandescription.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.ptb.epics.eve.data.PluginTypes;
import de.ptb.epics.eve.data.measuringstation.MeasuringStation;
import de.ptb.epics.eve.data.measuringstation.PlugIn;
import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.data.scandescription.PluginController;
import de.ptb.epics.eve.data.scandescription.PositionMode;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.data.scandescription.Stepfunctions;
import de.ptb.epics.eve.data.tests.mothers.measuringstation.MotorAxisMother;

/**
 * @author Marcus Michalsky
 * @since 1.34.1
 */
public class AxisTest {
	private boolean stepfunction = false;
	private boolean pluginController = false;
	private boolean positionMode = false;
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
	
	@Test
	public void testPluginControllerPropertyChange() {
		PlugIn plugin = new PlugIn("PluginName", "PluginLocation", 
				PluginTypes.POSITION, new MeasuringStation());
		final PluginController initialValue = new PluginController(plugin);
		PlugIn pluginNew = new PlugIn("PluginNameNew", "PluginLocationNew",
				PluginTypes.POSITION, new MeasuringStation());
		final PluginController newValue = new PluginController(pluginNew);
		axis.setPluginController(initialValue);
		axis.addPropertyChangeListener(Axis.PLUGIN_CONTROLLER_PROP, 
				new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				assertEquals(initialValue, evt.getOldValue());
				assertEquals(newValue, evt.getNewValue());
				pluginController = true;
			}
		});
		axis.setPluginController(newValue);
		assertEquals(newValue, axis.getPluginController());
		assertTrue(pluginController);
	}

	@Test
	public void testPositionModePropertyChange() {
		final PositionMode oldValue = axis.getPositionMode();
		assertEquals(PositionMode.ABSOLUTE, axis.getPositionMode());
		axis.addPropertyChangeListener(Axis.POSITON_MODE_PROP, 
				new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals(Axis.POSITON_MODE_PROP)) {
					assertEquals("old positionmode", 
							oldValue, evt.getOldValue());
					assertEquals("new positionmode", 
							PositionMode.RELATIVE, evt.getNewValue());
					positionMode = true;
				}
			}
		});
		axis.setPositionMode(PositionMode.RELATIVE);
		assertEquals(PositionMode.RELATIVE, axis.getPositionMode());
		assertTrue(positionMode);
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
