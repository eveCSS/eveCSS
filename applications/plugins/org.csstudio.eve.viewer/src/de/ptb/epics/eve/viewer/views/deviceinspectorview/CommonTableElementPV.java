package de.ptb.epics.eve.viewer.views.deviceinspectorview;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import de.ptb.epics.eve.viewer.pv.PVWrapper;

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
		super(pvname);
		this.commonTableElement = tableElement;
		
		this.addPropertyChangeListener("value", this);
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
		super(pvname, triggerPv);
		this.commonTableElement = tableElement;
		
		this.addPropertyChangeListener("value", this);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if(evt.getPropertyName().equals("value")) {
			commonTableElement.update();
		}
	}
}