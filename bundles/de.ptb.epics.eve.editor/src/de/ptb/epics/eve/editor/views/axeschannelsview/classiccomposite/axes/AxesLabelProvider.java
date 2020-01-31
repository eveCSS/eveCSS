package de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.data.scandescription.PositionMode;
import de.ptb.epics.eve.data.scandescription.Stepfunctions;
import de.ptb.epics.eve.data.scandescription.errors.AxisError;
import de.ptb.epics.eve.data.scandescription.errors.IModelError;

/**
 * @author Marcus Michalsky
 * @since 1.34
 */
public class AxesLabelProvider implements ITableLabelProvider {

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		// TODO check for memory leak
		if (columnIndex == 0) {
			return PlatformUI.getWorkbench().getSharedImages().getImage(
					ISharedImages.IMG_TOOL_DELETE);
		} else if (columnIndex == 5) {
			for (IModelError error : ((Axis) element).getModelErrors()) {
				if (error instanceof AxisError) {
					return PlatformUI.getWorkbench().getSharedImages().getImage(
							ISharedImages.IMG_OBJS_ERROR_TSK);
				}
			}
		}
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		Axis smAxis = (Axis) element;
		switch (columnIndex) {
		case 1: // name column
			return smAxis.getAbstractDevice().getName();
		case 2: // stepfunction column
			return smAxis.getStepfunction().toString();
		case 3: // main axis column
			if (!(Stepfunctions.ADD.equals(smAxis.getStepfunction()) || 
					Stepfunctions.MULTIPLY.equals(smAxis.getStepfunction()))) {
				return "n/a";
			}
			if (smAxis.isMainAxis()) {
				return "+";
			} else {
				return "";
			}
		case 4: // position mode column
			if (PositionMode.ABSOLUTE.equals(smAxis.getPositionMode())) {
				return "abs";
			} else {
				return "rel";
			}
		case 5: // value column
			return getValuesString(smAxis);
		case 6: // # points column
			if (smAxis.getMode().getPositionCount() == null) {
				return "n/a";
			} else {
				return smAxis.getMode().getPositionCount().toString();
			}
		}
		return null;
	}
	
	private String getValuesString(Axis axis) {
		switch (axis.getStepfunction()) {
		case ADD:
			return axis.getStart().toString() + '\u2192' +
					axis.getStop().toString() + " / " + 
					axis.getStepwidth().toString();
		case FILE:
			if (axis.getFile() != null && axis.getFile().getName() != null) {
				return axis.getFile().getName();
			}
			return "<path invalid>";
		case MULTIPLY:
			return axis.getStart().toString() + '\u2192' +
					axis.getStop().toString() + " / " + 
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
			return "--";
		}
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
	
	}

	@Override
	public void addListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub
	
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub
	
	}

}
