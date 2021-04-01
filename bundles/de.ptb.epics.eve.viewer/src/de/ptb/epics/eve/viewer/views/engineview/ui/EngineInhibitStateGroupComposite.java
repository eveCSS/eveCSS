package de.ptb.epics.eve.viewer.views.engineview.ui;

import org.eclipse.swt.widgets.Composite;

/**
 * @author Marcus Michalsky
 * @since 1.36
 */
public class EngineInhibitStateGroupComposite extends EngineGroupComposite {

	public EngineInhibitStateGroupComposite(Composite parent, int style) {
		super(parent, style);
		
		this.setText(" overridden ");
		this.setBGColor(this.yellow);
	}

}
