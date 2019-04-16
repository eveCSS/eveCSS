package de.ptb.epics.eve.data.tests.mothers.scandescription;

import de.ptb.epics.eve.data.measuringstation.IMeasuringStation;
import de.ptb.epics.eve.data.scandescription.ScanDescription;

/**
 * @author Marcus Michalsky
 * @since 1.31
 */
public class ScanDescriptionMother {
	public static ScanDescription createScanDescription(IMeasuringStation measuringStation) {
		return new ScanDescription(measuringStation);
	}
}