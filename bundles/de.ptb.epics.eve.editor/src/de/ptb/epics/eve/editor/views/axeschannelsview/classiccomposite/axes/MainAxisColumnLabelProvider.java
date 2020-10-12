package de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes;

import org.eclipse.jface.viewers.ColumnLabelProvider;

import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.data.scandescription.Stepfunctions;
import de.ptb.epics.eve.editor.StringLabels;

/**
 * @author Marcus Michalsky
 * @since 1.34
 */
public class MainAxisColumnLabelProvider extends ColumnLabelProvider {
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getText(Object element) {
		Axis smAxis = (Axis) element;
		if (!(Stepfunctions.ADD.equals(smAxis.getStepfunction()) || 
				Stepfunctions.MULTIPLY.equals(smAxis.getStepfunction()))) {
			return StringLabels.NOT_AVAILABLE;
		}
		if (smAxis.isMainAxis()) {
			return StringLabels.HEAVY_CHECK_MARK;
		} else {
			return StringLabels.EMPTY;
		}
	}
}
