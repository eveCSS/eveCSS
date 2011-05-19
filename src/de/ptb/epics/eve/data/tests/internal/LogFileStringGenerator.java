package de.ptb.epics.eve.data.tests.internal;

import org.apache.log4j.Logger;

import de.ptb.epics.eve.data.measuringstation.AbstractDevice;

/**
 * 
 * @author Marcus Michalsky
 * @since 0.4.1
 */
public class LogFileStringGenerator {

	/**
	 * Returns 
	 * 
	 * @param d
	 * @return <code>d.getName()</code> ( <code>d.getID()</code> )
	 */
	public static String deviceString(AbstractDevice d)
	{
		return d.getName() + " (" + d.getID() + ")";
	}
	
	/**
	 * 
	 * 
	 * @param logger
	 * @param name
	 */
	public static void log_start(Logger logger, String name)
	{
		logger.info("*******************************************************");
		logger.info("**** " + name + " started");
		logger.info("*******************************************************");
	}
	
	/**
	 * 
	 * 
	 * @param logger
	 * @param name
	 */
	public static void log_end(Logger logger, String name)
	{
		logger.info("*******************************************************");
		logger.info("**** " + name + " finished");
		logger.info("*******************************************************");
	}
	
	/**
	 * 
	 * @param logger
	 */
	public static void classSetUp(Logger logger)
	{
		logger.info("*******************************************************");
		logger.info("Class Wide Tear Down Done (files closed)");
		logger.info("*******************************************************");
	}
	
	/**
	 * 
	 * @param logger
	 */
	public static void classTearDown(Logger logger)
	{
		logger.info("*******************************************************");
		logger.info("Class Wide Tear Down Done (files closed)");
		logger.info("*******************************************************");
	}
	
	/**
	 * 
	 * @param logger
	 */
	public static void testSetUp(Logger logger)
	{
		logger.info("**********************************************************");
		logger.info(
			"Test Wide Setup Done (measuring station filter initialized)");
		logger.info("**********************************************************");
	}
	
	/**
	 * 
	 * @param logger
	 */
	public static void testTearDown(Logger logger)
	{
	logger.info("**********************************************************");
	logger.info(
		"Test Wide Tear Down Done (measuring station filter garbaged)");
	logger.info("**********************************************************");
	}
}