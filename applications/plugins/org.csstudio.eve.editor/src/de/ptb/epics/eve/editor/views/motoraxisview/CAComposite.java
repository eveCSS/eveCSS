package de.ptb.epics.eve.editor.views.motoraxisview;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Text;

import de.ptb.epics.eve.data.scandescription.Axis;

/**
 * 
 * 
 * @author Marcus Michalsky
 * @since 1.5
 */
public class CAComposite extends Composite implements PropertyChangeListener {
	
	private static Logger logger = Logger.getLogger(CAComposite.class.getName());
	
	private Text llmText;
	private ProgressBar progressBar;
	private ProgressBarPaintListener progressBarPaintListener;
	private Text hlmText;
	
	private Axis currentAxis;
	
	private String position;

	/**
	 * Constructor.
	 * 
	 * @param parent the parent
	 * @param style the style
	 */
	public CAComposite(Composite parent, int style) {
		super(parent, style);
		
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		this.setLayout(gridLayout);
		
		this.llmText = new Text(this, SWT.READ_ONLY | SWT.BORDER);
		
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
		
		this.hlmText = new Text(this, SWT.READ_ONLY | SWT.BORDER);
		
		this.position = "";
		
		this.currentAxis = null;
	}
	
	/**
	 * 
	 * @param axis
	 */
	public void setAxis(Axis axis) {
		if (this.currentAxis == axis) {
			return;
		}
		this.llmText.setText("");
		this.llmText.setToolTipText("");
		this.llmText.setEnabled(false);
		this.hlmText.setText("");
		this.hlmText.setToolTipText("");
		this.hlmText.setEnabled(false);
		this.position = "-----";
		this.progressBar.setSelection(0);
		this.progressBar.setEnabled(false);
		
		if (this.currentAxis != null) {
			this.currentAxis.getMotorAxis().removePropertyChangeListener(
					"position", this);
			this.currentAxis.getMotorAxis().removePropertyChangeListener(
					"offset", this);
			this.currentAxis.getMotorAxis().removePropertyChangeListener(
					"lowlimit", this);
			this.currentAxis.getMotorAxis().removePropertyChangeListener(
					"highlimit", this);
			this.progressBar.removePaintListener(progressBarPaintListener);
		}
		
		this.currentAxis = axis;
		
		if (axis != null) {
			axis.getMotorAxis().addPropertyChangeListener("position", this);
			axis.getMotorAxis().addPropertyChangeListener("offset", this);
			axis.getMotorAxis().addPropertyChangeListener("lowlimit", this);
			axis.getMotorAxis().addPropertyChangeListener("highlimit", this);
			this.progressBar.addPaintListener(progressBarPaintListener);
			
			if (axis.getMotorAxis().getSoftLowLimit() != null) {
				this.llmText.setEnabled(true);
				this.llmText.setToolTipText(axis.getMotorAxis()
						.getSoftLowLimit().getAccess().getVariableID());
			}
			if (axis.getMotorAxis().getSoftHighLimit() != null) {
				this.hlmText.setEnabled(true);
				this.hlmText.setToolTipText(axis.getMotorAxis()
						.getSoftHighLimit().getAccess().getVariableID());
			}
			this.progressBar.setEnabled(true);
			this.progressBar.setToolTipText(axis.getMotorAxis().getPosition()
					.getAccess().getVariableID());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void propertyChange(PropertyChangeEvent e) {
		if (e.getPropertyName().equals("position")) {
			this.position = e.getNewValue().toString();
		} else if (e.getPropertyName().equals("offset")) {
			
		} else if (e.getPropertyName().equals("lowlimit")) {
			this.llmText.setText(e.getNewValue().toString());
		} else if (e.getPropertyName().equals("highlimit")) {
			this.hlmText.setText(e.getNewValue().toString());
		}
		this.calculateBarValue();
		this.progressBar.redraw();
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
		this.progressBar.setEnabled(true);
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
			double g = Math.abs(min - max);
			double w = currentPos - min;
			double p = (w / g) * 100.0;
			this.progressBar.setSelection((int) p);
		} catch (NullPointerException e) {
			logger.debug(
					"lowlimit/highlimit/position CA is null, no progress bar");
			return;
		}
	}
	
	/* ******************************************************************** */
	
	/**
	 * {@link org.eclipse.swt.events.PaintListener} of <code>progressBar</code>.
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