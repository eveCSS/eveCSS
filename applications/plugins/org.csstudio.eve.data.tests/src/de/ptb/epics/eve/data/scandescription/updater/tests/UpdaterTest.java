package de.ptb.epics.eve.data.scandescription.updater.tests;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import de.ptb.epics.eve.data.scandescription.updater.Updater;
import de.ptb.epics.eve.data.scandescription.updater.VersionTooOldException;
import de.ptb.epics.eve.util.data.Version;

/**
 * @author Marcus Michalsky
 * @since 1.18
 */
public class UpdaterTest {
	
	/**
	 * Tests whether all included Updates are applied successfully by validating 
	 * the result with the XML schema.
	 */
	@Test
	public void testUpdate() {
		try {
			URL url = ClassLoader.getSystemResource("test.scml");
			File testFile = new File(url.getPath());

			url = ClassLoader.getSystemResource("schema.xsd");
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(new File(url.getPath()));
			Node node = document.getElementsByTagName("schema").item(0);
			String versionString = node.getAttributes().getNamedItem("version")
					.getNodeValue();
			Version currentVersion = new Version(Integer.parseInt(versionString
					.split("\\.")[0]), Integer.parseInt(versionString
					.split("\\.")[1]));
			
			builder = factory.newDocumentBuilder();
			document = builder.parse(testFile);
			
			Updater.getInstance().update(document, currentVersion);
			
			SchemaFactory schemaFactory = SchemaFactory
					.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schema = schemaFactory
					.newSchema(url);
			
			schema.newValidator().validate(new DOMSource(document));
		} catch (ParserConfigurationException e) {
			fail(e.getMessage());
		} catch (SAXException e) {
			fail(e.getMessage());
		} catch (IOException e) {
			fail(e.getMessage());
		} catch (VersionTooOldException e) {
			fail(e.getMessage());
		}
	}
}