package de.ptb.epics.eve.data.measuringstation;

import java.util.List;
import java.util.Set;

import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateProvider;

/**
 * <code>IMeasuringStation</code> defines the methods to compose a measuring 
 * station.
 * 
 * @author ?
 * @author Marcus Michalsky
 */
public interface IMeasuringStation extends IModelUpdateProvider {

	/**
	 * Returns a {@link java.util.List} of available 
	 * 			{@link de.ptb.epics.eve.data.measuringstation.Detector}s.
	 * 
	 * @return a {@link java.util.List} of available 
	 * 			{@link de.ptb.epics.eve.data.measuringstation.Detector}s
	 */
	public abstract List<Detector> getDetectors();

	/**
	 * Returns a {@link java.util.List} of available 
	 * 			{@link de.ptb.epics.eve.data.measuringstation.Device}s.
	 * 
	 * @return a {@link java.util.List} of available
	 * 			{@link de.ptb.epics.eve.data.measuringstation.Device}s
	 */
	public abstract List<Device> getDevices();

	/**
	 * Returns a {@link java.util.List} of available 
	 * 			{@link de.ptb.epics.eve.data.measuringstation.Event}s.
	 * 
	 * @return a {@link java.util.List} of available 
	 * 			{@link de.ptb.epics.eve.data.measuringstation.Event}s
	 */
	public abstract List<Event> getEvents();

	/**
	 * Returns a {@link java.util.List} of available
	 * 			{@link de.ptb.epics.eve.data.measuringstation.Motor}s.
	 * @return a {@link java.util.List} of available
	 * 			{@link de.ptb.epics.eve.data.measuringstation.Motor}s
	 */
	public abstract List<Motor> getMotors();

	/**
	 * Returns a {@link java.util.List} of available 
	 * 			{@link de.ptb.epics.eve.data.measuringstation.PlugIn}s.
	 * 
	 * @return a {@link java.util.List} of available
	 * 			{@link de.ptb.epics.eve.data.measuringstation.PlugIn}s
	 */
	public abstract List<PlugIn> getPlugins();

	/**
	 * Returns the {@link de.ptb.epics.eve.data.measuringstation.Selections} of 
	 * the measuring station.
	 * 
	 * @return the {@link de.ptb.epics.eve.data.measuringstation.Selections}
	 */
	public abstract Selections getSelections();

	/**
	 * Returns the version of the measuring station description.
	 * 
	 * @return the version of the measuring station or <code>null</code> if 
	 * 		   none is set
	 */
	public abstract String getVersion();

	/**
	 * Returns the file name containing the description of the measuring 
	 * station.
	 * 
	 * @return the file name containing the description of the measuring station
	 */
	public abstract String getLoadedFileName();

	/**
	 * Returns the file name of the schema file used to validate the measuring 
	 * station description.
	 * 
	 * @return the file name of the schema file used for validation
	 */
	public abstract String getSchemaFileName();

	/**
	 * Returns the {@link de.ptb.epics.eve.data.measuringstation.PlugIn} with 
	 * the given name.
	 * 
	 * @param name the name of the 
	 * 		  {@link de.ptb.epics.eve.data.measuringstation.PlugIn} that should 
	 * 		  be returned.
	 * @return the {@link de.ptb.epics.eve.data.measuringstation.PlugIn} with 
	 * 		   the given name or <code>null> if not found
	 */
	public abstract PlugIn getPluginByName(final String name);

	/**
	 * Returns the 
	 * {@link de.ptb.epics.eve.data.measuringstation.AbstractPrePostscanDevice} 
	 * with the given id.
	 * 
	 * @param id the id of the 
	 * {@link de.ptb.epics.eve.data.measuringstation.AbstractPrePostscanDevice} 
	 *  		that should be returned
	 * @return the 
	 * {@link de.ptb.epics.eve.data.measuringstation.AbstractPrePostscanDevice} 
	 *  		with the given id or <code>null</code> if not found
	 */
	public abstract AbstractPrePostscanDevice getPrePostscanDeviceById(
			final String id);

	/**
	 * Returns the {@link de.ptb.epics.eve.data.measuringstation.MotorAxis} with 
	 * the given id.
	 * 
	 * @param id the id of the 
	 * 		 {@link de.ptb.epics.eve.data.measuringstation.MotorAxis} that 
	 * 		 should be returned
	 * @return the {@link de.ptb.epics.eve.data.measuringstation.MotorAxis}
	 * 		   with the given id or <code>null</code> if not found
	 */
	public abstract MotorAxis getMotorAxisById(final String id);

	/**
	 * Returns the 
	 * {@link de.ptb.epics.eve.data.measuringstation.DetectorChannel} with the 
	 * given id.
	 * 
	 * @param id the id of the 
	 * 		{@link de.ptb.epics.eve.data.measuringstation.DetectorChannel} that 
	 * 		should be returned
	 * @return the 
	 * 		{@link de.ptb.epics.eve.data.measuringstation.DetectorChannel} with 
	 * 		the given id or <code>null</code> if not found.
	 */
	public abstract DetectorChannel getDetectorChannelById(final String id);

	/**
	 * Returns the {@link de.ptb.epics.eve.data.measuringstation.Event} with 
	 * the given id.
	 * 
	 * @param id the id of the 
	 * 		{@link de.ptb.epics.eve.data.measuringstation.Event} that should be 
	 * 		returned
	 * @return the {@link de.ptb.epics.eve.data.measuringstation.Event} with 
	 * 		   the given id or <code>null</code> if not found
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
	 * Returns a list of all available class names.
	 * 
	 * @return a set of <code>String</code>s with all available class names
	 */
	public abstract Set<String> getClassNameList();

	/**
	 * Returns all devices available in a certain class with the specified 
	 * class name. 
	 * 
	 * @param classname the name of the class of interest 
	 * @return a list of abstract devices available in the given class 
	 */
	public abstract List<AbstractDevice> getDeviceList(String classname);
}