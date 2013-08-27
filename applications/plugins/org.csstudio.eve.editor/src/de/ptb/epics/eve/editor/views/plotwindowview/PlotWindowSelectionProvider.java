package de.ptb.epics.eve.editor.views.plotwindowview;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;

import de.ptb.epics.eve.data.scandescription.PlotWindow;
import de.ptb.epics.eve.util.rcp.SingleSelectionProvider;

/**
 * @author Marcus Michalsky
 * @since 1.12
 */
public class PlotWindowSelectionProvider extends SingleSelectionProvider {

	private static final Logger LOGGER = Logger
			.getLogger(PlotWindowSelectionProvider.class.getName());
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		LOGGER.debug(part.getTitle() + " : " + selection.toString());
		if (!(part instanceof IViewPart)) {
			return;
		}
		if (!((IViewPart) part).getViewSite().getId()
				.equals("de.ptb.epics.eve.editor.views.ScanModulView")) {
			return;
		}
		if (!(selection instanceof IStructuredSelection)) {
			return;
		}
		IStructuredSelection sel = (IStructuredSelection)selection;
		if (sel.size() != 1) {
			return;
		}
		if (!(sel.getFirstElement() instanceof PlotWindow)) {
			return;
		}
		this.currentSelection = selection;
		for (ISelectionChangedListener listener : this.listeners) {
			listener.selectionChanged(new SelectionChangedEvent(this, selection));
		}
	}
}