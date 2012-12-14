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

import de.ptb.epics.eve.data.measuringstation.IMeasuringStation;
import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.data.scandescription.Channel;

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
	private boolean initialized;
	
	/** */
	public DefaultsManager() {
		this.defaults = null;
		this.initialized = false;
	}
	
	/**
	 * @param pathToDefaults the location of the defaults file
	 * @param schema the schema file
	 */
	public void init(File pathToDefaults, File schema) {
		if (!pathToDefaults.exists()) {
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
			this.initialized = true;
		} catch(JAXBException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (SAXException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
	
	/**
	 * Returns whether the manager is initialized.
	 * 
	 * @return whether the manager is initialized
	 */
	public boolean isInitialized() {
		return this.initialized;
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
	
	
	
	/**
	 * Transfers the properties from the given default channel to the 
	 * target channel.
	 * 
	 * @param from the source values (defaults)
	 * @param to the target channel
	 * @param measuringStation the device definition
	 */
	public static void transferDefaults(DefaultChannel from, Channel to, 
			IMeasuringStation measuringStation) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("transfering defaults for "
					+ to.getAbstractDevice().getID());
		}
		try {
			to.setAverageCount(from.getAverageCount());
		} catch (NullPointerException e) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("no default for average count of "
						+ to.getAbstractDevice().getID());
			}
		}
		try {
			to.setMaxAttempts(from.getMaxAttempts());
		} catch (NullPointerException e) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("no default for max attempts of "
						+ to.getAbstractDevice().getID());
			}
		}
		try {
			to.setMaxDeviation(from.getMaxDeviation());
		} catch (NullPointerException e) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("no default for max deviation of "
						+ to.getAbstractDevice().getID());
			}
		}
		try {
			to.setMinimum(from.getMinimum());
		} catch (NullPointerException e) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("no default for minimum of "
						+ to.getAbstractDevice().getID());
			}
		}
		try {
			to.setNormalizeChannel(measuringStation.getDetectorChannelById(from
					.getNormalizeId()));
		} catch (NullPointerException e) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("no default for normalize channel of "
						+ to.getAbstractDevice().getID());
			}
		}
		to.setDeferred(from.isDeferred());
	}
	
	/**
	 * Transfers the properties from the given default axis to the 
	 * target axis.
	 * 
	 * @param from the source values (defaults)
	 * @param to the target axis
	 */
	public static void transferDefaults(DefaultAxis from, Axis to) {
		to.setStepfunction(from.getStepfunction());
		to.setPositionMode(from.getPositionmode());
		switch (from.getStepfunction()) {
		case ADD:
		case MULTIPLY:
		case PLUGIN:
			// TODO
			break;
		case FILE:
			to.setFile(new File(from.getFile()));
			break;
		case POSITIONLIST:
			to.setPositionlist(from.getPositionList());
			break;
		}
	}
}