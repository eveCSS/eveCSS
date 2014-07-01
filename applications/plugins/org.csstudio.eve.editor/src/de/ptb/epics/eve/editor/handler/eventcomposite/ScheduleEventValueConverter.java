package de.ptb.epics.eve.editor.handler.eventcomposite;

import org.eclipse.core.commands.AbstractParameterValueConverter;
import org.eclipse.core.commands.ParameterValueConversionException;

import de.ptb.epics.eve.data.measuringstation.event.ScheduleEvent;
import de.ptb.epics.eve.data.scandescription.Chain;
import de.ptb.epics.eve.data.scandescription.ScanDescription;
import de.ptb.epics.eve.data.scandescription.ScanModule;

/**
 * @author Marcus Michalsky
 * @since 1.19
 */
public class ScheduleEventValueConverter extends
		AbstractParameterValueConverter {
	private ScanDescription scanDescription;
	
	/**
	 * Constructor.
	 * 
	 * @param scanDescripion the scan description the sm belongs to
	 * 	 */
	public ScheduleEventValueConverter(ScanDescription scanDescripion) {
		this.scanDescription = scanDescripion;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object convertToObject(String parameterValue)
			throws ParameterValueConversionException {
		// format is S-<chid>-<smid>-<S,E>
		String[] ids = parameterValue.split("-", 4);
		int chainId = Integer.parseInt(ids[1]);
		int smId = Integer.parseInt(ids[2]);
		if (chainId == 0 && smId == 0) {
			return this.scanDescription.getDefaultStartEvent();
		}
		for (Chain ch : this.scanDescription.getChains()) {
			if (ch.getId() != chainId) {
				continue;
			}
			for (ScanModule sm : ch.getScanModules()) {
				if (sm.getId() != smId) {
					continue;
				}
				return new ScheduleEvent(sm);
			}
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String convertToString(Object parameterValue)
			throws ParameterValueConversionException {
		if (parameterValue instanceof ScheduleEvent) {
			return ((ScheduleEvent)parameterValue).getId();
		}
		return null;
	}
}