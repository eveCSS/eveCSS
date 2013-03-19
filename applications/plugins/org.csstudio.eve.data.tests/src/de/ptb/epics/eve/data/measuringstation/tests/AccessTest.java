package de.ptb.epics.eve.data.measuringstation.tests;

import java.util.List;

import static org.junit.Assert.*;

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
		for(IMeasuringStation measuringStation : stations) {
			for(Motor m : measuringStation.getMotors()) {
				if(m.getTrigger() != null) {
					Access triggerAccess = m.getTrigger().getAccess();
					Access triggerClone = (Access)triggerAccess.clone();
					assertEquals(triggerAccess, triggerClone);
				}
				
				if(m.getUnit() != null && m.getUnit().getAccess() != null) {
					Access unitAccess = m.getUnit().getAccess();
					Access unitClone = (Access)unitAccess.clone();
					assertEquals(unitAccess, unitClone);
				}
				
				for(Option motorOption : m.getOptions()) {
					Access motorOptionAccess = motorOption.getValue().getAccess();
					Access motorOptionClone = (Access)motorOptionAccess.clone();
					assertEquals(motorOptionAccess, motorOptionClone);
				}
				
				for(MotorAxis ma : m.getAxes()) {
					// testing goto (mandatory)
					Access axisAccess = ma.getGoto().getAccess();
					Access axisClone = (Access)axisAccess.clone();
					assertEquals(axisAccess, axisClone);
					// testing position (mandatory)
					Access positionAccess = ma.getPosition().getAccess();
					Access positionClone = (Access)positionAccess.clone();
					assertEquals(positionAccess, positionClone);
					// testing stop (mandatory)
					Access stopAccess = ma.getStop().getAccess();
					Access stopClone = (Access)stopAccess.clone();
					assertEquals(stopAccess, stopClone);
					
					if(ma.getStatus() != null) {
						Access statusAccess = ma.getStatus().getAccess();
						Access statusClone = (Access)statusAccess.clone();
						assertEquals(statusAccess, statusClone);
					}
					
					if(ma.getTrigger() != null) {
						Access triggerAccess = ma.getTrigger().getAccess();
						Access triggerClone = (Access)triggerAccess.clone();
						assertEquals(triggerAccess, triggerClone);
					}
					
					if(ma.getDeadband() != null) {
						Access deadbandAccess = ma.getDeadband().getAccess();
						Access deadbandClone = (Access)deadbandAccess.clone();
						assertEquals(deadbandAccess, deadbandClone);
					}
					
					if(ma.getOffset() != null) {
						Access offsetAccess = ma.getOffset().getAccess();
						Access offsetClone = (Access)offsetAccess.clone();
						assertEquals(offsetAccess, offsetClone);
					}
					
					if(ma.getTweakValue() != null) {
						Access tweakValAccess = ma.getTweakValue().getAccess();
						Access tweakValClone = (Access) tweakValAccess.clone();
						assertEquals(tweakValAccess, tweakValClone);
					}
					
					if(ma.getTweakForward() != null) {
						Access tweakForwardAccess = ma.getTweakForward().getAccess();
						Access tweakForwardClone = (Access)tweakForwardAccess.clone();
						assertEquals(tweakForwardAccess, tweakForwardClone);
					}
					
					if(ma.getTweakReverse() != null) {
						Access tweakReverseAccess = ma.getTweakReverse().getAccess();
						Access tweakReverseClone = (Access)tweakReverseAccess.clone();
						assertEquals(tweakReverseAccess, tweakReverseClone);
					}
					
					if(ma.getUnit() != null && ma.getUnit().getAccess() != null) {
						Access unitAccess = ma.getUnit().getAccess();
						Access unitClone = (Access)unitAccess.clone();
						assertEquals(unitAccess, unitClone);
					}
					
					for(Option o : ma.getOptions()) {
						Access a = o.getValue().getAccess();
						Access clone = (Access)a.clone();
						assertTrue(a.equals(clone));
						assertTrue(clone.equals(a));
					}
				}
			} // end of motor for loop
			for(Detector detector : measuringStation.getDetectors()) {
				if(detector.getUnit() != null && detector.getUnit().getAccess() != null) {
					Access unitAccess = detector.getUnit().getAccess();
					Access unitClone = (Access)unitAccess.clone();
					assertEquals(unitAccess, unitClone);
				}
				
				if(detector.getTrigger() != null) {
					Access triggerAccess = detector.getTrigger().getAccess();
					Access triggerClone = (Access)triggerAccess.clone();
					assertEquals(triggerAccess, triggerClone);
				}
				
				for(Option detectorOption : detector.getOptions()) {
					Access detectorOptionAccess = detectorOption.getValue().getAccess();
					Access detectorOptionClone = (Access)detectorOptionAccess.clone();
					assertEquals(detectorOptionAccess, detectorOptionClone);
				}
				
				for(DetectorChannel ch : detector.getChannels()) {
					Access read = ch.getRead().getAccess();
					Access readClone = (Access)read.clone();
					assertEquals(read, readClone);
					
					if(ch.getUnit() != null && ch.getUnit().getAccess() != null) {
						Access unitAccess = ch.getUnit().getAccess();
						Access unitClone = (Access)unitAccess.clone();
						assertEquals(unitAccess, unitClone);
					}
					
					if(ch.getTrigger() != null) {
						Access triggerAccess = ch.getTrigger().getAccess();
						Access triggerClone = (Access)triggerAccess.clone();
						assertEquals(triggerAccess, triggerClone);
					}
					
					for(Option channelOption : ch.getOptions()) {
						Access channelOptionAccess = channelOption.getValue().getAccess();
						Access channelOptionClone = (Access)channelOptionAccess.clone();
						assertEquals(channelOptionAccess, channelOptionClone);
					}
				}
			}
			for(Device device : measuringStation.getDevices()) {
				Access deviceValueAccess = device.getValue().getAccess();
				Access deviceValueClone = (Access)deviceValueAccess.clone();
				assertEquals(deviceValueAccess, deviceValueClone);
				
				if(device.getUnit() != null && device.getUnit().getAccess() != null) {
					Access unitAccess = device.getUnit().getAccess();
					Access unitClone = (Access)unitAccess.clone();
					assertEquals(unitAccess, unitClone);
				}
				
				for(Option deviceOption : device.getOptions()) {
					Access deviceOptionAccess = deviceOption.getValue().getAccess();
					Access deviceOptionClone = (Access)deviceOptionAccess.clone();
					assertEquals(deviceOptionAccess, deviceOptionClone);
				}
			}
		}
	}

	// **********************************************************************
	// **************************** Setup ***********************************
	// **********************************************************************
	
	/**
	 * Class wide setup method of the test
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