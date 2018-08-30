package de.ptb.epics.eve.editor.jobs.batchupdate;

import org.eclipse.core.runtime.jobs.Job;

/**
 * @author Marcus Michalsky
 * @since 1.30
 */
public abstract class BatchUpdateJob extends Job {
	public static final String FAMILY = "BatchUpdateJobFamily";
	
	public BatchUpdateJob(String name) {
		super(name);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean belongsTo(Object family) {
		return BatchUpdateJob.FAMILY.equals(family);
	}
}