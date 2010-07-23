package de.ptb.epics.eve.viewer;

import java.util.List;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import de.ptb.epics.eve.data.measuringstation.AbstractDevice;
import de.ptb.epics.eve.data.measuringstation.Detector;
import de.ptb.epics.eve.data.measuringstation.DetectorChannel;
import de.ptb.epics.eve.data.measuringstation.Device;
import de.ptb.epics.eve.data.measuringstation.MeasuringStation;
import de.ptb.epics.eve.data.measuringstation.Motor;
import de.ptb.epics.eve.data.measuringstation.MotorAxis;




public class MeasuringStationTreeViewLabelProvider implements ILabelProvider {

	final Image motorImage = Activator.getDefault().getImageRegistry().get("MOTOR");
	final Image axisImage = Activator.getDefault().getImageRegistry().get("AXIS");
	final Image detectorImage = Activator.getDefault().getImageRegistry().get("DETECTOR");
	final Image channelImage = Activator.getDefault().getImageRegistry().get("CHANNEL");
	
	public Image getImage( final Object element ) {
		if( element instanceof Motor ) {
			return this.motorImage;
		} else if( element instanceof MotorAxis ) {
			return this.axisImage;
		} else if( element instanceof Detector ) {
			return this.detectorImage;
		} else if( element instanceof DetectorChannel ) {
			return this.channelImage;
		} else if( element instanceof Device ) {
			return PlatformUI.getWorkbench().getSharedImages().getImageDescriptor( ISharedImages.IMG_ELCL_SYNCED ).createImage();
		}
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
