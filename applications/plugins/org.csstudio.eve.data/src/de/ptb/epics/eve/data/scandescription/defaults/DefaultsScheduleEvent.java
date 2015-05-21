package de.ptb.epics.eve.data.scandescription.defaults;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import de.ptb.epics.eve.data.measuringstation.event.ScheduleTime;

/**
 * @author Marcus Michalsky
 * @since 1.23
 */
@XmlType(name = "scheduleevent", propOrder = { "scheduleTime", "chainId",
		"scanModuleId" })
public class DefaultsScheduleEvent extends DefaultsControlEvent {
	private ScheduleTime scheduleTime;
	private int chainId;
	private int scanModuleId;
	/**
	 * @return the scheduleTime
	 */
	public ScheduleTime getScheduleTime() {
		return scheduleTime;
	}
	/**
	 * @param scheduleTime the scheduleTime to set
	 */
	@XmlElement(name = "incident")
	public void setScheduleTime(ScheduleTime scheduleTime) {
		this.scheduleTime = scheduleTime;
	}
	/**
	 * @return the chainId
	 */
	public int getChainId() {
		return chainId;
	}
	/**
	 * @param chainId the chainId to set
	 */
	@XmlElement(name = "chainid")
	public void setChainId(int chainId) {
		this.chainId = chainId;
	}
	/**
	 * @return the scanModuleId
	 */
	public int getScanModuleId() {
		return scanModuleId;
	}
	/**
	 * @param scanModuleId the scanModuleId to set
	 */
	@XmlElement(name = "smid")
	public void setScanModuleId(int scanModuleId) {
		this.scanModuleId = scanModuleId;
	}
	
	public String getEventId() {
		return "S-" + this.chainId + "-" + this.scanModuleId + "-"
				+ scheduleTime.toString();
	}
}