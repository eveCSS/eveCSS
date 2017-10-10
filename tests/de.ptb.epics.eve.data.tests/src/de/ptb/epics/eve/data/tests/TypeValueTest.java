package de.ptb.epics.eve.data.tests;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.ptb.epics.eve.data.TypeValue;
import de.ptb.epics.eve.data.tests.mothers.measuringstation.TypeValueMother;

/**
 * @author Marcus Michalsky
 * @since 1.26
 */
public class TypeValueTest {
	private List<TypeValue> typeValues;
	
	@Test
	public void testClone() {
		for (TypeValue typeValue : typeValues) {
			TypeValue clone = (TypeValue)typeValue.clone();
			
			assertEquals(typeValue.getType(), clone.getType());
			assertEquals(typeValue.isDiscrete(), clone.isDiscrete());
			assertEquals(typeValue.getValues(), clone.getValues());
		}
	}
	
	/**
	 * Class wide setup method of the test.
	 */
	@BeforeClass
	public static void setUpBeforeClass() {
	}

	/**
	 * Class wide tear down method.
	 */
	@AfterClass
	public static void tearDownAfterClass() {
	}

	/**
	 * Test wide set up method.
	 */
	@Before
	public void setUp() {
		this.typeValues = new ArrayList<>();
		this.typeValues.add(TypeValueMother.createNewTypeValue());
	}

	/**
	 * Test wide tear down method.
	 */
	@After
	public void tearDown() {
	}
}