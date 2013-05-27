package de.ptb.epics.eve.data.scandescription.tests;

import static org.junit.Assert.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.ptb.epics.eve.data.scandescription.ScanDescription;
import de.ptb.epics.eve.data.tests.internal.Configurator;

public class ScanDescriptionTest implements PropertyChangeListener {

	private ScanDescription scanDescription;
	
	// indicators for PropertyChangeSupportTest
	private boolean repeatCount;
	
	/**
	 * 
	 */
	@Test
	public void testPropertyChangeSupport() {
		// initialize indicators
		this.repeatCount = false;
		
		// listen to properties
		this.scanDescription.addPropertyChangeListener(
				ScanDescription.REPEAT_COUNT_PROP, this);
		
		// manipulate properties
		this.scanDescription.setRepeatCount(1);
		
		// check whether the manipulation was notified
		assertTrue(this.repeatCount);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void propertyChange(PropertyChangeEvent e) {
		if (e.getPropertyName().equals(ScanDescription.REPEAT_COUNT_PROP)) {
			this.repeatCount = true;
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
		this.scanDescription = new ScanDescription(
				Configurator.getMeasuringStation());
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