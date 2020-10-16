package de.ptb.epics.eve.editor.views.prepostposplotview.classiccomposite.plot.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import de.ptb.epics.eve.data.scandescription.PlotWindow;
import de.ptb.epics.eve.editor.views.DialogCellEditorDialog;

/**
 * @author Marcus Michalsky
 * @since 1.35
 */
public class YAxisDialog extends DialogCellEditorDialog {
	private PlotWindow plotWindow;
	
	public YAxisDialog(Shell shell, Control control, PlotWindow plotWindow) {
		super(shell, control);
		this.plotWindow = plotWindow;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData();
		gridData.minimumWidth = 250;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		composite.setLayoutData(gridData);
		GridLayout gridLayout = new GridLayout();
		gridLayout.marginWidth = 5;
		gridLayout.marginHeight = 5;
		composite.setLayout(gridLayout);
		
		return composite;
	}
}
