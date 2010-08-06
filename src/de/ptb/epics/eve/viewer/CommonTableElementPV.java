/**
 * 
 */
package de.ptb.epics.eve.viewer;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

import org.csstudio.platform.data.IEnumeratedMetaData;
import org.csstudio.platform.data.IMetaData;
import org.csstudio.platform.data.ValueUtil;
import org.csstudio.utility.pv.PV;
import org.csstudio.utility.pv.PVFactory;
import org.csstudio.utility.pv.PVListener;

import de.ptb.epics.eve.data.TransportTypes;
import de.ptb.epics.eve.data.measuringstation.Option;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener;

/**
 * @author eden
 *
 */
public class CommonTableElementPV implements PVListener {

	private String value;
	private PV pv;
	private boolean isConnected = false;
	
	private boolean isReadOnly = false;
	private boolean extraReadOnly = false;
	private String[] discreteValues = null;
	private boolean isEnum = false;
	private String pvname;
	private CommonTableElement tableElement;
	private String pvstatus = "";
	private boolean hasDiscreteValues = false;
	
	public CommonTableElementPV( String pvname, CommonTableElement tableElement ) {

		this.tableElement = tableElement;
		this.pvname = pvname;
		try {
			pv = PVFactory.createPV("ca://" + pvname);
			pv.addListener(this);
			pv.start();
			this.value = pv.getStateInfo();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}				
	}

	/* (non-Javadoc)
	 * @see org.csstudio.utility.pv.PVListener#pvDisconnected(org.csstudio.utility.pv.PV)
	 */
	@Override
	public void pvDisconnected(PV pv) {
		isConnected = false;
		value = pv.getStateInfo();
		tableElement.update();
	}

	/* (non-Javadoc)
	 * @see org.csstudio.utility.pv.PVListener#pvValueUpdate(org.csstudio.utility.pv.PV)
	 */
	@Override
	public void pvValueUpdate(PV pv) {
		
		if(pv.isConnected()) isConnected = true;
		if(!pv.isWriteAllowed()) isReadOnly = true;
		pvstatus  = pv.getValue().getSeverity().toString();
		this.value = ValueUtil.getString(pv.getValue());
		try {
			Double.parseDouble( this.value );
			this.value = String.format( Locale.US, "%g", ValueUtil.getDouble( pv.getValue() ) );
		} catch( final Exception e ) {
			
		}
        final IMetaData meta = pv.getValue().getMetaData();
        // enum values override discreteValues
        if (meta instanceof IEnumeratedMetaData) {
        	this.discreteValues = ((IEnumeratedMetaData)meta).getStates();
        	isEnum  = true;
        }
        tableElement.update();
	}

	boolean isDiscrete(){
		return (isEnum || hasDiscreteValues);
	}
	public boolean isReadOnly(){
		return isReadOnly | extraReadOnly;
	}
	
	public String[] getDiscreteValues(){
		return discreteValues;
	}

	public void setDiscreteValues(String[] values){
		discreteValues = values;
		hasDiscreteValues = true;
	}

	public void dispose() {
        if (pv != null) {
            pv.removeListener(this);
            pv.stop();
            pv = null;
        }
	}

	public String getValue() {
		return value;
	}

	public String getStatus() {
		return pvstatus;
	}
	public boolean isConnected() {
		return isConnected;
	}

	public void setValue(Object newValue) {
		if (isReadOnly) return;
		try {
			pv.setValue(newValue);
		} catch (Exception ex) {
			// TODO Auto-generated catch block
        	System.err.println("Unable to set "+pv.getName()+" to "+value );
            ex.printStackTrace();
		}	
	}

	public void setReadOnly(boolean readOnly) {
		extraReadOnly = readOnly;
	}

}
