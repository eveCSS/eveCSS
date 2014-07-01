package de.ptb.epics.eve.data.scandescription.processors.adaptees;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import de.ptb.epics.eve.data.measuringstation.event.ScheduleEvent;
import de.ptb.epics.eve.data.scandescription.ScanDescription;
import de.ptb.epics.eve.data.scandescription.ScanModule;

/**
 * XML adapter to switch between mutable and immutable schedule events 
 * (XML Loading vs Application).
 * 
 * @author Marcus Michalsky
 * @since 1.19
 */
public class ScheduleEventAdapter extends
		XmlAdapter<ScheduleEvent, ScheduleEventAdaptee> {
	private ScanDescription scanDescription;
	
	/**
	 * Constructor.
	 * 
	 * @param scanDescription the scan description the schedule event belongs to
	 */
	public ScheduleEventAdapter(ScanDescription scanDescription) {
		this.scanDescription = scanDescription;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ScheduleEvent marshal(ScheduleEventAdaptee adaptee) throws Exception {
		if (adaptee.getChainId() == 0 && adaptee.getScanModuleId() == 0) {
			return null;
		}
		ScanModule scanModule = scanDescription.getChain(adaptee.getChainId())
				.getScanModuleById(adaptee.getScanModuleId());
		return new ScheduleEvent(scanModule);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * not implemented.
	 */
	@Override
	public ScheduleEventAdaptee unmarshal(ScheduleEvent scheduleEvent)
			throws Exception {
		return null;
	}
}