package de.ptb.epics.eve.viewer.pv;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.csstudio.utility.pv.PV;
import org.csstudio.utility.pv.PVFactory;
import org.epics.pvmanager.*;
import org.epics.pvmanager.data.Alarm;
import org.epics.pvmanager.data.AlarmSeverity;
import org.epics.pvmanager.data.SimpleValueFormat;
import org.epics.pvmanager.data.VEnum;
import org.epics.pvmanager.data.ValueFormat;
import org.epics.pvmanager.data.ValueUtil;

import de.ptb.epics.eve.viewer.Activator;
import de.ptb.epics.eve.viewer.preferences.PreferenceConstants;

import static org.epics.pvmanager.ExpressionLanguage.*;
import static org.epics.pvmanager.util.TimeDuration.*;
import static org.csstudio.utility.pvmanager.ui.SWTUtil.*;

/**
 * <code>PVWrapper</code> wraps a {@link org.epics.pvmanager.PV}. During object 
 * creation the process variable automatically tries to connect. Invoke 
 * {@link #disconnect()} to close the connection. It cannot be reopened.
 * 
 * @author Marcus Michalsky
 * @since 1.1
 */
public class PVWrapper {
	
	// logging
	private static Logger logger = Logger.getLogger(PVWrapper.class.getName());
	
	// the wrapped process variable
	private org.epics.pvmanager.PV<Object,Object> pv;
	
	// workaround (pvmanager does not support a readonly check)
	// using the "old" pv lib to connect during object initialization
	// when the first value change is triggered (PVListener) the 
	// isWriteAllowed status is saved and the pv is disconnected.
	private org.csstudio.utility.pv.PV pv2;
	
	// the name of the process variable
	private String pvName;
	
	// the value of the process variable
	private String pvValue;
	
	// the severity of the process variable (status)
	private AlarmSeverity pvStatus;
	
	// indicates whether the process variable is discrete
	private boolean isDiscrete;
	
	// indicates whether the process variable is readonly
	private boolean isReadOnly;
	
	// a pv is connected after its first subscription update
	// until it is disconnected via the corresponding method
	private boolean isConnected;
	
	// contains the discrete values of the process variable
	// (or empty if not discrete)
	private List<String> discreteValues;
	
	// the refresh interval
	private int pvUpdateInterval;
	
	// helper to format process variable objects
	private ValueFormat valueFormat;
	
	// listener for process variable updates
	private ReadListener readListener;
	
	// listener for process variable writes
	private WriteListener writeListener;
	
	// workaround listener
	private PVListener pvListener;
	
	// Delegated Observable
	private PropertyChangeSupport propertyChangeSupport;
	
	// indicates whether the ENUM values are already read (performance)
	private boolean isEnumInitialized;
	
	/**
	 * Constructs a <code>PVWrapper</code>.
	 * 
	 * @param pvname the name (id) of the process variable
	 */
	public PVWrapper(String pvname) {
		this.pvName = pvname;
		this.pvValue = "";
		this.pvStatus = AlarmSeverity.UNDEFINED;
		this.isConnected = false;
		this.isDiscrete = false;
		this.discreteValues = new ArrayList<String>(0);
		
		// workaround start (pvmanager does not support readonly check)
		try {
			pv2 = PVFactory.createPV("ca://" + pvname);
			pv2.start();
			pvListener = new PVListener();
			pv2.addListener(pvListener);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			this.isReadOnly = true;
		}
		// workaround end
		
		// fetch the preference entry for the update interval
		this.pvUpdateInterval = Activator.getDefault().getPreferenceStore().
							getInt(PreferenceConstants.P_PV_UPDATE_INTERVAL);
		
		// get a pv instance from the factory
		this.pv = PVManager.readAndWrite(channel(pvname)).
							notifyOn(swtThread()).
							asynchWriteAndReadEvery(ms(pvUpdateInterval));
		
		// start listening to changes
		this.readListener = new ReadListener();
		this.pv.addPVReaderListener(this.readListener);
		this.writeListener = new WriteListener();
		this.pv.addPVWriterListener(writeListener);
		
		this.valueFormat = new SimpleValueFormat(1);
		
		this.propertyChangeSupport = new PropertyChangeSupport(this);
	}
	
	/**
	 * Disconnects the process variable.
	 */
	public void disconnect() {
		this.pv2.removeListener(pvListener);
		this.pv2.stop();
		this.pv.removePVReaderListener(this.readListener);
		this.pv.close();
		this.isConnected = false;
	}
	
	/**
	 * Returns the name (id) of the process variable.
	 * 
	 * @return the name (id) of the process variable
	 */
	public String getName() {
		return this.pvName;
	}
	
	/**
	 * Returns the value of the process variable.
	 * 
	 * @return the value of the process variable
	 */
	public String getValue() {
		return this.pvValue;
	}
	
	/**
	 * Returns the status (severity) of the process variable.
	 * Possible values are defined in 
	 * {@link org.epics.pvmanager.data.AlarmSeverity}.
	 * 
	 * @return the status (severity) of the process variable
	 * @see {@link org.epics.pvmanager.data.AlarmSeverity}
	 */
	public AlarmSeverity getStatus() {
		return this.pvStatus;
	}
	
	/**
	 * Checks whether the process variable is discrete.
	 * 
	 * @return <code>true</code> if the process variable is discrete, 
	 * 			<code>false</code> otherwise
	 */
	public boolean isDiscrete() {
		return this.isDiscrete;
	}
	
	/**
	 * Returns the discrete values of the process variable (or an empty list, 
	 * if {@link #isDiscrete()} <code> == false</code>.
	 * 
	 * @return the discrete values of the process variable
	 */
	public String[] getDiscreteValues() {
		return this.discreteValues.toArray(new String[0]);
	}
	
	/**
	 * Sets a new value for the process variable.
	 * 
	 * @param newVal the value that should be set
	 */
	public void setValue(Object newVal) {
		this.pv.write(newVal);
		if(logger.isDebugEnabled()) {
			logger.debug("Wrote " + this.getName() + ": " + newVal);
		}
	}
	
	/**
	 * Checks whether the process variable is only readable.
	 * 
	 * @return <code>true</code> if the process variable is readonly, 
	 * 			<code>false</code> otherwise
	 */
	public boolean isReadOnly() {
		return this.isReadOnly;
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
	 * Register to observe a certain property.
	 * 
	 * @param propertyName the property of interest
	 * @param listener the {@link java.beans.PropertyChangeListener} that 
	 * 					should receive the notification
	 * @see {@link java.beans.PropertyChangeSupport#addPropertyChangeListener(String, PropertyChangeListener)}
	 */
	public void addPropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}

	/**
	 * Removes the listener (regardless of the property).
	 * 
	 * @param listener the listener that should be removed.
	 * @see {@link java.beans.PropertyChangeSupport#removePropertyChangeListener(PropertyChangeListener)}
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}
	
	/* ********************************************************************* */
	
	/**
	 * <code>ReadListener</code> is the 
	 * {@link org.epics.pvmanager.PVReaderListener} listening to changes on the 
	 * wrapped process variable.
	 * 
	 * @author Marcus Michalsky
	 * @since 1.1
	 */
	private class ReadListener implements PVReaderListener {
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void pvChanged() {
			isConnected = true;
			
			Object newVal = pv.getValue();
			
			propertyChangeSupport.firePropertyChange("value", pvValue,
					pvValue = valueFormat.format(newVal));
			propertyChangeSupport.firePropertyChange("status", pvStatus, 
					pvStatus = ((Alarm)ValueUtil.alarmOf(pv.getValue())).getAlarmSeverity());
			Exception e = pv.lastException();
			if(e != null) {
				logger.warn(e.getMessage(), e);
			}
			if(logger.isDebugEnabled()) {
				logger.debug("new value for '" + getName() + "' : " + 
							valueFormat.format(newVal) + 
							" (" + ValueUtil.timeOf(
							newVal).getTimeStamp().asDate().toString() 
							+ ")");
			}
			
			if(newVal instanceof VEnum && !isEnumInitialized) {
				isDiscrete = true;
				isEnumInitialized = true;
				discreteValues = ((VEnum)newVal).getLabels();
			}
		}
	}
	
	/**
	 * <code>WriteListener</code> is the 
	 * {@link org.epics.pvmanager.PVWriterListener} listening to writes on the 
	 * wrapped process variable. It sets the process variable read only if a 
	 * write failed.
	 * XXX Failsafe for write status. Still necessary ?
	 * 
	 * @author Marcus Michalsky
	 * @since 1.1
	 */
	private class WriteListener implements PVWriterListener {
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void pvWritten() {
			Exception lastException = pv.lastWriteException();
			if (lastException instanceof WriteFailException) {
				logger.warn("Write to PV failed", lastException);
				isReadOnly = true;
			}
		}
	}
	
	/**
	 * Part of the workaround.
	 * 
	 * @author Marcus Michalsky
	 * @since 1.1
	 */
	private class PVListener implements org.csstudio.utility.pv.PVListener {
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void pvValueUpdate(PV pv) {
			isReadOnly = !pv.isWriteAllowed();
			logger.debug("got read only status (" + 
					!pv.isWriteAllowed() + ") of " + 
					pv.getName());
			pv.removeListener(this);
			pv.stop();
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void pvDisconnected(PV pv) {
		}
	}
}