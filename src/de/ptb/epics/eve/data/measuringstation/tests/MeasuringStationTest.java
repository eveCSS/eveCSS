package de.ptb.epics.eve.data.measuringstation.tests;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.apache.log4j.RollingFileAppender;
import org.apache.log4j.xml.DOMConfigurator;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.xml.sax.SAXException;

import de.ptb.epics.eve.data.measuringstation.IMeasuringStation;
import de.ptb.epics.eve.data.measuringstation.filter.tests.ExcludeFilterTest;
import de.ptb.epics.eve.data.measuringstation.processors.MeasuringStationLoader;

/**
 * @author Marcus Michalsky
 * @since 0.4.1
 */
public class MeasuringStationTest {

	private static Logger logger = 
		Logger.getLogger(ExcludeFilterTest.class.getName());
	
	private static File schemaFile;
	private static File descriptionFile;
	private static IMeasuringStation measuringStation;
	
	/**
	 * 
	 */
	@Test
	public void testDummy()
	{
		assertTrue(true);
	}
	
	/**
	 * 
	 */
	@Ignore("Not implemented yet")
	@Test
	public void testCompareXMLandJava()
	{

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
		
		((RollingFileAppender)logger.getAppender("MeasuringStationTestAppender")).rollOver();
		
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
		logger.info("Test Wide Setup Done");
	}
	
	/**
	 * test wide tear down method
	 */
	@After
	public void afterEveryTest()
	{
		logger.info("Test Wide Tear Down Done");
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
