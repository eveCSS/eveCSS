package de.ptb.epics.eve.viewer;

import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.model.pvs.ProcessVariableAdressFactory;
import org.csstudio.platform.simpledal.ConnectionException;
import org.csstudio.platform.simpledal.IProcessVariableConnectionService;
import org.csstudio.platform.simpledal.ProcessVariableConnectionServiceFactory;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import de.ptb.epics.eve.data.TransportTypes;
import de.ptb.epics.eve.data.measuringstation.Option;

public class DeviceOptionsLabelProvider implements ITableLabelProvider {

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

}
