package de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes.addmultiply.intdouble.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import de.ptb.epics.eve.data.scandescription.axismode.AdjustParameter;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes.addmultiply.intdouble.AddIntDoubleTextConverter;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes.addmultiply.intdouble.AddIntDoubleValues;

/**
 * @author Marcus Michalsky
 * @since 1.34
 */
public class AddIntDoubleTextConverterTest {
	private static final Double DOUBLE_DELTA = 0.0001d;

	@Test
	public void testconvertIntError() {
		assertNull(AddIntDoubleTextConverter.convertInt("1 / 2 / 3 / 4"));
		assertNull(AddIntDoubleTextConverter.convertInt("1/2/3/4"));
		assertNull(AddIntDoubleTextConverter.
				convertDouble("1.0 / 2.0 / 3.0 / 4.0"));
		assertNull(AddIntDoubleTextConverter.
				convertDouble("1.0/2.0/3.0/4.0"));
	}
	
	@Test
	public void testConvertInt() {
		String input = "1 / 2 / 3 / -";
		this.testInt(input, AdjustParameter.STEPCOUNT, false);
		input = "1/2/3/-";
		this.testInt(input, AdjustParameter.STEPCOUNT, false);
		
		input = "1 / 2 / - / 4";
		this.testInt(input, AdjustParameter.STEPWIDTH, false);
		input = "1 / 2 / - / 4.0";
		this.testInt(input, AdjustParameter.STEPWIDTH, false);
		input = "1/2/-/4";
		this.testInt(input, AdjustParameter.STEPWIDTH, false);
		
		input = "1 / - / 3 / 4";
		this.testInt(input, AdjustParameter.STOP, false);
		input = "1 / - / 3 / 4.0";
		this.testInt(input, AdjustParameter.STOP, false);
		input = "1/-/3/4";
		this.testInt(input, AdjustParameter.STOP, false);
		
		input = "- / 2 / 3 / 4";
		this.testInt(input, AdjustParameter.START, false);
		input = "- / 2 / 3 / 4.0";
		this.testInt(input, AdjustParameter.START, false);
		input = "-/2/3/4";
		this.testInt(input, AdjustParameter.START, false);
	}
	
	@Test 
	public void testConvertIntMainAxis() {
		String input = "1 / 2 / -";
		this.testInt(input, AdjustParameter.STEPWIDTH, true);
		input = "1/2/-";
		this.testInt(input, AdjustParameter.STEPWIDTH, true);
		
		input = "1 / - / 3";
		this.testInt(input, AdjustParameter.STOP, true);
		input = "1/-/3";
		this.testInt(input, AdjustParameter.STOP, true);
		
		input = "- / 2 / 3";
		this.testInt(input, AdjustParameter.START, true);
		input = "-/2/3";
		this.testInt(input, AdjustParameter.START, true);
	}
	
	private void testInt(String input, AdjustParameter adjustParameter, boolean mainAxis) {
		AddIntDoubleValues<Integer> values = AddIntDoubleTextConverter.
				convertInt(input);
		if (!adjustParameter.equals(AdjustParameter.START)) {
			assertEquals(input + ": start incorrect", 
					1, values.getStart().intValue());
		}
		if (!adjustParameter.equals(AdjustParameter.STOP)) {
			assertEquals(input + ": stop incorrect", 
					2, values.getStop().intValue());
		}
		if (!adjustParameter.equals(AdjustParameter.STEPWIDTH)) {
			assertEquals(input + ": stepwidth incorrect", 
					3, values.getStepwidth().intValue());
		}
		if (!adjustParameter.equals(AdjustParameter.STEPCOUNT) && 
				!mainAxis) {
			assertEquals(input + ": stepcount incorrect", 4, values.
					getStepcount().doubleValue(), Double.parseDouble("0.001"));
		}
		assertEquals(input + ": adjust parameter incorrect", 
				adjustParameter, values.getAdjustParameter());
	}
	
	@Test
	public void testConvertDouble() {
		String input = "1.0 / 2.0 / 3.0 / -";
		this.testDouble(input, AdjustParameter.STEPCOUNT, false);
		input = "1.0/2.0/3.0/-";
		this.testDouble(input, AdjustParameter.STEPCOUNT, false);
		
		input = "1.0 / 2.0 / - / 4.0";
		this.testDouble(input, AdjustParameter.STEPWIDTH, false);
		input = "1.0/2.0/-/4.0";
		this.testDouble(input, AdjustParameter.STEPWIDTH, false);
		
		input = "1.0 / - / 3.0 / 4.0";
		this.testDouble(input, AdjustParameter.STOP, false);
		input = "1.0/-/3.0/4.0";
		this.testDouble(input, AdjustParameter.STOP, false);
		
		input = "- / 2.0 / 3.0 / 4.0";
		this.testDouble(input, AdjustParameter.START, false);
		input = "-/2.0/3.0/4.0";
		this.testDouble(input, AdjustParameter.START, false);
	}
	
	@Test
	public void testConvertDoubleMainAxis() {
		String input = "1.0 / 2.0 / -";
		this.testDouble(input, AdjustParameter.STEPWIDTH, true);
		input = "1.0/2.0/-";
		this.testDouble(input, AdjustParameter.STEPWIDTH, true);
		
		input = "1.0 / - / 3.0";
		this.testDouble(input, AdjustParameter.STOP, true);
		input = "1.0/-/3.0";
		this.testDouble(input, AdjustParameter.STOP, true);
		
		input = "- / 2.0 / 3.0";
		this.testDouble(input, AdjustParameter.START, true);
		input = "-/2.0/3.0";
		this.testDouble(input, AdjustParameter.START, true);
	}
	
	private void testDouble(String input, AdjustParameter adjustParameter, boolean mainAxis) {
		AddIntDoubleValues<Double> values = AddIntDoubleTextConverter.
				convertDouble(input);
		if (!adjustParameter.equals(AdjustParameter.START)) {
			assertEquals(input + ": start incorrect", 
					1.0, values.getStart().doubleValue(), DOUBLE_DELTA);
		}
		if (!adjustParameter.equals(AdjustParameter.STOP)) {
			assertEquals(input + ": stop incorrect", 
					2.0, values.getStop().doubleValue(), DOUBLE_DELTA);
		}
		if (!adjustParameter.equals(AdjustParameter.STEPWIDTH)) {
			assertEquals(input + ": stepwidth incorrect", 
					3.0, values.getStepwidth().doubleValue(), DOUBLE_DELTA);
		}
		if (!adjustParameter.equals(AdjustParameter.STEPCOUNT) && 
				!mainAxis) {
			assertEquals(input + ": stepcount incorrect", 
					4.0, values.getStepcount().doubleValue(), DOUBLE_DELTA);
		}
		assertEquals(input + ": adjust parameter incorrect", 
				adjustParameter, values.getAdjustParameter());
	}
}
