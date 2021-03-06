package de.ptb.epics.eve.data.scandescription.processors;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

import de.ptb.epics.eve.data.measuringstation.IMeasuringStation;
import de.ptb.epics.eve.data.measuringstation.util.NameProvider;
import de.ptb.epics.eve.data.scandescription.ScanDescription;

/**
 * This class loads a scan description form a file.
 * 
 * @author Stephan Rehfeld (- at-) ptb.de>
 * @author Hartmut Scherr
 */
public class ScanDescriptionLoader {
	private File fileToLoad;
	private final IMeasuringStation measuringStation;
	private ScanDescription scanDescription;
	private List<ScanDescriptionLoaderDeviceMessage> lostDevicesList;
	private final File schemaFile;

	/**
	 * Constructs a <code>ScanDescriptionLoader</code>.
	 * 
	 * @param measuringStation
	 * @param schemaFile
	 * @throws IllegalArgumentException
	 */
	public ScanDescriptionLoader(final IMeasuringStation measuringStation,
			final File schemaFile) {
		if (schemaFile == null) {
			throw new IllegalArgumentException(
					"The parameter 'schemaFile' must not be null!");
		}
		if (measuringStation == null) {
			throw new IllegalArgumentException(
					"The parameter 'measuringStation' must not be null!");
		}
		this.measuringStation = measuringStation;
		this.schemaFile = schemaFile;
	}

	/**
	 * This method loads the a scan description out of a given file.
	 * 
	 * @param fileToLoad
	 *            The file to load.
	 * @throws ParserConfigurationException
	 *             Gets thrown if the parser has been configured in a wrong way.
	 * @throws SAXException
	 *             Thrown by the SAX Parser.
	 * @throws IOException
	 *             Thrown is IO Operations to the file didn't work.
	 */
	public void load(final File fileToLoad)
			throws ParserConfigurationException, SAXException, IOException {
		this.fileToLoad = fileToLoad;
		this.load();
		this.scanDescription.setFileName(fileToLoad.getName());
	}

	/**
	 * This method loads a scan description from a byte array stream.
	 * 
	 * @param xmlData
	 *            The byte array stream that contains the scan description.
	 * @throws ParserConfigurationException
	 *             Gets thrown if the parser has been configured in a wrong way.
	 * @throws SAXException
	 *             Thrown by the SAX Parser.
	 * @throws IOException
	 *             Thrown is IO Operations to the file didn't work.
	 */
	public void loadFromByteArray(final byte[] xmlData)
			throws ParserConfigurationException, SAXException, IOException {
		final SchemaFactory sFactory = SchemaFactory
				.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		File schemaFile = new File(measuringStation.getSchemaFileName());
		final Schema schema = sFactory.newSchema(schemaFile);

		final SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setNamespaceAware(true);
		factory.setSchema(schema);
		final SAXParser saxParser = factory.newSAXParser();

		NameProvider nameProvider = new NameProvider(null, schemaFile);
		
		final ScanDescriptionLoaderHandler handler = new ScanDescriptionLoaderHandler(
				this.measuringStation, nameProvider);
		saxParser.parse(new ByteArrayInputStream(xmlData), handler);

		this.scanDescription = handler.getScanDescription();
	}

	/**
	 * This method loads the scan description out of the current file.
	 * 
	 * @throws ParserConfigurationException
	 *             Gets thrown if the parser has been configured in a wrong way.
	 * @throws SAXException
	 *             Thrown by the SAX Parser.
	 * @throws IOException
	 *             Thrown is IO Operations to the file didn't work.
	 */
	public void load() throws ParserConfigurationException, SAXException,
			IOException {

		this.scanDescription = null;

		final SchemaFactory sFactory = SchemaFactory
				.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		final Schema schema = sFactory.newSchema(schemaFile);

		final SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setNamespaceAware(true);
		factory.setSchema(schema);
		final SAXParser saxParser = factory.newSAXParser();
		
		NameProvider nameProvider = new NameProvider(fileToLoad, schemaFile);
		
		final ScanDescriptionLoaderHandler handler = new ScanDescriptionLoaderHandler(
				this.measuringStation, nameProvider);
		saxParser.parse(this.fileToLoad, handler);
		if (handler.hasErrors() || handler.hasFatalErrors()) {
			throw new SAXException("Document could not be parsed due to parser errors. " +
					"See the log files for details.");
		}

		this.scanDescription = handler.getScanDescription();
		this.scanDescription.setFileName(this.fileToLoad.getName());
		this.lostDevicesList = handler.getLostDevices();
		this.scanDescription.checkForOrphanedMonitors();
	}

	/**
	 * This method returns the currently used source file.
	 * 
	 * @return The currently used source file.
	 */
	public File getFileToLoad() {
		return this.fileToLoad;
	}

	/**
	 * This method sets the currently used source file.
	 * 
	 * @param fileToLoad
	 */
	public void setFileToLoad(final File fileToLoad) {
		this.fileToLoad = fileToLoad;
	}

	/**
	 * This method returns the currently used measuring station description.
	 * 
	 * @return The currently used measuring station.
	 */
	public IMeasuringStation getMeasuringStation() {
		return measuringStation;
	}

	/**
	 * This method returns the loaded scan description.
	 * 
	 * @return The loaded scan description.
	 */
	public ScanDescription getScanDescription() {
		return scanDescription;
	}

	/**
	 * 
	 * @return
	 */
	public List<ScanDescriptionLoaderDeviceMessage> getLostDevices() {
		if ((this.lostDevicesList != null) && (!this.lostDevicesList.isEmpty())) {
			return this.lostDevicesList;
		}
		return null;
	}
}