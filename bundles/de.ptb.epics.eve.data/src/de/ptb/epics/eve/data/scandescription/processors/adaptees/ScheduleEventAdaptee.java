package de.ptb.epics.eve.data.scandescription.processors.adaptees;

import de.ptb.epics.eve.data.measuringstation.event.ScheduleTime;

/**
 * Mutable Adaptee class for immutable 
 * {@link de.ptb.epics.eve.data.measuringstation.event.ScheduleEvent}.
 * 
 * @author Marcus Michalsky
 * @since 1.19
 */
public class ScheduleEventAdaptee {
	/*
	 * chain id and scan module id identify the scan module that is the 
	 * event trigger. schedule time defines whether the event should be 
	 * triggered at the start or end of execution of this scan module.
	 */
	private int chainId;
	private int scanModuleId;
	private ScheduleTime scheduleTime;

	/**
	 * @return the chainId
	 */
	public int getChainId() {
		return chainId;
	}

	/**
	 * @param chainId the chainId to set
	 */
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
	public void setScanModuleId(int scanModuleId) {
		this.scanModuleId = scanModuleId;
	}

	/**
	 * @return the scheduleTime
	 */
	public ScheduleTime getScheduleTime() {
		return scheduleTime;
	}

	/**
	 * @param scheduleTime the scheduleTime to set
	 */
	public void setScheduleTime(ScheduleTime scheduleTime) {
		this.scheduleTime = scheduleTime;
	}
}