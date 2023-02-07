package de.ptb.epics.eve.viewer.views.engineview;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.apache.log4j.Logger;

import de.ptb.epics.eve.ecp1.client.interfaces.IChainStatusListener;
import de.ptb.epics.eve.ecp1.client.interfaces.IConnectionStateListener;
import de.ptb.epics.eve.ecp1.client.interfaces.IEngineStatusListener;
import de.ptb.epics.eve.ecp1.commands.ChainStatusCommand;
import de.ptb.epics.eve.ecp1.types.EngineStatus;
import de.ptb.epics.eve.viewer.Activator;
import de.ptb.epics.eve.viewer.views.engineview.enginestate.EngineChainPause;
import de.ptb.epics.eve.viewer.views.engineview.enginestate.EngineDisconnected;
import de.ptb.epics.eve.viewer.views.engineview.enginestate.EngineExecuting;
import de.ptb.epics.eve.viewer.views.engineview.enginestate.EngineGUIPause;
import de.ptb.epics.eve.viewer.views.engineview.enginestate.EngineHalted;
import de.ptb.epics.eve.viewer.views.engineview.enginestate.EngineIdleNoXMLLoaded;
import de.ptb.epics.eve.viewer.views.engineview.enginestate.EngineIdleXMLLoaded;
import de.ptb.epics.eve.viewer.views.engineview.enginestate.EngineInvalid;
import de.ptb.epics.eve.viewer.views.engineview.enginestate.EngineState;
import de.ptb.epics.eve.viewer.views.engineview.enginestate.EngineStopped;
import de.ptb.epics.eve.viewer.views.engineview.enginestate.EngineTryingToConnect;

/**
 * Context of State Pattern maintaining the engine state for button enabling.
 * 
 * @author Marcus Michalsky
 * @since 1.25
 */
public class ButtonManager implements IEngineStatusListener, 
		IChainStatusListener, IConnectionStateListener {
	public static final String ENGINE_STATE_PROP = "engineState";
	private static final Logger LOGGER = Logger.getLogger(
			ButtonManager.class.getName());
	private static ButtonManager instance;
	private EngineState engineState;
	private PropertyChangeSupport propertyChangeSupport;

	private ButtonManager() {
		this.propertyChangeSupport = new PropertyChangeSupport(this);
		Activator.getDefault().getEcp1Client().addEngineStatusListener(this);
		Activator.getDefault().getEcp1Client().addChainStatusListener(this);
		Activator.getDefault().getEcp1Client().addConnectionStateListener(this);
		this.engineState = EngineDisconnected.getInstance();
	}
	
	public static ButtonManager getInstance() {
		if (ButtonManager.instance == null) {
			ButtonManager.instance = new ButtonManager();
		}
		return ButtonManager.instance;
	}	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void chainStatusChanged(ChainStatusCommand chainStatusCommand) {
		// nothing to do here (anymore since 1.38.1, see #6939)
	}

	/**
	 * @since 1.29
	 */
	public synchronized void tryingToConnect(boolean connecting) {
		EngineState oldValue = this.engineState;
		if (connecting) {
			this.engineState = EngineTryingToConnect.getInstance();
		} else {
			this.engineState = EngineDisconnected.getInstance();
		}
		this.logStateChange(oldValue, engineState);
		this.propertyChangeSupport.firePropertyChange(
				ButtonManager.ENGINE_STATE_PROP, oldValue, engineState);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void engineStatusChanged(EngineStatus engineStatus, String xmlName, 
			int repeatCount) {
		EngineState oldValue = this.engineState;
		switch (engineStatus) {
		case EXECUTING:
			this.engineState = EngineExecuting.getInstance();
			break;
		case HALTED:
			this.engineState = EngineHalted.getInstance();
			break;
		case IDLE_NO_XML_LOADED:
			this.engineState = EngineIdleNoXMLLoaded.getInstance();
			break;
		case IDLE_XML_LOADED:
			this.engineState = EngineIdleXMLLoaded.getInstance();
			break;
		case INVALID:
			this.engineState = EngineInvalid.getInstance();
			break;
		case LOADING_XML:
			this.engineState = EngineIdleNoXMLLoaded.getInstance();
			break;
		case PAUSED:
			break;
		case GUI_PAUSE:
			this.engineState = EngineGUIPause.getInstance();
			break;
		case CHAIN_PAUSE:
			this.engineState = EngineChainPause.getInstance();
			break;
		case STOPPED:
			this.engineState = EngineStopped.getInstance();
			break;
		default:
			break;
		}
		this.logStateChange(oldValue, engineState);
		this.propertyChangeSupport.firePropertyChange(
				ButtonManager.ENGINE_STATE_PROP, oldValue, engineState);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stackConnected() {
		// nothing to do here, after a successful connection, an engine state 
		// will be sent, setting the status
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stackDisconnected() {
		EngineState oldValue = this.engineState;
		this.engineState = EngineDisconnected.getInstance();
		this.logStateChange(oldValue, engineState);
		this.propertyChangeSupport.firePropertyChange(
				ButtonManager.ENGINE_STATE_PROP, oldValue, engineState);
	}

	private void logStateChange(EngineState oldValue, EngineState newValue) {
		LOGGER.debug("Engine state changed: " + 
				oldValue.getClass().getSimpleName() + 
				" " + 
				(char)10145 + 
				" " + 
				this.engineState.getClass().getSimpleName());
	}
	
	/**
	 * @see {@link java.beans.PropertyChangeSupport#addPropertyChangeListener(PropertyChangeListener)}
	 */
	public void addPropertyChangeListener(String property, 
			PropertyChangeListener listener) {
		this.propertyChangeSupport.addPropertyChangeListener(property, 
				listener);
	}

	/**
	 * @see {@link java.beans.PropertyChangeSupport#removePropertyChangeListener(PropertyChangeListener)}
	 */
	public void removePropertyChangeListener(String property, 
			PropertyChangeListener listener) {
		this.propertyChangeSupport.removePropertyChangeListener(property, 
				listener);
	}
}