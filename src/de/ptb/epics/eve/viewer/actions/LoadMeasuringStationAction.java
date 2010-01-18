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

import de.ptb.epics.eve.data.measuringstation.MeasuringStation;
import de.ptb.epics.eve.data.measuringstation.processors.MeasuringStationLoader;
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
		
		final MeasuringStationLoader measuringStationLoader = new MeasuringStationLoader();
		try {
			measuringStationLoader.load( file );
			final MeasuringStation measuringStation = measuringStationLoader.getMeasuringStation();
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
