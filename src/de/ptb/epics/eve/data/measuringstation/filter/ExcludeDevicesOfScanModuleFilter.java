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
import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.data.scandescription.Channel;
import de.ptb.epics.eve.data.scandescription.Positioning;
import de.ptb.epics.eve.data.scandescription.Postscan;
import de.ptb.epics.eve.data.scandescription.Prescan;
import de.ptb.epics.eve.data.scandescription.ScanModul;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener;
import de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent;

public class ExcludeDevicesOfScanModuleFilter extends MeasuringStationFilter {
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
	
	private final List< IModelUpdateListener > modelUpdateListener;
	
	
	private final List< AbstractDevice > excludeList;

	private ScanModul scanModule;
	
	private final boolean excludeAxis;
	private final boolean excludeChannels;
	private final boolean excludePrescans;
	private final boolean excludePostscans;
	private final boolean excludePositionsings;
	
	public ExcludeDevicesOfScanModuleFilter( final boolean excludeAxis, final boolean excludeChannels, final boolean excludePrescans, final boolean excludePostscans, final boolean excludePositionsings  ) {
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
		this.modelUpdateListener = new ArrayList< IModelUpdateListener >();
		this.excludeList = new ArrayList< AbstractDevice >();
		this.excludeAxis = excludeAxis;
		this.excludeChannels = excludeChannels;
		this.excludePrescans = excludePrescans;
		this.excludePostscans = excludePostscans;
		this.excludePositionsings = excludePositionsings;
	}
	
	/* (non-Javadoc)
	 * @see de.ptb.epics.eve.data.measuringstation.IMeasuringStation#getDetectors()
	 */
	public List<Detector> getDetectors() {
		return new ArrayList<Detector>( this.detectors );
	}

	/* (non-Javadoc)
	 * @see de.ptb.epics.eve.data.measuringstation.IMeasuringStation#getDevices()
	 */
	public List<Device> getDevices() {
		return new ArrayList<Device>( this.devices );
	}
	
	/* (non-Javadoc)
	 * @see de.ptb.epics.eve.data.measuringstation.IMeasuringStation#getEvents()
	 */
	public List<Event> getEvents() {
		return new ArrayList<Event>( this.events );
	}

	/* (non-Javadoc)
	 * @see de.ptb.epics.eve.data.measuringstation.IMeasuringStation#getMotors()
	 */
	public List<Motor> getMotors() {
		return new ArrayList<Motor>( this.motors );
	}

	/* (non-Javadoc)
	 * @see de.ptb.epics.eve.data.measuringstation.IMeasuringStation#getPlugins()
	 */
	public List<PlugIn> getPlugins() {
		return new ArrayList<PlugIn>( this.plugins );
	}
	
	/* (non-Javadoc)
	 * @see de.ptb.epics.eve.data.measuringstation.IMeasuringStation#getSelections()
	 */
	public Selections getSelections() {
		return this.selections;
	}

	/* (non-Javadoc)
	 * @see de.ptb.epics.eve.data.measuringstation.IMeasuringStation#getVersion()
	 */
	public String getVersion() {
		return this.getSource()!=null?this.getSource().getVersion():"";
	}

	/* (non-Javadoc)
	 * @see de.ptb.epics.eve.data.measuringstation.IMeasuringStation#getLoadedFileName()
	 */
	public String getLoadedFileName(){
		return this.getSource()!=null?this.getSource().getLoadedFileName():"";
	}
	
	/* (non-Javadoc)
	 * @see de.ptb.epics.eve.data.measuringstation.IMeasuringStation#getSchemaFileName()
	 */
	public String getSchemaFileName(){
		return this.getSource()!=null?this.getSource().getSchemaFileName():"";
	}
	
	/* (non-Javadoc)
	 * @see de.ptb.epics.eve.data.measuringstation.IMeasuringStation#getPluginByName(java.lang.String)
	 */
	public PlugIn getPluginByName( final String name ) {
		return this.pluginsMap.get( name );
	}
	
	/* (non-Javadoc)
	 * @see de.ptb.epics.eve.data.measuringstation.IMeasuringStation#getPrePostscanDeviceById(java.lang.String)
	 */
	public AbstractPrePostscanDevice getPrePostscanDeviceById( final String id ) {
		return this.prePostscanDeviceMap.get( id );
	}
	
	/* (non-Javadoc)
	 * @see de.ptb.epics.eve.data.measuringstation.IMeasuringStation#getMotorAxisById(java.lang.String)
	 */
	public MotorAxis getMotorAxisById( final String id ) {
		return this.motorAxisMap.get( id );
	}
	
	/* (non-Javadoc)
	 * @see de.ptb.epics.eve.data.measuringstation.IMeasuringStation#getDetectorChannelById(java.lang.String)
	 */
	public DetectorChannel getDetectorChannelById( final String id ) {
		return this.detectorChannelsMap.get( id );
	}
	
	/* (non-Javadoc)
	 * @see de.ptb.epics.eve.data.measuringstation.IMeasuringStation#getEventById(java.lang.String)
	 */
	public Event getEventById( final String id ) {
		return this.eventsMap.get( id );
	}
	
	/* (non-Javadoc)
	 * @see de.ptb.epics.eve.data.measuringstation.IMeasuringStation#getAxisFullIdentifyer()
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
	
	/* (non-Javadoc)
	 * @see de.ptb.epics.eve.data.measuringstation.IMeasuringStation#getChannelsFullIdentifyer()
	 */
	public List<String> getChannelsFullIdentifyer() {
		List<String> identifier = new ArrayList<String>();
		Iterator<Detector> detectorIterator = this.detectors.iterator();
		
		while( detectorIterator.hasNext() ) {
			Detector currentDetector = detectorIterator.next();
			if( this.excludeList.contains( currentDetector ) ) {
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
	
	/* (non-Javadoc)
	 * @see de.ptb.epics.eve.data.measuringstation.IMeasuringStation#getPrePostScanDevicesFullIdentifyer()
	 */
	public List<String> getPrePostScanDevicesFullIdentifyer() {
		
		long begin = System.nanoTime();
		
		List<String> identifier = new ArrayList<String>();
		Iterator<Device> deviceIterator = this.devices.iterator();
		while( deviceIterator.hasNext() ) {
			Device currentDevice = deviceIterator.next();
			if( this.excludeList.contains( currentDevice ) ) {
				continue;
			}
			
			identifier.add( currentDevice.getFullIdentifyer());
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
				
				identifier.add( currentOption.getFullIdentifyer());
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
					
					identifier.add( currentOption.getFullIdentifyer());
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
				
				identifier.add( currentOption.getFullIdentifyer());
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
					identifier.add( currentOption.getFullIdentifyer());
				}
			}
		}
		
		System.out.println( "Dauer: " + (System.nanoTime() - begin ) );
		System.out.println( "Size: " + identifier.size() );
		return identifier;
	}
	
	/* (non-Javadoc)
	 * @see de.ptb.epics.eve.data.measuringstation.IMeasuringStation#getAbstractDeviceByFullIdentifyer(java.lang.String)
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

	/* (non-Javadoc)
	 * @see de.ptb.epics.eve.data.measuringstation.IMeasuringStation#getClassNameList()
	 */
	public Set<String> getClassNameList(){
		return classMap.keySet();
	}

	/* (non-Javadoc)
	 * @see de.ptb.epics.eve.data.measuringstation.IMeasuringStation#getDeviceList(java.lang.String)
	 */
	public List<AbstractDevice> getDeviceList(String classname){
		if (classMap.containsKey(classname))
			return classMap.get(classname);
		else
			return null;
	}

	@Override
	public boolean addModelUpdateListener( final IModelUpdateListener modelUpdateListener ) {
		return this.modelUpdateListener.add( modelUpdateListener );
	}

	@Override
	public boolean removeModelUpdateListener( final IModelUpdateListener modelUpdateListener ) {
		return this.modelUpdateListener.remove( modelUpdateListener );
	}
	
	
	public void setScanModule( final ScanModul scanModule ) {
		if( this.scanModule != null ) {
			this.scanModule.removeModelUpdateListener( this );
		}
		this.scanModule = scanModule;
		if( this.scanModule != null ) {
			this.scanModule.addModelUpdateListener( this );
		}
		this.updateEvent( new ModelUpdateEvent( this, null ) );
	}
	
	@Override
	public void updateEvent( final ModelUpdateEvent modelUpdateEvent ) {
		this.events.clear();
		this.plugins.clear();
		this.devices.clear();
		this.motors.clear();
		this.detectors.clear();
		this.selections.setColors( new String[0] );
		this.selections.setLinestyles( new String[0] );
		this.selections.setMarkstyles( new String[0] );
		this.selections.setSmtypes( new String[0] );
		this.selections.setStepfunctions( new String[0] );
		this.pluginsMap.clear();
		this.motorAxisMap.clear();
		this.detectorChannelsMap.clear();
		this.prePostscanDeviceMap.clear();
		this.classMap.clear();
		this.eventsMap.clear();

		if( this.getSource() != null ) {
			this.excludeList.clear();
			if( this.scanModule != null ) {
				if( this.excludeAxis ) {
					for( final Axis axis : this.scanModule.getAxis() ) {
						this.excludeList.add( axis.getAbstractDevice() );
					}
				}
				if( this.excludeChannels ) {
					for( final Channel channel : this.scanModule.getChannels() ) {
						this.excludeList.add( channel.getAbstractDevice() );
					}
				}
				if( this.excludePrescans ) {
					for( final Prescan prescan : this.scanModule.getPrescans()  ) {
						this.excludeList.add( prescan.getAbstractDevice() );
					}
				}
				if( this.excludePostscans ) {
					for( final Postscan postscan : this.scanModule.getPostscans()  ) {
						this.excludeList.add( postscan.getAbstractDevice() );
					}
				}
				if( this.excludePositionsings ) {
					for( final Positioning positioning : this.scanModule.getPositionings() ) {
						this.excludeList.add( positioning.getMotorAxis() );
					}
				}
			}
			
			this.events.addAll( this.getSource().getEvents() );
			
			this.plugins.addAll( this.getSource().getPlugins() );
			
			this.devices.addAll( this.getSource().getDevices() );
			this.devices.removeAll( this.excludeList );
			
			this.motors.addAll( this.getSource().getMotors() );
			this.motors.removeAll( this.excludeList );
			
			this.detectors.addAll( this.getSource().getDetectors());
			this.detectors.removeAll(this.excludeList );
			
			this.selections.setColors( this.getSource().getSelections().getColors() );
			this.selections.setLinestyles( this.getSource().getSelections().getLinestyles() );
			this.selections.setMarkstyles( this.getSource().getSelections().getMarkstyles() );
			this.selections.setSmtypes( this.getSource().getSelections().getSmtypes() );
			this.selections.setStepfunctions( this.getSource().getSelections().getStepfunctions() );
			
			for( final PlugIn plugIn : this.plugins ) {
				this.pluginsMap.put( plugIn.getName(), plugIn );
			}
			
			for( final Motor motor : this.motors ) {
				for( final Option option : motor.getOptions() ) {
					this.prePostscanDeviceMap.put( option.getID(), option );
				}
				for( final MotorAxis motorAxis : motor.getAxis() ) {
					if( !this.excludeList.contains( motorAxis ) ) {
						this.motorAxisMap.put( motorAxis.getID(), motorAxis );
						for( final Option option : motorAxis.getOptions() ) {
							this.prePostscanDeviceMap.put( option.getID(), option );
						}
					}
				}
			}
			
			for( final Detector detector : this.detectors ) {
				for( final Option option : detector.getOptions() ) {
					this.prePostscanDeviceMap.put( option.getID(), option );
				}
				for( final DetectorChannel detectorChannel : detector.getChannels() ) {
					if( !this.excludeList.contains( detectorChannel ) ) {
						this.detectorChannelsMap.put( detectorChannel.getID(), detectorChannel );
						for( final Option option : detectorChannel.getOptions() ) {
							this.prePostscanDeviceMap.put( option.getID(), option );
						}
					}
				}
			}
			
			for( final Device device : this.devices ) {
				this.prePostscanDeviceMap.put( device.getID(), device );
			}
			
			
			for( final String key : this.getSource().getClassNameList() ) {
				final ArrayList<AbstractDevice> devices = new ArrayList< AbstractDevice >( this.getSource().getDeviceList( key ) );
				devices.removeAll( this.excludeList );
				if( devices.size() > 0 ) {
					this.classMap.put( key, devices );
				}
			}
			
			for( final Event event : this.events ) {
				this.eventsMap.put(  event.getID(), event );
			}
			
		}
		
		for( final IModelUpdateListener modelUpdateListener : this.modelUpdateListener ) {
			modelUpdateListener.updateEvent( new ModelUpdateEvent( this, null ) );
		}
		
	}
}
