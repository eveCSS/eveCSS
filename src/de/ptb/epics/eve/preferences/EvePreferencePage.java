package de.ptb.epics.eve.preferences;

import org.eclipse.jface.preference.*;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbench;

public class EvePreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public EvePreferencePage() {
		super( GRID );
		this.setPreferenceStore( Activator.getDefault().getPreferenceStore() );
		this.setDescription( "Configuration for Eve" );
	}
	
	public void createFieldEditors() {
		addField( new FileFieldEditor( PreferenceConstants.P_DEFAULT_MEASURING_STATION_DESCRIPTION, "Measuring Station description:", this.getFieldEditorParent() ) );
	}

	public void init( final IWorkbench workbench ) {
	}
	
}