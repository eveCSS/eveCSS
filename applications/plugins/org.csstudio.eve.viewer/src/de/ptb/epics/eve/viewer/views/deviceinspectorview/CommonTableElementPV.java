package de.ptb.epics.eve.viewer.views.deviceinspectorview;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import de.ptb.epics.eve.viewer.pv.PVWrapper;

/**
 * <code>CommonTableElementPV</code> wraps a {@link } 
 * (process variable) and corresponds to a 
 * {@link de.ptb.epics.eve.viewer.views.deviceinspectorview.CommonTableElement} 
 * (i.e. the row it is contained in).
 * 
 * @author Marcus Michalsky
 * @since 
 */
public class CommonTableElementPV extends PVWrapper implements PropertyChangeListener {

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
	
	public void setReadOnly(boolean foo) {
		// no way TODO remove ?
	}
	
	public void setDiscreteValues(String[] bar) {
		// i don't think so TODO remove ?
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