/*
 * Copyright (c) 2001, 2007 Physikalisch-Technische Bundesanstalt.
 * All rights reserved.
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 */
package de.ptb.epics.eve.data.measuringstation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener;

/**
 * This class is managing all purposes, that have to do with the measuring 
 * station description. A <code>MeasuringStation</code> is holding all 
 * instances of PlugIns, Devices, DetectorChannels and so on and provides 
 * methods for finding the element. It could be seen as the root of a tree, 
 * which describes the measuring station.
 * 
 * @author Stephan Rehfeld <stephan.rehfeld( -at -) ptb.de>
 * @version 1.2
 */
public class MeasuringStation implements IMeasuringStation {

	/**
	 * The file version of the measuring station description.
	 */
	private String version;
	
	/**
	 * The filename where this measuring station is loaded from. 
	 */
	private String loadedFileName;
	
	/**
	 * The name of the schema file that was used for the loading of the 
	 * measuring station description.
	 * 
	 */
	private String schemaFileName;
	
	// TODO
	// we have a List and a Map for events, plugins, channels, axes
	// they are not necessarily equal, (maps are unique) 
	// remove lists and use only maps
	
	/**
	 * A <code>List</code>, that is holding all events.
	 */
	private List<Event> events;
	
	/**
	 * A <code>List</code>, that is holding all plug ins.
	 */
	private final List<PlugIn> plugins;
	
	/**
	 * A Selection object, that describes all selections, that are available at 
	 * the measuring station.
	 */
	private final Selections selections;
	
	/**
	 * A <code>List</code>, that is holding all devices.
	 */
	private final List<Device> devices;
	
	/**
	 * A <code>List</code>, that is holding all motors.
	 */
	private final List<Motor> motors;
	
	/**
	 * A <code>List</code>, that is holding all detectors.
	 */
	private final List<Detector> detectors;
	
	/**
	 * A <code>Map</code>, that makes all PlugIns available by their names.
	 */
	private final Map<String, PlugIn> pluginsMap;
	
	/**
	 * A <code>Map</code>, that makes all motor axis available by their id.
	 */
	private final Map<String, MotorAxis> motorAxisMap;
	
	/**
	 * A <code>Map</code>. that makes all detector channels available by their 
	 * id.
	 */
	private final Map<String, DetectorChannel> detectorChannelsMap;
	
	/**
	 * A <code>Map</code>, that makes all events available by their id.
	 */
	private final Map<String, Event> eventsMap;
	
	/**
	 * A <code>Map</code>, that makes all 
	 * <code>AbstractPrePostscanDevice</code>s available by their id.
	 */
	private final Map<String, AbstractPrePostscanDevice> prePostscanDeviceMap;

	private Map<String, List<AbstractDevice>> classMap;

	/**
	 * Constructs a <code>MeasuringStation</code> that contains
	 * no sub elements like devices or motor axis. It provides an own 
	 * <code>Selections</code> object.
	 */
	public MeasuringStation() {
		this.events = new ArrayList<Event>();
		this.plugins = new ArrayList<PlugIn>();
		this.devices = new ArrayList<Device>();
		this.motors = new ArrayList<Motor>();
		this.detectors = new ArrayList<Detector>();
		this.selections = new Selections();
		this.pluginsMap = new HashMap<String, PlugIn>();
		this.motorAxisMap = new HashMap<String, MotorAxis>();
		this.detectorChannelsMap = new HashMap<String, DetectorChannel>();
		this.prePostscanDeviceMap = 
					new HashMap<String, AbstractPrePostscanDevice>();
		this.classMap = new HashMap<String, List<AbstractDevice>>();
		this.eventsMap = new HashMap<String, Event>();
		
	}
	
	/**
	 * Getter for detectors.
	 * 
	 * @return a <code>List&lt;Detector&gt;</code> containing the detectors of 
	 * 			the <code>MeasuringStation</code>
	 */
	public List<Detector> getDetectors() {
		return new ArrayList<Detector>(this.detectors);
	}

	/**
	 * Getter for devices.
	 * 
	 * @return a <code>List&lt;Device&gt;</code> containing the devices of the 
	 * 			<code>MeasuringStation</code>
	 */
	public List<Device> getDevices() {
		return new ArrayList<Device>(this.devices);
	}
	
	/**
	 * Getter for events.
	 * 
	 * @return <code>List&lt;Event&gt;</code> containing the events of the 
	 * 			<code>MeasuringStation</code>
	 */
	public List<Event> getEvents() {
		return new ArrayList<Event>(this.events);
	}

	/**
	 * Getter for motors.
	 * 
	 * @return a <code>List&lt;Motor&gt;</code> containing the motors of the 
	 * 			<code>MeasuringStation</code>
	 */
	public List<Motor> getMotors() {
		return new ArrayList<Motor>(this.motors);
	}

	/**
	 * Getter for plugins.
	 * 
	 * @return a <code>List&lt;PlugIn&gt;</code> containing the plug ins of the 
	 * 			<code>MeasuringStation</code>
	 */
	public List<PlugIn> getPlugins() {
		return new ArrayList<PlugIn>(this.plugins);
	}
	
	/**
	 * Getter for selections.
	 * 
	 * @return the <code>Selections</code> of the <code>MeasuringStation</code>
	 */
	public Selections getSelections() {
		return this.selections;
	}

	/**
	 * Getter for version.
	 * 
	 * @return the version of the <code>MeasuringStation</code>
	 */
	public String getVersion() {
		return this.version;
	}

	/**
	 * Getter for loadedFileName.
	 *
	 * @return the file name at load time of the <code>MeasuringStation</code>
	 */
	public String getLoadedFileName(){
		return loadedFileName;
	}
	
	/**
	 * Setter for loadedFileName.
	 * 
	 * @param filename the file name that should be set (after successfully 
	 * 		   loading the file)
	 */
	public void setLoadedFileName(String filename){
		loadedFileName = filename;
	}

	/**
	 * Getter for schemaFileName.
	 * 
	 * @return the file name of the schema of the <code>MeasuringStation</code>
	 */
	public String getSchemaFileName(){
		return schemaFileName;
	}
	
	/**
	 * Setter for schemaFileName.
	 * 
	 * @param filename the schema file name that should be used
	 */
	public void setSchemaFileName(String filename){
		schemaFileName = filename;
	}

	/**
	 * Adds a <code>Detector</code> to the measuring station.
	 * 
	 * @param detector the <code>Detector</code> that should be added.
	 * @return <code>true</code> if the detector was added, 
	 * 			<code>false</code> otherwise.
	 */
	public boolean add(final Detector detector) {
		classMapAdd(detector.getClassName(), detector);
		return detectors.add(detector);
	}

	/**
	 * Adds a <code>Device</code> to the measuring station. 
	 * 
	 * @param the <code>Device</code> that should be added.
	 * @return <code>true</code> if the device was added, 
	 * 			<code>false</code> otherwise
	 * @throws IllegalArgumentException if the argument is <code>null</code>
	 */
	public boolean add(final Device device) {
		if(device == null) {
			throw new IllegalArgumentException(
					"The parameter 'device' must not be null!");
		}
		classMapAdd(device.getClassName(), device);
		this.prePostscanDeviceMap.put(device.getID(), device);
		return devices.add(device);
	}

	/**
	 * Adds an <code>Event</code> to the measuring station. 
	 * 
	 * @param event the <code>Event</code> that should be added.
	 * @return <code>true</code> if the event was added, 
	 * 			<code>false</code> otherwise.
	 * @throws IllegalArgumentException if the argument is <code>null</code>.
	 */
	public boolean add(final Event event) {
		if(event == null) {
			throw new IllegalArgumentException(
					"The parameter 'event' must not be null!");
		}
		this.eventsMap.put(event.getID(), event);
		return events.add(event);
	}

	/**
	 * Adds a <code>Motor</code> to the measuring station. 
	 * 
	 * @param motor the <code>Motor</code> that should be added.
	 * @return <code>true</code> if the motor was added, 
	 * 			<code>false</code> otherwise.
	 */
	public boolean add(final Motor motor) {
		classMapAdd(motor.getClassName(), motor);
		return motors.add(motor);
	}
	
	/**
	 * Adds a <code>PlugIn</code> to the measuring station. 
	 * 
	 * @param plugIn the <code>PlugIn</code> that should be added.
	 * @return <code>true</code> if the plug in was added, 
	 * 			<code>false</code> otherwise.
	 * @throws IllegalArgumentException if the argument is <code>null</code>.
	 */
	public boolean add(final PlugIn plugIn) {
		if(plugIn == null) {
			throw new IllegalArgumentException(
					"The parameter 'plugIn' must not be null!");
		}
		this.pluginsMap.put(plugIn.getName(), plugIn);
		return plugins.add(plugIn);
	}
	
	/**
	 * Registers an <code>Option</code> at the measuring station. The 
	 * registration is necessary to assure that an option can be found quickly 
	 * by its id.
	 * 
	 * @param option the <code>Option</code> that should be registered.
	 * @throws IllegalArgumentException if the argument is <code>null</code>
	 */
	public void registerOption(final Option option) {
		if(option == null) {
			throw new IllegalArgumentException(
					"The parameter 'option' must not be null!");
		}
		this.prePostscanDeviceMap.put(option.getID(), option);
	}
	
	/**
	 * Register a <code>MotorAxis</code> at the measuring station. The 
	 * registration is necessary to assure that a motor axis can be found 
	 * quickly.
	 * 
	 * @param motorAxis the <code>MotorAxis</code> that should be registered.
	 * @throws IllegalArgumentException if the argument is <code>null</code>.
	 */
	public void registerMotorAxis(final MotorAxis motorAxis) {
		if(motorAxis == null) {
			throw new IllegalArgumentException(
					"The parameter 'motorAxis' must not be null!");
		}
		this.motorAxisMap.put(motorAxis.getID(), motorAxis);
	}
	
	/**
	 * Registers a <code>DetectorChannel</code> at the measuring station. The 
	 * registration is necessary to assure that a detector channel can be found 
	 * quickly.
	 * 
	 * @param detectorChannel the <code>DetectorChannel</code> that should be 
	 * 		   registered.
	 * @throws IllegalArgumentException if the argument is <code>null</code>.
	 */
	public void registerDetectorChannel(final DetectorChannel detectorChannel) {
		if(detectorChannel == null) {
			throw new IllegalArgumentException(
					"The parameter 'detectorChannel' must not be null!");
		}
		this.detectorChannelsMap.put(detectorChannel.getID(), detectorChannel);
	}

	/**
	 * Setter for version.
	 * 
	 * @param version the version that should be set. (defines which reader and 
	 * 		   writer are used for the description)
	 */
	public void setVersion(String version) {
		this.version = version; 
	}
	
	/**
	 * Returns the <code>PlugIn</code> with the given name.
	 * 
	 * @param name the name of the <code>PlugIn</code> that should be returned.
	 * @return the <code>PlugIn</code> with the given name.
	 */
	public PlugIn getPluginByName(final String name) {
		return this.pluginsMap.get(name);
	}
	
	/**
	 * Returns the <code>AbstractPrePostscanDevice</code> with the given id.
	 * 
	 * @param id the id of the <code>AbstractPrePostscanDevice</code> that 
	 * 		   should be returned.
	 * @return the <AbstractPrePostscanDevice</code> with the given id.
	 */
	public AbstractPrePostscanDevice getPrePostscanDeviceById(final String id) {
		return this.prePostscanDeviceMap.get(id);
	}
	
	/**
	 * Returns the <code>MotorAxis</code> with the given id.
	 * 
	 * @param id the id of the <code>MotorAxis</code> that should be returned.
	 * @return the <code>MotorAxis</code> with the given id.
	 */
	public MotorAxis getMotorAxisById(final String id) {
		return this.motorAxisMap.get(id);
	}
	
	/**
	 * Returns the <code>DetectorChannel</code> with the given id.
	 * 
	 * @param id the id of the <code>DetectorChannel</code> that should be 
	 * 			returned.
	 * @return the <code>DetectorChannel</code> with the given id.
	 */
	public DetectorChannel getDetectorChannelById(final String id) {
		return this.detectorChannelsMap.get(id);
	}
	
	/**
	 * Returns the <code>Event</code> with the given id.
	 * 
	 * @param id the id of the <code>Event</code> that should be returned.
	 * @return the <code>Event</code> with the given id.
	 */
	public Event getEventById(final String id) {
		return this.eventsMap.get(id);
	}
	
	/**
	 * Returns the identifiers of all enclosed motor axis'.
	 * 
	 * @return a <code>List</code> containing the identifiers of the motor axis'.
	 */
	public List<String> getAxisFullIdentifyer() {
		
		final List<String> identifier = new ArrayList<String>();
		final Iterator<Motor> motorIterator = this.motors.iterator();
		
		Motor currentMotor = null;
		MotorAxis currentAxis = null;
		Iterator<MotorAxis> axisIterator = null;
		while( motorIterator.hasNext() ) {
			currentMotor = motorIterator.next();
			axisIterator = currentMotor.axisIterator();
			while( axisIterator.hasNext() ) {
				currentAxis = axisIterator.next();
				int i = 0;
				for (Iterator<String> iterator = identifier.iterator(); iterator.hasNext();){
					final String test = iterator.next();
					if (currentAxis.getFullIdentifyer().compareToIgnoreCase(test) > 0 ){
						i++;
					}
					else
						break;
				}
				identifier.add(i, currentAxis.getFullIdentifyer());
			}
		}
		return identifier;
	}
	
	/**
	 * Returns the identifiers of all enclosed detector channels.
	 * 
	 * @return a <code>List</code> containing the identifiers of the detector  
	 * 			channels.
	 */
	public List<String> getChannelsFullIdentifyer() {
		List<String> identifier = new ArrayList<String>();
		Iterator<Detector> detectorIterator = this.detectors.iterator();
		
		while(detectorIterator.hasNext()) {
			Detector currentDetector = detectorIterator.next();
			Iterator<DetectorChannel> channelIterator = 
								currentDetector.channelIterator();
			while(channelIterator.hasNext()) {
				DetectorChannel currentChannel = channelIterator.next();
				int i = 0;
				for (Iterator<String> iterator = identifier.iterator(); iterator.hasNext();){
					final String test = iterator.next();
					if (currentDetector.getFullIdentifyer().compareToIgnoreCase(test) > 0 ){
						i++;
					}
					else
						break;
				}
				identifier.add(i, currentChannel.getFullIdentifyer());
			}
		}
		return identifier;
	}
	
	/**
	 * Returns the identifiers of all enclosed pre post scan devices.
	 * 
	 * @return a <code>List</code> containing the identifiers of the pre post 
	 * 			scan devices.
	 */
	public List<String> getPrePostScanDevicesFullIdentifyer() {
		List<String> identifier = new ArrayList<String>();
		Iterator<Device> deviceIterator = this.devices.iterator();
		while( deviceIterator.hasNext() ) {
			Device currentDevice = deviceIterator.next();
			int i = 0;
			for (Iterator<String> iterator = identifier.iterator(); iterator.hasNext();){
				final String test = iterator.next();
				if (currentDevice.getFullIdentifyer().compareToIgnoreCase(test) > 0 ){
					i++;
				}
				else
					break;
			}
			identifier.add(i, currentDevice.getFullIdentifyer());
		}
		
		Iterator<Motor> motorIterator = this.motors.iterator();
		while( motorIterator.hasNext() ) {
			Motor currentMotor = motorIterator.next();
			
			Iterator<Option> optionIterator = currentMotor.optionIterator();
			while( optionIterator.hasNext() ) {
				Option currentOption = optionIterator.next();
				int i = 0;
				for (Iterator<String> iterator = identifier.iterator(); iterator.hasNext();){
					final String test = iterator.next();
					if (currentOption.getFullIdentifyer().compareToIgnoreCase(test) > 0 ){
						i++;
					}
					else
						break;
				}
				identifier.add(i, currentOption.getFullIdentifyer());
			}
			
			Iterator<MotorAxis> axisIterator = currentMotor.axisIterator();
			while( axisIterator.hasNext() ) {
				MotorAxis currentAxis = axisIterator.next();
				
				Iterator<Option> optionIterator2 = currentAxis.getOptions().iterator();
				while( optionIterator2.hasNext() ) {
					Option currentOption = optionIterator2.next();
					int i = 0;
					for (Iterator<String> iterator = identifier.iterator(); iterator.hasNext();){
						final String test = iterator.next();
						if (currentOption.getFullIdentifyer().compareToIgnoreCase(test) > 0 ){
							i++;
						}
						else
							break;
					}
					identifier.add(i, currentOption.getFullIdentifyer());
				}
			}
		}
		
		Iterator<Detector> detectorIterator = this.detectors.iterator();
		while( detectorIterator.hasNext() ) {
			Detector currentDetector = detectorIterator.next();
			
			Iterator<Option> optionIterator = currentDetector.getOptions().iterator();
			while( optionIterator.hasNext() ) {
				Option currentOption = optionIterator.next();
				int i = 0;
				for (Iterator<String> iterator = identifier.iterator(); iterator.hasNext();){
					final String test = iterator.next();
					if (currentOption.getFullIdentifyer().compareToIgnoreCase(test) > 0 ){
						i++;
					}
					else
						break;
				}
				identifier.add(i, currentOption.getFullIdentifyer());
			}
			
			Iterator<DetectorChannel> channelsIterator = currentDetector.channelIterator();
			while( channelsIterator.hasNext() ) {
				DetectorChannel currentChannel = channelsIterator.next();
				
				Iterator<Option> optionIterator2 = currentChannel.getOptions().iterator();
				while( optionIterator2.hasNext() ) {
					Option currentOption = optionIterator2.next();
					int i = 0;
					for (Iterator<String> iterator = identifier.iterator(); iterator.hasNext();){
						final String test = iterator.next();
						if (currentOption.getFullIdentifyer().compareToIgnoreCase(test) > 0 ){
							i++;
						}
						else
							break;
					}
					identifier.add(i, currentOption.getFullIdentifyer());
				}
			}
		}
		return identifier;
	}
	
	/**
	 * Returns the <code>AbstractDevice</code> with the given identifier.
	 * 
	 * @param identifier the identifier of the <code>AbstractDevice</code> that 
	 * 		   should be returned.
	 * @return the <code>AbstractDevice</code> with the given identifier.
	 * @throws IllegalArgumentException if the argument is <code>null</code> or 
	 * 			an empty <code>String</code>.
	 */
	public AbstractDevice getAbstractDeviceByFullIdentifyer(
											final String identifier) {
		if(identifier == null) {
			throw new IllegalArgumentException(
					"The parameter 'identifier' must not be null!");
		} else if(identifier.equals("")) {
			throw new IllegalArgumentException(
					"The parameter 'identifier' must not be a empty string!");
		}
		
		Iterator<Motor> motorIterator = this.motors.iterator();
		while( motorIterator.hasNext() ) {
			final Motor currentMotor = motorIterator.next();
			if( currentMotor.getFullIdentifyer().equals( identifier ) ) {
				return currentMotor;
			} else {
				Iterator<Option> optionIterator = currentMotor.optionIterator();
				while( optionIterator.hasNext() ) {
					final Option currentOption = optionIterator.next();
					if( currentOption.getFullIdentifyer().equals( identifier ) ) {
						return currentOption;
					}
				}
				Iterator<MotorAxis> motorAxisIterator = currentMotor.axisIterator();
				while( motorAxisIterator.hasNext() ) {
					final MotorAxis currentMotorAxis = motorAxisIterator.next();
					if( currentMotorAxis.getFullIdentifyer().equals( identifier ) ) {
						return currentMotorAxis;
					}
					Iterator<Option> optionIterator2 = currentMotorAxis.getOptions().iterator();
					while( optionIterator2.hasNext() ) {
						final Option currentOption = optionIterator2.next();
						if( currentOption.getFullIdentifyer().equals( identifier ) ) {
							return currentOption;
						}
					}
				}
			}
		}
		
		Iterator<Detector> detectorIterator = this.detectors.iterator();
		while( detectorIterator.hasNext() ) {
			final Detector currentDetector = detectorIterator.next();
			if( currentDetector.getFullIdentifyer().equals( identifier ) ) {
				return currentDetector;
			} else {
				Iterator<Option> optionIterator = currentDetector.getOptions().iterator();
				while( optionIterator.hasNext() ) {
					final Option currentOption = optionIterator.next();
					if( currentOption.getFullIdentifyer().equals( identifier ) ) {
						return currentOption;
					}
				}
				Iterator<DetectorChannel> detectorChannelIterator = currentDetector.channelIterator();
				while( detectorChannelIterator.hasNext() ) {
					final DetectorChannel currentDetectorChannel = detectorChannelIterator.next();
					if( currentDetectorChannel.getFullIdentifyer().equals( identifier ) ) {
						return currentDetectorChannel;
					}
					Iterator<Option> optionIterator2 = currentDetectorChannel.getOptions().iterator();
					while( optionIterator2.hasNext() ) {
						final Option currentOption = optionIterator2.next();
						if( currentOption.getFullIdentifyer().equals( identifier ) ) {
							return currentOption;
						}
					}
				}
			}
		}
		
		Iterator<Device> deviceIterator = this.devices.iterator();
		while( deviceIterator.hasNext() ) {
			final Device currentDevice = deviceIterator.next();
			if( currentDevice.getFullIdentifyer().equals( identifier ) ) {
				return currentDevice;
			}
		}
		
		return null;
	}
	
	/**
	 * add an abstract device with class name to the class hash
	 * 
	 * @param className
	 * @param absdevice
	 */
	private void classMapAdd(String className, AbstractDevice absdevice) {
		// TODO Explain !!!		
		if( absdevice instanceof Motor ) {
			final List< MotorAxis> axis = ((Motor)absdevice).getAxis();
			for( final MotorAxis a : axis ) {
				this.classMapAdd( a.getClassName(), a );
			}
		} else if( absdevice instanceof Detector ) {
			final List< DetectorChannel > channels = ((Detector)absdevice).getChannels();
			for( final DetectorChannel c : channels ) {
				this.classMapAdd( c.getClassName(), c );
			}
		} 
		
		if ((className == null) || className.length() < 1) 
			return;
		
		List<AbstractDevice> adlist;
		if (classMap.containsKey(className)){
			adlist = classMap.get(className);
		}
		else {
			adlist = new ArrayList<AbstractDevice>();
		}
		adlist.add(absdevice);
		classMap.put(className, adlist);
	}
	
	/**
	 * 
	 *  
	 * @see de.ptb.epics.eve.data.measuringstation.IMeasuringStation#getClassNameList()
	 */
	public Set<String> getClassNameList(){
		// TODO Explain !
		return classMap.keySet();
	}

	/**
	 *
	 *  
	 * @see de.ptb.epics.eve.data.measuringstation.IMeasuringStation#getDeviceList(java.lang.String)
	 */
	public List<AbstractDevice> getDeviceList(String classname){
		// TODO Explain !
		if (classMap.containsKey(classname))
			return classMap.get(classname);
		else
			return null;
	}

	/**
	 * 
	 * @return <code>false</code>
	 */
	public boolean addModelUpdateListener(
			// TODO Explain !
				final IModelUpdateListener modelUpdateListener) {
		return false;
	}

	/**
	 * 
	 * @return <code>false</code>
	 */
	public boolean removeModelUpdateListener(
			// TODO Explain !
				final IModelUpdateListener modelUpdateListener) {
		return false;
	}
}