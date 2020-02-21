package de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.data.scandescription.errors.AxisError;
import de.ptb.epics.eve.data.scandescription.errors.IModelError;

/**
 * @author Marcus Michalsky
 * @since 1.34
 */
public class ValuesColumnLabelProvider extends ColumnLabelProvider {
	private static final String RIGHT_ARROW = Character.toString('\u2192');
	private static final String SLASH_WITH_SPACE = " / ";
	private static final String LONG_DASH = Character.toString('\u2014');
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Image getImage(Object element) {
		for (IModelError error : ((Axis) element).getModelErrors()) {
			if (error instanceof AxisError) {
				return PlatformUI.getWorkbench().getSharedImages().getImage(
						ISharedImages.IMG_OBJS_ERROR_TSK);
			}
		}
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
		public String getText(Object element) {
			return getValuesString((Axis)element);
		}
	
	private String getValuesString(Axis axis) {
		switch (axis.getStepfunction()) {
		case ADD:
			return axis.getStart().toString() + RIGHT_ARROW +
					axis.getStop().toString() + SLASH_WITH_SPACE + 
					axis.getStepwidth().toString();
		case FILE:
			if (axis.getFile() != null && axis.getFile().getName() != null) {
				return axis.getFile().getName();
			}
			return "<path invalid>";
		case MULTIPLY:
			return axis.getStart().toString() + RIGHT_ARROW +
					axis.getStop().toString() + SLASH_WITH_SPACE + 
					axis.getStepwidth().toString();
		case PLUGIN:
			if (axis.getPluginController() == null || 
				axis.getPluginController().getPlugin() == null) {
					return "Plugin";
			}
			return "Plugin (" + axis.getPluginController().
				getPlugin().getName() + ")";
		case POSITIONLIST:
			return axis.getPositionlist();
		case RANGE:
			return axis.getRange();
		default:
			return LONG_DASH;
		}
	}
}
