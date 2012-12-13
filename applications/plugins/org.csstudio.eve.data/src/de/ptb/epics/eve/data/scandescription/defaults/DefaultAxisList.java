package de.ptb.epics.eve.data.scandescription.defaults;

/**
 * @author Marcus Michalsky
 * @since 1.8
 */
public class DefaultAxisList extends DefaultAxis {
	private String positionList;

	/**
	 * @return the positionList
	 */
	public String getPositionList() {
		return positionList;
	}

	/**
	 * @param positionList the positionList to set
	 */
	public void setPositionList(String positionList) {
		this.positionList = positionList;
	}
}