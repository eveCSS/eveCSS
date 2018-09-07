package de.ptb.epics.eve.editor.views.batchupdateview.ui;

import org.eclipse.jface.viewers.Viewer;

import de.ptb.epics.eve.editor.views.batchupdateview.FileEntry;
import de.ptb.epics.eve.util.ui.jface.ViewerComparator;

/**
 * @author Marcus Michalsky
 * @since 1.30
 */
public class StatusViewerComparator extends ViewerComparator {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		FileEntry thisFile = (FileEntry) e1;
		FileEntry otherFile = (FileEntry) e2;
		if (thisFile.getStatus().equals(otherFile.getStatus())) {
			return direction
					* (thisFile.getName().compareTo(otherFile.getName()));
		}
		return direction * (thisFile.getStatus()
				.compareTo(otherFile.getStatus()));
	}
}