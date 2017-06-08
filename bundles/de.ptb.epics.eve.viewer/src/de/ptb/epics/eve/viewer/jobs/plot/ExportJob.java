package de.ptb.epics.eve.viewer.jobs.plot;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import de.ptb.epics.eve.util.pdf.PDFCreator;
import de.ptb.epics.eve.util.pdf.PlotStats;
import de.ptb.epics.eve.util.ui.swt.AWTBridge;

/**
 * @author Marcus Michalsky
 * @since 1.16
 */
public class ExportJob extends Job {
	private File fileName;
	private String scanDescription;
	private Image xyGraph;
	private List<PlotStats> statList;
	
	/**
	 * 
	 * @param jobName
	 * @param file
	 * @param scanDescription
	 * @param graph
	 * @param statList
	 */
	public ExportJob(String jobName, File file,
			String scanDescription, Image graph,
			List<PlotStats> statList) {
		super(jobName);
		this.fileName = file;
		this.scanDescription = scanDescription;
		this.xyGraph = graph;
		this.statList = statList;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IStatus run(final IProgressMonitor monitor) {
		final BufferedImage img = new BufferedImage(
				xyGraph.getBounds().width, xyGraph.getBounds().height, 
				BufferedImage.TYPE_INT_RGB);
		Display.getDefault().syncExec(new Runnable() {
			@Override public void run() {
				Graphics2D g = img.createGraphics();
				BufferedImage awtImage = AWTBridge.convertToAWT(xyGraph
						.getImageData());
				g.drawImage(awtImage, 0, 0, awtImage.getWidth(),
						awtImage.getHeight(), null);
				g.dispose();
			}
		});
		return PDFCreator.createPlotPage(img, fileName, scanDescription,
				statList, monitor);
	}
}