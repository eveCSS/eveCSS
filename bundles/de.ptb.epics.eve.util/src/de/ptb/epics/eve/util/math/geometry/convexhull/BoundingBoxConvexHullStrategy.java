package de.ptb.epics.eve.util.math.geometry.convexhull;

import java.awt.Point;
import java.awt.Polygon;
import java.util.List;

/**
 * Fake implementation of a convex hull just creates a bounding box around 
 * the given points.
 * 
 * @author Marcus Michalsky
 * @since 1.19
 */
public class BoundingBoxConvexHullStrategy implements ConvexHullStrategy {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Polygon compute(List<Point> points) {
		int xMin = Integer.MAX_VALUE;
		int xMax = Integer.MIN_VALUE;
		int yMin = Integer.MAX_VALUE;
		int yMax = Integer.MIN_VALUE;
		
		for (Point point : points) {
			if (point.x < xMin) {
				xMin = point.x;
			}
			if (point.x > xMax) {
				xMax = point.x;
			}
			if (point.y < yMin) {
				yMin = point.y;
			}
			if (point.y > yMax) {
				yMax = point.y;
			}
		}
		
		int[] xValues = {xMin, xMax, xMin, xMax};
		int [] yValues = {yMin, yMin, yMax, yMax};
		
		return new Polygon(xValues, yValues, 4);
	}
}