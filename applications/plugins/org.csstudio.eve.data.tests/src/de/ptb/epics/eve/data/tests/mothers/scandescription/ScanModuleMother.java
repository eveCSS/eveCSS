package de.ptb.epics.eve.data.tests.mothers.scandescription;

import de.ptb.epics.eve.data.scandescription.ScanModule;

/**
 * Fabricates scan module test fixtures and tailors them.
 * 
 * @author Marcus Michalsky
 * @since 1.27
 */
public class ScanModuleMother {
	private static int counter = 0;
	
	public static ScanModule createNewScanModule() {
		ScanModule scanModule = new ScanModule(++counter);
		scanModule.setName("SM " + counter);
		return scanModule;
	}
}