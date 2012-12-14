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
public class Load extends Job {
	private static final Logger LOGGER = Logger
			.getLogger(Load.class.getName());
	
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
	public Load(String name, DefaultsManager manager, File pathToDefaults,
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
		return Status.OK_STATUS;
	}
}
