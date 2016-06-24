package de.ptb.epics.eve.editor.jobs.file;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

/**
 * Job to save the given position list to the given file name
 * 
 * @author Marcus Michalsky
 * @since 1.26
 */
public class PositionlistToFile extends Job {
	private static final Logger LOGGER = 
			Logger.getLogger(PositionlistToFile.class.getName());
	private File file;
	private String positionList;
	
	public PositionlistToFile(String name, File file, String positionList) {
		super(name);
		this.file = file;
		this.positionList = positionList;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		List<String> positions = Arrays.asList(positionList.split(","));
		monitor.beginTask("Saving position list to file", 3 + positions.size());
		if (file.exists()) {
			monitor.subTask("deleting existing file");
			if (!file.delete()) {
				LOGGER.error("Existing File could not be deleted. " +
						"Positionlist could not be saved.");
				return Status.CANCEL_STATUS;
			}
		}
		monitor.worked(1);
		monitor.subTask("creating file");
		try {
			if (!file.createNewFile()) {
				LOGGER.error("File could not be created. " + 
						"Positionlist could not be saved.");
				return Status.CANCEL_STATUS;
			}
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
			return Status.CANCEL_STATUS;
		}
		monitor.worked(1);
		monitor.subTask("Opening File");
		Charset charset = Charset.forName("UTF-8");
		try (BufferedWriter writer = Files.newBufferedWriter(file.toPath(), charset)) {
			monitor.worked(1);
			monitor.subTask("Inserting Positions");
			for (String s : positions) {
				String trimmedString = s.trim();
				writer.write(trimmedString, 0, trimmedString.length());
				writer.newLine();
				monitor.worked(1);
			}
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}
		monitor.done();
		LOGGER.info("position list saved.");
		return Status.OK_STATUS;
	}
}