package de.ptb.epics.eve.viewer.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import de.ptb.epics.eve.viewer.Activator;


/**
 * 
 * 
 * @author Marcus Michalsky
 *
 */
public class EveViewerPreferencePage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {

	public EveViewerPreferencePage() {
		super(GRID);
		this.setPreferenceStore(Activator.getDefault().getPreferenceStore());
		this.setDescription("Configuration for EVE Viewer");
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createFieldEditors() {
		IntegerFieldEditor integerFieldEditor = new IntegerFieldEditor(
				PreferenceConstants.P_PV_UPDATE_INTERVAL, 
				"PV Update Interval (in ms):", 
				this.getFieldEditorParent()); 
		integerFieldEditor.setValidRange(250, 60000);
		addField(integerFieldEditor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(IWorkbench workbench) {
	}
}