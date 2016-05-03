package de.ptb.epics.eve.editor.views.scanmoduleview.motoraxiscomposite;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.data.scandescription.Stepfunctions;
import de.ptb.epics.eve.data.scandescription.errors.AxisError;
import de.ptb.epics.eve.data.scandescription.errors.IModelError;

/**
 * <code>MotorAxisLabelProvider</code> is the label provider of the table viewer
 * defined in
 * {@link de.ptb.epics.eve.editor.views.scanmoduleview.motoraxiscomposite.MotorAxisComposite}
 * .
 * 
 * @author ?
 * @author Marcus Michalsky
 */
public class LabelProvider implements ITableLabelProvider {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Image getColumnImage(final Object axis, final int colIndex) {
		if (colIndex == 0) {
			return PlatformUI.getWorkbench().getSharedImages()
					.getImageDescriptor(ISharedImages.IMG_TOOL_DELETE)
					.createImage();
		} else if (colIndex == 2) {
			for (IModelError error : ((Axis) axis).getModelErrors()) {
				if (error instanceof AxisError) {
					return PlatformUI.getWorkbench().getSharedImages()
							.getImage(ISharedImages.IMG_OBJS_ERROR_TSK);
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
	public boolean isLabelProperty(final Object arg0, String arg1) {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addListener(final ILabelProviderListener arg0) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeListener(final ILabelProviderListener arg0) {
	}
}