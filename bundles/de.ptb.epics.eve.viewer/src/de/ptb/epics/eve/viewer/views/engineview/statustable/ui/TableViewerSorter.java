package de.ptb.epics.eve.viewer.views.engineview.statustable.ui;

import java.util.List;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

import de.ptb.epics.eve.viewer.Activator;
import de.ptb.epics.eve.viewer.views.engineview.statustable.StatusTableElement;

/**
 * @author Marcus Michalsky
 * @since 1.26
 */
public class TableViewerSorter extends ViewerSorter {
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		StatusTableElement item = (StatusTableElement)e1;
		StatusTableElement other = (StatusTableElement)e2;
		
		if (!item.getChainId().equals(other.getChainId())) {
			return item.getChainId() - other.getChainId();
		}
		
		// below: items are in same chain
		
		if (item.getScanModuleId() == null) {
			// item is a chain, other one of its scanmodules
			return -1;
		}
		if (other.getScanModuleId() == null) {
			// item is a scan module, other its chain
			return 1;
		}
		
		// below: both items are scanmodules of the same chain
		
		List<Integer> executionOrder = Activator.getDefault().
				getCurrentScanDescription().getChain(item.getChainId()).
					getExecutionOrder();
		
		return executionOrder.indexOf(item.getScanModuleId()) - 
			executionOrder.indexOf(other.getScanModuleId());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int category(Object element) {
		return ((StatusTableElement)element).getChainId();
	}
}