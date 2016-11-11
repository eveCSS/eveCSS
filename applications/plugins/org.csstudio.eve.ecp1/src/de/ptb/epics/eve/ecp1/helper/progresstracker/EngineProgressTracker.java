package de.ptb.epics.eve.ecp1.helper.progresstracker;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.apache.log4j.Logger;

import de.ptb.epics.eve.ecp1.client.ECP1Client;
import de.ptb.epics.eve.ecp1.client.interfaces.IChainProgressListener;
import de.ptb.epics.eve.ecp1.client.interfaces.IConnectionStateListener;
import de.ptb.epics.eve.ecp1.client.interfaces.IEngineStatusListener;
import de.ptb.epics.eve.ecp1.client.interfaces.IErrorListener;
import de.ptb.epics.eve.ecp1.client.model.Error;
import de.ptb.epics.eve.ecp1.commands.ChainProgressCommand;
import de.ptb.epics.eve.ecp1.types.EngineStatus;
import de.ptb.epics.eve.ecp1.types.ErrorType;

/**
 * <p>Keeps track of the progress of the scan currently loaded in the given engine.</p>
 * 
 * <p>{@link java.beans.PropertyChangeListener} interested in changes should listen to the {@link #PROGRESS_PROP} property.</p>
 * 
 * <p>The current {@link de.ptb.epics.eve.ecp1.helper.progresstracker.Progress} could be retrieved via {@link #getProgress()}.</p>
 * 
 * @author Marcus Michalsky
 * @since 1.28
 */
public class EngineProgressTracker implements IErrorListener, IChainProgressListener, 
		IEngineStatusListener, IConnectionStateListener {
	public static final String PROGRESS_PROP = "progress";
	
	private static final Logger LOGGER = Logger.getLogger(EngineProgressTracker.class.getName());
	
	private Progress progress;
	private EngineStatus engineStatus;
	
	private PropertyChangeSupport propertyChangeSupport;
	
	public EngineProgressTracker(ECP1Client engineClient, PropertyChangeListener listener) {
		this.progress = null;
		this.propertyChangeSupport = new PropertyChangeSupport(this);
		// listener must be registered before listening to the engine, to avoid missed messages
		this.propertyChangeSupport.addPropertyChangeListener(listener);
		
		engineClient.addErrorListener(this);
		engineClient.addChainProgressListener(this);
		engineClient.addEngineStatusListener(this);
		engineClient.addConnectionStateListener(this);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void chainProgressChanged(ChainProgressCommand chainProgressCommand) {
		// current position is not measured yet -> progress = cPos-1
		this.progress.setCurrent(chainProgressCommand.getPositionCounter() - 1);
		this.propertyChangeSupport.firePropertyChange(
				EngineProgressTracker.PROGRESS_PROP, 
				null, this.progress);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void errorOccured(Error error) {
		if (error.getErrorType() == null) {
			return;
		}
		if (error.getErrorType().equals(ErrorType.MAX_POS_COUNT)) {
			final Error finalError = error;
			int max = Integer.parseInt(finalError.getText());
			LOGGER.debug("max poscount: " + max);
			this.progress = new Progress(max);
			if (EngineStatus.IDLE_XML_LOADED.equals(this.engineStatus)) {
				this.progress.setCurrent(0);
			}
			this.propertyChangeSupport.firePropertyChange(
					EngineProgressTracker.PROGRESS_PROP, 
					null, this.progress);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void engineStatusChanged(EngineStatus engineStatus, String xmlName, int repeatCount) {
		this.engineStatus = engineStatus;
		if (EngineStatus.IDLE_XML_LOADED.equals(engineStatus) && this.progress != null) {
			this.progress.setCurrent(0);
			this.propertyChangeSupport.firePropertyChange(
					EngineProgressTracker.PROGRESS_PROP, 
					null, this.progress);
		}
		if (EngineStatus.IDLE_NO_XML_LOADED.equals(engineStatus) && this.progress != null) {
			this.progress.setCurrent(this.progress.getMaximum());
			this.propertyChangeSupport.firePropertyChange(
					EngineProgressTracker.PROGRESS_PROP, 
					null, this.progress);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stackConnected() {
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stackDisconnected() {
		this.progress = null;
		this.propertyChangeSupport.firePropertyChange(
				EngineProgressTracker.PROGRESS_PROP, 
				null, this.progress);
	}
	
	/**
	 * <p>Returns the current progress or <code>null</code> if none available.</p>
	 * 
	 * <p>If {@link de.ptb.epics.eve.ecp1.helper.progresstracker.Progress#getCurrent()} is <code>null</code> 
	 * no data of processed positions were received yet.</p>
	 * 
	 * 
	 * @return the progress or <code>null</code> if none available
	 */
	public Progress getProgress() {
		return progress;
	}
}