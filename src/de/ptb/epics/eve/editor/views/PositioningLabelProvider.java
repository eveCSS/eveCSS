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

import de.ptb.epics.eve.data.scandescription.Positioning;

public class PositioningLabelProvider implements ITableLabelProvider {

	public Image getColumnImage( final Object positioning, final int colIndex ) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getColumnText( final Object positioning, final int colIndex ) {
		final Positioning pos = (Positioning)positioning;
		switch( colIndex ) {
			case 0:
				return (pos.getMotorAxis()!=null)?pos.getMotorAxis().getFullIdentifyer():"";
			case 1:
				return (pos.getPluginController().getPlugin()!=null)?pos.getPluginController().getPlugin().getName():"";
			case 2:
				return (pos.getDetectorChannel()!=null)?pos.getDetectorChannel().getFullIdentifyer():"";
			case 3:
				return (pos.getNormalization()!=null)?pos.getNormalization().getFullIdentifyer():"";
			case 4:
				return (pos.getPluginController().getPlugin()!=null)?pos.getPluginController().toString():"Choose a plugin to see options";
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
