package de.ptb.epics.eve.data.measuringstation.processors.tests;

import static de.ptb.epics.eve.data.tests.internal.LogFileStringGenerator.*;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.apache.log4j.RollingFileAppender;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.ptb.epics.eve.data.measuringstation.IMeasuringStation;
import de.ptb.epics.eve.data.tests.internal.Configurator;

/**
 * <code>MeasuringStationLoaderTest</code> contains 
 * <a href="http://www.junit.org/">JUnit</a>-Tests for 
 * {@link de.ptb.epics.eve.data.measuringstation.processors.MeasuringStationLoader}.
 * 
 * @author Marcus Michalsky
 * @since 0.4.1
 */
public class MeasuringStationLoaderTest {

	private static Logger logger = 
			Logger.getLogger(MeasuringStationLoaderTest.class.getName());
	
	private static List<IMeasuringStation> stations;
	
	/**
	 * Tests the available 
	 * {@link de.ptb.epics.eve.data.measuringstation.MeasuringStation}s for 
	 * duplicate IDs.
	 * <p>
	 * 
	 * @see java.util.Collections#sort(List)
	 */
	@Test
	public void testUniqueIDs() {
		
		log_start(logger, "testUniqueIDs");
		
		for(IMeasuringStation ims : stations) {
			
			String station = ims.getLoadedFileName();
			
			// get document builder factory
			DocumentBuilderFactory factory = 
				DocumentBuilderFactory.newInstance();
			
			DocumentBuilder builder = null;
			Document document = null;
			
			// get dom parser
			try {
				builder = factory.newDocumentBuilder();
				// get dom parse tree
				document = builder.parse(station);
			} catch (ParserConfigurationException e) {
				logger.error(e.getMessage(), e);
			} catch (SAXException e) {
				logger.error(e.getMessage(), e);
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
			
			if(document != null) {
				NodeList idNodes = document.getElementsByTagName("id");
				
				List<String> idNames = 
						new ArrayList<String>(idNodes.getLength());
				
				for(int i = 0; i < idNodes.getLength(); i++) {
					idNames.add(idNodes.item(i).getFirstChild().getNodeValue());
				}
				
				java.util.Collections.sort(idNames);
				
				logger.debug("Testing ids of: " + ims.getLoadedFileName());
				
				int count = 0;
				
				for(int i = 0; i < idNames.size()-1; i++) {
					if(idNames.get(i).equals(idNames.get(i+1))) {
						logger.debug("duplicate id found: " + idNames.get(i));
						count++;
					}
				}
				
				logger.debug("-----> Found " + count + " duplicates.");
				
				logger.debug("****************************************");
			}
		}
		
		log_end(logger, "testUniqueIDs");
	}
	
	// *********************************************************************
	
	/**
	 * Initializes logging and loads the measuring stations (Class wide setup 
	 * method of the test).
	 */
	@BeforeClass
	public static void runBeforeClass() {
		
		Configurator.configureLogging();
		
		((RollingFileAppender)logger.
				getAppender("MeasuringStationLoaderTestAppender")).rollOver();
		
		stations = Configurator.getMeasuringStations();
		
		for(IMeasuringStation ims : stations) {
			assertNotNull(ims);
		}
		classSetUp(logger);
	}
	
	/**
	 * test wide set up method
	 */
	@Before
	public void beforeEveryTest()
	{
		testSetUp(logger);
	}
	
	/**
	 * test wide tear down method
	 */
	@After
	public void afterEveryTest()
	{
		testTearDown(logger);
	}
	
	/**
	 * class wide tear down method
	 */
	@AfterClass
	public static void afterClass()
	{
		classTearDown(logger);
	}
}
