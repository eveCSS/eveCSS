package de.ptb.epics.eve.data.scandescription.macro;

/**
 * A Macro is a placeholder which is resolved into a string.
 * It has a name and a description explaining its meaning.
 * 
 * @author Marcus Michalsky
 * @since 1.23
 */
public abstract class Macro {
	private String name;
	private String description;
	
	/**
	 * Resolves the Macro by returning its corresponding string.
	 * 
	 * @return its corresponding string.
	 */
	public abstract String resolve();

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
}