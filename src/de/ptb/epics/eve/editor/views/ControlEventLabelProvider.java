package de.ptb.epics.eve.editor.views;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import de.ptb.epics.eve.data.EventTypes;
import de.ptb.epics.eve.data.ComparisonTypes;
import de.ptb.epics.eve.data.scandescription.ControlEvent;
import de.ptb.epics.eve.data.scandescription.PauseEvent;

/**
 * <code>ControlEventLabelProvider</code>.
 * 
 * @author ?
 * @author Marcus Michalsky
 */
public class ControlEventLabelProvider implements ITableLabelProvider {
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Image getColumnImage(Object controlEvent, int colIndex) {
		
		switch(colIndex) {
			
			// limit column...
			case 2:	if(((ControlEvent)controlEvent).getModelErrors().size() > 0) 
					{
						// errors present -> return error image
						return PlatformUI.getWorkbench().getSharedImages().
									getImage( ISharedImages.IMG_OBJS_ERROR_TSK);
					}
					break;
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getColumnText(Object controlEvent, int colIndex) {
		
		String returnValue = "";
		
		switch(colIndex) {
		
			// Source column
			case 0:
				returnValue = ((ControlEvent)controlEvent).getEvent().getName();
				break;
				
			// Operator column
			case 1:
				if(((ControlEvent)controlEvent).getEvent().getType() 
				   == EventTypes.MONITOR) 
				{
					returnValue = ComparisonTypes.typeToString(
						((ControlEvent)controlEvent).getLimit().getComparison());
				}
				break;
				
			// Limit column
			case 2:
				if(((ControlEvent)controlEvent).getEvent().getType() 
				   == EventTypes.MONITOR) 
				{
					returnValue = 
						((ControlEvent)controlEvent).getLimit().getValue();
				}
				break;
				
			// CIF column
			case 3:
				if(((ControlEvent)controlEvent).getEvent().getType() 
				   == EventTypes.MONITOR) 
				{
					returnValue = 
						"" + ((PauseEvent)controlEvent).isContinueIfFalse();
				}
				break;
		}
		return returnValue;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isLabelProperty(Object arg0, String arg1) {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addListener(ILabelProviderListener arg0) {
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeListener(ILabelProviderListener arg0) {
	}
}