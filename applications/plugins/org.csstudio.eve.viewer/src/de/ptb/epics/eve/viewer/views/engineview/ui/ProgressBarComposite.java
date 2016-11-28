package de.ptb.epics.eve.viewer.views.engineview.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ProgressBar;

import de.ptb.epics.eve.ecp1.helper.progresstracker.EngineProgressTracker;
import de.ptb.epics.eve.ecp1.helper.progresstracker.Progress;
import de.ptb.epics.eve.ecp1.helper.statustracker.EngineStatusTracker;
import de.ptb.epics.eve.viewer.Activator;

/**
 * @author Marcus Michalsky
 * @since 1.10
 */
public class ProgressBarComposite extends Composite implements PropertyChangeListener {
	private static final Logger LOGGER = Logger
			.getLogger(ProgressBarComposite.class.getName());
	
	private ProgressBar progressBar;
	private ProgressBarPaintListener progressBarPaintListener;

	private Progress progress;
	
	private Font font;
	
	private EngineStatusTracker engineStatusTracker;
	private EngineProgressTracker engineProgressTracker;
	
	/**
	 * @param parent the parent
	 * @param style the style
	 * @param connected initial connection state
	 */
	public ProgressBarComposite(Composite parent, int style, boolean connected) {
		super(parent, style);
		FillLayout fillLayout = new FillLayout();
		fillLayout.marginHeight = 3;
		fillLayout.marginWidth = 3;
		this.setLayout(fillLayout);
		this.progressBar = new ProgressBar(this, SWT.HORIZONTAL);
		this.progressBar.setState(SWT.NORMAL);
		this.progress = null;
		this.progressBar.setEnabled(false);
		this.progressBarPaintListener = new ProgressBarPaintListener(
				this.progressBar);
		this.progressBar.addPaintListener(this.progressBarPaintListener);
		
		this.engineStatusTracker = new EngineStatusTracker(
				Activator.getDefault().getEcp1Client(), this);
		this.engineProgressTracker = new EngineProgressTracker(
				Activator.getDefault().getEcp1Client(), this);
		
		FontData fdata = Display.getCurrent().getSystemFont().getFontData()[0];
		fdata.setStyle(SWT.BOLD);
		this.font = new Font(Display.getCurrent(), fdata);
	}
	
	/**
	 * @author Marcus Michalsky
	 * @since 1.10
	 */
	private class ProgressBarPaintListener implements PaintListener {
		private ProgressBar progressBar;
		
		/**
		 * @param bar the progress bar
		 */
		public ProgressBarPaintListener(ProgressBar bar) {
			this.progressBar = bar;
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void paintControl(PaintEvent e) {
			if (progress == null) {
				return;
			}

			/*
			 * calculates the length of the progress bar. 
			 * the ratio of the label text that overlaps with it 
			 * gets a different color.
			 */
			Point point = progressBar.getSize();

			String progressBarText = getProgressBarText(progress);
			
			FontMetrics fontMetrics = e.gc.getFontMetrics();
			int width = fontMetrics.getAverageCharWidth() * progressBarText.length();
			int height = fontMetrics.getHeight();
			e.gc.setClipping(e.gc.getClipping());
			e.gc.setAntialias(SWT.ON);
			e.gc.setTextAntialias(SWT.ON);
			e.gc.setFont(font);
			e.gc.setForeground(Display.getCurrent().getSystemColor(
					SWT.COLOR_BLACK));
			e.gc.drawString(progressBarText, (point.x - width) / 2,
					(point.y - height) / 2, true); // transparency boolean

			Rectangle all = e.gc.getClipping();//progressBar.getBounds();
			Rectangle clip = new Rectangle(all.x, all.y, all.width
					* progressBar.getSelection() / progress.getMaximum(), all.height);
			e.gc.setClipping(clip);
			e.gc.setForeground(Display.getCurrent().getSystemColor(
					SWT.COLOR_WHITE));
			e.gc.drawString(progressBarText, (point.x - width) / 2,
					(point.y - height) / 2, true); // transparency boolean
		}
	}

	private String getProgressBarText(Progress progress) {
		if (progress.getCurrent() == null) {
			return "no progress data available";
		} else {
			StringBuffer percentage = new StringBuffer();

			percentage.append(" (");
			percentage.append((int) Math.ceil((float) progress.getCurrent() 
					/ (float) progress.getMaximum() * 100));
			percentage.append(" %)");

			String position = Integer.toString(progress.getCurrent()) + 
					" / " + Integer.toString(progress.getMaximum())
					+ " positions" + percentage.toString();

			return position;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void propertyChange(final PropertyChangeEvent e) {
		switch (e.getPropertyName()) {
		case EngineStatusTracker.ENGINE_STATUS_PROP:
			LOGGER.debug("Engine status has changed, enable/disable widget");
			this.progressBar.getDisplay().syncExec(new Runnable() {
				@Override
				public void run() {
					if (engineStatusTracker.isConnected()) {
						progressBar.setEnabled(true);
					} else {
						progressBar.setEnabled(false);
					}
				}
			});
			break;
		case EngineProgressTracker.PROGRESS_PROP:
			LOGGER.debug("progress has changed, updating text label");
			this.progressBar.getDisplay().syncExec(new Runnable() {
				@Override
				public void run() {
					if (engineProgressTracker.getProgress() == null) {
						LOGGER.debug("Progress is null -> reset and disable");
						progress = null;
						progressBar.setSelection(0);
						progressBar.setEnabled(false);
					} else {
						LOGGER.debug("setting maximum");
						progress = engineProgressTracker.getProgress();
						progressBar.setMaximum(progress.getMaximum());
						if (progress.getCurrent() != null) {
							LOGGER.debug("setting current progress");
							progressBar.setSelection(progress.getCurrent());
						}
						progressBar.setEnabled(true);
					}
				}
			});
			break;
		}
		this.progressBar.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				progressBar.redraw();
			}
		});
	}
}