package de.ptb.epics.eve.viewer.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import de.ptb.epics.eve.viewer.Activator;

/**
 * 
 * 
 * @author Marcus Michalsky
 *
 */
public class PreferencePage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {

	/**
	 * 
	 */
	public PreferencePage() {
		super(GRID);
		this.setPreferenceStore(Activator.getDefault().getPreferenceStore());
		// this.setDescription("");
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createFieldEditors() {
		addField(new StringFieldEditor(
				PreferenceConstants.P_DEFAULT_ENGINE_ADDRESS, 
				"Engine (name:port):", 
				this.getFieldEditorParent()));
		FileFieldEditor engineFileFieldEditor = new FileFieldEditor(
				PreferenceConstants.P_DEFAULT_ENGINE_LOCATION, 
				"Engine location:", this.getFieldEditorParent());
		addField(engineFileFieldEditor);
		
		IntegerFieldEditor pvIntegerFieldEditor = new IntegerFieldEditor(
				PreferenceConstants.P_PV_UPDATE_INTERVAL, 
				"PV Update Interval (in ms):", 
				this.getFieldEditorParent()); 
		pvIntegerFieldEditor.setValidRange(250, 60000);
		addField(pvIntegerFieldEditor);
		IntegerFieldEditor plotIntegerFieldEditor = new IntegerFieldEditor(
				PreferenceConstants.P_PLOT_BUFFER_SIZE, 
				"Plot Buffer Size (in # points):", 
				this.getFieldEditorParent());
		plotIntegerFieldEditor.setValidRange(10, 2000);
		addField(plotIntegerFieldEditor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(IWorkbench workbench) {
	}
}