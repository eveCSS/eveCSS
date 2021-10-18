package de.ptb.epics.eve.data.scandescription;

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
import de.ptb.epics.eve.data.measuringstation.PlugIn;
import de.ptb.epics.eve.data.measuringstation.event.DetectorEvent;
import de.ptb.epics.eve.data.measuringstation.event.Event;
import de.ptb.epics.eve.data.measuringstation.event.ScheduleEvent;
import de.ptb.epics.eve.data.scandescription.errors.IModelError;
import de.ptb.epics.eve.data.scandescription.errors.IModelErrorProvider;
import de.ptb.epics.eve.data.scandescription.errors.ScanDescriptionError;
import de.ptb.epics.eve.data.scandescription.errors.ScanDescriptionErrorTypes;
import de.ptb.epics.eve.data.scandescription.macro.MacroResolver;
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
		IModelUpdateListener, IModelErrorProvider {
	
	private static final Logger LOGGER = 
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
	
	/** 
	 * @since 1.33
	 */
	public static final String COMMENT_PROP = "comment";
	
	/**
	 * @since 1.33
	 */
	public static final String SAVE_FILE_NAME_PROP = "saveFilename";
	
	/**
	 * @since 1.33
	 */
	public static final String FILE_FORMAT_PROP = "fileFormat";
	
	/**
	 * @since 1.33
	 */
	public static final String RESOLVED_FILENAME_PROP = "resolvedFilename";
	
	/**
	 * @since 1.33
	 */
	public static final String CONFIRM_SAVE_PROP = "confirmSave";
	
	/** 
	 * @since 1.33 
	 */
	public static final String AUTO_INCREMENT_PROP = "autoNumber";
	
	/**
	 * @since 1.33
	 */
	public static final String SAVE_SCAN_DESCRIPTION_PROP = "saveScanDescription";
	
	private String fileName;
	
	// version of the scan description.
	private int inputVersion;
	private int inputRevision;
	private int inputModification;
	
	private int repeatCount;
	
	private String comment;
	private String saveFilename;
	private String resolvedFilename;
	// indicates if the save should be manually confirmed by the user
	private boolean confirmSave;
	// indicates if the datafile name should be extended by an autoincrement #
	private boolean autoNumber;
	// indicates whether the scan description should be saved in the result file
	private boolean saveScanDescription;
	// The PluginController for the Save Plugin
	private PluginController savePluginController;
	
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
		this.chains = new ArrayList<>();
		this.modelUpdateListener = new ArrayList<>();
		// default start event
		Chain chain = new Chain(0);
		ScanModule sm = new ScanModule(0);
		chain.add(sm);
		startEvent = new ScheduleEvent(sm);
		this.fileName = "";
		this.comment = "";
		this.saveFilename = "";
		this.resolvedFilename = "";
		this.autoNumber = true;
		this.saveScanDescription = false;
		this.savePluginController = new PluginController();
		this.savePluginController.addModelUpdateListener(this);
		this.measuringStation = measuringStation;
		this.dirty = false;
		this.monitors = new MonitorDelegate(this);
		this.monitors.setType(MonitorOption.NONE);
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
		return inputVersion + "." + 
			   inputRevision + "." + 
			   inputModification;
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
	 * Returns the comment.
	 * 
	 * @return the comment
	 * @since 1.33
	 */
	public String getComment() {
		return this.comment;
	}
	
	/**
	 * Sets the comment.
	 * 
	 * @param comment the comment that should be set
	 * @throws IllegalArgumentException if <code>comment</code> is 
	 * 			<code>null</code>
	 * @since 1.33
	 */
	public void setComment(final String comment) {
		if (comment == null) {
			throw new IllegalArgumentException(
					"The parameter 'comment' must not be null!");
		}
		String oldValue = this.comment;
		this.comment = comment;
		this.propertyChangeSupport.firePropertyChange(
				ScanDescription.COMMENT_PROP, oldValue, this.comment);
		updateListeners();
	}
	
	/**
	 * Returns the filename where the results should be saved.
	 * 
	 * @return the filename where the results should be saved
	 * @since 1.33
	 */
	public String getSaveFilename() {
		return this.saveFilename;
	}
	
	/**
	 * Sets the filename where the results should be saved.
	 *
	 * @param saveFilename the filename where the results should be saved.
	 * @throws IllegalArgumentException if <code>saveFilename</code> is 
	 * 			<code>null</code>
	 * @since 1.33
	 */
	public void setSaveFilename(final String saveFilename) {
		if (saveFilename == null) {
			throw new IllegalArgumentException(
					"The parameter 'saveFilename' must not be null!");
		} else if (this.saveFilename.equals(saveFilename)) {
			LOGGER.debug("set savefilename that was already set -> do nothing");
			return;
		}
		String oldValue = this.saveFilename;
		this.saveFilename = saveFilename;
		this.propertyChangeSupport.firePropertyChange(
				ScanDescription.SAVE_FILE_NAME_PROP, oldValue, saveFilename);
		updateListeners();
		
		new Thread("Macro Resolver") {
			@Override
			public void run() {
				setResolvedFilename(MacroResolver.getInstance().resolve(
						saveFilename));
			}}.start();
	}
	
	/**
	 * Returns the filename after macro resolution
	 * @return the resolvedFilename
	 * @since 1.33
	 */
	public String getResolvedFilename() {
		return this.resolvedFilename;
	}
	
	/**
	 * 
	 * @param resolvedFilename the resolvedFilename to set
	 * @since 1.33
	 */
	public void setResolvedFilename(String resolvedFilename) {
		String oldValue = this.resolvedFilename;
		this.resolvedFilename = resolvedFilename;
		this.propertyChangeSupport.firePropertyChange(
				ScanDescription.RESOLVED_FILENAME_PROP, 
					oldValue, resolvedFilename);
	}
	
	/**
	 * Returns the save plugin of the used file format.
	 * 
	 * Used for data binding (combo viewer in Scan View of Editor).
	 * Old "design" used an exposed plugin controller. no delegation.
	 * 
	 * @return the save plugin of the used file format
	 * @since 1.33
	 */
	public PlugIn getFileFormat() {
		return this.savePluginController.getPlugin();
	}
	
	/**
	 * Sets the file format (save plugin).
	 * 
	 * Used for data binding (combo viewer in Scan View of Editor).
	 * Old "design" used an exposed plugin controller. no delegation.
	 * 
	 * @param plugin the plugin to set
	 * @since 1.33
	 */
	public void setFileFormat(PlugIn plugin) {
		PlugIn oldValue = this.getSavePluginController().getPlugin();
		this.getSavePluginController().setPlugin(plugin);
		this.propertyChangeSupport.firePropertyChange(
				ScanDescription.FILE_FORMAT_PROP, oldValue, plugin);
	}
	
	/**
	 * Checks whether saving of the results has to be confirmed manually.
	 * 
	 * @return <code>true</code> if saving has to be confirmed, 
	 * 			<code>false</code> otherwise
	 * @since 1.33
	 */
	public boolean isConfirmSave() {
		return this.confirmSave;
	}
	
	/**
	 * Sets whether saving of results has to be confirmed manually.
	 * 
	 * @param confirmSave <code>true</code> if saving should be confirmed, 
	 * 						<code>false</code> otherwise
	 * @since 1.33
	 */
	public void setConfirmSave(final boolean confirmSave) {
		boolean oldValue = this.confirmSave;
		this.confirmSave = confirmSave;
		this.propertyChangeSupport.firePropertyChange(
				ScanDescription.CONFIRM_SAVE_PROP, oldValue, confirmSave);
		updateListeners();
	}
	
	/**
	 * Checks whether auto incremented file names are enabled.
	 * 
	 * @return <code>true</code> if auto increment is enabled, 
	 * 			<code>false</code> otherwise
	 * @since 1.33
	 */
	public boolean isAutoNumber() {
		return this.autoNumber;
	}
	
	/**
	 * Sets whether auto incremented file names should be used.
	 * 
	 * @param autoNumber <code>true</code> if auto incremented file names 
	 * 					should be used, <code>false</code> otherwise
	 * @since 1.33
	 */
	public void setAutoNumber(final boolean autoNumber) {
		boolean oldValue = this.autoNumber;
		this.autoNumber = autoNumber;
		this.propertyChangeSupport.firePropertyChange(
				ScanDescription.AUTO_INCREMENT_PROP, oldValue, autoNumber);
		updateListeners();
	}
	
	/**
	 * Checks whether the scan description should be saved in the results file.
	 * 
	 * @return <code>true</code> if the scan description should be saved in the 
	 * 			results, <code>false</code> otherwise
	 * @since 1.33
	 */
	public boolean isSaveScanDescription() {
		return this.saveScanDescription;
	}
	
	/**
	 * Sets whether the scan description should be saved.
	 * @param saveScanDescription <code>true</code> if the scan description 
	 * 	should be saved, <code>false</code> otherwise
	 * @since 1.33
	 */
	public void setSaveScanDescription(boolean saveScanDescription) {
		boolean oldValue = this.saveScanDescription;
		this.saveScanDescription = saveScanDescription;
		this.propertyChangeSupport.firePropertyChange(
				ScanDescription.SAVE_SCAN_DESCRIPTION_PROP, 
				oldValue, saveScanDescription);
		updateListeners();
	}
	
	/**
	 * Returns the {@link de.ptb.epics.eve.data.scandescription.PluginController}.
	 * 
	 * @return the {@link de.ptb.epics.eve.data.scandescription.PluginController}
	 * @since 1.33
	 */
	public PluginController getSavePluginController() {
		return this.savePluginController;
	}
	
	/**
	 * Returns a list holding all chains.
	 * 
	 * @return a list holding all chain.
	 */
	public List<Chain> getChains() {
		return new ArrayList<>(this.chains);
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
		if (!this.getMonitorOption().equals(monitorOption)) {
			MonitorOption oldOption = this.monitors.getType();
			this.monitors.setType(monitorOption);
			this.propertyChangeSupport.firePropertyChange(
					ScanDescription.MONITOR_OPTION_PROP, 
					oldOption, this.monitors.getType());
			this.propertyChangeSupport.firePropertyChange(
					ScanDescription.MONITOR_OPTIONS_LIST_PROP,
					null, this.monitors.getMonitors());
			updateListeners();
		}
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
			this.updateEvent(null);
		} catch (UnsupportedOperationException e) {
			LOGGER.error("Monitor could not be added: " + e.getMessage());
		}
	}
	
	/**
	 * Adds all given options to the list of monitors
	 * @param options the list of options to be added as monitors
	 * @since 1.27
	 */
	public void addMonitors(List<Option> options) {
		try {
			for (Option o : options) {
				if (!this.monitors.getMonitors().contains(o)) {
					this.monitors.add(o);
				}
			}
			this.propertyChangeSupport.firePropertyChange(
					ScanDescription.MONITOR_OPTIONS_LIST_PROP, null, monitors);
			this.updateEvent(null);
		} catch (UnsupportedOperationException e) {
			LOGGER.error("Monitors could not be added: " + e.getMessage());
		}
	}
	
	/**
	 * Removes an option from the list of monitors
	 * @param option the option to remove
	 * @since 1.12
	 */
	public void removeMonitor(Option option) {
		try {
			this.monitors.remove(option);
			this.propertyChangeSupport.firePropertyChange(
					ScanDescription.MONITOR_OPTIONS_LIST_PROP, null, monitors);
			this.updateEvent(null);
		} catch (UnsupportedOperationException e) {
			LOGGER.error("Monitor could not be removed: " + e.getMessage());
		}
	}
	
	/**
	 * Removes all given options from the list of monitors
	 * @param options the options to be removed
	 * @since 1.27
	 */
	public void removeMonitors(List<Option> options) {
		try {
			for (Option o : options) {
				this.monitors.remove(o);
			}
			this.propertyChangeSupport.firePropertyChange(
					ScanDescription.MONITOR_OPTIONS_LIST_PROP, null, monitors);
			this.updateEvent(null);
		} catch (UnsupportedOperationException e) {
			LOGGER.error("Monitors could not be removed: " + e.getMessage());
		}
	}

	/**
	 * Removes all monitors.
	 * @since 1.30
	 */
	public void removeAllMonitors() {
		this.monitors.removeAll();
		this.propertyChangeSupport.firePropertyChange(
				ScanDescription.MONITOR_OPTIONS_LIST_PROP, null, monitors);
		this.updateEvent(null);
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
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("removed orphaned monitor " + o.getName());
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
			if (isEventOfList(channel, chain.getRedoEvents()) ||
					isEventOfList(channel, chain.getBreakEvents()) ||
					isEventOfList(channel, chain.getStopEvents())) {
				return true;
			}
			for (ScanModule sm : chain.getScanModules()) {
				if (isEventOfList(channel, sm.getRedoEvents()) ||
						isEventOfList(channel, sm.getBreakEvents()) ||
						isEventOfList(channel, sm.getTriggerEvents())) {
					return true;
				}
				for (Channel smChannel : sm.getChannels()) {
					switch (smChannel.getChannelMode()) {
					case INTERVAL:
						if (channel.getDetectorChannel().equals(smChannel.getStoppedBy())) {
							return true;
						}
						break;
					case STANDARD:
						if (isEventOfList(channel, smChannel.getRedoEvents())) {
							return true;
						}
						break;
					default:
						break;
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
		Set<Option> monitorSet = new HashSet<>();
		
		for(Chain chain : this.getChains()) {
			for(ScanModule sm : chain.getScanModules()) {
				if (!sm.getType().equals(ScanModuleTypes.CLASSIC)) {
					continue;
				}
			
				for(Axis a : sm.getAxes()) {
					for(Option o : a.getMotorAxis().getOptions()) {
						if(o.isMonitor()) {
							monitorSet.add(o);
						}
					}
					for(Option o : a.getMotorAxis().getMotor().getOptions()) {
						if(o.isMonitor()) {
							monitorSet.add(o);
						}
					}
				}
				for(Channel ch : sm.getChannels()) {
					for(Option o : ch.getDetectorChannel().getOptions()) {
						if(o.isMonitor()) {
							monitorSet.add(o);
						}
					}
					for(Option o : ch.getDetectorChannel().getDetector().getOptions()) {
						if(o.isMonitor()) {
							monitorSet.add(o);
						}
					}
				}
				for(Prescan prescan : sm.getPrescans()) {
					if(prescan.isOption()) {
						monitorSet.add((Option)prescan.getAbstractDevice());
						Option o = (Option)prescan.getAbstractDevice();
						monitorSet.add(o);
					}
					if(prescan.isDevice()) {
						for(Option o : prescan.getAbstractDevice().getOptions()) {
							if(o.isMonitor()) {
								monitorSet.add(o);
							}
						}
					}
				}
				for(Postscan postscan : sm.getPostscans()) {
					if(postscan.isOption()) {
						monitorSet.add((Option)postscan.getAbstractDevice());
						Option o = (Option)postscan.getAbstractDevice();
						monitorSet.add(o);
					}
					if(postscan.isDevice()) {
						for(Option o : postscan.getAbstractDevice().getOptions()) {
							if(o.isMonitor()) {
								monitorSet.add(o);
							}
						}
					}
				}
			}
		}
		List<Option> monitorList = new ArrayList<>(monitorSet);
		Collections.sort(monitorList);
		return monitorList;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateEvent(final ModelUpdateEvent modelUpdateEvent) {
		if(LOGGER.isDebugEnabled() && modelUpdateEvent != null) {
				LOGGER.debug(modelUpdateEvent.getSender());
		}
		if (this.monitors.getType().equals(MonitorOption.USED_IN_SCAN)) {
			this.monitors.update();
			this.propertyChangeSupport.firePropertyChange(
					ScanDescription.MONITOR_OPTIONS_LIST_PROP, null, monitors);
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
		final List<IModelError> errorList = new ArrayList<>();
		if (this.saveFilename.isEmpty()) {
			errorList.add(new ScanDescriptionError(
					this, ScanDescriptionErrorTypes.FILENAME_EMPTY));
		}
		errorList.addAll(this.savePluginController.getModelErrors());
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
			new CopyOnWriteArrayList<>(this.modelUpdateListener);
		
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
}