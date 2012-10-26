package de.ptb.epics.eve.editor.views.motoraxisview.addmultiplycomposite;

import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Marcus Michalsky
 * @since 1.7
 */
public class DateSelectorDialog extends Dialog {

	private static final Logger LOGGER = Logger
			.getLogger(DateSelectorDialog.class.getName());
	
	private DateTime date;
	private DateTime time;

	private Calendar result;
	
	/**
	 * @param parentShell parent shell
	 */
	public DateSelectorDialog(Shell parentShell) {
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
		
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		composite.setLayout(gridLayout);
		
		Label dateLabel = new Label(composite, SWT.NONE);
		dateLabel.setText("Date:");
		
		this.date = new DateTime(composite, SWT.CALENDAR);
		
		Label timeLabel = new Label(composite, SWT.NONE);
		timeLabel.setText("Time:");
		
		this.time = new DateTime(composite, SWT.TIME);
		
		return composite;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void okPressed() {
		this.result = Calendar.getInstance();
		if (LOGGER.isDebugEnabled()) {
		LOGGER.debug(this.date.getYear() + "-" + this.date.getMonth() + "-" + 
					this.date.getDay() + " " + this.time.getHours() + ":" + 
					this.time.getMinutes() + ":" + this.time.getSeconds());
		}
		this.result.set(Calendar.YEAR, this.date.getYear());
		this.result.set(Calendar.MONTH, this.date.getMonth());
		this.result.set(Calendar.DAY_OF_MONTH, this.date.getDay());
		this.result.set(Calendar.SECOND, this.time.getSeconds());
		this.result.set(Calendar.MINUTE, this.time.getMinutes());
		this.result.set(Calendar.HOUR_OF_DAY,
				this.time.getHours());
		this.result.set(Calendar.MILLISECOND, 0);
		super.okPressed();
	}
	
	/**
	 * @return the date (and time) that was selected
	 */
	public Date getDate() {
		return this.result.getTime();
	}
}