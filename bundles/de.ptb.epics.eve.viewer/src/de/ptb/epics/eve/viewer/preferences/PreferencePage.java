package de.ptb.epics.eve.viewer.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import de.ptb.epics.eve.viewer.Activator;

/**
 * @author Marcus Michalsky
 */
public class PreferencePage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {

	/**
	 * 
	 */
	public PreferencePage() {
		super(GRID);
		this.setPreferenceStore(Activator.getDefault().getPreferenceStore());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createFieldEditors() {
		StringFieldEditor engineLocationFieldEditor = new StringFieldEditor(
				PreferenceConstants.P_DEFAULT_ENGINE_ADDRESS,
				"Engine host:", this.getFieldEditorParent());
		engineLocationFieldEditor.getTextControl(getFieldEditorParent()).
				setToolTipText("the host eveCSS should connect to");
		engineLocationFieldEditor.setEmptyStringAllowed(false);
		addField(engineLocationFieldEditor);
		
		IntegerFieldEditor enginePortFieldEditor = new IntegerFieldEditor(
				PreferenceConstants.P_DEFAULT_ENGINE_PORT, "Engine Port:",
				this.getFieldEditorParent(), 5);
		enginePortFieldEditor.setValidRange(1, 65535);
		enginePortFieldEditor.getTextControl(getFieldEditorParent()).
				setToolTipText("Note that using ports outside of [49152,65535] can cause conflicts due to reserved or system ports\n\n" + 
					"In more Depth: http://www.iana.org/assignments/service-names-port-numbers/service-names-port-numbers.xml");
		enginePortFieldEditor.setEmptyStringAllowed(false);
		addField(enginePortFieldEditor);
		
		FileFieldEditor engineFileFieldEditor = new FileFieldEditor(
				PreferenceConstants.P_DEFAULT_ENGINE_LOCATION, 
				"Engine executable:", this.getFieldEditorParent());
		engineFileFieldEditor.getTextControl(getFieldEditorParent()).
				setToolTipText("location of the engine executable");
		engineFileFieldEditor.setEmptyStringAllowed(false);
		addField(engineFileFieldEditor);
		
		StringFieldEditor engineParametersFieldEditor = new StringFieldEditor(
				PreferenceConstants.P_DEFAULT_ENGINE_PARAMETERS,
				"Engine Parameters:", getFieldEditorParent());
		engineParametersFieldEditor.getTextControl(getFieldEditorParent()).
			setToolTipText("The following macros are available:\n" +
					"${VERSION}  : the current eveCSS version number\n" +
					"${LOCATION} : the name of the test site");
		addField(engineParametersFieldEditor);
		
		IntegerFieldEditor plotIntegerFieldEditor = new IntegerFieldEditor(
				PreferenceConstants.P_PLOT_BUFFER_SIZE, 
				"Plot Buffer Size (in # points):", 
				this.getFieldEditorParent());
		plotIntegerFieldEditor.setValidRange(10, 2000);
		addField(plotIntegerFieldEditor);
		
		BooleanFieldEditor showDefineWarningDialog = new BooleanFieldEditor(
				PreferenceConstants.P_SHOW_DEFINE_CONFIRM_DIALOG, 
				"Show Warning Dialog for Define of Motor Axes",
				this.getFieldEditorParent());
		addField(showDefineWarningDialog);
		
		BooleanFieldEditor showAutoplayWarningsFieldEditor = new BooleanFieldEditor(
				PreferenceConstants.P_SHOW_AUTOPLAY_WARNINGS,
				"Show Warnings for deactivated Autoplay",
				this.getFieldEditorParent());
		addField(showAutoplayWarningsFieldEditor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(IWorkbench workbench) {
	}
}