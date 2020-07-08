package de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes;

import org.eclipse.jface.viewers.ColumnLabelProvider;

import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.data.scandescription.Stepfunctions;

/**
 * @author Marcus Michalsky
 * @since 1.34
 */
public class MainAxisColumnLabelProvider extends ColumnLabelProvider {
	private static final String HEAVY_CHECK_MARK = Character.toString('\u2714');
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getText(Object element) {
		Axis smAxis = (Axis) element;
		if (!(Stepfunctions.ADD.equals(smAxis.getStepfunction()) || 
				Stepfunctions.MULTIPLY.equals(smAxis.getStepfunction()))) {
			return "n/a";
		}
		if (smAxis.isMainAxis()) {
			return HEAVY_CHECK_MARK;
		} else {
			return "";
		}
	}
}
