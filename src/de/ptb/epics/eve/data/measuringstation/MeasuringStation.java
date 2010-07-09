/*******************************************************************************
 * Copyright (c) 2001, 2007 Physikalisch Technische Bundesanstalt.
 * All rights reserved.
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package de.ptb.epics.eve.data.measuringstation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class is managing all purposes, that have to do with the measuring station
 * description. A MeasuringStation-Objekt is hold all instances of PlugIns, Devices,
 * DetectorChannels and so on and provides methods for fast finding the element. It
 * could be seen as the root of a tree, wich descripes the measuring station.
 * 
 * @author Stephan Rehfeld <stephan.rehfeld( -at -) ptb.de>
 * @version 1.2
 */
public class MeasuringStation {

	/**
	 * The file version of the measuring station description.
	 */
	private String version;
	
	/**
	 * The filename where this measuring station is loaded from. 
	 */
	private String loadedFileName;
	
	/**
	 * The name of the schema file that was used for the loading of this measuring station description.
	 * 
	 */
	private String schemaFileName;
	
	// TODO
	// we have a List and a Map for events, plugins, channels, axes
	// they are not necessarily equal, (maps are unique) 
	// remove lists and use only maps
	
	/**
	 * A List, that is holding all events.
	 */
	private List<Event> events;
	
	/**
	 * A List, that is holding all plugins.
	 */
	private final List<PlugIn> plugins;
	
	/**
	 * A Selection object, that descripes all selections, that are available at the measuring station.
	 */
	private final Selections selections;
	
	/**
	 * A List, that is holding all devices.
	 */
	private final List<Device> devices;
	
	/**
	 * A List, that is holding all motors.
	 */
	private final List<Motor> motors;
	
	/**
	 * A List, that is holding all detectors.
	 */
	private final List<Detector> detectors;
	
	/**
	 * A Map, that makes all PlugIns available by their names.
	 */
	private final Map< String, PlugIn> pluginsMap;
	
	/**
	 * A Map, that makes all motor axis available by their ids.
	 */
	private final Map< String, MotorAxis> motorAxisMap;
	
	/**
	 * A Map. that makes all detector channels avaiable by their ids.
	 */
	private final Map< String, DetectorChannel> detectorChannelsMap;
	
	/**
	 * A Map, that makes all events available by their ids.
	 */
	private final Map< String, Event > eventsMap;
	
	/**
	 * A Map, that makes all AbstractPrePostscanDevices available by their ids.
	 */
	private final Map< String, AbstractPrePostscanDevice > prePostscanDeviceMap;

	private Map<String, List<AbstractDevice>> classMap;

	/**
	 * This constructor constructs a new MeasuringStation object, that contains
	 * no subelements like devices or motor axis. It provides a own Selections
	 * object.
	 *
	 */
	public MeasuringStation() {
		this.events = new ArrayList<Event>();
		this.plugins = new ArrayList<PlugIn>();
		this.devices = new ArrayList<Device>();
		this.motors = new ArrayList<Motor>();
		this.detectors = new ArrayList<Detector>();
		this.selections = new Selections();
		this.pluginsMap = new HashMap< String, PlugIn>();
		this.motorAxisMap = new HashMap< String, MotorAxis >();
		this.detectorChannelsMap = new HashMap< String, DetectorChannel >();
		this.prePostscanDeviceMap = new HashMap< String, AbstractPrePostscanDevice >();
		this.classMap = new HashMap<String, List<AbstractDevice>>();
		this.eventsMap = new HashMap< String, Event >();
		
	}
	
	/**
	 * Gives back a copy of the internal list, that is holding all detector channels.
	 * 
	 * @return A List<Detector> object. Never returns null!
	 */
	public List<Detector> getDetectors() {
		return new ArrayList<Detector>( this.detectors );
	}

	/**
	 * Gives back a copy of the internal list, that is holding all devices.
	 * 
	 * @return A List<Device> object. Never returns null!
	 */
	public List<Device> getDevices() {
		return new ArrayList<Device>( this.devices );
	}
	
	/**
	 * Gives back a copy of the internal list, that is holding all events.
	 * 
	 * @return A List<Event> object. Never returns null!
	 */
	public List<Event> getEvents() {
		return new ArrayList<Event>( this.events );
	}

	/**
	 * Gives back a copy of the internal list, that is holding all motors.
	 * 
	 * @return A List<Motor> object. Never returns null!
	 */
	public List<Motor> getMotors() {
		return new ArrayList<Motor>( this.motors );
	}

	/**
	 * Gives back a copy of the internal list, that is holding all plugins.
	 * 
	 * @return A List<PlugIn> object. Never returns null!
	 */
	public List<PlugIn> getPlugins() {
		return new ArrayList<PlugIn>( this.plugins );
	}
	
	/**
	 * Gives back the Selections object of this measuring station.
	 * 
	 * @return A Selections object. Never giving back null.
	 */
	public Selections getSelections() {
		return this.selections;
	}

	/**
	 * Gives back the version of the measuring station description.
	 * 
	 * @return A string object containing the version of the measuring station description or null.
	 */
	public String getVersion() {
		return this.version;
	}

	/**
	 * 
	 * @return the name of the loaded device definition or null
	 */
	public String getLoadedFileName(){
		return loadedFileName;
	}
	
	/**
	 * 
	 * @param filename set the file name (after successfully loading the file)
	 */
	public void setLoadedFileName(String filename){
		loadedFileName = filename;
	}

	/**
	 * 
	 * @return the currently used schema File
	 */
	public String getSchemaFileName(){
		return schemaFileName;
	}
	/**
	 * 
	 * @param filename set the used schema file name
	 */
	public void setSchemaFileName(String filename){
		schemaFileName = filename;
	}

	/**
	 * Adds a Detector to this measuring station. This method is just wrapping the
	 * add method of the List, that is holding the detectors.
	 * 
	 * @see java.util.Collection
	 * @param detector A detector object.
	 * @return
	 */
	public boolean add( final Detector detector ) {
		classMapAdd(detector.getClassName(), detector);
		return detectors.add( detector );
	}

	/**
	 * Adds a Device to this measuring station. This method is just wrapping the
	 * add method of the List, that is holding the devices.
	 * 
	 * @see java.util.Collection
	 * @param device a Device object. Must not be null.
	 * @return
	 */
	public boolean add( final Device device ) {
		if( device == null ) {
			throw new IllegalArgumentException( "The parameter 'device' must not be null!" );
		}
		classMapAdd(device.getClassName(), device);
		this.prePostscanDeviceMap.put( device.getID(), device );
		return devices.add( device );
	}

	/**
	 * Adds a Event to this measuring station. This method is wrapping the
	 * add method of the List, that is holding the events.
	 * 
	 * @see java.util.Collection
	 * @param event A Event object. Must not be null.
	 * @return
	 */
	public boolean add( final Event event) {
		if( event == null ) {
			throw new IllegalArgumentException( "The parameter 'event' must not be null!" );
		}
		this.eventsMap.put( event.getID(), event );
		return events.add( event );
	}

	/**
	 * Adds a Motor to this measuring station. This method is just wrapping the
	 * add method of the List, that is holding the motors.
	 * 
	 * @see java.util.Collection
	 * @param motor
	 * @return
	 */
	public boolean add( final Motor motor ) {
		classMapAdd(motor.getClassName(), motor);
		return motors.add( motor );
	}
	
	/**
	 * Adds a PlugIn to this measuring station. This method is just wrapping the
	 * add method of the List, that is holding the plug-ins.
	 * @param plugIn A PlugIn object. Must not be null.
	 * @return
	 */
	public boolean add( final PlugIn plugIn ) {
		if( plugIn == null ) {
			throw new IllegalArgumentException( "The parameter 'plugIn' must not be null!" );
		}
		this.pluginsMap.put( plugIn.getName(), plugIn );
		return plugins.add( plugIn );
	}
	
	/**
	 * Use this method to register a Option at this measuring station. The registration
	 * is necessary that a option can be found by it's id very fast.
	 * 
	 * @param option A Option object, must not be null.
	 */
	public void registerOption( final Option option ) {
		if( option == null ) {
			throw new IllegalArgumentException( "The parameter 'option' must not be null!" );
		}
		this.prePostscanDeviceMap.put( option.getID(), option );
	}
	
	/**
	 * Use this method to register a Option at this measuring station. The registration
	 * is necessary that a motor axis can be found very fast.
	 * 
	 * @param motorAxis A MotorAxis object. Must not be null.
	 */
	public void registerMotorAxis( final MotorAxis motorAxis ) {
		if( motorAxis == null ) {
			throw new IllegalArgumentException( "The parameter 'motorAxis' must not be null!" );
		}
		this.motorAxisMap.put( motorAxis.getID(), motorAxis );
	}
	
	/**
	 * Use this method to register a detector channel at his measuring station. The registration
	 * is necessary that a detector channel can be founs very fast.
	 * 
	 * @param detectorChannel A DetectorChannel object. Must not be null!
	 */
	public void registerDetectorChannel( final DetectorChannel detectorChannel ) {
		if( detectorChannel == null ) {
			throw new IllegalArgumentException( "The parameter 'detectorChannel' must not be null!" );
		}
		this.detectorChannelsMap.put( detectorChannel.getID(), detectorChannel );
	}

	/**
	 * Sets the version of this measuring station description. The versioning is used to
	 * find out which reader and writer should be used for this description.
	 * 
	 * @param version A String object.
	 */
	public void setVersion(String version) {
		this.version = version; 
	}
	
	/**
	 * Gives back the PlugIn object for the correpondenting name.
	 * 
	 * @param name The name of the PlugIn.
	 * @return A PlugIn or null if the PlugIn was not found.
	 */
	public PlugIn getPluginByName( final String name ) {
		return this.pluginsMap.get( name );
	}
	
	/**
	 * Gives back the AbstractPrePostscanDevice object for the correpondenting id.
	 * 
	 * @param id A String object, containing the id of the device.
	 * @return A AbstractPrePostscanDevice or null if the AbstractPrePostscanDevice was not found.
	 */
	public AbstractPrePostscanDevice getPrePostscanDeviceById( final String id ) {
		return this.prePostscanDeviceMap.get( id );
	}
	
	/**
	 * Gives back the MotorAxis object for the correspondenting id.
	 * 
	 * @param id A String object, containing the id of the motor axis.
	 * @return A MotorAxis object or null if motor axis was not found.
	 */
	public MotorAxis getMotorAxisById( final String id ) {
		return this.motorAxisMap.get( id );
	}
	
	/**
	 * Gives back the DetectorChannel object for the correspondenting id.
	 * 
	 * @param id A String object, containing the id of the detector channel.
	 * @return A DetectorChannel object or null if detector channel was not found.
	 */
	public DetectorChannel getDetectorChannelById( final String id ) {
		return this.detectorChannelsMap.get( id );
	}
	
	/**
	 * Gives back the Event object for the correspondenting id.
	 * 
	 * @param id A String object, containing the id of the event.
	 * @return A Event object or null if event was not found.
	 */
	public Event getEventById( final String id ) {
		return this.eventsMap.get( id );
	}
	
	/**
	 * Creates an give back a list of all full identifiers of the axis inside
	 * of this measuring station.
	 * 
	 * @return A List that contains the full identifiers of the axis. Never returns null.
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
	 * Creates an give back a list of all full identifiers of the detector channels inside
	 * of this measuring station.
	 * 
	 * @return A List that contains the full identifiers of the detector channels. Never returns null.
	 */
	public List<String> getChannelsFullIdentifyer() {
		List<String> identifier = new ArrayList<String>();
		Iterator<Detector> detectorIterator = this.detectors.iterator();
		
		while( detectorIterator.hasNext() ) {
			Detector currentDetector = detectorIterator.next();
			Iterator<DetectorChannel> channelIterator = currentDetector.channelIterator();
			while( channelIterator.hasNext() ) {
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
 	 * Creates an give back a list of all full identifiers of the pre- postscan devices inside
	 * of this measuring station.

	 * @return A List that contains the full identifiers of the pre- and postscan devices. Never returns null.
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
	 * Give back the correpondenting device to a full identifier.
	 * 
	 * @param identifier A String object containing the identifier. Must not be null or a empty string.
	 * @return A Device or null if the device was not found.
	 */
	public AbstractDevice getAbstractDeviceByFullIdentifyer( final String identifier ) {
		if( identifier == null ) {
			throw new IllegalArgumentException( "The parameter 'identifier' must not be null!" );
		} else if( identifier.equals( "" ) ) {
			throw new IllegalArgumentException( "The parameter 'identifier' must not be a empty string!" );
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
	 * @param className
	 * @param absdevice
	 */
	private void classMapAdd(String className, AbstractDevice absdevice) {
		
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
	 * @return a String Array with all available class names
	 */
	public Set<String> getClassNameList(){
		return classMap.keySet();
	}

	/**
	 * 
	 * @param classname 
	 * @return the list of abstract devices with classname or null 
	 */
	public List<AbstractDevice> getDeviceList(String classname){
		if (classMap.containsKey(classname))
			return classMap.get(classname);
		else
			return null;
	}
}
