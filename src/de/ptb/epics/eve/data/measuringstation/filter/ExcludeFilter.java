package de.ptb.epics.eve.data.measuringstation.filter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

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
import de.ptb.epics.eve.data.scandescription.Chain;
import de.ptb.epics.eve.data.scandescription.Channel;
import de.ptb.epics.eve.data.scandescription.Positioning;
import de.ptb.epics.eve.data.scandescription.Postscan;
import de.ptb.epics.eve.data.scandescription.Prescan;
import de.ptb.epics.eve.data.scandescription.ScanDescription;
import de.ptb.epics.eve.data.scandescription.ScanModule;
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

	// logging
	private static Logger logger = 
			Logger.getLogger(ExcludeFilter.class.getName());
	
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
	
		// clear everything
		this.events.clear();
		this.plugins.clear();
		this.devices.clear();
		this.motors.clear();
		this.detectors.clear();
		this.selections.setSmtypes(new String[0]);
		this.selections.setStepfunctions(new String[0]);
		this.pluginsMap.clear();
		this.motorAxisMap.clear();
		this.detectorChannelsMap.clear();
		this.prePostscanDeviceMap.clear();
		this.classMap.clear();
		this.eventsMap.clear();
	
		// a source measuring station must be set !
		if(this.getSource() != null) {
			
			// include all events (no filtering)
			this.events.addAll(this.getSource().getEvents());
			
			// include all plug ins (no filtering)
			this.plugins.addAll(this.getSource().getPlugins());
			
			// include devices not in the exclude list
			this.devices.addAll(this.getSource().getDevices());
			this.devices.removeAll(this.excludeList);
			
			// include motors not in the exclude list
			for(final Motor motor : this.getSource().getMotors()) {
				// iterate over each motor in the source
				if(!this.excludeList.contains(motor)) {
					// it is not in the exclude list -> check if its axes are
					final Motor m = (Motor)motor.clone();
					for(final AbstractDevice d : this.excludeList) {
						if(d instanceof MotorAxis) {
							m.remove((MotorAxis)d);
						}
					}
					
					
					
					for(MotorAxis ma : m.getAxes())
					{
						for(AbstractDevice d : this.excludeList)
						{
							if(d instanceof Option)
							{
								ma.remove((Option)d);
							}
						}
					}
					
					
					
					// add the motor to the list of motors
					this.motors.add(m);
				}
			}
			
			// include detectors not in the exclude list
			for(final Detector detector : this.getSource().getDetectors()) {
				// iterate over each detector in the source
				if(!this.excludeList.contains(detector)) {
					// it is not in the exclude ilst -> check if its channels are
					final Detector m = (Detector)detector.clone();
					for(final AbstractDevice d : this.excludeList) {
						if(d instanceof DetectorChannel) {
							m.remove((DetectorChannel)d);
						}
					}
					// add the detector to the list of detectors
					this.detectors.add(m);
				}
			}
			
			// include all sm types (no filtering)
			this.selections.setSmtypes(this.getSource().getSelections().getSmtypes());
			// include all step functions (no filtering)
			this.selections.setStepfunctions(this.getSource().getSelections().getStepfunctions());
			
			// *******************
			// *** builds maps ***
			// *******************
			
			// build plug in map
			for(final PlugIn plugIn : this.plugins) {
				this.pluginsMap.put(plugIn.getName(), plugIn);
			}
			
			// build motor map and motor axis map (and options)
			for(final Motor motor : this.motors) {
				for(final Option option : motor.getOptions()) {
					if(!this.excludeList.contains(option)) {
						this.prePostscanDeviceMap.put(option.getID(), option);
					}
				}
	
				for(final MotorAxis motorAxis : motor.getAxes()) {
					if(!this.excludeList.contains(motorAxis)) {
						this.motorAxisMap.put(motorAxis.getID(), motorAxis);
						for(final Option option : motorAxis.getOptions()) {
							if( !this.excludeList.contains(option)) {
								this.prePostscanDeviceMap.put(option.getID(), option);
							}
						}
					}
				}
			}
			
			// build detector map and detector channel map (and options)
			for(final Detector detector : this.detectors) {
				for(final Option option : detector.getOptions()) {
					if (!this.excludeList.contains(option)) {
						this.prePostscanDeviceMap.put(option.getID(), option);
					}
				}
				for(final DetectorChannel detectorChannel : detector.getChannels()) {
					if(!this.excludeList.contains( detectorChannel)) {
						this.detectorChannelsMap.put(detectorChannel.getID(), detectorChannel);
						for(final Option option : detectorChannel.getOptions()) {
							if (!this.excludeList.contains(option)) {
								this.prePostscanDeviceMap.put(option.getID(), option);
							}
						}
					}
				}
			}
			
			// build device map
			for(final Device device : this.devices) {
				this.prePostscanDeviceMap.put(device.getID(), device);
			}
			
			// build class map
			buildClassMap();
			
			// build event map
			for(final Event event : this.events) {
				this.eventsMap.put(event.getID(), event);
			}
		} // end of: source != null ?
		
		// inform interested parties about the changes
		for(IModelUpdateListener modelUpdateListener : this.modelUpdateListener) {
			modelUpdateListener.updateEvent(new ModelUpdateEvent(this, null));
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
	
	/**
	 * Excludes devices that are not used in the given 
	 * {@link de.ptb.epics.eve.data.scandescription.ScanDescription}.
	 * 
	 * @param scandescription the 
	 * 		  {@link de.ptb.epics.eve.data.scandescription.ScanDescription} 
	 * 		  containing the devices which will be excluded
	 */
	public void excludeUnusedDevices(ScanDescription scandescription)
	{
		// all available motors
		List<Motor> all_motors = getMotors();
		// all available detectors
		List<Detector> all_detectors = getDetectors();
		// all available devices
		List<Device> all_devices = getDevices();
		
		// Sets to add used devices to (has tables because of speed AND
		// uniqueness
		HashSet<MotorAxis> used_motor_axes = new HashSet<MotorAxis>();
		HashSet<DetectorChannel> used_detector_channels = 
				new HashSet<DetectorChannel>();
		HashSet<Device> used_devices = new HashSet<Device>();
		HashSet<Option> used_options = new HashSet<Option>();
		
		
		// chains of the scan description
		List<Chain> chains = scandescription.getChains();
		
		// iterate through chains to identify used devices
		for(Chain chain : chains)
		{
			// iterate through each scan module of the chain
			List<ScanModule> scanModules = chain.getScanModuls();
			
			for(ScanModule sm : scanModules)
			{
				// iterate axes
				Axis[] axes = sm.getAxis();
				
				for(int i = 0; i < axes.length; i++)
				{
					used_motor_axes.add(axes[i].getMotorAxis());
				}
				
				// iterate channels
				Channel[] channels = sm.getChannels();
				
				for(int i = 0; i < channels.length; i++)
				{
					used_detector_channels.add(channels[i].getDetectorChannel());
				}
				
				
				// iterate prescans
				Prescan[] prescans = sm.getPrescans();
				
				for(Prescan prescan : prescans)
				{
					String id = prescan.getAbstractDevice().getID();
					
					if(prescan.isOption())
					{
						used_options.add((Option)prescan.getAbstractDevice());
					}
					
					if(prescan.isDevice())
					{
						used_devices.add((Device)this.getPrePostscanDeviceById(id));
					}
				}
				
				// iterate postscans
				Postscan[] postscans = sm.getPostscans();
				
				for(Postscan postscan : postscans)
				{
					String id = postscan.getAbstractDevice().getID();
					
					if(postscan.isOption())
					{
						used_options.add((Option)postscan.getAbstractDevice());
					}
					
					if(postscan.isDevice())
					{
						used_devices.add((Device)this.getPrePostscanDeviceById(id));
					}
				}
				
				Positioning[] positionings = sm.getPositionings();
				
				for(Positioning positioning : positionings)
				{
					used_motor_axes.add(positioning.getMotorAxis());
					used_detector_channels.add(positioning.getDetectorChannel());
				}
			}
		}
		
		
		
		// *******************************************************************
		
		// remove unused
		
		// iterate over available options -> exclude unused
		
		// iterate over available motors -> exclude their axes if not used
		for(Motor m : all_motors)
		{
			for(MotorAxis ma : m.getAxes())
			{
				if(!(used_motor_axes.contains(ma)))
				{
					boolean option_used = false;
					for(Option o : ma.getOptions())
					{
						if(used_options.contains(o))
						{
							option_used = true;
						} 
						
						
						
						else {
							exclude(o);
							logger.debug("Option " + ma.getName() + ":" + 
									o.getName() + " not used -> exclude");
						}
						
						
					}
					
					if(!option_used)
					{
						exclude(ma);
						logger.debug("Axis " + m.getName() + ":" + 
								ma.getName() + " not used -> exclude");
					}
				} 
				
				
				
				else {
					for(Option o : ma.getOptions())
					{
						if(!used_options.contains(o))
						{
							exclude(o);
							logger.debug("Option " + ma.getName() + ":" + 
									o.getName() + " not used -> exclude");
						}
					}
				}
				
				
				
				
			}
		}
		
		// check for motors with no axes -> exclude them
		for(Motor m : getMotors())
		{
			if(m.getAxes().size() == 0)
			{
				if(logger.isDebugEnabled())
					logger.debug("Motor " + m.getName() + " not used " + 
									   "-> exclude");
				exclude(m);
			}
		}
		
		// iterate over available detectors -> exclude their channels if not used
		for(Detector d : all_detectors)
		{
			for(DetectorChannel ch : d.getChannels())
			{
				if(!(used_detector_channels.contains(ch)))
				{
					exclude(ch);
					logger.debug("Detector Channel " + d.getName() + 
							":" + ch.getName() + " not used -> exclude");
				}
			}
		}
		
		// check for detectors with no channels -> exclude them
		for(Detector d : getDetectors())
		{
			if(d.getChannels().size() == 0)
			{
				boolean option_used = false;
				for(Option o : d.getOptions())
				{
					if(used_options.contains(o))
					{
						option_used = true;
					}
				}
				
				if(!option_used)
				{
					exclude(d);
					if(logger.isDebugEnabled())
						logger.debug("Detector " + d.getName() + " not used " + 
									 "-> exclude");
				}
			}
		}
		
		for(Device device : all_devices)
		{
			if(!(used_devices.contains(device)))
			{
				exclude(device);
				logger.debug("Device: " + device.getName() + " not used -> exclude");
			}
		}
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