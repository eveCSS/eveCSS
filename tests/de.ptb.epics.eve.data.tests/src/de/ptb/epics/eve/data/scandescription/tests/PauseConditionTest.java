package de.ptb.epics.eve.data.scandescription.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

import de.ptb.epics.eve.data.ComparisonTypes;
import de.ptb.epics.eve.data.measuringstation.DetectorChannel;
import de.ptb.epics.eve.data.measuringstation.MotorAxis;
import de.ptb.epics.eve.data.measuringstation.Option;
import de.ptb.epics.eve.data.scandescription.PauseCondition;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener;
import de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent;
import de.ptb.epics.eve.data.tests.mothers.measuringstation.DetectorChannelMother;
import de.ptb.epics.eve.data.tests.mothers.measuringstation.MotorAxisMother;
import de.ptb.epics.eve.data.tests.mothers.measuringstation.OptionMother;

/**
 * @author Marcus Michalsky
 * @since 1.36
 */
public class PauseConditionTest {
	
	@Test
	public void testGetDevice() {
		Option option = OptionMother.createNewOption();
		PauseCondition pauseCondition = new PauseCondition(1, option);
		assertEquals(option, pauseCondition.getDevice());
	}
	
	@Test
	public void testGetTypeAxis() {
		MotorAxis motorAxis = MotorAxisMother.createNewIntTypeMotorAxis();
		PauseCondition pauseCondition = new PauseCondition(1, motorAxis);
		assertEquals("int type does not match", 
			motorAxis.getPosition().getType(), pauseCondition.getType());
		motorAxis = MotorAxisMother.createNewDoubleTypeMotorAxis();
		pauseCondition = new PauseCondition(1, motorAxis);
		assertEquals("double type does not match",
			motorAxis.getPosition().getType(), pauseCondition.getType());
		motorAxis = MotorAxisMother.createNewStringTypeMotorAxis();
		pauseCondition = new PauseCondition(1, motorAxis);
		assertEquals("string type does not match",
			motorAxis.getPosition().getType(), pauseCondition.getType());
		motorAxis = MotorAxisMother.createNewDiscreteStringTypeMotorAxis();
		pauseCondition = new PauseCondition(1, motorAxis);
		assertEquals("discrete string type does not match",
			motorAxis.getPosition().getType(), pauseCondition.getType());
	}
	
	@Test
	public void testGetTypeChannel() {
		DetectorChannel channel = DetectorChannelMother.
				createNewIntTypeDetectorChannel();
		PauseCondition pauseCondition = new PauseCondition(1, channel);
		assertEquals("int type does not match",
			channel.getRead().getType(), pauseCondition.getType());
		channel = DetectorChannelMother.
				createNewDoubleTypeDetectorChannel();
		pauseCondition = new PauseCondition(1, channel);
		assertEquals("double type does not match",
			channel.getRead().getType(), pauseCondition.getType());
		channel = DetectorChannelMother.
				createNewStringTypeDetectorChannel();
		pauseCondition = new PauseCondition(1, channel);
		assertEquals("string type does not match",
			channel.getRead().getType(), pauseCondition.getType());
		channel = DetectorChannelMother.
				createNewDiscreteStringTypeDetectorChannel();
		pauseCondition = new PauseCondition(1, channel);
		assertEquals("discrete string type does not match",
			channel.getRead().getType(), pauseCondition.getType());
	}
	
	@Test
	public void testGetTypeOption() {
		Option option = OptionMother.createNewOption();
		PauseCondition pauseCondition = new PauseCondition(1, option);
		assertEquals(option.getValue().getType(), pauseCondition.getType());
	}
	
	@Test
	public void testIsDiscreteAxis() {
		PauseCondition pauseCondition = new PauseCondition(1, 
				MotorAxisMother.createNewStringTypeMotorAxis());
		assertFalse("axis condition should not be discrete", 
				pauseCondition.isDiscrete());
		PauseCondition discretePauseCondition = new PauseCondition(1, 
				MotorAxisMother.createNewDiscreteStringTypeMotorAxis());
		assertTrue("axis condition should be discrete", 
				discretePauseCondition.isDiscrete());
	}
	
	@Test
	public void testIsDiscreteChannel() {
		PauseCondition pauseCondition = new PauseCondition(1, 
			DetectorChannelMother.createNewStringTypeDetectorChannel());
		assertFalse("channel condition should not be discrete", 
				pauseCondition.isDiscrete());
		PauseCondition discretePauseCondition = new PauseCondition(1, 
			DetectorChannelMother.createNewDiscreteStringTypeDetectorChannel());
		assertTrue("channel condition should be discrete", 
				discretePauseCondition.isDiscrete());
	}
	
	@Test
	public void testIsDiscreteOption() {
		PauseCondition pauseCondition = new PauseCondition(1, 
				OptionMother.createNewOption());
		assertFalse("option condition should not be discrete", 
				pauseCondition.isDiscrete());
		PauseCondition discretePauseCondition = new PauseCondition(1, 
				OptionMother.createNewDiscreteStringOption());
		assertTrue("option condition should be discrete", 
				discretePauseCondition.isDiscrete());
	}
	
	@Test 
	public void testSetGetOperator() {
		Option option = OptionMother.createNewOption();
		PauseCondition pauseCondition = new PauseCondition(1, option);
		pauseCondition.setOperator(ComparisonTypes.LT);
		assertEquals(ComparisonTypes.LT, pauseCondition.getOperator());
	}
	
	@Test
	public void testIsValidOperator() {
		PauseCondition pauseCondition = new PauseCondition(1, 
				OptionMother.createNewOption());
		assertTrue("EQ should be valid for non-discrete devices", 
			pauseCondition.isValidOperator(ComparisonTypes.EQ));
		assertTrue("NE should be valid for non-discrete devices",
			pauseCondition.isValidOperator(ComparisonTypes.NE));
		assertTrue("LT should be valid for non-discrete devices",
			pauseCondition.isValidOperator(ComparisonTypes.LT));
		assertTrue("GT should be valid for non-discrete devices",
			pauseCondition.isValidOperator(ComparisonTypes.GT));
			
		PauseCondition discreteStringPauseCondition = new PauseCondition(1, 
				OptionMother.createNewDiscreteStringOption());
		assertTrue("EQ should be valid for discrete devices",
			discreteStringPauseCondition.isValidOperator(ComparisonTypes.EQ));
		assertTrue("NE should be valid for discrete devices",
			discreteStringPauseCondition.isValidOperator(ComparisonTypes.NE));
		assertFalse("LT should not be valid for discrete devices",
			discreteStringPauseCondition.isValidOperator(ComparisonTypes.LT));
		assertFalse("GT should not be valid for discrete devices",
			discreteStringPauseCondition.isValidOperator(ComparisonTypes.GT));
	}
	
	@Test
	public void testGetValidOperators() {
		PauseCondition pauseCondition = new PauseCondition(1, 
				OptionMother.createNewOption());
		assertEquals(Arrays.asList(ComparisonTypes.values()),
				pauseCondition.getValidOperators());
		
		PauseCondition discreteStringPauseCondition = new PauseCondition(1, 
				OptionMother.createNewDiscreteStringOption());
		assertEquals(Arrays.asList(ComparisonTypes.EQ, ComparisonTypes.NE), 
				discreteStringPauseCondition.getValidOperators());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testSetInvalidOperatorLT() {
		PauseCondition discreteStringPauseCondition = new PauseCondition(1, 
				OptionMother.createNewDiscreteStringOption());
		discreteStringPauseCondition.setOperator(ComparisonTypes.LT);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testSetInvalidOperatorGT() {
		PauseCondition discreteStringPauseCondition = new PauseCondition(1, 
				OptionMother.createNewDiscreteStringOption());
		discreteStringPauseCondition.setOperator(ComparisonTypes.GT);
	}
	
	@Test
	public void testSetGetPauseLimit() {
		PauseCondition pauseCondition = new PauseCondition(1, 
				OptionMother.createNewOption());
		pauseCondition.setPauseLimit("1.0");
		assertEquals("1.0", pauseCondition.getPauseLimit());
	}
	
	@Test
	public void testSetPauseLimitDiscrete() {
		PauseCondition pauseCondition = new PauseCondition(1, 
				OptionMother.createNewDiscreteStringOption());
		pauseCondition.setPauseLimit("Value2");
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testSetInvalidPauseLimitIntDouble() {
		PauseCondition integerPauseCondition = new PauseCondition(1, 
				OptionMother.createNewIntegerOption());
		integerPauseCondition.setPauseLimit("1.1");
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testSetInvalidPauseLimitIntString() {
		PauseCondition integerPauseCondition = new PauseCondition(1, 
				OptionMother.createNewIntegerOption());
		integerPauseCondition.setPauseLimit("foo");
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testSetInvalidPauseLimitDoubleString() {
		PauseCondition doublePauseCondition = new PauseCondition(1, 
				OptionMother.createNewDoubleOption());
		doublePauseCondition.setPauseLimit("foo");
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testSetInvalidPauseLimitDiscrete() {
		PauseCondition pauseCondition = new PauseCondition(1, 
				OptionMother.createNewDiscreteStringOption());
		pauseCondition.setPauseLimit("foo");
	}
	
	@Test
	public void testHasContinueLimit() {
		PauseCondition doublePauseCondition = new PauseCondition(1, 
				OptionMother.createNewDoubleOption());
		doublePauseCondition.setOperator(ComparisonTypes.EQ);
		assertFalse("EQ should not have a continue limit", 
				doublePauseCondition.hasContinueLimit());
		doublePauseCondition.setOperator(ComparisonTypes.NE);
		assertFalse("NE should not have a continue limit", 
				doublePauseCondition.hasContinueLimit());
		doublePauseCondition.setOperator(ComparisonTypes.LT);
		assertTrue("LT should have a continue limit", 
				doublePauseCondition.hasContinueLimit());
		doublePauseCondition.setOperator(ComparisonTypes.GT);
		assertTrue("GT should have a continue limit", 
				doublePauseCondition.hasContinueLimit());
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void testSetContinueLimitDiscrete() {
		PauseCondition pauseCondition = new PauseCondition(1, 
				OptionMother.createNewDiscreteStringOption());
		pauseCondition.setContinueLimit("Value2");
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void testSetContinueLimitNotAvailableEQ() {
		PauseCondition doublePauseCondition = new PauseCondition(1, 
				OptionMother.createNewDoubleOption());
		doublePauseCondition.setOperator(ComparisonTypes.EQ);
		doublePauseCondition.setContinueLimit("1.1");
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void testSetContinueLimitNotAvailableNE() {
		PauseCondition doublePauseCondition = new PauseCondition(1, 
				OptionMother.createNewDoubleOption());
		doublePauseCondition.setOperator(ComparisonTypes.NE);
		doublePauseCondition.setContinueLimit("1.1");
	}
	
	@Test
	public void testSetGetContinueLimit() {
		PauseCondition doublePauseCondition = new PauseCondition(1, 
				OptionMother.createNewDoubleOption());
		doublePauseCondition.setOperator(ComparisonTypes.LT);
		doublePauseCondition.setContinueLimit("1.1");
		assertEquals("1.1", doublePauseCondition.getContinueLimit());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testSetInvalidContinueLimitIntDouble() {
		PauseCondition integerPauseCondition = new PauseCondition(1, 
				OptionMother.createNewIntegerOption());
		integerPauseCondition.setOperator(ComparisonTypes.LT);
		integerPauseCondition.setContinueLimit("1.1");
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testSetInvalidContinueLimitIntString() {
		PauseCondition integerPauseCondition = new PauseCondition(1, 
				OptionMother.createNewIntegerOption());
		integerPauseCondition.setOperator(ComparisonTypes.LT);
		integerPauseCondition.setContinueLimit("foo");
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testSetInvalidContinueLimitDoubleString() {
		PauseCondition doublePauseCondition = new PauseCondition(1, 
				OptionMother.createNewDoubleOption());
		doublePauseCondition.setOperator(ComparisonTypes.LT);
		doublePauseCondition.setContinueLimit("foo");
	}
	
	@Test
	public void testIsValueValidInt() {
		PauseCondition integerPauseCondition = new PauseCondition(1, 
				OptionMother.createNewIntegerOption());
		assertTrue(integerPauseCondition.isValidValue("1"));
		assertFalse(integerPauseCondition.isValidValue("1.1"));
		assertFalse(integerPauseCondition.isValidValue("foo"));
	}
	
	@Test
	public void testIsValueValidDouble() {
		PauseCondition doublePauseCondition = new PauseCondition(1, 
				OptionMother.createNewDoubleOption());
		assertTrue(doublePauseCondition.isValidValue("1.1"));
		assertFalse(doublePauseCondition.isValidValue("foo"));
	}
	
	@Test
	public void testIsValueValidString() {
		PauseCondition stringPauseCondition = new PauseCondition(1, 
				OptionMother.createNewStringOption());
		assertTrue(stringPauseCondition.isValidValue("foo"));
	}
	
	@Test
	public void testIsValueValidDiscrete() {
		PauseCondition discretePauseCondition = new PauseCondition(1, 
				OptionMother.createNewDiscreteStringOption());
		assertTrue(discretePauseCondition.isValidValue("Value2"));
		assertFalse(discretePauseCondition.isValidValue("1"));
		assertFalse(discretePauseCondition.isValidValue("1.1"));
		assertFalse(discretePauseCondition.isValidValue("foo"));
	}
	
	@Test
	public void testEqualsReflexive() {
		Option option = OptionMother.createNewDoubleOption();
		PauseCondition pauseCondition = new PauseCondition(1, option);
		assertEquals(pauseCondition, pauseCondition);
	}
	
	@Test
	public void testEqualsSymmetric() {
		Option option = OptionMother.createNewDoubleOption();
		PauseCondition pauseCondition1 = new PauseCondition(1, option);
		PauseCondition pauseCondition2 = new PauseCondition(1, option);
		assertTrue(pauseCondition1.equals(pauseCondition2));
		assertTrue(pauseCondition2.equals(pauseCondition1));
	}
	
	@Test
	public void testEqualsTransitive() {
		Option option = OptionMother.createNewDoubleOption();
		PauseCondition pauseCondition1 = new PauseCondition(1, option);
		PauseCondition pauseCondition2 = new PauseCondition(1, option);
		PauseCondition pauseCondition3 = new PauseCondition(1, option);
		assertTrue(pauseCondition1.equals(pauseCondition2));
		assertTrue(pauseCondition2.equals(pauseCondition3));
		assertTrue(pauseCondition1.equals(pauseCondition3));
	}
	
	@Test
	public void testEqualsConsistent() {
		Option option = OptionMother.createNewDoubleOption();
		PauseCondition pauseCondition1 = new PauseCondition(1, option);
		PauseCondition pauseCondition2 = new PauseCondition(1, option);
		assertEquals(pauseCondition1.equals(pauseCondition2), 
				pauseCondition1.equals(pauseCondition2));
	}
	
	@Test
	public void testEqualsNull() {
		Option option = OptionMother.createNewDoubleOption();
		PauseCondition pauseCondition = new PauseCondition(1, option);
		assertFalse(pauseCondition.equals(null));
	}
	
	@Test
	public void testEqualsDifferentDevice() {
		Option option1 = OptionMother.createNewDoubleOption();
		PauseCondition pauseCondition1 = new PauseCondition(1, option1);
		try {
			Thread.sleep(2); 
			// option id contains timestamp, wait to ensure
			// that they are different
		} catch (InterruptedException e) {
		}
		Option option2 = OptionMother.createNewDoubleOption();
		PauseCondition pauseCondition2 = new PauseCondition(1, option2);
		assertFalse(pauseCondition1.equals(pauseCondition2));
	}
	
	@Test
	public void testEqualsDifferentOperator() {
		Option option = OptionMother.createNewDoubleOption();
		PauseCondition pauseCondition1 = new PauseCondition(1, option);
		pauseCondition1.setOperator(ComparisonTypes.EQ);
		PauseCondition pauseCondition2 = new PauseCondition(1, option);
		pauseCondition2.setOperator(ComparisonTypes.NE);
		assertFalse(pauseCondition1.equals(pauseCondition2));
	}
	
	@Test
	public void testEqualsDifferentPauseLimit() {
		Option option = OptionMother.createNewDoubleOption();
		PauseCondition pauseCondition1 = new PauseCondition(1, option);
		pauseCondition1.setPauseLimit("1.1");
		PauseCondition pauseCondition2 = new PauseCondition(1, option);
		pauseCondition2.setPauseLimit("2.2");
		assertFalse(pauseCondition1.equals(pauseCondition2));
	}
	
	@Test
	public void testEqualsDifferentContinueLimit() {
		Option option = OptionMother.createNewDoubleOption();
		PauseCondition pauseCondition1 = new PauseCondition(1, option);
		pauseCondition1.setOperator(ComparisonTypes.LT);
		pauseCondition1.setContinueLimit("1.1");
		PauseCondition pauseCondition2 = new PauseCondition(1, option);
		pauseCondition2.setOperator(ComparisonTypes.LT);
		pauseCondition2.setContinueLimit("2.2");
		assertFalse(pauseCondition1.equals(pauseCondition2));
	}
	
	@Test
	public void testEquals() {
		Option option = OptionMother.createNewDoubleOption();
		PauseCondition pauseCondition1 = new PauseCondition(1, option);
		PauseCondition pauseCondition2 = new PauseCondition(1, option);
		assertTrue(pauseCondition1.equals(pauseCondition2));
	}
	
	@Test
	public void testHashCodeConsistent() {
		Option option = OptionMother.createNewDoubleOption();
		PauseCondition pauseCondition = new PauseCondition(1, option);
		assertEquals(pauseCondition.hashCode(), pauseCondition.hashCode());
	}
	
	@Test
	public void testHashCodeEquality() {
		Option option = OptionMother.createNewDoubleOption();
		PauseCondition pauseCondition1 = new PauseCondition(1, option);
		PauseCondition pauseCondition2 = new PauseCondition(1, option);
		assertEquals(pauseCondition1, pauseCondition2);
		assertEquals(pauseCondition1.hashCode(), pauseCondition2.hashCode());
	}
	
	private boolean operatorFired;
	@Test
	public void testUpdateEventOperator() {
		this.operatorFired = false;
		Option option = OptionMother.createNewDoubleOption();
		PauseCondition pauseCondition = new PauseCondition(1, option);
		pauseCondition.addModelUpdateListener(new IModelUpdateListener() {
			@Override
			public void updateEvent(ModelUpdateEvent modelUpdateEvent) {
				operatorFired = true;
			}
		});
		pauseCondition.setOperator(ComparisonTypes.NE);
		assertTrue(this.operatorFired);
	}
	
	private boolean pauseLimitFired;
	@Test
	public void testUpdateEventPauseLimit() {
		this.pauseLimitFired = false;
		Option option = OptionMother.createNewDoubleOption();
		PauseCondition pauseCondition = new PauseCondition(1, option);
		pauseCondition.addModelUpdateListener(new IModelUpdateListener() {
			@Override
			public void updateEvent(ModelUpdateEvent modelUpdateEvent) {
				pauseLimitFired = true;
			}
		});
		pauseCondition.setPauseLimit("1.1");
		assertTrue(this.pauseLimitFired);
	}
	
	private boolean continueLimitFired;
	@Test
	public void testUpdateEventContinueLimit() {
		this.continueLimitFired = false;
		Option option = OptionMother.createNewDoubleOption();
		PauseCondition pauseCondition = new PauseCondition(1, option);
		pauseCondition.addModelUpdateListener(new IModelUpdateListener() {
			@Override
			public void updateEvent(ModelUpdateEvent modelUpdateEvent) {
				continueLimitFired = true;
			}
		});
		pauseCondition.setOperator(ComparisonTypes.LT);
		pauseCondition.setContinueLimit("1.1");
		assertTrue(this.continueLimitFired);
	}
}
