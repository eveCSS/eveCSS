package de.ptb.epics.eve.editor.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import de.ptb.epics.eve.editor.Activator;

/**
 * @author Marcus Michalsky
 * @since 1.26
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		
		store.setDefault(PreferenceConstants.P_CSV_DELIMITER, ";");
		store.setDefault(PreferenceConstants.P_CSV_SHOW_WARNING, true);
		store.setDefault(PreferenceConstants.P_CSV_SHOW_ERROR, true);
	}
}