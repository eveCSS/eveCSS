package de.ptb.epics.eve.editor.jobs.defaults;

import java.io.File;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import de.ptb.epics.eve.data.scandescription.defaults.DefaultsManager;

/**
 * @author Marcus Michalsky
 * @since 1.8
 */
public class LoadDefaults extends Job {
	private static final Logger LOGGER = Logger
			.getLogger(LoadDefaults.class.getName());
	
	private final String family = "defaults";
	
	private DefaultsManager manager;
	private File defaults;
	private File schema;
	
	/**
	 * 
	 * @param name
	 * @param manager
	 * @param pathToDefaults
	 * @param schema
	 */
	public LoadDefaults(String name, DefaultsManager manager, File pathToDefaults,
			File schema) {
		super(name);
		this.manager = manager;
		this.defaults = pathToDefaults;
		this.schema = schema;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		this.manager.init(defaults, schema);
		LOGGER.debug("Defaults Manager initialized");
		return Status.OK_STATUS;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean belongsTo(Object family) {
		return family.equals(this.family);
	}
}