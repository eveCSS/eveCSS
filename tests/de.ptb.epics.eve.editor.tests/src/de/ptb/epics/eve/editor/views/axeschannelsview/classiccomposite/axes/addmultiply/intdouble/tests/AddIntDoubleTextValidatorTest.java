package de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes.addmultiply.intdouble.tests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.ptb.epics.eve.data.DataTypes;
import de.ptb.epics.eve.data.MethodTypes;
import de.ptb.epics.eve.data.TransportTypes;
import de.ptb.epics.eve.data.TypeValue;
import de.ptb.epics.eve.data.measuringstation.Access;
import de.ptb.epics.eve.data.measuringstation.Function;
import de.ptb.epics.eve.data.measuringstation.MotorAxis;
import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes.addmultiply.intdouble.AddIntDoubleTextValidator;

/**
 * @author Marcus Michalsky
 * @since 1.34
 */
public class AddIntDoubleTextValidatorTest {
	/*
	 * test case where main axis is set, but is test axis is not tested because
	 * if check is successful test case is equivalent to if no main axis is set
	 */
	private static Axis intAxis;
	private static Axis doubleAxis;
	private static AddIntDoubleTextValidator intValidator;
	private static AddIntDoubleTextValidator doubleValidator;
	private static Axis intAxisWithMainAxisSet;
	private static Axis doubleAxisWithMainAxisSet;
	private static AddIntDoubleTextValidator intMainAxisValidator;
	private static AddIntDoubleTextValidator doubleMainAxisValidator;

	@Test
	public void testIsEmpty() {
		assertNotNull("intValidator is null for empty string", 
				intValidator.isValid(""));
		assertNotNull("doubleValidator is null for empty string", 
				doubleValidator.isValid(""));
	}
	
	@Test
	public void testValidInputInt() {
		assertNull("dash on 4", intValidator.isValid("1 / 2 / 3 / -"));
		assertNull("dash on 3", intValidator.isValid("1 / 2 / - / 4"));
		assertNull("dash on 3, stepcount double", 
				intValidator.isValid("1 / 2 / - / 4.0"));
		assertNull("dash on 2", intValidator.isValid("1 / - / 3 / 4"));
		assertNull("dash on 2, stepcount double", 
				intValidator.isValid("1 / - / 3 / 4.0"));
		assertNull("dash on 1", intValidator.isValid("- / 2 / 3 / 4"));
		assertNull("dash on 1, stepcount double", 
				intValidator.isValid("- / 2 / 3 / 4.0"));
		
		assertNull("dash on 4, no spaces", intValidator.isValid("1/2/3/-"));
		assertNull("dash on 3, no spaces", intValidator.isValid("1/2/-/4"));
		assertNull("dash on 3, no spaces, stepcount double", 
				intValidator.isValid("1/2/-/4.0"));
		assertNull("dash on 2, no spaces", intValidator.isValid("1/-/3/4"));
		assertNull("dash on 2, no spaces, stepcount double", 
				intValidator.isValid("1/-/3/4.0"));
		assertNull("dash on 1, no spaces", intValidator.isValid("-/2/3/4"));
		assertNull("dash on 1, no spaces, stepcount double", 
				intValidator.isValid("-/2/3/4.0"));
	}
	
	@Test
	public void testValidInputDouble() {
		assertNull("dash on 4", doubleValidator.isValid("1.0 / 2.0 / 3.0 / -"));
		assertNull("dash on 3", doubleValidator.isValid("1.0 / 2.0 / - / 4.0"));
		assertNull("dash on 2", doubleValidator.isValid("1.0 / - / 3.0 / 4.0"));
		assertNull("dash on 1", doubleValidator.isValid("- / 2.0 / 3.0 / 4.0"));
		
		assertNull("dash on 4, no spaces", 
				doubleValidator.isValid("1.0/2.0/3.0/-"));
		assertNull("dash on 3, no spaces", 
				doubleValidator.isValid("1.0/2.0/-/4.0"));
		assertNull("dash on 2, no spaces", 
				doubleValidator.isValid("1.0/-/3.0/4.0"));
		assertNull("dash on 1, no spaces", 
				doubleValidator.isValid("-/2.0/3.0/4.0"));
	}
	
	@Test
	public void testValidInputIntMainAxis() {
		assertNull("main axis, dash on 3", 
				intMainAxisValidator.isValid("1 / 2 / -"));
		assertNull("main axis, dash on 2", 
				intMainAxisValidator.isValid("1 / - / 3"));
		assertNull("main axis, dash on 1", 
				intMainAxisValidator.isValid("- / 2 / 3"));
		
		assertNull("main axis, dash on 3, no spaces", 
				intMainAxisValidator.isValid("1/2/-"));
		assertNull("main axis, dash on 2, no spaces", 
				intMainAxisValidator.isValid("1/-/3"));
		assertNull("main axis, dash on 1, no spaces", 
				intMainAxisValidator.isValid("-/2/3"));
	}
	
	@Test
	public void testValidInputDoubleMainAxis() {
		assertNull("dash on 3", 
				doubleMainAxisValidator.isValid("1.0 / 2.0 / -"));
		assertNull("dash on 2", 
				doubleMainAxisValidator.isValid("1.0 / - / 3.0"));
		assertNull("dash on 1", 
				doubleMainAxisValidator.isValid("- / 2.0 / 3.0"));
		
		assertNull("dash on 3, no spaces", 
				doubleMainAxisValidator.isValid("1.0/2.0/-"));
		assertNull("dash on 2, no spaces", 
				doubleMainAxisValidator.isValid("1.0/-/3.0"));
		assertNull("dash on 1, no spaces", 
				doubleMainAxisValidator.isValid("-/2.0/3.0"));
	}
	
	@Test
	public void testInvalidInputInt() {
		assertNotNull("4 dashes", intValidator.isValid("- / - / - / -"));
		
		assertNotNull("no dash on 1", intValidator.isValid("1 / - / - / -"));
		assertNotNull("no dash on 2", intValidator.isValid("- / 2 / - / -"));
		assertNotNull("no dash on 3", intValidator.isValid("- / - / 3 / -"));
		assertNotNull("no dash on 4", intValidator.isValid("- / - / - / 4"));
		
		assertNotNull("1,2 dash", intValidator.isValid("- / - / 3 / 4"));
		assertNotNull("1,3 dash", intValidator.isValid("- / 2 / - / 4"));
		assertNotNull("1,4 dash", intValidator.isValid("- / 2 / 3 / -"));
		assertNotNull("2,4 dash", intValidator.isValid("1 / - / 3 / -"));
		assertNotNull("3,4 dash", intValidator.isValid("1 / 2 / - / -"));
		assertNotNull("2,3 dash", intValidator.isValid("1 / - / - / 4"));
		
		assertNotNull("too many values", 
				intValidator.isValid("1 / 2 / 3 / - / 5"));
		
		assertNotNull("not enough values", 
				intValidator.isValid("1 / 2 / -"));
	}
	
	@Test
	public void testInvalidInputDouble() {
		assertNotNull("4 dashes", doubleValidator.isValid("- / - / - / -"));
		
		assertNotNull("no dash on 1", doubleValidator.isValid("1.0 / - / - / -"));
		assertNotNull("no dash on 2", doubleValidator.isValid("- / 2.0 / - / -"));
		assertNotNull("no dash on 3", doubleValidator.isValid("- / - / 3.0 / -"));
		assertNotNull("no dash on 4", doubleValidator.isValid("- / - / - / 4.0"));
		
		assertNotNull("1,2 dash", doubleValidator.isValid("- / - / 3.0 / 4.0"));
		assertNotNull("1,3 dash", doubleValidator.isValid("- / 2.0 / - / 4.0"));
		assertNotNull("1,4 dash", doubleValidator.isValid("- / 2.0 / 3.0 / -"));
		assertNotNull("2,4 dash", doubleValidator.isValid("1.0 / - / 3.0 / -"));
		assertNotNull("3,4 dash", doubleValidator.isValid("1.0 / 2.0 / - / -"));
		assertNotNull("2,3 dash", doubleValidator.isValid("1.0 / - / - / 4.0"));
		
		assertNotNull("too many values", 
				doubleValidator.isValid("1.0 / 2.0 / 3.0 / - / 5.0"));
		
		assertNotNull("not enough values (3)", 
				doubleValidator.isValid("1.0 / 2.0 / -"));
		assertNotNull("not enough values (2)", 
				doubleValidator.isValid("1.0 / -"));
		assertNotNull("not enough values (1)", 
				doubleValidator.isValid("1.0"));
	}
	
	@Test
	public void testInvalidInputIntMainAxis() {
		assertNotNull("main axis, 3 dashes", 
				intMainAxisValidator.isValid("- / - / -"));
		
		assertNotNull("main axis, no dash on 1", 
				intMainAxisValidator.isValid("1 / - / -"));
		assertNotNull("main axis, no dash on 2", 
				intMainAxisValidator.isValid("- / 2 / -"));
		assertNotNull("main axis, no dash on 3", 
				intMainAxisValidator.isValid("- / - / 3"));
		
		assertNotNull("main axis, legal input for: no main axis, dash on 4",
				intMainAxisValidator.isValid("1 / 2 / 3 / -"));
		assertNotNull("main axis, legal input for: no main axis, dash on 3",
				intMainAxisValidator.isValid("1 / 2 / - / 4"));
		assertNotNull("main axis, legal input for: no main axis, dash on 2",
				intMainAxisValidator.isValid("1 / - / 3 / 4"));
		assertNotNull("main axis, legal input for: no main axis, dash on 1",
				intMainAxisValidator.isValid("- / 2 / 3 / 4"));
		
		assertNotNull("not enough values (2)", 
				intMainAxisValidator.isValid("1 / -"));
		assertNotNull("not enough values (1)", 
				intMainAxisValidator.isValid("1"));
	}
	
	@Test
	public void testInvalidInputDoubleMainAxis() {
		assertNotNull("main axis, 3 dashes", 
				doubleMainAxisValidator.isValid("- / - / -"));
		
		assertNotNull("main axis, no dash on 1", 
				doubleMainAxisValidator.isValid("1.0 / - / -"));
		assertNotNull("main axis, no dash on 2", 
				doubleMainAxisValidator.isValid("- / 2.0 / -"));
		assertNotNull("main axis, no dash on 3", 
				doubleMainAxisValidator.isValid("- / - / 3.0"));
		
		assertNotNull("main axis, legal input for: no main axis, dash on 4",
				doubleMainAxisValidator.isValid("1.0 / 2.0 / 3.0 / -"));
		assertNotNull("main axis, legal input for: no main axis, dash on 3",
				doubleMainAxisValidator.isValid("1.0 / 2.0 / - / 4.0"));
		assertNotNull("main axis, legal input for: no main axis, dash on 2",
				doubleMainAxisValidator.isValid("1.0 / - / 3.0 / 4.0"));
		assertNotNull("main axis, legal input for: no main axis, dash on 1",
				doubleMainAxisValidator.isValid("- / 2.0 / 3.0 / 4.0"));
		
		assertNotNull("not enough values (2)", 
				doubleMainAxisValidator.isValid("1.0 / -"));
		assertNotNull("not enough values (1)", 
				doubleMainAxisValidator.isValid("1.0"));
		
		assertNotNull("main axis, illegal double", 
				doubleMainAxisValidator.isValid("1.0a / - / 3.0"));
	}
	
	@BeforeClass
	public static void beforeClass() {
		ScanModule scanModule = new ScanModule(1);
		Function intFunction = new Function(new Access("accessId", 
				DataTypes.INT, 1, MethodTypes.GET, TransportTypes.LOCAL, 10),
				new TypeValue(DataTypes.INT));
		MotorAxis motorAxis = new MotorAxis(intFunction, intFunction, 
				intFunction, intFunction, intFunction);
		intAxis = new Axis(scanModule, motorAxis);
		intValidator = new AddIntDoubleTextValidator(intAxis);
		
		scanModule = new ScanModule(2);
		Function doubleFunction = new Function(new Access("acessId",
				DataTypes.DOUBLE, 1, MethodTypes.GET, TransportTypes.LOCAL, 10),
				new TypeValue(DataTypes.DOUBLE));
		motorAxis = new MotorAxis(doubleFunction, doubleFunction, 
				doubleFunction, doubleFunction, doubleFunction);
		doubleAxis = new Axis(scanModule, motorAxis);
		doubleValidator = new AddIntDoubleTextValidator(doubleAxis);
		
		scanModule = new ScanModule(3);
		MotorAxis mainAxis = new MotorAxis(intFunction, intFunction, 
				intFunction, intFunction, intFunction);
		Axis intMainAxis = new Axis(scanModule, mainAxis);
		scanModule.add(intMainAxis);
		motorAxis = new MotorAxis(intFunction, intFunction, 
				intFunction, intFunction, intFunction);
		intAxisWithMainAxisSet = new Axis(scanModule, motorAxis);
		scanModule.add(intAxisWithMainAxisSet);
		intMainAxis.setMainAxis(true);
		intMainAxisValidator = new AddIntDoubleTextValidator(
				intAxisWithMainAxisSet);
		
		scanModule = new ScanModule(3);
		mainAxis = new MotorAxis(doubleFunction, doubleFunction, 
				doubleFunction, doubleFunction, doubleFunction);
		Axis doubleMainAxis = new Axis(scanModule, mainAxis);
		scanModule.add(doubleMainAxis);
		motorAxis = new MotorAxis(doubleFunction, doubleFunction, 
				doubleFunction, doubleFunction, doubleFunction);
		doubleAxisWithMainAxisSet = new Axis(scanModule, motorAxis);
		scanModule.add(doubleAxisWithMainAxisSet);
		doubleMainAxis.setMainAxis(true);
		doubleMainAxisValidator = new AddIntDoubleTextValidator(
				doubleAxisWithMainAxisSet);
	}
	
	@AfterClass
	public static void afterClass() {
		intAxis = null;
		intValidator = null;
		doubleAxis = null;
		doubleValidator = null;
	}
}
