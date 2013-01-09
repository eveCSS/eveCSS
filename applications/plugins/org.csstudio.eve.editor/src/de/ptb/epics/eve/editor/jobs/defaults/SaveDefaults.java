package de.ptb.epics.eve.editor.jobs.defaults;

import java.io.File;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import de.ptb.epics.eve.data.scandescription.ScanDescription;
import de.ptb.epics.eve.data.scandescription.defaults.DefaultsManager;

/**
 * @author Marcus Michalsky
 * @since 1.8
 */
public class SaveDefaults extends Job {

	private static final Logger LOGGER = Logger.getLogger(SaveDefaults.class
			.getName());
	
	private final String family = "defaults";
	
	private DefaultsManager defaultsManager;
	private ScanDescription scanDescription;
	private File defaultsFile;
	private File schemaFile;
	
	/**
	 * 
	 * @param name
	 * @param defaultsManager
	 * @param scanDescription
	 * @param defaultsFile
	 * @param schemaFile
	 */
	public SaveDefaults(String name, DefaultsManager defaultsManager,
			ScanDescription scanDescription, File defaultsFile, File schemaFile) {
		super(name);
		this.defaultsManager = defaultsManager;
		this.scanDescription = scanDescription;
		this.defaultsFile = defaultsFile;
		this.schemaFile = schemaFile;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		try {
			monitor.beginTask("Saving Defaults", 2);
			monitor.subTask("collecting data");
			this.defaultsManager.update(scanDescription);
			monitor.worked(1);
			monitor.subTask("writing to file");
			this.defaultsManager.save(defaultsFile, schemaFile);
			monitor.worked(1);
		} finally {
			monitor.done();
		}
		LOGGER.info("defaults saved");
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