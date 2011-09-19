package de.ptb.epics.eve.viewer.views.deviceoptionsview;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.csstudio.data.values.IEnumeratedMetaData;
import org.csstudio.data.values.IMetaData;
import org.csstudio.data.values.ValueUtil;
import org.csstudio.utility.pv.PV;
import org.csstudio.utility.pv.PVFactory;
import org.csstudio.utility.pv.PVListener;

import de.ptb.epics.eve.data.TransportTypes;
import de.ptb.epics.eve.data.measuringstation.Option;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateProvider;
import de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent;
import de.ptb.epics.eve.viewer.Activator;
import de.ptb.epics.eve.viewer.MessageSource;
import de.ptb.epics.eve.viewer.messages.MessageTypes;
import de.ptb.epics.eve.viewer.messages.ViewerMessage;

/**
 * <code>OptionConnector</code> connects the 
 * {@link de.ptb.epics.eve.data.measuringstation.Option} with a 
 * {@link org.csstudio.utility.pv.PV} (Process Variable).
 * 
 * @author ?
 * @author Marcus Michalsky
 */
public class OptionConnector implements IModelUpdateProvider, PVListener {

	private static Logger logger = 
			Logger.getLogger(OptionConnector.class.getName());
	
	private final Option option;
	private String value;
	private PV pv;
	private boolean isConnected = false;
	
	private final List<IModelUpdateListener> modelUpdateListener;
	private boolean readOnly = false;
	private String[] discreteValues = null;
	private boolean isDiscrete = false;
	
	/**
	 * Constructs an <code>OptionConnector</code>.
	 * 
	 * @param option
	 * @throws IllegalArgumentException if <code>option</code> is <code>null</code>
	 */
	public OptionConnector(final Option option) {
		if(option == null) {
			throw new IllegalArgumentException(
					"Option Connector: 'option' must not be null!");
		}
		this.modelUpdateListener = new ArrayList<IModelUpdateListener>();
		this.option = option;

		if((option.getValue() == null) || (option.getValue() == null)) return;

		isDiscrete = option.isDiscrete();
		readOnly = option.getValue().isReadOnly();
		if (isDiscrete) {
			discreteValues = 
				option.getValue().getDiscreteValues().toArray(new String[0]);
		}
		if (option.getValue().getAccess().getTransport() == TransportTypes.CA) {
			option.getValue().isReadOnly();
			String pvname = 
				"ca://" + option.getValue().getAccess().getVariableID();
			try {
				pv = PVFactory.createPV(pvname);
				pv.addListener(this);
				pv.start();
				this.value = pv.getStateInfo();
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
	}
	
	/**
	 * Returns the {@link de.ptb.epics.eve.data.measuringstation.Option} wrapped 
	 * in the <code>OptionConnector</code>.
	 * 
	 * @return the wrapped {@link de.ptb.epics.eve.data.measuringstation.Option}
	 */
	public Option getOption() {
		return this.option;
	}
	
	/**
	 * Returns the value of the process variable corresponding to the option.
	 * 
	 * @return the value of the process variable
	 */
	public String getValue() {
		return this.value;
	}
	
	/**
	 * Sets the value of the process variable corresponding to the option.
	 * 
	 * @param value the value that should be set
	 */
	public void setValue(final String value) {
		if ((pv == null) || !pv.isConnected() || readOnly)  return;
	
		try {	
			String newValue = value;
			if (isDiscrete){
				int number = Integer.parseInt(value);
				if ((number >= 0) && (number < discreteValues.length)) {
					newValue = discreteValues[number];
				}
			}
			if(logger.isInfoEnabled()) {
				logger.info("Now set " + pv.getName() + " to "+newValue);
			}
			pv.setValue(newValue);
		} catch (Throwable ex) {
			logger.error("Unable to set " + pv.getName() + " to " + value, ex);
		}
	}

	/**
	 * Checks whether the process variable is connected.
	 * 
	 * @return <code>true</code> if the process variable is connected, 
	 * 			<code>false</code> otherwise
	 */
	public boolean isConnected() {
		return this.isConnected;
	}

	/**
	 * Checks whether the process variable is read only.
	 * 
	 * @return <code>true</code> if the process variable is read only, 
	 * 			<code>false</code> otherwise
	 */
	public boolean isReadOnly() {
		return readOnly;
	}

	/**
	 * Checks whether the process variable is discrete. 
	 * 
	 * @return <code>true</code> if the process variable is discrete, 
	 * 			<code>false</code> otherwise
	 */
	public boolean isDiscrete() {
		return isDiscrete;
	}

	/**
	 * Returns the discrete values of the process variable or <code>null</code> 
	 * if {@link #isDiscrete()} == <code>false</code>.
	 * 
	 * @return the discrete values of the process variable or <code>null</code> 
	 * if {@link #isDiscrete()} == <code>false</code>
	 */
	public String[] getDiscreteValues() {
		return discreteValues;
	}

	/**
	 * Dispose-like method of the option. Removes all references 
	 * (e.g. listeners) from it.
	 */
	public void detach() {
	    if (pv != null)
	    {
	    	Activator.getDefault().getMessagesContainer().addMessage(
	    			new ViewerMessage(MessageSource.VIEWER, MessageTypes.INFO, 
	    					"Disposing text for " + pv.getName()));
	        pv.removeListener(this);
	        pv.stop();
	        pv = null;
	    }
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void pvValueUpdate(PV pv) {
		if(pv.isConnected()) isConnected = true;
		if(!pv.isWriteAllowed()) readOnly = true;
		this.value = ValueUtil.getString(pv.getValue());
		try {
			Double.parseDouble(this.value);
			this.value = String.format(
					Locale.US, "%g", ValueUtil.getDouble(pv.getValue()));
		} catch(final Exception e) {
			//logger.error(e.getMessage(), e);
		}
		
		final IMetaData meta = pv.getValue().getMetaData();
		if (meta instanceof IEnumeratedMetaData) {
			this.discreteValues = ((IEnumeratedMetaData)meta).getStates();
			isDiscrete  = true;
		}
		final Iterator<IModelUpdateListener> it = 
				this.modelUpdateListener.iterator();
		while(it.hasNext()) {
			it.next().updateEvent(new ModelUpdateEvent(this, null));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void pvDisconnected(PV pv) {
		isConnected = false;
		value = pv.getStateInfo();
		final Iterator<IModelUpdateListener> it = 
				this.modelUpdateListener.iterator();
		while(it.hasNext()) {
			it.next().updateEvent(new ModelUpdateEvent(this, null));
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
}