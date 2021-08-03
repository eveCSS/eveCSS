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

	private boolean addPauseConditionPropertyFired;
	@Test
	public void testAddPauseCondition() {
		this.addPauseConditionPropertyFired = false;
		Option option = OptionMother.createNewDoubleOption();
		final PauseCondition pauseCondition = new PauseCondition(option);
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
		final PauseCondition pauseCondition = new PauseCondition(option);
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
		PauseCondition pauseCondition1 = new PauseCondition(motorAxis);
		this.chain.addPauseCondition(pauseCondition1);
		DetectorChannel channel = DetectorChannelMother.
				createNewIntTypeDetectorChannel();
		PauseCondition pauseCondition2 = new PauseCondition(channel);
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