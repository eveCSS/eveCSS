package de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes;

import java.util.Map;
import java.util.Map.Entry;

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
		return ValuesColumnStringFormatter.getValuesString((Axis)element);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getToolTipText(Object element) {
		Axis axis = (Axis)element;
		switch (axis.getStepfunction()) {
		case ADD:
		case MULTIPLY:
			break;
		case FILE:
			if (axis.getFile() != null) {
				return axis.getFile().getAbsolutePath();
			}
			break;
		case PLUGIN:
			if (axis.getPluginController() != null && 
					axis.getPluginController().getPlugin() != null) {
				StringBuilder sb = new StringBuilder();
				sb.append("Plugin '" + 
						axis.getPluginController().getPlugin().getName() +
						"'");
				Map<String,String> parameters = 
						axis.getPluginController().getValues();
				if (!parameters.isEmpty()) {
					sb.append(", Parameters:");
				} else {
					sb.append(" (no parameters)");
				}
				sb.append("\n");
				for (Entry<String,String> entry : parameters.entrySet()) {
					sb.append(entry.getKey() + ": " + entry.getValue() + "\n");
				}
				return sb.toString().substring(0, sb.toString().length()-1);
			}
			break;
		case POSITIONLIST:
			break;
		case RANGE:
			break;
		default:
			break;
		}
		return null;
	}
}
