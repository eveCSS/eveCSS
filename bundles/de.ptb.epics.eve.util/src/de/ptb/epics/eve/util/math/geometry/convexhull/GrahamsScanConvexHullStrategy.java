package de.ptb.epics.eve.util.math.geometry.convexhull;

import java.awt.Point;
import java.awt.Polygon;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import de.ptb.epics.eve.util.math.geometry.PolarPoint;

/**
 * Convex Hull with Graham's scan.
 * 
 * @author Marcus Michalsky
 * @since 1.19
 * @see http://www.introductiontoalgorithms.com
 */
public class GrahamsScanConvexHullStrategy implements ConvexHullStrategy {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Polygon compute(List<Point> points) {
		// let p0 be the point with the minimum y coordinate, 
		// or the leftmost such point in case of a tie
		Point p0 = new Point(Integer.MAX_VALUE, Integer.MAX_VALUE);
		for (Point p : points) {
			if (p.y < p0.y) {
				p0 = p;
			}
			if (p.y == p0.y && p.x > p0.x) {
				p0 = p;
			}
		}
		// let <p1, p2, ..., pm> be the remaining points...
		List<Point> q = new LinkedList<Point>(points);
		q.remove(p0);
		// ... sorted by polar angle in counterclockwise order around p0
		List<PolarPoint> qp = new LinkedList<PolarPoint>();
		for (Point p : q) {
			qp.add(new PolarPoint(p.x, p.y));
		}
		Collections.sort(qp);
		// if more than one point has the same angle, remove all but the one
		// that is farthest from p0
		PolarPoint[] array = qp.toArray(new PolarPoint[]{});
		List<PolarPoint> equalAngles = new LinkedList<PolarPoint>();
		List<PolarPoint> toRemove = new LinkedList<PolarPoint>();
		PolarPoint predecessor = array[0];
		for (int i = 1; i < array.length; i++) {
			if (array[i].compareTo(predecessor) == 0) {
				if (!equalAngles.contains(predecessor)) {
					equalAngles.add(predecessor);
				}
				equalAngles.add(array[i]);
			} else if (!equalAngles.isEmpty()) {
				PolarPoint rMax = PolarPoint.getMaxR(equalAngles);
				equalAngles.remove(rMax);
				toRemove.addAll(equalAngles);
				equalAngles.clear();
			}
			predecessor = array[i];
		}
		for (PolarPoint p : toRemove) {
			qp.remove(p);
		}
		Stack<PolarPoint> stack = new Stack<PolarPoint>();
		stack.push(new PolarPoint(p0.x, p0.y));
		stack.push(qp.get(0));
		stack.push(qp.get(1));
		for (int i = 2; i < qp.size(); i++) {
			// while the angle formed by points Next-To-Top(S), Top(S), 
			// and pi makes a nonleft turn: pop
			PolarPoint pp0 = stack.get(stack.size() - 2);
			PolarPoint pp1 = stack.peek();
			PolarPoint pp2 = qp.get(i);
			while (PolarPoint.CrossProduct(
					new PolarPoint(pp2.x - pp0.x, pp2.y - pp0.y),
					new PolarPoint(pp1.x - pp0.x, pp1.y - pp0.y)) > 0) {
				stack.pop();
				pp0 = stack.get(stack.size() - 2);
				pp1 = stack.peek();
				pp2 = qp.get(i);
			}
			stack.push(qp.get(i));
		}
		
		Polygon result = new Polygon();
		for (PolarPoint p : stack) {
			result.addPoint(p.x, p.y);
		}
		
		return result;
	}
}