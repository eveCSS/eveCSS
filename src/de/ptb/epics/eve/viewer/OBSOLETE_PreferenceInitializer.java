package de.ptb.epics.eve.viewer;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import de.ptb.epics.eve.viewer.Activator;

/**
 * Class used to initialize default preference values.
 */
public class OBSOLETE_PreferenceInitializer extends AbstractPreferenceInitializer {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setDefault( PreferenceConstants.P_DEFAULT_MEASURING_STATION_DESCRIPTION, "" );
		
	}

}
