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

import de.ptb.epics.eve.data.EventTypes;
import de.ptb.epics.eve.data.ComparisonTypes;
import de.ptb.epics.eve.data.scandescription.errorhandling.ModelError;
import de.ptb.epics.eve.data.scandescription.errorhandling.reasons.TypeValueMismatch;
import de.ptb.epics.eve.data.scandescription.ControlEvent;
import de.ptb.epics.eve.data.scandescription.PauseEvent;

public class ControlEventLabelProvider implements ITableLabelProvider {
	
	public Image getColumnImage( Object controlEvent, int colIndex ) {
		
		switch( colIndex ) {
			case 2:
				Iterator<ModelError> it = ((ControlEvent)controlEvent).getFullErrorList().iterator();
				while( it.hasNext() ) {
					if( it.next().getReason() instanceof TypeValueMismatch ) {
						return PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJS_ERROR_TSK );
					}
				}
				break;
		
		}
		
		return null;
	}

	public String getColumnText(  Object controlEvent, int colIndex ) {
		
		String returnValue = "";
		switch( colIndex ) {
			case 0:
				returnValue = ((ControlEvent)controlEvent).getEvent().getName();
				break;
				
			case 1:
				if( ((ControlEvent)controlEvent).getEvent().getType() != EventTypes.SCHEDULE )  {
					returnValue = ComparisonTypes.typeToString( ((ControlEvent)controlEvent).getLimit().getComparison() );
				}
				break;
				
			case 2:
				if( ((ControlEvent)controlEvent).getEvent().getType() != EventTypes.SCHEDULE )  {
					returnValue = ((ControlEvent)controlEvent).getLimit().getValue();
				}
				break;
				
			case 3:
				if( ((ControlEvent)controlEvent).getEvent().getType() != EventTypes.SCHEDULE )  {
					returnValue = "" + ((PauseEvent)controlEvent).isContinueIfFalse();
				}
				break;
		}
		
		return returnValue;
	}

	public void addListener(ILabelProviderListener arg0) {
		// TODO Auto-generated method stub
		
	}

	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	public boolean isLabelProperty(Object arg0, String arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	public void removeListener(ILabelProviderListener arg0) {
		// TODO Auto-generated method stub
		
	}

}
