package de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.channels.standard.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import de.ptb.epics.eve.data.scandescription.Channel;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.ui.DialogCellEditorDialog;

/**
 * @author Marcus Michalsky
 * @since 1.34
 */
public class ChannelStandardDialog extends DialogCellEditorDialog {
	/*
	 * indentation used for text input layout to leave space for decorators
	 */
	private static final int TEXT_INDENTATION = 7;
	
	private Channel channel;
	
	public ChannelStandardDialog(Shell shell, Control control, Channel channel) {
		super(shell, control);
		this.channel = channel;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.marginWidth = 5;
		gridLayout.marginHeight = 5;
		gridLayout.numColumns = 2;
		composite.setLayout(gridLayout);
		GridData gridData = new GridData();
		gridData.minimumWidth = 250;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		composite.setLayoutData(gridData);

		Label averageLabel = new Label(composite, SWT.NONE);
		averageLabel.setText("Average:");
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		averageLabel.setLayoutData(gridData);

		Text averageText = new Text(composite, SWT.BORDER);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.horizontalIndent = TEXT_INDENTATION;
		gridData.grabExcessHorizontalSpace = true;
		averageText.setLayoutData(gridData);
		// TODO add Focus, Mouse and Focus Listener ?

		Label maxDeviationLabel = new Label(composite, SWT.NONE);
		maxDeviationLabel.setText("Max. Deviation (%):");
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		maxDeviationLabel.setLayoutData(gridData);

		Text maxDeviationText = new Text(composite, SWT.BORDER);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.horizontalIndent = TEXT_INDENTATION;
		gridData.grabExcessHorizontalSpace = true;
		maxDeviationText.setLayoutData(gridData);
		// TODO Focus, Mouse, Focus Listener

		Label minimumLabel = new Label(composite, SWT.NONE);
		minimumLabel.setText("Minimum:");
		minimumLabel.setToolTipText("for values < minimum no deviation check");
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		minimumLabel.setLayoutData(gridData);

		Text minimumText = new Text(composite, SWT.BORDER);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.horizontalIndent = TEXT_INDENTATION;
		gridData.grabExcessHorizontalSpace = true;
		minimumText.setLayoutData(gridData);
		// TODO Focus, Mouse, Focus

		Label maxAttemptsLabel = new Label(composite, SWT.NONE);
		maxAttemptsLabel.setText("Max. Attempts:");
		maxAttemptsLabel.setToolTipText("Maximum attempts to calculate deviation");
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		maxAttemptsLabel.setLayoutData(gridData);

		Text maxAttemptsText = new Text(composite, SWT.BORDER);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.horizontalIndent = TEXT_INDENTATION;
		gridData.grabExcessHorizontalSpace = true;
		maxAttemptsText.setLayoutData(gridData);
		// TODO Focus, Mouse, Focus

		Label redoEventsLabel = new Label(composite, SWT.NONE);
		redoEventsLabel.setText("Redo Events:");
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		gridData.horizontalAlignment = SWT.LEFT;
		redoEventsLabel.setLayoutData(gridData);

		// TODO Event composite benötigt Parent View ?!?
		
		
		// TODO Auto-generated method stub
		return composite;
	}
}
