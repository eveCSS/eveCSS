package de.ptb.epics.eve.viewer;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;

import de.ptb.epics.eve.editor.IScanDescriptionReceiver;

/**
 * <code>ScanDescriptionReceiver</code>.
 * 
 * @author Marcus Michalsky
 * @since 1.1
 */
public class ScanDescriptionReceiver implements IScanDescriptionReceiver {
	
	private static Logger LOGGER = Logger.getLogger(
			ScanDescriptionReceiver.class.getName());
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void scanDescriptionReceived(File location, boolean switchPerspective) {
		if (switchPerspective) {
			try {
				PlatformUI.getWorkbench().showPerspective(
						"EveEnginePerspective", Activator.getDefault().
						getWorkbench().getActiveWorkbenchWindow());
			} catch (WorkbenchException e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
		
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {
			LOGGER.warn("Thread sleep interrupted: " + e1.getMessage());
		}
		
		Activator.getDefault().connectEngine();
		
		// either we were connected before or have done it above, we are 
		// connected now and can add the scan description to the play list.
		if (Activator.getDefault().getEcp1Client().isRunning()) {
			try {
				Activator.getDefault().getEcp1Client().getPlayListController().
					addLocalFile(location);
			} catch(final IOException e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
		
		LOGGER.debug("scan description received.");
	}
}