package de.ptb.epics.eve.data.scandescription;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.log4j.Logger;

import de.ptb.epics.eve.data.EventTypes;
import de.ptb.epics.eve.data.measuringstation.IMeasuringStation;
import de.ptb.epics.eve.data.measuringstation.Option;
import de.ptb.epics.eve.data.measuringstation.event.DetectorEvent;
import de.ptb.epics.eve.data.measuringstation.event.Event;
import de.ptb.epics.eve.data.measuringstation.event.ScheduleEvent;
import de.ptb.epics.eve.data.scandescription.errors.IModelError;
import de.ptb.epics.eve.data.scandescription.errors.IModelErrorProvider;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateProvider;
import de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent;

/**
 * <code>ScanDescription</code> is the representation of a scan. It contains 
 * all components necessary to describe a scan (e.g. chains, scan modules).
 * 
 * @author Stephan Rehfeld <stephan.rehfeld( -at -) ptb.de>
 * @author Hartmut Scherr
 * @author Marcus Michalsky
 */
public class ScanDescription implements IModelUpdateProvider,
		IModelUpdateListener, IModelErrorProvider, PropertyChangeListener {
	
	private static Logger logger = 
		Logger.getLogger(ScanDescription.class.getName());

	/** */
	public static final String REPEAT_COUNT_PROP = "repeatCount";
	
	/** */
	public static final String MONITOR_OPTION_PROP = "monitorOption";

	/** */
	public static final String MONITOR_OPTIONS_LIST_PROP = "monitors";
	
	/** */
	public static final String DIRTY_PROP = "dirty";
	
	/** */
	public static final String FILE_NAME_PROP = "fileName";
	
	private String fileName;
	
	// version of the scan description.
	private int inputVersion;
	private int inputRevision;
	private int inputModification;
	
	private int repeatCount;
	
	private Event startEvent;
	
	private List<Chain> chains;
	
	private List<IModelUpdateListener> modelUpdateListener;
	
	private final IMeasuringStation measuringStation;
	
	private boolean dirty;
	
	private PropertyChangeSupport propertyChangeSupport;
	
	private MonitorDelegate monitors;

	/**
	 * Constructs a <code>ScanDescription</code> and adds the S0 start event
	 * to it's event list.
	 *
	 * @param measuringStation the measuring station the scan description is 
	 * 		  based on
	 */
	public ScanDescription(final IMeasuringStation measuringStation) {
		super();
		this.chains = new ArrayList<Chain>();
		this.modelUpdateListener = new ArrayList<IModelUpdateListener>();
		// default start event
		Chain chain = new Chain(0);
		ScanModule sm = new ScanModule(0);
		chain.add(sm);
		startEvent = new ScheduleEvent(sm);
		this.fileName = "";
		this.measuringStation = measuringStation;
		this.dirty = false;
		this.monitors = new MonitorDelegate(this);
		this.monitors.setType(MonitorOption.NONE);
		this.monitors.addPropertyChangeListener(MonitorDelegate.TYPE_PROP, this);
		this.propertyChangeSupport = new PropertyChangeSupport(this);
	}

	/**
	 * Adds a chain to the scan description. 
	 * 
	 * @param chain the chain that should be added
	 * @return <code>true</code> if the chain was added, 
	 * 		   <code>false</code> otherwise
	 */
	public boolean add(final Chain chain) {
		chain.setScanDescription(this);
		boolean returnValue = chains.add(chain);
		chain.addModelUpdateListener(this);
		updateListeners();
		return returnValue;
	}

	/**
	 * Removes a chain from the scan description.
	 * 
	 * @param chain the chain that should be removed
	 * @return <code>true</code> if the chain was removed, 
	 * 		   <code>false</code> otherwise
	 */
	public boolean remove(final Chain chain) {
		boolean returnValue = chains.remove(chain);
		chain.removeModelUpdateListener(this);
		updateListeners();
		return returnValue;
	}
	
	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName) {
		this.propertyChangeSupport.firePropertyChange(
				ScanDescription.FILE_NAME_PROP, this.fileName,
				this.fileName = fileName);
	}

	/**
	 * Returns the version of the scan description.
	 * 
	 * @return the version of the scan description.
	 */
	public String getVersion() {
		return String.valueOf(inputVersion) + "." + 
			   String.valueOf(inputRevision) + "." + 
			   String.valueOf(inputModification);
	}

	/**
	 * Sets the version of the scan description.
	 * 
	 * @param version The version of the scan description.
	 */
	public void setVersion(final String version) {
		String[] versionArray = version.split("\\.");
		if(versionArray.length == 3) {
			inputVersion =  Integer.parseInt(versionArray[0]);
			inputRevision =  Integer.parseInt(versionArray[1]);
			inputModification =  Integer.parseInt(versionArray[2]);
		}
	}

	/**
	 * Gives back the repeat count of the scan description.
	 * 
	 * @return repeatCount The number of repeats of the scan description.
	 */
	public int getRepeatCount() {
		return this.repeatCount;
	}

	/**
	 * Sets the repeat count of the scan description.
	 * 
	 * @param repeatCount the scan will be repeated repeatCount times
	 */
	public void setRepeatCount(final int repeatCount) {
		this.propertyChangeSupport.firePropertyChange(REPEAT_COUNT_PROP, 
				this.repeatCount, this.repeatCount=repeatCount);
		updateListeners();
	}
	
	/**
	 * Returns a list holding all chains.
	 * 
	 * @return a list holding all chain.
	 */
	public List<Chain> getChains() {
		return new ArrayList<Chain>(this.chains);
	}

	/**
	 * Returns a list of monitored options.
	 * 
	 * @return a list of monitored options
	 */
	public List<Option> getMonitors() {
		return this.monitors.getMonitors();
	}

	/**
	 * Returns the type of the monitor options.
	 * 
	 * @return a MonitorOption for the monitor option type.
	 */
	public MonitorOption getMonitorOption() {
		return this.monitors.getType();
	}

	/**
	 * Sets the type of the monitor options.
	 * @param monitorOption the selection of monitored options will be 
	 * specific by monitorOption
	 */
	public void setMonitorOption(final MonitorOption monitorOption) {
		this.monitors.setType(monitorOption);
		updateListeners();
	}
	
	/**
	 * Returns the chain corresponding to the given id.
	 * 
	 * @param chainId the id of the chain
	 * @return the chain corresponding to the given id or 
	 * 		   <code>null</code> if none
	 */
	public Chain getChain(int chainId) {
		Chain retChain = null;
		for (Chain chain : this.chains) {
			if (chain.getId() == chainId) {
				retChain = chain;
			}
		}
		return retChain;
	}

	/**
	 * Returns a default start event for chains without start event tag
	 * this is a hack to not break existing code
	 * 
	 * @return the default StartEvent
	 */
	public Event getDefaultStartEvent() {
		return this.startEvent;
	}
	
	/**
	 * Returns a valid id for a plot.
	 * 
	 * @return a valid id for a plot
	 */
	public int getAvailablePlotId() {
		List<Integer> plotIds = new ArrayList<Integer>();
		for(Chain ch : this.chains) {
			for(ScanModule sm : ch.getScanModules()) {
				for(PlotWindow pw : sm.getPlotWindows()) {
					plotIds.add(pw.getId());
				}
			}
		}
		Collections.sort(plotIds);
		int i=1;
		while(plotIds.contains(i)) {
			i++;
		}
		return i;
	}
	
	/**
	 * This method returns the used measuring station of this scan description.
	 * 
	 * @return The used measuring station. Never returns 'null'.
	 */
	public IMeasuringStation getMeasuringStation() {
		return this.measuringStation;
	}

	/**
	 * @return the dirty
	 */
	public boolean isDirty() {
		return dirty;
	}

	/**
	 * @param dirty the dirty to set
	 */
	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}
	
	/**
	 * Adds an option to the list of monitors
	 * @param option the option to add
	 * @since 1.12
	 */
	public void addMonitor(Option option) {
		try {
			this.monitors.add(option);
			this.propertyChangeSupport.firePropertyChange(
					ScanDescription.MONITOR_OPTIONS_LIST_PROP, null, monitors);
		} catch (UnsupportedOperationException e) {
			logger.error("Monitor could not be added: " + e.getMessage());
		}
	}
	
	/**
	 * Removes an option to the list of monitors
	 * @param option the option to remove
	 * @since 1.12
	 */
	public void removeMonitor(Option option) {
		try {
			this.monitors.remove(option);
			this.propertyChangeSupport.firePropertyChange(
					ScanDescription.MONITOR_OPTIONS_LIST_PROP, null, monitors);
		} catch (UnsupportedOperationException e) {
			logger.error("Monitor could not be added: " + e.getMessage());
		}
	}

	/**
	 * Checks the list of monitors for invalid entries and removes them.
	 * <p>
	 * Necessary due to devices lost during SCML file loading.
	 */
	public void checkForOrphanedMonitors() {
		for (Option o : new CopyOnWriteArrayList<Option>(this.getMonitors())) {
			if (o.getParent() == null) {
				this.monitors.remove(o);
				if (logger.isDebugEnabled()) {
					logger.debug("removed orphaned monitor " + o.getName());
				}
			}
		}
	}
	
	/**
	 * Checks whether a detector ready event of a given channel is used
	 * 
	 * @param channel the channel of the detector ready event
	 * @return <code>true</code> if a detector ready event of the given channel 
	 * 		is used, <code>false</code> otherwise
	 * @author Marcus Michalsky
	 * @since 1.19
	 */
	public boolean isUsedAsEvent(Channel channel) {
		for (Chain chain : this.chains) {
			if (isEventOfList(channel, chain.getPauseEvents()) ||
					isEventOfList(channel, chain.getRedoEvents()) ||
					isEventOfList(channel, chain.getBreakEvents()) ||
					isEventOfList(channel, chain.getStopEvents())) {
				return true;
			}
			for (ScanModule sm : chain.getScanModules()) {
				if (isEventOfList(channel, sm.getPauseEvents()) ||
						isEventOfList(channel, sm.getRedoEvents()) ||
						isEventOfList(channel, sm.getBreakEvents()) ||
						isEventOfList(channel, sm.getTriggerEvents())) {
					return true;
				}
				for (Channel smChannel : sm.getChannels()) {
					if (isEventOfList(channel, smChannel.getRedoEvents())) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	/*
	 * Checks whether the given channel is used as (detector) event in the 
	 * given list.
	 * (Helper for #isUsedAsEvent(Channel))
	 */
	private boolean isEventOfList(Channel channel, List<ControlEvent> events) {
		for (ControlEvent e : events) {
			if (e.getEventType() != EventTypes.DETECTOR) {
				continue;
			}
			DetectorEvent detectorEvent = (DetectorEvent)e.getEvent();
			if (detectorEvent.getChannel().getAbstractDevice().getID().equals(
					channel.getAbstractDevice().getID())
					&& detectorEvent.getChannel().getScanModule().getChain()
							.getId() == channel.getScanModule().getChain()
							.getId()
					&& detectorEvent.getChannel().getScanModule().getId() == channel
							.getScanModule().getId()) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Returns all options of devices used in the scan.
	 * <p>
	 * Specific Scanmodules (e.g. save axes positions / save channel values) 
	 * are ignored. 
	 * 
	 * @return all options of devices used in the scan
	 * @since 1.19
	 * @author Marcus Michalsky
	 */
	public List<Option> getMonitorOptions() {
		Set<Option> monitors = new HashSet<Option>();
		
		for(Chain chain : this.getChains()) {
			for(ScanModule sm : chain.getScanModules()) {
				if (!sm.getType().equals(ScanModuleTypes.CLASSIC)) {
					continue;
				}
			
				for(Axis a : sm.getAxes()) {
					for(Option o : a.getMotorAxis().getOptions()) {
						if(o.isMonitor()) {
							monitors.add(o);
						}
					}
					for(Option o : a.getMotorAxis().getMotor().getOptions()) {
						if(o.isMonitor()) {
							monitors.add(o);
						}
					}
				}
				for(Channel ch : sm.getChannels()) {
					for(Option o : ch.getDetectorChannel().getOptions()) {
						if(o.isMonitor()) {
							monitors.add(o);
						}
					}
					for(Option o : ch.getDetectorChannel().getDetector().getOptions()) {
						if(o.isMonitor()) {
							monitors.add(o);
						}
					}
				}
				for(Prescan prescan : sm.getPrescans()) {
					if(prescan.isOption()) {
						monitors.add((Option)prescan.getAbstractDevice());
						Option o = (Option)prescan.getAbstractDevice();
						if(o.isMonitor()) {
							monitors.add(o);
						}
					}
					if(prescan.isDevice()) {
						for(Option o : prescan.getAbstractDevice().getOptions()) {
							if(o.isMonitor()) {
								monitors.add(o);
							}
						}
					}
				}
				for(Postscan postscan : sm.getPostscans()) {
					if(postscan.isOption()) {
						monitors.add((Option)postscan.getAbstractDevice());
						Option o = (Option)postscan.getAbstractDevice();
						if(o.isMonitor()) {
							monitors.add(o);
						}
					}
					if(postscan.isDevice()) {
						for(Option o : postscan.getAbstractDevice().getOptions()) {
							if(o.isMonitor()) {
								monitors.add(o);
							}
						}
					}
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
	@Override
	public void updateEvent(final ModelUpdateEvent modelUpdateEvent) {
		if(logger.isDebugEnabled()) {
			if(modelUpdateEvent != null) {
				logger.debug(modelUpdateEvent.getSender());
			}
		}
		updateListeners();
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
	public List<IModelError> getModelErrors() {
		final List<IModelError> errorList = new ArrayList<IModelError>();
		final Iterator<Chain> it = this.chains.iterator();
		while(it.hasNext()) {
			errorList.addAll(it.next().getModelErrors());
		}
		return errorList;
	}
	
	/*
	 * 
	 */
	private void updateListeners() {
		final CopyOnWriteArrayList<IModelUpdateListener> list = 
			new CopyOnWriteArrayList<IModelUpdateListener>(this.modelUpdateListener);
		
		Iterator<IModelUpdateListener> it = list.iterator();
		
		while(it.hasNext()) {
			it.next().updateEvent(new ModelUpdateEvent(this, null));
		}
	}
	
	/**
	 * @see java.beans.PropertyChangeSupport#addPropertyChangeListener(String, PropertyChangeListener)
	 */
	public void addPropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		this.propertyChangeSupport.addPropertyChangeListener(propertyName,
				listener);
	}

	/**
	 * @see java.beans.PropertyChangeSupport#removePropertyChangeListener(String, PropertyChangeListener)
	 */
	public void removePropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		this.propertyChangeSupport.removePropertyChangeListener(propertyName,
				listener);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void propertyChange(PropertyChangeEvent e) {
		if (e.getSource() == this.monitors) {
			this.propertyChangeSupport.firePropertyChange(
					ScanDescription.MONITOR_OPTION_PROP, e.getOldValue(),
					e.getNewValue());
			this.propertyChangeSupport.firePropertyChange(
					ScanDescription.MONITOR_OPTIONS_LIST_PROP, null,
					this.getMonitors());
		}
	}
}