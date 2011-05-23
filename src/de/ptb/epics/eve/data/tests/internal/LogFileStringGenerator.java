package de.ptb.epics.eve.data.tests.internal;

import org.apache.log4j.Logger;

import de.ptb.epics.eve.data.measuringstation.AbstractDevice;

/**
 * <code>LogFileStringGenerator</code> offers methods to write often used 
 * strings to a log.
 * 
 * @author Marcus Michalsky
 * @since 0.4.1
 */
public class LogFileStringGenerator {

	/**
	 * Returns a {@link java.lang.String} of a device as follows: 
	 * <code>&lt;device name&gt; (&lt;device id&gt;)</code>.
	 * 
	 * @param d the device of interest
	 * @return <code>&lt;device name&gt; (&lt;device id&gt;)</code>
	 */
	public static String deviceString(AbstractDevice d)
	{
		return d.getName() + " (" + d.getID() + ")";
	}
	
	/**
	 * Generates an info level log message for the start of a test.
	 * 
	 * @param logger the logger the log message should be written to
	 * @param name the name of the test
	 */
	public static void log_start(Logger logger, String name)
	{
		logger.info("*******************************************************");
		logger.info("**** " + name + " started");
		logger.info("*******************************************************");
	}
	
	/**
	 * Generates an info level log message for the end of a test.
	 * 
	 * @param logger the logger the log message should be written to
	 * @param name the name of the test
	 */
	public static void log_end(Logger logger, String name)
	{
		logger.info("*******************************************************");
		logger.info("**** " + name + " finished");
		logger.info("*******************************************************");
	}
	
	/**
	 * Generates an info level log message for the set up of a class.
	 * 
	 * @param logger the logger the log message should be written to
	 */
	public static void classSetUp(Logger logger)
	{
		logger.info("*******************************************************");
		logger.info("Class Wide Set Up Done");
		logger.info("*******************************************************");
	}
	
	/**
	 * Generates an info level log message for the tear down of a class.
	 * 
	 * @param logger the logger the log message should be written to
	 */
	public static void classTearDown(Logger logger)
	{
		logger.info("*******************************************************");
		logger.info("Class Wide Tear Down Done");
		logger.info("*******************************************************");
	}
	
	/**
	 * Generates an info level log message for the set up of a test.
	 * 
	 * @param logger the logger the log message should be written to
	 */
	public static void testSetUp(Logger logger)
	{
		logger.info("*******************************************************");
		logger.info(
			"Test Wide Setup Done");
		logger.info("*******************************************************");
	}
	
	/**
	 * Generates an info level log message for the tear down of a test.
	 * 
	 * @param logger the logger the log message should be written to
	 */
	public static void testTearDown(Logger logger)
	{
	logger.info("**********************************************************");
	logger.info(
		"Test Wide Tear Down Done");
	logger.info("**********************************************************");
	}
}