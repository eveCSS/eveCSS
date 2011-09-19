package de.ptb.epics.eve.editor.views.motoraxisview;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import de.ptb.epics.eve.data.scandescription.PluginController;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener;
import de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent;

/**
 * <code>PluginControllerInputWrapper</code>.
 * 
 * @author ?
 */
public class PluginControllerInputWrapper implements IModelUpdateListener,
													IStructuredContentProvider {

	private Viewer currentViewer;
	private PluginController pluginController;
	
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
	public Object[] getElements(final Object arg0) {
		// FIXME: Hier müssen eigentlich alle Parameter gepaart mit werden 
		// zurückgegeben werden und nicht nur die gesetzten werte.
		return pluginController.getElements();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void inputChanged(final Viewer viewer, final Object oldInput, 
							 final Object newInput) {
		if(oldInput != null) {
			((PluginController)oldInput).removeModelUpdateListener(this);
		}
		if(newInput != null) {
			((PluginController)newInput).addModelUpdateListener(this);
		}
		this.pluginController = (PluginController)newInput;
		this.currentViewer = viewer;

	}
	
	/**
	 * Returns the 
	 * {@link de.ptb.epics.eve.data.scandescription.PluginController}.
	 * 
	 * @return the plug in controller
	 */
	public PluginController getPluginController() {
		return this.pluginController;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {
		if(pluginController == null) return;
		pluginController.removeModelUpdateListener(this);
	}
}