/*******************************************************************************
 * Copyright (c) 2001, 2008 Physikalisch Technische Bundesanstalt.
 * All rights reserved.
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package de.ptb.epics.eve.editor.views.scanmoduleview;

import java.util.Iterator;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import de.ptb.epics.eve.data.DataTypes;
import de.ptb.epics.eve.data.scandescription.Prescan;
import de.ptb.epics.eve.data.scandescription.errors.IModelError;
import de.ptb.epics.eve.data.scandescription.errors.PrescanError;

public class PrescanLabelProvider implements ITableLabelProvider {

	public Image getColumnImage( final Object prescan, final int colIndex ) {
		final Prescan pos = (Prescan)prescan;
		if( colIndex == 1 ) {
			final Iterator< IModelError > it = pos.getModelErrors().iterator();
			while( it.hasNext() ) {
				final IModelError modelError = it.next();
				if( modelError instanceof PrescanError ) {
					return PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJS_ERROR_TSK );
				}
			}
		}
		return null;
	}

	public String getColumnText( final Object prescan, final int colIndex ) {
		final Prescan pos = (Prescan)prescan;
		switch( colIndex ) {
			case 0:
				return (pos.getAbstractPrePostscanDevice()!=null)?pos.getAbstractPrePostscanDevice().getFullIdentifyer():"";
			case 1:
				if (pos.getAbstractPrePostscanDevice().getValue().getType().equals(DataTypes.ONOFF)) {
					// Datentyp ONOFF vorhanden, als Value wird On oder Off gesetzt
					String[] werte = pos.getAbstractPrePostscanDevice().getValue().getDiscreteValues().toArray(new String[0]);
					if (werte[0].equals(pos.getValue()))
						// Erster Eintrag ist gesetzt, On anzeigen
						return "On";
					else if (werte[1].equals(pos.getValue()))
						// Zweiter Eintrag ist gesetzt, Off anzeigen
						return "Off";
					else
						return "";
				}
				else if (pos.getAbstractPrePostscanDevice().getValue().getType().equals(DataTypes.OPENCLOSE)) {
					// Datentyp OPENCLOSE vorhanden, als Value wird Open oder Close gesetzt
					String[] werte = pos.getAbstractPrePostscanDevice().getValue().getDiscreteValues().toArray(new String[0]);
					if (werte[0].equals(pos.getValue()))
						// Erster Eintrag ist gesetzt, Open anzeigen
						return "Open";
					else if (werte[1].equals(pos.getValue()))
						// Zweiter Eintrag ist gesetzt, Close anzeigen
						return "Close";
					else
						return "";
				}
				else
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
