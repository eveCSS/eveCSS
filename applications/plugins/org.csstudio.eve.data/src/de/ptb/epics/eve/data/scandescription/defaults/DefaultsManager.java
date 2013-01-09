package de.ptb.epics.eve.data.scandescription.defaults;

import java.io.File;
import java.io.IOException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import de.ptb.epics.eve.data.measuringstation.IMeasuringStation;
import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.data.scandescription.Chain;
import de.ptb.epics.eve.data.scandescription.Channel;
import de.ptb.epics.eve.data.scandescription.ScanDescription;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.util.io.*;

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
	
	private Defaults defaults;
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
	 * ... {@link #isInitialized()} should be checked before.
	 * 
	 * @param targetFile the destination
	 * @param schemaFile the schema file
	 */
	public synchronized void save(File targetFile, File schemaFile) {
		if (targetFile.exists()) {
			try {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("target file already exists, creating backup");
				}
				File backup = new File(targetFile.getParent() + "/"
						+ targetFile.getName() + ".bup");
				FileUtil.copyFile(targetFile, backup);
			} catch (IOException e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
		SchemaFactory schemaFactory = SchemaFactory.newInstance(
				XMLConstants.W3C_XML_SCHEMA_NS_URI);
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Defaults.class);
			Schema schema = schemaFactory.newSchema(schemaFile);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setSchema(schema);
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			jaxbMarshaller.marshal(this.defaults, targetFile);
			LOGGER.info("defaults saved");
		} catch (JAXBException e) {
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
	public DefaultsAxis getAxis(String id) {
		if (this.defaults == null) {
			return null;
		}
		for (DefaultsAxis axis : this.defaults.getAxes()) {
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
	public DefaultsChannel getChannel(String id) {
		if (this.defaults == null) {
			return null;
		}
		for (DefaultsChannel channel : this.defaults.getChannels()) {
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
	public static void transferDefaults(DefaultsChannel from, Channel to, 
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
	public static void transferDefaults(DefaultsAxis from, Axis to) {
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
	
	/**
	 * 
	 * @param axis
	 * @return
	 */
	public static DefaultsAxis getDefaultsAxis(Axis axis) {
		DefaultsAxis defaultsAxis = new DefaultsAxis();
		defaultsAxis.setId(axis.getMotorAxis().getID());
		defaultsAxis.setPositionmode(axis.getPositionMode());
		defaultsAxis.setStepfunction(axis.getStepfunction());
		switch(axis.getStepfunction()) {
		case ADD:
		case MULTIPLY:
			// TODO
			break;
		case FILE:
			defaultsAxis.setFile(axis.getFile().getAbsolutePath());
			break;
		case PLUGIN:
			// TODO
			break;
		case POSITIONLIST:
			defaultsAxis.setPositionList(axis.getPositionlist());
			break;
		}
		return defaultsAxis;
	}
	
	/**
	 * 
	 * @param channel
	 * @return
	 */
	public static DefaultsChannel getDefaultsChannel(Channel channel) {
		DefaultsChannel defaultsChannel = new DefaultsChannel();
		defaultsChannel.setId(channel.getDetectorChannel().getID());
		defaultsChannel.setAverageCount(channel.getAverageCount());
		defaultsChannel.setMaxAttempts(channel.getMaxAttempts());
		defaultsChannel.setMinimum(channel.getMinimum());
		defaultsChannel.setMaxDeviation(channel.getMaxDeviation());
		defaultsChannel.setDeferred(channel.isDeferred());
		defaultsChannel.setNormalizeId(channel.getNormalizeChannel().getID());
		return defaultsChannel;
	}
	
	/**
	 * 
	 * @param scanDescription
	 */
	public synchronized void update(ScanDescription scanDescription) {
		for (Chain ch : scanDescription.getChains()) {
			for (ScanModule sm : ch.getScanModules()) {
				for (Axis axis : sm.getAxes()) {
					this.defaults.updateAxis(DefaultsManager
							.getDefaultsAxis(axis));
				}
				for (Channel channel : sm.getChannels()) {
					this.defaults.updateChannel(DefaultsManager
							.getDefaultsChannel(channel));
				}
			}
		}
	}
}