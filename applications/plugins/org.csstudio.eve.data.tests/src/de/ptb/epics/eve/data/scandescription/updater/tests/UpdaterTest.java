package de.ptb.epics.eve.data.scandescription.updater.tests;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.junit.Ignore;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import de.ptb.epics.eve.data.scandescription.updater.Updater;
import de.ptb.epics.eve.data.scandescription.updater.VersionTooOldException;
import de.ptb.epics.eve.data.scandescription.updater.patches.SCMLUpdater;
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
	@Ignore
	@Test
	public void testUpdate() {
		try {
			File testFile = new File(ClassLoader.getSystemResource("test.scml")
					.getFile());

			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(new File(ClassLoader
					.getSystemResource("schema.xsd").getFile()));
			Node node = document.getElementsByTagName("schema").item(0);
			String versionString = node.getAttributes().getNamedItem("version")
					.getNodeValue();
			Version currentVersion = new Version(Integer.parseInt(versionString
					.split("\\.")[0]), Integer.parseInt(versionString
					.split("\\.")[1]));

			factory = DocumentBuilderFactory.newInstance();
			SchemaFactory schemaFactory = SchemaFactory
					.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema oldSchema = schemaFactory.newSchema(ClassLoader
					.getSystemResource("updateSchema.xsd"));
			factory.setNamespaceAware(true);
			factory.setSchema(oldSchema);
			builder = factory.newDocumentBuilder();
			
			document = builder.parse(testFile);

			Updater updater = new SCMLUpdater();
			updater.update(document, currentVersion);

			Schema schema = SchemaFactory.newInstance(
					XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(
					ClassLoader.getSystemResource("schema.xsd"));
			schema.newValidator().validate(new DOMSource(document));

			document.normalizeDocument();
			
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(document);
			StreamResult result = new StreamResult(new File("/tmp/eveJUnit.xml"));
	 
			// Output to console for testing
			// StreamResult result = new StreamResult(System.out);
	 
			transformer.transform(source, result);
			
			schema.newValidator().validate(new DOMSource(document));
		} catch (ParserConfigurationException e) {
			fail(e.getMessage());
		} catch (SAXException e) {
			fail(e.getMessage());
		} catch (IOException e) {
			fail(e.getMessage());
		} catch (VersionTooOldException e) {
			fail(e.getMessage());
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}
}