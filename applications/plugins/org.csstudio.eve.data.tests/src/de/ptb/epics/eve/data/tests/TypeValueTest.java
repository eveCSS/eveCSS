package de.ptb.epics.eve.data.tests;

import java.util.List;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.ptb.epics.eve.data.TypeValue;
import de.ptb.epics.eve.data.measuringstation.Detector;
import de.ptb.epics.eve.data.measuringstation.Device;
import de.ptb.epics.eve.data.measuringstation.IMeasuringStation;
import de.ptb.epics.eve.data.measuringstation.Motor;
import de.ptb.epics.eve.data.measuringstation.MotorAxis;
import de.ptb.epics.eve.data.measuringstation.Option;
import de.ptb.epics.eve.data.tests.internal.Configurator;

/**
 * <code>TypeValueTest</code> contains 
 * <a href="http://www.junit.org/">JUnit</a>-Tests for 
 * {@link de.ptb.epics.eve.data.TypeValue}.
 * 
 * @author Marcus Michalsky
 * @since 0.4.1
 */
public class TypeValueTest {
	private static List<IMeasuringStation> stations;

	/**
	 * Test method for {@link de.ptb.epics.eve.data.TypeValue#hashCode()}.
	 */
	@Ignore("Not Implemented Yet!")
	@Test
	public final void testHashCode() {
		fail("Not yet implemented"); // TODO
	}
	
	/**
	 * Test method for
	 * {@link de.ptb.epics.eve.data.TypeValue#clone()}.
	 */
	@Ignore("Not Implemented Yet!")
	@Test
	public void testClone() {
		// TODO
	}
	
	/**
	 * Test method for
	 * {@link de.ptb.epics.eve.data.TypeValue#clone()} and
	 * {@link de.ptb.epics.eve.data.TypeValue#equals(java.lang.Object)}.
	 */
	@Test
	public final void testCloneEquals() {
		for(IMeasuringStation measuringStation : stations) {
			for(Motor m : measuringStation.getMotors()) {
				for(MotorAxis ma : m.getAxes()) {
					// testing goto (mandatory)
					TypeValue gotoTypeValue = ma.getGoto().getValue();
					if(gotoTypeValue != null) {
						TypeValue gotoClone = (TypeValue)gotoTypeValue.clone();
						assertEquals(gotoTypeValue, gotoClone);
					}
					// testing position (mandatory)
					TypeValue positionTypeValue = ma.getPosition().getValue();
					if(positionTypeValue != null) {
						TypeValue positionClone = (TypeValue)positionTypeValue.clone();
						assertEquals(positionTypeValue, positionClone);
					}
					// testing stop (mandatory)
					TypeValue stopTypeValue = ma.getStop().getValue();
					if(stopTypeValue != null) {
						TypeValue stopClone = (TypeValue) stopTypeValue.clone();
						assertEquals(stopTypeValue, stopClone);
					}
					
					// testing status (optional)
					if(ma.getStatus() != null && ma.getStatus().getValue() != null) {
						TypeValue statusTypeValue = ma.getStatus().getValue();
						TypeValue statusClone = (TypeValue)statusTypeValue.clone();
						assertEquals(statusTypeValue, statusClone);
					}
					
					// testing trigger (optional)
					if(ma.getTrigger() != null && ma.getTrigger().getValue() != null) {
						TypeValue triggerTypeValue = ma.getTrigger().getValue();
						TypeValue triggerClone = (TypeValue)triggerTypeValue.clone();
						assertEquals(triggerTypeValue, triggerClone);
					}
					
					// testing deadband (optional)
					if(ma.getDeadband() != null && ma.getDeadband().getValue() != null) {
						TypeValue deadbandTypeValue = ma.getDeadband().getValue();
						TypeValue deadbandClone = (TypeValue)deadbandTypeValue.clone();
						assertEquals(deadbandTypeValue, deadbandClone);
					}
					
					// testing offset (optional)
					if(ma.getOffset() != null && ma.getOffset().getValue() != null) {
						TypeValue offsetTypeValue = ma.getOffset().getValue();
						TypeValue offsetClone = (TypeValue)offsetTypeValue.clone();
						assertEquals(offsetTypeValue, offsetClone);
					}
					
					// testing tweakvalue (optional)
					if(ma.getTweakValue() != null && ma.getTweakValue().getValue() != null) {
						TypeValue tweakValueTypeValue = ma.getTweakValue().getValue();
						TypeValue tweakValueClone = (TypeValue)tweakValueTypeValue.clone();
						assertEquals(tweakValueTypeValue, tweakValueClone);
					}
					
					// testing tweak forward (optional)
					if(ma.getTweakForward() != null && ma.getTweakForward().getValue() != null) {
						TypeValue tweakForwardTypeValue = ma.getTweakForward().getValue();
						TypeValue tweakForwardClone = (TypeValue)tweakForwardTypeValue.clone();
						assertEquals(tweakForwardTypeValue, tweakForwardClone);
					}
					
					// testing tweak reverse (optional)
					if(ma.getTweakReverse() != null && ma.getTweakReverse().getValue() != null) {
						TypeValue tweakReverseTypeValue = ma.getTweakReverse().getValue();
						TypeValue tweakReverseClone = (TypeValue)tweakReverseTypeValue.clone();
						assertEquals(tweakReverseTypeValue, tweakReverseClone);
					}
					
					// TODO Unit ?
					
					for(Option o : ma.getOptions()) {
						if(o.getValue().getValue() != null) {
							TypeValue value = o.getValue().getValue();
							TypeValue clone = (TypeValue)value.clone();
							assertTrue(value.equals(clone));
							assertTrue(clone.equals(value));
						}
					}
				}
			}
			for (Detector d : measuringStation.getDetectors()) {
				// TODO
			}
			for (Device dev : measuringStation.getDevices()) {
				if (dev.getValue().getValue() != null) {
					TypeValue value = dev.getValue().getValue();
					TypeValue clone = (TypeValue)value.clone();
					assertTrue(value.equals(clone));
					assertTrue (clone.equals(value));
				}
			}
		}
	}
	
	/**
	 * Test method for
	 * {@link de.ptb.epics.eve.data.TypeValue#equals(java.lang.Object)}.
	 */
	@Test
	public void testEquals() {
		for(IMeasuringStation measuringStation : stations) {
			for(Motor m : measuringStation.getMotors()) {
				for(MotorAxis ma : m.getAxes()) {
					// testing goto (mandatory)
					TypeValue gotoTypeValue = ma.getGoto().getValue();
					if(gotoTypeValue != null) {
						TypeValue gotoClone = ma.getGoto().getValue();
						assertEquals(gotoTypeValue, gotoClone);
					}
					// testing position (mandatory)
					TypeValue positionTypeValue = ma.getPosition().getValue();
					if(positionTypeValue != null) {
						TypeValue positionClone = ma.getPosition().getValue();
						assertEquals(positionTypeValue, positionClone);
					}
					// testing stop (mandatory)
					TypeValue stopTypeValue = ma.getStop().getValue();
					if(stopTypeValue != null) {
						TypeValue stopClone = ma.getStop().getValue();
						assertEquals(stopTypeValue, stopClone);
					}
					
					// TODO test optional type values of:
					// status, trigger, unit, deadband, offset, tweakvalue, 
					// tweakforward, tweakreverse
					
					for(Option o : ma.getOptions()) {
						if(o.getValue().getValue() != null) {
							TypeValue value = o.getValue().getValue();
							TypeValue clone = o.getValue().getValue();
							assertTrue(value.equals(clone));
							assertTrue(clone.equals(value));
						}
					}
				}
			}
			for (Detector d : measuringStation.getDetectors()) {
				// TODO
			}
			for (Device dev : measuringStation.getDevices()) {
				TypeValue typeValue = dev.getValue().getValue();
				if (typeValue != null) {
					TypeValue cloneValue = dev.getValue().getValue();
					assertEquals(typeValue, cloneValue);
				}
			}
		}
	}

	// **********************************************************************
	// **************************** Setup ***********************************
	// **********************************************************************
	
	/**
	 * Initializes logging and loads the measuring station (Class wide setup 
	 * method of the test).
	 */
	@BeforeClass
	public static void setUpBeforeClass() {
		stations = Configurator.getMeasuringStations();
		
		for(IMeasuringStation measuringStation : stations) {
			assertNotNull(measuringStation);
		}
	}

	/**
	 * Class wide tear down method.
	 */
	@AfterClass
	public static void tearDownAfterClass() {
	}

	/**
	 * Test wide set up method.
	 */
	@Before
	public void setUp() {
	}

	/**
	 * Test wide tear down method.
	 */
	@After
	public void tearDown() {
	}
}