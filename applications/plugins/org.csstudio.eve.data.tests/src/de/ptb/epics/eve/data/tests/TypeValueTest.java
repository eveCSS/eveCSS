package de.ptb.epics.eve.data.tests;

import static de.ptb.epics.eve.data.tests.internal.LogFileStringGenerator.*;

import java.util.List;

import static org.junit.Assert.*;

import org.apache.log4j.Logger;
import org.apache.log4j.RollingFileAppender;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.ptb.epics.eve.data.TypeValue;
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
	
	private static Logger logger = 
			Logger.getLogger(TypeValueTest.class.getName());

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

		log_start(logger, "testEquals");
		
		for(IMeasuringStation measuringStation : stations) {
			log_station(logger, measuringStation);
			for(Motor m : measuringStation.getMotors()) {
				logger.debug("Testing motor " + deviceString(m));
				for(MotorAxis ma : m.getAxes()) {
					logger.debug("Testing motor axis " + deviceString(ma));
					// testing goto (mandatory)
					TypeValue gotoTypeValue = ma.getGoto().getValue();
					if(gotoTypeValue != null) {
						TypeValue gotoClone = (TypeValue)gotoTypeValue.clone();
						assertEquals(gotoTypeValue, gotoClone);
						logger.debug("goto type values are equal");
					}
					// testing position (mandatory)
					TypeValue positionTypeValue = ma.getPosition().getValue();
					if(positionTypeValue != null) {
						TypeValue positionClone = (TypeValue)positionTypeValue.clone();
						assertEquals(positionTypeValue, positionClone);
						logger.debug("position type values are equal");
					}
					// testing stop (mandatory)
					TypeValue stopTypeValue = ma.getStop().getValue();
					if(stopTypeValue != null) {
						TypeValue stopClone = (TypeValue) stopTypeValue.clone();
						assertEquals(stopTypeValue, stopClone);
						logger.debug("stop type values are equal");
					}
					
					// testing status (optional)
					if(ma.getStatus() != null && ma.getStatus().getValue() != null) {
						TypeValue statusTypeValue = ma.getStatus().getValue();
						TypeValue statusClone = (TypeValue)statusTypeValue.clone();
						assertEquals(statusTypeValue, statusClone);
						logger.debug("status type values are equal");
					} else {
						logger.debug("no status entry");
					}
					
					// testing trigger (optional)
					if(ma.getTrigger() != null && ma.getTrigger().getValue() != null) {
						TypeValue triggerTypeValue = ma.getTrigger().getValue();
						TypeValue triggerClone = (TypeValue)triggerTypeValue.clone();
						assertEquals(triggerTypeValue, triggerClone);
						logger.debug("trigger type values are equal");
					} else {
						logger.debug("no trigger entry");
					}
					
					// testing deadband (optional)
					if(ma.getDeadband() != null && ma.getDeadband().getValue() != null) {
						TypeValue deadbandTypeValue = ma.getDeadband().getValue();
						TypeValue deadbandClone = (TypeValue)deadbandTypeValue.clone();
						assertEquals(deadbandTypeValue, deadbandClone);
						logger.debug("deadband type values are equal");
					} else {
						logger.debug("no deadband entry");
					}
					
					// testing offset (optional)
					if(ma.getOffset() != null && ma.getOffset().getValue() != null) {
						TypeValue offsetTypeValue = ma.getOffset().getValue();
						TypeValue offsetClone = (TypeValue)offsetTypeValue.clone();
						assertEquals(offsetTypeValue, offsetClone);
						logger.debug("offset type values are equal");
					} else {
						logger.debug("no offset entry");
					}
					
					// testing tweakvalue (optional)
					if(ma.getTweakValue() != null && ma.getTweakValue().getValue() != null) {
						TypeValue tweakValueTypeValue = ma.getTweakValue().getValue();
						TypeValue tweakValueClone = (TypeValue)tweakValueTypeValue.clone();
						assertEquals(tweakValueTypeValue, tweakValueClone);
						logger.debug("tweak value type values are equal");
					} else {
						logger.debug("no tweak value entry");
					}
					
					// testing tweak forward (optional)
					if(ma.getTweakForward() != null && ma.getTweakForward().getValue() != null) {
						TypeValue tweakForwardTypeValue = ma.getTweakForward().getValue();
						TypeValue tweakForwardClone = (TypeValue)tweakForwardTypeValue.clone();
						assertEquals(tweakForwardTypeValue, tweakForwardClone);
						logger.debug("tweak forward type values are equal");
					} else {
						logger.debug("no tweak forward entry");
					}
					
					// testing tweak reverse (optional)
					if(ma.getTweakReverse() != null && ma.getTweakReverse().getValue() != null) {
						TypeValue tweakReverseTypeValue = ma.getTweakReverse().getValue();
						TypeValue tweakReverseClone = (TypeValue)tweakReverseTypeValue.clone();
						assertEquals(tweakReverseTypeValue, tweakReverseClone);
						logger.debug("tweak reverse type values are equal");
					} else {
						logger.debug("no tweak reverse entry");
					}
					
					// TODO Unit ?
					
					for(Option o : ma.getOptions()) {
						if(o.getValue().getValue() != null) {
							logger.info("Testing TypeValue of option " + 
									deviceString(o));
							TypeValue value = o.getValue().getValue();
							TypeValue clone = (TypeValue)value.clone();
							assertTrue(value.equals(clone));
							assertTrue(clone.equals(value));
							logger.info("type value and clone are equal");
						} else {
							logger.info(deviceString(o) + "has no type value");
						}
					}
				}
			}
			// TODO Detectors ? , Devices ? , etc ?
		}
		log_end(logger, "testEquals");
	}
	
	/**
	 * Test method for
	 * {@link de.ptb.epics.eve.data.TypeValue#equals(java.lang.Object)}.
	 */
	@Test
	public void testEquals() {
		log_start(logger, "testEquals");
		
		for(IMeasuringStation measuringStation : stations) {
			log_station(logger, measuringStation);
			for(Motor m : measuringStation.getMotors()) {
				logger.debug("Testing motor " + deviceString(m));
				for(MotorAxis ma : m.getAxes()) {
					logger.debug("Testing motor axis " + deviceString(ma));
					// testing goto (mandatory)
					TypeValue gotoTypeValue = ma.getGoto().getValue();
					if(gotoTypeValue != null) {
						TypeValue gotoClone = ma.getGoto().getValue();
						assertEquals(gotoTypeValue, gotoClone);
						logger.debug("goto type values are equal");
					}
					// testing position (mandatory)
					TypeValue positionTypeValue = ma.getPosition().getValue();
					if(positionTypeValue != null) {
						TypeValue positionClone = ma.getPosition().getValue();
						assertEquals(positionTypeValue, positionClone);
						logger.debug("position type values are equal");
					}
					// testing stop (mandatory)
					TypeValue stopTypeValue = ma.getStop().getValue();
					if(stopTypeValue != null) {
						TypeValue stopClone = ma.getStop().getValue();
						assertEquals(stopTypeValue, stopClone);
						logger.debug("stop type values are equal");
					}
					
					// TODO test optional type values of:
					// status, trigger, unit, deadband, offset, tweakvalue, 
					// tweakforward, tweakreverse
					
					for(Option o : ma.getOptions()) {
						if(o.getValue().getValue() != null) {
							logger.info("Testing TypeValue of option " + 
									deviceString(o));
							TypeValue value = o.getValue().getValue();
							TypeValue clone = o.getValue().getValue();
							assertTrue(value.equals(clone));
							assertTrue(clone.equals(value));
							logger.info("type value and clone are equal");
						} else {
							logger.info(deviceString(o) + "has no type value");
						}
					}
				}
			}
		}
		log_end(logger, "testEquals");
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
		
		Configurator.configureLogging();
		
		((RollingFileAppender)logger.
				getAppender("TypeValueTestAppender")).rollOver();
		
		stations = Configurator.getMeasuringStations();
		
		for(IMeasuringStation measuringStation : stations) {
			assertNotNull(measuringStation);
		}
		
		classSetUp(logger);
	}

	/**
	 * Class wide tear down method.
	 */
	@AfterClass
	public static void tearDownAfterClass() {
		classTearDown(logger);
	}

	/**
	 * Test wide set up method.
	 */
	@Before
	public void setUp() {
		testSetUp(logger);
	}

	/**
	 * Test wide tear down method.
	 */
	@After
	public void tearDown() {
		testTearDown(logger);
	}
}