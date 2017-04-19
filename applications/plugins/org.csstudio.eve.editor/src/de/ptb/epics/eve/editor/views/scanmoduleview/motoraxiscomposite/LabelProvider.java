package de.ptb.epics.eve.editor.views.scanmoduleview.motoraxiscomposite;
import org.eclipse.swt.graphics.Image;

import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.data.scandescription.Stepfunctions;
import de.ptb.epics.eve.data.scandescription.errors.AxisError;
import de.ptb.epics.eve.data.scandescription.errors.IModelError;
import de.ptb.epics.eve.editor.views.scanmoduleview.ActionCompositeLabelProvider;

/**
 * <code>MotorAxisLabelProvider</code> is the label provider of the table viewer
 * defined in
 * {@link de.ptb.epics.eve.editor.views.scanmoduleview.motoraxiscomposite.MotorAxisComposite}
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
		switch (colIndex) {
		case 1:
			if (((Axis)axis).isMainAxis()) {
				return ((Axis)axis).getAbstractDevice().getName() + " " + (char) 171 + " Main Axis";
			}
			return ((Axis)axis).getAbstractDevice().getName();
		case 2:
			if (Stepfunctions.PLUGIN.equals(((Axis)axis).getStepfunction())) {
				if (((Axis)axis).getPluginController() == null || 
					((Axis)axis).getPluginController().getPlugin() == null) {
						return "Plugin";
				}
				return "Plugin (" + ((Axis)axis).getPluginController().
						getPlugin().getName() + ")";
			}
			return ((Axis)axis).getStepfunction().toString();
		case 3:
			return ((Axis)axis).getMode().getPositionCount() == null 
				? "N/A"
				: ((Axis)axis).getMode().getPositionCount().toString();
		}
		return null;
	}
}