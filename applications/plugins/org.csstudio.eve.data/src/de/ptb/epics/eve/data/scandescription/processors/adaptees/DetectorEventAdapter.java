package de.ptb.epics.eve.data.scandescription.processors.adaptees;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import de.ptb.epics.eve.data.measuringstation.event.DetectorEvent;
import de.ptb.epics.eve.data.scandescription.Chain;
import de.ptb.epics.eve.data.scandescription.Channel;
import de.ptb.epics.eve.data.scandescription.ScanDescription;
import de.ptb.epics.eve.data.scandescription.ScanModule;

/**
 * XML adapter to switch between mutable and immutable detector events 
 * (XML Loading vs Application).
 * 
 * @author Marcus Michalsky
 * @since 1.19
 */
public class DetectorEventAdapter extends
		XmlAdapter<DetectorEvent, DetectorEventAdaptee> {
	private ScanDescription scanDescription;
	
	/**
	 * Constructor.
	 * 
	 * @param scanDescription the scan description the detector event belongs to
	 */
	public DetectorEventAdapter(ScanDescription scanDescription) {
		this.scanDescription = scanDescription;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public DetectorEvent marshal(DetectorEventAdaptee adaptee) throws Exception {
		// detector event id format: D-<chainid>-<smid>-<detid> 
		int chainId = Integer.parseInt(adaptee.getId().substring(2, 3));
		int smId = Integer.parseInt(adaptee.getId().substring(4, 5));
		String detectorId = adaptee.getId().substring(6);
		for (Chain chain : this.scanDescription.getChains()) {
			if (chain.getId() != chainId) {
				continue;
			}
			for (ScanModule sm : chain.getScanModules()) {
				if (sm.getId() != smId) {
					continue;
				}
				for (Channel ch : sm.getChannels()) {
					if (ch.getAbstractDevice().getID().equals(detectorId)) {
						return new DetectorEvent(ch);
					}
				}
			}
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * not implemented.
	 */
	@Override
	public DetectorEventAdaptee unmarshal(DetectorEvent detectorEvent)
			throws Exception {
		return null;
	}
}