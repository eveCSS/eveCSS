package de.ptb.epics.eve.data.scandescription.tests;

import static org.junit.Assert.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.ptb.epics.eve.data.EventTypes;
import de.ptb.epics.eve.data.scandescription.Chain;
import de.ptb.epics.eve.data.scandescription.ControlEvent;

/**
 * 
 * @author Marcus Michalsky
 * @since 1.12
 */
public class ChainTest implements PropertyChangeListener {
	private Chain chain;
	
	// indicators for PropertyChangeSupportTest
	private boolean positionCount;
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
		this.positionCount = false;
		this.saveFileName = false;
		this.saveScanDescription = false;
		this.confirmSave = false;
		this.autoNumber = false;
		this.comment = false;
		
		// listen to properties
		this.chain.addPropertyChangeListener(Chain.POSITION_COUNT_PROP, this);
		this.chain.addPropertyChangeListener(Chain.FILE_NAME_PROP, this);
		this.chain.addPropertyChangeListener(Chain.SAVE_SCAN_DESCRIPTION_PROP,
				this);
		this.chain.addPropertyChangeListener(Chain.CONFIRM_SAVE_PROP, this);
		this.chain.addPropertyChangeListener(Chain.AUTO_INCREMENT_PROP, this);
		this.chain.addPropertyChangeListener(Chain.COMMENT_PROP, this);
		
		// manipulate properties
		this.chain.calculatePositionCount();
		this.chain.setSaveFilename("filename");
		this.chain.setSaveScanDescription(true);
		this.chain.setConfirmSave(true);
		this.chain.setAutoNumber(true);
		this.chain.setComment("comment");
		
		// check whether the manipulation was notified
		assertTrue(this.positionCount);
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
		if (e.getPropertyName().equals(Chain.POSITION_COUNT_PROP)) {
			this.positionCount = true;
		} else if (e.getPropertyName().equals(Chain.FILE_NAME_PROP)) {
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
		this.chain.addStartEvent(new ControlEvent(EventTypes.SCHEDULE));
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