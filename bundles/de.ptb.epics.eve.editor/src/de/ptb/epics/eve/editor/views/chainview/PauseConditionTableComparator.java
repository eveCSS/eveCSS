package de.ptb.epics.eve.editor.views.chainview;

import org.eclipse.jface.viewers.Viewer;

import de.ptb.epics.eve.data.scandescription.PauseCondition;
import de.ptb.epics.eve.util.ui.jface.viewercomparator.alphabetical.AlphabeticalViewerComparator;
import de.ptb.epics.eve.util.ui.jface.viewercomparator.alphabetical.SortOrder;

/**
 * @author Marcus Michalsky
 * @since 1.36
 */
public class PauseConditionTableComparator extends AlphabeticalViewerComparator {
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		int result = ((PauseCondition)e1).toString().compareTo(
				((PauseCondition)e2).toString());
		if (SortOrder.ASCENDING.equals(getSortOrder())) {
			return result;
		} else if (SortOrder.DESCENDING.equals(getSortOrder())) {
			return result * -1;
		}
		return 0;
	}
}
