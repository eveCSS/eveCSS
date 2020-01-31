package de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes.range.ui;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.viewers.ICellEditorListener;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import de.ptb.epics.eve.data.DataTypes;
import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.util.io.StringUtil;
import de.ptb.epics.eve.util.math.range.BigDecimalRange;
import de.ptb.epics.eve.util.math.range.DoubleRange;
import de.ptb.epics.eve.util.math.range.IntegerRange;

/**
 * @author Marcus Michalsky
 * @since 1.34
 */
public class RangeTextCellEditor extends TextCellEditor {
	private FieldDecorationRegistry registry = 
			FieldDecorationRegistry.getDefault();
	private Image errorImage = registry.getFieldDecoration(
			FieldDecorationRegistry.DEC_ERROR).getImage();
	
	private final ControlDecoration decoration;
	private final Axis axis;
	
	public RangeTextCellEditor(Composite parent, final Axis axis) {
		super(parent, SWT.NONE);
		this.axis = axis;
		this.decoration = new ControlDecoration(getControl(), SWT.LEFT);
		this.setValidator(new ICellEditorValidator() {
			@Override
			public String isValid(Object value) {
				String patternString = "";
				switch (axis.getType()) {
				case DOUBLE:
					patternString = DoubleRange.DOUBLE_RANGE_REPEATED_REGEXP;
					break;
				case INT:
					patternString = IntegerRange.INTEGER_RANGE_REPEATED_REGEXP;
					break;
				default:
					return "Data types other than int or double are not supported!";
				}
				Pattern p = Pattern.compile(patternString);
				Matcher m = p.matcher((String)value);
				if (!m.matches()) {
					return "expression is invalid";
				}
				return null;
			}
		});
		this.addListener(new ICellEditorListener() {
			@Override
			public void editorValueChanged(boolean oldValidState, boolean newValidState) {
				if (!newValidState) {
					decoration.setDescriptionText(getErrorMessage());
					decoration.setImage(errorImage);
					decoration.show();
					
					getControl().setToolTipText("");
				} else {
					decoration.setDescriptionText("");
					decoration.setImage(null);
					decoration.hide();
					
					setTooltip();
				}
			}
			
			@Override
			public void cancelEditor() {
				// nothing to do
			}
			
			@Override
			public void applyEditorValue() {
				// nothing to do
			}
		});
	}
	
	public void setTooltip() {
		String positionList = getPositionlist(((Text)getControl()).
				getText(), axis);
		int length = positionList.split(",").length;
		getControl().setToolTipText(positionList + 
				" (" + length + " positions)");
	}
	
	/*
	 * duplicate code from Model (AxisMode RangeMode). AxisMode is a delegate of
	 * Axis but AxisModes themselves were used with data binding in former views.
	 * never intended to expose this function (prior to 1.34).
	 */
	private String getPositionlist(String expression, Axis axis) {
		DataTypes type = axis.getMotorAxis().getGoto().getType();
		if (type.equals(DataTypes.INT)) {
			List<IntegerRange> intRanges = new ArrayList<>();
			for (String s : expression.split(",")) {
				intRanges.add(new IntegerRange(s.trim()));
			}
			return StringUtil.buildCommaSeparatedString(intRanges);
		} else if (type.equals(DataTypes.DOUBLE)) {
			List<BigDecimalRange> decimalRanges = new ArrayList<>();
			for (String s : expression.split(",")) {
				decimalRanges.add(new BigDecimalRange(s.trim()));
			}
			StringBuilder buffer = new StringBuilder();
			for (BigDecimalRange decimalRange : decimalRanges) {
				for (BigDecimal decimal : decimalRange.getValues()) {
					buffer.append(decimal);
					buffer.append(", ");
				}
			}
			return buffer.toString().substring(
					0, buffer.toString().lastIndexOf(','));
		} else {
			return "axis is neither of type int nor double" 
					+ " -> no positions calculated";
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void activate() {
		setTooltip();
		super.activate();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void focusLost() {
		if (isActivated()) {
			fireCancelEditor();
		}
		deactivate();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void handleDefaultSelection(SelectionEvent event) {
		if (!isValueValid()) {
			event.doit = false;
			return;
		}
		super.handleDefaultSelection(event);
	}
}
