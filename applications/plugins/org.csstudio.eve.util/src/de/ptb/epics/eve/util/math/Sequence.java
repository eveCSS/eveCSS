package de.ptb.epics.eve.util.math;

import java.util.Calendar;
import java.util.Date;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;

import org.apache.log4j.Logger;

/**
 * Contains functions to complete the missing value of a quadruple consisting 
 * of <code>start, stop, stepwidth </code> and <code>stepcount</code> values.
 * The Quadruple defines a sequence from <code>start</code> to <code>stop</code> 
 * with <code>stepcount+1</code> values with equidistant width of 
 * <code>stepwidth</code> (except for <code>stop</code>).
 * 
 * @author Marcus Michalsky
 * @since 1.7
 */
public final class Sequence {
	
	private Sequence() {
	}
	
	private static final Logger LOGGER = Logger.getLogger(Sequence.class
			.getName());
	
	/**
	 * Calculates the <code>start</code> value for the given values.
	 * 
	 * @param stop end value of the sequence
	 * @param stepwidth distance between each value
	 * @param stepcount number of steps
	 * @return the start value
	 */
	public static int getStart(int stop, int stepwidth, double stepcount) {
		return (int)(stop - stepwidth * stepcount);
	}
	
	/**
	 * Calculates the <code>start</code> value for the given values.
	 * 
	 * @param stop end value of the sequence
	 * @param stepwidth distance between each value
	 * @param stepcount number of steps
	 * @return the start value
	 */
	public static double getStart(double stop, double stepwidth,
			double stepcount) {
		return stop - stepwidth * stepcount;
	}
	
	/**
	 * Calculates the <code>start</code> value for the given values.
	 * 
	 * @param stop end value of the sequence
	 * @param stepwidth distance between each value
	 * @param stepcount number of steps
	 * @return the start value
	 */
	public static Date getStart(Date stop, Date stepwidth, double stepcount) {
		/* start = stop - (stepwidth * stepcount) */
		Calendar start = Calendar.getInstance();
		start.setTimeInMillis((long) (stop.getTime() - ((Sequence
				.adjustStepwidth(stepwidth).getTime().getTime() + start
				.get(Calendar.ZONE_OFFSET)) * stepcount)));
		return start.getTime();
	}
	
	/**
	 * Calculates the <code>start</code> value for the given values.
	 * 
	 * @param stop end value of the sequence
	 * @param stepwidth distance between each value
	 * @param stepcount number of steps
	 * @return the start value
	 */
	public static Duration getStart(Duration stop, Duration stepwidth,
			double stepcount) {
		try {
			Calendar cal = Calendar.getInstance();
			DatatypeFactory factory = DatatypeFactory.newInstance();
			return factory.newDuration((long) (stop.getTimeInMillis(cal) - 
					(stepwidth.getTimeInMillis(cal) * stepcount)));
		} catch (DatatypeConfigurationException e) {
			LOGGER.error(e.getMessage(), e);
			return null;
		}
	}
	
	/**
	 * Calculates the <code>stop</code> value for the given values.
	 * 
	 * @param start start value of the sequence
	 * @param stepwidth distance between each value
	 * @param stepcount number of steps
	 * @return the stop value
	 */
	public static int getStop(int start, int stepwidth, double stepcount) {
		return (int)(start + stepwidth * stepcount);
	}
	
	/**
	 * Calculates the <code>stop</code> value for the given values.
	 * 
	 * @param start start value of the sequence
	 * @param stepwidth distance between each value
	 * @param stepcount number of steps
	 * @return the stop value
	 */
	public static double getStop(double start, double stepwidth,
			double stepcount) {
		return start + stepwidth * stepcount;
	}
	
	/**
	 * Calculates the <code>stop</code> value for the given values.
	 * 
	 * @param start start value of the sequence
	 * @param stepwidth distance between each value
	 * @param stepcount number of steps
	 * @return the stop value
	 */
	public static Date getStop(Date start, Date stepwidth, double stepcount) {
		// stop = start + (stepwidth * stepcount)
		Calendar stop = Calendar.getInstance();
		stop.setTimeInMillis((long) (start.getTime() + ((Sequence
				.adjustStepwidth(stepwidth).getTime().getTime() + stop
				.get(Calendar.ZONE_OFFSET)) * stepcount)));
		return stop.getTime();
	}
	
	/**
	 * Calculates the <code>stop</code> value for the given values.
	 * 
	 * @param start start value of the sequence
	 * @param stepwidth distance between each value
	 * @param stepcount number of steps
	 * @return the stop value
	 */
	public static Duration getStop(Duration start, Duration stepwidth,
			double stepcount) {
		try {
			Calendar cal = Calendar.getInstance();
			DatatypeFactory factory = DatatypeFactory.newInstance();
			return factory.newDuration((long) (start.getTimeInMillis(cal) + 
					(stepwidth.getTimeInMillis(cal) * stepcount)));
		} catch (DatatypeConfigurationException e) {
			LOGGER.error(e.getMessage(), e);
			return null;
		}
	}
	
	/**
	 * Calculates the distance between each value.
	 * 
	 * @param start start value of the sequence
	 * @param stop stop value of the sequence
	 * @param stepcount number of steps
	 * @return the distance between each value
	 */
	public static int getStepwidth(int start, int stop, double stepcount) {
		if (stepcount == 0) {
			return 0;
		}
		return (int)((stop - start) / stepcount);
	}
	
	/**
	 * Calculates the distance between each value.
	 * 
	 * @param start start value of the sequence
	 * @param stop stop value of the sequence
	 * @param stepcount number of steps
	 * @return the distance between each value
	 */
	public static double getStepwidth(double start, double stop,
			double stepcount) {
		return (stop - start) / stepcount;
	}
	
	/**
	 * Calculates the distance between each value.
	 * 
	 * @param start start value of the sequence
	 * @param stop stop value of the sequence
	 * @param stepcount number of steps
	 * @return the distance between each value
	 */
	public static Date getStepwidth(Date start, Date stop, double stepcount) {
		Calendar stepwidth = Calendar.getInstance();
		stepwidth.setTimeInMillis(((long) 
				((stop.getTime() - start.getTime()) / stepcount)) - 
						stepwidth.get(Calendar.ZONE_OFFSET));
		return stepwidth.getTime();
	}
	
	/**
	 * Calculates the distance between each value.
	 * 
	 * @param start start value of the sequence
	 * @param stop stop value of the sequence
	 * @param stepcount number of steps
	 * @return the distance between each value
	 */
	public static Duration getStepwidth(Duration start, Duration stop,
			double stepcount) {
		try {
			Calendar cal = Calendar.getInstance();
			DatatypeFactory factory = DatatypeFactory.newInstance();
			return factory.newDuration((long) ((stop.getTimeInMillis(cal) - 
					start.getTimeInMillis(cal)) / stepcount));
		} catch (DatatypeConfigurationException e) {
			LOGGER.error(e.getMessage(), e);
			return null;
		}
	}
	
	/**
	 * Calculates the number of steps.
	 * 
	 * @param start start value of the sequence
	 * @param stop stop value of the sequence
	 * @param stepwidth distance between each value
	 * @return the number of steps
	 */
	public static double getStepcount(int start, int stop, int stepwidth) {
		return Sequence.getStepcount((double) start, (double) stop,
				(double) stepwidth);
	}
	
	/**
	 * Calculates the number of steps.
	 * 
	 * @param start start value of the sequence
	 * @param stop stop value of the sequence
	 * @param stepwidth distance between each value
	 * @return the number of steps
	 */
	public static double getStepcount(double start, double stop, double stepwidth) {
		if (stepwidth == 0) {
			return Double.NaN;
		}
		return (stop - start) / stepwidth;
	}
	
	/**
	 * Calculates the number of steps.
	 * 
	 * @param start start value of the sequence
	 * @param stop stop value of the sequence
	 * @param stepwidth distance between each value
	 * @return the number of steps
	 */
	public static double getStepcount(Date start, Date stop, Date stepwidth) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(stop.getTime() + " - " + start.getTime() + " / "
					+ stepwidth.getTime());
		}
		Calendar offset = Calendar.getInstance();
		return (stop.getTime() - start.getTime()) / 
				((double)stepwidth.getTime() + offset.get(Calendar.ZONE_OFFSET));
	}
	
	/**
	 * Calculates the number of steps.
	 * 
	 * @param start start value of the sequence
	 * @param stop stop value of the sequence
	 * @param stepwidth distance between each value
	 * @return the number of steps
	 */
	public static double getStepcount(Duration start, Duration stop,
			Duration stepwidth) {
		Calendar cal = Calendar.getInstance();
		return (stop.getTimeInMillis(cal) - start.getTimeInMillis(cal))
				/ (double)stepwidth.getTimeInMillis(cal);
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