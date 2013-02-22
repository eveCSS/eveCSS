package de.ptb.epics.eve.util.math.statistics;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/**
 * @author Marcus Michalsky
 * @since 1.10
 */
public class DescriptiveStats {
	private List<Double> sample;
	private Double mean;
	private Double minimum;
	private Double maximum;
	private Double deviation;
	private Double median;
	private long size;
	
	/**
	 * Creates a descriptive statistics object of a given sample. Note that 
	 * calculation must be triggered manually by {@link #calculateStats()}.
	 * 
	 * @param sample the sample
	 */
	public DescriptiveStats(List<Double> sample) {
		this.sample = sample;
		this.mean = null;
		this.minimum = null;
		this.maximum = null;
		this.deviation = null;
		this.median = null;
		this.size = 0;
	}
	
	/**
	 * Creates a descriptive statistics object. Used for non-numeric samples 
	 * where just the sample size is important but the same data structure 
	 * should be used.
	 * Do not call {@link #calculateStats()} when creating the object this way!
	 * 
	 * @param sampleSize the sample size
	 */
	public DescriptiveStats(int sampleSize) {
		this.sample = new ArrayList<Double>();
		this.mean = null;
		this.minimum = null;
		this.maximum = null;
		this.deviation = null;
		this.median = null;
		this.size = sampleSize;
	}
	
	/**
	 * For performance reasons the calculation is not triggered during 
	 * object creation but has to be triggered manually here.
	 */
	public void calculateStats() {
		DescriptiveStatistics stats = new DescriptiveStatistics();
		
		for (double d : this.sample) {
			stats.addValue(d);
		}
		this.mean = stats.getMean();
		this.minimum = stats.getMin();
		this.maximum = stats.getMax();
		this.deviation = stats.getStandardDeviation();
		this.median = stats.getPercentile(50);
		this.size = stats.getN();
	}

	/**
	 * @return the mean
	 */
	public Double getMean() {
		return mean;
	}

	/**
	 * @return the minimum
	 */
	public Double getMinimum() {
		return minimum;
	}

	/**
	 * @return the maximum
	 */
	public Double getMaximum() {
		return maximum;
	}

	/**
	 * @return the deviation
	 */
	public Double getDeviation() {
		return deviation;
	}

	/**
	 * @return the median
	 */
	public Double getMedian() {
		return median;
	}
	
	/**
	 * @return the sample size
	 */
	public Long getSampleSize() {
		return this.size;
	}
}