package de.ptb.epics.eve.data.scandescription.updater.tests;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

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

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import de.ptb.epics.eve.data.scandescription.updater.Modification;
import de.ptb.epics.eve.data.scandescription.updater.Patch;
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

			SchemaFactory schemaFactory = SchemaFactory
					.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schema = schemaFactory.newSchema(ClassLoader
					.getSystemResource("schema.xsd"));
			
			factory.setNamespaceAware(true);
			factory.setSchema(schema);
			
			builder.reset();
			document = builder.parse(testFile);
			
			List<Patch> patches = Updater.getInstance().update(document, 
					currentVersion);
			for (Patch p : patches) {
				for (Modification m : p.getModifications()) {
					System.out.println(m.getChangeLog());
				}
			}
			
			schema.newValidator().validate(new DOMSource(document));
			
			DOMSource domSource = new DOMSource(document);
		       StringWriter writer = new StringWriter();
		       StreamResult result = new StreamResult(writer);
		       TransformerFactory tf = TransformerFactory.newInstance();
		       Transformer transformer = tf.newTransformer();
		       transformer.transform(domSource, result);
		       System.out.println(writer.toString());
		} catch (ParserConfigurationException e) {
			fail(e.getMessage());
		} catch (SAXException e) {
			fail(e.getMessage());
		} catch (IOException e) {
			fail(e.getMessage());
		} catch (VersionTooOldException e) {
			fail(e.getMessage());
		} catch (TransformerConfigurationException e) {
			fail(e.getMessage());
		} catch (TransformerException e) {
			fail(e.getMessage());
		}
	}
}