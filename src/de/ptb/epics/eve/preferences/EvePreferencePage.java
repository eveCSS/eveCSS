package de.ptb.epics.eve.preferences;

import java.io.File;

import org.eclipse.jface.preference.*;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbench;

/**
 * 
 * @author ?
 * @author Marcus Michalsky
 */
public class EvePreferencePage extends FieldEditorPreferencePage 
										implements IWorkbenchPreferencePage {

	/**
	 * Constructor
	 */
	public EvePreferencePage() {
		super(GRID);
		this.setPreferenceStore(Activator.getDefault().getPreferenceStore());
		this.setDescription("Configuration for Eve");
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createFieldEditors() {
		FileFieldEditor fileFieldEditor = new FileFieldEditor(
				PreferenceConstants.P_DEFAULT_MEASURING_STATION_DESCRIPTION, 
				"device definition file:", this.getFieldEditorParent());
		fileFieldEditor.setFileExtensions(new String[]{"*.xml"});
		
		String rootdir = Activator.getDefault().getRootDirectory();
		File file = new File(rootdir + "scml/");
		if(file.exists()) {
			// TODO set filter path to "file" (introduced in Eclipse 3.6 - Bug # 184)
		} else {
			// TODO set filter path to "new File(rootdir)"
		}
		file = null;
		
		addField(fileFieldEditor);
		addField(new StringFieldEditor(
				PreferenceConstants.P_DEFAULT_ENGINE_ADDRESS, 
				"Engine (name:port):", 
				this.getFieldEditorParent()));
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(final IWorkbench workbench) {
	}
}