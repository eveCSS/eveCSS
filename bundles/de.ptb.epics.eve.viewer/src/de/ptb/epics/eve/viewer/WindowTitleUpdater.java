package de.ptb.epics.eve.viewer;

import org.eclipse.ui.PlatformUI;

import de.ptb.epics.eve.ecp1.client.ECP1Client;
import de.ptb.epics.eve.ecp1.client.interfaces.IConnectionStateListener;
import de.ptb.epics.eve.ecp1.client.interfaces.ISimulationStatusListener;

/**
 * @author Marcus Michalsky
 * @since 1.37
 */
public class WindowTitleUpdater implements IConnectionStateListener, ISimulationStatusListener {
	private static final String SIMULATION_PREFIX = "SIM: ";
	
	private String defaultTitle;
	
	public WindowTitleUpdater(ECP1Client ecpClient) {
		this.defaultTitle = PlatformUI.getWorkbench().
				getActiveWorkbenchWindow().getShell().getText();
		ecpClient.addConnectionStateListener(this);
		ecpClient.addSimulationStatusListener(this);
	}
	
	private void setTitle(boolean simulation) {
		StringBuilder title = new StringBuilder();
		if(simulation) {
			title.append(WindowTitleUpdater.SIMULATION_PREFIX);
		}
		title.append(this.defaultTitle);
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().
				getShell().setText(title.toString());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void simulationStatusChanged(boolean simulationButtonEnabled, boolean simulation) {
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
