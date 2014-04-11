package de.ptb.epics.eve.util.math.geometry;

import java.awt.Point;
import java.awt.Polygon;
import java.util.List;

import de.ptb.epics.eve.util.math.geometry.convexhull.BoundingBoxConvexHullStrategy;
import de.ptb.epics.eve.util.math.geometry.convexhull.ConvexHullStrategy;

/**
 * @author Marcus Michalsky
 * @since 1.19
 */
public class Geometry {
	
	/**
	 * Computes the convex hull of the given points (at least three).
	 * 
	 * @param points the points the hull should be computed from
	 * @return the convex hull of the given points
	 */
	public static Polygon convexHull(List<Point> points) {
		ConvexHullStrategy strategy = new BoundingBoxConvexHullStrategy();
				// new GrahamsScanConvexHullStrategy();
		return strategy.compute(points);
	}
}