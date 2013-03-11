package de.ptb.epics.eve.data.scandescription;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.log4j.Logger;

import de.ptb.epics.eve.data.scandescription.updatenotification.ControlEventManager;
import de.ptb.epics.eve.data.scandescription.updatenotification.ControlEventMessage;
import de.ptb.epics.eve.data.scandescription.updatenotification.ControlEventMessageEnum;
import de.ptb.epics.eve.data.scandescription.updatenotification.ControlEventTypes;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateProvider;
import de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent;
import de.ptb.epics.eve.data.scandescription.errors.ChainError;
import de.ptb.epics.eve.data.scandescription.errors.ChainErrorTypes;
import de.ptb.epics.eve.data.scandescription.errors.IModelError;
import de.ptb.epics.eve.data.scandescription.errors.IModelErrorProvider;

/**
 * This class describes a chain in a scan description.
 * 
 * @author Stephan Rehfeld <stephan.rehfeld( -at -) ptb.de>
 * @author Marcus Michalsky
 */
public class Chain implements IModelUpdateProvider, IModelUpdateListener, IModelErrorProvider {
	
	private static Logger logger = Logger.getLogger(Chain.class.getName());
	
	// unique id of the chain
	private int id;
	
	// filename where the results should be saved
	private String saveFilename;
	
	// indicates if the save should be manually confirmed by the user
	private boolean confirmSave;
	
	// indicates if the datafile name should be extended by an autoincrement #
	private boolean autoNumber;
	
	// The PluginController for the Save Plugin
	private final PluginController savePlugInController;
	
	// holds the configuration for the redo events
	private List<ControlEvent> redoEvents;
	
	// holds the configuration for the break events
	private List<ControlEvent> breakEvents;
	
	// holds the configuration for the pause events
	private List<PauseEvent> pauseEvents;
	
	// holds the configuration for the break events
	private List<ControlEvent> stopEvents;
	
	// TODO Caution, we have a list of ControlEvents called startEvents and 
	// one StartEvent called startEvent, tidy up this
	// For now just the first ControlEvent from startEvents is used as the event
	// part for startEvent

	// the configuration for the break events
	private List<ControlEvent> startEvents;
	
	// the event, that will start the chain
	private StartEvent startEvent;
	
	// holds all the scan modules
	private List<ScanModule> scanModules;
	
	// the scan description, that is the parent of this chain
	private ScanDescription scanDescription;
	
	// A map for id <-> scan module
	private Map<Integer, ScanModule> scanModuleMap;
	
	// holds all objects that need to get an update message
	private List<IModelUpdateListener> updateListener;
	
	// control event manager for break events
	private ControlEventManager breakControlEventManager;
	
	// control event manager for start events
	private ControlEventManager startControlEventManager;
	
	// control event manager for stop events
	private ControlEventManager stopControlEventManager;
	
	// control event manager for redo events
	private ControlEventManager redoControlEventManager;
	
	// control event manager for pause events
	private ControlEventManager pauseControlEventManager;
	
	private Integer positionCount;
	
	// chain comment
	private String comment;
	
	// indicates whether the scan description should be saved in the result file
	private boolean saveScanDescription;
	
	private PropertyChangeSupport propertyChangeSupport;
	
	/** */
	public static final String SCANMODULE_ADDED_PROP = 
			"Chain.SCANMODULE_ADDED_PROP";
	/** */
	public static final String SCANMODULE_REMOVED_PROP = 
			"Chain.SCANMODULE_REMOVED_PROP";
	
	public static final String CHAIN_POSITION_COUNT_PROP = "positionCount";
	
	/**
	 * Constructs a <code>ScanDescription</code> with the given id.
	 * 
	 * @param id the id of the chain
	 * @throws IllegalArgumentException if <code>id</code> < 1
	 */
	public Chain(final int id) {
		super();
		if(id < 1) {
			throw new IllegalArgumentException(
					"The parameter 'id' must be at least 1!");
		}
		this.id = id;
		this.scanModules = new ArrayList<ScanModule>();
		this.scanModuleMap = new HashMap<Integer, ScanModule>();
		this.redoEvents = new ArrayList<ControlEvent>();
		this.breakEvents = new ArrayList<ControlEvent>();
		this.startEvents = new ArrayList<ControlEvent>();
		this.stopEvents = new ArrayList<ControlEvent>();
		this.pauseEvents = new ArrayList<PauseEvent>();
		this.saveFilename = "";
		this.updateListener = new ArrayList<IModelUpdateListener>();
		
		this.savePlugInController = new PluginController();
		this.savePlugInController.addModelUpdateListener(this);
		// TODO make start event a regular event
		this.startEvent = null;
		this.breakControlEventManager = new ControlEventManager(
				this, this.breakEvents, ControlEventTypes.CONTROL_EVENT);
		this.startControlEventManager = new ControlEventManager(
				this, this.startEvents, ControlEventTypes.CONTROL_EVENT);
		this.stopControlEventManager = new ControlEventManager(
				this, this.stopEvents, ControlEventTypes.CONTROL_EVENT);
		this.redoControlEventManager = new ControlEventManager(
				this, this.redoEvents, ControlEventTypes.CONTROL_EVENT);
		this.pauseControlEventManager = new ControlEventManager(
				this, this.pauseEvents, ControlEventTypes.PAUSE_EVENT);
		
		this.breakControlEventManager.addModelUpdateListener(this);
		this.startControlEventManager.addModelUpdateListener(this);
		this.stopControlEventManager.addModelUpdateListener(this);
		this.redoControlEventManager.addModelUpdateListener(this);
		this.pauseControlEventManager.addModelUpdateListener(this);
		
		this.positionCount = null;
		
		this.comment = "";
		this.saveScanDescription = false;
		this.autoNumber = true;
		
		this.propertyChangeSupport = new PropertyChangeSupport(this);
	}
	
	/**
	 * Returns the id.
	 * 
	 * @return the id
	 */
	public int getId() {
		return this.id;
	}
	
	/**
	 * Sets the id.
	 * 
	 * @param id the id that should be set
	 * @throws IllegalArgumentException if <code>id</code> < 1
	 */
	public void setId(final int id) {
		if(id < 1) {
			throw new IllegalArgumentException(
					"The parameter 'id' must be at least 1!");
		}
		this.id = id;
		updateListeners();
	}
	
	/**
	 * Returns the {@link de.ptb.epics.eve.data.scandescription.ScanDescription} 
	 * the chain belongs to.
	 * 
	 * @return the {@link de.ptb.epics.eve.data.scandescription.ScanDescription} 
	 * 			belongs to
	 */
	public ScanDescription getScanDescription() {
		return this.scanDescription;
	}
	
	/**
	 * Sets the {@link de.ptb.epics.eve.data.scandescription.ScanDescription} 
	 * the chain should belong to.
	 * 
	 * @param scanDescription the 
	 * 		{@link de.ptb.epics.eve.data.scandescription.ScanDescription} the 
	 * 		chain should belong to
	 */
	protected void setScanDescription(final ScanDescription scanDescription) {
		this.scanDescription = scanDescription;
		updateListeners();
	}
	
	/**
	 * 
	 * @return
	 */
	public int getAvailableScanModuleId() {
		int i = 1;
		while(this.getScanModuleById(i) != null) {
			i++;
		}
		return i;
	}
	
	/**
	 * Returns the number of motor positions. Note that this value is updated 
	 * only by invoking {@link #calculatePositionCount()} due to performance 
	 * reasons.
	 * 
	 * @return the number of motor positions (as last calculated) or 
	 * 			<code>null</code> if calculation was not possible
	 * @author Marcus Michalsky
	 * @since 1.10
	 */
	public Integer getPositionCount() {
		return this.positionCount;
	}
	
	/**
	 * (Re-)calculates the number of motor positions which can be retrieved by 
	 * {@link #getPositionCount()}.
	 * 
	 * @author Marcus Michalsky
	 * @since 1.10
	 */
	public void calculatePositionCount() {
		if (this.getStartEvent().getConnector() == null) {
			this.positionCount = null;
			return;
		}
		ScanModule first = 
				this.getStartEvent().getConnector().getChildScanModule();
		this.propertyChangeSupport.firePropertyChange(
				Chain.CHAIN_POSITION_COUNT_PROP, this.positionCount,
				this.positionCount = this.calculatePositionCount(first));
	}
	
	/*
	 * Double recursive calculation of motor positions in the chain tree.
	 * 
	 * @author Marcus Michalsky
	 * @since 1.10
	 */
	private Integer calculatePositionCount(ScanModule subChainHead) {
		try {
			if (subChainHead.getAppended() == null
					&& subChainHead.getNested() == null) {
				return subChainHead.getPositionCount();
			} else if (subChainHead.getAppended() == null) {
				return subChainHead.getPositionCount()
						* this.calculatePositionCount(subChainHead
								.getNested().getChildScanModule());
			} else if (subChainHead.getNested() == null) {
				return subChainHead.getPositionCount()
						+ this.calculatePositionCount(subChainHead.getAppended()
								.getChildScanModule());
			} else {
				return subChainHead.getPositionCount()
						* this.calculatePositionCount(subChainHead
								.getNested().getChildScanModule())
						+ this.calculatePositionCount(subChainHead.getAppended()
								.getChildScanModule());
			}
		} catch (NullPointerException e) {
			return null;
		}
	}
	
	/**
	 * Adds a {@link de.ptb.epics.eve.data.scandescription.ScanModule}.
	 * 
	 * @param scanModule the 
	 * 		{@link de.ptb.epics.eve.data.scandescription.ScanModule} that 
	 * 		should be added
	 * @throws IllegalArgumentException if <code>scanModule</code> is 
	 * 		<code>null</code>
	 */
	public void add(final ScanModule scanModule) {
		if(scanModule == null) {
			throw new IllegalArgumentException(
					"The parameter 'scanModule' must not be null!");
		}
		this.scanModuleMap.put(scanModule.getId(), scanModule);
		this.scanModules.add(scanModule);
		scanModule.setChain(this);
		scanModule.addModelUpdateListener(this);
		this.propertyChangeSupport.firePropertyChange(
				Chain.SCANMODULE_ADDED_PROP, null, scanModule);
		updateListeners();
	}
	
	/**
	 * Removes the given 
	 * {@link de.ptb.epics.eve.data.scandescription.ScanModule}.
	 * 
	 * @param scanModule The 
	 * 		{@link de.ptb.epics.eve.data.scandescription.ScanModule} that 
	 * 		should be removed
	 */
	public void remove(final ScanModule scanModule) {
		if(scanModule == null) {
			throw new IllegalArgumentException(
					"The parameter 'scanModul' must not be null!");
		}
		this.scanModules.remove(scanModule);
		this.scanModuleMap.remove(scanModule.getId());
		scanModule.removeModelUpdateListener(this);
		scanModule.setChain(null);
		this.propertyChangeSupport.firePropertyChange(
				Chain.SCANMODULE_REMOVED_PROP, scanModule, null);
		updateListeners();
	}
	
	/**
	 * Returns the {@link de.ptb.epics.eve.data.scandescription.ScanModule} with 
	 * the given id.
	 * 
	 * @param id the id of the The id of the 
	 * 		{@link de.ptb.epics.eve.data.scandescription.ScanModule} that should 
	 * 		be returned.
	 * @return the {@link de.ptb.epics.eve.data.scandescription.ScanModule} with 
	 * 			the given id <b>or</b> <code>null</code> if none
	 */
	public ScanModule getScanModuleById(final int id) {
		return this.scanModuleMap.get(id);
	}
	
	/**
	 * Returns a new {@link java.util.List} of all scan modules.
	 * 
	 * @return a new {@link java.util.List} of all scan modules
	 */
	public List<ScanModule> getScanModules() {
		return new ArrayList<ScanModule>(this.scanModules);
	}
	
	/**
	 * Returns the filename where the results should be saved.
	 * 
	 * @return the filename where the results should be saved
	 */
	public String getSaveFilename() {
		return saveFilename;
	}
	
	/**
	 * Sets the filename where the results should be saved.
	 *
	 * @param saveFilename the filename where the results should be saved.
	 * @throws IllegalArgumentException if <code>saveFilename</code> is 
	 * 			<code>null</code>
	 */
	public void setSaveFilename(final String saveFilename) {
		if(saveFilename == null) {
			throw new IllegalArgumentException(
					"The parameter 'saveFilename' must not be null!");
		}
		this.saveFilename = saveFilename;
		updateListeners();
	}
	
	/**
	 * Checks whether the scan description should be saved in the results file.
	 * 
	 * @return <code>true</code> if the scan description should be saved in the 
	 * 			results, <code>false</code> otherwise
	 */
	public boolean isSaveScanDescription() {
		return this.saveScanDescription;
	}
	
	/**
	 * Sets whether the scan description should be saved in the results file.
	 * 
	 * @param saveScanDescription <code>true</code> if the scan description 
	 * 		should be saved in the results file, <code>false</code> otherwise
	 */
	public void setSaveScanDescription(final boolean saveScanDescription) {
		this.saveScanDescription = saveScanDescription;
		updateListeners();
	}
	
	/**
	 * Checks whether saving of the results has to be confirmed manually.
	 * 
	 * @return <code>true</code> if saving has to be confirmed, 
	 * 			<code>false</code> otherwise
	 */
	public boolean isConfirmSave() {
		return this.confirmSave;
	}
	
	/**
	 * Sets whether saving of results has to be confirmed manually.
	 * 
	 * @param confirmSave <code>true</code> if saving should be confirmed, 
	 * 						<code>false</code> otherwise
	 */
	public void setConfirmSave(final boolean confirmSave) {
		this.confirmSave = confirmSave;
		updateListeners();
	}
	
	/**
	 * Checks whether auto incremented file names are enabled.
	 * 
	 * @return <code>true</code> if auto increment is enabled, 
	 * 			<code>false</code> otherwise
	 */
	public boolean isAutoNumber() {
		return this.autoNumber;
	}
	
	/**
	 * Sets whether auto incremented file names should be used.
	 * 
	 * @param autoNumber <code>true</code> if auto incremented file names 
	 * 					should be used, <code>false</code> otherwise
	 */
	public void setAutoNumber(final boolean autoNumber) {
		this.autoNumber = autoNumber;
		updateListeners();
	}
	
	/**
	 * Returns the comment.
	 * 
	 * @return the comment
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
	 */
	public void setComment(final String comment) {
		if(comment == null) {
			throw new IllegalArgumentException(
					"The parameter 'comment' must not be null!");
		}
		this.comment = comment;
		updateListeners();
	}
	
	/**
	 * Returns the {@link de.ptb.epics.eve.data.scandescription.PluginController}.
	 * 
	 * @return the {@link de.ptb.epics.eve.data.scandescription.PluginController}
	 */
	public PluginController getSavePluginController() {
		return this.savePlugInController;
	}
	
	/**
	 * Returns the {@link de.ptb.epics.eve.data.scandescription.StartEvent}.
	 * 
	 * @return the {@link de.ptb.epics.eve.data.scandescription.StartEvent}
	 */
	public StartEvent getStartEvent() {
		return this.startEvent;
	}
	
	/**
	 *	Sets the {@link de.ptb.epics.eve.data.scandescription.StartEvent}.
	 * 
	 * @param startEvent the 
	 * 		{@link de.ptb.epics.eve.data.scandescription.StartEvent} that 
	 * 		should be set
	 */
	public void setStartEvent(final StartEvent startEvent) {
		this.startEvent = startEvent;
		updateListeners();
	}
	
	/**
	 * Adds the given {@link de.ptb.epics.eve.data.scandescription.PauseEvent}.
	 * 
	 * @param pauseEvent the 
	 * 		{@link de.ptb.epics.eve.data.scandescription.PauseEvent} that 
	 * 		should be added
	 * @return <code>true</code> if successful, <code>false</code> otherwise
	 */
	public boolean addPauseEvent(final PauseEvent pauseEvent) {
		if(this.pauseEvents.add(pauseEvent)) {
			updateListeners();
			pauseEvent.addModelUpdateListener(this.pauseControlEventManager);
			this.pauseControlEventManager.updateEvent(new ModelUpdateEvent(
					this, new ControlEventMessage(pauseEvent, 
							ControlEventMessageEnum.ADDED)));
			return true;
		} 
		return false;
	}
	
	/**
	 * Removes the given 
	 * {@link de.ptb.epics.eve.data.scandescription.PauseEvent}.
	 * 
	 * @param pauseEvent the 
	 * 		{@link de.ptb.epics.eve.data.scandescription.PauseEvent} that 
	 * 		should be removed
	 * @return <code>true</code> if successful, <code>false</code> otherwise
	 */
	public boolean removePauseEvent(final PauseEvent pauseEvent) {
		if(this.pauseEvents.remove(pauseEvent)) {
			updateListeners();
			pauseEvent.removeModelUpdateListener(this.pauseControlEventManager);
			this.pauseControlEventManager.updateEvent(new ModelUpdateEvent(this, 
					new ControlEventMessage(pauseEvent, 
							ControlEventMessageEnum.REMOVED)));
			return true;
		} 
		return false;
	}
	
	/**
	 * Adds the given {@link de.ptb.epics.eve.data.scandescription.ControlEvent} 
	 * as a break event.
	 * 
	 * @param breakEvent the 
	 * 		{@link de.ptb.epics.eve.data.scandescription.ControlEvent} that 
	 * 		should be added as a break event
	 * @return <code>true</code> if successful, <code>false</code> otherwise
	 */
	public boolean addBreakEvent(final ControlEvent breakEvent) {
		if(this.breakEvents.add(breakEvent)) {
			updateListeners();
			breakEvent.addModelUpdateListener(this.breakControlEventManager);
			this.breakControlEventManager.updateEvent(new ModelUpdateEvent(this, 
					new ControlEventMessage(breakEvent, 
							ControlEventMessageEnum.ADDED)));
			return true;
		} 
		return false;
	}
	
	/**
	 * Removes the given break event.
	 * 
	 * @param breakEvent the break event that should be removed
	 * @return <code>true</code> if successfull, </code> otherwise
	 */
	public boolean removeBreakEvent(final ControlEvent breakEvent) {
		if(this.breakEvents.remove(breakEvent)) {
			updateListeners();
			breakEvent.removeModelUpdateListener(this.breakControlEventManager);
			this.breakControlEventManager.updateEvent(new ModelUpdateEvent(this, 
					new ControlEventMessage(breakEvent, 
							ControlEventMessageEnum.REMOVED)));
			return true;
		} 
		return false;
	}
	
	/**
	 * Adds the given {@link de.ptb.epics.eve.data.scandescription.ControlEvent} 
	 * as a start event.
	 * 
	 * @param startEvent the 
	 * 		{@link de.ptb.epics.eve.data.scandescription.ControlEvent} that 
	 * 		should be added as a start event
	 * @return <code>true</code> if successful, <code>false</code> otherwise
	 */
	public boolean addStartEvent(final ControlEvent startEvent) {
		if(this.startEvents.add(startEvent)) {
			updateListeners();
			startEvent.addModelUpdateListener(this.startControlEventManager);
			this.startControlEventManager.updateEvent(new ModelUpdateEvent(this, 
					new ControlEventMessage(startEvent, 
							ControlEventMessageEnum.ADDED)));
			return true;
		} 
		return false;
	}
	
	/**
	 * Removes the given start event.
	 * 
	 * @param startEvent the start event that should be removed
	 * @return <code>true</code> if successful, <code>false</code> otherwise
	 */
	public boolean removeStartEvent(final ControlEvent startEvent) {
		if(this.startEvents.remove(startEvent)) {
			updateListeners();
			startEvent.removeModelUpdateListener(this.startControlEventManager);
			this.startControlEventManager.updateEvent(new ModelUpdateEvent(this, 
					new ControlEventMessage(startEvent, 
							ControlEventMessageEnum.REMOVED)));
			return true;
		} 
		return false;
	}
	
	/**
	 * Adds the given {@link de.ptb.epics.eve.data.scandescription.ControlEvent} 
	 * as a stop event.
	 * 
	 * @param stopEvent the 
	 * 		{@link de.ptb.epics.eve.data.scandescription.ControlEvent} that 
	 * 		should be added as a stop event
	 * @return <code>true</code> if successful, <code>false</code> otherwise
	 */
	public boolean addStopEvent(final ControlEvent stopEvent) {
		if(this.stopEvents.add(stopEvent)) {
			updateListeners();
			stopEvent.addModelUpdateListener(this.stopControlEventManager);
			this.stopControlEventManager.updateEvent(new ModelUpdateEvent(this, 
					new ControlEventMessage(stopEvent, 
							ControlEventMessageEnum.ADDED)));
			return true;
		} 
		return false;
	}
	
	/**
	 * Removes the given stop event.
	 * 
	 * @param stopEvent the stop event that should be removed
	 * @return <code>true</code> if successful, <code>false</code> otherwise
	 */
	public boolean removeStopEvent(final ControlEvent stopEvent) {
		if(this.stopEvents.remove( stopEvent)) {
			updateListeners();
			stopEvent.removeModelUpdateListener(this.stopControlEventManager);
			this.stopControlEventManager.updateEvent(new ModelUpdateEvent(this, 
					new ControlEventMessage(stopEvent, 
							ControlEventMessageEnum.REMOVED)));
			return true;
		} 
		return false;
	}
	
	/**
	 * Adds the given {@link de.ptb.epics.eve.data.scandescription.ControlEvent} 
	 * as a redo event.
	 * 
	 * @param redoEvent the 
	 * 		{@link de.ptb.epics.eve.data.scandescription.ControlEvent} that 
	 * 		should be added as a redo event
	 * @return <code>true</code> if successful, <code>false</code> otherwise
	 */
	public boolean addRedoEvent(final ControlEvent redoEvent) {
		if( this.redoEvents.add(redoEvent)) {
			updateListeners();
			this.redoControlEventManager.updateEvent(new ModelUpdateEvent(this, 
					new ControlEventMessage(redoEvent, 
							ControlEventMessageEnum.ADDED)));
			redoEvent.addModelUpdateListener(this.redoControlEventManager);
			return true;
		} 
		return false;
	}
	
	/**
	 * Removes the given redo event.
	 * 
	 * @param redoEvent the redo event that should be removed
	 * @return <code>true</code> if successful, <code>false</code> otherwise
	 */
	public boolean removeRedoEvent(final ControlEvent redoEvent) {
		if( this.redoEvents.remove(redoEvent)) {
			updateListeners();
			this.redoControlEventManager.updateEvent(new ModelUpdateEvent(this, 
					new ControlEventMessage(redoEvent, 
							ControlEventMessageEnum.REMOVED)));
			redoEvent.removeModelUpdateListener(this.redoControlEventManager);
			return true;
		} 
		return false;
	}
	
	/**
	 * Checks whether the given 
	 * {@link de.ptb.epics.eve.data.scandescription.PauseEvent} exists.
	 * 
	 * @param pauseEvent the 
	 * 		{@link de.ptb.epics.eve.data.scandescription.PauseEvent} that 
	 * 		should be checked
	 * @return <code>true</code> if the given 
	 * 			{@link de.ptb.epics.eve.data.scandescription.PauseEvent} exists,
	 * 			<code>false</code> otherwise
	 */
	public boolean isPauseEventOfTheChain(final PauseEvent pauseEvent) {
		return this.pauseEvents.contains(pauseEvent);
	}
	
	/**
	 * Checks whether the given 
	 * {@link de.ptb.epics.eve.data.scandescription.ControlEvent} exists as 
	 * as a redo event.
	 * 
	 * @param redoEvent the 
	 * 		{@link de.ptb.epics.eve.data.scandescription.ControlEvent} that
	 * 		should be checked
	 * @return <code>true</code> if the given 
	 * 		{@link de.ptb.epics.eve.data.scandescription.ControlEvent} exists 
	 * 		as a redo event, <code>false</code> otherwise
	 */
	public boolean isRedoEventOfTheChain(final ControlEvent redoEvent) {
		return this.redoEvents.contains(redoEvent);
	}
	
	/**
	 * Checks whether the given 
	 * {@link de.ptb.epics.eve.data.scandescription.ControlEvent} exists as 
	 * a break event.
	 * 
	 * @param breakEvent the 
	 * 		{@link de.ptb.epics.eve.data.scandescription.ControlEvent} that 
	 * 		should be checked
	 * @return <code>true</code> if the given 
	 * 		{@link de.ptb.epics.eve.data.scandescription.ControlEvent} exists 
	 * 		as a break event, <code>false</code> otherwise
	 */
	public boolean isBreakEventOfTheChain(final ControlEvent breakEvent) {
		return this.breakEvents.contains(breakEvent);
	}
	
	/**
	 * Checks whether the given 
	 * {@link de.ptb.epics.eve.data.scandescription.ControlEvent} exists.
	 * 
	 * @param controlEvent the 
	 * 		{@link de.ptb.epics.eve.data.scandescription.ControlEvent} that 
	 * 		should be checked
	 * @return <code>true</code> if the given 
	 * 		{@link de.ptb.epics.eve.data.scandescription.ControlEvent} exists, 
	 * 		<code>false</code> otherwise
	 */
	public boolean isAEventOfTheChain(final ControlEvent controlEvent) {
		return (controlEvent instanceof PauseEvent && 
				this.isPauseEventOfTheChain((PauseEvent)controlEvent)) || 
				this.isBreakEventOfTheChain(controlEvent) || 
				this.isRedoEventOfTheChain(controlEvent);
	}
	
	/**
	 * This method returns an iterator over all pause events.
	 * 
	 * @return An iterator over all pause events. Never returns 'null'.
	 * @deprecated use {@link #getPauseControlEventManager()} and 
	 * 	{@link de.ptb.epics.eve.data.scandescription.updatenotification.ControlEventManager#getControlEventsList()} 
	 * in conjunction with the for-each-loop
	 * @see <a href="http://docs.oracle.com/javase/1.5.0/docs/guide/language/foreach.html">oracle documentation</a>
	 */
	public Iterator<PauseEvent> getPauseEventsIterator() {
		return this.pauseEvents.iterator();
	}
	
	/**
	 * This method returns an iterator over all start events.
	 * 
	 * @return an iterator over all start events
	 * @deprecated use {@link #getStartControlEventManager()} and 
	 * 	{@link de.ptb.epics.eve.data.scandescription.updatenotification.ControlEventManager#getControlEventsList()} 
	 * in conjunction with the for-each-loop
	 * @see <a href="http://docs.oracle.com/javase/1.5.0/docs/guide/language/foreach.html">oracle documentation</a>
	 */
	public Iterator<ControlEvent> getStartEventsIterator() {
		return this.startEvents.iterator();
	}
	
	/**
	 * This method returns an iterator over all stop events.
	 * 
	 * @return an iterator over all stop events
	 * @deprecated use {@link #getStopControlEventManager()} and 
	 * 	{@link de.ptb.epics.eve.data.scandescription.updatenotification.ControlEventManager#getControlEventsList()} 
	 * in conjunction with the for-each-loop
	 * @see <a href="http://docs.oracle.com/javase/1.5.0/docs/guide/language/foreach.html">oracle documentation</a>
	 */
	public Iterator<ControlEvent> getStopEventsIterator() {
		return this.stopEvents.iterator();
	}
	
	/**
	 * This method returns an iterator over all break events.
	 * 
	 * @return an iterator over all break events
	 * @deprecated use {@link #getBreakControlEventManager()} and 
	 * 	{@link de.ptb.epics.eve.data.scandescription.updatenotification.ControlEventManager#getControlEventsList()} 
	 * in conjunction with the for-each-loop
	 * @see <a href="http://docs.oracle.com/javase/1.5.0/docs/guide/language/foreach.html">oracle documentation</a>
	 */
	public Iterator<ControlEvent> getBreakEventsIterator() {
		return this.breakEvents.iterator();
	}
	
	/**
	 * This method returns an iterator over all redo events.
	 * 
	 * @return an iterator over all redo events
	 * @deprecated use {@link #getRedoControlEventManager()} and 
	 * 	{@link de.ptb.epics.eve.data.scandescription.updatenotification.ControlEventManager#getControlEventsList()} 
	 * in conjunction with the for-each-loop
	 * @see <a href="http://docs.oracle.com/javase/1.5.0/docs/guide/language/foreach.html">oracle documentation</a>
	 */
	public Iterator<ControlEvent> getRedoEventsIterator() {
		return this.redoEvents.iterator();
	}
	
	/**
	 * Returns the {@link de.ptb.epics.eve.data.scandescription.updatenotification.ControlEventManager} 
	 * for break events.
	 * 
	 * @return the 
	 * 	{@link de.ptb.epics.eve.data.scandescription.updatenotification.ControlEventManager}
	 * 		for break events
	 */
	public ControlEventManager getBreakControlEventManager() {
		return this.breakControlEventManager;
	}
	
	/**
	 * Returns the {@link de.ptb.epics.eve.data.scandescription.updatenotification.ControlEventManager} 
	 * for start events.
	 * 
	 * @return the 
	 * {@link de.ptb.epics.eve.data.scandescription.updatenotification.ControlEventManager}
	 * 		for start events
	 */
	public ControlEventManager getStartControlEventManager() {
		return this.startControlEventManager;
	}
	
	/**
	 * Returns the {@link de.ptb.epics.eve.data.scandescription.updatenotification.ControlEventManager} 
	 * for stop events.
	 * 
	 * @return the 
	 * {@link de.ptb.epics.eve.data.scandescription.updatenotification.ControlEventManager}
	 * 		for stop events
	 */
	public ControlEventManager getStopControlEventManager() {
		return this.stopControlEventManager;
	}
	
	/**
	 * Returns the {@link de.ptb.epics.eve.data.scandescription.updatenotification.ControlEventManager} 
	 * for pause events.
	 * 
	 * @return the 
	 * {@link de.ptb.epics.eve.data.scandescription.updatenotification.ControlEventManager}
	 * 		for pause events
	 */
	public ControlEventManager getPauseControlEventManager() {
		return this.pauseControlEventManager;
	}
	
	/**
	 * Returns the {@link de.ptb.epics.eve.data.scandescription.updatenotification.ControlEventManager} 
	 * for redo events.
	 * 
	 * @return the 
	 * {@link de.ptb.epics.eve.data.scandescription.updatenotification.ControlEventManager}
	 * 		for redo events
	 */
	public ControlEventManager getRedoControlEventManager() {
		return this.redoControlEventManager;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<IModelError> getModelErrors() {
		final List<IModelError> errorList = new ArrayList<IModelError>();
		if(this.saveFilename.isEmpty()) {
			errorList.add(new ChainError(this, ChainErrorTypes.FILENAME_EMPTY));
		}
		errorList.addAll(this.savePlugInController.getModelErrors());
		errorList.addAll(this.pauseControlEventManager.getModelErrors());
		errorList.addAll(this.breakControlEventManager.getModelErrors());
		errorList.addAll(this.redoControlEventManager.getModelErrors());
		errorList.addAll(this.stopControlEventManager.getModelErrors());
		errorList.addAll(this.startControlEventManager.getModelErrors());
		
		for(ScanModule sm : this.scanModules) {
			errorList.addAll(sm.getModelErrors());
		}
		return errorList;
	}
	
	/*
	 * 
	 */
	private void updateListeners() {
		final CopyOnWriteArrayList<IModelUpdateListener> list = 
			new CopyOnWriteArrayList<IModelUpdateListener>(this.updateListener);
		
		for(IModelUpdateListener imul : list) {
			imul.updateEvent(new ModelUpdateEvent(this, null));
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateEvent(final ModelUpdateEvent modelUpdateEvent) {
		logger.debug("update event");
		updateListeners();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean addModelUpdateListener(
			final IModelUpdateListener modelUpdateListener) {
		return this.updateListener.add(modelUpdateListener);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean removeModelUpdateListener(
			final IModelUpdateListener modelUpdateListener) {
		return this.updateListener.remove(modelUpdateListener);
	}
	
	/**
	 * 
	 * @param property
	 * @param listener
	 */
	public void addPropertyChangeListener(String property,
			PropertyChangeListener listener) {
		this.propertyChangeSupport
				.addPropertyChangeListener(property, listener);
	}

	/**
	 * 
	 * @param property
	 * @param listener
	 */
	public void removePropertyChangeListener(String property,
			PropertyChangeListener listener) {
		this.propertyChangeSupport.removePropertyChangeListener(property,
				listener);
	}
}