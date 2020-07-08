package de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes.addmultiply.datetime.ui;

import java.util.Calendar;
import java.util.Date;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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
 * @since 1.34
 */
public class DateSelectorDialog extends Dialog {
	private DateTime date;
	private DateTime time;
	private Date presetDate;

	// no new Java 8 Date/Time API :-(
	private Calendar result;
	
	protected DateSelectorDialog(Shell parentShell, Date presetDate) {
		super(parentShell);
		this.presetDate = presetDate;
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
		this.time.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				System.out.println(e.data);
				// TODO Auto-generated method stub
				super.widgetSelected(e);
			}
		});
		
		this.setPresetTime();
		
		return composite;
	}
	
	private void setPresetTime() {
			Calendar presetCal = Calendar.getInstance();
			presetCal.setTime(this.presetDate);
			this.date.setDate(presetCal.get(Calendar.YEAR), 
					presetCal.get(Calendar.MONTH), 
					presetCal.get(Calendar.DAY_OF_MONTH));
			int hour = presetCal.get(Calendar.AM_PM) == 0
					? presetCal.get(Calendar.HOUR)
					: presetCal.get(Calendar.HOUR) + 12;
			this.time.setTime(hour, 
					presetCal.get(Calendar.MINUTE), 
					presetCal.get(Calendar.SECOND));
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
		// Java 7 Date Time API :-(
		this.result = Calendar.getInstance();
		this.result.set(Calendar.YEAR, this.date.getYear());
		this.result.set(Calendar.MONTH, this.date.getMonth());
		this.result.set(Calendar.DAY_OF_MONTH, this.date.getDay());
		this.result.set(Calendar.SECOND, this.time.getSeconds());
		this.result.set(Calendar.MINUTE, this.time.getMinutes());
		this.result.set(Calendar.HOUR_OF_DAY, this.time.getHours());
				//- this.result.getTimeZone().getRawOffset()/3600000); // ?
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
