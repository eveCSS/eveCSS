package de.ptb.epics.eve.viewer;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.ui.IViewReference;
import org.eclipse.ui.PlatformUI;
import org.xml.sax.SAXException;


import de.ptb.epics.eve.data.measuringstation.MeasuringStation;
import de.ptb.epics.eve.data.measuringstation.processors.MeasuringStationLoader;
import de.ptb.epics.eve.ecp1.client.interfaces.INewXMLFileListener;

public class XMLFileDispatcher implements INewXMLFileListener {

	public void newXMLFileReceived( final byte[] xmlData ) {
		try {
			
			
			final MeasuringStation measuringStation = MeasuringStationLoader.loadFromByteArray( xmlData );
			Activator.getDefault().getWorkbench().getDisplay().syncExec( new Runnable() {

				public void run() {
					IViewReference[] ref = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPartService().getActivePart().getSite().getPage().getViewReferences();
					MeasuringStationView view = null;
					for( int i = 0; i < ref.length; ++i ) {
						if( ref[i].getId().equals( MeasuringStationView.ID ) ) {
							view = (MeasuringStationView)ref[i].getPart( false );
						}
					}
					view.setMeasuringStation( measuringStation );
					
				}} );
			
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
