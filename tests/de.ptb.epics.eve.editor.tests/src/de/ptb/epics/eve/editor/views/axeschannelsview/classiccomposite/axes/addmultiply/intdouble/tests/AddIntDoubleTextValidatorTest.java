package de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes.addmultiply.intdouble.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Ignore;
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

	@Test
	public void testIsValidInt() {
		ScanModule scanModule = new ScanModule(1);
		Function intFunction = new Function(new Access("accessId", 
				DataTypes.INT, 1, MethodTypes.GET, TransportTypes.LOCAL, 10),
				new TypeValue(DataTypes.INT));
		MotorAxis motorAxis = new MotorAxis(intFunction, intFunction, 
				intFunction, intFunction, intFunction);
		Axis axis = new Axis(scanModule, motorAxis);
		AddIntDoubleTextValidator validator = new AddIntDoubleTextValidator(axis);
		assertNull(validator.isValid("1 / 2 / 3 / -"));
	}
	
	@Test
	public void testIsValidDouble() {
		ScanModule scanModule = new ScanModule(1);
		Function intFunction = new Function(new Access("accessId", 
				DataTypes.DOUBLE, 1, MethodTypes.GET, TransportTypes.LOCAL, 10),
				new TypeValue(DataTypes.DOUBLE));
		MotorAxis motorAxis = new MotorAxis(intFunction, intFunction, 
				intFunction, intFunction, intFunction);
		Axis axis = new Axis(scanModule, motorAxis);
		AddIntDoubleTextValidator validator = new AddIntDoubleTextValidator(axis);
		assertNull(validator.isValid("1.0 / 2.0 / 3.0 / -"));
	}
}
