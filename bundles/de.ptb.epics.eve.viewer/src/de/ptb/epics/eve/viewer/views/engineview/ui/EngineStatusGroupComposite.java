package de.ptb.epics.eve.viewer.views.engineview.ui;

import org.eclipse.swt.widgets.Composite;

import de.ptb.epics.eve.ecp1.client.interfaces.IConnectionStateListener;
import de.ptb.epics.eve.ecp1.client.interfaces.IEngineStatusListener;
import de.ptb.epics.eve.ecp1.types.EngineStatus;

/**
 * @author Marcus Michalsky
 * @since 1.36
 */
public class EngineStatusGroupComposite extends EngineGroupComposite implements 
		IConnectionStateListener, IEngineStatusListener {
	
	public EngineStatusGroupComposite(Composite parent, int style) {
		super(parent, style);
		this.setText("running");
		this.setBGColor(this.green);
	}

	@Override
	public void engineStatusChanged(EngineStatus engineStatus, String xmlName, int repeatCount) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stackConnected() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stackDisconnected() {
		// TODO Auto-generated method stub
		
	}

}
