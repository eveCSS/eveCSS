package de.ptb.epics.eve.viewer.views.engineview.ui;

import java.util.List;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;

import de.ptb.epics.eve.ecp1.commands.PauseStatusCommand;
import de.ptb.epics.eve.ecp1.commands.PauseStatusEntry;
import de.ptb.epics.eve.ecp1.types.EngineStatus;
import de.ptb.epics.eve.ecp1.types.PauseStatus;

/**
 * @author Marcus Michalsky
 * @since 1.36
 */
public class EngineInhibitStateGroupComposite extends EngineGroupComposite {
	private final String initText = "";
	private final Color initColor = grey;
	
	public EngineInhibitStateGroupComposite(Composite parent, int style) {
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

	@Override
	public void setEngineStatus(EngineStatus engineStatus) {
		switch (engineStatus) {
		case EXECUTING:
			this.setFGColor(black);
			this.setText("false");
			this.setBGColor(green);
			break;
		case HALTED:
		case STOPPED:
		case INVALID:
		case IDLE_NO_XML_LOADED:
			this.setText("");
			this.setBGColor(grey);
			break;
		case IDLE_XML_LOADED:
		case LOADING_XML:
		case PAUSED:
		default:
			break;
		}
	}

	@Override
	public void setPauseStatus(PauseStatusCommand pauseStatus) {
		if (isPause(pauseStatus.getPauseStatusList())) {
			this.setFGColor(white);
			this.setText("true");
			this.setBGColor(red);
			this.setToolTipText(this.getTooltip(pauseStatus));
		} else if (isOverridden(pauseStatus.getPauseStatusList())) {
			this.setFGColor(black);
			this.setText("overriden");
			this.setBGColor(yellow);
			this.setToolTipText(this.getTooltip(pauseStatus));
		} else {
			this.setFGColor(black);
			this.setText("");
			this.setBGColor(green);
			this.setToolTipText(null);
		}
	}
	
	/*
	 * Checks whether the given list contains an entry with status pause activated
	 */
	private boolean isPause(List<PauseStatusEntry> entries) {
		for (PauseStatusEntry entry : entries) {
			if (entry.getPauseStatus().equals(PauseStatus.PAUSE_ACTIVATED)) {
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
			if (entry.getPauseStatus().equals(PauseStatus.PAUSE_OVERRIDE)) {
				return true;
			}
		}
		return false;
	}
	
	private String getTooltip(PauseStatusCommand pauseStatus) {
		StringBuilder sb = new StringBuilder();
		sb.append("Timestamp: " + pauseStatus.getTimestamp() + "\n");
		for (PauseStatusEntry entry : pauseStatus.getPauseStatusList()) {
			sb.append(entry.getDeviceId() + " : " + entry.getPauseStatus());
		}
		return sb.toString();
	}
}
