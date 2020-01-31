package de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes.file;

import java.util.Collections;
import java.util.List;

/**
 * @author Marcus Michalsky
 * @since 1.34
 */
public class FileStats {
	private int valueCount;
	private double firstValue;
	private double lastValue;
	private double minimum;
	private double maximum;
	
	public FileStats(List<Double> values) {
		this.valueCount = values.size();
		this.firstValue = values.get(0);
		this.lastValue = values.get(values.size()-1);
		Collections.sort(values);
		this.minimum = values.get(0);
		this.maximum = values.get(values.size()-1);
	}

	public String getValueCount() {
		return Integer.toString(valueCount);
	}

	public String getFirstValue() {
		return Double.toString(firstValue);
	}

	public String getLastValue() {
		return Double.toString(lastValue);
	}

	public String getMinimum() {
		return Double.toString(minimum);
	}

	public String getMaximum() {
		return Double.toString(maximum);
	}
}
