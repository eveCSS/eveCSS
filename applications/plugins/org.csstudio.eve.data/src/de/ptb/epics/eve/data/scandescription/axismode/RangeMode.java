package de.ptb.epics.eve.data.scandescription.axismode;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import de.ptb.epics.eve.data.DataTypes;
import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.data.scandescription.errors.AxisError;
import de.ptb.epics.eve.data.scandescription.errors.AxisErrorTypes;
import de.ptb.epics.eve.data.scandescription.errors.IModelError;
import de.ptb.epics.eve.util.io.StringUtil;
import de.ptb.epics.eve.util.math.range.BigDecimalRange;
import de.ptb.epics.eve.util.math.range.DoubleRange;
import de.ptb.epics.eve.util.math.range.IntegerRange;

/**
 * @author Marcus Michalsky
 * @since 1.28
 */
public class RangeMode extends AxisMode {
	public static final Logger LOGGER = 
			Logger.getLogger(RangeMode.class.getName());
	
	public static final String RANGE_PROP = "range";
	public static final String POSITIONS_PROP = "positions";
	
	private String range;
	private String positions;
	
	public RangeMode(Axis axis) {
		super(axis);
		this.range = null;
		this.positions = null;
	}
	
	/**
	 * @return the range
	 */
	public String getRange() {
		return range;
	}

	/**
	 * @param range the range to set
	 */
	public void setRange(String range) {
		String patternString;
		switch (this.axis.getType()) {
		case DOUBLE:
			patternString = DoubleRange.DOUBLE_RANGE_REPEATED_REGEXP;
			break;
		case INT:
			patternString = IntegerRange.INTEGER_RANGE_REPEATED_REGEXP;
			break;
		default:
			LOGGER.error("Axis is neither of type int nor double");
			return;
		}
		Pattern p = Pattern.compile(patternString);
		Matcher m = p.matcher(range);
		if (!m.matches()) {
			LOGGER.error("Expression is invalid!");
			return;
		}
		String oldValue = this.range;
		this.range = range;
		this.propertyChangeSupport.firePropertyChange(
				RangeMode.RANGE_PROP, oldValue, range);
		this.calculatePositions(range);
	}
	
	/**
	 * @return the positions
	 */
	public String getPositions() {
		return positions;
	}

	private void calculatePositions(String regexp) {
		DataTypes type = this.getAxis().getMotorAxis().getGoto().getType();
		if (type.equals(DataTypes.INT)) {
			List<IntegerRange> intRanges = new ArrayList<>();
			for (String s : regexp.split(",")) {
				intRanges.add(new IntegerRange(s.trim()));
			}
			this.positions = StringUtil.buildCommaSeparatedString(intRanges);
		} else if (type.equals(DataTypes.DOUBLE)) {
			/*
			List<DoubleRange> doubleRanges = new ArrayList<>();
			for (String s : regexp.split(",")) {
				doubleRanges.add(new DoubleRange(s.trim()));
			}
			this.positions = StringUtil.buildCommaSeparatedString(doubleRanges);
			*/
			List<BigDecimalRange> decimalRanges = new ArrayList<>();
			for (String s : regexp.split(",")) {
				decimalRanges.add(new BigDecimalRange(s.trim()));
			}
			NumberFormat numberFormat = DecimalFormat.getInstance(Locale.US);
			StringBuffer buffer = new StringBuffer();
			for (BigDecimalRange range : decimalRanges) {
				for (BigDecimal decimal : range.getValues()) {
					buffer.append(numberFormat.format(decimal));
					buffer.append(", ");
				}
			}
			this.positions = buffer.toString().substring(
					0, buffer.toString().lastIndexOf(","));
		} else {
			LOGGER.error("axis is neither of type int nor double" 
					+ " -> no positions calculated");
			this.positions = null;
		}
		this.propertyChangeSupport.firePropertyChange(
				RangeMode.POSITIONS_PROP, null, this.positions);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer getPositionCount() {
		if (range == null || positions == null) {
			return null;
		}
		return this.positions.split(",").length;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<IModelError> getModelErrors() {
		List<IModelError> errors = new ArrayList<IModelError>();
		if (this.range == null || this.range.isEmpty()) {
			errors.add(new AxisError(this.axis, 
					AxisErrorTypes.RANGE_NOT_SET));
		}
		return errors;
	}
}