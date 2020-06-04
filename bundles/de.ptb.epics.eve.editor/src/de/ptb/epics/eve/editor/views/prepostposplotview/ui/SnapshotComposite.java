package de.ptb.epics.eve.editor.views.prepostposplotview.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * @author Marcus Michalsky
 * @since 1.34
 */
public class SnapshotComposite extends Composite {
	
	public SnapshotComposite(Composite parent, int style) {
		super(parent, style);
		
		this.setLayout(new GridLayout());
		Label label = new Label(this, SWT.WRAP);
		label.setText(
			"Pre-/Postscans are only available in Scan Modules of type classic.");
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		label.setLayoutData(gridData);
	}
}
