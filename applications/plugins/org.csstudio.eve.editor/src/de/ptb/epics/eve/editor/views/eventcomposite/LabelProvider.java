package de.ptb.epics.eve.editor.views.eventcomposite;

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
public class LabelProvider implements ITableLabelProvider {
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Image getColumnImage(Object controlEvent, int colIndex) {
		switch (colIndex) {
		case 0: return PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_TOOL_DELETE)
				.createImage();
		case 3: // limit column
			if (((ControlEvent) controlEvent).getModelErrors().size() > 0) {
				// errors present -> return error image
				return PlatformUI.getWorkbench().getSharedImages()
						.getImage(ISharedImages.IMG_OBJS_ERROR_TSK);
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
		String returnValue = null;
		ControlEvent ce = (ControlEvent)controlEvent;
		switch (colIndex) {
		case 1: // Source column
			returnValue = ce.getEvent().getName();
			break;
		case 2: // Operator column
			if (ce.getEvent().getType() == EventTypes.MONITOR) {
				returnValue = ComparisonTypes
						.typeToString(((ControlEvent) controlEvent).getLimit()
								.getComparison());
			} else {
				returnValue = "---";
			}
			break;
		case 3: // Limit column
			if (ce.getEvent().getType() == EventTypes.MONITOR) {
				returnValue = ((ControlEvent) controlEvent).getLimit()
						.getValue();
			} else {
				returnValue = "---";
			}
			break;
		case 4: // Action column
			returnValue = ((PauseEvent)ce).getEventAction().toString();
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