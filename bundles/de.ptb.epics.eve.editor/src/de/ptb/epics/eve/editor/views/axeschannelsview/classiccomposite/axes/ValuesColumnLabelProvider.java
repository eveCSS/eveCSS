package de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import de.ptb.epics.eve.data.DataTypes;
import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.data.scandescription.PositionMode;
import de.ptb.epics.eve.data.scandescription.axismode.RangeMode;
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
			if (axis.getType().equals(DataTypes.DATETIME) && 
					axis.getPositionMode().equals(PositionMode.ABSOLUTE)) {
				SimpleDateFormat dateFormat = 
						new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
				StringBuilder sb = new StringBuilder();
				sb.append("Start: " + dateFormat.format((Date)axis.getStart()));
				sb.append("\n");
				sb.append("Stop: " + dateFormat.format((Date)axis.getStop()));
				sb.append("\n");
				sb.append("Stepwidth: " + new SimpleDateFormat("HH:mm:ss.SSS").
						format((Date)axis.getStepwidth()));
				return sb.toString();
			}
			if (axis.getType().equals(DataTypes.DATETIME) && 
					axis.getPositionMode().equals(PositionMode.RELATIVE)) {
				StringBuilder sb = new StringBuilder();
				sb.append("Start: " + axis.getStart());
				sb.append("\n");
				sb.append("Stop: " + axis.getStop());
				sb.append("\n");
				sb.append("Stepwidth: " + axis.getStepwidth());
				return sb.toString();
			}
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
			if (axis.getMode() != null) {
				String positions = ((RangeMode)axis.getMode()).getPositions();
				int count = positions.split(",").length;
				return positions + " (" + count + " positions)";
			}
			break;
		default:
			break;
		}
		return null;
	}
}
