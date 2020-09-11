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

/**
 * @author Marcus Michalsky
 * @since 1.35
 */
public class YAxisLabelProvider extends StyledCellLabelProvider {
	private static final String LONG_DASH = "\u2014";
	private static final String MULTIPLY_SIGN = "\u00D7";
	
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
			cell.setText(LONG_DASH);
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
	private String getText(YAxis yAxis) {
		StringBuilder sb = new StringBuilder();
		sb.append(" ");
		sb.append(this.getPointStyleText(yAxis));
		sb.append(" ");
		sb.append(this.getLineStyleText(yAxis));
		sb.append(" ");
		
		if (yAxis.getModifier().equals(YAxisModifier.INVERSE)) {
			sb.append(" -1 " + MULTIPLY_SIGN + " ");
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
			return "|";
		case CIRCLE:
			return "\u25CB";
		case CROSS:
			return "+";
		case DIAMOND:
			return "\u25C7";
		case FILLED_DIAMOND:
			return "\u25C6";
		case FILLED_SQUARE:
			return "\u25A0";
		case FILLED_TRIANGLE:
			return "\u25B2";
		case NONE:
			return " ";
		case POINT:
			return "\u25CF";
		case SQUARE:
			return "\u25A1";
		case TRIANGLE:
			return "\u25B3";
		case XCROSS:
			return "\u00D7";
		default:
			return " ";
		}
	}

	// in order to correctly color the text, must return exactly one character
	private String getLineStyleText(YAxis yAxis) {
		switch (yAxis.getLinestyle()) {
		case AREA:
			return "\u25AE";
		case BAR:
			return "\u2502";
		case DASH_LINE:
			return "\u2504";
		case POINT:
			return " ";
		case SOLID_LINE:
			return "\u2500";
		case STEP_HORIZONTALLY:
			return "\u2500";
		case STEP_VERTICALLY:
			return "\u2500";
		default:
			return " ";
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
