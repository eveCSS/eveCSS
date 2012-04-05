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
	
	private static Logger logger = Logger.getLogger(
			ScanDescriptionReceiver.class.getName());
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void scanDescriptionReceived(File location, boolean switchPerspective) {
		Activator.getDefault().connectEngine();
		// either we were connected before or have done it above, we are 
		// connected now and can add the scan description to the play list.
		if (Activator.getDefault().getEcp1Client().isRunning()) {
			try {
				Activator.getDefault().getEcp1Client().getPlayListController().
					addLocalFile(location);
			} catch(final IOException e) {
				logger.error(e.getMessage(), e);
			}
		}
		
		if (switchPerspective) {
			try {
				PlatformUI.getWorkbench().showPerspective(
						"EveEnginePerspective", Activator.getDefault().
						getWorkbench().getActiveWorkbenchWindow());
			} catch (WorkbenchException e) {
				logger.error(e.getMessage(), e);
			}
		}
		logger.debug("scan description received.");
	}
}