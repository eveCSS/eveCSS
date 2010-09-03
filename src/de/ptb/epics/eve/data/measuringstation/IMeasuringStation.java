package de.ptb.epics.eve.data.measuringstation;

import java.util.List;
import java.util.Set;

import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateProvider;

public interface IMeasuringStation extends IModelUpdateProvider {

	/**
	 * Gives back a copy of the internal list, that is holding all detector channels.
	 * 
	 * @return A List<Detector> object. Never returns null!
	 */
	public abstract List<Detector> getDetectors();

	/**
	 * Gives back a copy of the internal list, that is holding all devices.
	 * 
	 * @return A List<Device> object. Never returns null!
	 */
	public abstract List<Device> getDevices();

	/**
	 * Gives back a copy of the internal list, that is holding all events.
	 * 
	 * @return A List<Event> object. Never returns null!
	 */
	public abstract List<Event> getEvents();

	/**
	 * Gives back a copy of the internal list, that is holding all motors.
	 * 
	 * @return A List<Motor> object. Never returns null!
	 */
	public abstract List<Motor> getMotors();

	/**
	 * Gives back a copy of the internal list, that is holding all plugins.
	 * 
	 * @return A List<PlugIn> object. Never returns null!
	 */
	public abstract List<PlugIn> getPlugins();

	/**
	 * Gives back the Selections object of this measuring station.
	 * 
	 * @return A Selections object. Never giving back null.
	 */
	public abstract Selections getSelections();

	/**
	 * Gives back the version of the measuring station description.
	 * 
	 * @return A string object containing the version of the measuring station description or null.
	 */
	public abstract String getVersion();

	/**
	 * 
	 * @return the name of the loaded device definition or null
	 */
	public abstract String getLoadedFileName();

	/**
	 * 
	 * @return the currently used schema File
	 */
	public abstract String getSchemaFileName();

	/**
	 * Gives back the PlugIn object for the correpondenting name.
	 * 
	 * @param name The name of the PlugIn.
	 * @return A PlugIn or null if the PlugIn was not found.
	 */
	public abstract PlugIn getPluginByName(final String name);

	/**
	 * Gives back the AbstractPrePostscanDevice object for the correpondenting id.
	 * 
	 * @param id A String object, containing the id of the device.
	 * @return A AbstractPrePostscanDevice or null if the AbstractPrePostscanDevice was not found.
	 */
	public abstract AbstractPrePostscanDevice getPrePostscanDeviceById(
			final String id);

	/**
	 * Gives back the MotorAxis object for the correspondenting id.
	 * 
	 * @param id A String object, containing the id of the motor axis.
	 * @return A MotorAxis object or null if motor axis was not found.
	 */
	public abstract MotorAxis getMotorAxisById(final String id);

	/**
	 * Gives back the DetectorChannel object for the correspondenting id.
	 * 
	 * @param id A String object, containing the id of the detector channel.
	 * @return A DetectorChannel object or null if detector channel was not found.
	 */
	public abstract DetectorChannel getDetectorChannelById(final String id);

	/**
	 * Gives back the Event object for the correspondenting id.
	 * 
	 * @param id A String object, containing the id of the event.
	 * @return A Event object or null if event was not found.
	 */
	public abstract Event getEventById(final String id);

	/**
	 * Creates an give back a list of all full identifiers of the axis inside
	 * of this measuring station.
	 * 
	 * @return A List that contains the full identifiers of the axis. Never returns null.
	 */
	public abstract List<String> getAxisFullIdentifyer();

	/**
	 * Creates an give back a list of all full identifiers of the detector channels inside
	 * of this measuring station.
	 * 
	 * @return A List that contains the full identifiers of the detector channels. Never returns null.
	 */
	public abstract List<String> getChannelsFullIdentifyer();

	/**
	 * Creates an give back a list of all full identifiers of the pre- postscan devices inside
	 * of this measuring station.
	
	 * @return A List that contains the full identifiers of the pre- and postscan devices. Never returns null.
	 */
	public abstract List<String> getPrePostScanDevicesFullIdentifyer();

	/**
	 * Give back the correpondenting device to a full identifier.
	 * 
	 * @param identifier A String object containing the identifier. Must not be null or a empty string.
	 * @return A Device or null if the device was not found.
	 */
	public abstract AbstractDevice getAbstractDeviceByFullIdentifyer(
			final String identifier);

	/**
	 * 
	 * @return a String Array with all available class names
	 */
	public abstract Set<String> getClassNameList();

	/**
	 * 
	 * @param classname 
	 * @return the list of abstract devices with classname or null 
	 */
	public abstract List<AbstractDevice> getDeviceList(String classname);

}