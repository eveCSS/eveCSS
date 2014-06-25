package de.ptb.epics.eve.data.measuringstation.event;

import org.apache.log4j.Logger;

import javafx.collections.ListChangeListener;
import de.ptb.epics.eve.data.scandescription.ScanModule;

/**
 * A schedule event is triggered either before a scan module start or after 
 * a scan module has finished. The behavior can be changed via 
 * {@link #setScheduleTime(ScheduleTime)}.
 * 
 * @author Marcus Michalsky
 * @since 1.19
 */
public class ScheduleEvent extends ScanEvent implements 
		ListChangeListener<ScanModule> {
	private static final Logger LOGGER = Logger.getLogger(ScheduleEvent.class
			.getName());
	private ScanModule scanModule;
	private ScheduleTime scheduleTime;
	private final String id;
	private final String name;
	
	/**
	 * Constructor.
	 */
	public ScheduleEvent(ScanModule scanModule) {
		this.scanModule = scanModule;
		this.scheduleTime = ScheduleTime.END;
		this.id = "S-" + 
				this.scanModule.getChain().getId() + "-" + 
				this.scanModule.getId() + "-" + 
				this.scheduleTime.toString();
		this.name = "Schedule ( " + this.id + " )";
		this.scanModule.getChain().addScanModuleChangeListener(this);
	}
	
	/**
	 * @return the scanModule
	 */
	public ScanModule getScanModule() {
		return scanModule;
	}
	
	/**
	 * @return the scheduleTime
	 */
	public ScheduleTime getScheduleTime() {
		return scheduleTime;
	}
	
	/**
	 * Sets whether this event is triggered before the scan module starts or 
	 * after it has finished.
	 * 
	 * @param scheduleTime {@link de.ptb.epics.eve.data.measuringstation.event.ScheduleTime#START} 
	 * 		if the event should be triggered before the scan module starts, 
	 * 		{@link de.ptb.epics.eve.data.measuringstation.event.ScheduleTime#END} 
	 * 		if the event should be triggered after the scan module has finished 
	 */
	public void setScheduleTime(ScheduleTime scheduleTime) {
		this.scheduleTime = scheduleTime;
		//TODO change id...
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getId() {
		return this.id;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return this.name;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onChanged(
			ListChangeListener.Change<? extends ScanModule> change) {
		while (change.next()) {
			if (change.wasRemoved()) {
				for (ScanModule sm : change.getRemoved()) {
					if (sm == this.scanModule) {
						LOGGER.debug(sm.getName() + 
								" was deleted -> event is invalid");
						sm.getChain().removeScanModuleChangeListener(this);
						this.propertyChangeSupport.firePropertyChange(
								ScanEvent.VALID_PROP, this.valid,
								this.valid = false);
					}
				}
			}
		}
	}
}