/*******************************************************************************
 * Copyright (c) 2001, 2008 Physikalisch Technische Bundesanstalt.
 * All rights reserved.
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package de.ptb.epics.eve.editor.views;

import java.util.Map;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

public class PluginControllerLabelProvider implements ITableLabelProvider {

	public Image getColumnImage( final Object entry, final int colIndex) {
		return null;
	}

	public String getColumnText( final Object entry, final int colIndex ) {
		String returnValue = "";
		Map.Entry<String, String> entry2 = ((Map.Entry< String, String >)entry);
		switch( colIndex ) {
			case 0:
				returnValue = entry2.getKey();
				break;
			case 1:
				returnValue = entry2.getValue();
				break;
		}
		return returnValue;
	}

	public void addListener( final ILabelProviderListener arg0 ) {
		// TODO Auto-generated method stub

	}

	public void dispose() {
		// TODO Auto-generated method stub

	}

	public boolean isLabelProperty( final Object arg0, final String arg1 ) {
		// TODO Auto-generated method stub
		return false;
	}

	public void removeListener( final ILabelProviderListener arg0 ) {
		// TODO Auto-generated method stub

	}

}
