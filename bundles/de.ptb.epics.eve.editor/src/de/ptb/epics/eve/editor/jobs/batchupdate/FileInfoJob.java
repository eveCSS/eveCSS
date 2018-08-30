package de.ptb.epics.eve.editor.jobs.batchupdate;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import de.ptb.epics.eve.editor.views.batchupdateview.BatchUpdater;
import de.ptb.epics.eve.editor.views.batchupdateview.FileEntry;
import de.ptb.epics.eve.editor.views.batchupdateview.FileStatus;
import de.ptb.epics.eve.util.data.Version;

/**
 * @author Marcus Michalsky
 * @since 1.30
 */
public class FileInfoJob extends BatchUpdateJob {
	private static final Logger LOGGER = Logger
			.getLogger(FileInfoJob.class.getName());
	
	private BatchUpdater batchUpdater;
	private FileEntry fileEntry;
	
	public FileInfoJob(String name, BatchUpdater batchUpdater,
			FileEntry fileEntry) {
		super(name);
		this.batchUpdater = batchUpdater;
		this.fileEntry = fileEntry;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
			Document document = builder.parse(this.fileEntry.getPath());
			Node node = document.getElementsByTagName("version").item(0);
			if (node == null) {
				this.fileEntry.setStatus(FileStatus.ERROR);
				this.fileEntry.setErrorMessage("version element not found");
				return Status.OK_STATUS;
			}
			String versionString = node.getChildNodes().item(0).getNodeValue();
			this.fileEntry.setVersion(new Version(
					Integer.parseInt(versionString.split("\\.")[0]), 
					Integer.parseInt(versionString.split("\\.")[1])));
		} catch (ParserConfigurationException | SAXException | IOException e) {
			this.fileEntry.setStatus(FileStatus.ERROR);
			this.fileEntry.setErrorMessage(e.getMessage());
			LOGGER.error(e.getMessage(), e);
			return Status.OK_STATUS;
		}
		if (fileEntry.getVersion()
				.compareTo(this.batchUpdater.getCurrentVersion()) == 0) {
			this.fileEntry.setStatus(FileStatus.UPTODATE);
		} else if (fileEntry.getVersion().compareTo(new Version(2, 3)) < 0) {
			fileEntry.setStatus(FileStatus.ERROR);
			fileEntry.setErrorMessage("Versions less than 2.3 are not supported");
		} else {
			fileEntry.setStatus(FileStatus.OUTDATED);
		}
		return Status.OK_STATUS;
	}
}