package de.ptb.epics.eve.editor.jobs.batchupdate;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import de.ptb.epics.eve.data.scandescription.updater.Updater;
import de.ptb.epics.eve.data.scandescription.updater.VersionTooNewException;
import de.ptb.epics.eve.data.scandescription.updater.patches.SCMLUpdater;
import de.ptb.epics.eve.editor.views.batchupdateview.FileEntry;
import de.ptb.epics.eve.editor.views.batchupdateview.FileStatus;
import de.ptb.epics.eve.util.data.Version;

/**
 * @author Marcus Michalsky
 * @since 1.30
 */
public class FileUpdateJob extends BatchUpdateJob {
	private static final Logger LOGGER = Logger
			.getLogger(FileUpdateJob.class.getName());
	
	private FileEntry fileEntry;
	private String savePath;
	private Version currentVersion;
	
	public FileUpdateJob(FileEntry fileToUpdate, String savePath,
			Version currentVersion) {
		super(fileToUpdate.getName());
		this.fileEntry = fileToUpdate;
		this.savePath = savePath;
		this.currentVersion = currentVersion;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		this.fileEntry.setStatus(FileStatus.UPDATING);
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
			Document document = builder.parse(this.fileEntry.getFile());
			
			Updater updater = new SCMLUpdater();

			updater.update(document, currentVersion);
			
			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer transformer = tFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "no");
			DOMSource source = new DOMSource(document);
			StreamResult result = new StreamResult(new File(
					savePath + File.separator + fileEntry.getName()));
			transformer.transform(source, result);
		} catch (ParserConfigurationException | SAXException | IOException
				| TransformerException | VersionTooNewException e) {
			LOGGER.error(e.getMessage(), e);
			this.fileEntry.setErrorMessage(e.getMessage());
			this.fileEntry.setStatus(FileStatus.ERROR_DURING_UPDATE);
			return Status.OK_STATUS;
		}
		this.fileEntry.setStatus(FileStatus.UPDATED);
		this.fileEntry.setVersion(this.currentVersion);
		return Status.OK_STATUS;
	}
}