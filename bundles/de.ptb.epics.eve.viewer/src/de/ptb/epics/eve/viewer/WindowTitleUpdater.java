package de.ptb.epics.eve.viewer;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Shell;

import de.ptb.epics.eve.ecp1.client.ECP1Client;
import de.ptb.epics.eve.ecp1.client.interfaces.IConnectionStateListener;
import de.ptb.epics.eve.ecp1.client.interfaces.ISimulationStatusListener;

/**
 * @author Marcus Michalsky
 * @since 1.37
 */
public class WindowTitleUpdater implements IConnectionStateListener, 
		ISimulationStatusListener {
	private static final Logger LOGGER = Logger.getLogger(
			WindowTitleUpdater.class.getName());
	private static final String SIMULATION_PREFIX = "SIM: ";
	
	private String defaultTitle;
	private Shell shell;
	
	public WindowTitleUpdater(ECP1Client ecpClient, Shell shell) {
		this.shell = shell;
		this.defaultTitle =this.shell.getText();
		LOGGER.debug("Default Title:" + defaultTitle);
		ecpClient.addConnectionStateListener(this);
		ecpClient.addSimulationStatusListener(this);
	}
	
	private void setTitle(boolean simulation) {
		final StringBuilder title = new StringBuilder();
		if(simulation) {
			title.append(WindowTitleUpdater.SIMULATION_PREFIX);
		}
		title.append(this.defaultTitle);
		this.shell.getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				shell.setText(title.toString());
			}
		});
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void simulationStatusChanged(boolean simulationButtonEnabled, 
			boolean simulation) {
		LOGGER.debug("EngineView simulationChanged: button: " + 
				simulationButtonEnabled + ", Sim " + simulation);
		this.setTitle(simulation);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stackConnected() {
		// nothing to do here
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stackDisconnected() {
		this.setTitle(false);
	}
}
