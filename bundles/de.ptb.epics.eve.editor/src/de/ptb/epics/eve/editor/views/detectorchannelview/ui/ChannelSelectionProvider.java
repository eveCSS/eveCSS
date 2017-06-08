package de.ptb.epics.eve.editor.views.detectorchannelview.ui;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;

import de.ptb.epics.eve.data.scandescription.Channel;
import de.ptb.epics.eve.util.ui.rcp.SingleSelectionProvider;

/**
 * @author Marcus Michalsky
 * @since 1.8
 */
public class ChannelSelectionProvider extends SingleSelectionProvider {

	private static final Logger LOGGER = Logger
			.getLogger(ChannelSelectionProvider.class.getName());
	
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
		if (!(sel.getFirstElement() instanceof Channel)) {
			return;
		}
		this.currentSelection = selection;
		for (ISelectionChangedListener listener : this.listeners) {
			listener.selectionChanged(new SelectionChangedEvent(this, selection));
		}
	}
}