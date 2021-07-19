package de.ptb.epics.eve.viewer.views.engineview.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

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
	
	public EngineGroupComposite(Composite parent, int style) {
		super(parent, style);
		
		this.red = new Color(this.getDisplay(), new RGB(0xff, 0, 0));
		this.yellow = new Color(this.getDisplay(), new RGB(0xff, 0xff, 0));
		this.green = new Color(this.getDisplay(), new RGB(0, 0xd0, 0));
		this.grey = new Color(this.getDisplay(), new RGB(127, 127, 127));
		
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
	
	protected void setText(String text) {
		this.text.setText(text);
	}
	
	protected void setBGColor(Color color) {
		this.setBackground(color);
		this.text.setBackground(color);
	}
}
