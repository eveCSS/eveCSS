package de.ptb.epics.eve.viewer;

import org.eclipse.swt.widgets.Composite;

import de.ptb.epics.eve.data.measuringstation.MotorAxis;

public class MotorAxisInspectorComposite extends Composite {

	private final MotorAxis motorAxis;
	
	public MotorAxisInspectorComposite( final Composite parent, final int style, final MotorAxis motorAxis ) {
		super( parent, style );
		this.motorAxis = motorAxis;
		initialize();
	}
	
	private void initialize() {
		
		
	}
}
