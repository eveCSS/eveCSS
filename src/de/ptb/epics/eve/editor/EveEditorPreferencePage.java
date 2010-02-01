package de.ptb.epics.eve.editor;

import org.eclipse.jface.preference.*;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbench;
import de.ptb.epics.eve.editor.Activator;

public class EveEditorPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public EveEditorPreferencePage() {
		super( GRID );
		this.setPreferenceStore(Activator.getDefault().getPreferenceStore());
		this.setDescription("Configuration for Eve-Editor");
	}
	
	public void createFieldEditors() {
		addField( new FileFieldEditor( PreferenceConstants.P_DEFAULT_MEASURING_STATION_DESCRIPTION, "Measuring Station description:", this.getFieldEditorParent() ) );
	}

	public void init( final IWorkbench workbench ) {
	}
	
}