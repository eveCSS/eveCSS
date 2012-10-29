package de.ptb.epics.eve.editor.views.motoraxisview.addmultiplycomposite;

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
 * @since 1.7
 */
public class DurationSelectorDialog extends Dialog {
	
	private static final Logger LOGGER = Logger
			.getLogger(DurationSelectorDialog.class.getName());
	
	private Duration duration;
	
	private Spinner hourSpinner;
	private Spinner minuteSpinner;
	private Spinner secondSpinner;
	private Spinner millisSpinner;
	
	/**
	 * @param parentShell the parent shell
	 */
	public DurationSelectorDialog(Shell parentShell) {
		super(parentShell);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Point getInitialLocation(Point initialSize) {
		Point mouseLocation = Display.getCurrent().getCursorLocation();
		Point dialogLocation = new Point(mouseLocation.x - initialSize.x,
				mouseLocation.y);
		return dialogLocation;
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
		Label minuteLabel = new Label(composite, SWT.NONE);
		minuteLabel.setText("m");
		Label secondLabel = new Label(composite, SWT.NONE);
		secondLabel.setText("s");
		Label millisLabel = new Label(composite, SWT.NONE);
		millisLabel.setText("ms");
		
		hourSpinner = new Spinner(composite, SWT.NONE);
		minuteSpinner = new Spinner(composite, SWT.NONE);
		secondSpinner = new Spinner(composite, SWT.NONE);
		millisSpinner = new Spinner(composite, SWT.NONE);
		
		return composite;
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
			this.duration = factory.newDuration(duration);
		} catch (DatatypeConfigurationException e) {
			LOGGER.error(e.getMessage(), e);
			this.duration = null;
		} catch (IllegalArgumentException e) {
			LOGGER.error(e.getMessage(), e);
		}
		super.okPressed();
	}
	
	/**
	 * @return the duration that was selected
	 */
	public Duration getDuration() {
		return this.duration;
	}
}