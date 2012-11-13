package de.ptb.epics.eve.editor.views.detectorchannelview;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;

import de.ptb.epics.eve.data.scandescription.Channel;
import de.ptb.epics.eve.editor.Activator;

/**
 * @author Marcus Michalsky
 * @since 1.8
 */
public class SingleSelectionProvider implements ISelectionProvider,
		ISelectionListener {

	private static final Logger LOGGER = Logger
			.getLogger(SingleSelectionProvider.class.getName());
	
	private List<ISelectionChangedListener> listeners;
	
	private ISelection currentSelection;
	
	/**
	 * 
	 */
	public SingleSelectionProvider() {
		this.listeners = new ArrayList<ISelectionChangedListener>();
		this.currentSelection = null;
		Activator.getDefault().getWorkbench().getActiveWorkbenchWindow()
				.getSelectionService().addSelectionListener(this);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ISelection getSelection() {
		return this.currentSelection;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setSelection(ISelection selection) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		this.listeners.add(listener);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeSelectionChangedListener(
			ISelectionChangedListener listener) {
		this.listeners.remove(listener);
	}

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