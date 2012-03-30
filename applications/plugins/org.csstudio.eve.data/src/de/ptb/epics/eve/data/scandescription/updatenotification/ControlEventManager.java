package de.ptb.epics.eve.data.scandescription.updatenotification;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import de.ptb.epics.eve.data.scandescription.Chain;
import de.ptb.epics.eve.data.scandescription.Channel;
import de.ptb.epics.eve.data.scandescription.ControlEvent;
import de.ptb.epics.eve.data.scandescription.PauseEvent;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.data.scandescription.errors.IModelError;
import de.ptb.epics.eve.data.scandescription.errors.IModelErrorProvider;

/**
 * <code>ControlEventManager</code>.
 * 
 * @author ?
 * @author Marcus Michalsky
 */
public class ControlEventManager implements IControlEventProvider, IModelErrorProvider {

	private List<? extends ControlEvent> controlEventList;
	private List<IModelUpdateListener> modelUpdateListener;
	
	private Chain parentChain;
	private ScanModule parentScanModule;
	private Channel parentChannel;
	
	private ControlEventTypes controlEventType;
	
	private ControlEventManager(
			final List<? extends ControlEvent> controlEventList) {
		if(controlEventList == null) {
			throw new IllegalArgumentException("ControlEventManager:  " +
									"'controlEventList' must not be null!"); 
		}
		this.controlEventList = controlEventList;
		this.modelUpdateListener = new ArrayList<IModelUpdateListener>();
	}
	
	/**
	 * Constructs a <code>ControlEventManager</code>.
	 * 
	 * @param parentChain
	 * @param controlEventList
	 * @param controlEventType
	 * @throws IllegalArgumentException if <code>parentChain</code> is 
	 * 		   <code>null</code>
	 */
	public ControlEventManager(final Chain parentChain, 
						final List<? extends ControlEvent> controlEventList, 
						final ControlEventTypes controlEventType) {
		this(controlEventList);
		if(parentChain == null) {
			throw new IllegalArgumentException("ControlEventManager: " +
											"'parentChain' must not be null!");
		}
		this.parentChain = parentChain;
		this.controlEventType = controlEventType;
	}
	
	/**
	 * Constructs a <code>ControlEventManager</code>.
	 * 
	 * @param parentScanModule
	 * @param controlEventList
	 * @param controlEventType
	 * @throws IllegalArgumentException if <code>parentScanModule</code> is 
	 * 		   <code>null</code>
	 */
	public ControlEventManager(final ScanModule parentScanModule, 
						final List<? extends ControlEvent> controlEventList, 
						final ControlEventTypes controlEventType) {
		this(controlEventList);
		if(parentScanModule == null) {
			throw new IllegalArgumentException("ControlEventManager: " +
										"'parentScanModule' must not be null!");
		}
		this.parentScanModule = parentScanModule;
		this.controlEventType = controlEventType;
	}	

	/**
	 * Constructs a <code>ControlEventManager</code>.
	 * 
	 * @param parentChannel
	 * @param controlEventList
	 * @param controlEventType
	 * @throws IllegalArgumentException if <code>parentChannel</code> is
	 * 		   <code>null</code>
	 */
	public ControlEventManager(final Channel parentChannel, 
							final List<? extends ControlEvent> controlEventList,
			final ControlEventTypes controlEventType) {
		this(controlEventList);
		if(parentChannel == null) {
			throw new IllegalArgumentException("ControlEventManager: " +
										"'parentChannel' must not be null!");
		}
		this.parentChannel = parentChannel;
		this.controlEventType = controlEventType;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<? extends ControlEvent> getControlEventsList() {
		return new ArrayList<ControlEvent>(this.controlEventList);
	}

	/**
	 * 
	 * @param controlEvent
	 */
	public void addControlEvent(final ControlEvent controlEvent) {
		if(this.parentChain != null) {
			if (this.parentChain.getBreakControlEventManager() == this) {
				this.parentChain.addBreakEvent(controlEvent);
			} else if (this.parentChain.getRedoControlEventManager() == this) {
				this.parentChain.addRedoEvent(controlEvent);
			} else if (this.parentChain.getStopControlEventManager() == this) {
				this.parentChain.addStopEvent(controlEvent);
			} else if (this.parentChain.getStartControlEventManager() == this) {
				this.parentChain.addStartEvent(controlEvent);
			} else if (this.parentChain.getPauseControlEventManager() == this) {
				this.parentChain.addPauseEvent((PauseEvent)controlEvent);
			}
		} else if (this.parentScanModule != null) {
			if (this.parentScanModule.getBreakControlEventManager() == this) {
				this.parentScanModule.addBreakEvent(controlEvent);
			} else if (this.parentScanModule.getRedoControlEventManager() == this) {
				this.parentScanModule.addRedoEvent(controlEvent);
			} else if (this.parentScanModule.getPauseControlEventManager() == this) {
				this.parentScanModule.addPauseEvent( (PauseEvent)controlEvent );
			} else if (this.parentScanModule.getTriggerControlEventManager() == this) {
				this.parentScanModule.addTriggerEvent(controlEvent);
			}
		} else if(this.parentChannel != null) {
			this.parentChannel.addRedoEvent(controlEvent);
		}
	}
	
	/**
	 * 
	 * @param controlEvent
	 */
	public void removeControlEvent(final ControlEvent controlEvent) {
		if (this.parentChain != null) {
			if (this.parentChain.getBreakControlEventManager() == this) {
				this.parentChain.removeBreakEvent(controlEvent);
			} else if (this.parentChain.getRedoControlEventManager() == this) {
				this.parentChain.removeRedoEvent(controlEvent);
			} else if (this.parentChain.getStartControlEventManager() == this) {
				this.parentChain.removeStartEvent(controlEvent);
			} else if (this.parentChain.getStopControlEventManager() == this) {
				this.parentChain.removeStopEvent( controlEvent );
			} else if (this.parentChain.getPauseControlEventManager() == this) {
				this.parentChain.removePauseEvent((PauseEvent)controlEvent);
			}
		} else if (this.parentScanModule != null) {
			if (this.parentScanModule.getBreakControlEventManager() == this) {
				this.parentScanModule.removeBreakEvent(controlEvent);
			} else if (this.parentScanModule.getRedoControlEventManager() == this) {
				this.parentScanModule.removeRedoEvent(controlEvent);
			} else if (this.parentScanModule.getPauseControlEventManager() == this) {
				this.parentScanModule.removePauseEvent((PauseEvent)controlEvent);
			} else if (this.parentScanModule.getTriggerControlEventManager() == this) {
				this.parentScanModule.removeTriggerEvent(controlEvent);
			}
		} else if (this.parentChannel != null) {
			this.parentChannel.removeRedoEvent(controlEvent);
		}
	}
	
	/**
	 * Removes all events.
	 */
	public void removeAllControlEvents() {
		for(ControlEvent ce : this.getControlEventsList()) {
			this.removeControlEvent(ce);
		}
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
		updateListeners(modelUpdateEvent);
	}
	
	/**
	 * 
	 * @return
	 */
	public Chain getParentChain() {
		return this.parentChain;
	}

	/**
	 * 
	 * @return
	 */
	public ScanModule getParentScanModule() {
		return this.parentScanModule;
	}

	/**
	 * 
	 * @return
	 */
	public ControlEventTypes getControlEventType() {
		return this.controlEventType;
	}

	/**
	 * 
	 * @return
	 */
	public Channel getParentChannel() {
		return this.parentChannel;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<IModelError> getModelErrors() {
		final List<IModelError> errorList = new ArrayList<IModelError>();
		for (ControlEvent event: this.controlEventList) {
			errorList.addAll(event.getModelErrors());
		}
		return errorList;
	}
	
	/*
	 * 
	 */
	private void updateListeners(final ModelUpdateEvent event) {
		final CopyOnWriteArrayList<IModelUpdateListener> list = 
			new CopyOnWriteArrayList<IModelUpdateListener>(this.modelUpdateListener);
		
		for(IModelUpdateListener imul : list) {
			imul.updateEvent(event);
		}
	}
}