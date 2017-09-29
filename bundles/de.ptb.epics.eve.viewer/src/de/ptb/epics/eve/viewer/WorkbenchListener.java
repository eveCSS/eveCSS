package de.ptb.epics.eve.viewer;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchListener;

/**
 * 
 * @author Marcus Michalsky
 * @since 1.4
 */
public class WorkbenchListener implements IWorkbenchListener {

	private static Logger logger = 
			Logger.getLogger(WorkbenchListener.class.getName());
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean preShutdown(IWorkbench workbench, boolean forced) {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void postShutdown(IWorkbench workbench) {
		// Obtain the Platform job manager to sync with certain jobs
		IJobManager manager = Job.getJobManager();
		try {
			manager.join("messages", new NullProgressMonitor());
		} catch (OperationCanceledException e1) {
			logger.warn(e1.getMessage(), e1);
			logger.warn("shutdown aborted");
		} catch (InterruptedException e1) {
			logger.warn(e1.getMessage(), e1);
			logger.warn("shutdown aborted");
		}
		logger.debug("shutdown ok");
	}
}