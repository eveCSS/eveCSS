package de.ptb.epics.eve.data.scandescription.updater;

/**
 * @author Marcus Michalsky
 * @since 1.18
 */
public abstract class AbstractModification implements Modification {
	private final Patch belongsTo;
	private String changeLog;
	
	/**
	 * Constructor.
	 * 
	 * @param changeLog description of changes
	 */
	public AbstractModification(Patch patch, String changeLog) {
		this.belongsTo = patch;
		this.changeLog = changeLog;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getChangeLog() {
		return this.changeLog;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Patch belongsTo() {
		return this.belongsTo;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void appendMessage(String message) {
		this.changeLog = changeLog + "\n" + message;
	}
}