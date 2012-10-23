package de.ptb.epics.eve.util.math;

import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;

/**
 * @author Marcus Michalsky
 * @since 1.7
 */
public class Sequence {
	
	private static final Logger LOGGER = Logger.getLogger(Sequence.class
			.getName());
	
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
	 * @param stop
	 * @param stepwidth
	 * @param stepcount
	 * @return
	 */
	public static Date getStart(Date stop, Date stepwidth, double stepcount) {
		/* start = stop - (stepwidth * stepcount) */
		Calendar start = Calendar.getInstance();
		Calendar realStepwidth = Sequence.adjustStepwidth(stepwidth);
		start.setTimeInMillis((long) (stop.getTime() - 
				(realStepwidth.getTime().getTime() * stepcount)));
		return start.getTime();
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
	 * @param stepwidth
	 * @param stepcount
	 * @return
	 */
	public static Date getStop(Date start, Date stepwidth, double stepcount) {
		// TODO
		return null;
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
	 * @param stepcount
	 * @return
	 */
	public static Date getStepwidth(Date start, Date stop, double stepcount) {
		// TODO
		Calendar stepwidth = Calendar.getInstance();
		stepwidth.set(Calendar.DAY_OF_MONTH, 1);
		stepwidth.set(Calendar.MONTH, 0);
		stepwidth.set(Calendar.YEAR, 1970);
		return null;
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
	
	/**
	 * 
	 * @param start
	 * @param stop
	 * @param stepwidth
	 * @return
	 */
	public static double getStepcount(Date start, Date stop, Date stepwidth) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(stop.getTime() + " - " + start.getTime() + " / "
					+ stepwidth.getTime());
		}
		return (stop.getTime() - start.getTime()) / ((double)stepwidth.getTime());
	}
	
	/*
	 * 
	 */
	private static Calendar adjustStepwidth(Date stepwidth) {
		Calendar realStepwidth = Calendar.getInstance();
		realStepwidth.setTime(stepwidth);
		realStepwidth.set(Calendar.DAY_OF_MONTH, 1);
		realStepwidth.set(Calendar.MONTH, 0);
		realStepwidth.set(Calendar.YEAR, 1970);
		return realStepwidth;
	}
}
