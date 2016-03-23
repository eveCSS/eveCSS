package de.ptb.epics.eve.viewer.views.deviceinspectorview.ui;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * Not used right now. Planned to be used as super class for axes, channels 
 * and devices in the DeviceInspectorView to avoid redundant code.
 * 
 * @author Marcus Michalsky
 * @since 1.26
 */
public abstract class DeviceInspectorSashComposite extends Composite {
	private SashForm sashForm;
	private Label icon;
	private Label text;
	private TableViewer tableViewer;
	
	public DeviceInspectorSashComposite(SashForm sashForm, Image icon, String text) {
		super(sashForm, SWT.BORDER);
		this.sashForm = sashForm;
		
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		this.setLayout(gridLayout);
		
		this.icon = new Label(this, SWT.NONE);
		this.icon.setImage(icon);
		this.icon.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				// TODO Auto-generated method stub
				super.mouseDown(e);
			}
		});
		
		this.text = new Label(this, SWT.NONE);
		this.text.setText(text);
		
		this.createViewer();
	}
	
	private void createViewer() {
		this.tableViewer = new TableViewer(this, SWT.BORDER | SWT.V_SCROLL | 
				SWT.FULL_SELECTION | SWT.MULTI);
		GridData gridData = new GridData();
		gridData.horizontalSpan = 3;
		gridData.minimumHeight = 25;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		this.tableViewer.getTable().setLayoutData(gridData);
		this.tableViewer.getTable().setHeaderVisible(true);
		this.tableViewer.getTable().setLinesVisible(true);
	}
	
	
}