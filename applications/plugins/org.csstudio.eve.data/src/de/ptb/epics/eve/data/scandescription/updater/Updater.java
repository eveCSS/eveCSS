package de.ptb.epics.eve.data.scandescription.updater;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import de.ptb.epics.eve.data.scandescription.updater.patches.Patch2o3T3o3;
import de.ptb.epics.eve.util.data.Version;

/**
 * Used to update an old scan description file to the current version.
 * Use as follows:
 * <ul>
 * 	<li></li>
 * </ul>
 * 
 * @author Marcus Michalsky
 * @since 1.18
 */
public class Updater {
	private static final Logger LOGGER = 
			Logger.getLogger(Updater.class.getName());
	
	/*
	 * Singleton with Eager initialization
	 */
	private static final Updater INSTANCE = new Updater();
	
	/*
	 * the generated patch list must obey the following condition:
	 * for each patch in list: patch.compareTo(next) < 0
	 * meaning the patches are ordered (ascending) containing no gaps:
	 * patch.targetVersion == next.sourceVersion
	 */
	private Updater() {
		this.patches = new LinkedList<Patch>();
		this.patches.add(Patch2o3T3o3.getInstance());
		
		// TODO add patches
	}
	
	/**
	 * 
	 * @return
	 */
	public static Updater getInstance() {
		return INSTANCE;
	}
	
	private List<Patch> patches;
	
	/**
	 * Updates the given document to the given target version.
	 * If the document version is equal or greater than the target version
	 * the document remains unchanged.
	 * 
	 * @param document the document to update
	 * @param target the target version
	 * @return a list containing all applied patches or <code>null</code> if 
	 * 		document version > program version
	 * @throws VersionTooOldException if document version is older than 2.2
	 */
	public List<Patch> update(Document document, Version target) 
			throws VersionTooOldException {
		List<Patch> changes = new LinkedList<Patch>();
		Version source = this.getDocumentVersion(document);
		if (source.compareTo(target) == 0) {
			LOGGER.info("version match, no update necessary");
			return changes;
		} else if (source.compareTo(target) > 0) {
			LOGGER.info("back to the future");
			return null;
		} else if (source.compareTo(new Version(2,2)) < 0) {
			throw new VersionTooOldException();
		} else if (source.compareTo(target) < 0) {
			if (LOGGER.isInfoEnabled()) { 
				LOGGER.info("file version (" + source
					+ ") older than current version (" + target
					+ "), updating...");
			}
		}

		for (Patch patch : this.patches) {
			if (patch.getSourceVersion().compareTo(source) < 0) {
				continue;
			} else {
				patch.execute(document);
				changes.add(patch);
			}
		}
		return changes;
	}
	
	private Version getDocumentVersion(Document document) {
		Node node = document.getElementsByTagName("version").item(0);
		String versionString = node.getChildNodes().item(0).getNodeValue();
		return new Version(
				Integer.parseInt(versionString.split("\\.")[0]), 
				Integer.parseInt(versionString.split("\\.")[1]));
	}
}