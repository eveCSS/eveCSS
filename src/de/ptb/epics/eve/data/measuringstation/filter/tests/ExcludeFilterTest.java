package de.ptb.epics.eve.data.measuringstation.filter.tests;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.*;
import org.xml.sax.SAXException;

import de.ptb.epics.eve.data.measuringstation.DetectorChannel;
import de.ptb.epics.eve.data.measuringstation.IMeasuringStation;
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
	
	private static File schemaFile;
	private static File descriptionFile;
	private static IMeasuringStation measuringStation;
	
	private ExcludeFilter filteredMeasuringStation;
	
	/**
	 * Tests the exclusion of a motor axis.
	 */
	@Test
	public void testExcludeIncludeMotorAxis()
	{
		// the motor axis with id: "Counter01.01" should be found
		assertNotNull(filteredMeasuringStation.getMotorAxisById("Counter01.01"));
		
		// exclude it
		MotorAxis ma = measuringStation.getMotorAxisById("Counter01.01");
		filteredMeasuringStation.exclude(ma);
		
		// now the axis shouldn't be found anymore
		assertNull(filteredMeasuringStation.getMotorAxisById("Counter01.01"));
		
		// include it
		filteredMeasuringStation.include(ma);
		
		// should be found again
		assertNotNull(filteredMeasuringStation.getMotorAxisById("Counter01.01"));
	}
	
	/**
	 * Tests the exclusion of a detector channel.
	 */
	@Test
	public void testExcludeIncludeDetectorChannel()
	{
		// the detector channel with id: "ringCurrent1chan1" should be found
		assertNotNull(filteredMeasuringStation.
						getDetectorChannelById("ringCurrent1chan1"));
		
		// exclude the device
		DetectorChannel detCh = 
				measuringStation.getDetectorChannelById("ringCurrent1chan1");
		filteredMeasuringStation.exclude(detCh);
		
		// now the detector shouldn't be found anymore
		assertNull(filteredMeasuringStation.
						getDetectorChannelById("ringCurrent1chan1"));
		
		// include it
		filteredMeasuringStation.include(detCh);
		
		// should be found again
		assertNotNull(filteredMeasuringStation.
				getDetectorChannelById("ringCurrent1chan1"));
	}
	
	// ***********************************************************************
	// ***********************************************************************
	// ***********************************************************************
	
	/**
	 * class wide setup method
	 */
	@BeforeClass
	public static void runBeforeClass() {
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
	}
	
	/**
	 * test wide tear down method
	 */
	@After
	public void afterEveryTest()
	{
		filteredMeasuringStation = null;
	}
	
	/**
	 * class wide tear down method
	 */
	@AfterClass
	public static void afterClass()
	{
		schemaFile = null;
		descriptionFile = null;
	}
}