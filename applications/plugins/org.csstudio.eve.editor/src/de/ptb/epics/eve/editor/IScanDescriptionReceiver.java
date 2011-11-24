package de.ptb.epics.eve.editor;

import java.io.File;

/**
 * 
 * 
 * @author Marcus Michalsky
 * @since 1.1
 */
public interface IScanDescriptionReceiver {

	/**
	 * 
	 * @param location
	 * @param switchPerspective
	 */
	public void scanDescriptionReceived(File location, boolean switchPerspective);
}
