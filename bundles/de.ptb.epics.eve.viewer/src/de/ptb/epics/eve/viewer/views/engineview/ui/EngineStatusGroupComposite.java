package de.ptb.epics.eve.viewer.views.engineview.ui;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Marcus Michalsky
 * @since 1.36
 */
public class EngineStatusGroupComposite extends EngineGroupComposite {
	private final String initText = "";
	private final Color initColor = red;
	
	public EngineStatusGroupComposite(Composite parent, int style) {
		super(parent, style);
		this.disable();
	}
	
	@Override
	public void enable() {
		this.setText(initText);
		this.setBGColor(initColor);
	}
	
	@Override
	public void disable() {
		this.setText("");
		this.setBGColor(null);
	}
}
