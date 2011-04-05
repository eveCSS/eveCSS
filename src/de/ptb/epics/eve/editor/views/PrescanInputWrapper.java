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

import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener;
import de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent;

public class PrescanInputWrapper implements IModelUpdateListener,
		IStructuredContentProvider {

	private Viewer currentViewer;
	private ScanModule scanModul;
	
	public void updateEvent( final ModelUpdateEvent modelUpdateEvent ) {
		this.currentViewer.refresh();

	}

	public Object[] getElements( final Object inputElement ) {
		return ((ScanModule)inputElement).getPrescans();
	}

	public void dispose() {
	}

	public void inputChanged( final Viewer viewer, final Object oldInput, final Object newInput ) {
		if( oldInput != null ) {
			((ScanModule)oldInput).removeModelUpdateListener( this );
		}
		if( newInput != null ) {
			((ScanModule)newInput).addModelUpdateListener( this );
		}
		this.scanModul = (ScanModule)newInput;
		this.currentViewer = viewer;
	}
}
