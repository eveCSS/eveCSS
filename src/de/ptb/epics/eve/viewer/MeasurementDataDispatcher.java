package de.ptb.epics.eve.viewer;

import java.util.List;
import java.util.Map;

import org.eclipse.ui.IViewReference;
import org.eclipse.ui.PlatformUI;

import de.ptb.epics.eve.ecp1.client.interfaces.IMeasurementDataListener;
import de.ptb.epics.eve.ecp1.client.model.MeasurementData;

public class MeasurementDataDispatcher implements IMeasurementDataListener {
	
	public MeasurementDataDispatcher() {
		
	}
	
	public void measurementDataTransmitted( final MeasurementData measurementData ) {
		
		System.err.println( measurementData.getName() + " with the Value: " );
		final List< ? > data = measurementData.getValues();
		for( int i = 0; i < data.size(); ++i ) {
			System.err.println( "" + i + " = " + data.get( i ).toString() );
		}
		
		Activator.getDefault().getWorkbench().getDisplay().syncExec( new Runnable() {

			public void run() {
				
				IViewReference[] ref = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPartService().getActivePart().getSite().getPage().getViewReferences();
				for( int i = 0; i < ref.length; ++i ) {
					if( ref[i].getId().equals( PlotView.ID ) ) {
						PlotView view = (PlotView)ref[i].getPart( false );
						view.measurementData( measurementData );
					}
				}

			}
		});
		
	}

}
