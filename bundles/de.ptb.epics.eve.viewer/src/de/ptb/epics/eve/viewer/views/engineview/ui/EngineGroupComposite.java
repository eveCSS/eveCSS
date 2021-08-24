package de.ptb.epics.eve.viewer.views.engineview.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import de.ptb.epics.eve.ecp1.commands.ChainStatusCommand;
import de.ptb.epics.eve.ecp1.commands.PauseStatusCommand;
import de.ptb.epics.eve.ecp1.types.EngineStatus;

/**
 * @author Marcus Michalsky
 * @since 1.36
 */
public abstract class EngineGroupComposite extends Composite {
	private Label text;
	protected final Color red;
	protected final Color yellow;
	protected final Color green;
	protected final Color grey;
	protected final Color white;
	protected final Color black;
	
	public EngineGroupComposite(Composite parent, int style) {
		super(parent, style);
		
		this.red = new Color(this.getDisplay(), new RGB(0xff, 0, 0));
		this.yellow = new Color(this.getDisplay(), new RGB(0xff, 0xff, 0));
		this.green = new Color(this.getDisplay(), new RGB(0, 0xd0, 0));
		this.grey = new Color(this.getDisplay(), new RGB(190, 190, 190));
		this.white = new Color(this.getDisplay(), new RGB(255, 255, 255));
		this.black = new Color(this.getDisplay(), new RGB(0, 0, 0));
		
		GridLayout gridLayout = new GridLayout();
		this.setLayout(gridLayout);
		this.text = new Label(this, SWT.CENTER);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.CENTER;
		gridData.verticalAlignment = GridData.CENTER;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		this.text.setText("unknown");
		this.setBGColor(this.grey);
		this.text.setLayoutData(gridData);
	}
	
	public abstract void enable();
	public abstract void disable();
	public abstract void setEngineStatus(EngineStatus engineStatus);
	public abstract void setPauseStatus(PauseStatusCommand pauseStatus);
	public abstract void setChainStatus(ChainStatusCommand chainStatus);
	
	protected void setText(String text) {
		this.text.setText(text);
		//this.text.getParent().layout();
	}
	
	protected void setFGColor(Color color) {
		this.text.setForeground(color);
		//this.text.getParent().layout();
	}
	
	protected void setBGColor(Color color) {
		this.setBackground(color);
		this.text.setBackground(color);
		//this.text.getParent().layout();
	}
}
