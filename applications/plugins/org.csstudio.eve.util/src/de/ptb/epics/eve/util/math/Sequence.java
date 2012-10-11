package de.ptb.epics.eve.util.math;

/**
 * @author Marcus Michalsky
 * @since 1.7
 */
public class Sequence {

	/**
	 * 
	 * @param stop
	 * @param stepwidth
	 * @param stepcount
	 * @return
	 */
	public static int getStart(int stop, int stepwidth, double stepcount) {
		return (int)(stop - stepwidth * stepcount);
	}
	
	/**
	 * 
	 * @param stop
	 * @param stepwidth
	 * @param stepcount
	 * @return
	 */
	public static double getStart(double stop, double stepwidth,
			double stepcount) {
		return stop - stepwidth * stepcount;
	}
	
	/**
	 * 
	 * @param start
	 * @param stepwidth
	 * @param stepcount
	 * @return
	 */
	public static int getStop(int start, int stepwidth, double stepcount) {
		return (int)(start + stepwidth * stepcount);
	}
	
	/**
	 * 
	 * @param start
	 * @param stepwidth
	 * @param stepcount
	 * @return
	 */
	public static double getStop(double start, double stepwidth,
			double stepcount) {
		return start + stepwidth * stepcount;
	}
	
	/**
	 * 
	 * @param start
	 * @param stop
	 * @param stepcount
	 * @return
	 */
	public static int getStepwidth(int start, int stop, double stepcount) {
		if (stepcount == 0) {
			return 0;
		}
		return (int)((stop - start) / stepcount);
	}
	
	/**
	 * 
	 * @param start
	 * @param stop
	 * @param stepcount
	 * @return
	 */
	public static double getStepwidth(double start, double stop,
			double stepcount) {
		return (stop - start) / stepcount;
	}
	
	/**
	 * 
	 * @param start
	 * @param stop
	 * @param stepwidth
	 * @return
	 */
	public static double getStepcount(int start, int stop, int stepwidth) {
		return Sequence.getStepcount((double) start, (double) stop,
				(double) stepwidth);
	}
	
	/**
	 * 
	 * @param start
	 * @param stop
	 * @param stepwidth
	 * @return
	 */
	public static double getStepcount(double start, double stop, double stepwidth) {
		if (stepwidth == 0) {
			return Double.NaN;
		}
		return (stop - start) / stepwidth;
	}
}
