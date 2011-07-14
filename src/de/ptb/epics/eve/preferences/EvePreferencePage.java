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
		FileFieldEditor stationFileFieldEditor = new FileFieldEditor(
				PreferenceConstants.P_DEFAULT_MEASURING_STATION_DESCRIPTION, 
				"device definition file:", this.getFieldEditorParent());
		stationFileFieldEditor.setFileExtensions(new String[]{"*.xml"});
		
		FileFieldEditor engineFileFieldEditor = new FileFieldEditor(
				PreferenceConstants.P_DEFAULT_ENGINE_LOCATION, 
				"Engine location:", this.getFieldEditorParent());
		
		String rootdir = Activator.getDefault().getRootDirectory();
		File file = new File(rootdir + "scml/");
		if(file.exists()) {
			// TODO set filter path to "file" (introduced in Eclipse 3.6 - Bug # 184)
			// with fileFieldEditor.setFilterPath...
		} else {
			// TODO set filter path to "new File(rootdir)"
		}
		file = null;
		
		addField(stationFileFieldEditor);
		addField(new StringFieldEditor(
				PreferenceConstants.P_DEFAULT_ENGINE_ADDRESS, 
				"Engine (name:port):", 
				this.getFieldEditorParent()));
		addField(engineFileFieldEditor);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(final IWorkbench workbench) {
	}
}