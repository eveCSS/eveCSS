package de.ptb.epics.eve.data.measuringstation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener;

/**
 * <code>MeasuringStation</code> is the model of a measuring station. It 
 * contains all elements available at a measuring station (e.g. motors, 
 * detectors) and defines their functionality.
 *  
 * @author Stephan Rehfeld <stephan.rehfeld( -at -) ptb.de>
 * @author Marcus Michalsky
 */
public class MeasuringStation implements IMeasuringStation {

	// the version of the measuring station description
	private String version;
	
	// the filename containing the measuring station that is loaded
	private String loadedFileName;
	
	// the name of the schema file used for validation of the measuring station
	private String schemaFileName;
	
	// the name of the measuring station (e.g., sx700, qnim, ...)
	private String name;
	
	// TODO
	// we have a List and a Map for events, plug ins, channels, axes
	// they are not necessarily equal, (maps are unique) 
	// remove lists and use only maps
	
	// a List containing available events
	private List<Event> events;
	
	// a List containing available plug ins
	private final List<PlugIn> plugins;
	
	// a List, that is holding all devices
	private final List<Device> devices;
	
	// a List, that is holding all motors
	private final List<Motor> motors;
	
	// a List, that is holding all detectors
	private final List<Detector> detectors;
	
	/*
	 * A Selection object, that describes all selections, that are available 
	 * at the measuring station.
	 */
	private final Selections selections;
	
	// a Map, that makes all PlugIns available by their names
	private final Map<String, PlugIn> pluginsMap;
	
	// a Map, that makes all motor axis available by their ids
	private final Map<String, MotorAxis> motorAxisMap;
	
	// a Map. that makes all detector channels available by their ids
	private final Map<String, DetectorChannel> detectorChannelsMap;
	
	// a Map, that makes all events available by their ids
	private final Map<String, Event> eventsMap;
	
	// a Map, that makes all AbstractPrePostscanDevices available by their ids
	private final Map<String, AbstractPrePostscanDevice> prePostscanDeviceMap;

	/* 
	 * ???
	 */
	private Map<String, List<AbstractDevice>> classMap;

	/**
	 * Constructs an empty <code>MeasuringStation</code>.
	 */
	public MeasuringStation() {
		this.events = new ArrayList<Event>();
		this.plugins = new ArrayList<PlugIn>();
		this.devices = new ArrayList<Device>();
		this.motors = new ArrayList<Motor>();
		this.detectors = new ArrayList<Detector>();
		this.selections = new Selections();
		this.pluginsMap = new HashMap< String, PlugIn>();
		this.motorAxisMap = new HashMap<String, MotorAxis>();
		this.detectorChannelsMap = new HashMap<String, DetectorChannel>();
		this.prePostscanDeviceMap = new HashMap<String, AbstractPrePostscanDevice>();
		this.classMap = new HashMap<String, List<AbstractDevice>>();
		this.eventsMap = new HashMap<String, Event>();	
	}
	
	// *************************
	
	/**
	 * Sets the version of the measuring station description.
	 * 
	 * @param version the version that should be set
	 */
	public void setVersion(String version) {
		this.version = version; 
	}
	
	/**
	 * Sets the name of the file used to load the measuring station description.
	 * 
	 * @param filename the name of the file
	 */
	public void setLoadedFileName(String filename) {
		loadedFileName = filename;
	}

	/**
	 * Sets the name of the schema file used to validate the measuring station 
	 * description.
	 * 
	 * @param filename the name of the schema file
	 */
	public void setSchemaFileName(String filename) {
		schemaFileName = filename;
	}

	/**
	 * Returns the name.
	 * 
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Sets the name.
	 * 
	 * @param name the name that should be set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	// *************************
	
	/**
	 * Adds a {@link de.ptb.epics.eve.data.measuringstation.Detector} 
	 * to the measuring station. 
	 * 
	 * @param detector the 
	 * 		  {@link de.ptb.epics.eve.data.measuringstation.Detector} that 
	 * 		  should be added. 
	 * @return <code>true</code> if the detector was added, 
	 * 		   <code>false</code> otherwise
	 */
	public boolean add(final Detector detector) {
		classMapAdd(detector.getClassName(), detector);
		return detectors.add(detector);
	}

	/**
	 * Adds a {@link de.ptb.epics.eve.data.measuringstation.Device} to the 
	 * measuring station. 
	 * 
	 * @param device the {@link de.ptb.epics.eve.data.measuringstation.Device} 
	 * 		  that should be added.
	 * @return <code>true</code> if the device was added,
	 * 		   <code>false</code> otherwise
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
	 * Adds an {@link de.ptb.epics.eve.data.measuringstation.Event} to the 
	 * measuring station. 
	 * 
	 * @param event the {@link de.ptb.epics.eve.data.measuringstation.Event} 
			  that should be added.
	 * @return <code>true</code> if the event was added, 
	 * 		   <code>false</code> otherwise
	 * @throws IllegalArgumentException if the argument is <code>null</code>
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
	 * Adds a {@link de.ptb.epics.eve.data.measuringstation.Motor} to the 
	 * measuring station. 
	 * 
	 * @param motor the {@link de.ptb.epics.eve.data.measuringstation.Motor} 
	 * 		  that should be added.
	 * @return <code>true</code> if the motor was added,
	 * 		   <code>false</code> otherwise
	 * @throws IllegalArgumentException if the argument is <code>null</code>
	 */
	public boolean add(final Motor motor) {
		if(motor == null) {
			throw new IllegalArgumentException(
					"The parameter 'motor' must not be null!");
		}
		classMapAdd(motor.getClassName(), motor);
		return motors.add(motor);
	}
	
	/**
	 * Adds a {@link de.ptb.epics.eve.data.measuringstation.PlugIn} to the 
	 * measuring station. 
	 * 
	 * @param plugIn the {@link de.ptb.epics.eve.data.measuringstation.PlugIn} 
	 * 		  that should be added.
	 * @return <code>true</code> if the plug in was added,
	 * 		   <code>false</code> otherwise
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
	
	// *************************

	/**
	 * Registers a {@link de.ptb.epics.eve.data.measuringstation.MotorAxis} at 
	 * the measuring station. An axis registered this way can later be 
	 * retrieved through {@link #getMotorAxisById(String)}. (Faster than 
	 * iterating through the list gained by {@link #getMotors()}.)
	 * 
	 * @param motorAxis the 
	 * 		  {@link de.ptb.epics.eve.data.measuringstation.MotorAxis} that 
	 * 		  should be registered
	 * @throws IllegalArgumentException if the argument is <code>null</code>
	 */
	public void registerMotorAxis(final MotorAxis motorAxis) {
		if(motorAxis == null) {
			throw new IllegalArgumentException(
					"The parameter 'motorAxis' must not be null!");
		}
		this.motorAxisMap.put(motorAxis.getID(), motorAxis);
	}
	
	/**
	 * Registers a {@link de.ptb.epics.eve.data.measuringstation.DetectorChannel} 
	 * at the measuring station. A channel registered this way can later be 
	 * retrieved through {@link #getDetectorChannelById(String)}. (Faster than 
	 * iterating through the list gained by {@link #getDetectors()}.)
	 * 
	 * @param detectorChannel the 
	 * 		  {@link de.ptb.epics.eve.data.measuringstation.DetectorChannel}  
	 * 		  that should be registered
	 * @throws IllegalArgumentException if the argument is <code>null</code>
	 */
	public void registerDetectorChannel(final DetectorChannel detectorChannel) {
		if(detectorChannel == null) {
			throw new IllegalArgumentException(
					"The parameter 'detectorChannel' must not be null!");
		}
		this.detectorChannelsMap.put(detectorChannel.getID(), detectorChannel);
	}

	/**
	 * Registers an {@link de.ptb.epics.eve.data.measuringstation.Option} at 
	 * the measuring station. An option registered this way can later be 
	 * retrieved through {@link #getPrePostscanDeviceById(String)}.
	 * 
	 * @param option the {@link de.ptb.epics.eve.data.measuringstation.Option} 
	 * 		  that should be registered
	 * @throws IllegalArgumentException if the argument is <code>null</code>
	 */
	public void registerOption(final Option option) {
		if(option == null) {
			throw new IllegalArgumentException(
					"The parameter 'option' must not be null!");
		}
		this.prePostscanDeviceMap.put(option.getID(), option);
	}
	
	// *************************
	
	/**
	 * Adds an {@link de.ptb.epics.eve.data.measuringstation.AbstractDevice} 
	 * with class name to the class hash.
	 * 
	 * @param className the name of the class the device is part of
	 * @param absdevice the 
	 * 		{@link de.ptb.epics.eve.data.measuringstation.AbstractDevice} that 
	 * 		should be added
	 */
	private void classMapAdd(String className, AbstractDevice absdevice) {
		
		if(absdevice instanceof Motor) {
			final List<MotorAxis> axis = ((Motor)absdevice).getAxes();
			for(final MotorAxis a : axis) {
				this.classMapAdd(a.getClassName(), a);
			}
		} else if(absdevice instanceof Detector) {
			final List<DetectorChannel> channels = ((Detector)absdevice).getChannels();
			for(final DetectorChannel c : channels) {
				this.classMapAdd(c.getClassName(), c);
			}
		} 
		
		if ((className == null) || className.length() < 1) {
			return;
		}
		
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
	
	// ************************************************************************
	// *********** methods inherited from IMeasuringStation *******************
	// ************************************************************************
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Detector> getDetectors() {
		return new ArrayList<Detector>(this.detectors);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Device> getDevices() {
		return new ArrayList<Device>(this.devices);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Event> getEvents() {
		return new ArrayList<Event>(this.events);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Motor> getMotors() {
		return new ArrayList<Motor>(this.motors);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<PlugIn> getPlugins() {
		return new ArrayList<PlugIn>(this.plugins);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Selections getSelections() {
		return this.selections;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getVersion() {
		return this.version;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getLoadedFileName() {
		return loadedFileName;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getSchemaFileName() {
		return schemaFileName;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public PlugIn getPluginByName(final String name) {
		return this.pluginsMap.get(name);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public AbstractPrePostscanDevice getPrePostscanDeviceById(final String id) {
		return this.prePostscanDeviceMap.get(id);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public MotorAxis getMotorAxisById(final String id) {
		return this.motorAxisMap.get(id);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public DetectorChannel getDetectorChannelById(final String id) {
		return this.detectorChannelsMap.get(id);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Event getEventById(final String id) {
		return this.eventsMap.get(id);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
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
				for (Iterator<String> iterator = identifier.iterator(); 
					 iterator.hasNext();) 
				{
					final String test = iterator.next();
					if (currentAxis.getFullIdentifyer().
							compareToIgnoreCase(test) > 0) {
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
	 * {@inheritDoc}
	 */
	@Override
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
				for (Iterator<String> iterator = identifier.iterator(); 
					 iterator.hasNext();) 
				{
					final String test = iterator.next();
					if (currentDetector.getFullIdentifyer().
							compareToIgnoreCase(test) > 0) {
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
	 * {@inheritDoc}
	 */
	@Override
	public List<String> getPrePostScanDevicesFullIdentifyer() {
		List<String> identifier = new ArrayList<String>();
		Iterator<Device> deviceIterator = this.devices.iterator();
		while(deviceIterator.hasNext()) {
			Device currentDevice = deviceIterator.next();
			int i = 0;
			for (Iterator<String> iterator = identifier.iterator(); 
				 iterator.hasNext();) 
			{
				final String test = iterator.next();
				if (currentDevice.getFullIdentifyer().
						compareToIgnoreCase(test) > 0) {
					i++;
				}
				else
					break;
			}
			identifier.add(i, currentDevice.getFullIdentifyer());
		}
		
		Iterator<Motor> motorIterator = this.motors.iterator();
		while(motorIterator.hasNext()) {
			Motor currentMotor = motorIterator.next();
			
			Iterator<Option> optionIterator = currentMotor.optionIterator();
			while(optionIterator.hasNext()) {
				Option currentOption = optionIterator.next();
				int i = 0;
				for (Iterator<String> iterator = identifier.iterator(); 
					 iterator.hasNext();) 
				{
					final String test = iterator.next();
					if (currentOption.getFullIdentifyer().
							compareToIgnoreCase(test) > 0) {
						i++;
					}
					else
						break;
				}
				identifier.add(i, currentOption.getFullIdentifyer());
			}
			
			Iterator<MotorAxis> axisIterator = currentMotor.axisIterator();
			while(axisIterator.hasNext()) {
				MotorAxis currentAxis = axisIterator.next();
				
				Iterator<Option> optionIterator2 = 
						currentAxis.getOptions().iterator();
				while( optionIterator2.hasNext() ) {
					Option currentOption = optionIterator2.next();
					int i = 0;
					for (Iterator<String> iterator = identifier.iterator(); 
						 iterator.hasNext();) 
					{
						final String test = iterator.next();
						if (currentOption.getFullIdentifyer().
								compareToIgnoreCase(test) > 0) {
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
		while(detectorIterator.hasNext()) {
			Detector currentDetector = detectorIterator.next();
			
			Iterator<Option> optionIterator = 
				currentDetector.getOptions().iterator();
			while(optionIterator.hasNext()) {
				Option currentOption = optionIterator.next();
				int i = 0;
				for (Iterator<String> iterator = identifier.iterator(); 
					 iterator.hasNext();) 
				{
					final String test = iterator.next();
					if (currentOption.getFullIdentifyer().
							compareToIgnoreCase(test) > 0) {
						i++;
					}
					else
						break;
				}
				identifier.add(i, currentOption.getFullIdentifyer());
			}
			
			Iterator<DetectorChannel> channelsIterator = 
				currentDetector.channelIterator();
			while(channelsIterator.hasNext()) {
				DetectorChannel currentChannel = channelsIterator.next();
				
				Iterator<Option> optionIterator2 = 
						currentChannel.getOptions().iterator();
				while(optionIterator2.hasNext()) {
					Option currentOption = optionIterator2.next();
					int i = 0;
					for (Iterator<String> iterator = identifier.iterator(); 
						 iterator.hasNext();){
						final String test = iterator.next();
						if (currentOption.getFullIdentifyer().
								compareToIgnoreCase(test) > 0) {
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
	 * {@inheritDoc}
	 * @throws IllegalArgumentException if the argument is <code>null</code>
	 * @throws IllegalArgumentException if identifier is an empty string
	 */
	@Override
	public AbstractDevice getAbstractDeviceByFullIdentifyer(
												final String identifier) {
		if(identifier == null) {
			throw new IllegalArgumentException(
					"The parameter 'identifier' must not be null!");
		} else if(identifier.equals("")) {
			throw new IllegalArgumentException(
					"The parameter 'identifier' must not be an empty string!");
		}
		
		Iterator<Motor> motorIterator = this.motors.iterator();
		while(motorIterator.hasNext()) {
			final Motor currentMotor = motorIterator.next();
			if(currentMotor.getFullIdentifyer().equals(identifier)) {
				return currentMotor;
			} else {
				Iterator<Option> optionIterator = currentMotor.optionIterator();
				while(optionIterator.hasNext()) {
					final Option currentOption = optionIterator.next();
					if(currentOption.getFullIdentifyer().equals(identifier)) {
						return currentOption;
					}
				}
				Iterator<MotorAxis> motorAxisIterator = 
						currentMotor.axisIterator();
				while(motorAxisIterator.hasNext()) {
					final MotorAxis currentMotorAxis = motorAxisIterator.next();
					if(currentMotorAxis.getFullIdentifyer().equals(identifier)) {
						return currentMotorAxis;
					}
					Iterator<Option> optionIterator2 = 
						currentMotorAxis.getOptions().iterator();
					while(optionIterator2.hasNext()) {
						final Option currentOption = optionIterator2.next();
						if(currentOption.getFullIdentifyer().equals(identifier)) {
							return currentOption;
						}
					}
				}
			}
		}
		
		Iterator<Detector> detectorIterator = this.detectors.iterator();
		while(detectorIterator.hasNext()) {
			final Detector currentDetector = detectorIterator.next();
			if(currentDetector.getFullIdentifyer().equals(identifier)) {
				return currentDetector;
			} else {
				Iterator<Option> optionIterator = 
					currentDetector.getOptions().iterator();
				while(optionIterator.hasNext()) {
					final Option currentOption = optionIterator.next();
					if(currentOption.getFullIdentifyer().equals(identifier)) {
						return currentOption;
					}
				}
				Iterator<DetectorChannel> detectorChannelIterator = 
					currentDetector.channelIterator();
				while(detectorChannelIterator.hasNext()) {
					final DetectorChannel currentDetectorChannel = 
						detectorChannelIterator.next();
					if(currentDetectorChannel.getFullIdentifyer().equals(identifier)) {
						return currentDetectorChannel;
					}
					Iterator<Option> optionIterator2 = 
						currentDetectorChannel.getOptions().iterator();
					while(optionIterator2.hasNext()) {
						final Option currentOption = optionIterator2.next();
						if(currentOption.getFullIdentifyer().equals(identifier)) {
							return currentOption;
						}
					}
				}
			}
		}
		
		Iterator<Device> deviceIterator = this.devices.iterator();
		while(deviceIterator.hasNext()) {
			final Device currentDevice = deviceIterator.next();
			if(currentDevice.getFullIdentifyer().equals(identifier)) {
				return currentDevice;
			}
		}
		return null;	
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<String> getClassNameList(){
		return classMap.keySet();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<AbstractDevice> getDeviceList(String classname) {
		if (classMap.containsKey(classname))
			return classMap.get(classname);
		else
			return null;
	}
	
	// ************************************************************************
	// *********** methods inherited from IModelUpdateProvider ****************
	// ************************************************************************
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean addModelUpdateListener(
			final IModelUpdateListener modelUpdateListener) {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean removeModelUpdateListener(
			final IModelUpdateListener modelUpdateListener) {
		return false;
	}
}