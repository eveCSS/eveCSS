package de.ptb.epics.eve.editor.jobs.changesmtype;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.progress.UIJob;

/**
 * @author Marcus Michalsky
 * @since 1.31
 */
public abstract class ChangeToJob extends UIJob {
	public static final String FAMILY = "changesmtype";
	
	public ChangeToJob(String name) {
		super(name);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean belongsTo(Object family) {
		return family.equals(ChangeToJob.FAMILY);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean shouldRun() {
		return Job.getJobManager().find(ChangeToJob.FAMILY).length == 1;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean shouldSchedule() {
		return Job.getJobManager().find(ChangeToJob.FAMILY).length == 0;
	}
}
