package de.ptb.epics.eve.data.scandescription.defaults;

import javax.xml.bind.annotation.*;

/**
 * @author Marcus Michalsky
 * @since 1.23
 */
@XmlType(name = "smevent")
public class DefaultsRedoEvent {
	private DefaultsControlEvent controlEvent;

	public DefaultsRedoEvent() {
	}
	
	public DefaultsRedoEvent(DefaultsControlEvent controlEvent) {
		this.controlEvent = controlEvent;
	}
	
	/**
	 * @return the controlEvent
	 */
	public DefaultsControlEvent getControlEvent() {
		return controlEvent;
	}

	/**
	 * @param controlEvent the controlEvent to set
	 */
	@XmlElements(value = {
		@XmlElement(name = "detectorevent", type = DefaultsDetectorEvent.class),
		@XmlElement(name = "monitorevent", type = DefaultsMonitorEvent.class), 
		@XmlElement(name = "scheduleevent", type = DefaultsScheduleEvent.class)
	})
	public void setControlEvent(DefaultsControlEvent controlEvent) {
		this.controlEvent = controlEvent;
	}
}