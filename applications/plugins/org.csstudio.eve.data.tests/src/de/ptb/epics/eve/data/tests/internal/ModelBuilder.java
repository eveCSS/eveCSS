package de.ptb.epics.eve.data.tests.internal;

import de.ptb.epics.eve.data.measuringstation.IMeasuringStation;
import de.ptb.epics.eve.data.measuringstation.MeasuringStation;

/**
 * 
 * @author Marcus Michalsky
 * @since 1.12
 */
public class ModelBuilder {
	
	/**
	 * 
	 * @return
	 */
	protected static IMeasuringStation createMeasuringStation() {
		MeasuringStation ims = new MeasuringStation();
		ims.setLoadedFileName("JUNIT");
		ims.setVersion("2.2");
		ims.setSchemaFileName(null);
		
		return ims;
	}
	
	
}