package de.ptb.epics.eve.editor;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchListener;

import de.ptb.epics.eve.editor.jobs.batchupdate.BatchUpdateJob;

/**
 * <code>WorkbenchListener</code> interrupts the shutdown until all save jobs 
 * are done.
 * 
 * @author Marcus Michalsky
 * @since 1.1
 */
public class WorkbenchListener implements IWorkbenchListener {
	
	// logging
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
		// Obtain the Platform job manager to sync with "file" jobs
		IJobManager manager = Job.getJobManager();
		try {
			manager.join("file", new NullProgressMonitor());
			manager.join("defaults", new NullProgressMonitor());
			manager.join(BatchUpdateJob.FAMILY, new NullProgressMonitor());
		} catch (OperationCanceledException | InterruptedException e1) {
			logger.warn(e1.getMessage(), e1);
			logger.warn("shutdown aborted");
		}
		logger.debug("shutdown ok");
	}
}