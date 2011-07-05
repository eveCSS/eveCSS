package de.ptb.epics.eve.viewer.views.deviceinspectorview;

import java.util.Locale;

import org.apache.log4j.Logger;
import org.csstudio.platform.data.IEnumeratedMetaData;
import org.csstudio.platform.data.IMetaData;
import org.csstudio.platform.data.ValueUtil;
import org.csstudio.utility.pv.PV;
import org.csstudio.utility.pv.PVFactory;
import org.csstudio.utility.pv.PVListener;

/**
 * <code>CommonTableElementPV</code> wraps a {@link org.csstudio.utility.pv.PV} 
 * (process variable) and corresponds to a 
 * {@link de.ptb.epics.eve.viewer.views.deviceinspectorview.CommonTableElement} 
 * (i.e. the row it is contained in).
 * 
 * @author Jens Eden
 * @author Marcus Michalsky
 */
public class CommonTableElementPV implements PVListener {

	private static Logger logger = 
			Logger.getLogger(CommonTableElementPV.class.getName());
	
	private String value;
	private PV pv;
	private boolean isConnected = false;
	
	private boolean isReadOnly = false;
	private boolean extraReadOnly = false;
	private String[] discreteValues = null;
	private boolean isEnum = false;
	//private String pvname;
	private CommonTableElement tableElement;
	private String pvstatus = "";
	private boolean hasDiscreteValues = false;
	
	/**
	 * Constructs a <code>CommonTableElementPV</code>.
	 * 
	 * @param pvname the name of the process variable
	 * @param tableElement the 
	 * {@link de.ptb.epics.eve.viewer.views.deviceinspectorview.CommonTableElement}
	 * 		(row) the process variable corresponds to
	 */
	public CommonTableElementPV(String pvname, CommonTableElement tableElement) {
		this.tableElement = tableElement;
		//this.pvname = pvname;
		try {
			pv = PVFactory.createPV("ca://" + pvname);
			pv.addListener(this);
			pv.start();
			this.value = pv.getStateInfo();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void pvDisconnected(PV pv) {
		isConnected = false;
		value = pv.getStateInfo();
		tableElement.update();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void pvValueUpdate(PV pv) {
		if(pv.isConnected()) isConnected = true;
		if(!pv.isWriteAllowed()) isReadOnly = true;
		pvstatus  = pv.getValue().getSeverity().toString();
		this.value = ValueUtil.getString(pv.getValue());
		try {
			Double.parseDouble(this.value);
			this.value = String.format(
					Locale.US, "%g", ValueUtil.getDouble(pv.getValue()));
		} catch(final Exception e) {
			// pv value is not a double
		}
        final IMetaData meta = pv.getValue().getMetaData();
        // enum values override discreteValues
        if (meta instanceof IEnumeratedMetaData) {
        	this.discreteValues = ((IEnumeratedMetaData)meta).getStates();
        	isEnum  = true;
        }
        tableElement.update();
	}

	/**
	 * Checks whether the process variable is discrete.
	 * 
	 * @return <code>true</code> if the process variable is discrete, 
	 * 			<code>false</code> otherwise
	 */
	boolean isDiscrete() {
		return (isEnum || hasDiscreteValues);
	}
	
	/**
	 * Checks whether the process variable is read only.
	 * 
	 * @return <code>true</code> if the process variable is read only, 
	 * 			<code>false</code> otherwise
	 */
	public boolean isReadOnly() {
		return isReadOnly | extraReadOnly;
	}
	
	/**
	 * Returns the discrete values of the process variable or <code>null</code> 
	 * if {@link #isDiscrete()} == <code>false</code>.
	 * 
	 * @return the discrete values of the process variable or <code>null</code> 
	 * 			if {@link #isDiscrete()} == <code>false</code>
	 */
	public String[] getDiscreteValues() {
		return discreteValues;
	}

	/**
	 * 
	 * @param values
	 */
	public void setDiscreteValues(String[] values) {
		discreteValues = values;
		hasDiscreteValues = true;
	}

	/**
	 *  Dispose-like method. Removes all references 
	 * (e.g. listeners) from it.
	 */
	public void dispose() {
        if (pv != null) {
            pv.removeListener(this);
            pv.stop();
            pv = null;
        }
	}

	/**
	 * Returns the value of the process variable.
	 * 
	 * @return the value of the process variable
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Returns the status of the process variable.
	 * 
	 * @return the status of the process variable
	 */
	public String getStatus() {
		return pvstatus;
	}
	
	/**
	 * Checks whether the process variable is connected.
	 * 
	 * @return <code>true</code> if the process variable is connected, 
	 * 			<code>false</code> otherwise
	 */
	public boolean isConnected() {
		return isConnected;
	}

	/**
	 * Sets the value of the process variable.
	 * 
	 * @precondition {@link #isReadOnly()} == <code>false</code>
	 * @param newValue the value that should be set
	 */
	public void setValue(Object newValue) {
		if (isReadOnly) return;
		try {
			pv.setValue(newValue);
		} catch (Exception ex) {
			logger.error("Unable to set " + pv.getName() + " to " + value, ex);
		}
	}

	/**
	 * 
	 * @param readOnly
	 */
	public void setReadOnly(boolean readOnly) {
		extraReadOnly = readOnly;
	}
}