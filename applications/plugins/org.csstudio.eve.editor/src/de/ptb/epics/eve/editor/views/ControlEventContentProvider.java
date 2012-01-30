package de.ptb.epics.eve.editor.views;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import de.ptb.epics.eve.data.scandescription.updatenotification.ControlEventManager;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener;
import de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent;

/**
 * 
 * @author ?
 * @author Marcus Michalsky
 */
public class ControlEventContentProvider implements IModelUpdateListener, 
												IStructuredContentProvider {

	/*
	 * 
	 */
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
		ControlEventManager input = (ControlEventManager)inputElement;
		return input.getControlEventsList().toArray();	
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
			((ControlEventManager)oldInput).removeModelUpdateListener(this);
		}
		if(newInput != null) {
			((ControlEventManager)newInput).addModelUpdateListener(this);
		}
		this.currentViewer = viewer;
	}
}