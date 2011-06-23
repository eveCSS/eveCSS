package de.ptb.epics.eve.editor.logging;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.ILogListener;
import org.eclipse.core.runtime.IStatus;

/**
 * <code>LogListener</code>.
 * 
 * @author Marcus Michalsky
 * @since 0.4.1
 * @see <a href="http://random-eclipse-tips.blogspot.com/2009/07/eclipse-rcp-redirecting-eclipse-errors.html">
 * 		Random Eclipse Tips: Eclipse RCP: Redirecting eclipse errors to log4j</a>
 */
public class EclipseLogListener implements ILogListener {

	private static final Logger logger = 
		Logger.getLogger(EclipseLogListener.class.getName());
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void logging(IStatus status, String plugin) {
		if (status.getSeverity() == IStatus.WARNING) {
			if (status.getException() == null) {
				logger.warn(status.getMessage());
			} else {
				logger.warn(status.getMessage(), status.getException());
			}
		} else if (status.getSeverity() == IStatus.ERROR) {
			if (status.getException() == null) {
				logger.error(status.getMessage());
			} else {
				logger.error(status.getMessage(), status.getException());
			}
		}
	}
}