package de.ptb.epics.eve.editor.handler.eventcomposite;

import org.eclipse.core.commands.AbstractParameterValueConverter;
import org.eclipse.core.commands.ParameterValueConversionException;

import de.ptb.epics.eve.data.measuringstation.event.DetectorEvent;
import de.ptb.epics.eve.data.scandescription.Chain;
import de.ptb.epics.eve.data.scandescription.Channel;
import de.ptb.epics.eve.data.scandescription.ScanDescription;
import de.ptb.epics.eve.data.scandescription.ScanModule;

/**
 * @author Marcus Michalsky
 * @since 1.19
 */
public class DetectorEventValueConverter extends
		AbstractParameterValueConverter {
	private ScanDescription scanDescription;
	
	/**
	 * Constructor.
	 * 
	 * @param scanDescription the scan description the 
	 */
	public DetectorEventValueConverter(ScanDescription scanDescription) {
		this.scanDescription = scanDescription;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object convertToObject(String parameterValue)
			throws ParameterValueConversionException {
		// format is D-<chainId>-<scanmoduleId>-<detectorId>
		String[] ids = parameterValue.split("-", 4);
		int chainId = Integer.parseInt(ids[1]);
		int smId = Integer.parseInt(ids[2]);
		String detectorId = ids[3];
		for (Chain ch : this.scanDescription.getChains()) {
			if (ch.getId() != chainId) {
				continue;
			}
			for (ScanModule sm : ch.getScanModules()) {
				if (sm.getId() != smId) {
					continue;
				}
				for (Channel chan : sm.getChannels()) {
					if (chan.getDetectorChannel().getID().equals(detectorId)) {
						return new DetectorEvent(chan);
					}
				}
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
		if (parameterValue instanceof DetectorEvent) {
			return ((DetectorEvent)parameterValue).getId();
		}
		return null;
	}
}