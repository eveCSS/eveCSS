/*******************************************************************************
 * Copyright (c) 2001, 2008 Physikalisch Technische Bundesanstalt.
 * All rights reserved.
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package de.ptb.epics.eve.editor.views;

import java.util.Iterator;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import de.ptb.epics.eve.data.scandescription.Channel;
import de.ptb.epics.eve.data.scandescription.errors.IModelError;
import de.ptb.epics.eve.data.scandescription.errors.ChannelError;

public class DetectorChannelLabelProvider implements ITableLabelProvider {

	public Image getColumnImage( final Object channel, final int colIndex ) {
		final Channel pos = (Channel)channel;
		if( colIndex == 1 ) {
			final Iterator< IModelError > it = pos.getModelErrors().iterator();
			while( it.hasNext() ) {
				final IModelError modelError = it.next();
				if( modelError instanceof ChannelError ) {
					return PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJS_ERROR_TSK );
				}
			}
		}
		return null;
	}

	public String getColumnText( final Object channel, final int colIndex ) {
		final Channel pos = (Channel)channel;
		switch( colIndex ) {
			case 0:
				return (pos.getAbstractDevice()!=null)?pos.getAbstractDevice().getFullIdentifyer():"";
			case 1:
				int av = pos.getAverageCount();
				return "" + av;
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
