package de.ptb.epics.eve.data.scandescription.processors.adaptees;

/**
 * Mutable Adaptee class for immutable 
 * {@link de.ptb.epics.eve.data.measuringstation.event.DetectorEvent}.
 * 
 * @author Marcus Michalsky
 * @since 1.19
 */
public class DetectorEventAdaptee {
	private String id;

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
}