package de.ptb.epics.eve.data.scandescription.defaults;

import java.io.File;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

/**
 * Tries to read a given XML file (as defined in 
 * de.ptb.epics.eve.resources/cfg/defaults.xsd) for default values.
 * Values for individual devices can later be requested via 
 * {@link #getAxis(String)} and {@link #getChannel(String)}.
 * 
 * @author Marcus Michalsky
 * @since 1.8
 */
public class DefaultsManager {
	private static final Logger LOGGER = Logger.getLogger(DefaultsManager.class
			.getName());
	
	private volatile Defaults defaults;
	
	/**
	 * @param pathToDefaults the location of the defaults file
	 * @param schema the schema file
	 */
	public void load(File pathToDefaults, File schema) {
		if (!pathToDefaults.exists()) {
			LOGGER.error("schema file not found!");
			defaults = null;
			return;
		}
		SchemaFactory schemaFactory = SchemaFactory.newInstance(
				XMLConstants.W3C_XML_SCHEMA_NS_URI);
		try {
			Schema schemaFile = schemaFactory.newSchema(schema);
			JAXBContext jaxbContext = JAXBContext.newInstance(Defaults.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			jaxbUnmarshaller.setSchema(schemaFile);
			this.defaults = (Defaults) jaxbUnmarshaller
					.unmarshal(pathToDefaults);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("found defaults: \n" + this.defaults.toString());
			} else {
				LOGGER.info("found defaults");
			}
		} catch(JAXBException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (SAXException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
	
	/**
	 * Returns the axis with the given id or <code>null</code> if not found.
	 * 
	 * @param id the id of the axis to get defaults for
	 * @return the axis with the given id or <code>null</code> if not found.
	 */
	public DefaultAxis getAxis(String id) {
		if (this.defaults == null) {
			return null;
		}
		for (DefaultAxis axis : this.defaults.getAxes()) {
			if (axis.getId().equals(id)) {
				return axis;
			}
		}
		return null;
	}
	
	/**
	 * Returns the channel with the given id or <code>null</code> if not found.
	 * 
	 * @param id the id of the channel to get defaults for
	 * @return the channel with the given id or <code>null</code> if not found.
	 */
	public DefaultChannel getChannel(String id) {
		if (this.defaults == null) {
			return null;
		}
		for (DefaultChannel channel : this.defaults.getChannels()) {
			if (channel.getId().equals(id)) {
				return channel;
			}
		}
		return null;
	}
}