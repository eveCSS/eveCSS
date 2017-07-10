package de.ptb.epics.eve.data.measuringstation.filter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import de.ptb.epics.eve.data.measuringstation.AbstractDevice;
import de.ptb.epics.eve.data.measuringstation.Detector;
import de.ptb.epics.eve.data.measuringstation.DetectorChannel;
import de.ptb.epics.eve.data.measuringstation.Device;
import de.ptb.epics.eve.data.measuringstation.Motor;
import de.ptb.epics.eve.data.measuringstation.MotorAxis;
import de.ptb.epics.eve.data.measuringstation.Option;
import de.ptb.epics.eve.data.measuringstation.PlugIn;
import de.ptb.epics.eve.data.measuringstation.event.Event;
import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.data.scandescription.Chain;
import de.ptb.epics.eve.data.scandescription.Channel;
import de.ptb.epics.eve.data.scandescription.ControlEvent;
import de.ptb.epics.eve.data.scandescription.Positioning;
import de.ptb.epics.eve.data.scandescription.Postscan;
import de.ptb.epics.eve.data.scandescription.Prescan;
import de.ptb.epics.eve.data.scandescription.ScanDescription;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.data.scandescription.channelmode.ChannelModes;
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
 * Notice that this implementation of 
 * {@link de.ptb.epics.eve.data.measuringstation.IMeasuringStation} does not 
 * support adding devices.
 * 
 * @author ?
 * @author Marcus Michalsky
 * @author Hartmut Scherr
 */
public class ExcludeFilter extends MeasuringStationFilter {
	private static Logger logger = 
			Logger.getLogger(ExcludeFilter.class.getName());

	private boolean filterInProgress;
	
	/**
	 * Constructs an <code>ExcludeFilter</code>.
	 */
	public ExcludeFilter() {
		super();
		filterInProgress = false;
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
		while (motorIterator.hasNext()) {
			currentMotor = motorIterator.next();
			if (this.excludeList.contains(currentMotor)) {
				continue;
			}
			axisIterator = currentMotor.axisIterator();
			while (axisIterator.hasNext()) {
				currentAxis = axisIterator.next();
				if (this.excludeList.contains(currentAxis)) {
					continue;
				}
				int i = 0;
				for (Iterator<String> iterator = identifier.iterator(); iterator
						.hasNext();) {
					final String test = iterator.next();
					if (currentAxis.getFullIdentifyer().compareToIgnoreCase(
							test) > 0) {
						i++;
					} else {
						break;
					}
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

		while (detectorIterator.hasNext()) {
			Detector currentDetector = detectorIterator.next();
			if (this.excludeList.contains(currentDetector)) {
				continue;
			}
			Iterator<DetectorChannel> channelIterator = currentDetector
					.channelIterator();
			while (channelIterator.hasNext()) {
				DetectorChannel currentChannel = channelIterator.next();
				if (this.excludeList.contains(currentChannel)) {
					continue;
				}
				int i = 0;
				for (Iterator<String> iterator = identifier.iterator(); iterator
						.hasNext();) {
					final String test = iterator.next();
					if (currentDetector.getFullIdentifyer()
							.compareToIgnoreCase(test) > 0) {
						i++;
					} else {
						break;
					}
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
		while (deviceIterator.hasNext()) {
			Device currentDevice = deviceIterator.next();
			if (this.excludeList.contains(currentDevice)) {
				continue;
			}
			int i = 0;
			for (Iterator<String> iterator = identifier.iterator(); iterator
					.hasNext();) {
				final String test = iterator.next();
				if (currentDevice.getFullIdentifyer().compareToIgnoreCase(test) > 0) {
					i++;
				} else {
					break;
				}
			}
			identifier.add(i, currentDevice.getFullIdentifyer());
		}

		Iterator<Motor> motorIterator = this.motors.iterator();
		while (motorIterator.hasNext()) {
			Motor currentMotor = motorIterator.next();
			if (this.excludeList.contains(currentMotor)) {
				continue;
			}
			Iterator<Option> optionIterator = currentMotor.optionIterator();
			while (optionIterator.hasNext()) {
				Option currentOption = optionIterator.next();
				if (this.excludeList.contains(currentOption)) {
					continue;
				}
				int i = 0;
				for (Iterator<String> iterator = identifier.iterator(); iterator
						.hasNext();) {
					final String test = iterator.next();
					if (currentOption.getFullIdentifyer().compareToIgnoreCase(
							test) > 0) {
						i++;
					} else {
						break;
					}
				}
				identifier.add(i, currentOption.getFullIdentifyer());
			}

			Iterator<MotorAxis> axisIterator = currentMotor.axisIterator();
			while (axisIterator.hasNext()) {
				MotorAxis currentAxis = axisIterator.next();
				if (this.excludeList.contains(currentAxis)) {
					continue;
				}
				Iterator<Option> optionIterator2 = currentAxis.getOptions()
						.iterator();
				while (optionIterator2.hasNext()) {
					Option currentOption = optionIterator2.next();
					if (this.excludeList.contains(currentOption)) {
						continue;
					}
					int i = 0;
					for (Iterator<String> iterator = identifier.iterator(); iterator
							.hasNext();) {
						final String test = iterator.next();
						if (currentOption.getFullIdentifyer()
								.compareToIgnoreCase(test) > 0) {
							i++;
						} else {
							break;
						}
					}
					identifier.add(i, currentOption.getFullIdentifyer());
				}
			}
		}

		Iterator<Detector> detectorIterator = this.detectors.iterator();
		while (detectorIterator.hasNext()) {
			Detector currentDetector = detectorIterator.next();
			if (this.excludeList.contains(currentDetector)) {
				continue;
			}
			Iterator<Option> optionIterator = currentDetector.getOptions()
					.iterator();
			while (optionIterator.hasNext()) {
				Option currentOption = optionIterator.next();
				if (this.excludeList.contains(currentOption)) {
					continue;
				}
				int i = 0;
				for (Iterator<String> iterator = identifier.iterator(); iterator
						.hasNext();) {
					final String test = iterator.next();
					if (currentOption.getFullIdentifyer().compareToIgnoreCase(
							test) > 0) {
						i++;
					} else {
						break;
					}
				}
				identifier.add(i, currentOption.getFullIdentifyer());
			}

			Iterator<DetectorChannel> channelsIterator = currentDetector
					.channelIterator();
			while (channelsIterator.hasNext()) {
				DetectorChannel currentChannel = channelsIterator.next();
				if (this.excludeList.contains(currentChannel)) {
					continue;
				}
				Iterator<Option> optionIterator2 = currentChannel.getOptions()
						.iterator();
				while (optionIterator2.hasNext()) {
					Option currentOption = optionIterator2.next();
					if (this.excludeList.contains(currentOption)) {
						continue;
					}
					int i = 0;
					for (Iterator<String> iterator = identifier.iterator(); iterator
							.hasNext();) {
						final String test = iterator.next();
						if (currentOption.getFullIdentifyer()
								.compareToIgnoreCase(test) > 0) {
							i++;
						} else {
							break;
						}
					}
					identifier.add(i, currentOption.getFullIdentifyer());
				}
			}
		}
		return identifier;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<AbstractDevice> getDeviceList(String classname){
		if (classMap.containsKey(classname)) {
			return classMap.get(classname);
		}
		return null;
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
			
			// include motors not in the exclude list
			for(final Motor motor : this.getSource().getMotors()) {
				// iterate over each motor in the source
				if(!this.excludeList.contains(motor)) {
					// it is not in the exclude list
					
					// clone the whole motor
					final Motor m = (Motor)motor.clone();
					
					// delete options & axes which are in the exclude list
					for(final AbstractDevice d : this.excludeList) {
						if(d instanceof MotorAxis) {
							m.remove((MotorAxis)d);
						}
						if(d instanceof Option) {
							m.remove((Option)d);
						}
					}
					
					// for the remaining axes: remove options which are in the 
					// exclude list
					for(MotorAxis ma : m.getAxes()) {
						for(AbstractDevice d : this.excludeList) {
							if(d instanceof Option) {
								ma.remove((Option)d);
							}
						}
					}
					
					// add the (rest of the) motor to the list of motors
					this.motors.add(m);
				} // end of: if motor not in exclude list
			}
			// (if the motor itself is in the exclude list, nothing happens
			
			// include detectors not in the exclude list
			for(final Detector detector : this.getSource().getDetectors()) {
				// iterate over each detector in the source
				if(!this.excludeList.contains(detector)) {
					// it is not in the exclude list
					
					// clone the whole detector
					final Detector m = (Detector)detector.clone();
					
					// delete options and channels which are in the exclude list
					for(final AbstractDevice d : this.excludeList) {
						if(d instanceof DetectorChannel) {
							m.remove((DetectorChannel)d);
						}
						if(d instanceof Option){
							m.remove((Option)d);
						}
					}
					// for the remaining channels remove options which are in 
					// the exclude list
					for(DetectorChannel ch : m.getChannels()) {
						for(AbstractDevice d : this.excludeList) {
							if(d instanceof Option) {
								ch.remove((Option)d);
							}
						}
					}
					// add the (rest of the) detector to the list of detectors
					this.detectors.add(m);
				}
			}
			
			for(Device device : getSource().getDevices()) {
				if(!excludeList.contains(device)) {
					final Device d = (Device)device.clone();
					
					for(final AbstractDevice ad : excludeList) {
						if(ad instanceof Option) {
							d.remove((Option)ad);
						}
					}
					this.devices.add(d);
				}
			}

			// *******************
			// *** build  maps ***
			// *******************
			
			// build plug in map
			for(final PlugIn plugIn : this.plugins) {
				this.pluginsMap.put(plugIn.getName(), plugIn);
			}
			
			// build motor axis and prepostscandevice maps
			for(final Motor motor : this.motors) {
				// add motor options (not in the exclude list)
				for(final Option option : motor.getOptions()) {
					if(!this.excludeList.contains(option)) {
						this.prePostscanDeviceMap.put(option.getID(), option);
					}
				}
				
				// add motor axes (not in the exclude list) AND
				// add axis options (not in the exclude list)
				for(final MotorAxis motorAxis : motor.getAxes()) {
					if(!this.excludeList.contains(motorAxis)) {
						this.motorAxisMap.put(motorAxis.getID(), motorAxis);
						for(final Option option : motorAxis.getOptions()) {
							if( !this.excludeList.contains(option)) {
								this.prePostscanDeviceMap.put(
										option.getID(), option);
							}
						}
					}
				}
			}
			
			// build detector channel and prepostscandevice map
			for(final Detector detector : this.detectors) {
				// add detector options (not in the exclude list) 
				for(final Option option : detector.getOptions()) {
					if (!this.excludeList.contains(option)) {
						this.prePostscanDeviceMap.put(option.getID(), option);
					}
				}
				// add detector channels (not in the exclude list) AND
				// add channel options (not in the exclude list)
				for(final DetectorChannel detectorChannel : 
							detector.getChannels()) {
					if(!this.excludeList.contains(detectorChannel)) {
						this.detectorChannelsMap.put(
								detectorChannel.getID(), detectorChannel);
						for(final Option option : detectorChannel.getOptions()) {
							if (!this.excludeList.contains(option)) {
								this.prePostscanDeviceMap.put(
										option.getID(), option);
							}
						}
					}
				}
			}
			
			// build device map
			for(final Device device : this.devices) {
				this.prePostscanDeviceMap.put(device.getID(), device);
				
				for(Option option : device.getOptions()) {
					if(!this.excludeList.contains(option)) {
						this.prePostscanDeviceMap.put(option.getID(), option);
					}
				}
			}
			
			// build class map
			buildClassMap();
			
			// build event map
			for(final Event event : this.events) {
				this.eventsMap.put(event.getId(), event);
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
		if(!filterInProgress) {
			this.updateEvent(new ModelUpdateEvent(this, null));
		}
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
		if(!filterInProgress) {
			this.updateEvent(new ModelUpdateEvent(this, null));
		}
	}
	
	/**
	 * Excludes devices that are not used in the given
	 * {@link de.ptb.epics.eve.data.scandescription.ScanDescription}.
	 * <p>
	 * The exclusion works as follows:
	 * <ul>
	 * <li>a {@link de.ptb.epics.eve.data.measuringstation.Motor} is excluded if
	 * <u>all</u> of the following conditions are met:
	 * <ul>
	 * <li>none of its options are used</li>
	 * <li>none of its axes are used</li>
	 * <li>none of its axes options (pre or post scan) are used</li>
	 * </ul>
	 * </li>
	 * <li>an {@link de.ptb.epics.eve.data.measuringstation.Option} of a
	 * {@link de.ptb.epics.eve.data.measuringstation.Motor} is excluded if it is
	 * not used.</li>
	 * <li>a {@link de.ptb.epics.eve.data.measuringstation.MotorAxis} is
	 * excluded if <u>all</u> of the following conditions are met:
	 * <ul>
	 * <li>the motor axis itself is not used</li>
	 * <li>none of its options are used</li>
	 * </ul>
	 * </li>
	 * <li>an {@link de.ptb.epics.eve.data.measuringstation.Option} of a
	 * {@link de.ptb.epics.eve.data.measuringstation.MotorAxis} is excluded if
	 * it is not used.</li>
	 * <li>a {@link de.ptb.epics.eve.data.measuringstation.Detector} will be
	 * excluded if <u>all</u> of the following conditions are met:
	 * <ul>
	 * <li>none of its options are used</li>
	 * <li>none of its channels are used</li>
	 * <li>none of its channels options are used</li>
	 * </ul>
	 * </li>
	 * <li>an {@link de.ptb.epics.eve.data.measuringstation.Option} of a
	 * {@link de.ptb.epics.eve.data.measuringstation.Detector} is excluded if it
	 * is not used.</li>
	 * <li>a {@link de.ptb.epics.eve.data.measuringstation.DetectorChannel} will
	 * be excluded if <u>all</u> of the following conditions are met:
	 * <ul>
	 * <li>the detector channel itself is not used</li>
	 * <li>none of its options are used</li>
	 * </ul>
	 * </li>
	 * <li>an {@link de.ptb.epics.eve.data.measuringstation.Option} of a
	 * {@link de.ptb.epics.eve.data.measuringstation.DetectorChannel} is
	 * excluded if it is not used.</li>
	 * </ul>
	 * 
	 * <u>Note:</u>
	 * <ul>
	 * <li>A device is also considered as "used" if it is part of an event.</li>
	 * <li>An option is also considered as "used" if it should be monitored.</li>
	 * </ul>
	 * 
	 * @param scandescription
	 *            the
	 *            {@link de.ptb.epics.eve.data.scandescription.ScanDescription}
	 *            containing the devices which will remain (all other are
	 *            excluded)
	 */
	public void excludeUnusedDevices(ScanDescription scandescription) {
		/*
		 * implementation works as follows:
		 * Hash sets are created for motor axes, detector channels, devices 
		 * and options. Hash because of performance reasons (they will be 
		 * accessed frequently) and Sets because of uniqueness (marking a 
		 * device as used if it was already doesn't hurt).
		 * The whole scan description is iterated (each scan module of each 
		 * chain) and all devices and options found are added to the 
		 * corresponding used lists.
		 * The second part iterates through all available devices and options 
		 * of the measuring station (source). If a device is not used (it is 
		 * not in the used list) it is excluded.
		 */
		
		filterInProgress = true;
		
		// all available motors
		List<Motor> allMotors = getSource().getMotors();
		// all available detectors
		List<Detector> allDetectors = getSource().getDetectors();
		// all available devices
		List<Device> allDevices = getSource().getDevices();
		
		// Sets to add used devices to
		HashSet<MotorAxis> usedMotorAxes = new HashSet<MotorAxis>();
		HashSet<DetectorChannel> usedDetectorChannels = 
				new HashSet<DetectorChannel>();
		HashSet<Device> usedDevices = new HashSet<Device>();
		HashSet<Option> usedOptions = new HashSet<Option>();
		
		
		// iterate through chains to identify used devices
		for(Chain chain : scandescription.getChains()) {
			// iterate through each scan module of the chain
			for(ScanModule sm : chain.getScanModules()) {
				// iterate axes
				for(Axis a : sm.getAxes()) {
					usedMotorAxes.add(a.getMotorAxis());
				}
				
				// iterate channels
				for(Channel ch : sm.getChannels()) {
					usedDetectorChannels.add(ch.getDetectorChannel());
					
					if (!ch.getChannelMode().equals(ChannelModes.STANDARD)) {
						continue;
					}
					
					List<ControlEvent> chEvents = new ArrayList<ControlEvent>();
					chEvents.addAll(ch.getRedoEvents());
					
					for(ControlEvent ce : chEvents) {
						AbstractDevice dev = this.getAbstractDeviceById(
								ce.getDeviceId());
						if (dev instanceof MotorAxis) {
							usedMotorAxes.add((MotorAxis)dev);
						} else if (dev instanceof DetectorChannel) {
							usedDetectorChannels.add((DetectorChannel)dev);
						} else if (dev instanceof Device) {
							usedDevices.add((Device)dev);
						} else if (dev instanceof Option) {
							usedOptions.add((Option)dev);
						}
					}
				}
				
				// iterate prescans
				for(Prescan prescan : sm.getPrescans()) {
					String id = prescan.getAbstractDevice().getID();
					
					if(prescan.isOption()) {
						usedOptions.add((Option)prescan.getAbstractDevice());
					}
					if(prescan.isDevice()) {
						usedDevices.add((Device)getSource().
								getPrePostscanDeviceById(id));
					}
				}
				
				// iterate postscans
				for(Postscan postscan : sm.getPostscans()) {
					String id = postscan.getAbstractDevice().getID();
					
					if(postscan.isOption()) {
						usedOptions.add((Option)postscan.getAbstractDevice());
					}
					if(postscan.isDevice()) {
						usedDevices.add((Device)getSource().
								getPrePostscanDeviceById(id));
					}
				}
				
				for(Positioning positioning : sm.getPositionings()) {
					usedMotorAxes.add(positioning.getMotorAxis());
					usedDetectorChannels.add(positioning.
							getDetectorChannel());
				}
				
				// events of the scan module
				List<ControlEvent> smEvents = new ArrayList<ControlEvent>();
				smEvents.addAll(sm.getPauseEvents());
				smEvents.addAll(sm.getRedoEvents());
				smEvents.addAll(sm.getBreakEvents());
				smEvents.addAll(sm.getTriggerEvents());
				
				for(ControlEvent ce : smEvents) {
					AbstractDevice dev = this.getAbstractDeviceById(
							ce.getDeviceId());
					if (dev instanceof MotorAxis) {
						usedMotorAxes.add((MotorAxis)dev);
					} else if (dev instanceof DetectorChannel) {
						usedDetectorChannels.add((DetectorChannel)dev);
					} else if (dev instanceof Device) {
						usedDevices.add((Device)dev);
					} else if (dev instanceof Option) {
						usedOptions.add((Option)dev);
					}
				}
			}
			
			// events of the chain
			List<ControlEvent> chainEvents = new ArrayList<ControlEvent>();
			chainEvents.addAll(chain.getPauseEvents());
			chainEvents.addAll(chain.getRedoEvents());
			chainEvents.addAll(chain.getBreakEvents());
			chainEvents.addAll(chain.getStopEvents());
			
			for(ControlEvent ce : chainEvents) {
				AbstractDevice dev = this.getAbstractDeviceById(
						ce.getDeviceId());
				if (dev instanceof MotorAxis) {
					usedMotorAxes.add((MotorAxis)dev);
				} else if (dev instanceof DetectorChannel) {
					usedDetectorChannels.add((DetectorChannel)dev);
				} else if (dev instanceof Device) {
					usedDevices.add((Device)dev);
				} else if (dev instanceof Option) {
					usedOptions.add((Option)dev);
				}
			}
		}
		
		// // add monitor options
		// usedOptions.addAll(scandescription.getMonitors());
		
		switch (scandescription.getMonitorOption()) {
			case AS_IN_DEVICE_DEFINITION:
				usedOptions.addAll(this.getSource().getMonitorOptions());
				break;
			case CUSTOM:
				usedOptions.addAll(scandescription.getMonitors());
				break;
			case NONE:
				break;
			case USED_IN_SCAN:
				usedOptions.addAll(scandescription.getMonitorOptions());
				break;
		}
		
		// *******************************************************************
		
		// remove unused by iterating all available devices and check if they 
		// are used by checking its presence in the used_sets...
		
		// iterate over available motors -> exclude their axes if not used
		for(Motor m : allMotors) {
			for(MotorAxis ma : m.getAxes()) {
				if(!(usedMotorAxes.contains(ma))) {
					boolean optionUsed = false;
					for(Option o : ma.getOptions()) {
						if(usedOptions.contains(o)) {
							// option is used, set flag
							optionUsed = true;
						} else {
							// option not used -> exclude
							exclude(o);
							logger.debug("Option " + ma.getName() + ":" + 
									o.getName() + " not used -> exclude");
						}
					}
					
					// if any option is used, the flag was set, 
					// if not -> exclude axis
					if(!optionUsed) {
						exclude(ma);
						logger.debug("Axis " + m.getName() + ":" + 
									ma.getName() + 
									"(and options) not used -> exclude");
					} else {
						logger.debug("Axis " + m.getName() + ":" + 
								ma.getName() + 
								"has at least one used option -> do not exclude");
					}
				} else {
					// motor axis is used -> exclude unused options
					for(Option o : ma.getOptions()) {
						if(!usedOptions.contains(o)) {
							exclude(o);
							logger.debug("Option " + ma.getName() + ":" + 
									o.getName() + " not used -> exclude");
						}
					}
				}
				
				// exclude unused motor options as well
				for(Option o : m.getOptions()) {
					if(!usedOptions.contains(o)) {
						exclude(o);
						logger.debug("Option " + m.getName() + ":" + 
								o.getName() + " not used -> exclude");
					}
				}
			}
		}
		
		// check for motors with no axes AND no options -> exclude them
		Motors:
		for(Motor m : allMotors) {
			for(MotorAxis ma : m.getAxes()) {
				if(usedMotorAxes.contains(ma)) {
					continue Motors;
				}
				for(Option axisOption : ma.getOptions()) {
					if(usedOptions.contains(axisOption)) {
						continue Motors;
					}
				}
			}
			for(Option motorOption : m.getOptions()) {
				if(usedOptions.contains(motorOption)) {
					continue Motors;
				}
			}
			exclude(m);
		}
		
		// iterate over available detectors -> exclude their channels if not used
		for(Detector d : allDetectors) {
			for(DetectorChannel ch : d.getChannels()) {
				if(!(usedDetectorChannels.contains(ch))) {
					boolean optionUsed = false;
					for(Option o : ch.getOptions()) {
						if(usedOptions.contains(o)) {
							// option is used, set flag
							optionUsed = true;
						} else {
							// option not used -> exclude
							exclude(o);
							logger.debug("Option " + ch.getName() + ":" + 
									o.getName() + " not used -> exclude");
						}
					}
					
					// if any option is used, the flag is set, 
					// if not -> exclude channel
					if(!optionUsed) {
						exclude(ch);
						logger.debug("Detector Channel " + d.getName() + 
								":" + ch.getName() + " not used -> exclude");
					}
				} else {
					// detector channel is used -> exclude unused options
					for(Option o : ch.getOptions()) {
						if(!usedOptions.contains(o)) {
							exclude(o);
							logger.debug("Option " + ch.getName() + ":" + 
									o.getName() + " not used -> exclude");
						}
					}
				}
			}
			
			// exclude unused options as well
			for(Option o : d.getOptions()) {
				if(!usedOptions.contains(o)) {
					exclude(o);
					logger.debug("Option " + d.getName() + ":" + 
							o.getName() + " not used -> exclude");
				}
			}
		}
		
		// check for detectors with no channels -> exclude them
		Detectors:
			for(Detector d : allDetectors) {
				for(DetectorChannel ch : d.getChannels()) {
					if(usedDetectorChannels.contains(ch)) {
						continue Detectors;
					}
					for(Option channelOption : ch.getOptions()) {
						if(usedOptions.contains(channelOption)) {
							continue Detectors;
						}
					}
				}
				for(Option detectorOption : d.getOptions()) {
					if(usedOptions.contains(detectorOption)) {
						continue Detectors;
					}
				}
				exclude(d);
			}
		
		// iterate over all devices ->
		for(Device device : allDevices) {
			if(!(usedDevices.contains(device))) {
				boolean optionUsed = false;
				for(Option o : device.getOptions()) {
					if(usedOptions.contains(o)) {
						// option is used, set flag
						optionUsed = true;
						logger.debug("Option " + device.getName() + ":" + 
								o.getName() + " is used");
					} else {
						// option not used -> exclude
						exclude(o);
						logger.debug("Option " + device.getName() + ":" + 
								o.getName() + " not used -> exclude");
					}
				}
				
				// if any option is used, the flag is set, 
				// if not -> exclude device
				if(!optionUsed) {
					exclude(device);
					logger.debug("Device " + device.getID() + " (" +
							device.getName() + ") not used -> exclude");
				}
			} else {
				logger.debug("Device " + device.getName() + " is used");
				// device is used -> exclude unused options
				for(Option o : device.getOptions()) {
					if(!usedOptions.contains(o)) {
						exclude(o);
						logger.debug("Option " + device.getName() + ":" + 
								o.getName() + " not used -> exclude");
					}
				}
			}
		}
		filterInProgress = false;
		
		updateEvent(null);
	}
	
	/*
	 * Be aware of the fact that the locally used devices list is NOT the global 
	 * list of Devices used in the class !
	 */
	private void buildClassMap() {
		this.classMap.clear();
		
		// handle motors
		for(final Motor motor : this.motors) {
			if(motor.getClassName() != null && !motor.getClassName().isEmpty()) {
				// the motor has a non null, non empty class name
				// -> check if this class already exists
				List<AbstractDevice> devices = null;
				if(this.classMap.containsKey(motor.getClassName())) {
					// class already exists -> get it
					devices = this.classMap.get(motor.getClassName());
				} else {
					// new class -> create new list
					devices = new ArrayList<AbstractDevice>();
					// add the new class to the class map
					this.classMap.put(motor.getClassName(), devices);
				}
				// add the motor to the list
				devices.add(motor);
				
				// handle axes
				for(final MotorAxis motorAxis : motor.getAxes()) {
					if (motorAxis.getClassName() != null && 
						!motorAxis.getClassName().isEmpty()) {
						// the axis has a non null, non empty class name
						// -> check if this class already exists
						devices = null;
						if(this.classMap.containsKey(motorAxis.getClassName())) {
							// true -> get class
							devices = this.classMap.get(motorAxis.getClassName());
						} else {
							// false -> create new list
							devices = new ArrayList<AbstractDevice>();
							// add it to the class map
							this.classMap.put(motorAxis.getClassName(), devices);
						}
						devices.add(motorAxis);
					}
				}
			}
		}
		
		// handle detectors
		for(final Detector detector : this.detectors) {
			if (detector.getClassName() != null && 
				!detector.getClassName().isEmpty()) {
				// detector has a non null non empty class name
				List<AbstractDevice> devices = null;
				if(this.classMap.containsKey(detector.getClassName())) {
					// class already exists -> get it
					devices = this.classMap.get(detector.getClassName());
				} else {
					// class does not exist -> create new list
					devices = new ArrayList<AbstractDevice>();
					// add it to the class map
					this.classMap.put(detector.getClassName(), devices);
				}
				devices.add(detector);
				
				// handle channels
				for(final DetectorChannel detectorChannel : detector.getChannels()) {
					if (detectorChannel.getClassName() != null && 
						!detectorChannel.getClassName().isEmpty()) {
						// channel has a non null non empty class name
						devices = null;
						if(this.classMap.containsKey(detectorChannel.getClassName())) {
							// class already exists -> get it
							devices = this.classMap.get(detectorChannel.getClassName());
						} else {
							// class does not exist -> create new list
							devices = new ArrayList<AbstractDevice>();
							// add it to the class map
							this.classMap.put(detectorChannel.getClassName(), devices);
						}
						// add the channel to the class
						devices.add(detectorChannel);
					}
				}
			}
		}
		
		// handle devices
		for(final Device device : this.devices) {
			if(device.getClassName() != null && !device.getClassName().isEmpty()) {
				List<AbstractDevice> devices = null;
				if(this.classMap.containsKey(device.getClassName())) {
					devices = this.classMap.get(device.getClassName());
				} else {
					devices = new ArrayList<AbstractDevice>();
					this.classMap.put(device.getClassName(), devices);
				}
				devices.add(device);
			}
		}
	}
}