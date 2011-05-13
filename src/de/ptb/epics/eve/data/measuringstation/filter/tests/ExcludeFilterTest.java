package de.ptb.epics.eve.data.measuringstation.filter.tests;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.apache.log4j.RollingFileAppender;
import org.apache.log4j.xml.DOMConfigurator;
import org.junit.*;
import org.xml.sax.SAXException;

import de.ptb.epics.eve.data.measuringstation.Detector;
import de.ptb.epics.eve.data.measuringstation.DetectorChannel;
import de.ptb.epics.eve.data.measuringstation.IMeasuringStation;
import de.ptb.epics.eve.data.measuringstation.Motor;
import de.ptb.epics.eve.data.measuringstation.MotorAxis;
import de.ptb.epics.eve.data.measuringstation.filter.ExcludeFilter;
import de.ptb.epics.eve.data.measuringstation.processors.MeasuringStationLoader;

/**
 * <code>ExcludeFilterTest</code> contains 
 * <a href="http://www.junit.org/">JUnit</a>-Tests for 
 * {@link de.ptb.epics.eve.data.measuringstation.filter.ExcludeFilter}.
 * 
 * @author Marcus Michalsky
 * @since 0.4.1
 */
public class ExcludeFilterTest {
	
	private static Logger logger = 
		Logger.getLogger(ExcludeFilterTest.class.getName());
	
	private static File schemaFile;
	private static File descriptionFile;
	private static IMeasuringStation measuringStation;
	
	private ExcludeFilter filteredMeasuringStation;
	
	/**
	 * temporary test
	 */
	@Ignore("temporary")
	@Test
	public void testSameObject()
	{
		log_start("testSameObject()");
		
		for(Motor m : measuringStation.getMotors())
		{
			assertNotSame(m, filteredMeasuringStation.getAbstractDeviceByFullIdentifyer(m.getFullIdentifyer()));
			logger.info(m + " and " + filteredMeasuringStation.getAbstractDeviceByFullIdentifyer(m.getFullIdentifyer()) + " are not the same object.");
		}
		log_end("testSameObject()");
	}
	
	/**
	 * temporary test
	 */
	@Ignore("temporary")
	@Test
	public void testIfExcludeNeedsSameObject()
	{
		log_start("testIfExcludeNeedsSameObject");
		for(Motor m : measuringStation.getMotors())
		{
			if(!(m == (Motor)filteredMeasuringStation.getAbstractDeviceByFullIdentifyer(m.getFullIdentifyer())))
			{
				filteredMeasuringStation.exclude(m);
				assertNull(filteredMeasuringStation.getAbstractDeviceByFullIdentifyer(m.getFullIdentifyer()));
			}
		}
		log_end("testIfExcludeNeedsSameObject");
		// seems that exclude does not need the same object
	}
	
	/**
	 * <code>testExcludeIncludeMotor</code> tries to exclude motors (each one 
	 * by one) and checks the getter methods for its presence. 
	 * Afterwards they are included and presence is checked again.
	 */
	@Test
	public void testExcludeIncludeMotor()
	{
		log_start("testExcludeIncludeMotor()");
		
		for(Motor m : measuringStation.getMotors())
		{
			logger.info("Testing Motor: " + m.getName() + " (" + m.getID() + ")");
			
			// the motor should be found
			assertNotNull(filteredMeasuringStation.
					getAbstractDeviceByFullIdentifyer(m.getFullIdentifyer()));
			logger.info("Motor " + m.getName() + " (" + m.getID() + ") found.");
			
			// exclude the motor
			filteredMeasuringStation.exclude(m);
			logger.info("Motor " + m.getName() + " (" + m.getID() + ") excluded.");
			
			// now the motor shouldn't be found anymore
			assertNull(filteredMeasuringStation.
					getAbstractDeviceByFullIdentifyer(m.getFullIdentifyer()));
			logger.info("Motor " + m.getName() + " (" + m.getID() + ") not found.");
			
			// all axes of the motor also shouldn't be found
			for(MotorAxis ma : m.getAxes())
			{
				logger.info("Checking if axis " + ma.getName() + " (" + 
							ma.getID() + ") of the motor is also excluded");
				assertNull(filteredMeasuringStation.
						getMotorAxisById(ma.getID()));
				logger.info("Motor Axis " + ma.getName() + " (" + 
							ma.getID() + ") not found.");
			}
			
			// include the motor
			filteredMeasuringStation.include(m);
			logger.info("Motor " + m.getName() + " (" + m.getID() + ") included.");
			
			// now the motor should be found again
			assertNotNull(filteredMeasuringStation.
					getAbstractDeviceByFullIdentifyer(m.getFullIdentifyer()));
			logger.info("Motor " + m.getName() + " (" + m.getID() + ") found.");

			// all axes of the motor also should be back
			for(MotorAxis ma : m.getAxes())
			{
				logger.info("Checking if axis " + ma.getName() + " (" + 
							ma.getID() + ") of the motor is also included");
				assertNotNull(filteredMeasuringStation.
						getMotorAxisById(ma.getID()));
				logger.info("Motor Axis " + ma.getName() + " (" + 
							ma.getID() + ") found.");
			}
			logger.info("-----");
		}	
		log_end("testExcludeIncludeMotor()");
	}
	
	/**
	 * Tests the exclusion of a motor axis.
	 */
	@Test
	public void testExcludeIncludeMotorAxis()
	{
		log_start("testExcludeIncludeMotorAxis()");
		
		for(Motor m : measuringStation.getMotors())
		{
			logger.info("Testing axes of motor " + m.getName() + " (" + m.getID() + ")");
			for(MotorAxis ma : m.getAxes())
			{
				MotorAxis currentAxis = 
					filteredMeasuringStation.getMotorAxisById(ma.getID());
				
				logger.debug("Testing motor axis " + ma.getName() + " (" + 
							ma.getID() + ")");
				
				// the motor axis should be found
				assertNotNull(filteredMeasuringStation.getMotorAxisById(ma.getID()));
				logger.info("Motor axis " + ma.getName() + " (" + 
						ma.getID() + ") found");
				
				// exclude it
				filteredMeasuringStation.exclude(currentAxis);
				logger.info("Motor axis " + ma.getName() + " (" + 
							ma.getID() + ") excluded.");
				
				// now it shouldn't be found anymore
				assertNull(filteredMeasuringStation.getMotorAxisById(ma.getID()));
				logger.info("Motor axis " + ma.getName() + " (" + 
							ma.getID() + ") not found");
				
				// include it
				filteredMeasuringStation.include(currentAxis);
				logger.info("Motor axis " + ma.getName() + " (" + 
						ma.getID() + ") included.");
				
				// now it should be found again
				assertNotNull(filteredMeasuringStation.getMotorAxisById(ma.getID()));
				logger.info("Motor axis " + ma.getName() + " (" + 
						ma.getID() + ") found");
			}
			logger.info("-----");
		}
		log_end("testExcludeIncludeMotorAxis()");
	}
	
	/**
	 * 
	 */
	@Test
	public void testExcludeIncludeDetector()
	{
		log_start("testExcludeIncludeDetector()");
		
		for(Detector d : measuringStation.getDetectors())
		{
			// the motor should be found
			assertNotNull(filteredMeasuringStation.
					getAbstractDeviceByFullIdentifyer(d.getFullIdentifyer()));
			logger.info("Detector " + d.getName() + " (" + d.getID() + ") found.");
			
			// exclude the detector
			filteredMeasuringStation.exclude(d);
			logger.info("Detector " + d.getName() + " (" + d.getID() + ") excluded.");
			
			// now the detector shouldn't be found anymore
			assertNull(filteredMeasuringStation.
					getAbstractDeviceByFullIdentifyer(d.getFullIdentifyer()));
			logger.info("Motor " + d.getName() + " (" + d.getID() + ") not found.");
			
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
			logger.info("Detector " + d.getName() + " (" + d.getID() + ") included.");
			
			// now the detector should be found again
			assertNotNull(filteredMeasuringStation.
					getAbstractDeviceByFullIdentifyer(d.getFullIdentifyer()));
			logger.info("Detector " + d.getName() + " (" + d.getID() + ") found.");

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
	 * Tests the exclusion of a detector channel.
	 */
	@Test
	public void testExcludeIncludeDetectorChannel()
	{
		log_start("testExcludeIncludeDetectorChannel()");
		
		for(Detector d : measuringStation.getDetectors())
		{
			logger.info("Testing channels of detector " + d.getName() + " (" + d.getID() + ")");
			
			for(DetectorChannel ch : d.getChannels())
			{
				logger.debug("Testing detector channel " + ch.getName() + " (" + 
							ch.getID() + ")");
				
				// the detector channel should be found
				assertNotNull(filteredMeasuringStation.getDetectorChannelById(ch.getID()));
				logger.info("DetectorChannel " + ch.getName() + " (" + 
						ch.getID() + ") found");
				
				// exclude it
				filteredMeasuringStation.exclude(ch);
				logger.info("Detector Channel " + ch.getName() + " (" + 
							ch.getID() + ") excluded.");
				
				// now it shouldn't be found anymore
				assertNull(filteredMeasuringStation.getDetectorChannelById(ch.getID()));
				logger.info("Detector Channel " + ch.getName() + " (" + 
							ch.getID() + ") not found");
				
				// include it
				filteredMeasuringStation.include(ch);
				logger.info("Detector Channel " + ch.getName() + " (" + 
						ch.getID() + ") included.");
				
				// now it should be found again
				assertNotNull(filteredMeasuringStation.getDetectorChannelById(ch.getID()));
				logger.info("Detector Channel " + ch.getName() + " (" + 
						ch.getID() + ") found");
			}
			logger.info("-----");
		}
		log_end("testExcludeIncludeDetectorChannel()");
	}
	
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
	 * class wide setup method
	 */
	@BeforeClass
	public static void runBeforeClass() {
		
		DOMConfigurator.configure("log4j-conf.xml");
		
		((RollingFileAppender)logger.getAppender("ExcludeFilterTestAppender")).rollOver();
		
		// run for one time before all test cases
		schemaFile = new File("xml/scml.xsd");
		descriptionFile = new File("xml/test.xml");
		
		final MeasuringStationLoader measuringStationLoader = 
			new MeasuringStationLoader(schemaFile);
		
		try {
			measuringStationLoader.load(descriptionFile);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		measuringStation = measuringStationLoader.getMeasuringStation();
		
		assertNotNull(measuringStation);
		logger.info("Class Wide Setup Done (measuring station loaded)");
	}
	
	/**
	 * test wide set up method
	 */
	@Before
	public void beforeEveryTest()
	{
		filteredMeasuringStation = new ExcludeFilter();
		filteredMeasuringStation.setSource(measuringStation);
		assertNotNull(filteredMeasuringStation);
		logger.info("Test Wide Setup Done (measuring station filter initialized)");
	}
	
	/**
	 * test wide tear down method
	 */
	@After
	public void afterEveryTest()
	{
		filteredMeasuringStation.setSource(null);
		filteredMeasuringStation = null;
		logger.info("Test Wide Tear Down Done (measuring station filter garbaged)");
	}
	
	/**
	 * class wide tear down method
	 */
	@AfterClass
	public static void afterClass()
	{
		schemaFile = null;
		descriptionFile = null;
		logger.info("Class Wide Tear Down Done (files closed)");
	}
}