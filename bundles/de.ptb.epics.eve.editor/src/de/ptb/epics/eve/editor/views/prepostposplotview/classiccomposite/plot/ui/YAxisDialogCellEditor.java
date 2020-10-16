package de.ptb.epics.eve.editor.views.prepostposplotview.classiccomposite.plot.ui;

import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import de.ptb.epics.eve.data.scandescription.PlotWindow;
import de.ptb.epics.eve.editor.views.prepostposplotview.classiccomposite.plot.axesdialog.ui.AxesDialog;

/**
 * @author Marcus Michalsky
 * @since 1.35
 */
public class YAxisDialogCellEditor extends DialogCellEditor {
	private PlotWindow plotWindow;
	
	public YAxisDialogCellEditor(Composite parent, PlotWindow plotWindow) {
		super(parent, SWT.NONE);
		this.plotWindow = plotWindow;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Object openDialogBox(Control cellEditorWindow) {
		new AxesDialog(cellEditorWindow.getShell(), getControl(), 
				this.plotWindow).open();
		return this.plotWindow;
	}
}
