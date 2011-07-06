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

	// logging
	private static Logger logger = Logger.getLogger(AccessTest.class.getName());

	// the measuring station
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
					
					// TODO test optional type values of:
					// status, trigger, unit, deadband, offset, tweakvalue, 
					// tweakforward, tweakreverse
					
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
			}
			for(Detector detector : measuringStation.getDetectors()) {
				logger.info("Testing detector " + deviceString(detector));
				
				// TODO optional unit, trigger
				
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
					
					// TODO optional unit, trigger
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
				
				// TODO optional unit
				
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