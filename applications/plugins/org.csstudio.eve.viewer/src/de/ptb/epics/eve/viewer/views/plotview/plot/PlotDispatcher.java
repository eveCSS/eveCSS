package de.ptb.epics.eve.viewer.views.plotview.plot;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import de.ptb.epics.eve.data.scandescription.Chain;
import de.ptb.epics.eve.data.scandescription.PlotWindow;
import de.ptb.epics.eve.data.scandescription.ScanDescription;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.ecp1.client.interfaces.IChainStatusListener;
import de.ptb.epics.eve.ecp1.commands.ChainStatusCommand;
import de.ptb.epics.eve.ecp1.types.ChainStatus;
import de.ptb.epics.eve.util.data.Pair;
import de.ptb.epics.eve.viewer.Activator;
import de.ptb.epics.eve.viewer.XMLDispatcher;
import de.ptb.epics.eve.viewer.views.plotview.ui.PlotView;

/**
 * PlotDispatcher controls the creation and removal of plot windows.
 * 
 * @author Marcus Michalsky
 * @since 1.13
 */
public class PlotDispatcher implements PropertyChangeListener,
		IChainStatusListener {
	private static final Logger LOGGER = Logger.getLogger(PlotDispatcher.class
			.getName());

	private ScanDescription scanDescription = null;
	private List<PlotView> plotViews = new ArrayList<PlotView>();
	private List<Pair<Integer, Integer>> initializedModules;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void propertyChange(PropertyChangeEvent e) {
		if (e.getPropertyName().equals(XMLDispatcher.SCAN_DESCRIPTION_PROP)) {
			if (e.getNewValue() instanceof ScanDescription) {
				this.scanDescription = (ScanDescription)e.getNewValue();
				this.initializedModules = new ArrayList<Pair<Integer, Integer>>();
				LOGGER.debug("new scan description received");
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void chainStatusChanged(ChainStatusCommand chainStatusCommand) {
		if (this.scanDescription == null) {
			LOGGER.debug("chain status changed, but no scan description received.");
			return;
		}
		if (chainStatusCommand.getChainStatus().equals(ChainStatus.EXECUTING_SM)) {
			// scan module executes
			final Chain chain = this.scanDescription
					.getChain(chainStatusCommand.getChainId());
			if (chain == null) {
				return;
			}
			final ScanModule sm = chain.getScanModuleById(chainStatusCommand
					.getScanModulId());
			if (sm == null) {
				return;
			}
			if (this.initializedModules.contains(
					new Pair<Integer, Integer>(chain.getId(), sm.getId()))) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("SM " + sm.getId() + " (Chain "
							+ chain.getId() + ") already initialized");
				}
				return;
			}
			this.initializedModules.add(new Pair<Integer, Integer>(chain
					.getId(), sm.getId()));
			Activator.getDefault().getWorkbench().getDisplay()
					.syncExec(new Runnable() {
						@Override
						public void run() {
							initPlotWindows(java.util.Arrays.asList(sm
									.getPlotWindows()));
						}
					});
		} else if (chainStatusCommand.getChainStatus().equals(ChainStatus.EXITING_SM)) {
			// scan module finished
			final Chain chain = this.scanDescription
					.getChain(chainStatusCommand.getChainId());
			if (chain == null) {
				return;
			}
			final ScanModule sm = chain.getScanModuleById(chainStatusCommand
					.getScanModulId());
			if (sm == null) {
				return;
			}
			this.initializedModules.remove(new Pair<Integer, Integer>(chain
					.getId(), sm.getId()));
			LOGGER.debug("finishing SM " + sm.getId() + " (Chain "
					+ chain.getId() + ")");
			for (final PlotView plotView : this.plotViews) {
				Activator.getDefault().getWorkbench().getDisplay()
				.syncExec(new Runnable() {
					@Override
					public void run() {
						plotView.finish();
					}});
			}
		}
	}
	
	/*
	 * 
	 */
	private void initPlotWindows(List<PlotWindow> plotWindows) {
		this.plotViews.clear();
		for (PlotWindow plotWindow : plotWindows) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("init PlotWindow " + plotWindow.getId());
			}
			try {
				IViewPart view = PlatformUI
						.getWorkbench()
						.getActiveWorkbenchWindow()
						.getActivePage()
						.showView(PlotView.ID,
								Integer.toString(plotWindow.getId()),
								IWorkbenchPage.VIEW_ACTIVATE);
				this.plotViews.add((PlotView)view);
				((PlotView)view).setPlotWindow(plotWindow);
			} catch (PartInitException e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
	}
}