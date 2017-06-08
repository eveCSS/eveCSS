package de.ptb.epics.eve.util.ui.rcp;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.ui.ISelectionListener;

import de.ptb.epics.eve.util.ui.Activator;

/**
 * Use as Selection Provider for Views that implement the master slave binding.
 * During construction the class is registered as listener to the selection 
 * service. Override 
 * {@link org.eclipse.ui.ISelectionListener#selectionChanged(org.eclipse.ui.IWorkbenchPart, ISelection)}
 * to specify behavior. Contained listeners should be informed only for single 
 * selections and types used as master.
 * 
 * @author Marcus Michalsky
 * @since 1.8
 */
public abstract class SingleSelectionProvider implements ISelectionProvider,
		ISelectionListener {
	
	protected List<ISelectionChangedListener> listeners;
	
	protected ISelection currentSelection;
	
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
}