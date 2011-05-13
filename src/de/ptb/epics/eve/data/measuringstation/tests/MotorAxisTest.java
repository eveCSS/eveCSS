package de.ptb.epics.eve.data.measuringstation.tests;

import static org.junit.Assert.*;

import org.junit.Test;

import de.ptb.epics.eve.data.measuringstation.MotorAxis;

/**
 * @author Marcus Michalsky
 * @since 0.4.1
 */
public class MotorAxisTest {

	/**
	 * 
	 */
	@Test
	public void testEquals()
	{
		MotorAxis ma = new MotorAxis();
		
		MotorAxis clone = (MotorAxis) ma.clone();
		
		assertEquals(ma, clone);
		
		ma.setId("MyMotorAxis1");
		clone.setId("MyMotorAxis1");
		
		assertEquals(ma,clone);
		assertEquals(ma,ma);
		assertEquals(clone,clone);
		
		clone.setId("MyMotorAxis2");
		
		assertFalse(ma.equals(clone));
	}
}