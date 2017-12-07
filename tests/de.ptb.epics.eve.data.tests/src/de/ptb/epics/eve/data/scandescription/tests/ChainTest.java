package de.ptb.epics.eve.data.scandescription.tests;

import static org.junit.Assert.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
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
public class ChainTest implements PropertyChangeListener {
	private Chain chain;
	
	// indicators for PropertyChangeSupportTest
	private boolean saveFileName;
	private boolean saveScanDescription;
	private boolean confirmSave;
	private boolean autoNumber;
	private boolean comment;
	
	/**
	 * 
	 */
	@Test
	public void testPropertyChangeSupport() {
		// initialize indicators
		this.saveFileName = false;
		this.saveScanDescription = false;
		this.confirmSave = false;
		this.autoNumber = false;
		this.comment = false;
		
		this.chain.setAutoNumber(false);

		// listen to properties
		this.chain.addPropertyChangeListener(Chain.FILE_NAME_PROP, this);
		this.chain.addPropertyChangeListener(Chain.SAVE_SCAN_DESCRIPTION_PROP,
				this);
		this.chain.addPropertyChangeListener(Chain.CONFIRM_SAVE_PROP, this);
		this.chain.addPropertyChangeListener(Chain.AUTO_INCREMENT_PROP, this);
		this.chain.addPropertyChangeListener(Chain.COMMENT_PROP, this);
		
		// manipulate properties
		this.chain.setSaveFilename("filename");
		this.chain.setSaveScanDescription(true);
		this.chain.setConfirmSave(true);
		this.chain.setAutoNumber(true);
		this.chain.setComment("comment");
		
		// check whether the manipulation was notified
		assertTrue(this.saveFileName);
		assertTrue(this.saveScanDescription);
		assertTrue(this.confirmSave);
		assertTrue(this.autoNumber);
		assertTrue(this.comment);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void propertyChange(PropertyChangeEvent e) {
		if (e.getPropertyName().equals(Chain.FILE_NAME_PROP)) {
			this.saveFileName = true;
		} else if (e.getPropertyName().equals(Chain.SAVE_SCAN_DESCRIPTION_PROP)) {
			this.saveScanDescription = true;
		} else if (e.getPropertyName().equals(Chain.CONFIRM_SAVE_PROP)) {
			this.confirmSave = true;
		} else if (e.getPropertyName().equals(Chain.AUTO_INCREMENT_PROP)) {
			this.autoNumber = true;
		} else if (e.getPropertyName().equals(Chain.COMMENT_PROP)) {
			this.comment = true;
		}
	}
	
	/* ******************************************************************** */
	
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