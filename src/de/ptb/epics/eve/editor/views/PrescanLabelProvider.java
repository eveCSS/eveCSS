/*******************************************************************************
 * Copyright (c) 2001, 2008 Physikalisch Technische Bundesanstalt.
 * All rights reserved.
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package de.ptb.epics.eve.editor.views;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import de.ptb.epics.eve.data.scandescription.Prescan;

public class PrescanLabelProvider implements ITableLabelProvider {

	public Image getColumnImage( final Object prescan, final int colIndex ) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getColumnText( final Object prescan, final int colIndex ) {
		final Prescan pos = (Prescan)prescan;
		switch( colIndex ) {
			case 0:
				return (pos.getAbstractPrePostscanDevice()!=null)?pos.getAbstractPrePostscanDevice().getFullIdentifyer():"";
			case 1:
				return (pos.getValue()!=null)?pos.getValue():"";
		}
		return "";
	}

	public void addListener( final ILabelProviderListener arg0 ) {
		// TODO Auto-generated method stub

	}

	public void dispose() {
		// TODO Auto-generated method stub

	}

	public boolean isLabelProperty( final Object arg0, String arg1 ) {
		// TODO Auto-generated method stub
		return false;
	}

	public void removeListener( final ILabelProviderListener arg0 ) {
		// TODO Auto-generated method stub

	}

}
