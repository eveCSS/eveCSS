package de.ptb.epics.eve.data.scandescription.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.junit.Before;
import org.junit.Test;

import de.ptb.epics.eve.data.measuringstation.Option;
import de.ptb.epics.eve.data.scandescription.Chain;
import de.ptb.epics.eve.data.scandescription.PauseCondition;
import de.ptb.epics.eve.data.tests.mothers.measuringstation.OptionMother;

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
	
	@Before
	public void before() {
		this.chain = new Chain(1);
	}
}