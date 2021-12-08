package de.ptb.epics.eve.data.measuringstation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.ptb.epics.eve.data.AutoAcquireTypes;
import de.ptb.epics.eve.data.measuringstation.event.Event;

/**
 *  
 * @author Hartmut Scherr
 */
public abstract class AbstractMeasuringStation implements IMeasuringStation {
	protected List<Event> events;
	protected final List<PlugIn> plugins;
	protected final List<Device> devices;
	protected final List<Motor> motors;
	protected final List<Detector> detectors;
	
	protected final Map<String, Event> eventsMap;
	protected final Map<String, PlugIn> pluginsMap;
	protected Map<String, MotorAxis> motorAxisMap;
	protected final Map<String, DetectorChannel> detectorChannelsMap;
	protected final Map<String, AbstractPrePostscanDevice> prePostscanDeviceMap;
	
	protected Map<String, List<AbstractDevice>> classMap;
	
	public AbstractMeasuringStation() {
		this.events = new ArrayList<Event>();
		this.plugins = new ArrayList<PlugIn>();
		this.devices = new ArrayList<Device>();
		this.motors = new ArrayList<Motor>();
		this.detectors = new ArrayList<Detector>();
		this.eventsMap = new HashMap<String, Event>();
		this.pluginsMap = new HashMap< String, PlugIn>();
		this.motorAxisMap = new HashMap<String, MotorAxis>();
		this.detectorChannelsMap = new HashMap<String, DetectorChannel>();
		this.prePostscanDeviceMap = new HashMap<String, AbstractPrePostscanDevice>();
		this.classMap = new HashMap<String, List<AbstractDevice>>();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, MotorAxis> getMotorAxes() {
		return motorAxisMap;
	}

	/**
	 * {@inheritDoc}
	 * @since 1.31
	 */
	@Override
	public Map<String, DetectorChannel> getDetectorChannels() {
		return detectorChannelsMap;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @since 1.19
	 * @author Marcus Michalsky
	 */
	@Override
	public List<Option> getMonitorOptions() {
		Set<Option> monitors = new HashSet<Option>();
		for(Detector d : this.getDetectors()) {
			for(Option o : d.getOptions()) {
				if(o.isMonitor()) {
					monitors.add(o);
				}
			}
			for(DetectorChannel ch : d.getChannels()) {
				for(Option o : ch.getOptions()) {
					if(o.isMonitor()) {
						monitors.add(o);
					}
				}
			}
		}
		for(Motor m : this.getMotors()) {
			for(Option o : m.getOptions()) {
				if(o.isMonitor()) {
					monitors.add(o);
				}
			}
			for(MotorAxis ma : m.getAxes()) {
				for(Option o : ma.getOptions()) {
					if(o.isMonitor()) {
						monitors.add(o);
					}
				}
			}
		}
		for(Device dev : this.getDevices()) {
			for(Option o : dev.getOptions()) {
				if(o.isMonitor()) {
					monitors.add(o);
				}
			}
		}
		List<Option> monitorList = new ArrayList<Option>(monitors);
		Collections.sort(monitorList);
		return monitorList;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public List<Option> getAutoAcquireOptions() {
		Set<Option> autoacqOptions = new HashSet<>();
		
		for(Detector d : this.getDetectors()) {
			for(Option o : d.getOptions()) {
				if(!o.getAutoAcquire().equals(AutoAcquireTypes.NO)) {
					autoacqOptions.add(o);
				}
			}
			for(DetectorChannel ch : d.getChannels()) {
				for(Option o : ch.getOptions()) {
					if(!o.getAutoAcquire().equals(AutoAcquireTypes.NO)) {
						autoacqOptions.add(o);
					}
				}
			}
		}
		for(Motor m : this.getMotors()) {
			for(Option o : m.getOptions()) {
				if(!o.getAutoAcquire().equals(AutoAcquireTypes.NO)) {
					autoacqOptions.add(o);
				}
			}
			for(MotorAxis ma : m.getAxes()) {
				for(Option o : ma.getOptions()) {
					if(!o.getAutoAcquire().equals(AutoAcquireTypes.NO)) {
						autoacqOptions.add(o);
					}
				}
			}
		}
		for(Device dev : this.getDevices()) {
			for(Option o : dev.getOptions()) {
				if(!o.getAutoAcquire().equals(AutoAcquireTypes.NO)) {
					autoacqOptions.add(o);
				}
			}
		}
		List<Option> acquireList = new ArrayList<>(autoacqOptions);
		Collections.sort(acquireList); // TODO necessary ?
		return acquireList;
	}
	
	/**
	 * Returns the {@link de.ptb.epics.eve.data.measuringstation.AbstractDevice}
	 * with the given <code>id</code> or <code>null</code> if none.
	 * 
	 * @param id the id of the device
	 * @return the device with the given id
	 */
	public AbstractDevice getAbstractDeviceById(String id) {
		for(Motor m : this.motors) {
			if (m.getID().equals(id)) {
				return m;
			}
			for(Option o : m.getOptions()) {
				if (o.getID().equals(id)) {
					return o;
				}
			}
			for(MotorAxis ma : m.getAxes()) {
				if (ma.getID().equals(id)) {
					return ma;
				}
				for (Option o : ma.getOptions()) {
					if (o.getID().equals(id)) {
						return o;
					}
				}
			}
		}
		
		for(Detector d : this.detectors) {
			if (d.getID().equals(id)) {
				return d;
			}
			for(Option o : d.getOptions()) {
				if (o.getID().equals(id)) {
					return o;
				}
			}
			for(DetectorChannel ch : d.getChannels()) {
				if (ch.getID().equals(id)) {
					return ch;
				}
				for(Option o : ch.getOptions()) {
					if (o.getID().equals(id)) {
						return o;
					}
				}
			}
		}
		
		for(Device dev : this.devices) {
			if (dev.getID().equals(id)) {
				return dev;
			}
			for(Option o : dev.getOptions()) {
				if (o.getID().equals(id)) {
					return o;
				}
			}
		}
		
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public MotorAxis getMotorAxisByName(String name) {
		for (MotorAxis axis : this.getMotorAxes().values()) {
			if (axis.getName().equals(name)) {
				return axis;
			}
		}
		return null;
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
	public List<PlugIn> getPlugins() {
		return new ArrayList<PlugIn>(this.plugins);
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
	public List<Motor> getMotors() {
		return new ArrayList<Motor>(this.motors);
	}
	
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
	public Set<String> getClassNameList(){
		return classMap.keySet();
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
	public AbstractPrePostscanDevice getPrePostscanDeviceById(final String id) {
		return this.prePostscanDeviceMap.get(id);
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
	 * @throws IllegalArgumentException if the argument is <code>null</code>
	 * @throws IllegalArgumentException if identifier is an empty string
	 * @deprecated
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
}