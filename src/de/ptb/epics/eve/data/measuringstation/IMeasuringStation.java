/*
 * Copyright (c) 2001, 2007 Physikalisch-Technische Bundesanstalt.
 * All rights reserved.
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 */
package de.ptb.epics.eve.data.measuringstation;

import java.util.List;
import java.util.Set;

import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateProvider;

public interface IMeasuringStation extends IModelUpdateProvider {

	/**
	 * Returns a copy of the internal list, that is holding all detector 
	 * channels.
	 * 
	 * @return a <code>List&lt;Detector&gt;</code>
	 */
	public abstract List<Detector> getDetectors();

	/**
	 * Returns a copy of the internal list, that is holding all devices.
	 * 
	 * @return a <code>List&lt;Device&gt;</code>
	 */
	public abstract List<Device> getDevices();

	/**
	 * Returns a copy of the internal list, that is holding all events.
	 * 
	 * @return A <code>List&lt;Event&gt;</code> object.
	 */
	public abstract List<Event> getEvents();

	/**
	 * Returns a copy of the internal list, that is holding all motors.
	 * 
	 * @return A <code>List&lt;Motor&gt;</code>
	 */
	public abstract List<Motor> getMotors();

	/**
	 * Returns a copy of the internal list, that is holding all plug ins.
	 * 
	 * @return A <code>List&lt;PlugIn&gt;</code>
	 */
	public abstract List<PlugIn> getPlugins();

	/**
	 * Returns the <code>Selections</code> of the measuring station.
	 * 
	 * @return A Selections object.
	 */
	public abstract Selections getSelections();

	/**
	 * Returns the version of the measuring station description.
	 * 
	 * @return A <code>String</code> containing the version of the measuring 
	 * 			station description or null.
	 */
	public abstract String getVersion();

	/**
	 * 
	 * @return the name of the loaded device definition or null
	 */
	public abstract String getLoadedFileName();

	/**
	 * 
	 * @return the currently used schema file
	 */
	public abstract String getSchemaFileName();

	/**
	 * Returns the PlugIn object for the corresponding name.
	 * 
	 * @param name The name of the PlugIn.
	 * @return A PlugIn or null if the PlugIn was not found.
	 */
	public abstract PlugIn getPluginByName(final String name);

	/**
	 * Returns the <code>AbstractPrePostscanDevice</code> of the corresponding 
	 * id.
	 * 
	 * @param id a <code>String</code> containing the id of the device.
	 * @return an <code>AbstractPrePostscanDevice</code> or null if the 
	 * 			<code>AbstractPrePostscanDevice</code> was not found.
	 */
	public abstract AbstractPrePostscanDevice getPrePostscanDeviceById(
			final String id);

	/**
	 * Returns the <code>MotorAxis</code> of the corresponding id.
	 * 
	 * @param id a <code>String</code> containing the id of the motor axis.
	 * @return a <code>MotorAxis</code> or null if motor axis was not found.
	 */
	public abstract MotorAxis getMotorAxisById(final String id);

	/**
	 * Returns the <code>DetectorChannel</code> for the corresponding id.
	 * 
	 * @param id a <code>String</code> containing the id of the detector 
	 * 		   channel.
	 * @return a <code>DetectorChannel</code> or null if detector channel was 
	 * 			not found.
	 */
	public abstract DetectorChannel getDetectorChannelById(final String id);

	/**
	 * Returns the <code>Event</code> of the corresponding id.
	 * 
	 * @param id a <code>String</code> containing the id of the event.
	 * @return an <code>Event</code> or null if event was not found.
	 */
	public abstract Event getEventById(final String id);

	/**
	 * Returns a <code>List</code> of all full identifiers of the axis inside
	 * of this measuring station.
	 * 
	 * @return A <code>List</code> that contains the full identifiers of the 
	 * 			axis.
	 */
	public abstract List<String> getAxisFullIdentifyer();

	/**
	 * Returns a <code>List</code> of all full identifiers of the detector 
	 * channels inside of this measuring station.
	 * 
	 * @return a <code>List</code> that contains the full identifiers of the 
	 * 			detector channels.
	 */
	public abstract List<String> getChannelsFullIdentifyer();

	/**
	 * Returns a list of all full identifiers of the pre and post scan devices 
	 * inside of the measuring station.
	
	 * @return a <code>List</code> that contains the full identifiers of the 
	 * 			pre and post scan devices.
	 */
	public abstract List<String> getPrePostScanDevicesFullIdentifyer();

	/**
	 * Returns the corresponding device to a full identifier.
	 * 
	 * @param identifier a <code>String</code> containing the identifier.
	 * @return a device or null if the device was not found.
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
	 * @return the list of abstract devices with class name or null 
	 */
	public abstract List<AbstractDevice> getDeviceList(String classname);

}