package de.ptb.epics.eve.viewer.views.engineview.ui;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;

import de.ptb.epics.eve.ecp1.commands.ChainStatusCommand;
import de.ptb.epics.eve.ecp1.commands.PauseStatusCommand;
import de.ptb.epics.eve.ecp1.types.EngineStatus;

/**
 * @author Marcus Michalsky
 * @since 1.36
 */
public class EngineStatusGroupComposite extends EngineGroupComposite {
	private final String initText = "";
	private final Color initColor = grey;
	
	public EngineStatusGroupComposite(Composite parent, int style) {
		super(parent, style);
		this.disable();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void enable() {
		this.setText(initText);
		this.setBGColor(initColor);
		this.layout();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void disable() {
		this.setText("");
		this.setBGColor(null);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setEngineStatus(EngineStatus engineStatus) {
		switch (engineStatus) {
		case EXECUTING:
			this.setText("executing");
			this.setFGColor(black);
			this.setBGColor(green);
			break;
		case PAUSED:
			break;
		case GUI_PAUSE:
			this.setFGColor(black);
			this.setText("paused");
			this.setBGColor(yellow);
			break;
		case CHAIN_PAUSE:
			this.setFGColor(white);
			this.setText("paused");
			this.setBGColor(red);
			break;
		case HALTED:
		case IDLE_NO_XML_LOADED:
			this.setFGColor(black);
			this.setText("");
			this.setBGColor(grey);
			break;
		case IDLE_XML_LOADED:
		case INVALID:
		case LOADING_XML:
		case STOPPED:
		default:
			this.setFGColor(white);
			this.setText("stopped");
			this.setBGColor(red);
			break;
		}
		this.layout();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setPauseStatus(PauseStatusCommand pauseStatus) {
		// not interested in those messages -> nothing to do
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setChainStatus(ChainStatusCommand chainStatus) {
		// not interested (anymore since 1.38 bugfix) -> nothing to do
	}
}
