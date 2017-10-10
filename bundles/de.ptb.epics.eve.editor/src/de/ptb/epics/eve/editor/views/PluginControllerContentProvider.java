package de.ptb.epics.eve.editor.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import de.ptb.epics.eve.data.measuringstation.PluginParameter;
import de.ptb.epics.eve.data.scandescription.PluginController;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener;
import de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent;
import de.ptb.epics.eve.editor.views.motoraxisview.plugincomposite.PluginParameterValue;

/**
 * <code>PluginControllerInputWrapper</code>.
 * 
 * @author ?
 */

public class PluginControllerContentProvider implements IModelUpdateListener,
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
	public Object[] getElements(final Object input) {

		List<PluginParameterValue> elements = new ArrayList<>();
		
		PluginController pluginController = (PluginController)input;
		for (PluginParameter param : pluginController.getPlugin().getParameters()){
			elements.add(new PluginParameterValue(param, pluginController, pluginController.getValues().get(param.getName())));
		}		
		return elements.toArray();
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
		if(pluginController == null) {
			return;
		}
		pluginController.removeModelUpdateListener(this);
	}
}