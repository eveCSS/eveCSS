package de.ptb.epics.eve.viewer.views.engineview.ui;

import java.util.List;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;

import de.ptb.epics.eve.ecp1.commands.ChainStatusCommand;
import de.ptb.epics.eve.ecp1.commands.PauseStatusCommand;
import de.ptb.epics.eve.ecp1.commands.PauseStatusEntry;
import de.ptb.epics.eve.ecp1.types.EngineStatus;
import de.ptb.epics.eve.ecp1.types.PauseStatus;
import de.ptb.epics.eve.viewer.Activator;

/**
 * @author Marcus Michalsky
 * @since 1.36
 */
public class EngineInhibitStateGroupComposite extends EngineGroupComposite {
	private final String initText = "";
	private final Color initColor = grey;
	private InhibitStateTooltip tooltip;
	
	public EngineInhibitStateGroupComposite(Composite parent, int style) {
		super(parent, style);
		this.tooltip = new InhibitStateTooltip(this);
		this.tooltip.setHideDelay(0);
		this.disable();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void enable() {
		this.setText(initText);
		this.setBGColor(initColor);
		this.tooltip.activate();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void disable() {
		this.setText("");
		this.setBGColor(null);
		this.tooltip.deactivate();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setEngineStatus(EngineStatus engineStatus) {
		switch (engineStatus) {
		case HALTED:
		case STOPPED:
		case INVALID:
		case IDLE_NO_XML_LOADED:
			this.setText("");
			this.setBGColor(grey);
			this.tooltip.setPauseConditions(null);
			this.tooltip.hide();
			this.tooltip.deactivate();
			break;
		case IDLE_XML_LOADED:
			this.tooltip.setPauseConditions(Activator.getDefault().
				getCurrentScanDescription().getChain(1).getPauseConditions());
			this.tooltip.activate();
			break;
		case EXECUTING:
		case LOADING_XML:
		case PAUSED:
		default:
			break;
		}
		this.layout();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setPauseStatus(PauseStatusCommand pauseStatus) {
		if (isPause(pauseStatus.getPauseStatusList())) {
			this.setFGColor(white);
			this.setText("true");
			this.setBGColor(red);
		} else if (!isPause(pauseStatus.getPauseStatusList()) && 
				isOverridden(pauseStatus.getPauseStatusList())) {
			this.setFGColor(black);
			this.setText("overriden");
			this.setBGColor(yellow);
		} else {
			this.setFGColor(black);
			this.setText("false");
			this.setBGColor(green);
		}
		this.layout();
		this.tooltip.setPauseStatus(pauseStatus);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setChainStatus(ChainStatusCommand chainStatus) {
		// not interested in those messages
	}
	/*
	 * Checks whether the given list contains an entry with status pause activated
	 */
	private boolean isPause(List<PauseStatusEntry> entries) {
		for (PauseStatusEntry entry : entries) {
			if (entry.getPauseStatus().equals(PauseStatus.PAUSE_ACTIVE_NOT_OVERRIDDEN)) {
				return true;
			}
		}
		return false;
	}
	
	/*
	 * Checks whether the given list contains an entry with status overridden
	 */
	private boolean isOverridden(List<PauseStatusEntry> entries) {
		for (PauseStatusEntry entry : entries) {
			if (entry.getPauseStatus().equals(PauseStatus.PAUSE_ACTIVE_OVERRIDDEN)) {
				return true;
			}
		}
		return false;
	}
}
