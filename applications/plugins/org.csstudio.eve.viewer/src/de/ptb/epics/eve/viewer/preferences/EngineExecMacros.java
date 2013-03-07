package de.ptb.epics.eve.viewer.preferences;

import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Version;

import de.ptb.epics.eve.viewer.Activator;

/**
 * @author Marcus Michalsky
 * @since 1.10
 */
public enum EngineExecMacros {
	
	/**
	 * current version of eveCSS
	 */
	VERSION {
		@Override public String toString() { return "${VERSION}"; }
	},
	
	/**
	 * name of the test site
	 */
	LOCATION {
		@Override public String toString() { return "${LOCATION}"; }
	};
	
	/**
	 * Returns the ENUM of the corresponding string.
	 * 
	 * @param value the string
	 * @return the ENUM of the corresponding string
	 * @throws IllegalArgumentException if no match is found
	 */
	public static EngineExecMacros getEnum(String value)
			throws IllegalArgumentException {
		if (value.equals(EngineExecMacros.VERSION.toString())) {
			return EngineExecMacros.VERSION;
		} else if (value.equals(EngineExecMacros.LOCATION.toString())) {
			return EngineExecMacros.LOCATION;
		}
		throw new IllegalArgumentException("no match");
	}
	
	/**
	 * Returns the string value of the given macro.
	 * 
	 * @param macro the macro to be substituted
	 * @return the string value of the given macro
	 */
	public static String substituteMacro(EngineExecMacros macro) {
		switch(macro) {
		case LOCATION:
			return Activator.getDefault().getMeasuringStation().getName();
		case VERSION:
			Version version = Platform.getProduct().getDefiningBundle()
			.getVersion();
	return version.getMajor() + "." + version.getMinor();
		default:
			return "";
		}
	}
}