package de.ptb.epics.eve.editor.gef;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import de.ptb.epics.eve.data.scandescription.updater.Patch;
import de.ptb.epics.eve.data.scandescription.updater.Updater;
import de.ptb.epics.eve.data.scandescription.updater.VersionTooNewException;
import de.ptb.epics.eve.data.scandescription.updater.VersionTooOldException;
import de.ptb.epics.eve.data.scandescription.updater.patches.SCMLUpdater;
import de.ptb.epics.eve.util.data.Version;


/**
 * @author Marcus Michalsky
 * @since 1.18
 */
public class ScanDescriptionEditorFileLoader {
	private static final Logger LOGGER = Logger
			.getLogger(ScanDescriptionEditorFileLoader.class.getName());
	
	private List<Patch> appliedPatches;
	
	/**
	 * 
	 * @param scanDescription
	 * @throws VersionTooOldException 
	 * @throws VersionTooNewException 
	 */
	public File loadFile(File scanDescription) throws VersionTooOldException, VersionTooNewException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
			Document document = builder.parse(scanDescription);
			
			Updater updater = new SCMLUpdater();

			this.appliedPatches = updater.update(document,
					getCurrentSchemaVersion());
			
			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer transformer = tFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "no");
			DOMSource source = new DOMSource(document);
			File tempFile = File.createTempFile(
					"eveCSS-" + System.getProperty("user.name")
							+ scanDescription.getName(), ".tmp");
			tempFile.deleteOnExit();
			StreamResult result = new StreamResult(tempFile.getAbsolutePath());
			transformer.transform(source, result);
			return tempFile;
		} catch (ParserConfigurationException | SAXException | IOException | 
				TransformerException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}
	
	private Version getCurrentSchemaVersion() {
		File schema = de.ptb.epics.eve.resources.Activator.getXMLSchema();
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
			Document document = builder.parse(schema);
			
			Node node = document.getElementsByTagName("schema").item(0);
			String versionString = node.getAttributes().getNamedItem("version")
					.getNodeValue();
			return new Version(
					Integer.parseInt(versionString.split("\\.")[0]), 
					Integer.parseInt(versionString.split("\\.")[1]));
		} catch (SAXException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (ParserConfigurationException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}
	
	/**
	 * Returns a list of patches applied during the update procedure.
	 * 
	 * @return a list of patches applied during the update procedure
	 */
	public List<Patch> getChanges() {
		return this.appliedPatches;
	}
}