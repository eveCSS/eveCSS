package de.ptb.epics.eve.editor;

import java.io.File;

/**
 * Interface of extension point "de.ptb.epics.eve.editor.dispatcher".
 * Implementors are informed of new scan descriptions via
 * {@link #scanDescriptionReceived(File, boolean)}.
 * 
 * @author Marcus Michalsky
 * @since 1.1
 */
public interface IScanDescriptionReceiver {

	/**
	 * Called whenever the Editor propagates a scan description.
	 * 
	 * @param location the file that is propagated
	 * @param switchPerspective
	 *			<code>true</code> if perspective activation is requested,
	 *			<code>false</code> otherwise
	 */
	void scanDescriptionReceived(File location, boolean switchPerspective);
}