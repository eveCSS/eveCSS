package de.ptb.epics.eve.editor.preferences;

import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import de.ptb.epics.eve.editor.Activator;

/**
 * @author Marcus Michalsky
 * @since 1.20
 */
public class PreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	public PreferencePage() {
		super(GRID);
		this.setPreferenceStore(Activator.getDefault().getPreferenceStore());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(IWorkbench workbench) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createFieldEditors() {
		ComboFieldEditor csvDelimiter = new ComboFieldEditor(
				PreferenceConstants.P_CSV_DELIMITER, 
				"CSV Delimiter:", 
				new String [][] {{"Comma", ","},{"Semicolon", ";"}}, 
				this.getFieldEditorParent());
		addField(csvDelimiter);
		
		StringFieldEditor topUpPV = new StringFieldEditor(
				PreferenceConstants.P_TOPUP_PV, 
				"TopUP PV name:", 
				this.getFieldEditorParent());
		addField(topUpPV);
	}
}