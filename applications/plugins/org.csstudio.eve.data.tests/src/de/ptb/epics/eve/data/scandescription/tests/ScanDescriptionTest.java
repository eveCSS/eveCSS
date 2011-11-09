package de.ptb.epics.eve.data.scandescription.tests;

import static de.ptb.epics.eve.data.tests.internal.LogFileStringGenerator.*;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.xml.sax.SAXException;

import de.ptb.epics.eve.data.measuringstation.processors.MeasuringStationLoader;
import de.ptb.epics.eve.data.scandescription.ScanDescription;
import de.ptb.epics.eve.data.tests.internal.Configurator;

public class ScanDescriptionTest {
	
	private static Logger logger = 
			Logger.getLogger(ScanDescriptionTest.class.getName());
	
	private ScanDescription scanDescription;
	
	/**
	 * 
	 */
	@Ignore("Not implemented yet!")
	@Test
	public void testModelUpdate() {
		
	}
	
	/*
	 * 
	 */
	private ScanDescription createScanDescription() {
		
		File schemaFile = new File("xml/scml.xsd");
		
		final MeasuringStationLoader measuringStationLoader = 
				new MeasuringStationLoader(schemaFile);
		try {
			measuringStationLoader.load(new File("xml/test.xml"));
		} catch (ParserConfigurationException e) {
			e.printStackTrace(); return null;
		} catch (SAXException e) {
			e.printStackTrace(); return null;
		} catch (IOException e) {
			e.printStackTrace(); return null;
		}
		ScanDescription scanDescription = new ScanDescription(
				measuringStationLoader.getMeasuringStation());
		return scanDescription;
	}
	
	/* ******************************************************************** */
	
	/**
	 * Initializes logging (Class wide setup 
	 * method of the test).
	 */
	@BeforeClass
	public static void setUpBeforeClass() {
		Configurator.configureLogging();
		classSetUp(logger);
	}
	
	/**
	 * test wide set up method
	 */
	@Before
	public void setUp() {
		testSetUp(logger);
		this.scanDescription = createScanDescription();
	}
	
	/**
	 * test wide tear down method
	 */
	@After
	public void tearDown() {
		testTearDown(logger);
	}
	
	/**
	 * class wide tear down method
	 */
	@AfterClass
	public static void tearDownAfterClass() {
		classTearDown(logger);
	}
}