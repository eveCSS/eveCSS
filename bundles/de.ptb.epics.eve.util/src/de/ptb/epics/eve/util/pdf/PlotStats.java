package de.ptb.epics.eve.util.pdf;

import de.ptb.epics.eve.util.data.Pair;

/**
 * Bean which summarizes plot statistics
 * 
 * @author Marcus Michalsky
 * @since 1.16
 */
public class PlotStats {
	private String detectorName;
	private String motorName;

	private Pair<String,String> minimum;
	private Pair<String,String> maximum;
	private Pair<String,String> center;
	private Pair<String,String> edge;
	private Pair<String,String> average;
	private Pair<String,String> deviation;
	private Pair<String,String> fullWidthHalfMinimum;
	
	private static final String EMPTY_STRING = "-";
	
	/**
	 * 
	 */
	public PlotStats() {
		this.detectorName = PlotStats.EMPTY_STRING;
		this.motorName = PlotStats.EMPTY_STRING;
		
		this.minimum = new Pair<String, String>(PlotStats.EMPTY_STRING,
				PlotStats.EMPTY_STRING);
		this.maximum = new Pair<String, String>(PlotStats.EMPTY_STRING,
				PlotStats.EMPTY_STRING);
		this.center = new Pair<String, String>(PlotStats.EMPTY_STRING,
				PlotStats.EMPTY_STRING);
		this.edge = new Pair<String, String>(PlotStats.EMPTY_STRING,
				PlotStats.EMPTY_STRING);
		this.average = new Pair<String, String>(PlotStats.EMPTY_STRING,
				PlotStats.EMPTY_STRING);
		this.deviation = new Pair<String, String>(PlotStats.EMPTY_STRING,
				PlotStats.EMPTY_STRING);
		this.fullWidthHalfMinimum = new Pair<String, String>(
				PlotStats.EMPTY_STRING, PlotStats.EMPTY_STRING);
	}
	
	/**
	 * @return the detectorId
	 */
	public String getDetectorName() {
		return (detectorName == null) ? "" : detectorName;
	}

	/**
	 * @param detectorName
	 *            the detectorName to set
	 */
	public void setDetectorName(String detectorName) {
		this.detectorName = detectorName;
	}

	/**
	 * @return the motorId
	 */
	public String getMotorName() {
		return (motorName == null) ? "" : motorName;
	}

	/**
	 * @param motorName
	 *            the motorName to set
	 */
	public void setMotorName(String motorName) {
		this.motorName = motorName;
	}

	/**
	 * @return the minimum
	 */
	public Pair<String, String> getMinimum() {
		return minimum;
	}

	/**
	 * @param minimum the minimum to set
	 */
	public void setMinimum(Pair<String, String> minimum) {
		this.minimum = minimum;
	}

	/**
	 * @return the maximum
	 */
	public Pair<String, String> getMaximum() {
		return maximum;
	}

	/**
	 * @param maximum the maximum to set
	 */
	public void setMaximum(Pair<String, String> maximum) {
		this.maximum = maximum;
	}

	/**
	 * @return the center
	 */
	public Pair<String, String> getCenter() {
		return center;
	}

	/**
	 * @param center the center to set
	 */
	public void setCenter(Pair<String, String> center) {
		this.center = center;
	}

	/**
	 * @return the edge
	 */
	public Pair<String, String> getEdge() {
		return edge;
	}

	/**
	 * @param edge the edge to set
	 */
	public void setEdge(Pair<String, String> edge) {
		this.edge = edge;
	}

	/**
	 * @return the average
	 */
	public Pair<String, String> getAverage() {
		return average;
	}

	/**
	 * @param average the average to set
	 */
	public void setAverage(Pair<String, String> average) {
		this.average = average;
	}

	/**
	 * @return the deviation
	 */
	public Pair<String, String> getDeviation() {
		return deviation;
	}

	/**
	 * @param deviation the deviation to set
	 */
	public void setDeviation(Pair<String, String> deviation) {
		this.deviation = deviation;
	}

	/**
	 * @return the fullWidthHalfMinimum
	 */
	public Pair<String, String> getFullWidthHalfMinimum() {
		return fullWidthHalfMinimum;
	}

	/**
	 * @param fullWidthHalfMinimum the fullWidthHalfMinimum to set
	 */
	public void setFullWidthHalfMinimum(
			Pair<String, String> fullWidthHalfMinimum) {
		this.fullWidthHalfMinimum = fullWidthHalfMinimum;
	}
}