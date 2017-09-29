package de.ptb.epics.eve.editor.views.scanmoduleview;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;

import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener;
import de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent;

/**
 * Base class for content provider of table viewers used in
 * {@link ActionComposite}s. Clients using this class have to extend it and 
 * override {@link #getElements(Object)}.
 * <p>
 * Selects a newly added item of the table or the first one if one was removed.
 * 
 * @author Marcus Michalsky
 * @since 1.2
 */
public abstract class CompositeContentProvider implements
		IStructuredContentProvider, IModelUpdateListener {
	
	private TableViewer currentViewer;
	private ScanModule scanModule;

	/**
	 * Default Implementation. Does nothing. Should be overwritten to respond 
	 * to changes of current input. Overwriting methods should still call it 
	 * afterwards to ensure refresh of viewer.
	 */
	@Override
	public void updateEvent(ModelUpdateEvent modelUpdateEvent) {
		this.currentViewer.refresh();
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
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.currentViewer = (TableViewer) viewer;
		if (oldInput != null) {
			((ScanModule) oldInput).removeModelUpdateListener(this);
		}
		this.scanModule = (ScanModule)newInput;
		if (newInput != null) {
			((ScanModule) newInput).addModelUpdateListener(this);
		}
	}
	
	protected ScanModule getScanModule() {
		return this.scanModule;
	}
	
	protected TableViewer getTableViewer() {
		return this.currentViewer;
	}
}