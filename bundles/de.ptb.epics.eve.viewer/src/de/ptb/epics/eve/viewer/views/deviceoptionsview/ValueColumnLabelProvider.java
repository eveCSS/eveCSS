package de.ptb.epics.eve.viewer.views.deviceoptionsview;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Color;

import de.ptb.epics.eve.viewer.Activator;

/**
 * <code>ValueColumnLabelProvider</code> is the 
 * {@link org.eclipse.jface.viewers.ColumnLabelProvider} of the value column 
 * of the table viewer defined in 
 * {@link de.ptb.epics.eve.viewer.views.deviceoptionsview.DeviceOptionsView}.
 * 
 * @author Marcus Michalsky
 * @since 1.1
 */
public class ValueColumnLabelProvider extends ColumnLabelProvider {
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getText(Object element) {
		return ((OptionPV)element).getValue();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Color getForeground(Object element) {
		OptionPV opv = (OptionPV) element;
		if(!opv.isConnected()) {
			return Activator.getDefault().getColor("COLOR_PV_ALARM");
		} else {
			return Activator.getDefault().getColor("COLOR_PV_OK");
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getToolTipText(Object element) {
		return ((OptionPV)element).getName();
	}
}