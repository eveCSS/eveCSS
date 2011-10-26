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
	 * Sets whether the process variable should be readonly.
	 * 
	 * @param readonly <code>true</code> if the process variable should be 
	 * 					readonly, <code>false</code> otherwise
	 */
	protected void setReadOnly(boolean readonly) {
		isReadOnly = readonly;
	}
	
	public void setDiscreteValues(String[] foo) {
		// TODO remove ?
		// left only to sustain the interface and not break the code
		// possible discrete values should be read from the device itself via 
		// channel access.
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