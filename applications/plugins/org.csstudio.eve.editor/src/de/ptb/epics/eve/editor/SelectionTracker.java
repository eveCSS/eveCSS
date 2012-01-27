package de.ptb.epics.eve.editor;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;

/**
 * 
 * 
 * @author Marcus Michalsky
 * @since 1.1
 */
public class SelectionTracker implements ISelectionListener {

	private static Logger logger = 
			Logger.getLogger(SelectionTracker.class.getName());
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		logger.debug("selection changed. Part: " + part.getTitle() + 
				" Selected Item: " + selection.toString());
	}
}