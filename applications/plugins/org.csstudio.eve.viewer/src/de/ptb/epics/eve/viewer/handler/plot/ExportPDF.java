package de.ptb.epics.eve.viewer.handler.plot;

import java.io.File;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.handlers.HandlerUtil;

import de.ptb.epics.eve.viewer.jobs.plot.ExportJob;
import de.ptb.epics.eve.viewer.views.plotview.ui.PlotView;

/**
 * @author Marcus Michalsky
 * @since 1.16
 */
public class ExportPDF implements IHandler {

	private static final Logger LOGGER = Logger.getLogger(ExportPDF.class
			.getName());
	
	private static final String[] FILTER_NAMES = {"PDF Files (*.pdf)"};
	private static final String[] FILTER_EXTS = {"*.pdf"};
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		if (HandlerUtil.getActivePart(event) instanceof PlotView) {
			PlotView view = (PlotView) HandlerUtil.getActivePart(event);
			
			FileDialog dlg = new FileDialog(HandlerUtil.getActivePart(event)
					.getSite().getShell(), SWT.SAVE);
			dlg.setFilterNames(FILTER_NAMES);
			dlg.setFilterExtensions(FILTER_EXTS);
			String fn = dlg.open();
			if (fn == null) {
				return null;
			}
			
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Save Plot as PDF to " + fn);
				LOGGER.debug("Loaded SCML: " + view.getLoadedScmlFile());
			}
			
			ExportJob exportJob = new ExportJob("Export Plot as PDF", new File(fn),
					view.getLoadedScmlFile(), view.getPlotFigure().getGraph().getImage(),
					view.getPlotStatistics());
			exportJob.setUser(true);
			exportJob.schedule();
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEnabled() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isHandled() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addHandlerListener(IHandlerListener handlerListener) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeHandlerListener(IHandlerListener handlerListener) {
	}
}