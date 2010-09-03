package de.ptb.epics.eve.viewer.actions;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.xml.sax.SAXException;

import de.ptb.epics.eve.data.measuringstation.IMeasuringStation;
import de.ptb.epics.eve.data.measuringstation.processors.MeasuringStationLoader;
import de.ptb.epics.eve.preferences.PreferenceConstants;
import de.ptb.epics.eve.viewer.MeasuringStationView;

public class LoadMeasuringStationAction extends Action implements IWorkbenchAction {

	private static final String ID = "de.ptb.epics.eve.viewer.actions.LoadMeasuringStationAction";
	
	final MeasuringStationView measuringStationView;
	
	public LoadMeasuringStationAction( final MeasuringStationView measuringStationView ) {  
		this.setId( LoadMeasuringStationAction.ID );
		this.measuringStationView = measuringStationView;
	} 
	
	public void run() {  
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		String name = new FileDialog( shell, SWT.OPEN ).open();
		
		if( name == null )
		      return;
		
		final File file = new File( name );
		
		final String measuringStationDescription = de.ptb.epics.eve.preferences.Activator.getDefault().getPreferenceStore().getString( PreferenceConstants.P_DEFAULT_MEASURING_STATION_DESCRIPTION );
		final int lastSeperatorIndex = measuringStationDescription.lastIndexOf( File.separatorChar );
		final String schemaFileLocation = measuringStationDescription.substring( 0, lastSeperatorIndex + 1 ) + "scml.xsd";
		final File schemaFile = new File( schemaFileLocation );
		
		
		final MeasuringStationLoader measuringStationLoader = new MeasuringStationLoader( schemaFile );
		try {
			measuringStationLoader.load( file );
			final IMeasuringStation measuringStation = measuringStationLoader.getMeasuringStation();
			this.measuringStationView.setMeasuringStation( measuringStation );
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
	
	public void dispose() {
		// TODO Auto-generated method stub

	}

}
