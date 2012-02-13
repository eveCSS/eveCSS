package de.ptb.epics.eve.editor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;

/**
 * <code>SelectionProviderWrapper</code> allows to have multiple selection 
 * providers in one view by registering itself at the selection service (as a 
 * {@link org.eclipse.jface.viewers.ISelectionProvider}). The contained viewers 
 * interested in providing selections have to manage which one is the currently 
 * active provider (e.g., with a {@link org.eclipse.swt.events.FocusListener}) 
 * by using {@link #setSelectionProvider(ISelectionProvider)}. 
 * 
 * @author Marcus Michalsky
 * @since 0.4.1
 * @see <a href="http://www.eclipsezone.com/eclipse/forums/t74510.html">
 * 		Multiple Selection Providers (EclipseZone)</a>
 */
public class SelectionProviderWrapper implements ISelectionProvider {

	private ISelectionProvider selectionProvider;
	private List<ISelectionChangedListener> selectionChangedListeners;
	private ISelection emptySelection;

	/**
	 * Constructs a <code>SelectionProviderWrapper</code>.
	 */
	public SelectionProviderWrapper() {
		selectionProvider = null;
		selectionChangedListeners = new ArrayList<ISelectionChangedListener>();
		emptySelection = StructuredSelection.EMPTY;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ISelection getSelection() {
		return (selectionProvider != null) 
				? selectionProvider.getSelection() 
				: emptySelection;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setSelection(ISelection selection) {
		if(selectionProvider != null) {
			selectionProvider.setSelection(selection);
		} else {
			this.emptySelection = selection;
			SelectionChangedEvent selectionChangedEvent = 
					new SelectionChangedEvent(this, selection);
			for(ISelectionChangedListener iscl : selectionChangedListeners) {
				iscl.selectionChanged(selectionChangedEvent);
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		selectionChangedListeners.add(listener);
		if(selectionProvider != null) {
			selectionProvider.addSelectionChangedListener(listener);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeSelectionChangedListener(
			ISelectionChangedListener listener) {
		selectionChangedListeners.remove(listener);
		if(selectionProvider != null) {
			selectionProvider.removeSelectionChangedListener(listener);
		}
	}

	/**
	 * Sets the given {@link org.eclipse.jface.viewers.ISelectionProvider} as 
	 * the selection provider of the view. 
	 * 
	 * @param selectionProvider the 
	 * 	{@link org.eclipse.jface.viewers.ISelectionProvider} that should be set 
	 */
	public void setSelectionProvider(ISelectionProvider selectionProvider) {
		if(this.selectionProvider != selectionProvider) {
			if(this.selectionProvider != null) {
				for(ISelectionChangedListener iscl : selectionChangedListeners) {
					this.selectionProvider.removeSelectionChangedListener(iscl);
				}
			}
			this.selectionProvider = selectionProvider;
			if(this.selectionProvider != null) {
				for(ISelectionChangedListener iscl : selectionChangedListeners) {
					this.selectionProvider.addSelectionChangedListener(iscl);
				}
				setSelection(selectionProvider.getSelection());
			} else {
				setSelection(emptySelection);
			}
		} else {
			if(this.selectionProvider != null) {
				setSelection(selectionProvider.getSelection());
			} else {
				setSelection(emptySelection);
			}
		}
	}
	
	/**
	 * Returns the currently set 
	 * {@link org.eclipse.jface.viewers.ISelectionProvider}.
	 * 
	 * @return the current set
	 * 			{@link org.eclipse.jface.viewers.ISelectionProvider}
	 */
	public ISelectionProvider getSelectionProvider() {
		return selectionProvider;
	}
}