package de.ptb.epics.eve.viewer.views.engineview.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import de.ptb.epics.eve.ecp1.types.ChainStatus;
import de.ptb.epics.eve.ecp1.types.ScanModuleReason;
import de.ptb.epics.eve.ecp1.types.ScanModuleStatus;
import de.ptb.epics.eve.viewer.views.engineview.statustable.ChainInfo;
import de.ptb.epics.eve.viewer.views.engineview.statustable.ScanInfo;
import de.ptb.epics.eve.viewer.views.engineview.statustable.ScanModuleInfo;
import de.ptb.epics.eve.viewer.views.engineview.statustable.StatusTableElement;
import de.ptb.epics.eve.viewer.views.engineview.statustable.StatusTracker;

/**
 * @author Marcus Michalsky
 * @since 1.26
 */
public class ContentProvider implements IStructuredContentProvider, PropertyChangeListener {
	private Viewer viewer;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (oldInput != null) {
			((StatusTracker) oldInput).addPropertyChangeListener(
					StatusTracker.SCAN_INFO_PROPERTY, this);
			((StatusTracker) oldInput).addPropertyChangeListener(
					StatusTracker.CHAIN_INFO_PROPERTY, this);
		}
		this.viewer = viewer;
		if (newInput != null) {
			((StatusTracker) newInput).addPropertyChangeListener(
					StatusTracker.SCAN_INFO_PROPERTY, this);
			((StatusTracker) newInput).addPropertyChangeListener(
					StatusTracker.CHAIN_INFO_PROPERTY, this);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object[] getElements(Object inputElement) {
		List<StatusTableElement> result = new ArrayList<>();
		ScanInfo scanInfo = ((StatusTracker)inputElement).getScanInfo();
		if (scanInfo == null) {
			return result.toArray();
		}
		for (ChainInfo chainInfo : scanInfo.getChainInfos()) {
			StatusTableElement chainElement = new StatusTableElement();
			chainElement.setChainId(chainInfo.getId());
			chainElement.setStatus(ChainStatus.toString(chainInfo.getStatus()));
			chainElement.setRemainingTime(chainInfo.getRemainingTime());
			result.add(chainElement);
			for (ScanModuleInfo scanModuleInfo : chainInfo.getScanModuleInfos()) {
				StatusTableElement scanModuleElement = new StatusTableElement();
				scanModuleElement.setChainId(chainInfo.getId());
				scanModuleElement.setScanModuleId(scanModuleInfo.getId());
				scanModuleElement.setScanModuleName(scanModuleInfo.getName());
				scanModuleElement.setStatus(ScanModuleStatus.toString(
						scanModuleInfo.getStatus()));
				scanModuleElement.setReason(ScanModuleReason.toString(
						scanModuleInfo.getReason()));
				result.add(scanModuleElement);
			}
		}
		return result.toArray();
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
	public void propertyChange(PropertyChangeEvent evt) {
		viewer.getControl().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				viewer.refresh();
			}
		});
	}
}