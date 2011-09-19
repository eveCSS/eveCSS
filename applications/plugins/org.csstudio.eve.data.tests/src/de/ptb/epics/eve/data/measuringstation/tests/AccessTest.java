package de.ptb.epics.eve.data.measuringstation.tests;

import static de.ptb.epics.eve.data.tests.internal.LogFileStringGenerator.*;

import java.util.List;

import static org.junit.Assert.*;

import org.apache.log4j.Logger;
import org.apache.log4j.RollingFileAppender;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.ptb.epics.eve.data.measuringstation.Access;
import de.ptb.epics.eve.data.measuringstation.Detector;
import de.ptb.epics.eve.data.measuringstation.DetectorChannel;
import de.ptb.epics.eve.data.measuringstation.Device;
import de.ptb.epics.eve.data.measuringstation.IMeasuringStation;
import de.ptb.epics.eve.data.measuringstation.Motor;
import de.ptb.epics.eve.data.measuringstation.MotorAxis;
import de.ptb.epics.eve.data.measuringstation.Option;
import de.ptb.epics.eve.data.tests.internal.Configurator;

/**
 * <code>AccessTest</code> contains 
 * <a href="http://www.junit.org/">JUnit</a>-Tests for 
 * {@link de.ptb.epics.eve.data.measuringstation.Access}.
 * 
 * @author Marcus Michalsky
 * @since 0.4.1
 */
public class AccessTest {

	private static Logger logger = Logger.getLogger(AccessTest.class.getName());

	private static List<IMeasuringStation> stations;
	
	/**
	 * Test method for 
	 * {@link de.ptb.epics.eve.data.measuringstation.Access#clone()} and 
	 * {@link de.ptb.epics.eve.data.measuringstation.Access#equals(java.lang.Object)}.
	 * 
	 * <p>
	 * Consider that this test could only detect asymmetric problems. A 
	 * conceptual error (e.g. omit to test/clone a property both in equals and 
	 * clone) could not be detected. If for example a property A is not cloned 
	 * and also not verified with equals an equality test of the object and its 
	 * clone would be successful although the cloned object does not have the 
	 * same property A.
	 */
	@Test
	public void testCloneEquals() {
		log_start(logger, "testEquals");
		for(IMeasuringStation measuringStation : stations) {
			log_station(logger, measuringStation);
			for(Motor m : measuringStation.getMotors()) {
				logger.debug("Testing motor " + deviceString(m));
				
				if(m.getTrigger() != null) {
					Access triggerAccess = m.getTrigger().getAccess();
					Access triggerClone = (Access)triggerAccess.clone();
					assertEquals(triggerAccess, triggerClone);
					logger.debug("trigger Access' are equal");
				} else {
					logger.debug("no trigger entry");
				}
				
				if(m.getUnit() != null && m.getUnit().getAccess() != null) {
					Access unitAccess = m.getUnit().getAccess();
					Access unitClone = (Access)unitAccess.clone();
					assertEquals(unitAccess, unitClone);
					logger.debug("unit Access' are equal");
				} else {
					logger.debug("no unit entry");
				}
				
				for(Option motorOption : m.getOptions()) {
					logger.info("Testing Access of option " + deviceString(motorOption));
					Access motorOptionAccess = motorOption.getValue().getAccess();
					Access motorOptionClone = (Access)motorOptionAccess.clone();
					assertEquals(motorOptionAccess, motorOptionClone);
				}
				
				for(MotorAxis ma : m.getAxes()) {
					logger.debug("Testing motor axis " + deviceString(ma));
					// testing goto (mandatory)
					Access axisAccess = ma.getGoto().getAccess();
					Access axisClone = (Access)axisAccess.clone();
					assertEquals(axisAccess, axisClone);
					logger.debug("goto Access' are equal");
					// testing position (mandatory)
					Access positionAccess = ma.getPosition().getAccess();
					Access positionClone = (Access)positionAccess.clone();
					assertEquals(positionAccess, positionClone);
					logger.debug("position Access' are equal");
					// testing stop (mandatory)
					Access stopAccess = ma.getStop().getAccess();
					Access stopClone = (Access)stopAccess.clone();
					assertEquals(stopAccess, stopClone);
					logger.debug("stop Access' are equal");
					
					if(ma.getStatus() != null) {
						Access statusAccess = ma.getStatus().getAccess();
						Access statusClone = (Access)statusAccess.clone();
						assertEquals(statusAccess, statusClone);
						logger.debug("status Access' are equal");
					} else {
						logger.debug("no status entry");
					}
					
					if(ma.getTrigger() != null) {
						Access triggerAccess = ma.getTrigger().getAccess();
						Access triggerClone = (Access)triggerAccess.clone();
						assertEquals(triggerAccess, triggerClone);
						logger.debug("trigger Access' are equal");
					} else {
						logger.debug("no trigger entry");
					}
					
					if(ma.getDeadband() != null) {
						Access deadbandAccess = ma.getDeadband().getAccess();
						Access deadbandClone = (Access)deadbandAccess.clone();
						assertEquals(deadbandAccess, deadbandClone);
						logger.debug("deadband Access' are equal");
					} else {
						logger.debug("no deadband entry");
					}
					
					if(ma.getOffset() != null) {
						Access offsetAccess = ma.getOffset().getAccess();
						Access offsetClone = (Access)offsetAccess.clone();
						assertEquals(offsetAccess, offsetClone);
						logger.debug("offset Access' are equal");
					} else {
						logger.debug("no offset entry");
					}
					
					if(ma.getSet() != null) {
						Access setAccess = ma.getSet().getAccess();
						Access setClone = (Access)setAccess.clone();
						assertEquals(setAccess, setClone);
						logger.debug("set Access' are equal");
					} else {
						logger.debug("no setmode entry");
					}
					
					if(ma.getTweakValue() != null) {
						Access tweakValAccess = ma.getTweakValue().getAccess();
						Access tweakValClone = (Access) tweakValAccess.clone();
						assertEquals(tweakValAccess, tweakValClone);
						logger.debug("tweakValue Access' are equal");
					} else {
						logger.debug("no tweak value entry");
					}
					
					if(ma.getTweakForward() != null) {
						Access tweakForwardAccess = ma.getTweakForward().getAccess();
						Access tweakForwardClone = (Access)tweakForwardAccess.clone();
						assertEquals(tweakForwardAccess, tweakForwardClone);
						logger.debug("tweak forward Access' are equal");
					} else {
						logger.debug("no tweak forward entry");
					}
					
					if(ma.getTweakReverse() != null) {
						Access tweakReverseAccess = ma.getTweakReverse().getAccess();
						Access tweakReverseClone = (Access)tweakReverseAccess.clone();
						assertEquals(tweakReverseAccess, tweakReverseClone);
						logger.debug("tweak reverse Access' are equal");
					} else {
						logger.debug("no tweak reverse entry");
					}
					
					if(ma.getUnit() != null && ma.getUnit().getAccess() != null) {
						Access unitAccess = ma.getUnit().getAccess();
						Access unitClone = (Access)unitAccess.clone();
						assertEquals(unitAccess, unitClone);
						logger.debug("unit Access' are equal");
					} else {
						logger.debug("no unit entry");
					}
					
					for(Option o : ma.getOptions()) {
						logger.info("Testing Access of option " + deviceString(o));
					
						Access a = o.getValue().getAccess();
						Access clone = (Access)a.clone();
						assertTrue(a.equals(clone));
						assertTrue(clone.equals(a));
					
						logger.debug("Access " + a.getVariableID() + 
							" and clone are equal.");
					}
				}
			} // end of motor for loop
			for(Detector detector : measuringStation.getDetectors()) {
				logger.info("Testing detector " + deviceString(detector));
				
				if(detector.getUnit() != null && detector.getUnit().getAccess() != null) {
					Access unitAccess = detector.getUnit().getAccess();
					Access unitClone = (Access)unitAccess.clone();
					assertEquals(unitAccess, unitClone);
					logger.debug("unit Access' are equal");
				} else {
					logger.debug("no unit entry");
				}
				
				if(detector.getTrigger() != null) {
					Access triggerAccess = detector.getTrigger().getAccess();
					Access triggerClone = (Access)triggerAccess.clone();
					assertEquals(triggerAccess, triggerClone);
					logger.debug("trigger Access' are equal");
				} else {
					logger.debug("no trigger entry");
				}
				
				for(Option detectorOption : detector.getOptions()) {
					Access detectorOptionAccess = detectorOption.getValue().getAccess();
					Access detectorOptionClone = (Access)detectorOptionAccess.clone();
					assertEquals(detectorOptionAccess, detectorOptionClone);
					logger.debug("detector option Access' are equal");
				}
				
				for(DetectorChannel ch : detector.getChannels()) {
					logger.info("Testing detector channel " + deviceString(ch));
					Access read = ch.getRead().getAccess();
					Access readClone = (Access)read.clone();
					assertEquals(read, readClone);
					logger.debug("read Access' are equal");
					
					if(ch.getUnit() != null && ch.getUnit().getAccess() != null) {
						Access unitAccess = ch.getUnit().getAccess();
						Access unitClone = (Access)unitAccess.clone();
						assertEquals(unitAccess, unitClone);
						logger.debug("unit Access' are equal");
					} else {
						logger.debug("no unit entry");
					}
					
					if(ch.getTrigger() != null) {
						Access triggerAccess = ch.getTrigger().getAccess();
						Access triggerClone = (Access)triggerAccess.clone();
						assertEquals(triggerAccess, triggerClone);
						logger.debug("trigger Access' are equal");
					} else {
						logger.debug("no trigger entry");
					}
					
					for(Option channelOption : ch.getOptions()) {
						logger.info("Testing detector channel option " + deviceString(channelOption));
						Access channelOptionAccess = channelOption.getValue().getAccess();
						Access channelOptionClone = (Access)channelOptionAccess.clone();
						assertEquals(channelOptionAccess, channelOptionClone);
						logger.debug("channel option Access' are equal");
					}
				}
			}
			for(Device device : measuringStation.getDevices()) {
				logger.info("Testing device " + deviceString(device));
				Access deviceValueAccess = device.getValue().getAccess();
				Access deviceValueClone = (Access)deviceValueAccess.clone();
				assertEquals(deviceValueAccess, deviceValueClone);
				logger.debug("device value Access' are equal");
				
				if(device.getUnit() != null && device.getUnit().getAccess() != null) {
					Access unitAccess = device.getUnit().getAccess();
					Access unitClone = (Access)unitAccess.clone();
					assertEquals(unitAccess, unitClone);
					logger.debug("unit Access' are equal");
				} else {
					logger.debug("no unit entry");
				}
				
				for(Option deviceOption : device.getOptions()) {
					logger.info("Testing device option " + deviceString(deviceOption));
					Access deviceOptionAccess = deviceOption.getValue().getAccess();
					Access deviceOptionClone = (Access)deviceOptionAccess.clone();
					assertEquals(deviceOptionAccess, deviceOptionClone);
					logger.debug("device option Access' are equal");
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
		Configurator.configureLogging();
		
		((RollingFileAppender)logger.
				getAppender("AccessTestAppender")).rollOver();
		
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