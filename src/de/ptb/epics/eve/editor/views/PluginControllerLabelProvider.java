/*******************************************************************************
 * Copyright (c) 2001, 2008 Physikalisch Technische Bundesanstalt.
 * All rights reserved.
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package de.ptb.epics.eve.editor.views;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import de.ptb.epics.eve.data.scandescription.PluginController;
import de.ptb.epics.eve.data.scandescription.errors.IModelError;
import de.ptb.epics.eve.data.scandescription.errors.PluginError;
import de.ptb.epics.eve.data.scandescription.errors.PluginErrorTypes;

public class PluginControllerLabelProvider implements ITableLabelProvider {

	private PluginController pluginController;
		
	public Image getColumnImage( final Object entry, final int colIndex) {
		final List< IModelError > modelErrors = this.pluginController.getModelErrors();
		
		if( pluginController.getModelErrors().size() > 0 && colIndex == 1 ) {
			Map.Entry<String, String> entry2 = ((Map.Entry< String, String >)entry);
			final Iterator< IModelError > it = modelErrors.iterator();
			while( it.hasNext() ) {
				final IModelError modelError = it.next();
				if( modelError instanceof PluginError ) {
					final PluginError pluginError = (PluginError)modelError;
					if( pluginError.getPluginErrorType() == PluginErrorTypes.WRONG_VALUE && pluginError.getParameterName().equals( entry2.getKey() ) ) {
						return PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJS_ERROR_TSK );
					} else if( pluginError.getPluginErrorType() == PluginErrorTypes.MISSING_MANDATORY_PARAMETER && pluginError.getParameterName().equals( entry2.getKey() ) ) {
						return PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJS_ERROR_TSK );
					}
				}
			}
		}
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

	public void setPluginController( final PluginController pluginController ) {
		this.pluginController = pluginController;
	}

}
