package de.ptb.epics.eve.editor.views.prepostposplotview.classiccomposite.plot.ui;

import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import de.ptb.epics.eve.data.PlotModes;
import de.ptb.epics.eve.data.scandescription.PlotWindow;
import de.ptb.epics.eve.data.scandescription.YAxis;
import de.ptb.epics.eve.data.scandescription.YAxisModifier;
import de.ptb.epics.eve.data.scandescription.errors.IModelError;
import de.ptb.epics.eve.data.scandescription.errors.PlotWindowError;
import de.ptb.epics.eve.data.scandescription.errors.PlotWindowErrorTypes;
import de.ptb.epics.eve.editor.StringLabels;

/**
 * @author Marcus Michalsky
 * @since 1.35
 */
public class YAxisLabelProvider extends StyledCellLabelProvider {
	private static final Image ERROR_IMG = PlatformUI.getWorkbench().
			getSharedImages().getImage(ISharedImages.IMG_OBJS_ERROR_TSK);
	
	private int yAxisNumber;
	
	/**
	 * @param yAxisNumber the number of the yAxis (0 or 1)
	 * @throws IllegalArgumentException if yAxis is not equal 0 or 1
	 */
	public YAxisLabelProvider(int yAxisNumber) {
		if (!(yAxisNumber == 0 || yAxisNumber == 1)) {
			throw new IllegalArgumentException("yAxis must be 0 or 1");
		}
		this.yAxisNumber = yAxisNumber;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void update(ViewerCell cell) {
		PlotWindow plotWindow = (PlotWindow)cell.getElement();
		if (!(this.yAxisNumber == 0 && plotWindow.getYAxes().isEmpty() ||
				this.yAxisNumber == 1 && plotWindow.getYAxes().size() < 2)) {
			YAxis yAxis = plotWindow.getYAxes().get(yAxisNumber);
			cell.setText(getText(yAxis));
			StyleRange myStyledRange = new StyleRange(0, 4, new Color(
					cell.getControl().getDisplay(), yAxis.getColor()), null);
			StyleRange[] range = {myStyledRange};
			cell.setStyleRanges(range);
		} else {
			cell.setText(StringLabels.LONG_DASH);
			cell.setStyleRanges(null);
		}
		if (!plotWindow.getModelErrors().isEmpty() && this.yAxisNumber == 0) {
			for (IModelError error : plotWindow.getModelErrors()) {
				PlotWindowError plotError = (PlotWindowError)error;
				if (plotError.getErrorType().equals(
						PlotWindowErrorTypes.NO_Y_AXIS_SET)) {
					cell.setImage(ERROR_IMG);
				}
			}
		} else {
			cell.setImage(null);
		}
		super.update(cell);
	}
	
	/*
	 * builds the label text from
	 *  - Unicode Sign for Point Style
	 *  - Unicode Sign for Mark Style
	 *  - "-1 x " if axis modifier is INVERSE
	 *  - Detector Name
	 *  - " / " Normalize Name (if any)
	 *  - " (log)" if Plot Mode is LOG
	 *  and colors Point Style and Mark Style in selected Plot color
	 */
	public String getText(YAxis yAxis) {
		StringBuilder sb = new StringBuilder();
		sb.append(" ");
		sb.append(this.getPointStyleText(yAxis));
		sb.append(" ");
		sb.append(this.getLineStyleText(yAxis));
		sb.append(" ");
		
		if (yAxis.getModifier().equals(YAxisModifier.INVERSE)) {
			sb.append(" -1 " + StringLabels.MULTIPLY_SIGN + " ");
		}
		sb.append(yAxis.getNormalizedName());
		if (yAxis.getMode().equals(PlotModes.LOG)) {
			sb.append(" (log)");
		}
		return sb.toString();
	}
	
	// in order to correctly color the text, must return exactly one character
	private String getPointStyleText(YAxis yAxis) {
		switch (yAxis.getMarkstyle()) {
		case BAR:
			return StringLabels.BAR;
		case CIRCLE:
			return StringLabels.CIRCLE;
		case CROSS:
			return StringLabels.CROSS;
		case DIAMOND:
			return StringLabels.DIAMOND;
		case FILLED_DIAMOND:
			return StringLabels.FILLED_DIAMOND;
		case FILLED_SQUARE:
			return StringLabels.FILLED_SQUARE;
		case FILLED_TRIANGLE:
			return StringLabels.FILLED_TRIANGLE;
		case NONE:
			return StringLabels.SPACE;
		case POINT:
			return StringLabels.POINT;
		case SQUARE:
			return StringLabels.SQUARE;
		case TRIANGLE:
			return StringLabels.TRIANGLE;
		case XCROSS:
			return StringLabels.XCROSS;
		default:
			return StringLabels.SPACE;
		}
	}

	// in order to correctly color the text, must return exactly one character
	private String getLineStyleText(YAxis yAxis) {
		switch (yAxis.getLinestyle()) {
		case AREA:
			return StringLabels.BOLD_BAR;
		case BAR:
			return StringLabels.LONG_BAR;
		case DASH_LINE:
			return StringLabels.DASH_LINE;
		case POINT:
			return StringLabels.SPACE;
		case SOLID_LINE:
			return StringLabels.SOLID_LINE;
		case STEP_HORIZONTALLY:
			return StringLabels.SOLID_LINE;
		case STEP_VERTICALLY:
			return StringLabels.SOLID_LINE;
		default:
			return StringLabels.SPACE;
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getToolTipText(Object element) {
		PlotWindow plotWindow = (PlotWindow)element;
		if (!plotWindow.getModelErrors().isEmpty() && this.yAxisNumber == 0) {
			for (IModelError error : plotWindow.getModelErrors()) {
				PlotWindowError plotError = (PlotWindowError)error;
				if (plotError.getErrorType().equals(
						PlotWindowErrorTypes.NO_Y_AXIS_SET)) {
					return "At least one y axis has to be set.";
				}
			}
		}
		return null;
	}
}
