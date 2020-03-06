package de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.apache.log4j.Logger;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Text;

import de.ptb.epics.eve.data.measuringstation.MotorAxis;
import de.ptb.epics.eve.data.scandescription.Axis;

/**
 * @author Marcus Michalsky
 * @since 1.34
 */
public class AxesCAComposite extends Composite implements PropertyChangeListener {
	private static final Logger LOGGER = Logger.getLogger(
			AxesCAComposite.class.getName());
	
	private Text lowLimitText;
	private ProgressBar progressBar;
	private ProgressBarPaintListener progressBarPaintListener;
	private ControlDecoration barMinimumControlDecoration;
	private ControlDecoration barMaximumControlDecoration;
	private Text highLimitText;
	
	private String position;
	private Axis currentAxis;
	
	private Image warnImage;
	
	public AxesCAComposite(Composite parent, int style) {
		super(parent, style);
		
		this.warnImage = FieldDecorationRegistry.getDefault().getFieldDecoration(
				FieldDecorationRegistry.DEC_WARNING).getImage();
		
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		gridLayout.horizontalSpacing = 10;
		this.setLayout(gridLayout);
		
		this.lowLimitText = new Text(this, SWT.READ_ONLY | SWT.BORDER);
		this.lowLimitText.setText("");
		
		this.progressBar = new ProgressBar(this, SWT.HORIZONTAL);
		this.progressBar.setState(SWT.NORMAL);
		this.progressBar.setMinimum(0);
		this.progressBar.setMaximum(100);
		this.progressBar.setSelection(0);
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		this.progressBar.setLayoutData(gridData);
		
		this.progressBarPaintListener = new ProgressBarPaintListener();
		
		this.barMinimumControlDecoration = new ControlDecoration(
				this.progressBar, SWT.LEFT);
		this.barMinimumControlDecoration.setImage(warnImage);
		this.barMinimumControlDecoration.setDescriptionText(
				"Current position is below low limit!");
		this.barMinimumControlDecoration.hide();
		
		this.barMaximumControlDecoration = new ControlDecoration(
				this.progressBar, SWT.RIGHT);
		this.barMaximumControlDecoration.setImage(warnImage);
		this.barMaximumControlDecoration.setDescriptionText(
				"Current position is above high limit!");
		this.barMaximumControlDecoration.hide();
		
		this.highLimitText = new Text(this, SWT.READ_ONLY | SWT.BORDER);
		this.highLimitText.setText("");
		
		this.position = "";
		this.currentAxis = null;
		this.setAxis(null);
	}
	
	/**
	 * Sets the axis which should be shown or <code>null</code> to reset.
	 * 
	 * @param axis the axis which should be shown or <code>null</code> to reset
	 */
	public void setAxis(Axis axis) {
		if (this.currentAxis != null && this.currentAxis == axis) {
			return;
		}
		this.lowLimitText.setText("");
		this.lowLimitText.setToolTipText("");
		this.lowLimitText.setEnabled(false);
		this.highLimitText.setText("");
		this.highLimitText.setToolTipText("");
		this.highLimitText.setEnabled(false);
		this.position = Character.toString('\u2014');
		this.progressBar.setSelection(0);
		this.progressBar.setToolTipText("");
		this.progressBar.setEnabled(false);
		
		if (this.currentAxis != null) {
			this.currentAxis.getMotorAxis().disconnect();
			this.currentAxis.getMotorAxis().removePropertyChangeListener(
					MotorAxis.POSITION_PROP, this);
			this.currentAxis.getMotorAxis().removePropertyChangeListener(
					MotorAxis.LOW_LIMIT_PROP, this);
			this.currentAxis.getMotorAxis().removePropertyChangeListener(
					MotorAxis.HIGH_LIMIT_PROP, this);
			this.progressBar.addPaintListener(progressBarPaintListener);
		}
		
		this.currentAxis = axis;
		
		if (axis != null) {
			axis.getMotorAxis().addPropertyChangeListener(
					MotorAxis.POSITION_PROP, this);
			axis.getMotorAxis().addPropertyChangeListener(
					MotorAxis.LOW_LIMIT_PROP, this);
			axis.getMotorAxis().addPropertyChangeListener(
					MotorAxis.HIGH_LIMIT_PROP, this);
			this.progressBar.addPaintListener(progressBarPaintListener);
			this.currentAxis.getMotorAxis().connect();
			if (axis.getMotorAxis().getSoftLowLimit() != null) {
				this.lowLimitText.setEnabled(true);
				this.lowLimitText.setToolTipText(axis.getMotorAxis().
						getSoftLowLimit().getAccess().getVariableID());
			}
			if (axis.getMotorAxis().getSoftHighLimit() != null) {
				this.highLimitText.setEnabled(true);
				this.highLimitText.setToolTipText(axis.getMotorAxis().
						getSoftHighLimit().getAccess().getVariableID());
			}
			this.progressBar.setEnabled(true);
			this.progressBar.setToolTipText(axis.getMotorAxis().getPosition().
					getAccess().getVariableID());
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void propertyChange(final PropertyChangeEvent e) {
		if (e.getPropertyName().equals(MotorAxis.POSITION_PROP)) {
			this.position = e.getNewValue().toString();
		} else if (e.getPropertyName().equals(MotorAxis.LOW_LIMIT_PROP)) {
					lowLimitText.setText(e.getNewValue().toString());
		} else if (e.getPropertyName().equals(MotorAxis.HIGH_LIMIT_PROP)) {
					highLimitText.setText(e.getNewValue().toString());
		}
		calculateBarValue();
		progressBar.redraw();
	}
	
	/*
	 * Calculate bar value by percentage:
	 * bar min is 0%. bar max is 100%.
	 * the absolute difference (|min - max|) is the value for 100% (G).
	 * the position is the percent value (W).
	 * we need the percentage (P).
	 * => P = W * 100 / G
	 */
	private void calculateBarValue() {
		progressBar.setEnabled(true);
		try {
			Double min = this.currentAxis.getMotorAxis().getChannelAccess()
					.getLowLimit();
			Double max = this.currentAxis.getMotorAxis().getChannelAccess()
					.getHighLimit();
			if (min == null || max == null) {
				return;
			}
			Double currentPos = this.currentAxis.getMotorAxis().
					getChannelAccess().getPosition();
			if (currentPos.compareTo(min) <= 0) {
				this.progressBar.setSelection(0);
				this.barMinimumControlDecoration.show();
				return;
			}
			if (currentPos.compareTo(max) >= 0) {
				this.progressBar.setSelection(100);
				this.barMaximumControlDecoration.show();
				return;
			}
			this.barMinimumControlDecoration.hide();
			this.barMaximumControlDecoration.hide();
			double g = Math.abs(min - max);
			double w = currentPos - min;
			double p = (w / g) * 100.0;
			this.progressBar.setSelection((int) p);
		} catch (NullPointerException e) {
			LOGGER.debug(
					"lowlimit/highlimit/position CA is null, no progress bar");
		}
	}
	
	/**
	 * 
	 * @author Marcus Michalsky
	 * @since 1.5
	 */
	private class ProgressBarPaintListener implements PaintListener {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void paintControl(PaintEvent e) {
			/*
			 * calculates the length of the progress bar. 
			 * the ratio of the label text that overlaps with it 
			 * gets a different color.
			 */
			Point point = progressBar.getSize();

			FontMetrics fontMetrics = e.gc.getFontMetrics();
			int width = fontMetrics.getAverageCharWidth() * position.length();
			int height = fontMetrics.getHeight();
			e.gc.setClipping(e.gc.getClipping());
			e.gc.setForeground(Display.getCurrent().getSystemColor(
					SWT.COLOR_BLACK));
			e.gc.drawString(position, (point.x - width) / 2,
					(point.y - height) / 2, true); // transparency boolean

			Rectangle all = e.gc.getClipping();//progressBar.getBounds();
			Rectangle clip = new Rectangle(all.x, all.y, all.width
					* progressBar.getSelection() / 100, all.height);
			e.gc.setClipping(clip);
			e.gc.setForeground(Display.getCurrent().getSystemColor(
					SWT.COLOR_WHITE));
			e.gc.drawString(position, (point.x - width) / 2,
					(point.y - height) / 2, true); // transparency boolean
		}
	}
}
