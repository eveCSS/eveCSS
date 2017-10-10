package de.ptb.epics.eve.viewer.views.deviceoptionsview;

import org.eclipse.jface.viewers.ColumnLabelProvider;

/**
 * <code>OptionColumnLabelProvider</code> is the 
 * {@link org.eclipse.jface.viewers.ColumnLabelProvider} of the option column 
 * of the table viewer defined in 
 * {@link de.ptb.epics.eve.viewer.views.deviceoptionsview.DeviceOptionsView}.
 * 
 * @author Marcus Michalsky
 * @since 1.1
 */
public class OptionColumnLabelProvider extends ColumnLabelProvider {
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getText(Object element) {
		return ((OptionPV)element).getOption().getName();
	}
}