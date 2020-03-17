package de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes.addmultiply.datetime.ui;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;

/**
 * @author Marcus Michalsky
 * @since 1.34
 */
public class DurationSelectorDialog extends Dialog {
	private static final Logger LOGGER = Logger.getLogger(
			DurationSelectorDialog.class.getName());
	private Duration presetDuration;
	
	private Spinner hourSpinner;
	private Spinner minuteSpinner;
	private Spinner secondSpinner;
	private Spinner millisSpinner;
	
	private Duration result;
	
	public DurationSelectorDialog(Shell parentShell, Duration presetDuration) {
		super(parentShell);
		this.presetDuration = presetDuration;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.CENTER;
		composite.setLayoutData(gridData);
		
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 4;
		composite.setLayout(gridLayout);
		
		Label hourLabel = new Label(composite, SWT.NONE);
		hourLabel.setText("h");
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.CENTER;
		hourLabel.setLayoutData(gridData);
		Label minuteLabel = new Label(composite, SWT.NONE);
		minuteLabel.setText("min");
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.CENTER;
		minuteLabel.setLayoutData(gridData);
		Label secondLabel = new Label(composite, SWT.NONE);
		secondLabel.setText("s");
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.CENTER;
		secondLabel.setLayoutData(gridData);
		Label millisLabel = new Label(composite, SWT.NONE);
		millisLabel.setText("ms");
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.CENTER;
		millisLabel.setLayoutData(gridData);
		
		hourSpinner = new Spinner(composite, SWT.NONE);
		minuteSpinner = new Spinner(composite, SWT.NONE);
		secondSpinner = new Spinner(composite, SWT.NONE);
		millisSpinner = new Spinner(composite, SWT.NONE);
		millisSpinner.setMaximum(999);

		this.setPresetDuration();
		
		return composite;
	}
	
	private void setPresetDuration() {
		this.hourSpinner.setSelection(this.presetDuration.getHours());
		this.minuteSpinner.setSelection(this.presetDuration.getMinutes());
		this.secondSpinner.setSelection(this.presetDuration.getSeconds());
		this.millisSpinner.setSelection(0);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Point getInitialLocation(Point initialSize) {
		Point mouseLocation = Display.getCurrent().getCursorLocation();
		return new Point(mouseLocation.x - initialSize.x, mouseLocation.y);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void okPressed() {
		try {
			DatatypeFactory factory = DatatypeFactory.newInstance();
			String duration = "P0Y0M0DT" + hourSpinner.getText() + "H" + 
					minuteSpinner.getText() + "M" + 
					secondSpinner.getText() + "." + 
					millisSpinner.getText() + "S";
			this.result = factory.newDuration(duration);
		} catch (DatatypeConfigurationException | IllegalArgumentException e) {
			LOGGER.error(e.getMessage(), e);
			this.result = null;
		}
		super.okPressed();
	}
	
	/**
	 * @return the duration that was selected
	 */
	public Duration getDuration() {
		return this.result;
	}
}
