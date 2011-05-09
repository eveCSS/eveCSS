package de.ptb.epics.eve.data.measuringstation.filter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.ptb.epics.eve.data.measuringstation.AbstractDevice;
import de.ptb.epics.eve.data.measuringstation.AbstractPrePostscanDevice;
import de.ptb.epics.eve.data.measuringstation.Detector;
import de.ptb.epics.eve.data.measuringstation.DetectorChannel;
import de.ptb.epics.eve.data.measuringstation.Device;
import de.ptb.epics.eve.data.measuringstation.Event;
import de.ptb.epics.eve.data.measuringstation.Motor;
import de.ptb.epics.eve.data.measuringstation.MotorAxis;
import de.ptb.epics.eve.data.measuringstation.Option;
import de.ptb.epics.eve.data.measuringstation.PlugIn;
import de.ptb.epics.eve.data.measuringstation.Selections;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener;
import de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent;

/**
 * <code>ExcludeFilter</code>  is a 
 * {@link de.ptb.epics.eve.data.measuringstation.filter.MeasuringStationFilter} 
 * capable of including and excluding 
 * {@link de.ptb.epics.eve.data.measuringstation.AbstractDevice}s via 
 * {@link #include(AbstractDevice)} and {@link #exclude(AbstractDevice)}.
 * An {@link de.ptb.epics.eve.data.measuringstation.AbstractDevice} excluded 
 * this way is not returned by the corresponding getter method.
 * 
 * @author ?
 * @author Marcus Michalsky
 */
public class ExcludeFilter extends MeasuringStationFilter {

	// available events
	private List<Event> events;
	
	// available plug ins
	private final List<PlugIn> plugins;
	
	// available selections
	private final Selections selections;
	
	// available devices
	private final List<Device> devices;
	
	// available motors
	private final List<Motor> motors;
	
	// available detectors
	private final List<Detector> detectors;
	
	// a map of available plug ins (key = plug in name)
	private final Map<String, PlugIn> pluginsMap;
	
	// a map of available motor axes (key = id)
	private final Map<String, MotorAxis> motorAxisMap;
	
	// a map of available detector channels (key = id)
	private final Map<String, DetectorChannel> detectorChannelsMap;
	
	// a map of available events (key = id)
	private final Map<String, Event> eventsMap;
	
	// a map of abstract pre post scan devices (key = id)
	private final Map<String, AbstractPrePostscanDevice> prePostscanDeviceMap;

	// a map if ???
	private Map<String, List<AbstractDevice>> classMap;
	
	// parties interested in changes
	private final List<IModelUpdateListener> modelUpdateListener;
	
	// excluded devices
	private final List<AbstractDevice> excludeList;
	
	/**
	 * Constructs an <code>ExcludeFilter</code>.
	 */
	public ExcludeFilter() {
		this.events = new ArrayList<Event>();
		this.plugins = new ArrayList<PlugIn>();
		this.devices = new ArrayList<Device>();
		this.motors = new ArrayList<Motor>();
		this.detectors = new ArrayList<Detector>();
		this.selections = new Selections();
		this.pluginsMap = new HashMap<String, PlugIn>();
		this.motorAxisMap = new HashMap<String, MotorAxis>();
		this.detectorChannelsMap = new HashMap<String, DetectorChannel>();
		this.prePostscanDeviceMap = new HashMap<String, AbstractPrePostscanDevice>();
		this.classMap = new HashMap<String, List<AbstractDevice>>();
		this.eventsMap = new HashMap<String, Event>();
		this.modelUpdateListener = new ArrayList<IModelUpdateListener>();
		this.excludeList = new ArrayList<AbstractDevice>();
	}
	
	/**
	 * {@inheritDoc}
	 */
	public List<Detector> getDetectors() {
		return new ArrayList<Detector>(this.detectors);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Device> getDevices() {
		return new ArrayList<Device>(this.devices);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public List<Event> getEvents() {
		return new ArrayList<Event>(this.events);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Motor> getMotors() {
		return new ArrayList<Motor>(this.motors);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<PlugIn> getPlugins() {
		return new ArrayList<PlugIn>(this.plugins);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public Selections getSelections() {
		return this.selections;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getVersion() {
		return this.getSource() != null
				? this.getSource().getVersion()
				: "";
	}

	/**
	 * {@inheritDoc}
	 */
	public String getLoadedFileName(){
		return this.getSource() != null
				? this.getSource().getLoadedFileName()
				: "";
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String getSchemaFileName(){
		return this.getSource() != null
				? this.getSource().getSchemaFileName()
				: "";
	}
	
	/**
	 * {@inheritDoc}
	 */
	public PlugIn getPluginByName(final String name) {
		return this.pluginsMap.get(name);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public AbstractPrePostscanDevice getPrePostscanDeviceById(final String id) {
		return this.prePostscanDeviceMap.get(id);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public MotorAxis getMotorAxisById(final String id) {
		return this.motorAxisMap.get(id);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public DetectorChannel getDetectorChannelById(final String id) {
		return this.detectorChannelsMap.get(id);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public Event getEventById(final String id) {
		return this.eventsMap.get(id);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public List<String> getAxisFullIdentifyer() {
		
		final List<String> identifier = new ArrayList<String>();
		final Iterator<Motor> motorIterator = this.motors.iterator();
		
		Motor currentMotor = null;
		MotorAxis currentAxis = null;
		Iterator<MotorAxis> axisIterator = null;
		while( motorIterator.hasNext() ) {
			currentMotor = motorIterator.next();
			if( this.excludeList.contains( currentMotor ) ) {
				continue;
			}
			axisIterator = currentMotor.axisIterator();
			while( axisIterator.hasNext() ) {
				currentAxis = axisIterator.next();
				if( this.excludeList.contains( currentAxis ) ) {
					continue;
				}
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
	 * {@inheritDoc}
	 */
	public List<String> getChannelsFullIdentifyer() {
		List<String> identifier = new ArrayList<String>();
		Iterator<Detector> detectorIterator = this.detectors.iterator();
		
		while(detectorIterator.hasNext()) {
			Detector currentDetector = detectorIterator.next();
			if(this.excludeList.contains(currentDetector)) {
				continue;
			}
			Iterator<DetectorChannel> channelIterator = currentDetector.channelIterator();
			while( channelIterator.hasNext() ) {
				DetectorChannel currentChannel = channelIterator.next();
				if( this.excludeList.contains( currentChannel ) ) {
					continue;
				}
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
	 * {@inheritDoc}
	 */
	public List<String> getPrePostScanDevicesFullIdentifyer() {
		List<String> identifier = new ArrayList<String>();
		Iterator<Device> deviceIterator = this.devices.iterator();
		while(deviceIterator.hasNext()) {
			Device currentDevice = deviceIterator.next();
			if( this.excludeList.contains(currentDevice)) {
				continue;
			}
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
			if( this.excludeList.contains( currentMotor ) ) {
				continue;
			}
			Iterator<Option> optionIterator = currentMotor.optionIterator();
			while( optionIterator.hasNext() ) {
				Option currentOption = optionIterator.next();
				if( this.excludeList.contains( currentOption ) ) {
					continue;
				}
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
				if( this.excludeList.contains( currentAxis ) ) {
					continue;
				}
				Iterator<Option> optionIterator2 = currentAxis.getOptions().iterator();
				while( optionIterator2.hasNext() ) {
					Option currentOption = optionIterator2.next();
					if( this.excludeList.contains( currentOption ) ) {
						continue;
					}
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
			if( this.excludeList.contains( currentDetector ) ) {
				continue;
			}
			Iterator<Option> optionIterator = currentDetector.getOptions().iterator();
			while( optionIterator.hasNext() ) {
				Option currentOption = optionIterator.next();
				if( this.excludeList.contains( currentOption ) ) {
					continue;
				}
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
				if( this.excludeList.contains( currentChannel ) ) {
					continue;
				}
				Iterator<Option> optionIterator2 = currentChannel.getOptions().iterator();
				while( optionIterator2.hasNext() ) {
					Option currentOption = optionIterator2.next();
					if( this.excludeList.contains( currentOption ) ) {
						continue;
					}
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
	 * {@inheritDoc}
	 * @throws IllegalArgumentException if the argument is <code>null</code> or 
	 * 		   an empty {@link java.lang.String}
	 */
	public AbstractDevice getAbstractDeviceByFullIdentifyer(final String identifier) {
		if(identifier == null) {
			throw new IllegalArgumentException(
					"The parameter 'identifier' must not be null!");
		} else if( identifier.equals( "" ) ) {
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
	 * {@inheritDoc}
	 */
	public Set<String> getClassNameList(){
		return classMap.keySet();
	}

	/**
	 * {@inheritDoc}
	 */
	public List<AbstractDevice> getDeviceList(String classname){
		if (classMap.containsKey(classname))
			return classMap.get(classname);
		else
			return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean addModelUpdateListener(
			final IModelUpdateListener modelUpdateListener) {
		return this.modelUpdateListener.add(modelUpdateListener);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean removeModelUpdateListener(
			final IModelUpdateListener modelUpdateListener) {
		return this.modelUpdateListener.remove(modelUpdateListener);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateEvent(final ModelUpdateEvent modelUpdateEvent) {
	
		this.events.clear();
		this.plugins.clear();
		this.devices.clear();
		this.motors.clear();
		this.detectors.clear();
		this.selections.setSmtypes( new String[0] );
		this.selections.setStepfunctions( new String[0] );
		this.pluginsMap.clear();
		this.motorAxisMap.clear();
		this.detectorChannelsMap.clear();
		this.prePostscanDeviceMap.clear();
		this.classMap.clear();
		this.eventsMap.clear();
	
		if( this.getSource() != null ) {
			this.events.addAll( this.getSource().getEvents() );
			
			this.plugins.addAll( this.getSource().getPlugins() );
			
			this.devices.addAll( this.getSource().getDevices() );
			this.devices.removeAll( this.excludeList );
			
			for( final Motor motor : this.getSource().getMotors() ) {
				if( !this.excludeList.contains( motor ) ) {
					final Motor m = (Motor)motor.clone();
					for( final AbstractDevice d : this.excludeList ) {
						if( d instanceof MotorAxis ) {
							m.remove( (MotorAxis)d );
						}
					}
					this.motors.add( m );
				}
			}
			
			for( final Detector detector : this.getSource().getDetectors() ) {
				if( !this.excludeList.contains( detector ) ) {
					final Detector m = (Detector)detector.clone();
					for( final AbstractDevice d : this.excludeList ) {
						if( d instanceof DetectorChannel ) {
							m.remove( (DetectorChannel)d );
						}
					}
					this.detectors.add( m );
				}
			}
			
			this.selections.setSmtypes( this.getSource().getSelections().getSmtypes() );
			this.selections.setStepfunctions( this.getSource().getSelections().getStepfunctions() );
			
			for( final PlugIn plugIn : this.plugins ) {
				this.pluginsMap.put( plugIn.getName(), plugIn );
			}
			
			for( final Motor motor : this.motors ) {
				for( final Option option : motor.getOptions() ) {
					if( !this.excludeList.contains( option ) ) {
						this.prePostscanDeviceMap.put( option.getID(), option );
					}
				}
	
				for( final MotorAxis motorAxis : motor.getAxes() ) {
					if( !this.excludeList.contains( motorAxis ) ) {
						this.motorAxisMap.put( motorAxis.getID(), motorAxis );
						for( final Option option : motorAxis.getOptions() ) {
							if( !this.excludeList.contains( option ) ) {
								this.prePostscanDeviceMap.put( option.getID(), option );
							}
						}
					}
				}
			}
			
			for( final Detector detector : this.detectors ) {
				for( final Option option : detector.getOptions() ) {
					if ( !this.excludeList.contains(option)) {
						this.prePostscanDeviceMap.put( option.getID(), option );
					}
				}
				for( final DetectorChannel detectorChannel : detector.getChannels() ) {
					if( !this.excludeList.contains( detectorChannel ) ) {
						this.detectorChannelsMap.put( detectorChannel.getID(), detectorChannel );
						for( final Option option : detectorChannel.getOptions() ) {
							if ( !this.excludeList.contains(option)) {
								this.prePostscanDeviceMap.put( option.getID(), option );
							}
						}
					}
				}
			}
			
			for( final Device device : this.devices ) {
				this.prePostscanDeviceMap.put( device.getID(), device );
			}
			
			buildClassMap();
			
			for( final Event event : this.events ) {
				this.eventsMap.put(  event.getID(), event );
			}
		}
		
		for( final IModelUpdateListener modelUpdateListener : this.modelUpdateListener ) {
			modelUpdateListener.updateEvent( new ModelUpdateEvent( this, null ) );
		}
	}

	/**
	 * Excludes an {@link de.ptb.epics.eve.data.measuringstation.AbstractDevice}.
	 * 
	 * @param abstractDevice the 
	 * 		  {@link de.ptb.epics.eve.data.measuringstation.AbstractDevice} 
	 * 		  that should be excluded.
	 */
	public void exclude(final AbstractDevice abstractDevice) {
		this.excludeList.add(abstractDevice);
		this.updateEvent(new ModelUpdateEvent(this, null));
	}
	
	/**
	 * Includes an {@link de.ptb.epics.eve.data.measuringstation.AbstractDevice}.
	 * 
	 * @param abstractDevice the 
	 * 		  {@link de.ptb.epics.eve.data.measuringstation.AbstractDevice} 
	 * 		  that should be included.
	 */
	public void include(final AbstractDevice abstractDevice) {
		this.excludeList.remove(abstractDevice);
		this.updateEvent(new ModelUpdateEvent(this, null));
	}
	
	/*
	 * 
	 */
	private void buildClassMap() {
		this.classMap.clear();
		
		for( final Motor motor : this.motors ) {
			if( motor.getClassName() != null && !motor.getClassName().equals( "" ) ) {
				List< AbstractDevice > devices = null;
				if( this.classMap.containsKey( motor.getClassName() ) ) {
					devices = this.classMap.get( motor.getClassName() );
				} else {
					devices = new ArrayList< AbstractDevice >();
					this.classMap.put( motor.getClassName(), devices );
				}
				devices.add( motor );
				
				for( final MotorAxis motorAxis : motor.getAxes() ) {
					if( motorAxis.getClassName() != null && !motorAxis.getClassName().equals( "" ) ) {
						devices = null;
						if( this.classMap.containsKey( motorAxis.getClassName() ) ) {
							devices = this.classMap.get( motorAxis.getClassName() );
						} else {
							devices = new ArrayList< AbstractDevice >();
							this.classMap.put( motorAxis.getClassName(), devices );
						}
						devices.add( motorAxis );
					}
				}
			}
		}
		
		for( final Detector detector : this.detectors ) {
			if( detector.getClassName() != null && !detector.getClassName().equals( "" ) ) {
				List< AbstractDevice > devices = null;
				if( this.classMap.containsKey( detector.getClassName() ) ) {
					devices = this.classMap.get( detector.getClassName() );
				} else {
					devices = new ArrayList< AbstractDevice >();
					this.classMap.put( detector.getClassName(), devices );
				}
				devices.add( detector );
				
				for( final DetectorChannel detectorChannel : detector.getChannels() ) {
					if( detectorChannel.getClassName() != null && !detectorChannel.getClassName().equals( "" ) ) {
						devices = null;
						if( this.classMap.containsKey( detectorChannel.getClassName() ) ) {
							devices = this.classMap.get( detectorChannel.getClassName() );
						} else {
							devices = new ArrayList< AbstractDevice >();
							this.classMap.put( detectorChannel.getClassName(), devices );
						}
						devices.add( detectorChannel );
					}
				}
			}
		}
	}
}