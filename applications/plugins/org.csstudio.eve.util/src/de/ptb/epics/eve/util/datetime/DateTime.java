package de.ptb.epics.eve.util.datetime;

/**
 * @author Marcus Michalsky
 * @since 1.26
 */
public class DateTime {
	public final static int MINUTE_IN_SECONDS = 60;
	public final static int HOUR_IN_MINUTES = 60;
	public final static int DAY_IN_HOURS = 24;
	
	/**
	 * Returns a human readable string of the given duration in seconds as follows 
	 * (first match applies):
	 * 
	 * <ul>
	 *   <li>Given duration >= 10 days: <pre>XXd YYh ZZmin AAs</pre></li>
	 *   <li>Given duration >= 1 day: <pre>Xd YYh ZZmin AAs</pre></li>
	 *   <li>Given duration >= 10 hours: <pre>YYh ZZmin AAs</pre></li>
	 *   <li>Given duration >= 1 hour: <pre>Yh ZZmin AAs</pre></li>
	 *   <li>Given duration >= 10 minutes: <pre>ZZmin AAs</pre></li>
	 *   <li>Given duration >= 1 minute: <pre>Zmin AAs</pre></li>
	 *   <li>Given duration >= 10 seconds: <pre>AAs</pre></li>
	 *   <li>Given duration >= 1 second: <pre>As</pre></li>
	 *   <li>otherwise: empty String</li>
	 * </ul>
	 * 
	 * @param duration the duration in seconds
	 * @return a human readable string
	 */
	public static String humanReadable(int duration) {
		int seconds = duration % DateTime.MINUTE_IN_SECONDS;
		duration /= DateTime.MINUTE_IN_SECONDS;
		int minutes = duration % DateTime.HOUR_IN_MINUTES;
		duration /= DateTime.HOUR_IN_MINUTES;
		int hours = duration % DateTime.DAY_IN_HOURS;
		int days = duration / DateTime.DAY_IN_HOURS;
		
		if (days != 0) {
			return String.format("%dd %02dh %02dmin %02ds" ,
					days, hours, minutes, seconds);
		}
		if (hours != 0) {
			return String.format("%dh %02dmin %02ds", hours, minutes, seconds);
		}
		if (minutes != 0) {
			return String.format("%dmin %02ds", minutes, seconds);
		}
		if (seconds != 0) {
			return String.format("%ds", seconds);
		}
		return "";
	}
}