package de.ptb.epics.eve.ecp1.helper.statustracker;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.apache.log4j.Logger;

import de.ptb.epics.eve.ecp1.client.ECP1Client;
import de.ptb.epics.eve.ecp1.client.interfaces.IConnectionStateListener;
import de.ptb.epics.eve.ecp1.client.interfaces.IEngineStatusListener;
import de.ptb.epics.eve.ecp1.types.EngineStatus;

/**
 * <p>Keeps track of the engine status. Anyone interested in engine status changed 
 * can call the constructor giving an engine client and a listener which will be 
 * informed of engine status changes.</p>
 * 
 * <p>Possible engine states are defined in {@link de.ptb.epics.eve.ecp1.types.EngineStatus}.</p>
 * 
 * <p>{@link java.beans.PropertyChangeListener} interested in changes should listen to the {@link #ENGINE_STATUS_PROP} property.</p>
 * 
 * <p>The connection state of the current engine status can be retrieved via {@link #isConnected()}.</p>
 * 
 * @author Marcus Michalsky
 * @since 1.28
 */
public class EngineStatusTracker implements IConnectionStateListener, IEngineStatusListener {
	private static final Logger LOGGER = Logger.getLogger(EngineStatusTracker.class.getName());
	
	public static final String ENGINE_STATUS_PROP = "currentState";
	
	private final EngineState connected;
	private final EngineState disconnected;
	private final EngineState executing;
	private final EngineState halted;
	private final EngineState idleNoXMLLoaded;
	private final EngineState idleXMLLoaded;
	private final EngineState invalid;
	private final EngineState loadingXML;
	private final EngineState paused;
	private final EngineState stopped;
	
	private EngineState currentState;
	
	private PropertyChangeSupport propertyChangeSupport;
	
	public EngineStatusTracker(ECP1Client engineClient, PropertyChangeListener listener) {
		this.connected = Connected.getInstance();
		this.disconnected = Disconnected.getInstance();
		this.executing = Executing.getInstance();
		this.halted = Halted.getInstance();
		this.idleNoXMLLoaded = IdleNoXMLLoaded.getInstance();
		this.idleXMLLoaded = IdleXMLLoaded.getInstance();
		this.invalid = Invalid.getInstance();
		this.loadingXML = LoadingXML.getInstance();
		this.paused = Paused.getInstance();
		this.stopped = Stopped.getInstance();
		
		this.currentState = this.disconnected;
		
		this.propertyChangeSupport = new PropertyChangeSupport(this);
		// listener must be registered before listening to the engine, to avoid missed messages
		this.propertyChangeSupport.addPropertyChangeListener(listener);
		engineClient.addConnectionStateListener(this);
		engineClient.addEngineStatusListener(this);
		
		LOGGER.debug("EngineStatusTracker constructed.");
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void engineStatusChanged(EngineStatus engineStatus, String xmlName, int repeatCount) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Engine Status changed: " + engineStatus.toString());
		}
		EngineState oldState = this.currentState;
		switch (engineStatus) {
		case EXECUTING:
			this.currentState = this.executing;
			break;
		case HALTED:
			this.currentState = this.halted; 
			break;
		case IDLE_NO_XML_LOADED:
			this.currentState = this.idleNoXMLLoaded;
			break;
		case IDLE_XML_LOADED:
			this.currentState = this.idleXMLLoaded;
			break;
		case INVALID:
			this.currentState = this.invalid;
			break;
		case LOADING_XML:
			this.currentState = this.loadingXML;
			break;
		case PAUSED:
			this.currentState = this.paused;
			break;
		case STOPPED:
			this.currentState = this.stopped;
			break;
		default:
			break;
		}
		this.propertyChangeSupport.firePropertyChange(
				EngineStatusTracker.ENGINE_STATUS_PROP, 
				oldState, this.currentState);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stackConnected() {
		LOGGER.debug("Engine connected");
		EngineState oldState = this.currentState;
		this.currentState = this.connected;
		this.propertyChangeSupport.firePropertyChange(
				EngineStatusTracker.ENGINE_STATUS_PROP, 
				oldState, this.currentState);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stackDisconnected() {
		LOGGER.debug("Engine disconnected");
		EngineState oldState = this.currentState;
		this.currentState = this.disconnected;
		this.propertyChangeSupport.firePropertyChange(
				EngineStatusTracker.ENGINE_STATUS_PROP, 
				oldState, this.currentState);
	}
	
	/**
	 * Returns whether the engine is connected
	 * @return <code>true</code> if the engine is connected, <code>false</code> otherwise
	 */
	public boolean isConnected() {
		return this.currentState.isConnected();
	}
}