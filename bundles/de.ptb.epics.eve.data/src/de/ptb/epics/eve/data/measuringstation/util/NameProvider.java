package de.ptb.epics.eve.data.measuringstation.util;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import de.ptb.epics.eve.data.measuringstation.AbstractDevice;
import de.ptb.epics.eve.data.measuringstation.AbstractPrePostscanDevice;
import de.ptb.epics.eve.data.measuringstation.DetectorChannel;
import de.ptb.epics.eve.data.measuringstation.IMeasuringStation;
import de.ptb.epics.eve.data.measuringstation.MotorAxis;
import de.ptb.epics.eve.data.measuringstation.processors.MeasuringStationLoader;

/**
 * NameProvider translates ids into names for different types in a device definition.
 * If the given device definition could not be loaded the given id strings are returned without modification.
 * 
 * @author Marcus Michalsky
 * @since 1.27
 */
public class NameProvider {
	private static final Logger LOGGER = Logger.getLogger(NameProvider.class.getName());
	
	private IMeasuringStation measuringStation;
	
	/**
	 * Creates a name provider based on the given device definition (and schema).
	 * An object representation of the given XML file is created by parsing it.
	 * If device definition is null all translation methods behave like an identity function.
	 * 
	 * @param deviceDefinition
	 * @param schema
	 */
	public NameProvider(File deviceDefinition, File schema) {
		if (deviceDefinition == null) {
			this.measuringStation = null;
			return;
		}
		MeasuringStationLoader measuringStationLoader = new MeasuringStationLoader(schema);
		try {
			measuringStationLoader.load(deviceDefinition);
		} catch (IOException e) {
			LOGGER.warn(e.getMessage(), e);
			LOGGER.warn("Name Provider will behave like an identity function. No translation possibile");
		} catch (SAXException e) {
			LOGGER.warn(e.getMessage(), e);
			LOGGER.warn("Name Provider will behave like an identity function. No translation possibile");
		} catch (ParserConfigurationException e) {
			LOGGER.warn(e.getMessage(), e);
			LOGGER.warn("Name Provider will behave like an identity function. No translation possibile");
		}
		this.measuringStation = measuringStationLoader.getMeasuringStation();
	}
	
	/**
	 * Returns a string containing the name of the motor axis with the given id appended by the id in parenthesis.
	 * @param id the id of the motor axis to be translated
	 * @return a string containing the name of the motor axis with the given id appended by the id in parenthesis
	 */
	public String translateMotorAxisId(String id) {
		if (this.measuringStation == null) {
			return id;
		}
		MotorAxis motorAxis = measuringStation.getMotorAxisById(id);
		if (motorAxis == null) {
			return id;
		}
		return motorAxis.getName() + " (" + id + ")";
	}
	
	/**
	 * Returns a string containing the name of the detector channel with the given id appended by the id in parenthesis.
	 * @param id the id of the detector channel to be translated
	 * @return a string containing the name of the detector channel with the given id appended by the id in parenthesis
	 */
	public String translateDetectorChannelId(String id) {
		if (this.measuringStation == null) {
			return id;
		}
		DetectorChannel channel = measuringStation.getDetectorChannelById(id);
		if (channel == null) {
			return id;
		}
		return channel.getName() + " (" + id + ")";
	}
	
	/**
	 * Returns a string containing the name of the (pre/postscan) device with the given id appended by the id in 
	 * parenthesis.
	 * @param id the id of the (pre/postscan) device to be translated
	 * @return a string containing the name of the (pre/postscan) device with the given id appended by the id in 
	 * 		parenthesis
	 */
	public String translatePrePostScanDeviceId(String id) {
		if (this.measuringStation == null) {
			return id;
		}
		AbstractPrePostscanDevice device = measuringStation.getPrePostscanDeviceById(id);
		if (device == null) {
			return id;
		}
		return device.getName() + " (" + id + ")";
	}
	
	/**
	 * @since 1.36
	 * @param id
	 * @return
	 */
	public String translateAbstractDeviceId(String id) {
		if (this.measuringStation == null) {
			return id;
		}
		AbstractDevice device = measuringStation.getAbstractDeviceById(id);
		if (device == null) {
			return id;
		}
		return device.getName() + " (" + id + ")";
	}
}