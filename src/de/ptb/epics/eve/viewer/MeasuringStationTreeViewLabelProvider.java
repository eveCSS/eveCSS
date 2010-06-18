package de.ptb.epics.eve.viewer;

import java.util.List;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

import de.ptb.epics.eve.data.measuringstation.AbstractDevice;
import de.ptb.epics.eve.data.measuringstation.Detector;
import de.ptb.epics.eve.data.measuringstation.Device;
import de.ptb.epics.eve.data.measuringstation.MeasuringStation;
import de.ptb.epics.eve.data.measuringstation.Motor;




public class MeasuringStationTreeViewLabelProvider implements ILabelProvider {

	public Image getImage( final Object element ) {
		return null;
	}

	public String getText( final Object element ) {
		if( element instanceof MeasuringStation ) {
			return "Measuring Station";
		}
		else if( element instanceof List<?> ) {
			Object obj = ((List<Object>)element).get( 0 );
			if( obj instanceof Motor ) {
				return "Motors";
			}
			else if( obj instanceof Detector ) {
				return "Detectors";
			}
			else if( obj instanceof Device ) {
				return "Devices";
			}
		}
		else if (element instanceof String){
				return (String)element;
		}
		else if( element instanceof AbstractDevice ) {
			final AbstractDevice device = (AbstractDevice)element;
			String label = device.getName();
			if (label.length() == 0) label = device.getID();
			return label;
		}
		return null;
	}

	public void addListener( final ILabelProviderListener listener ) {
		// TODO Auto-generated method stub
		
	}

	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	public boolean isLabelProperty( final Object element, final String property ) {
		// TODO Auto-generated method stub
		return false;
	}

	public void removeListener( final ILabelProviderListener listener ) {
		// TODO Auto-generated method stub
		
	}



}
