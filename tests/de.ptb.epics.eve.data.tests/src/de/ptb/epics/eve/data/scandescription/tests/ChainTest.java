package de.ptb.epics.eve.data.scandescription.tests;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.ptb.epics.eve.data.measuringstation.event.ScheduleEvent;
import de.ptb.epics.eve.data.scandescription.Chain;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.data.scandescription.StartEvent;

/**
 * 
 * @author Marcus Michalsky
 * @since 1.12
 */
public class ChainTest {
	private Chain chain;

	@Ignore
	@Test
	public void noTest() {
		
	}
	
	/**
	 * Class wide setup method of the test
	 */
	@BeforeClass
	public static void setUpBeforeClass() {
	}
	
	/**
	 * test wide set up method
	 */
	@Before
	public void setUp() {
		this.chain = new Chain(1);
		Chain zeroChain = new Chain(0);
		ScanModule zeroModule = new ScanModule(0);
		zeroChain.add(zeroModule);
		this.chain.setStartEvent(new StartEvent(new ScheduleEvent(zeroModule)
				, chain));
		ScanModule sm = new ScanModule(1);
		sm.setName("SM1");
		this.chain.add(sm);
	}
	
	/**
	 * test wide tear down method
	 */
	@After
	public void tearDown() {
	}
	
	/**
	 * class wide tear down method
	 */
	@AfterClass
	public static void tearDownAfterClass() {
	}
}