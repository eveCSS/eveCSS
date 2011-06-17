package de.ptb.epics.eve.viewer.views.deviceoptionsview;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

import de.ptb.epics.eve.viewer.Activator;

/**
 * <code>DeviceOptionsLabelProvider</code> is the label and color provider of 
 * the table viewer defined in 
 * {@link de.ptb.epics.eve.viewer.views.deviceoptionsview.DeviceOptionsView}.
 * 
 * @author ?
 * @author Marcus Michalsky
 */
public class DeviceOptionsLabelProvider implements ITableLabelProvider, ITableColorProvider {

	/**
	 * {@inheritDoc}
	 */
	public Image getColumnImage(final Object element, final int columnIndex) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getColumnText(final Object element, int columnIndex) {
		final OptionConnector optionConnector = (OptionConnector)element;
		if(columnIndex == 0) {
			return optionConnector.getOption().getName();
		}
		return optionConnector.getValue();
	}

	/**
	 * {@inheritDoc}
	 */
	public void dispose() {
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isLabelProperty(final Object element, final String property) {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public void addListener(final ILabelProviderListener listener) {
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void removeListener(final ILabelProviderListener listener) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Color getBackground(Object element, int columnIndex) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Color getForeground(Object element, int columnIndex) {
		if(columnIndex == 0) {
			return null;
		}
		final OptionConnector optionConnector = (OptionConnector)element;
		if( optionConnector.isConnected())
			return Activator.getDefault().getColor("COLOR_PV_OK");
		else
			return Activator.getDefault().getColor("COLOR_PV_ALARM");
	}
}