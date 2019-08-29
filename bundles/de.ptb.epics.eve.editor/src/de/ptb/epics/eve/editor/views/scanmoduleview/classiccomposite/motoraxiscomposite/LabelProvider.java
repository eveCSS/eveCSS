package de.ptb.epics.eve.editor.views.scanmoduleview.classiccomposite.motoraxiscomposite;

import org.eclipse.swt.graphics.Image;

import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.data.scandescription.errors.AxisError;
import de.ptb.epics.eve.data.scandescription.errors.IModelError;
import de.ptb.epics.eve.editor.views.scanmoduleview.classiccomposite.ActionCompositeLabelProvider;

/**
 * <code>MotorAxisLabelProvider</code> is the label provider of the table viewer
 * defined in
 * {@link de.ptb.epics.eve.editor.views.scanmoduleview.classiccomposite.motoraxiscomposite.MotorAxisComposite}
 * .
 * 
 * @author ?
 * @author Marcus Michalsky
 */
public class LabelProvider extends ActionCompositeLabelProvider {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Image getColumnImage(final Object axis, final int colIndex) {
		if (colIndex == 0) {
			return getDeleteImage();
		} else if (colIndex == 2) {
			for (IModelError error : ((Axis) axis).getModelErrors()) {
				if (error instanceof AxisError) {
					return getErrorImage();
				}
			}
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getColumnText(final Object axis, final int colIndex) {
		Axis smAxis = (Axis) axis;
		switch (colIndex) {
		case 1: // name column
			String stepfunction = ((Axis)axis).getStepfunction().toString();
			if (((Axis)axis).isMainAxis()) {
				return ((Axis)axis).getAbstractDevice().getName() + " (" + stepfunction + ") " + (char) 171 + " Main Axis";
			}
			return ((Axis)axis).getAbstractDevice().getName() + " (" + stepfunction + ")";
		case 2: // value column
			switch (((Axis)axis).getStepfunction()) {
			case ADD:
				return smAxis.getStart().toString() + '\u2192' +
						smAxis.getStop().toString() + " / " + 
						smAxis.getStepwidth().toString();
			case FILE:
				if (smAxis.getFile() != null && smAxis.getFile().getName() != null) {
					return ((Axis)axis).getFile().getName();
				}
				return "<path invalid>";
			case MULTIPLY:
				return smAxis.getStart().toString() + '\u2192' +
						smAxis.getStop().toString() + " / " + 
						smAxis.getStepwidth().toString();
			case PLUGIN:
				if (((Axis)axis).getPluginController() == null || 
					((Axis)axis).getPluginController().getPlugin() == null) {
						return "Plugin";
				}
				return "Plugin (" + ((Axis)axis).getPluginController().
					getPlugin().getName() + ")";
			case POSITIONLIST:
				return ((Axis)axis).getPositionlist();
			case RANGE:
				return ((Axis)axis).getRange();
			default:
				return "--";
			}
		case 3: // #points column
			return ((Axis)axis).getMode().getPositionCount() == null 
				? "N/A"
				: ((Axis)axis).getMode().getPositionCount().toString();
		}
		return null;
	}
}