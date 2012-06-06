package de.ptb.epics.eve.viewer.messages;

import gov.aps.jca.dbr.TimeStamp;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import de.ptb.epics.eve.ecp1.client.model.Error;
import de.ptb.epics.eve.viewer.MessageSource;

/**
 * <code>ViewerMessage</code> represents a message displayed in the Messages
 * View of the Viewer. It is composed of
 * <ul>
 * <li>a time stamp - indicating when the message occurred</li>
 * <li>a source - indicating the origin of the message</li>
 * <li>a type - indicating the type of the message (e.g. info or error)</li>
 * <li>the content - the text part of the message</li>
 * </ul>
 * 
 * @author ?
 * @author Marcus Michalsky
 */
public class ViewerMessage implements Comparable<ViewerMessage> {
	// another representation of the time
	private TimeStamp timestamp;
	// the difference between epics and unix epoch in seconds
	// epics epoch is 1990/01/01 and unix is 1970/01/01
	private final long epoch_diff_secs = 631152000;
	// the source of the message
	private final MessageSource messageSource;
	// the type of the message
	private final Levels messageType;
	// the contents of the message
	private final String message;

	private final Calendar calendar;

	/**
	 * Constructs a <code>ViewerMessage</code> which has the current date as
	 * time.
	 * 
	 * @param messageSource
	 *            the source of the message
	 * @param messageType
	 *            the type of the message
	 * @param message
	 *            the content of the message
	 */
	public ViewerMessage(final MessageSource messageSource,
			final Levels messageType, final String message) {
		this.timestamp = new TimeStamp();
		this.messageSource = messageSource;
		this.messageType = messageType;
		this.message = message;
		this.calendar = Calendar.getInstance(Locale.GERMAN);
	}

	/**
	 * Constructs a <code>ViewerMessage</code> which has the current date as
	 * time and the Viewer as source.
	 * 
	 * @param messageType
	 *            the type of the message
	 * @param message
	 *            the content of the message
	 */
	public ViewerMessage(final Levels messageType, final String message) {
		this(MessageSource.VIEWER, messageType, message);
	}

	/**
	 * Constructs a <code>ViewerMessage</code>.
	 * 
	 * @param cal
	 *            the date of the message
	 * @param messageSource
	 *            the source of the message
	 * @param messageType
	 *            the type of the message
	 * @param message
	 *            the content of the message
	 */
	public ViewerMessage(final Calendar cal, final MessageSource messageSource,
			final Levels messageType, final String message) {
		this(messageSource, messageType, message);
		this.timestamp = new TimeStamp(cal.getTimeInMillis() - epoch_diff_secs
				* 1000.0);
	}

	/**
	 * Constructs a <code>ViewerMessage</code> out of an
	 * {@link de.ptb.epics.eve.ecp1.client.model.Error}.
	 * 
	 * @param error
	 *            the error as in
	 *            {@link de.ptb.epics.eve.ecp1.client.model.Error}
	 */
	public ViewerMessage(final Error error) {
		this(MessageSource.convertFromErrorFacility(error.getErrorFacility()),
				Levels.convertFromErrorSeverity(error.getErrorSeverity()),
				error.getText());
		this.timestamp = new TimeStamp(error.getGerenalTimeStamp()
				- epoch_diff_secs, error.getNanoseconds());
	}

	/**
	 * Returns the contents of the message.
	 * 
	 * @return the message contents
	 */
	public String getMessage() {
		return this.message;
	}

	/**
	 * Returns the source of the message.
	 * 
	 * @return the message' source
	 */
	public MessageSource getMessageSource() {
		return this.messageSource;
	}

	/**
	 * Returns the type of the message.
	 * 
	 * @return the message' type
	 */
	public Levels getMessageType() {
		return this.messageType;
	}

	/**
	 * Returns the {@link gov.aps.jca.dbr.TimeStamp} of the message.
	 * 
	 * @return the time stamp
	 */
	public TimeStamp getTimeStamp() {
		return this.timestamp;
	}

	/**
	 * Returns the date of origin of the message.
	 * 
	 * @return the date of origin of the message
	 */
	public Date getDate() {
		this.calendar.setTime(new Date(this.timestamp.secPastEpoch() * 1000));
		// EPICS vs UNIX epoch diff is 20 years
		this.calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + 20);
		return this.calendar.getTime();
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * compare here works vice versa as expected in order to get descending 
	 * order when using {@link java.util.Collections#sort(java.util.List)}.
	 */
	@Override
	public int compareTo(ViewerMessage o) {
		return -1 * this.getDate().compareTo(o.getDate());
	}
}