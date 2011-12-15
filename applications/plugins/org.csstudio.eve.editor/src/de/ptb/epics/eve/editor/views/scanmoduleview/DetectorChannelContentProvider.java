package de.ptb.epics.eve.editor.views.scanmoduleview;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener;
import de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent;

/**
 * <code>DetectorChannelInputWrapper</code> is the content provider of the table 
 * viewer defined in 
 * {@link de.ptb.epics.eve.editor.views.scanmoduleview.DetectorChannelComposite}.
 * 
 * @author ?
 * @author Marcus Michalsky
 */
public class DetectorChannelContentProvider implements IModelUpdateListener,
		IStructuredContentProvider {

	private Viewer currentViewer;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateEvent(final ModelUpdateEvent modelUpdateEvent) {
		this.currentViewer.refresh();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object[] getElements(final Object inputElement) {
		return ((ScanModule)inputElement).getChannels();
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
		if(oldInput != null) {
			((ScanModule)oldInput).removeModelUpdateListener(this);
		}
		if(newInput != null) {
			((ScanModule)newInput).addModelUpdateListener(this);
		}
		this.currentViewer = viewer;
	}
}