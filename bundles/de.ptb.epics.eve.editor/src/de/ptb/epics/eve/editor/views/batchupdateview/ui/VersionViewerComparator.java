package de.ptb.epics.eve.editor.views.batchupdateview.ui;

import org.eclipse.jface.viewers.Viewer;

import de.ptb.epics.eve.editor.views.batchupdateview.FileEntry;
import de.ptb.epics.eve.util.ui.jface.ViewerComparator;

/**
 * @author Marcus Michalsky
 * @since 1.30
 */
public class VersionViewerComparator extends ViewerComparator {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		FileEntry thisFile = (FileEntry) e1;
		FileEntry otherFile = (FileEntry) e2;
		// file entries with no version (errorness) always appear at the end
		if (thisFile.getVersion() == null) {
			return 1;
		}
		if (otherFile.getVersion() == null) {
			return -1;
		}
		if (thisFile.getVersion().compareTo(otherFile.getVersion()) == 0) {
			return direction
					* (thisFile.getName().compareTo(otherFile.getName()));
		}
		return direction * (thisFile.getVersion()
				.compareTo(otherFile.getVersion()));
	}
}