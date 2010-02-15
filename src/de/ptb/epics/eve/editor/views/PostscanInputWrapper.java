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

import de.ptb.epics.eve.data.scandescription.ScanModul;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener;
import de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent;

public class PostscanInputWrapper implements IModelUpdateListener,
		IStructuredContentProvider {

	private Viewer currentViewer;
	private ScanModul scanModul;
	
	public void updateEvent( final ModelUpdateEvent modelUpdateEvent ) {
		this.currentViewer.refresh();

	}

	public Object[] getElements( final Object inputElement ) {
		return ((ScanModul)inputElement).getPostscans();
	}

	public void dispose() {
	}

	public void inputChanged( final Viewer viewer, final Object oldInput, final Object newInput ) {
		if( oldInput != null ) {
			((ScanModul)oldInput).removeModelUpdateListener( this );
		}
		if( newInput != null ) {
			((ScanModul)newInput).addModelUpdateListener( this );
		}
		this.scanModul = (ScanModul)newInput;
		this.currentViewer = viewer;
	}
}
