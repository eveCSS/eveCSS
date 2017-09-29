package de.ptb.epics.eve.util.math.geometry.convexhull;

import java.awt.Point;
import java.awt.Polygon;
import java.util.List;

/**
 * Strategy Pattern interface.
 * 
 * @author Marcus Michalsky
 * @since 1.19
 */
public interface ConvexHullStrategy {
	
	/**
	 * Computes the Convex Hull.
	 * 
	 * @param points a list of at least three points
	 * @return the convex hull as a polygon
	 */
	Polygon compute(List<Point> points);
}