package de.ptb.epics.eve.data.scandescription.updatenotification;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import de.ptb.epics.eve.data.scandescription.ControlEvent;
import de.ptb.epics.eve.data.scandescription.errors.IModelError;
import de.ptb.epics.eve.data.scandescription.errors.IModelErrorProvider;

/**
 * Event Delegate.
 * 
 * @author Marcus Michalsky
 */
public class ControlEventManager implements IModelUpdateProvider,
		IModelUpdateListener, IModelErrorProvider {
	private List<ControlEvent> controlEvents;
	private List<IModelUpdateListener> modelUpdateListener;
	
	private ControlEventTypes controlEventType;
	
	private ControlEventManager() {
		this.controlEvents = new ArrayList<ControlEvent>();
		this.modelUpdateListener = new ArrayList<IModelUpdateListener>();
	}
	
	/**
	 * Constructor.
	 * 
	 * @param controlEventType the event type
	 * @since 1.19
	 */
	public ControlEventManager(final ControlEventTypes controlEventType) {
		this();
		this.controlEventType = controlEventType;
	}
	
	/**
	 * Copy Constructor.
	 * 
	 * @param controlEventManager the control event manager that should be copied
	 * @return a copy of the given control event manager
	 * @author Marcus Michalsky
	 * @since 1.19
	 */
	public static ControlEventManager newInstance(
			ControlEventManager controlEventManager) {
		ControlEventManager newControlEventManager = new ControlEventManager();
		newControlEventManager.controlEventType = controlEventManager
				.getControlEventType();
		for (ControlEvent event : controlEventManager.getEvents()) {
			newControlEventManager.addControlEvent(ControlEvent
					.newInstance(event));
		}
		return newControlEventManager;
	}
	
	/**
	 * Returns contained events (original list).
	 * 
	 * @return contained events (original list)
	 * @author Marcus Michalsky
	 * @since 1.19
	 */
	public List<ControlEvent> getEvents() {
		return this.controlEvents;
	}

	/**
	 * 
	 * @param controlEvent
	 */
	public boolean addControlEvent(final ControlEvent controlEvent) {
		if (this.controlEvents.add(controlEvent)) {
			controlEvent.addModelUpdateListener(this);
			this.updateListeners(new ModelUpdateEvent(this,
					new ControlEventMessage(controlEvent,
							ControlEventMessageEnum.ADDED)));
			return true;
		}
		return false;
	}
	
	/**
	 * 
	 * @param controlEvent
	 */
	public boolean removeEvent(final ControlEvent controlEvent) {
		if (this.controlEvents.remove(controlEvent)) {
			controlEvent.removeModelUpdateListener(this);
			this.updateListeners(new ModelUpdateEvent(this,
					new ControlEventMessage(controlEvent,
							ControlEventMessageEnum.REMOVED)));
			return true;
		}
		return false;
	}
	
	/**
	 * Removes all events.
	 */
	public void removeAllEvents() {
		CopyOnWriteArrayList<ControlEvent> events = 
				new CopyOnWriteArrayList<ControlEvent>(this.getEvents());
		
		for(ControlEvent ce : events) {
			this.removeEvent(ce);
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public ControlEventTypes getControlEventType() {
		return this.controlEventType;
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<IModelError> getModelErrors() {
		final List<IModelError> errorList = new ArrayList<IModelError>();
		for (ControlEvent event: this.controlEvents) {
			errorList.addAll(event.getModelErrors());
		}
		return errorList;
	}
}