package de.ptb.epics.eve.data.scandescription.defaults.channel;

import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;

/**
 * @author Marcus Michalsky
 * @since 1.23
 */
@XmlTransient
@XmlSeeAlso({
	DefaultsDetectorEvent.class, 
	DefaultsMonitorEvent.class, 
	DefaultsScheduleEvent.class
})
public abstract class DefaultsControlEvent {
}