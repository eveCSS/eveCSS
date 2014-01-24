package de.ptb.epics.eve.data.scandescription.updater;

/**
 * @author Marcus Michalsky
 * @since 1.18
 */
public abstract class AbstractModification implements Modification {
	private String changeLog;

	/**
	 * Constructor.
	 * 
	 * @param changeLog description of changes
	 */
	public AbstractModification(String changeLog) {
		this.changeLog = changeLog;
	}
	
	/**
	 * @return the changeLog
	 */
	public String getChangeLog() {
		return changeLog;
	}
}