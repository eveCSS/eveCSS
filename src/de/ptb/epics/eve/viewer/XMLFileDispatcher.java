package de.ptb.epics.eve.viewer;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.ui.IViewReference;
import org.eclipse.ui.PlatformUI;
import org.xml.sax.SAXException;


import de.ptb.epics.eve.data.measuringstation.MeasuringStation;
import de.ptb.epics.eve.data.measuringstation.processors.MeasuringStationLoader;
import de.ptb.epics.eve.data.scandescription.ScanDescription;
import de.ptb.epics.eve.data.scandescription.processors.ScanDescriptionLoader;
import de.ptb.epics.eve.ecp1.client.interfaces.INewXMLFileListener;
import de.ptb.epics.eve.preferences.PreferenceConstants;

public class XMLFileDispatcher implements INewXMLFileListener {

	public void newXMLFileReceived( final byte[] xmlData ) {
		try {
			
			final String measuringStationDescription = de.ptb.epics.eve.preferences.Activator.getDefault().getPreferenceStore().getString( PreferenceConstants.P_DEFAULT_MEASURING_STATION_DESCRIPTION );
			final int lastSeperatorIndex = measuringStationDescription.lastIndexOf( File.separatorChar );
			final String schemaFileLocation = measuringStationDescription.substring( 0, lastSeperatorIndex + 1 ) + "scml.xsd";
			final File schemaFile = new File( schemaFileLocation );
			
			final MeasuringStationLoader measuringStationLoader = new MeasuringStationLoader( schemaFile );
			final MeasuringStation measuringStation = measuringStationLoader.loadFromByteArray( xmlData );
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
			
			final ScanDescriptionLoader scanDescriptionLoader = new ScanDescriptionLoader( measuringStation, schemaFile );
			scanDescriptionLoader.loadFromByteArray( xmlData );
			final ScanDescription scanDescription = scanDescriptionLoader.getScanDescription();
			Activator.getDefault().setCurrentScanDescription( scanDescription );
		} catch( final ParserConfigurationException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch( final SAXException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch( final IOException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
