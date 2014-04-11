package de.ptb.epics.eve.util.math.geometry;

import java.awt.Point;
import java.util.List;

/**
 * A java.awt.Point with Polar Coordinates which is comparable.
 * 
 * @author Marcus Michalsky
 * æ@since 1.19
 */
public class PolarPoint extends Point implements Comparable<PolarPoint> {

	/**
	 * Constructor.
	 * 
	 * @param x cartesian x
	 * @param y cartesian y
	 */
	public PolarPoint(Integer x, Integer y) {
		super(x, y);
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Returns the r component of the polar coordinate.
	 * 
	 * @return the r component of the polar coordinate
	 */
	public double getR() {
		return Math.sqrt(this.x^2 + this.y^2);
	}
	
	/**
	 * Returns the phi component of the polar coordiante.
	 * 
	 * @return the phi component of the polar coordinate
	 */
	public double getPhi() {
		return Math.atan2(this.y, this.x);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @param other
	 * @return
	 */
	@Override
	public int compareTo(PolarPoint other) {
		if (this.getPhi() < other.getPhi()) {
			return -1;
		}
		if (this.getPhi() > other.getPhi()) {
			return 1;
		}
		return 0;
	}
	
	/**
	 * Returns the polar point (r, phi) from the list where 
	 * for all other p_i = (r_i, phi_i) in the list : r > r_i.
	 * 
	 * @param points the list of points
	 * @return the point with the maximum r
	 */
	public static PolarPoint getMaxR(List<PolarPoint> points) {
		if (points.size() == 0) {
			throw new IllegalArgumentException("empty list");
		}
		if (points.size() == 1) {
			return points.get(0);
		}
		PolarPoint rMax = points.get(0);
		for (PolarPoint p : points) {
			if (p.getR() > rMax.getR()) {
				rMax = p;
			}
		}
		return rMax;
	}
	
	/**
	 * Returns the y value of the topmost point (the one with the lowest y).
	 * 
	 * @param points the points to be considered
	 * @return the y value of the topmost point
	 */
	public static int getTopmostY(List<PolarPoint> points) {
		int y = Integer.MAX_VALUE;
		for (PolarPoint point : points) {
			if (point.y < y) {
				y = point.y;
			}
		}
		return y;
	}
	
	/**
	 * Computes the Cross Product of two points.
	 * 
	 * @param p1 point one
	 * @param p2 point two
	 * @return the cross product of the two points
	 */
	public static Integer CrossProduct(Point p1, Point p2) {
		return (p1.x * p2.y - p2.x * p1.y);
	}
}