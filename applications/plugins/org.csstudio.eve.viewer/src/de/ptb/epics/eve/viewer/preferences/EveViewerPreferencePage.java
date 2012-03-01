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

	/**
	 * 
	 */
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