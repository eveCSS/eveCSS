package de.ptb.epics.eve.editor.views.batchupdateview.ui;

import org.eclipse.jface.viewers.Viewer;

import de.ptb.epics.eve.editor.views.batchupdateview.FileEntry;
import de.ptb.epics.eve.util.ui.jface.ViewerComparator;

/**
 * @author Marcus Michalsky
 * @since 1.30
 */
public class NameViewerComparator extends ViewerComparator {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		return direction * (((FileEntry) e1).getName()
				.compareTo(((FileEntry) e2).getName()));
	}
}