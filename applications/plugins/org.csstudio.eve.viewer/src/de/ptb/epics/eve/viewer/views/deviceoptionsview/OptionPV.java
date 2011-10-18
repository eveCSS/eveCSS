package de.ptb.epics.eve.viewer.views.deviceoptionsview;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;

import org.apache.log4j.Logger;
import org.csstudio.csdata.ProcessVariable;
import org.epics.pvmanager.PV;
import org.epics.pvmanager.PVManager;
import org.epics.pvmanager.PVReaderListener;
import org.epics.pvmanager.data.SimpleValueFormat;
import org.epics.pvmanager.data.ValueFormat;
import org.epics.pvmanager.data.ValueUtil;
import org.epics.pvmanager.data.VEnum;

import de.ptb.epics.eve.data.measuringstation.Option;
import de.ptb.epics.eve.viewer.Activator;
import de.ptb.epics.eve.viewer.preferences.PreferenceConstants;

import static org.epics.pvmanager.ExpressionLanguage.*;
import static org.epics.pvmanager.util.TimeDuration.*;
import static org.csstudio.utility.pvmanager.ui.SWTUtil.*;

/**
 * <code>OptionPV</code>.
 * 
 * @author Marcus Michalsky
 * @since 1.1
 */
public class OptionPV extends ProcessVariable {

	private static Logger logger = Logger.getLogger(OptionPV.class.getName());
	
	private static final long serialVersionUID = 1L;

	private PV<Object,Object> pv;
	private final Option option;
	private String value;
	private boolean isReadonly = false;
	private boolean isDiscrete = false;
	private List<String> discreteValues = null;
	
	private ReadListener readListener;
	private PropertyChangeSupport propertyChangeSupport;
	private ValueFormat valueFormat;
	
	private int pvUpdateInterval;
	
	/**
	 * Constructs an <code>OptionPV</code>.
	 * 
	 * @param option the option the PV is related to
	 */
	public OptionPV(final Option option) {
		super(option.getValue().getAccess().getVariableID());
		this.option = option;
		this.value = "";
		
		valueFormat = new SimpleValueFormat(1);
		
		pvUpdateInterval = Activator.getDefault().getPreferenceStore().
				getInt(PreferenceConstants.P_PV_UPDATE_INTERVAL);
		
		pv = PVManager.readAndWrite(
				channel(this.getName())).notifyOn(swtThread()).
					asynchWriteAndReadEvery(ms(pvUpdateInterval));
		
		readListener = new ReadListener();
		pv.addPVReaderListener(readListener);
		
		if(logger.isDebugEnabled()) {
			logger.debug("OptionPV created for '" + 
						option.getValue().getAccess().getVariableID() + "'");
			logger.debug("Option discrete ? " + option.isDiscrete());
			logger.debug("Option Val discrete ? " + option.getValue().isDiscrete());
			logger.debug("Option readonly ? " + option.getValue().isReadOnly());
		}
		
		propertyChangeSupport = new PropertyChangeSupport(this);
	}
	
	/**
	 * Returns the {@link de.ptb.epics.eve.data.measuringstation.Option} wrapped 
	 * in the <code>OptionPV</code>.
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
	 * Checks whether the option is read only.
	 * 
	 * @return <code>true</code> if the option is read only, 
	 * 			<code>false</code> otherwise
	 */
	public boolean isReadOnly() {
		return this.isReadonly;
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
	 * Returns the discrete values of the process variable or <code>null</code> 
	 * if {@link #isDiscrete()} == <code>false</code>.
	 * 
	 * @return the discrete values of the process variable or <code>null</code> 
	 * if {@link #isDiscrete()} == <code>false</code>
	 */
	public String[] getDiscreteValues() {
		if(!this.isDiscrete) return null;
		return this.discreteValues.toArray(new String[0]);
	}
	
	/**
	 * Sets the value of the process variable corresponding to the option.
	 * 
	 * @param value the value that should be set
	 */
	public void setValue(String newValue) {
		pv.write(newValue);
		propertyChangeSupport.firePropertyChange("value", this.value,
				this.value = newValue);
		logger.debug("value set");
	}
	
	/**
	 * 
	 */
	public void disconnect() {
		pv.removePVReaderListener(readListener);
		pv.close();
		if(logger.isDebugEnabled()) {
			logger.debug("OptionPV '" + getName() + "' disconnected.");
		}
	}
	
	/**
	 * 
	 * @param propertyName
	 * @param listener
	 */
	public void addPropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}

	/**
	 * 
	 * @param listener
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}
	
	/**
	 * 
	 * @return
	 */
	protected PV<Object,Object> getPV() {
		return this.pv;
	}
	
	/* ******************************************************************* */
	/* ************************ Listener ********************************* */
	/* ******************************************************************* */
	
	/**
	 * <code>ReadListener</code>.
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
			propertyChangeSupport.firePropertyChange("value", value,
					value = valueFormat.format(pv.getValue()));
			Exception e = pv.lastException();
			if(e != null) {
				logger.warn(e.getMessage(), e);
			}
			if(logger.isDebugEnabled()) {
				logger.debug("new value for '" + getName() + "' : " + 
							valueFormat.format(pv.getValue()) + 
							" (" + ValueUtil.timeOf(
							pv.getValue()).getTimeStamp().asDate().toString() 
							+ ")");
			}
			
			if(pv.getValue() instanceof VEnum) {
				isDiscrete = true;
				discreteValues = ((VEnum)pv.getValue()).getLabels();
			}
		}
	}
}