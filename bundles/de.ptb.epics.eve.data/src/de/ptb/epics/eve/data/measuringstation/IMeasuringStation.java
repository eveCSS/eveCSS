package de.ptb.epics.eve.data.measuringstation;

import java.util.List;
import java.util.Map;
import java.util.Set;

import de.ptb.epics.eve.data.measuringstation.event.Event;
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
	List<Detector> getDetectors();

	/**
	 * Returns a {@link java.util.List} of available 
	 * 			{@link de.ptb.epics.eve.data.measuringstation.Device}s.
	 * 
	 * @return a {@link java.util.List} of available
	 * 			{@link de.ptb.epics.eve.data.measuringstation.Device}s
	 */
	List<Device> getDevices();

	/**
	 * Returns a {@link java.util.List} of available 
	 * 			{@link de.ptb.epics.eve.data.measuringstation.event.Event}s.
	 * 
	 * @return a {@link java.util.List} of available 
	 * 			{@link de.ptb.epics.eve.data.measuringstation.event.Event}s
	 */
	List<Event> getEvents();

	/**
	 * Returns a {@link java.util.List} of available
	 * 			{@link de.ptb.epics.eve.data.measuringstation.Motor}s.
	 * @return a {@link java.util.List} of available
	 * 			{@link de.ptb.epics.eve.data.measuringstation.Motor}s
	 */
	List<Motor> getMotors();

	/**
	 * Returns a {@link java.util.List} of available 
	 * 			{@link de.ptb.epics.eve.data.measuringstation.PlugIn}s.
	 * 
	 * @return a {@link java.util.List} of available
	 * 			{@link de.ptb.epics.eve.data.measuringstation.PlugIn}s
	 */
	List<PlugIn> getPlugins();

	/**
	 * Returns the version of the measuring station description.
	 * 
	 * @return the version of the measuring station or <code>null</code> if 
	 * 		   none is set
	 */
	String getVersion();

	/**
	 * Returns the file name containing the description of the measuring 
	 * station.
	 * 
	 * @return the file name containing the description of the measuring station
	 */
	String getLoadedFileName();

	/**
	 * Returns the file name of the schema file used to validate the measuring 
	 * station description.
	 * 
	 * @return the file name of the schema file used for validation
	 */
	String getSchemaFileName();

	/**
	 * Returns the name of the measuring station.
	 * 
	 * @return the name of the measuring station
	 */
	String getName();
	
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
	PlugIn getPluginByName(final String name);

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
	AbstractPrePostscanDevice getPrePostscanDeviceById(
			final String id);

	/**
	 * Returns the {@link de.ptb.epics.eve.data.measuringstation.AbstractDevice}
	 * with the given <code>id</code> or <code>null</code> if none.
	 * 
	 * @param id the id of the device
	 * @return the device with the given id
	 */
	public AbstractDevice getAbstractDeviceById(String id);
	
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
	MotorAxis getMotorAxisById(final String id);

	/**
	 * Returns the motor axis with the given name (first occurrence).
	 * 
	 * @param name the name of the motor axis
	 * @return the motor axis with the given name (first occurrence)
	 * @since 1.25.2
	 */
	MotorAxis getMotorAxisByName(final String name);
	
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
	DetectorChannel getDetectorChannelById(final String id);

	/**
	 * Returns the {@link de.ptb.epics.eve.data.measuringstation.event.Event} with 
	 * the given id.
	 * 
	 * @param id the id of the 
	 * 		{@link de.ptb.epics.eve.data.measuringstation.event.Event} that should be 
	 * 		returned
	 * @return the {@link de.ptb.epics.eve.data.measuringstation.event.Event} with 
	 * 		   the given id or <code>null</code> if not found
	 */
	Event getEventById(final String id);

	/**
	 * Returns a {@link java.util.List} of axis identifiers of the measuring 
	 * station.
	 * 
	 * @return A {@link java.util.List} of axis identifiers of the measuring
	 * 		   station.
	 */
	List<String> getAxisFullIdentifyer();

	/**
	 * Returns a {@link java.util.Map} of motor axis of the measuring 
	 * station.
	 * 
	 * @return A {@link java.util.Map} of motor axis of the measuring
	 * 		   station.
	 */
	Map<String, MotorAxis> getMotorAxes();

	/**
	 * Returns a {@link java.util.List} of channel identifiers of the measuring 
	 * station.
	 * 
	 * @return a {@link java.util.List} of channel identifiers of the measuring 
	 * 			station.
	 */
	List<String> getChannelsFullIdentifyer();

	/**
	 * Returns a {@link java.util.List} of pre/post-scan identifiers of the 
	 * measuring station.
	
	 * @return a {@link java.util.List} of pre/post-scan identifiers of the 
	 * 			measuring station.
	 */
	List<String> getPrePostScanDevicesFullIdentifyer();

	/**
	 * Returns the {@link de.ptb.epics.eve.data.measuringstation.AbstractDevice} 
	 * with the given identifier.
	 * 
	 * @param identifier the identifier of the
	 * 		{@link de.ptb.epics.eve.data.measuringstation.AbstractDevice} that 
	 * 		should be returned. 
	 * @return the {@link de.ptb.epics.eve.data.measuringstation.AbstractDevice} 
	 * 			with the given identifier or <code>null</code> if not found.
	 */
	AbstractDevice getAbstractDeviceByFullIdentifyer(final String identifier);

	/**
	 * Returns a {@link java.util.Set} of available class names.
	 * 
	 * @return a {@link java.util.Set} of available class names
	 */
	Set<String> getClassNameList();

	/**
	 * Returns a {@link java.util.List} of devices available in the class with 
	 * the given class name. 
	 * 
	 * @param classname the class name whose available devices should be 
	 * 		  returned.
	 * @return a {@link java.util.List} of devices available in the class with 
	 * 		   the given class name
	 */
	List<AbstractDevice> getDeviceList(String classname);

	/**
	 * Returns all options marked with monitor=true in the device definition.
	 * 
	 * @return all options marked with monitor=true
	 */
	List<Option> getMonitorOptions();
}