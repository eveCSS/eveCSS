package de.ptb.epics.eve.data.scandescription.processors.adaptees.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.BeforeClass;
import org.junit.Test;

import de.ptb.epics.eve.data.ComparisonTypes;
import de.ptb.epics.eve.data.measuringstation.Detector;
import de.ptb.epics.eve.data.measuringstation.MeasuringStation;
import de.ptb.epics.eve.data.measuringstation.Option;
import de.ptb.epics.eve.data.scandescription.PauseCondition;
import de.ptb.epics.eve.data.scandescription.processors.adaptees.PauseConditionAdaptee;
import de.ptb.epics.eve.data.scandescription.processors.adaptees.PauseConditionAdapter;
import de.ptb.epics.eve.data.tests.mothers.measuringstation.DetectorMother;
import de.ptb.epics.eve.data.tests.mothers.measuringstation.MeasuringStationMother;
import de.ptb.epics.eve.data.tests.mothers.measuringstation.OptionMother;

/**
 * @author Marcus Michalsky
 * @since 1.36
 */
public class PauseConditionAdapterTest {
	private static PauseConditionAdapter adapter;
	private static MeasuringStation measuringStation;
	
	@Test
	public void testUnmarshal() {
		Option option = OptionMother.createNewDoubleOption();
		PauseCondition pauseCondition = new PauseCondition(option);
		try {
			PauseConditionAdaptee adaptee = adapter.unmarshal(pauseCondition);
			assertEquals(option.getID(), adaptee.getId());
			assertEquals(pauseCondition.getOperator(), adaptee.getOperator());
			assertEquals(pauseCondition.getType(), adaptee.getPauseType());
			assertEquals(pauseCondition.getPauseLimit(), adaptee.getPauseLimit());
			if (pauseCondition.hasContinueLimit()) {
				assertEquals(pauseCondition.getType(), adaptee.getContinueType());
			}
			assertEquals(pauseCondition.getContinueLimit(), adaptee.getContinueLimit());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testMarshal() {
		Detector detector = DetectorMother.createNewDetector();
		Option option = OptionMother.createNewDoubleOption();
		detector.add(option);
		measuringStation.add(detector);
		measuringStation.registerOption(option);
		
		PauseConditionAdaptee adaptee = new PauseConditionAdaptee();
		adaptee.setId(option.getID());
		adaptee.setOperator(ComparisonTypes.EQ);
		adaptee.setPauseType(option.getValue().getType());
		adaptee.setPauseLimit("1.1");
		
		try {
			PauseCondition pauseCondition = adapter.marshal(adaptee);
			assertEquals(adaptee.getId(), pauseCondition.getDevice().getID());
			assertEquals(adaptee.getOperator(), pauseCondition.getOperator());
			assertEquals(adaptee.getPauseType(), pauseCondition.getType());
			assertEquals(adaptee.getPauseLimit(), pauseCondition.getPauseLimit());
			assertEquals(adaptee.getContinueLimit(), pauseCondition.getContinueLimit());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	@BeforeClass
	public static void beforeClass() {
		measuringStation = MeasuringStationMother.createNewEmptyMeasuringStation();
		adapter = new PauseConditionAdapter(measuringStation);
	}
}
