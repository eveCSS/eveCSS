package de.ptb.epics.eve.editor.views.prepostposplotview.classiccomposite.plot.ui;

import java.util.List;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Image;

import de.ptb.epics.eve.data.scandescription.PlotWindow;
import de.ptb.epics.eve.data.scandescription.ScanModule;

/**
 * @author Marcus Michalsky
 * @since 1.35
 */
public class IdColumnLabelProvider extends ColumnLabelProvider {
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getText(Object element) {
		return Integer.toString(((PlotWindow)element).getId());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Image getImage(Object element) {
		final PlotWindow pw = (PlotWindow)element;
		List<ScanModule> scanModulesUsingSameId = pw.getScanModule().getChain().
				getUsedIds().get(pw.getId());
		scanModulesUsingSameId.remove(pw.getScanModule());
		if (!scanModulesUsingSameId.isEmpty()) {
			return de.ptb.epics.eve.editor.Activator.getDefault().
					getImageRegistry().get("INFO");
		}
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getToolTipText(Object element) {
		final PlotWindow pw = (PlotWindow)element;
		List<ScanModule> scanModulesUsingSameId = pw.getScanModule().getChain().
				getUsedIds().get(pw.getId());
		scanModulesUsingSameId.remove(pw.getScanModule());
		StringBuilder stringBuilder = new StringBuilder();
		for (ScanModule sm : scanModulesUsingSameId) {
			stringBuilder.append(sm.getName() + ", ");
		}
		String smList = stringBuilder.toString();
		smList = smList.substring(0, smList.length()-2);
		return "Scan Modules using the same plot window id: " + smList;
	}
}
