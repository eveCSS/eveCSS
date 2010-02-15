/*******************************************************************************
 * Copyright (c) 2001, 2008 Physikalisch Technische Bundesanstalt.
 * All rights reserved.
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package de.ptb.epics.eve.editor.views;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import de.ptb.epics.eve.data.scandescription.PluginController;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener;
import de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent;

public class PluginControllerInputWrapper implements IModelUpdateListener,
		IStructuredContentProvider {

	private Viewer currentViewer;
	private PluginController pluginController;
	
	
	public void updateEvent( final ModelUpdateEvent modelUpdateEvent ) {
		this.currentViewer.refresh();
	}

	public Object[] getElements( final Object arg0 ) {
		// FIXME: Hier müssen eigentlich alle Parameter gepaart mit werden zurückgegeben werden und nicht nur die gesetzten werte.
		return pluginController.getElements();
	}

	public void dispose() {	
	}

	public void inputChanged( final Viewer viewer, final Object oldInput, final Object newInput ) {
		if( oldInput != null ) {
			((PluginController)oldInput).removeModelUpdateListener( this );
		}
		if( newInput != null ) {
			((PluginController)newInput).addModelUpdateListener( this );
		}
		this.pluginController = (PluginController)newInput;
		this.currentViewer = viewer;

	}
	
	public PluginController getPluginController() {
		return this.pluginController;
	}

}
