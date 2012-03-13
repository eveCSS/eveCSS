package de.ptb.epics.eve.data.measuringstation.filter.tests;

import static de.ptb.epics.eve.data.tests.internal.LogFileStringGenerator.*;

import java.util.List;

import static org.junit.Assert.*;

import org.apache.log4j.Logger;
import org.apache.log4j.RollingFileAppender;
import org.junit.*;

import de.ptb.epics.eve.data.EventTypes;
import de.ptb.epics.eve.data.measuringstation.AbstractDevice;
import de.ptb.epics.eve.data.measuringstation.Detector;
import de.ptb.epics.eve.data.measuringstation.DetectorChannel;
import de.ptb.epics.eve.data.measuringstation.Device;
import de.ptb.epics.eve.data.measuringstation.Event;
import de.ptb.epics.eve.data.measuringstation.IMeasuringStation;
import de.ptb.epics.eve.data.measuringstation.Motor;
import de.ptb.epics.eve.data.measuringstation.MotorAxis;
import de.ptb.epics.eve.data.measuringstation.Option;
import de.ptb.epics.eve.data.measuringstation.filter.ExcludeFilter;
import de.ptb.epics.eve.data.scandescription.Channel;
import de.ptb.epics.eve.data.scandescription.ControlEvent;
import de.ptb.epics.eve.data.scandescription.PauseEvent;
import de.ptb.epics.eve.data.scandescription.ScanDescription;
import de.ptb.epics.eve.data.tests.internal.Configurator;

/**
 * <code>ExcludeFilterTest</code> contains 
 * <a href="http://www.junit.org/">JUnit</a>-Tests for 
 * {@link de.ptb.epics.eve.data.measuringstation.filter.ExcludeFilter}.
 * Most of the tests just test each 
 * {@link de.ptb.epics.eve.data.measuringstation.AbstractDevice} by checking 
 * its presence, exclude it and check his absence, include it and check its 
 * presence again. The presence and absence of excluded/included sub devices 
 * (e.g. an axis of a motor or a channel of a detector or options) are also 
 * tested.
 * 
 * @author Marcus Michalsky
 * @since 0.4.1
 */
public class ExcludeFilterTest {
	
	// logging
	private static Logger logger = 
		Logger.getLogger(ExcludeFilterTest.class.getName());
	
	private static List<IMeasuringStation> stations;
	
	/**
	 * <code>testExcludeIncludeMotor</code> tries to exclude motors (each one 
	 * by one) and verifies their absence. It also checks the presence / absence 
	 * of their axis and options. Afterwards they are included and presence is 
	 * checked again.
	 */
	@Test
	public void testExcludeIncludeMotor() {
		log_start(logger, "testExcludeIncludeMotor()");
		
		for(IMeasuringStation measuringStation : stations) {
		
			log_station(logger, measuringStation);
			
			ExcludeFilter filteredMeasuringStation = new ExcludeFilter();
			filteredMeasuringStation.setSource(measuringStation);
			assertNotNull(filteredMeasuringStation);
			
		for(Motor m : measuringStation.getMotors()) {
			logger.info("Testing Motor: " + deviceString(m));
			
			// the motor should be found
			assertTrue(isMotor(filteredMeasuringStation, m));
			logger.info("Motor " + deviceString(m) + " found.");
			
			// its options should be found
			for(Option o : m.getOptions()) {
				assertTrue(isOption(filteredMeasuringStation, o));
				logger.info("Motor Option " + deviceString(o) + " found.");
			}
			if(m.getOptions().size() == 0)
				logger.info("Motor " + deviceString(m) + " has no options.");
			
			// its axis should also be found
			for(MotorAxis ma : m.getAxes()) {
				assertTrue(isMotorAxis(filteredMeasuringStation, ma));
				logger.info("Motor Axis " + deviceString(ma) + " found.");
				
				// axis options should be found as well
				for(Option o : ma.getOptions()) {
					assertTrue(isOption(filteredMeasuringStation, o));
					logger.info("Motor Axis Option " + deviceString(o) + 
								" found.");
				}
				if(ma.getOptions().size() == 0)
					logger.info("Motor Axis " + deviceString(ma) + 
								" has no options.");
			}
			
			// ***
			
			// exclude the motor
			filteredMeasuringStation.exclude(m);
			logger.info("Motor " + deviceString(m) + " excluded.");
			
			// now the motor shouldn't be found anymore
			assertFalse(isMotor(filteredMeasuringStation, m));
			logger.info("Motor " + deviceString(m) + " not found.");
			
			// its options shouldn't be found
			for(Option o : m.getOptions()) {
				assertFalse(isOption(filteredMeasuringStation, o));
				logger.info("Motor Option " + deviceString(o) + " not found.");
			}
			if(m.getOptions().size() == 0)
				logger.info("Motor " + deviceString(m) + " has no options.");
			
			// all axes of the motor also shouldn't be found
			for(MotorAxis ma : m.getAxes()) {
				logger.info("Checking if axis " + deviceString(ma) + 
							" of the motor is also excluded");
				assertFalse(isMotorAxis(filteredMeasuringStation, ma));
				logger.info("Motor Axis " + deviceString(ma) + " not found.");
				
				// axis options shouldn't be found as well
				for(Option o : ma.getOptions()) {
					assertFalse(isOption(filteredMeasuringStation, o));
					logger.info("Motor Axis Option " + deviceString(o) + 
								" not found.");
				}
				if(ma.getOptions().size() == 0)
					logger.info("Motor Axis " + deviceString(ma) + 
								" has no options.");
			}
			
			// ***"
			
			// include the motor
			filteredMeasuringStation.include(m);
			logger.info("Motor " + deviceString(m) + " included.");
			
			// now the motor should be found again
			assertTrue(isMotor(filteredMeasuringStation, m));
			logger.info("Motor " + deviceString(m) + " found.");
			
			// its options should be found
			for(Option o : m.getOptions()) {
				assertTrue(isOption(filteredMeasuringStation, o));
				logger.info("Motor Option " + deviceString(o) + " found.");
			}
			if(m.getOptions().size() == 0)
				logger.info("Motor " + deviceString(m) + " has no options.");
			
			// all axes of the motor also should be back
			for(MotorAxis ma : m.getAxes()) {
				logger.info("Checking if axis " + deviceString(ma) + 
							" of the motor is also included");
				assertTrue(isMotorAxis(filteredMeasuringStation, ma));
				logger.info("Motor Axis " + deviceString(ma) + " found.");
				
				// axis options should be found as well
				for(Option o : ma.getOptions())
				{
					assertTrue(isOption(filteredMeasuringStation, o));
					logger.info("Motor Axis Option " + deviceString(o) + 
								" found.");
				}
				if(ma.getOptions().size() == 0)
					logger.info("Motor Axis " + deviceString(ma) + 
								" has no options.");
			}
			logger.info("-----");
		}
		}
		log_end(logger, "testExcludeIncludeMotor()");
	}
	
	/**
	 * <code>testExcludeIncludeMotorAxis</code> tries to exclude motor axis 
	 * (each one by one) and verifies their absence. It also checks the 
	 * presence / absence of their options. Afterwards they are included and 
	 * presence is checked again.
	 */
	@Test
	public void testExcludeIncludeMotorAxis() {
		log_start(logger, "testExcludeIncludeMotorAxis()");
		
		for(IMeasuringStation measuringStation : stations) {
			
			log_station(logger, measuringStation);
			
			ExcludeFilter filteredMeasuringStation = new ExcludeFilter();
			filteredMeasuringStation.setSource(measuringStation);
			assertNotNull(filteredMeasuringStation);
		
		for(Motor m : measuringStation.getMotors()) {
			logger.info("Testing axes of motor " + deviceString(m));
			assertTrue(isMotor(filteredMeasuringStation, m));
			
			for(MotorAxis ma : m.getAxes()) {
				logger.info("Testing motor axis " + deviceString(ma));
				
				// the motor axis should be found
				assertTrue(isMotorAxis(filteredMeasuringStation, ma));
				logger.info("Motor axis " + deviceString(ma) + " found");
				
				// axis options should be found as well
				for(Option o : ma.getOptions()) {
					assertTrue(isOption(filteredMeasuringStation, o));
					logger.info("Motor Axis Option " + deviceString(o) + 
								" found.");
				}
				
				// ***
				
				// exclude it
				filteredMeasuringStation.exclude(ma);
				logger.info("Motor axis " + deviceString(ma) + " excluded.");
				
				// now it shouldn't be found anymore
				assertFalse(isMotorAxis(filteredMeasuringStation, ma));
				logger.info("Motor axis " + deviceString(ma) + " not found");
				
				// axis options shouldn't be found as well
				for(Option o : ma.getOptions()) {
					assertFalse(isOption(filteredMeasuringStation, o));
					logger.info("Motor Axis Option " + deviceString(o) + 
								" not found.");
				}
				
				assertTrue(isMotor(filteredMeasuringStation, m));
				logger.info("Motor " + deviceString(m) + " found");
				
				// ***
				
				// include it
				filteredMeasuringStation.include(ma);
				logger.info("Motor axis " + deviceString(ma) + " included.");
				
				// now it should be found again
				assertNotNull(filteredMeasuringStation.
								getMotorAxisById(ma.getID()));
				logger.info("Motor axis " + deviceString(ma) + " found");
				
				// axis options should be found as well
				for(Option o : ma.getOptions()) {
					assertTrue(isOption(filteredMeasuringStation, o));
					logger.info("Motor Axis Option " + deviceString(o) + 
								" found.");
				}
				
				logger.info("***");
			}
			logger.info("-----");
		}
		}
		log_end(logger, "testExcludeIncludeMotorAxis()");
	}
	
	/**
	 * <code>testExcludeIncludeMotorOption</code> tries to exclude motor options 
	 * (each one by one) and verifies their absence. Afterwards they are 
	 * included and presence is checked again.
	 */
	@Test
	public void testExcludeIncludeMotorOption() {
		log_start(logger, "testExcludeIncludeMotorOption()");
		
		for(IMeasuringStation measuringStation : stations) {
			
			log_station(logger, measuringStation);
			
			ExcludeFilter filteredMeasuringStation = new ExcludeFilter();
			filteredMeasuringStation.setSource(measuringStation);
			assertNotNull(filteredMeasuringStation);
		
		for(Motor m : measuringStation.getMotors()) {
			logger.info("Testing options of motor " + deviceString(m));
			for(Option o : m.getOptions()) {
				logger.info("Testing option " + deviceString(o));
				
				// test if the option is found
				assertTrue(isOption(filteredMeasuringStation, o));
				logger.info("Option " + deviceString(o) + " found");
				
				// ***"
				
				// exclude it
				filteredMeasuringStation.exclude(o);
				logger.info("Option " + deviceString(o) + " excluded");
				
				// test its absence
				assertFalse(isOption(filteredMeasuringStation, o));
				logger.info("Option " + deviceString(o) + " not found");
				
				// check if the (parent) motor is still there
				assertTrue(isMotor(filteredMeasuringStation, m));
				
				// ***
				
				// include it
				filteredMeasuringStation.include(o);
				logger.info("Option " + deviceString(o) + " included");
				
				// test its presence
				assertNotNull(filteredMeasuringStation.
						getPrePostscanDeviceById(o.getID()));
				logger.info("Option " + deviceString(o) + " found");
				
				logger.info("***");
			}
			if(m.getOptions().size() == 0)
			{
				logger.info("Motor " + deviceString(m) + " has no options");
			}
			
			logger.info("-----");
		}
		}
		log_end(logger, "testExcludeIncludeMotorOption()");
	}
	
	/**
	 * <code>testExcludeIncludeMotorAxisOption</code> tries to exclude motor 
	 * axes options (each one by one) and verifies their absence. Afterwards 
	 * they are included and presence is checked again.
	 */
	@Test
	public void testExcludeIncludeMotorAxisOption() {
		log_start(logger, "testExcludeIncludeMotorAxisOption()");
		
		for(IMeasuringStation measuringStation : stations) {
			
			log_station(logger, measuringStation);
			
			ExcludeFilter filteredMeasuringStation = new ExcludeFilter();
			filteredMeasuringStation.setSource(measuringStation);
			assertNotNull(filteredMeasuringStation);
		
		for(Motor m : measuringStation.getMotors()) {
			for(MotorAxis ma : m.getAxes()) {
				logger.info("Testing options of motor axis " + 
							deviceString(ma) + " of motor " + 
							deviceString(m));
				for(Option o : ma.getOptions()) {
					logger.info("Testing option " + deviceString(o) + 
								" of motor axis " + deviceString(ma));
					assertTrue(isOption(filteredMeasuringStation, o));
					logger.info("Option " + deviceString(o) + " found");
					
					// ***
					
					filteredMeasuringStation.exclude(o);
					logger.info("Option " + deviceString(o) + " excluded");
					if(filteredMeasuringStation.
							getPrePostscanDeviceById(o.getID()) != null)
						logger.debug(deviceString(filteredMeasuringStation.
								getPrePostscanDeviceById(o.getID())));
					assertTrue(isMotorAxis(filteredMeasuringStation, ma));
					assertTrue(isMotor(filteredMeasuringStation, m));
					
					assertFalse(isOption(filteredMeasuringStation, o));
					logger.info("Option " + deviceString(o) + " not found");
					
					// ***
					
					filteredMeasuringStation.include(o);
					logger.info("Option " + deviceString(o) + " included");
					
					assertTrue(isOption(filteredMeasuringStation, o));
					logger.info("Option " + deviceString(o) + " found");
					
					logger.info("***");
				}
				logger.info("-----");
			}
		}
		}
		log_end(logger, "testExcludeIncludeMotorAxisOption()");
	}
	
	// **********************************************************
	
	/**
	 * <code>testExcludeIncludeDetector</code> tries to exclude detectors (each 
	 * one by one) and verifies their absence. It also checks the presence / 
	 * absence of their channels and options. Afterwards they are included and 
	 * presence is checked again.
	 */
	@Test
	public void testExcludeIncludeDetector() {
		log_start(logger, "testExcludeIncludeDetector()");
		
		for(IMeasuringStation measuringStation : stations) {
			
			log_station(logger, measuringStation);
			
			ExcludeFilter filteredMeasuringStation = new ExcludeFilter();
			filteredMeasuringStation.setSource(measuringStation);
			assertNotNull(filteredMeasuringStation);
		
		for(Detector d : measuringStation.getDetectors()) {
			logger.info("Testing Detector: " + deviceString(d));
			
			// the detector should be found
			assertTrue(isDetector(filteredMeasuringStation, d));
			logger.info("Detector " + deviceString(d) + " found.");
			
			// its options should be found
			for(Option o : d.getOptions()) {
				assertTrue(isOption(filteredMeasuringStation, o));
				logger.info("Detector Option " + deviceString(o) + " found.");
			}
			if(d.getOptions().size() == 0) {
				logger.info("Motor " + deviceString(d) + " has no options.");
			}
						
			// its channels should also be found
			for(DetectorChannel ch : d.getChannels()) {
				assertTrue(isDetectorChannel(filteredMeasuringStation, ch));
				logger.info("DetectorChannel " + deviceString(ch) + " found.");
				
				// channel options should be found as well
				for(Option o : ch.getOptions()) {
					assertTrue(isOption(filteredMeasuringStation, o));
					logger.info("DetectorChannel Option " + deviceString(o) + 
											" found.");
				}
				if(ch.getOptions().size() == 0) {
					logger.info("Motor Axis " + deviceString(ch) + 
							" has no options.");
				}
			}
			
			// ***
			
			// exclude the detector
			filteredMeasuringStation.exclude(d);
			logger.info("Detector " + deviceString(d) + " excluded.");
			
			// now the detector shouldn't be found anymore
			assertFalse(isDetector(filteredMeasuringStation, d));
			logger.info("Detector " + deviceString(d) + " not found.");
			
			// its options shouldn't be found
			for(Option o : d.getOptions()) {
				assertFalse(isOption(filteredMeasuringStation, o));
				logger.info("Detector Option " + deviceString(o) + " not found.");
			}
			if(d.getOptions().size() == 0) {
				logger.info("Motor " + deviceString(d) + " has no options.");
			}
			
			// all channels of the detector also shouldn't be found
			for(DetectorChannel ch : d.getChannels()) {
				logger.info("Checking if channel " + deviceString(ch) + 
							" of the detector is also excluded");
				assertFalse(isDetectorChannel(filteredMeasuringStation, ch));
				logger.info("Detector Channel " + deviceString(ch) + 
							" not found.");
				
				// channel options shouldn't be found as well
				for(Option o : ch.getOptions()) {
					assertFalse(isOption(filteredMeasuringStation, o));
					logger.info("Motor Axis Option " + deviceString(o) + 
								" not found.");
				}
				if(ch.getOptions().size() == 0) {
					logger.info("Motor Axis " + deviceString(ch) + 
							" has no options.");
				}
			}
			
			// ***
			
			// include the detector
			filteredMeasuringStation.include(d);
			logger.info("Detector " + deviceString(d) + " included.");
			
			// now the detector should be found again
			assertTrue(isDetector(filteredMeasuringStation, d));
			logger.info("Detector " + deviceString(d) + " found.");
			
			// its options should be found
			for(Option o : d.getOptions()) {
				assertTrue(isOption(filteredMeasuringStation, o));
				logger.info("Detector Option " + deviceString(o) + " found.");
			}
			if(d.getOptions().size() == 0) {
				logger.info("Motor " + deviceString(d) + " has no options.");
			}
			
			// all channels of the detector also should be back
			for(DetectorChannel ch : d.getChannels()) {
				logger.info("Checking if channel " + deviceString(ch) + 
							" of the detector is also included");
				assertTrue(isDetectorChannel(filteredMeasuringStation, ch));
				logger.info("DetectorChannel " + deviceString(ch) + " found.");
				
				// channel options should be found as well
				for(Option o : ch.getOptions()) {
					assertTrue(isOption(filteredMeasuringStation, o));
					logger.info("DetectorChannel Option " + deviceString(o) + 
											" found.");
				}
				if(ch.getOptions().size() == 0) {
					logger.info("Motor Axis " + deviceString(ch) + 
							" has no options.");
				}
			}
			logger.info("-----");
		}
		}
		log_end(logger, "testExcludeIncludeDetector()");
	}
	
	/**
	 * <code>testExcludeIncludeDetectorChannel</code> tries to exclude 
	 * detector channels (each one by one) and verifies their absence. 
	 * Afterwards they are included and presence is checked again.
	 */
	@Test
	public void testExcludeIncludeDetectorChannel() {
		log_start(logger, "testExcludeIncludeDetectorChannel()");
		
		for(IMeasuringStation measuringStation : stations) {
			
			log_station(logger, measuringStation);
			
			ExcludeFilter filteredMeasuringStation = new ExcludeFilter();
			filteredMeasuringStation.setSource(measuringStation);
			assertNotNull(filteredMeasuringStation);
		
		for(Detector d : measuringStation.getDetectors()) {
			logger.info("Testing channels of detector " + deviceString(d));
			
			for(DetectorChannel ch : d.getChannels()) {
				logger.info("Testing detector channel " + deviceString(ch));
				
				// the detector channel should be found
				assertTrue(isDetectorChannel(filteredMeasuringStation, ch));
				logger.info("Detector Channel " + deviceString(ch) + " found");
				
				// its options should also be found
				for(Option o : ch.getOptions()) {
					assertTrue(isOption(filteredMeasuringStation, o));
					logger.info("Detector Channel Option " + deviceString(o) + " found");
				}
				
				// ***
				
				// exclude it
				filteredMeasuringStation.exclude(ch);
				logger.info("Detector Channel " + deviceString(ch) + " excluded");
				
				// now it shouldn't be found anymore
				assertFalse(isDetectorChannel(filteredMeasuringStation, ch));
				logger.info("Detector Channel " + deviceString(ch) + " not found");
				
				// its options shouldn't be found
				for(Option o : ch.getOptions()) {
					assertFalse(isOption(filteredMeasuringStation, o));
					logger.info("Detector Channel Option " + deviceString(o) + " not found");
				}
				
				// ***"
				
				// include it
				filteredMeasuringStation.include(ch);
				logger.info("Detector Channel " + deviceString(ch) + " included");
				
				// now it should be found again
				assertTrue(isDetectorChannel(filteredMeasuringStation, ch));
				logger.info("Detector Channel " + deviceString(ch) + " found");
				
				// its options should also be found
				for(Option o : ch.getOptions()) {
					assertTrue(isOption(filteredMeasuringStation, o));
					logger.info("Detector Channel Option " + deviceString(o) + " found");
				}
				logger.info("***");
			}
			logger.info("-----");
		}
		}
		log_end(logger, "testExcludeIncludeDetectorChannel()");
	}
	
	/**
	 * <code>testExcludeIncludeDetectorOption</code> tries to exclude detector 
	 * options (each one by one) and verifies their absence.Afterwards they are 
	 * included and presence is checked again.
	 */
	@Test
	public void testExcludeIncludeDetectorOption() {
		for(IMeasuringStation measuringStation : stations) {
			
			log_station(logger, measuringStation);
			
			ExcludeFilter filteredMeasuringStation = new ExcludeFilter();
			filteredMeasuringStation.setSource(measuringStation);
			assertNotNull(filteredMeasuringStation);
		
		for(Detector d : measuringStation.getDetectors()) {
			logger.info("Testing options of detector " + deviceString(d));
			
			for(Option o : d.getOptions()) {
				assertTrue(isOption(filteredMeasuringStation, o));
				logger.info("Option " + deviceString(o) + " found");
				
				// ***"
				
				filteredMeasuringStation.exclude(o);
				logger.info("Option " + deviceString(o) + " excluded");
				
				assertFalse(isOption(filteredMeasuringStation, o));
				logger.info("Option " + deviceString(o) + " not found");
				
				// ***"
				
				filteredMeasuringStation.include(o);
				logger.info("Option " + deviceString(o) + " included");
				
				assertTrue(isOption(filteredMeasuringStation, o));
				logger.info("Option " + deviceString(o) + " found");
				
				logger.info("***");
			}
			if(d.getOptions().size() == 0) {
				logger.info("Detector " + deviceString(d) + " has no options");
			}
			
			logger.info("-----");
		}
		}
	}
	
	/**
	 * <code>testExcludeIncludeDetectorChannelOption</code> tries to exclude 
	 * detector channel options (each one by one) and verifies their absence. 
	 * Afterwards they are included and presence is checked again.
	 */
	@Test
	public void testExcludeIncludeDetectorChannelOption() {
		log_start(logger, "testExcludeIncludeDetectorChannelOption");
		
		for(IMeasuringStation measuringStation : stations) {
			
			log_station(logger, measuringStation);
			
			ExcludeFilter filteredMeasuringStation = new ExcludeFilter();
			filteredMeasuringStation.setSource(measuringStation);
			assertNotNull(filteredMeasuringStation);
		
		for(Detector d : measuringStation.getDetectors()) {
			for(DetectorChannel ch : d.getChannels()) {
				logger.info("Testing options of detector channel " + deviceString(ch));
				
				for(Option o : ch.getOptions()) {
					assertTrue(isOption(filteredMeasuringStation, o));
					logger.info("Option " + deviceString(o) + " found");
					
					// ***
					
					filteredMeasuringStation.exclude(o);
					logger.info("Option " + deviceString(o) + " excluded");
					
					assertFalse(isOption(filteredMeasuringStation, o));
					logger.info("Option " + deviceString(o) + " not found");
					
					// ***
					
					filteredMeasuringStation.include(o);
					logger.info("Option " + deviceString(o) + " included");
					
					assertTrue(isOption(filteredMeasuringStation, o));
					logger.info("Option " + deviceString(o) + " found");
					
					logger.info("***");
				}
				logger.info("-----");
			}
		}
		}
		log_end(logger, "testExcludeIncludeDetectorChannelOption");
	}
	
	/**
	 * <code>testExcludeIncludeDevice</code> tries to exclude devices 
	 * (each one by one) and verifies their absence (options of the devices 
	 * are also checked). Afterwards they are included and presence is checked 
	 * again.
	 */
	@Test
	public void testExcludeIncludeDevice() {
		log_start(logger, "testExcludeIncludeDevice()");
		
		for(IMeasuringStation measuringStation : stations) {
			
			log_station(logger, measuringStation);
			
			ExcludeFilter filteredMeasuringStation = new ExcludeFilter();
			filteredMeasuringStation.setSource(measuringStation);
			assertNotNull(filteredMeasuringStation);
		
		for(Device dev : measuringStation.getDevices()) {
			logger.info("Testing device " + deviceString(dev));
			
			// the device should be found
			assertTrue(isDevice(filteredMeasuringStation, dev));
			logger.info("Device " + deviceString(dev) + " found");
			
			// its options should be found
			for(Option o : dev.getOptions()) {
				assertTrue(isOption(filteredMeasuringStation, o));
				logger.info("Device Option " + deviceString(o) + " found");
			}
			
			// ***
			
			// exclude it
			filteredMeasuringStation.exclude(dev);
			logger.info("Device " + deviceString(dev) + " excluded");
			
			// now it shouldn't be found anymore
			assertFalse(isDevice(filteredMeasuringStation, dev));
			logger.info("Device " + deviceString(dev) + " not found");
			
			// its options should be found
			for(Option o : dev.getOptions()) {
				assertFalse(isOption(filteredMeasuringStation, o));
				logger.info("Device Option " + deviceString(o) + " not found");
			}
			
			// ***
			
			// include it
			filteredMeasuringStation.include(dev);
			logger.info("Device " + deviceString(dev) + " included");
			
			// now it should be found again
			assertTrue(isDevice(filteredMeasuringStation, dev));
			logger.info("Device " + deviceString(dev) + " found");
			
			// its options should be found
			for(Option o : dev.getOptions()) {
				assertTrue(isOption(filteredMeasuringStation, o));
				logger.info("Device Option " + deviceString(o) + " found");
			}
			
			logger.info("-----");
		}
		}
		log_end(logger, "testExcludeIncludeDevice()");
	}
	
	/**
	 * <code>testExcludeIncludeDeviceOption</code> tries to exclude 
	 * device options (each one by one) and verifies their absence. Afterwards 
	 * they are included and presence is checked again.
	 */
	@Test
	public void testExcludeIncludeDeviceOption() {
		log_start(logger, "testExcludeIncludeDeviceOption()");
		
		for(IMeasuringStation measuringStation : stations) {
			
			log_station(logger, measuringStation);
			
			ExcludeFilter filteredMeasuringStation = new ExcludeFilter();
			filteredMeasuringStation.setSource(measuringStation);
			assertNotNull(filteredMeasuringStation);
		
		for(Device dev : measuringStation.getDevices()) {
			logger.info("Testing options of device " + deviceString(dev));
			
			for(Option o : dev.getOptions()) {
				assertTrue(isOption(filteredMeasuringStation, o));
				logger.info("Option " + deviceString(o) + " found");
				
				filteredMeasuringStation.exclude(o);
				assertFalse(isOption(filteredMeasuringStation, o));
				logger.info("Option " + deviceString(o) + " not found");
				
				filteredMeasuringStation.include(o);
				assertTrue(isOption(filteredMeasuringStation, o));
				logger.info("Option " + deviceString(o) + " found");
				
				logger.info("***");
			}
			if(dev.getOptions().size() == 0)
				logger.info("Device " + deviceString(dev) + " has no options.");
			
			logger.info("-----");
		}
		}
		log_end(logger, "testExcludeIncludeDeviceOption()");
	}
	
	/**
	 * Tests whether a device is being filtered (should not) if it is (only) 
	 * used as an event (Pause/Redo/Break/Stop/Trigger) in a chain/scan module 
	 * or detector.
	 */
	@Test
	public void testDevicePresenceIfEventPresent() {
		log_start(logger, "testDevicePresenceIfEventPresent()");
		
		for(IMeasuringStation measuringStation : stations) {
			assertNotNull(measuringStation);
			
			log_station(logger, measuringStation);
			
			// event in pause event manager of chain
			for(Event e : measuringStation.getEvents()) {
				logger.debug("Testing chain event " + e.getMonitor().getName());
				
				ExcludeFilter filteredMeasuringStation = new ExcludeFilter();
				filteredMeasuringStation.setSource(measuringStation);
				assertNotNull(filteredMeasuringStation);
				
				ScanDescription sd = Configurator.getBasicScanDescription(
						measuringStation);
				
				sd.getChain(1).getPauseControlEventManager().addControlEvent(
						new PauseEvent(EventTypes.MONITOR, e, 
								e.getMonitor().getID()));
				
				filteredMeasuringStation.excludeUnusedDevices(sd);
				
				assertNotNull(filteredMeasuringStation.
						getAbstractDeviceById(e.getID()));
				
				logger.debug("Device " + filteredMeasuringStation.
						getAbstractDeviceById(e.getID()).getID() + 
						" is not excluded.");
			}
			
			// event in redo event manager of chain
			for(Event e : measuringStation.getEvents()) {
				logger.debug("Testing chain event " + e.getMonitor().getName());
				
				ExcludeFilter filteredMeasuringStation = new ExcludeFilter();
				filteredMeasuringStation.setSource(measuringStation);
				assertNotNull(filteredMeasuringStation);
				
				ScanDescription sd = Configurator.getBasicScanDescription(
						measuringStation);
				
				sd.getChain(1).getRedoControlEventManager().addControlEvent(
						new ControlEvent(EventTypes.MONITOR, e, 
								e.getMonitor().getID()));
				
				filteredMeasuringStation.excludeUnusedDevices(sd);
				
				assertNotNull(filteredMeasuringStation.
						getAbstractDeviceById(e.getID()));
				
				logger.debug("Device " + filteredMeasuringStation.
						getAbstractDeviceById(e.getID()).getID() + 
						" is not excluded.");
			}
			
			// event in break event manager of chain
			for(Event e : measuringStation.getEvents()) {
				logger.debug("Testing chain event " + e.getMonitor().getName());
				
				ExcludeFilter filteredMeasuringStation = new ExcludeFilter();
				filteredMeasuringStation.setSource(measuringStation);
				assertNotNull(filteredMeasuringStation);
				
				ScanDescription sd = Configurator.getBasicScanDescription(
						measuringStation);
				
				sd.getChain(1).getBreakControlEventManager().addControlEvent(
						new ControlEvent(EventTypes.MONITOR, e, 
								e.getMonitor().getID()));
				
				filteredMeasuringStation.excludeUnusedDevices(sd);
				
				assertNotNull(filteredMeasuringStation.
						getAbstractDeviceById(e.getID()));
				
				logger.debug("Device " + filteredMeasuringStation.
						getAbstractDeviceById(e.getID()).getID() + 
						" is not excluded.");
			}
			
			// event in stopk event manager of chain
			for(Event e : measuringStation.getEvents()) {
				logger.debug("Testing chain event " + e.getMonitor().getName());
				
				ExcludeFilter filteredMeasuringStation = new ExcludeFilter();
				filteredMeasuringStation.setSource(measuringStation);
				assertNotNull(filteredMeasuringStation);
				
				ScanDescription sd = Configurator.getBasicScanDescription(
						measuringStation);
				
				sd.getChain(1).getStopControlEventManager().addControlEvent(
						new ControlEvent(EventTypes.MONITOR, e, 
								e.getMonitor().getID()));
				
				filteredMeasuringStation.excludeUnusedDevices(sd);
				
				assertNotNull(filteredMeasuringStation.
						getAbstractDeviceById(e.getID()));
				
				logger.debug("Device " + filteredMeasuringStation.
						getAbstractDeviceById(e.getID()).getID() + 
						" is not excluded.");
			}
			
			// event in pause event manager of scan module
			for(Event e : measuringStation.getEvents()) {
				logger.debug("Testing sm event " + e.getMonitor().getName());
				
				ExcludeFilter filteredMeasuringStation = new ExcludeFilter();
				filteredMeasuringStation.setSource(measuringStation);
				assertNotNull(filteredMeasuringStation);
				
				ScanDescription sd = Configurator.getBasicScanDescription(
						measuringStation);
				
				sd.getChain(1).getScanModulById(1).getPauseControlEventManager().
						addControlEvent(new PauseEvent(EventTypes.MONITOR, e, 
								e.getMonitor().getID()));
				
				filteredMeasuringStation.excludeUnusedDevices(sd);
				
				assertNotNull(filteredMeasuringStation.
						getAbstractDeviceById(e.getID()));
				
				logger.debug("Device " + filteredMeasuringStation.
						getAbstractDeviceById(e.getID()).getID() + 
						" is not excluded.");
			}
			
			// event in redo event manager of scan module
			for(Event e : measuringStation.getEvents()) {
				logger.debug("Testing sm event " + e.getMonitor().getName());
				
				ExcludeFilter filteredMeasuringStation = new ExcludeFilter();
				filteredMeasuringStation.setSource(measuringStation);
				assertNotNull(filteredMeasuringStation);
				
				ScanDescription sd = Configurator.getBasicScanDescription(
						measuringStation);
				
				sd.getChain(1).getScanModulById(1).getRedoControlEventManager().
						addControlEvent(new ControlEvent(EventTypes.MONITOR, e, 
								e.getMonitor().getID()));
				
				filteredMeasuringStation.excludeUnusedDevices(sd);
				
				assertNotNull(filteredMeasuringStation.
						getAbstractDeviceById(e.getID()));
				
				logger.debug("Device " + filteredMeasuringStation.
						getAbstractDeviceById(e.getID()).getID() + 
						" is not excluded.");
			}
			
			// event in break event manager of scan module
			for(Event e : measuringStation.getEvents()) {
				logger.debug("Testing sm event " + e.getMonitor().getName());
				
				ExcludeFilter filteredMeasuringStation = new ExcludeFilter();
				filteredMeasuringStation.setSource(measuringStation);
				assertNotNull(filteredMeasuringStation);
				
				ScanDescription sd = Configurator.getBasicScanDescription(
						measuringStation);
				
				sd.getChain(1).getScanModulById(1).getBreakControlEventManager().
						addControlEvent(new ControlEvent(EventTypes.MONITOR, e, 
								e.getMonitor().getID()));
				
				filteredMeasuringStation.excludeUnusedDevices(sd);
				
				assertNotNull(filteredMeasuringStation.
						getAbstractDeviceById(e.getID()));
				
				logger.debug("Device " + filteredMeasuringStation.
						getAbstractDeviceById(e.getID()).getID() + 
						" is not excluded.");
			}
			
			// event in trigger event manager of scan module
			for(Event e : measuringStation.getEvents()) {
				logger.debug("Testing sm event " + e.getMonitor().getName());
				
				ExcludeFilter filteredMeasuringStation = new ExcludeFilter();
				filteredMeasuringStation.setSource(measuringStation);
				assertNotNull(filteredMeasuringStation);
				
				ScanDescription sd = Configurator.getBasicScanDescription(
						measuringStation);
				
				sd.getChain(1).getScanModulById(1).getTriggerControlEventManager().
						addControlEvent(new ControlEvent(EventTypes.MONITOR, e, 
								e.getMonitor().getID()));
				
				filteredMeasuringStation.excludeUnusedDevices(sd);
				
				assertNotNull(filteredMeasuringStation.
						getAbstractDeviceById(e.getID()));
				
				logger.debug("Device " + filteredMeasuringStation.
						getAbstractDeviceById(e.getID()).getID() + 
						" is not excluded.");
			}
			
			// event in redo event manager of detector
			for(Event e : measuringStation.getEvents()) {
				logger.debug("Testing detector event " + e.getMonitor().getName());
				
				ExcludeFilter filteredMeasuringStation = new ExcludeFilter();
				filteredMeasuringStation.setSource(measuringStation);
				assertNotNull(filteredMeasuringStation);
				
				ScanDescription sd = Configurator.getBasicScanDescription(
						measuringStation);
				
				Detector d = new Detector();
				d.setId("Det1");
				DetectorChannel ch = new DetectorChannel();
				ch.setId("DetCh1");
				d.add(ch);
				
				Channel chan = new Channel(sd.getChain(1).getScanModulById(1));
				chan.setDetectorChannel(ch);
				sd.getChain(1).getScanModulById(1).add(chan);
				
				chan.getRedoControlEventManager().addControlEvent(
						new ControlEvent(EventTypes.MONITOR, e, 
								e.getMonitor().getID()));
				
				filteredMeasuringStation.excludeUnusedDevices(sd);
				
				assertNotNull(filteredMeasuringStation.
						getAbstractDeviceById(e.getID()));
				
				logger.debug("Device " + filteredMeasuringStation.
						getAbstractDeviceById(e.getID()).getID() + 
						" is not excluded.");
			}
		}
		log_end(logger, "testDevicePresenceIfEventPresent()");
	}
	
	// *********************************************************************
	
	/**
	 * <code>testEqualityMotorListMap</code> checks whether each motor axis 
	 * contained in the motor list is also available via 
	 * {@link de.ptb.epics.eve.data.measuringstation.filter.ExcludeFilter#getMotorAxisById(String)}.
	 */
	@Test
	public void testEqualityMotorListMap() {
		log_start(logger, "testEqualityMotorListMap");
		
		for(IMeasuringStation measuringStation : stations) {
			
			log_station(logger, measuringStation);
			
			ExcludeFilter filteredMeasuringStation = new ExcludeFilter();
			filteredMeasuringStation.setSource(measuringStation);
			assertNotNull(filteredMeasuringStation);
		
		filteredMeasuringStation.updateEvent(null);
		
		for(Motor m : filteredMeasuringStation.getMotors()) {
			for(MotorAxis ma : m.getAxes()) {
				logger.info("Testing motor axis " + deviceString(ma) + " (of the list)");
				assertNotNull(filteredMeasuringStation.getMotorAxisById(ma.getID()));
				logger.info("Motor Axis " + deviceString(ma) + " found in Map");
			}
		}
		}
		log_end(logger, "testEqualityMotorListMap");
	}
	
	// ****************************************************************
	
	/**
	 * Test for {@link de.ptb.epics.eve.data.measuringstation.filter.ExcludeFilter#excludeUnusedDevices(ScanDescription)}.
	 * Tests if the devices used in the scan description (obtained by 
	 * {@link de.ptb.epics.eve.data.tests.internal.Configurator#getScanDescription()}) 
	 * are available after executing 
	 * {@link de.ptb.epics.eve.data.measuringstation.filter.ExcludeFilter#excludeUnusedDevices(ScanDescription)}. 
	 * Does NOT test (for now) whether unused devices aren't available.
	 */
	@Ignore("has to be updated")
	@Test
	public void testExcludeUnusedDevices() {
		log_start(logger, "testExcludeUnusedDevices()");
		
		/*
		for(Pair<IMeasuringStation, List<ScanDescription>> p : 
				Configurator.getScanDescriptions()) {
			
		}
		
		ScanDescription sd = Configurator.getScanDescription();
		assertNotNull(sd);
		
		IMeasuringStation measuringStation = Configurator.getMeasuringStation();
		ExcludeFilter filteredMeasuringStation = new ExcludeFilter();
		filteredMeasuringStation.setSource(measuringStation);
		
		filteredMeasuringStation.excludeUnusedDevices(sd);
		logger.info("excluded unused devices (unused = not in the scan description)");
		*/
		
		log_end(logger, "testExcludeUnunsedDevices()");
	}
	
	/**
	 * Takes a device from the measuring station and checks it for equality with 
	 * its counterpart got by 
	 * {@link de.ptb.epics.eve.data.measuringstation.filter.ExcludeFilter#getAbstractDeviceById(String)}.
	 */
	@Test
	public void testGetAbstractDeviceById() {
		log_start(logger, "testGetAbstractDeviceById()");
		
		for(IMeasuringStation measuringStation : stations) {
			log_station(logger, measuringStation);
			
			ExcludeFilter filteredMeasuringStation = new ExcludeFilter();
			filteredMeasuringStation.setSource(measuringStation);
			assertNotNull(filteredMeasuringStation);
			
			for(Motor m : measuringStation.getMotors()) {
				AbstractDevice motorFromFilter = filteredMeasuringStation.
						getAbstractDeviceById(m.getID());
				logger.debug("Testing equality of " + m.getName());
				assertEquals(m, motorFromFilter);
				logger.debug(m.getName() + " and " + motorFromFilter.getName() + 
							" are equal.");
				
				for(Option o : m.getOptions()) {
					AbstractDevice optionFromFilter = filteredMeasuringStation.
							getAbstractDeviceById(o.getID());
					logger.debug("Testing equality of " + o.getName());
					assertEquals(o, optionFromFilter);
					logger.debug(o.getName() + " and " + 
								optionFromFilter.getName() + " are equal.");
				}
				
				for(MotorAxis ma : m.getAxes()) {
					AbstractDevice axisFromFilter = filteredMeasuringStation.
							getAbstractDeviceById(ma.getID());
					logger.debug("Testing equality of " + ma.getName());
					assertEquals(ma, axisFromFilter);
					logger.debug(ma.getName() + " and " + 
								axisFromFilter.getName() + " are equal.");
					
					for(Option o : ma.getOptions()) {
						AbstractDevice optionFromFilter = filteredMeasuringStation.
								getAbstractDeviceById(o.getID());
						logger.debug("Testing equality of " + o.getName());
						assertEquals(o, optionFromFilter);
						logger.debug(o.getName() + " and " + 
									optionFromFilter.getName() + " are equal.");
					}
				}
			}
			logger.debug("*****");
			
			for(Detector d : measuringStation.getDetectors()) {
				AbstractDevice detectorFromFilter = filteredMeasuringStation.
						getAbstractDeviceById(d.getID());
				logger.debug("Testing equality of " + d.getName());
				assertEquals(d, detectorFromFilter);
				logger.debug(d.getName() + " and " + 
						detectorFromFilter.getName() + " are equal.");
				
				for(Option o : d.getOptions()) {
					AbstractDevice optionFromFilter = filteredMeasuringStation.
							getAbstractDeviceById(o.getID());
					logger.debug("Testing equality of " + o.getName());
					assertEquals(o, optionFromFilter);
					logger.debug(o.getName() + " and " + 
							optionFromFilter.getName() + " are equal.");
				}
				
				for(DetectorChannel ch : d.getChannels()) {
					AbstractDevice channelFromFilter = filteredMeasuringStation.
							getAbstractDeviceById(ch.getID());
					logger.debug("Testing equality of " + ch.getName());
					assertEquals(ch, channelFromFilter);
					logger.debug(ch.getName() + " and " + 
							channelFromFilter.getName() + " are equal.");
					
					for(Option o : ch.getOptions()) {
						AbstractDevice optionFromFilter = filteredMeasuringStation.
								getAbstractDeviceById(o.getID());
						logger.debug("Testing equality of " + o.getName());
						assertEquals(o, optionFromFilter);
						logger.debug(o.getName() + " and " + 
								optionFromFilter.getName() + " are equal.");
					}
				}
			}
			logger.debug("*****");
			
			for(Device dev : measuringStation.getDevices()) {
				AbstractDevice deviceFromFilter = filteredMeasuringStation.
						getAbstractDeviceById(dev.getID());
				logger.debug("Testing equality of " + dev.getName());
				assertEquals(dev, deviceFromFilter);
				logger.debug(dev.getName() + " and " + 
						deviceFromFilter.getName() + " are equal.");
				
				for(Option o : dev.getOptions()) {
					AbstractDevice optionFromFilter = filteredMeasuringStation.
							getAbstractDeviceById(o.getID());
					logger.debug("Testing equality of " + o.getName());
					assertEquals(o, optionFromFilter);
					logger.debug(o.getName() + " and " + 
							optionFromFilter.getName() + " are equal.");
				}
			}
		}
		log_end(logger, "testGetAbstractDeviceById()");
	}
	
	// ****************************************************************
	
	/*
	 * 
	 */
	private boolean isMotor(IMeasuringStation measuringstation, Motor m) {
		Motor motor = (Motor) measuringstation.
				getAbstractDeviceByFullIdentifyer(m.getFullIdentifyer());
		return motor != null;
	}
	
	/*
	 * 
	 */
	private boolean isMotorAxis(IMeasuringStation measuringstation, 
								MotorAxis ma) {
		MotorAxis motoraxis = measuringstation.getMotorAxisById(ma.getID());
		return motoraxis != null;
	}
	
	/*
	 * 
	 */
	private boolean isDetector(IMeasuringStation measuringstation, Detector d) {
		Detector detector = (Detector) measuringstation.
				getAbstractDeviceByFullIdentifyer(d.getFullIdentifyer());
		return detector != null;
	}
	
	/*
	 * 
	 */
	private boolean isDetectorChannel(IMeasuringStation measuringstation, 
										DetectorChannel ch) {
		DetectorChannel channel = measuringstation.
				getDetectorChannelById(ch.getID());
		return channel != null;
	}
	
	/*
	 * 
	 */
	private boolean isDevice(IMeasuringStation measuringstation, 
								Device dev) {
		Device device = (Device) measuringstation.
				getAbstractDeviceByFullIdentifyer(dev.getFullIdentifyer());
		return device != null;
	}
	
	/*
	 * 
	 */
	private boolean isOption(IMeasuringStation measuringstation, Option o) {
		Option option = (Option) 
				measuringstation.getPrePostscanDeviceById(o.getID());
		return option != null;
	}

	// ***********************************************************************
	// ***********************************************************************
	// ***********************************************************************
	
	/**
	 * Initializes logging and loads the measuring station (Class wide setup 
	 * method of the test).
	 */
	@BeforeClass
	public static void beforeClass() {
		
		Configurator.configureLogging();
		
		((RollingFileAppender)logger.
				getAppender("ExcludeFilterTestAppender")).rollOver();

		stations = Configurator.getMeasuringStations();
		for(IMeasuringStation ims : stations) {
			assertNotNull(ims);
		}
		classSetUp(logger);
	}
	
	/**
	 * Test wide set up method.
	 */
	@Before
	public void beforeTest() {
		testSetUp(logger);
	}
	
	/**
	 * Test wide tear down method.
	 */
	@After
	public void afterTest() {
		testTearDown(logger);
	}
	
	/**
	 * Class wide tear down method.
	 */
	@AfterClass
	public static void afterClass() {
		classTearDown(logger);
	}
}