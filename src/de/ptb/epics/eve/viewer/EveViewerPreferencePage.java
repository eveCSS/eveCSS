package de.ptb.epics.eve.viewer;

import org.eclipse.jface.preference.*;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbench;
import de.ptb.epics.eve.viewer.Activator;

public class EveViewerPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public EveViewerPreferencePage() {
		super( GRID );
		this.setPreferenceStore(Activator.getDefault().getPreferenceStore());
		this.setDescription("Configuration for Eve Viewer");
	}
	
	public void createFieldEditors() {
		addField( new FileFieldEditor( PreferenceConstants.P_DEFAULT_MEASURING_STATION_DESCRIPTION, "device definition file:", this.getFieldEditorParent() ) );
	}

	public void init( final IWorkbench workbench ) {
	}
	
}