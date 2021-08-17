package de.ptb.epics.eve.data.scandescription.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.junit.Before;
import org.junit.Test;

import de.ptb.epics.eve.data.measuringstation.DetectorChannel;
import de.ptb.epics.eve.data.measuringstation.MotorAxis;
import de.ptb.epics.eve.data.measuringstation.Option;
import de.ptb.epics.eve.data.scandescription.Chain;
import de.ptb.epics.eve.data.scandescription.PauseCondition;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener;
import de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent;
import de.ptb.epics.eve.data.tests.mothers.measuringstation.DetectorChannelMother;
import de.ptb.epics.eve.data.tests.mothers.measuringstation.MotorAxisMother;
import de.ptb.epics.eve.data.tests.mothers.measuringstation.OptionMother;
import de.ptb.epics.eve.util.collection.ListUtil;

/**
 * 
 * @author Marcus Michalsky
 * @since 1.36
 */
public class ChainTest {
	private Chain chain;

	@Test
	public void testGetAvailablePlotId() {
		assertEquals(1, this.chain.getAvailablePlotId());
		// TODO
	}
	
	@Test
	public void testGetAvailablePauseConditionId() {
		assertEquals("no pause conditions", 
				1, this.chain.getAvailablePauseConditionId());
		PauseCondition pauseCondition1 = new PauseCondition(1, 
				OptionMother.createNewOption());
		this.chain.addPauseCondition(pauseCondition1);
		assertEquals("1 pause condition", 
				2, this.chain.getAvailablePauseConditionId());
		PauseCondition pauseCondition2 = new PauseCondition(2, 
				OptionMother.createNewOption());
		this.chain.addPauseCondition(pauseCondition2);
		assertEquals("2 pause conditions", 
				3, this.chain.getAvailablePauseConditionId());
		PauseCondition pauseCondition3 = new PauseCondition(3, 
				OptionMother.createNewOption());
		this.chain.addPauseCondition(pauseCondition3);
		assertEquals("3 pause conditions", 
				4, this.chain.getAvailablePauseConditionId());
		this.chain.removePauseCondition(pauseCondition2);
		assertEquals("ids 1 and 3 used, 2 unused", 
				2, this.chain.getAvailablePauseConditionId());
	}
	
	private boolean addPauseConditionPropertyFired;
	@Test
	public void testAddPauseCondition() {
		this.addPauseConditionPropertyFired = false;
		Option option = OptionMother.createNewDoubleOption();
		final PauseCondition pauseCondition = new PauseCondition(1, option);
		this.chain.addPropertyChangeListener(Chain.PAUSE_CONDITION_PROP, 
				new PropertyChangeListener() {
					@Override
					public void propertyChange(PropertyChangeEvent e) {
						assertEquals(pauseCondition, e.getNewValue());
						addPauseConditionPropertyFired = true;
					}
		});
		chain.addPauseCondition(pauseCondition);
		assertTrue(this.addPauseConditionPropertyFired);
	}

	private boolean removePauseConditionPropertyFired;
	@Test
	public void testRemovePauseCondition() {
		this.removePauseConditionPropertyFired = false;
		Option option = OptionMother.createNewDoubleOption();
		final PauseCondition pauseCondition = new PauseCondition(1, option);
		this.chain.addPauseCondition(pauseCondition);
		this.chain.addPropertyChangeListener(Chain.PAUSE_CONDITION_PROP, 
				new PropertyChangeListener() {
					@Override
					public void propertyChange(PropertyChangeEvent e) {
						assertEquals(pauseCondition, e.getOldValue());
						assertEquals(null, e.getNewValue());
						removePauseConditionPropertyFired = true;
					}
		});
		chain.removePauseCondition(pauseCondition);
		assertTrue(removePauseConditionPropertyFired);
	}
	
	private boolean pauseConditionModelUpdate;
	@Test
	public void testModelUpdatePauseConditionListChange() {
		MotorAxis motorAxis = MotorAxisMother.createNewIntTypeMotorAxis();
		PauseCondition pauseCondition1 = new PauseCondition(1, motorAxis);
		this.chain.addPauseCondition(pauseCondition1);
		DetectorChannel channel = DetectorChannelMother.
				createNewIntTypeDetectorChannel();
		PauseCondition pauseCondition2 = new PauseCondition(2, channel);
		this.chain.addPauseCondition(pauseCondition2);
		this.pauseConditionModelUpdate = false;
		this.chain.addModelUpdateListener(new IModelUpdateListener() {
			@Override
			public void updateEvent(ModelUpdateEvent modelUpdateEvent) {
				pauseConditionModelUpdate = true;
			}
		});
		ListUtil.move(chain.getPauseConditions(), 0, 1);
		assertTrue(pauseConditionModelUpdate);
	}
	
	@Before
	public void before() {
		this.chain = new Chain(1);
	}
}