package de.ptb.epics.eve.viewer;

import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

public class DeviceOptionsLabelProvider implements ITableLabelProvider, ITableColorProvider {

	public Image getColumnImage( final Object element, final int columnIndex) {
		return null;
	}

	public String getColumnText( final Object element, int columnIndex ) {
		final OptionConnector optionConnector = (OptionConnector)element;
		if( columnIndex == 0 ) {
			return optionConnector.getOption().getName();
		}
		return optionConnector.getValue();
	}

	public void addListener( final ILabelProviderListener listener ) {
		// TODO Auto-generated method stub

	}

	public void dispose() {
		// TODO Auto-generated method stub

	}

	public boolean isLabelProperty( final Object element, final String property) {
		// TODO Auto-generated method stub
		return false;
	}

	public void removeListener( final ILabelProviderListener listener ) {
		// TODO Auto-generated method stub

	}

	@Override
	public Color getBackground(Object element, int columnIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Color getForeground(Object element, int columnIndex) {
		if( columnIndex == 0 ) {
			return null;
		}
		final OptionConnector optionConnector = (OptionConnector)element;
		if( optionConnector.isConnected())
			return Activator.getDefault().getColor("COLOR_PV_OK");
		else
			return Activator.getDefault().getColor("COLOR_PV_ALARM");
	}

}
