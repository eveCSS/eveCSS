package de.ptb.epics.eve.editor.views.eventcomposite;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import de.ptb.epics.eve.data.ComparisonTypes;
import de.ptb.epics.eve.data.measuringstation.event.MonitorEvent;
import de.ptb.epics.eve.data.scandescription.ControlEvent;
import de.ptb.epics.eve.editor.StringLabels;

/**
 * <code>ControlEventLabelProvider</code>.
 * 
 * @author ?
 * @author Marcus Michalsky
 */
public class LabelProvider implements ITableLabelProvider {
	private static final Image DELETE_IMG = PlatformUI.getWorkbench().getSharedImages().
			getImage(ISharedImages.IMG_TOOL_DELETE);
	private static final Image ERROR_IMG = PlatformUI.getWorkbench().getSharedImages().
			getImage(ISharedImages.IMG_OBJS_ERROR_TSK);
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Image getColumnImage(Object controlEvent, int colIndex) {
		switch (colIndex) {
		case 0: return LabelProvider.DELETE_IMG;
		case 3: // limit column
			if (((ControlEvent) controlEvent).getModelErrors().size() > 0) {
				// errors present -> return error image
				return LabelProvider.ERROR_IMG;
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
			if (ce.getEvent() instanceof MonitorEvent) {
				returnValue = ComparisonTypes
						.typeToString(((ControlEvent) controlEvent).getLimit()
								.getComparison());
			} else {
				returnValue = StringLabels.LONG_DASH;
			}
			break;
		case 3: // Limit column
			if (ce.getEvent() instanceof MonitorEvent) {
				returnValue = ((ControlEvent) controlEvent).getLimit()
						.getValue();
			} else {
				returnValue = StringLabels.LONG_DASH;
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