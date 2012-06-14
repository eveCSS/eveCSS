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
public class PreferencePage extends FieldEditorPreferencePage 
										implements IWorkbenchPreferencePage {

	/**
	 * Constructor
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
	public void createFieldEditors() {
		FileFieldEditor stationFileFieldEditor = new FileFieldEditor(
				PreferenceConstants.P_DEFAULT_MEASURING_STATION_DESCRIPTION, 
				"device definition file:", this.getFieldEditorParent());
		stationFileFieldEditor.setFileExtensions(new String[]{"*.xml"});
		
		String rootdir = Activator.getDefault().getRootDirectory();
		File file = new File(rootdir + "eve/");
		if(file.exists()) {
			stationFileFieldEditor.setFilterPath(file);
		} else {
			stationFileFieldEditor.setFilterPath(new File(rootdir));
		}
		file = null;
		
		addField(stationFileFieldEditor);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(final IWorkbench workbench) {
	}
}