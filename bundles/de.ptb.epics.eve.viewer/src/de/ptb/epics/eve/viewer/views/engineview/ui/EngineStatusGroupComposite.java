package de.ptb.epics.eve.viewer.views.engineview.ui;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;

import de.ptb.epics.eve.ecp1.commands.ChainStatusCommand;
import de.ptb.epics.eve.ecp1.commands.PauseStatusCommand;
import de.ptb.epics.eve.ecp1.types.EngineStatus;
import de.ptb.epics.eve.ecp1.types.ScanModuleReason;
import de.ptb.epics.eve.ecp1.types.ScanModuleStatus;

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
			break;
		case PAUSED:
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
		boolean allExecuting = true;
		boolean smPaused = false;
		boolean userPause = false;
		
		for (int i : chainStatus.getAllScanModuleIds()) {
			if (!chainStatus.getScanModuleStatus(i).equals(ScanModuleStatus.EXECUTING)) {
				allExecuting = false;
			}
			if (chainStatus.getScanModuleStatus(i).equals(ScanModuleStatus.PAUSED)) {
				smPaused = true;
			}
			if (chainStatus.getScanModuleReason(i).equals(ScanModuleReason.USER_PAUSE)) {
				userPause = true;
			}
		}
		
		if (allExecuting) {
			this.setText("executing");
			this.setFGColor(black);
			this.setBGColor(green);
		}
		if (smPaused && !userPause) {
			this.setFGColor(white);
			this.setText("paused");
			this.setBGColor(red);
		}
		if (smPaused && userPause) {
			this.setFGColor(black);
			this.setText("paused");
			this.setBGColor(yellow);
		}
		
		this.layout();
	}
}
