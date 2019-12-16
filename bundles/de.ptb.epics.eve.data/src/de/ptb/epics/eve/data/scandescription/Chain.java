package de.ptb.epics.eve.data.scandescription;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import org.apache.log4j.Logger;

import de.ptb.epics.eve.data.measuringstation.event.DetectorEvent;
import de.ptb.epics.eve.data.measuringstation.event.ScanEvent;
import de.ptb.epics.eve.data.measuringstation.event.ScheduleEvent;
import de.ptb.epics.eve.data.scandescription.updatenotification.ControlEventManager;
import de.ptb.epics.eve.data.scandescription.updatenotification.ControlEventTypes;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateProvider;
import de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent;
import de.ptb.epics.eve.util.graph.tree.Node;
import de.ptb.epics.eve.data.scandescription.errors.IModelError;
import de.ptb.epics.eve.data.scandescription.errors.IModelErrorProvider;

/**
 * This class describes a chain in a scan description.
 * 
 * @author Stephan Rehfeld <stephan.rehfeld( -at -) ptb.de>
 * @author Marcus Michalsky
 */
public class Chain implements IModelUpdateProvider, IModelUpdateListener, IModelErrorProvider, PropertyChangeListener {
	public static final String SCANMODULE_ADDED_PROP = 
			"Chain.SCANMODULE_ADDED_PROP";
	public static final String SCANMODULE_REMOVED_PROP = 
			"Chain.SCANMODULE_REMOVED_PROP";
	public static final String POSITION_COUNT_PROP = "positionCount";

	/**
	 * @since 1.19
	 */
	public static final String BREAK_EVENT_PROP = "breakEvent";
	public static final String PAUSE_EVENT_PROP = "pauseEvent";
	public static final String REDO_EVENT_PROP =  "redoEvent";
	public static final String STOP_EVENT_PROP =  "stopEvent";

	private static Logger logger = Logger.getLogger(Chain.class.getName());

	private int id;

	// the event, that will start the chain
	private StartEvent startEvent;

	// holds all the scan modules
	private ObservableList<ScanModule> scanModules;

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

	private boolean reserveIds;
	private List<Integer> reservedIds;

	private PropertyChangeSupport propertyChangeSupport;

	/**
	 * Constructs a <code>ScanDescription</code> with the given id.
	 * 
	 * @param id the id of the chain
	 * @throws IllegalArgumentException if <code>id</code> < 1
	 */
	public Chain(final int id) {
		super();
		if (id < 0) {
			throw new IllegalArgumentException(
					"The parameter 'id' must be at least 1!");
		}
		this.id = id;
		this.scanModules = FXCollections
				.observableList(new ArrayList<ScanModule>());
		this.scanModuleMap = new HashMap<Integer, ScanModule>();
		this.updateListener = new ArrayList<IModelUpdateListener>();
		
		this.startEvent = null;
		this.breakControlEventManager = new ControlEventManager(
				ControlEventTypes.CONTROL_EVENT);
		this.startControlEventManager = new ControlEventManager(
				ControlEventTypes.CONTROL_EVENT);
		this.stopControlEventManager = new ControlEventManager(
				ControlEventTypes.CONTROL_EVENT);
		this.redoControlEventManager = new ControlEventManager(
				ControlEventTypes.CONTROL_EVENT);
		this.pauseControlEventManager = new ControlEventManager(
				ControlEventTypes.PAUSE_EVENT);

		this.breakControlEventManager.addModelUpdateListener(this);
		this.startControlEventManager.addModelUpdateListener(this);
		this.stopControlEventManager.addModelUpdateListener(this);
		this.redoControlEventManager.addModelUpdateListener(this);
		this.pauseControlEventManager.addModelUpdateListener(this);

		this.positionCount = null;

		this.reserveIds = false;
		this.reservedIds = new LinkedList<Integer>();

		this.propertyChangeSupport = new PropertyChangeSupport(this);

		if (logger.isDebugEnabled()) {
			this.addScanModuleChangeListener(new ListChangeListener<ScanModule>() {
				@Override
				public void onChanged(final javafx.collections.ListChangeListener.
						Change<? extends ScanModule> c) {
					while (c.next()) {
						if (c.wasPermutated()) {
							logger.debug("scan modules permutated");
						} else if (c.wasUpdated()) {
							logger.debug("scan module updated");
						} else {
							for (ScanModule sm : c.getRemoved()) {
								logger.debug("scan module '" + sm.getName() 
										+ "' was removed.");
							}
							for (ScanModule sm : c.getAddedSubList()) {
								logger.debug("scan module '" + sm.getName() 
										+ "' was added.");
							}
						}
					}
				}
			});
		}
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
		if (id < 1) {
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
	 * Get an available id for a scan module. The ID is reserved until
	 * {@link #resetReservedIds()} is called if reservation is set via 
	 * {@link #setReserveIds(boolean)}.
	 * <p>
	 * Not Thread safe !
	 * 
	 * @return an available id for a scan module
	 */
	public int getAvailableScanModuleId() {
		int i = 1;
		while (this.getScanModuleById(i) != null 
				|| reservedIds.contains(i)) {
			i++;
		}
		if (this.reserveIds) {
			this.reservedIds.add(i);
		}
		return i;
	}

	/**
	 * Clears the list of reserved scan module IDs.
	 * 
	 * @since 1.19
	 */
	public void resetReservedIds() {
		this.reservedIds.clear();
	}

	/**
	 * Sets whether IDs requested by {@link #getAvailableScanModuleId()} are 
	 * reserved.
	 * 
	 * @param reserve <code>true</code> if IDs should be reserved, 
	 * 		<code>false</code> otherwise
	 * 
	 * @since 1.19
	 */
	public void setReserveIds(boolean reserve) {
		this.reserveIds = reserve;
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
			this.propertyChangeSupport.firePropertyChange(
					Chain.POSITION_COUNT_PROP, this.positionCount,
					this.positionCount = null);
			return;
		}
		ScanModule first = 
				this.getStartEvent().getConnector().getChildScanModule();
		this.propertyChangeSupport.firePropertyChange(
				Chain.POSITION_COUNT_PROP, this.positionCount,
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
				return subChainHead.getPositionCount() + 
						subChainHead.getPositionCount()
						* this.calculatePositionCount(subChainHead
								.getNested().getChildScanModule());
			} else if (subChainHead.getNested() == null) {
				return subChainHead.getPositionCount()
						+ this.calculatePositionCount(subChainHead.getAppended()
								.getChildScanModule());
			} else {
				return subChainHead.getPositionCount() + 
						subChainHead.getPositionCount()
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
		if (scanModule == null) {
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
		if (scanModule == null) {
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
		return new ArrayList<>(this.scanModules);
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
	 * Returns a list of pause events (original list).
	 * 
	 * @return a list of pause events (original list)
	 */
	public List<ControlEvent> getPauseEvents() {
		return this.pauseControlEventManager.getEvents();
	}

	/**
	 * Adds the given {@link de.ptb.epics.eve.data.scandescription.PauseEvent}.
	 * 
	 * @param pauseEvent the 
	 * 		{@link de.ptb.epics.eve.data.scandescription.PauseEvent} that 
	 * 		should be added
	 * @return <code>true</code> if successful, <code>false</code> otherwise
	 * @author Marcus Michalsky
	 * @since 1.19
	 */
	public boolean addPauseEvent(final PauseEvent pauseEvent) {
		if (this.pauseControlEventManager.addControlEvent(pauseEvent)) {
			this.registerEventValidProperty(pauseEvent);
			this.propertyChangeSupport.firePropertyChange(Chain.PAUSE_EVENT_PROP,
					null, pauseEvent);
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
	 * @author Marcus Michalsky
	 * @since 1.19
	 */
	public boolean removePauseEvent(final PauseEvent pauseEvent) {
		if (this.pauseControlEventManager.removeEvent(pauseEvent)) {
			this.unregisterEventValidProperty(pauseEvent);
			this.propertyChangeSupport.firePropertyChange(Chain.PAUSE_EVENT_PROP,
					pauseEvent, null);
			return true;
		}
		return false;
	}
	
	/**
	 * Removes all pause events.
	 * 
	 * @author Marcus Michalsky
	 * @since 1.19
	 */
	public void removePauseEvents() {
		this.pauseControlEventManager.removeAllEvents();
		this.propertyChangeSupport.firePropertyChange(Chain.PAUSE_EVENT_PROP,
				this.pauseControlEventManager.getEvents(), null);
	}

	/**
	 * Returns a list of break events (original list).
	 * 
	 * @return a list of break events (original list)
	 * @author Marcus Michalsky
	 * @since 1.19
	 */
	public List<ControlEvent> getBreakEvents() {
		return this.breakControlEventManager.getEvents();
	}

	/**
	 * Adds the given {@link de.ptb.epics.eve.data.scandescription.ControlEvent} 
	 * as a break event.
	 * 
	 * @param breakEvent the 
	 * 		{@link de.ptb.epics.eve.data.scandescription.ControlEvent} that 
	 * 		should be added as a break event
	 * @return <code>true</code> if successful, <code>false</code> otherwise
	 * @author Marcus Michalsky
	 * @since 1.19
	 */
	public boolean addBreakEvent(final ControlEvent breakEvent) {
		if (this.breakControlEventManager.addControlEvent(breakEvent)) {
			this.registerEventValidProperty(breakEvent);
			this.propertyChangeSupport.firePropertyChange(Chain.BREAK_EVENT_PROP,
					null, breakEvent);
			return true;
		}
		return false;
	}

	/**
	 * Removes the given break event.
	 * 
	 * @param breakEvent the break event that should be removed
	 * @return <code>true</code> if successfull, </code> otherwise
	 * @author Marcus Michalsky
	 * @since 1.19
	 */
	public boolean removeBreakEvent(final ControlEvent breakEvent) {
		if (this.breakControlEventManager.removeEvent(breakEvent)) {
			this.unregisterEventValidProperty(breakEvent);
			this.propertyChangeSupport.firePropertyChange(Chain.BREAK_EVENT_PROP,
					breakEvent, null);
			return true;
		}
		return false;
	}

	/**
	 * Removes all break events.
	 * 
	 * @author Marcus Michalsky
	 * @since 1.19
	 */
	public void removeBreakEvents() {
		this.breakControlEventManager.removeAllEvents();
		this.propertyChangeSupport.firePropertyChange(Chain.BREAK_EVENT_PROP,
				this.breakControlEventManager.getEvents(), null);
	}

	/**
	 * Returns a list of start events (original list).
	 * <p>
	 * Never contains more than one element !
	 * @return a list of start events (original list)
	 */
	public List<ControlEvent> getStartEvents() {
		return this.startControlEventManager.getEvents();
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
		return this.startControlEventManager.addControlEvent(startEvent);
	}

	/**
	 * Removes the given start event.
	 * 
	 * @param startEvent the start event that should be removed
	 * @return <code>true</code> if successful, <code>false</code> otherwise
	 */
	public boolean removeStartEvent(final ControlEvent startEvent) {
		return this.startControlEventManager.removeEvent(startEvent);
	}

	/**
	 * Returns a list of stop events (original list).
	 * 
	 * @return a list of stop events (original list)
	 * @author Marcus Michalsky
	 * @since 1.19
	 */
	public List<ControlEvent> getStopEvents() {
		return this.stopControlEventManager.getEvents();
	}

	/**
	 * Adds the given {@link de.ptb.epics.eve.data.scandescription.ControlEvent} 
	 * as a stop event.
	 * 
	 * @param stopEvent the 
	 * 		{@link de.ptb.epics.eve.data.scandescription.ControlEvent} that 
	 * 		should be added as a stop event
	 * @return <code>true</code> if successful, <code>false</code> otherwise
	 * @author Marcus Michalsky
	 * @since 1.19
	 */
	public boolean addStopEvent(final ControlEvent stopEvent) {
		if (this.stopControlEventManager.addControlEvent(stopEvent)) {
			this.registerEventValidProperty(stopEvent);
			this.propertyChangeSupport.firePropertyChange(Chain.STOP_EVENT_PROP,
					null, stopEvent);
			return true;
		}
		return false;
	}

	/**
	 * Removes the given stop event.
	 * 
	 * @param stopEvent the stop event that should be removed
	 * @return <code>true</code> if successful, <code>false</code> otherwise
	 * @author Marcus Michalsky
	 * @since 1.19
	 */
	public boolean removeStopEvent(final ControlEvent stopEvent) {
		if (this.stopControlEventManager.removeEvent(stopEvent)) {
			this.unregisterEventValidProperty(stopEvent);
			this.propertyChangeSupport.firePropertyChange(Chain.STOP_EVENT_PROP,
					stopEvent, null);
			return true;
		}
		return false;
	}

	/**
	 * Removes all stop events.
	 * 
	 * @author Marcus Michalsky
	 * @since 1.19
	 */
	public void removeStopEvents() {
		this.stopControlEventManager.removeAllEvents();
		this.propertyChangeSupport.firePropertyChange(Chain.STOP_EVENT_PROP,
				this.stopControlEventManager.getEvents(), null);
	}

	/**
	 * Returns a list of redo events (original list).
	 * 
	 * @return a list of redo events (original list)
	 * @author Marcus Michalsky
	 * @since 1.19
	 */
	public List<ControlEvent> getRedoEvents() {
		return this.redoControlEventManager.getEvents();
	}

	/**
	 * Adds the given {@link de.ptb.epics.eve.data.scandescription.ControlEvent} 
	 * as a redo event.
	 * 
	 * @param redoEvent the 
	 * 		{@link de.ptb.epics.eve.data.scandescription.ControlEvent} that 
	 * 		should be added as a redo event
	 * @return <code>true</code> if successful, <code>false</code> otherwise
	 * @author Marcus Michalsky
	 * @since 1.19
	 */
	public boolean addRedoEvent(final ControlEvent redoEvent) {
		if (this.redoControlEventManager.addControlEvent(redoEvent)) {
			this.registerEventValidProperty(redoEvent);
			this.propertyChangeSupport.firePropertyChange(Chain.REDO_EVENT_PROP,
					null, redoEvent);
			return true;
		}
		return false;
	}

	/**
	 * Removes the given redo event.
	 * 
	 * @param redoEvent the redo event that should be removed
	 * @return <code>true</code> if successful, <code>false</code> otherwise
	 * @author Marcus Michalsky
	 * @since 1.19
	 */
	public boolean removeRedoEvent(final ControlEvent redoEvent) {
		if (this.redoControlEventManager.removeEvent(redoEvent)) {
			this.unregisterEventValidProperty(redoEvent);
			this.propertyChangeSupport.firePropertyChange(Chain.REDO_EVENT_PROP,
					redoEvent, null);
			return true;
		}
		return false;
	}

	/**
	 * Removes all redo events.
	 * 
	 * @author Marcus Michalsky
	 * @since 1.19
	 */
	public void removeRedoEvents() {
		this.redoControlEventManager.removeAllEvents();
		this.propertyChangeSupport.firePropertyChange(Chain.REDO_EVENT_PROP,
				this.redoControlEventManager.getEvents(), null);
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
		return this.pauseControlEventManager.getEvents().contains(pauseEvent);
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
		return this.redoControlEventManager.getEvents().contains(redoEvent);
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
		return this.breakControlEventManager.getEvents().contains(breakEvent);
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
		return (controlEvent instanceof PauseEvent 
				&& this.isPauseEventOfTheChain((PauseEvent) controlEvent)) 
				|| this.isBreakEventOfTheChain(controlEvent) 
				|| this.isRedoEventOfTheChain(controlEvent);
	}

	/**
	 * Returns a valid id for a plot.
	 * 
	 * @return a valid id for a plot
	 */
	public int getAvailablePlotId() {
		List<Integer> plotIds = new ArrayList<>();
		for (ScanModule sm : this.scanModules) {
			for (PlotWindow pw : sm.getPlotWindows()) {
				plotIds.add(pw.getId());
			}
		}
		Collections.sort(plotIds);
		int i = 1;
		while (plotIds.contains(i)) {
			i++;
		}
		return i;
	}

	/**
	 * Returns a map which keys are plot window ids and values are lists of 
	 * scan modules which contain plot windows using this id.
	 * 
	 * Can be used to check if a used plot window id is also used elsewhere
	 * (and where it is used). Remember that the scan module of the plot window
	 * to check for is also included.
	 * 
	 * @return a map of plot window id keys and scan module list values 
	 *   containing plot windows using this id
	 * @since 1.30
	 */
	public Map<Integer,List<ScanModule>> getUsedIds() {
		/*
		 * Idea: the plod id is the key. Iterate over each scan module and 
		 * for each plot in it put the scan module into the corresponding list.
		 * Each List with size == 1 must be removed afterwards. The remaining 
		 * Map contains all Ids (Keys) which are used more than once. The value
		 * is the list of scan modules where this plot id is used.
		 */
		Map<Integer, List<ScanModule>> usedIds = new HashMap<>();
		for (ScanModule sm : this.scanModules) {
			for (PlotWindow plotWindow : sm.getPlotWindows()) {
				if (usedIds.get(plotWindow.getId()) == null) {
					List<ScanModule> smList = new LinkedList<>();
					smList.add(sm);
					usedIds.put(plotWindow.getId(), smList);
				} else {
					usedIds.get(plotWindow.getId()).add(sm);
				}
			}
		}
		return usedIds;
	}

	/**
	 * Returns a list of integers containing the scan module ids in order 
	 * they are executed (started).
	 * 
	 * @return a list of integers containing scan module ids in execution order
	 * @since 1.26
	 */
	public List<Integer> getExecutionOrder() {
		Node<Integer> binTree = getBinaryTree(this.getStartEvent().
				getConnector().getChildScanModule());
		List<Integer> preOrderList = new ArrayList<>();
		binTree.preOrder(preOrderList);
		return preOrderList;
	}

	private Node<Integer> getBinaryTree(ScanModule root) {
		if (root.getNested() == null && root.getAppended() == null) {
			return new Node<>(root.getId(), null, null);
		}
		if (root.getNested() == null) {
			return new Node<>(root.getId(), null, 
					getBinaryTree(root.getAppended().getChildScanModule()));
		}
		if (root.getAppended() == null) {
			return new Node<>(root.getId(),  
					getBinaryTree(root.getNested().getChildScanModule()), 
					null);
		}
		return new Node<>(root.getId(), 
				getBinaryTree(root.getNested().getChildScanModule()), 
				getBinaryTree(root.getAppended().getChildScanModule()));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<IModelError> getModelErrors() {
		final List<IModelError> errorList = new ArrayList<>();

		errorList.addAll(this.pauseControlEventManager.getModelErrors());
		errorList.addAll(this.breakControlEventManager.getModelErrors());
		errorList.addAll(this.redoControlEventManager.getModelErrors());
		errorList.addAll(this.stopControlEventManager.getModelErrors());
		errorList.addAll(this.startControlEventManager.getModelErrors());

		for (ScanModule sm : this.scanModules) {
			errorList.addAll(sm.getModelErrors());
		}
		return errorList;
	}

	/*
	 * 
	 */
	private void updateListeners() {
		final CopyOnWriteArrayList<IModelUpdateListener> list = 
			new CopyOnWriteArrayList<>(this.updateListener);
		
		for (IModelUpdateListener imul : list) {
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

	/**
	 * Add FXCollection listener to detect changes of scan modules.
	 * <p>
	 * A change can be one of the following:
	 * <ul>
	 * 	<li>a scan module was updated</li>
	 *  <li>one or more scan modules have been added</li>
	 *  <li>one or more scan modules have been removed</li>
	 * </ul>
	 * 
	 * @param listener the list change listener
	 * @since 1.19
	 * @author Marcus Michalsky
	 * @see {@link javafx.collections.ObservableList}
	 */
	public void addScanModuleChangeListener(
			ListChangeListener<? super ScanModule> listener) {
		this.scanModules.addListener(listener);
	}

	/**
	 * Remove FXCollection listener to no longer detect changes of scan modules
	 * 
	 * @param listener the list change listener
	 * @since 1.19
	 * @author Marcus Michalsky
	 */
	public void removeScanModuleChangeListener(
			ListChangeListener<? super ScanModule> listener) {
		this.scanModules.removeListener(listener);
	}

	private void registerEventValidProperty(ControlEvent controlEvent) {
		if (controlEvent.getEvent() instanceof ScheduleEvent 
				|| controlEvent.getEvent() instanceof DetectorEvent) {
			((ScanEvent) controlEvent.getEvent()).addPropertyChangeListener(
					ScanEvent.VALID_PROP, this);
		}
	}

	private void unregisterEventValidProperty(ControlEvent controlEvent) {
		if (controlEvent.getEvent() instanceof ScheduleEvent 
				|| controlEvent.getEvent() instanceof DetectorEvent) {
			((ScanEvent) controlEvent.getEvent()).removePropertyChangeListener(
					ScanEvent.VALID_PROP, this);
		}
	}

	/**
	 * Due to the late registration of ScanEvents (due to mutability) during 
	 * scan description loading the control events don't register themselves 
	 * via registerEventValidProperty(ControlEvent) because their events aren't
	 * set at that time. So it must be triggered manually afterwards.
	 * Usage of this function is therefore only necessary during scan description 
	 * loading.
	 * 
	 * @author Marcus Michalsky
	 * @since 1.19
	 * @see Redmine #1401 Comments #16,#17
	 */
	public void registerEventValidProperties() {
		for (ControlEvent controlEvent : this.getPauseEvents()) {
			this.registerEventValidProperty(controlEvent);
		}
		for (ControlEvent controlEvent : this.getRedoEvents()) {
			this.registerEventValidProperty(controlEvent);
		}
		for (ControlEvent controlEvent : this.getStopEvents()) {
			this.registerEventValidProperty(controlEvent);
		}
		for (ControlEvent controlEvent : this.getBreakEvents()) {
			this.registerEventValidProperty(controlEvent);
		}
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void propertyChange(PropertyChangeEvent e) {
		if (e.getPropertyName().equals(ScanEvent.VALID_PROP) 
				&& e.getNewValue().equals(Boolean.FALSE)) {
			logger.debug(((ScanEvent)e.getSource()).getName() + 
					" (Chain: " + this.getId() + ") " +
					" got invalid -> start removal");
			this.removeInvalidScanEvents();
		}
	}
	
	private void removeInvalidScanEvents() {
		for (ControlEvent controlEvent : new CopyOnWriteArrayList<ControlEvent>(
				this.getPauseEvents())) {
			if (controlEvent.getEvent() instanceof ScanEvent 
					&& !((ScanEvent) controlEvent.getEvent()).isValid()) {
				this.removePauseEvent((PauseEvent) controlEvent);
				logger.debug("Pause Event " + 
						controlEvent.getEvent().getName() + 
						" removed from chain " + this.getId());
			}
		}
		for (ControlEvent controlEvent : new CopyOnWriteArrayList<ControlEvent>(
				this.getBreakEvents())) {
			if (controlEvent.getEvent() instanceof ScanEvent 
					&& !((ScanEvent) controlEvent.getEvent()).isValid()) {
				this.removeBreakEvent(controlEvent);
				logger.debug("Break Event " + 
						controlEvent.getEvent().getName() + 
						" removed from chain " + this.getId());
			}
		}
		for (ControlEvent controlEvent : new CopyOnWriteArrayList<ControlEvent>(
				this.getStopEvents())) {
			if (controlEvent.getEvent() instanceof ScanEvent 
					&& !((ScanEvent) controlEvent.getEvent()).isValid()) {
				this.removeStopEvent(controlEvent);
				logger.debug("Stop Event " + 
						controlEvent.getEvent().getName() + 
						" removed from chain " + this.getId());
			}
		}
		for (ControlEvent controlEvent : new CopyOnWriteArrayList<ControlEvent>(
				this.getRedoEvents())) {
			if (controlEvent.getEvent() instanceof ScanEvent 
					&& !((ScanEvent) controlEvent.getEvent()).isValid()) {
				this.removeRedoEvent(controlEvent);
				logger.debug("Redo Event " + 
						controlEvent.getEvent().getName() + 
						" removed from chain " + this.getId());
			}
		}
	}
}