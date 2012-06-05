package de.ptb.epics.eve.viewer.views.messagesview;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;

import de.ptb.epics.eve.viewer.messages.IMessagesContainerUpdateListener;
import de.ptb.epics.eve.viewer.messages.MessagesContainer;
import de.ptb.epics.eve.viewer.messages.ViewerMessage;

/**
 * 
 * @author ?
 * @author Marcus Michalsky
 */
public class ContentProvider 
		implements IStructuredContentProvider, IMessagesContainerUpdateListener {

	private TableViewer viewer;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object[] getElements(final Object inputElement) {
		return ((MessagesContainer) inputElement).getList().toArray();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void inputChanged(final Viewer viewer, final Object oldInput, 
			final Object newInput) {
		this.viewer = (TableViewer) viewer;
		if(oldInput != null) {
			((MessagesContainer)oldInput).
					removeMessagesContainerUpdateListener(this);
		}
		if(newInput != null) {
			((MessagesContainer)newInput).
					addMessagesContainerUpdateListener(this);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void update() {
		if (!viewer.getControl().isDisposed()) {
			
			// TODO this is a severe performance killer
			// do not switch to syncExec and do not do viewer.refresh()
			// use a tableviewer with  viewer.update() or viewer.add() instead!
			
			this.viewer.getControl().getDisplay().asyncExec( new Runnable() {
	
				@Override
				public void run() {
					if (!viewer.getControl().isDisposed()) viewer.refresh();
				}
				
			});
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addElement(final ViewerMessage viewerMessage) {
		this.update();
	}
}