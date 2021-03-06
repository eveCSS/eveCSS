package de.ptb.epics.eve.viewer.views.deviceinspectorview;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.swt.widgets.Display;

import de.ptb.epics.eve.preferences.Activator;
import de.ptb.epics.eve.preferences.PreferenceConstants;
import de.ptb.epics.eve.util.pv.PVWrapper;

/**
 * <code>CommonTableElementPV</code> wraps a {@link org.epics.pvmanager.PV} 
 * (process variable) and corresponds to a 
 * {@link de.ptb.epics.eve.viewer.views.deviceinspectorview.CommonTableElement} 
 * (i.e. the row it is contained in).
 * 
 * @author Marcus Michalsky
 * @since 1.1
 */
public class CommonTableElementPV extends PVWrapper implements PropertyChangeListener {

	// the table element (row) it belongs to
	private CommonTableElement commonTableElement;
	
	/**
	 * Constructs a <code>CommonTableElementPV</code>.
	 * 
	 * @param pvname the name of the process variable
	 * @param tableElement the 
	 * {@link de.ptb.epics.eve.viewer.views.deviceinspectorview.CommonTableElement}
	 * 		(row) the process variable corresponds to
	 */
	public CommonTableElementPV(String pvname, CommonTableElement tableElement) {
		super(pvname, Activator.getDefault().getPreferenceStore().
				getInt(PreferenceConstants.P_PV_UPDATE_INTERVAL));
		this.commonTableElement = tableElement;
		
		this.addPropertyChangeListener(PVWrapper.VALUE, this);
		this.addPropertyChangeListener(PVWrapper.CONNECTION_STATUS, this);
	}
	
	/**
	 * Constructs a <code>CommonTableElementPV</code>.
	 * 
	 * @param pvname the name of the process variable
	 * @param triggerPv the name of the associated trigger process variable
	 * @param tableElement the 
	 * {@link de.ptb.epics.eve.viewer.views.deviceinspectorview.CommonTableElement}
	 * 		(row) the process variable corresponds to
	 */
	public CommonTableElementPV(String pvname, String triggerPv, CommonTableElement tableElement) {
		super(pvname, Activator.getDefault().getPreferenceStore().
				getInt(PreferenceConstants.P_PV_UPDATE_INTERVAL), triggerPv);
		this.commonTableElement = tableElement;
		
		this.addPropertyChangeListener(PVWrapper.VALUE, this);
		this.addPropertyChangeListener(PVWrapper.CONNECTION_STATUS, this);
		this.addPropertyChangeListener(PVWrapper.SEVERITY, this);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if(evt.getPropertyName().equals(PVWrapper.VALUE)) {
			commonTableElement.update();
		} else if (evt.getPropertyName().equals(PVWrapper.CONNECTION_STATUS)) {
			commonTableElement.update();
		} else if (evt.getPropertyName().equals(PVWrapper.SEVERITY)) {
			commonTableElement.update();
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void disconnect() {
		this.removePropertyChangeListener(PVWrapper.VALUE, this);
		this.removePropertyChangeListener(PVWrapper.CONNECTION_STATUS, this);
		this.removePropertyChangeListener(PVWrapper.SEVERITY, this);
		super.disconnect();
	}
}