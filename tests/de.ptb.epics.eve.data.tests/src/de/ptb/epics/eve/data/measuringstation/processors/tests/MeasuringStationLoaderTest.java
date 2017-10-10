package de.ptb.epics.eve.data.measuringstation.processors.tests;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

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
		int count = 0;
		
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
				System.err.println(e.getMessage());
			} catch (SAXException e) {
				System.err.println(e.getMessage());
			} catch (IOException e) {
				System.err.println(e.getMessage());
			}
			
			if(document != null) {
				NodeList idNodes = document.getElementsByTagName("id");
				
				List<String> idNames = 
						new ArrayList<String>(idNodes.getLength());
				
				for(int i = 0; i < idNodes.getLength(); i++) {
					idNames.add(idNodes.item(i).getFirstChild().getNodeValue());
				}
				
				java.util.Collections.sort(idNames);
				
				count = 0;
				
				for(int i = 0; i < idNames.size()-1; i++) {
					if(idNames.get(i).equals(idNames.get(i+1))) {
						count++;
					}
				}
			}
			assertTrue(count == 0);
		}
	}
	
	// *********************************************************************
	
	/**
	 * Class wide setup method of the test
	 */
	@BeforeClass
	public static void runBeforeClass() {
		stations = Configurator.getMeasuringStations();
		
		for(IMeasuringStation ims : stations) {
			assertNotNull(ims);
		}
	}
	
	/**
	 * test wide set up method
	 */
	@Before
	public void beforeEveryTest() {
	}
	
	/**
	 * test wide tear down method
	 */
	@After
	public void afterEveryTest() {
	}
	
	/**
	 * class wide tear down method
	 */
	@AfterClass
	public static void afterClass() {
	}
}
