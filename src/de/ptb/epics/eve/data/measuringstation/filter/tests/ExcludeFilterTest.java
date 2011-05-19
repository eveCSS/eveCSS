package de.ptb.epics.eve.data.measuringstation.filter.tests;

import static org.junit.Assert.*;

import org.apache.log4j.Logger;
import org.apache.log4j.RollingFileAppender;
import org.junit.*;

import de.ptb.epics.eve.data.measuringstation.AbstractDevice;
import de.ptb.epics.eve.data.measuringstation.Detector;
import de.ptb.epics.eve.data.measuringstation.DetectorChannel;
import de.ptb.epics.eve.data.measuringstation.IMeasuringStation;
import de.ptb.epics.eve.data.measuringstation.Motor;
import de.ptb.epics.eve.data.measuringstation.MotorAxis;
import de.ptb.epics.eve.data.measuringstation.Option;
import de.ptb.epics.eve.data.measuringstation.filter.ExcludeFilter;
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
	
	// the "real"/source measuring station
	private static IMeasuringStation measuringStation;
	
	// the exclude filter
	private ExcludeFilter filteredMeasuringStation;
	
	/**
	 * <code>testExcludeIncludeMotor</code> tries to exclude motors (each one 
	 * by one) and checks the getter methods for its absence. It also checks 
	 * the presence / absence of their axis and options. Afterwards they are 
	 * included and presence is checked again.
	 */
	@Test
	public void testExcludeIncludeMotor()
	{
		log_start("testExcludeIncludeMotor()");
		
		for(Motor m : measuringStation.getMotors())
		{
			logger.info("Testing Motor: " + deviceString(m));
			
			// the motor should be found
			assertTrue(isMotor(filteredMeasuringStation, m));
			logger.info("Motor " + deviceString(m) + " found.");
			
			// its options should be found
			for(Option o : m.getOptions())
			{
				assertTrue(isOption(filteredMeasuringStation, o));
				logger.info("Motor Option " + deviceString(o) + " found.");
			}
			if(m.getOptions().size() == 0)
				logger.info("Motor " + deviceString(m) + " has no options.");
			
			// its axis should also be found
			for(MotorAxis ma : m.getAxes())
			{
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
			
			logger.info("***");
			
			// exclude the motor
			filteredMeasuringStation.exclude(m);
			logger.info("Motor " + deviceString(m) + " excluded.");
			
			// now the motor shouldn't be found anymore
			assertFalse(isMotor(filteredMeasuringStation, m));
			logger.info("Motor " + deviceString(m) + " not found.");
			
			// its options shouldn't be found
			for(Option o : m.getOptions())
			{
				assertFalse(isOption(filteredMeasuringStation, o));
				logger.info("Motor Option " + deviceString(o) + " not found.");
			}
			if(m.getOptions().size() == 0)
				logger.info("Motor " + deviceString(m) + " has no options.");
			
			// all axes of the motor also shouldn't be found
			for(MotorAxis ma : m.getAxes())
			{
				logger.info("Checking if axis " + deviceString(ma) + 
							" of the motor is also excluded");
				assertFalse(isMotorAxis(filteredMeasuringStation, ma));
				logger.info("Motor Axis " + deviceString(ma) + " not found.");
				
				// axis options shouldn't be found as well
				for(Option o : ma.getOptions())
				{
					assertFalse(isOption(filteredMeasuringStation, o));
					logger.info("Motor Axis Option " + deviceString(o) + 
								" not found.");
				}
				if(ma.getOptions().size() == 0)
					logger.info("Motor Axis " + deviceString(ma) + 
								" has no options.");
			}
			
			logger.info("***");
			
			// include the motor
			filteredMeasuringStation.include(m);
			logger.info("Motor " + deviceString(m) + " included.");
			
			// now the motor should be found again
			assertTrue(isMotor(filteredMeasuringStation, m));
			logger.info("Motor " + deviceString(m) + " found.");
			
			// its options should be found
			for(Option o : m.getOptions())
			{
				assertTrue(isOption(filteredMeasuringStation, o));
				logger.info("Motor Option " + deviceString(o) + " found.");
			}
			if(m.getOptions().size() == 0)
				logger.info("Motor " + deviceString(m) + " has no options.");
			
			// all axes of the motor also should be back
			for(MotorAxis ma : m.getAxes())
			{
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
		log_end("testExcludeIncludeMotor()");
	}
	
	/**
	 * <code>testExcludeIncludeMotorAxis</code> tries to exclude motor axis 
	 * (each one by one) and checks the getter methods for its absence. It also 
	 * checks the presence / absence of their options.Afterwards they are 
	 * included and presence is checked again.
	 */
	@Test
	public void testExcludeIncludeMotorAxis()
	{
		log_start("testExcludeIncludeMotorAxis()");
		
		for(Motor m : measuringStation.getMotors())
		{
			logger.info("Testing axes of motor " + deviceString(m));
			assertTrue(isMotor(filteredMeasuringStation, m));
			
			for(MotorAxis ma : m.getAxes())
			{
				logger.info("Testing motor axis " + deviceString(ma));
				
				// the motor axis should be found
				assertTrue(isMotorAxis(filteredMeasuringStation, ma));
				logger.info("Motor axis " + deviceString(ma) + " found");
				
				// axis options should be found as well
				for(Option o : ma.getOptions())
				{
					assertTrue(isOption(filteredMeasuringStation, o));
					logger.info("Motor Axis Option " + deviceString(o) + 
								" found.");
				}
				
				logger.info("***");
				
				// exclude it
				filteredMeasuringStation.exclude(ma);
				logger.info("Motor axis " + deviceString(ma) + " excluded.");
				
				// now it shouldn't be found anymore
				assertFalse(isMotorAxis(filteredMeasuringStation, ma));
				logger.info("Motor axis " + deviceString(ma) + " not found");
				
				// axis options shouldn't be found as well
				for(Option o : ma.getOptions())
				{
					assertFalse(isOption(filteredMeasuringStation, o));
					logger.info("Motor Axis Option " + deviceString(o) + 
								" not found.");
				}
				
				assertTrue(isMotor(filteredMeasuringStation, m));
				logger.info("Motor " + deviceString(m) + " found");
				
				logger.info("***");
				
				// include it
				filteredMeasuringStation.include(ma);
				logger.info("Motor axis " + deviceString(ma) + " included.");
				
				// now it should be found again
				assertNotNull(filteredMeasuringStation.
								getMotorAxisById(ma.getID()));
				logger.info("Motor axis " + deviceString(ma) + " found");
				
				// axis options should be found as well
				for(Option o : ma.getOptions())
				{
					assertTrue(isOption(filteredMeasuringStation, o));
					logger.info("Motor Axis Option " + deviceString(o) + 
								" found.");
				}
			}
			logger.info("-----");
		}
		log_end("testExcludeIncludeMotorAxis()");
	}
	
	/**
	 * <code>testExcludeIncludeMotorOption</code> tries to exclude motor options 
	 * (each one by one) and checks the getter methods for its absence. 
	 * Afterwards they are included and presence is checked again.
	 */
	@Test
	public void testExcludeIncludeMotorOption()
	{
		log_start("testExcludeIncludeMotorOption()");
		
		for(Motor m : measuringStation.getMotors())
		{
			logger.info("Testing options of motor " + deviceString(m));
			for(Option o : m.getOptions())
			{
				logger.info("Testing option " + deviceString(o));
				
				// test if the option is found
				assertTrue(isOption(filteredMeasuringStation, o));
				logger.info("Option " + deviceString(o) + " found");
				
				// exclude it
				filteredMeasuringStation.exclude(o);
				logger.info("Option " + deviceString(o) + " excluded");
				
				// test its absence
				assertFalse(isOption(filteredMeasuringStation, o));
				logger.info("Option " + deviceString(o) + " not found");
				
				// check if the (parent) motor is still there
				assertTrue(isMotor(filteredMeasuringStation, m));
				
				// include it
				filteredMeasuringStation.include(o);
				logger.info("Option " + deviceString(o) + " included");
				
				// test its presence
				assertNotNull(filteredMeasuringStation.
						getPrePostscanDeviceById(o.getID()));
				logger.info("Option " + deviceString(o) + " found");
			}
		}	
		log_end("testExcludeIncludeMotorOption()");
	}
	
	/**
	 * <code>testExcludeIncludeMotorAxisOption</code> tries to exclude motor 
	 * axes options (each one by one) and checks the getter methods for its 
	 * absence. Afterwards they are included and presence is checked again.
	 */
	@Test
	public void testExcludeIncludeMotorAxisOption()
	{
		log_start("testExcludeIncludeMotorAxisOption()");
		for(Motor m : measuringStation.getMotors())
		{
			for(MotorAxis ma : m.getAxes())
			{
				logger.info("Testing options of motor axis " + 
							deviceString(ma) + " of motor " + 
							deviceString(m));
				for(Option o : ma.getOptions())
				{
					logger.info("Testing option " + deviceString(o) + 
								" of motor axis " + deviceString(ma));
					assertTrue(isOption(filteredMeasuringStation, o));
					logger.info("Option " + deviceString(o) + " found");
					
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
					
					filteredMeasuringStation.include(o);
					logger.info("Option " + deviceString(o) + " included");
					
					assertTrue(isOption(filteredMeasuringStation, o));
					logger.info("Option " + deviceString(o) + " found");
				}
			}
		}
		log_end("testExcludeIncludeMotorAxisOption()");
	}
	
	// **********************************************************
	
	/**
	 * <code>testExcludeIncludeDetector</code> tries to exclude detectors (each 
	 * one by one) and checks the getter methods for its absence. It also checks 
	 * the presence / absence of their channels and options.Afterwards they are 
	 * included and presence is checked again.
	 */
	@Test
	public void testExcludeIncludeDetector()
	{
		log_start("testExcludeIncludeDetector()");
		
		for(Detector d : measuringStation.getDetectors())
		{
			logger.info("Testing Detector: " + d.getName() + 
					" (" + d.getID() + ")");
			
			// the motor should be found
			assertNotNull(filteredMeasuringStation.
					getAbstractDeviceByFullIdentifyer(d.getFullIdentifyer()));
			logger.info("Detector " + d.getName() + " (" + 
						d.getID() + ") found.");
			
			// exclude the detector
			filteredMeasuringStation.exclude(d);
			logger.info("Detector " + d.getName() + " (" + 
						d.getID() + ") excluded.");
			
			// now the detector shouldn't be found anymore
			assertNull(filteredMeasuringStation.
					getAbstractDeviceByFullIdentifyer(d.getFullIdentifyer()));
			logger.info("Detector " + d.getName() + " (" + 
						d.getID() + ") not found.");
			
			// all channels of the detector also shouldn't be found
			for(DetectorChannel ch : d.getChannels())
			{
				logger.info("Checking if channel " + ch.getName() + " (" + 
							ch.getID() + ") of the detector is also excluded");
				assertNull(filteredMeasuringStation.
						getDetectorChannelById(ch.getID()));
				logger.info("Detector Channel " + ch.getName() + " (" + 
							ch.getID() + ") not found.");
			}
			
			// include the detector
			filteredMeasuringStation.include(d);
			logger.info("Detector " + d.getName() + " (" + 
						d.getID() + ") included.");
			
			// now the detector should be found again
			assertNotNull(filteredMeasuringStation.
					getAbstractDeviceByFullIdentifyer(d.getFullIdentifyer()));
			logger.info("Detector " + d.getName() + " (" + 
						d.getID() + ") found.");

			// all channels of the detector also should be back
			for(DetectorChannel ch : d.getChannels())
			{
				logger.info("Checking if channel " + ch.getName() + " (" + 
							ch.getID() + ") of the detector is also included");
				assertNotNull(filteredMeasuringStation.
						getDetectorChannelById(ch.getID()));
				logger.info("DetectorChannel " + ch.getName() + " (" + 
							ch.getID() + ") found.");
			}
			logger.info("-----");
		}
		
		log_end("testExcludeIncludeDetector()");
	}
	
	/**
	 * <code>testExcludeIncludeDetectorChannel</code> tries to exclude 
	 * detectors (each one by one) and checks the getter methods for its 
	 * absence. Afterwards they are included and presence is checked again.
	 */
	@Test
	public void testExcludeIncludeDetectorChannel()
	{
		log_start("testExcludeIncludeDetectorChannel()");
		
		for(Detector d : measuringStation.getDetectors())
		{
			logger.info("Testing channels of detector " + d.getName() + 
						" (" + d.getID() + ")");
			
			for(DetectorChannel ch : d.getChannels())
			{
				logger.info("Testing detector channel " + ch.getName() + " (" + 
							ch.getID() + ")");
				
				// the detector channel should be found
				assertNotNull(filteredMeasuringStation.
								getDetectorChannelById(ch.getID()));
				logger.info("DetectorChannel " + ch.getName() + " (" + 
							ch.getID() + ") found");
				
				// exclude it
				filteredMeasuringStation.exclude(ch);
				logger.info("Detector Channel " + ch.getName() + " (" + 
							ch.getID() + ") excluded.");
				
				// now it shouldn't be found anymore
				assertNull(filteredMeasuringStation.
								getDetectorChannelById(ch.getID()));
				logger.info("Detector Channel " + ch.getName() + " (" + 
							ch.getID() + ") not found");
				
				// include it
				filteredMeasuringStation.include(ch);
				logger.info("Detector Channel " + ch.getName() + " (" + 
						ch.getID() + ") included.");
				
				// now it should be found again
				assertNotNull(filteredMeasuringStation.
								getDetectorChannelById(ch.getID()));
				logger.info("Detector Channel " + ch.getName() + " (" + 
						ch.getID() + ") found");
			}
			logger.info("-----");
		}
		log_end("testExcludeIncludeDetectorChannel()");
	}
	
	// *********************************************************************
	
	/**
	 * 
	 */
	@Test
	public void testEqualityMotorListMap()
	{
		log_start("testEqualityMotorListMap");
		
		filteredMeasuringStation.updateEvent(null);
		
		for(Motor m : filteredMeasuringStation.getMotors())
		{
			logger.info("Testing motor " + m.getID() + " (" + m.getName() + ")");
			assertNotNull(filteredMeasuringStation.
							getAbstractDeviceByFullIdentifyer(m.getFullIdentifyer()));
			logger.info("Motor " + m.getID() + " (" + m.getName() + ") found in Map");
			
			for(MotorAxis ma : m.getAxes())
			{
				logger.info("Testing motor axis " + ma.getID() + " (" + ma.getName() + ")");
				assertNotNull(filteredMeasuringStation.getMotorAxisById(ma.getID()));
				logger.info("Motor Axis " + ma.getID() + " (" + ma.getName() + ") found in Map");
			}
		}
		
		log_end("testEqualityMotorListMap");
	}
	
	// ****************************************************************
	
	/**
	 * 
	 */
	@Ignore("Not Implemented Yet")
	@Test
	public void testExcludeUnusedDevicesWithMotors()
	{
		assertTrue(true);
	}
	
	/**
	 * 
	 */
	@Ignore("Not Implemented Yet")
	@Test
	public void testExcludeUnusedDevicesWithDetectors()
	{
		assertTrue(true);
	}
	
	/**
	 * 
	 */
	@Ignore("Not Implemented Yet")
	@Test
	public void testExcludeUnusedDevicesWithDevices()
	{
		assertTrue(true);
	}
	
	// ****************************************************************
	
	/*
	 * 
	 */
	private boolean isMotor(IMeasuringStation measuringstation, Motor m)
	{
		Motor motor = (Motor) measuringstation.
				getAbstractDeviceByFullIdentifyer(m.getFullIdentifyer());
		return motor != null;
	}
	
	/*
	 * 
	 */
	private boolean isMotorAxis(IMeasuringStation measuringstation, 
								MotorAxis ma)
	{
		MotorAxis motoraxis = measuringstation.getMotorAxisById(ma.getID());
		return motoraxis != null;
	}
	
	/*
	 * 
	 */
	private boolean isOption(IMeasuringStation measuringstation, Option o)
	{
		Option option = (Option) 
				measuringstation.getPrePostscanDeviceById(o.getID());
		return option != null;
	}
	
	// ****************************************************************
	
	/*
	 * 
	 */
	private String deviceString(AbstractDevice d)
	{
		return d.getName() + " (" + d.getID() + ")";
	}
	
	/*
	 * 
	 */
	private void log_start(String name)
	{
		logger.info("*******************************************************");
		logger.info("**** " + name + " started");
		logger.info("*******************************************************");
	}
	
	/*
	 * 
	 */
	private void log_end(String name)
	{
		logger.info("*******************************************************");
		logger.info("**** " + name + " finished");
		logger.info("*******************************************************");
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
		
		measuringStation = Configurator.getMeasuringStation();
		
		assertNotNull(measuringStation);
		
		logger.info("*******************************************************");
		logger.info("Class Wide Setup Done (measuring station loaded)");
		logger.info("*******************************************************");
	}
	
	/**
	 * Initializes the measuring station filter (test wide set up method).
	 */
	@Before
	public void beforeTest()
	{
		filteredMeasuringStation = new ExcludeFilter();
		filteredMeasuringStation.setSource(measuringStation);
		assertNotNull(filteredMeasuringStation);
		logger.info("**********************************************************");
		logger.info(
			"Test Wide Setup Done (measuring station filter initialized)");
		logger.info("**********************************************************");
	}
	
	/**
	 * Garbages the measuring station filter (test wide tear down method).
	 */
	@After
	public void afterTest()
	{
		filteredMeasuringStation.setSource(null);
		filteredMeasuringStation = null;
		logger.info("**********************************************************");
		logger.info(
			"Test Wide Tear Down Done (measuring station filter garbaged)");
		logger.info("**********************************************************");
	}
	
	/**
	 * Class wide tear down method.
	 */
	@AfterClass
	public static void afterClass()
	{
		logger.info("*******************************************************");
		logger.info("Class Wide Tear Down Done (files closed)");
		logger.info("*******************************************************");
	}
}