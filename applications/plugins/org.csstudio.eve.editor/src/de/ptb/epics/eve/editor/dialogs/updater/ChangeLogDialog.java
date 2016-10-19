package de.ptb.epics.eve.editor.dialogs.updater;

import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import de.ptb.epics.eve.data.scandescription.updater.Patch;

/**
 * @author Marcus Michalsky
 * @since 1.18
 */
public class ChangeLogDialog extends TitleAreaDialog {
	private List<Patch> patches;
	private TableViewer viewer;
	
	/**
	 * Constructor.
	 * 
	 * @param parentShell the parent shell
	 * @param patches the list of applied patches
	 */
	public ChangeLogDialog(final Shell parentShell, final List<Patch> patches) {
		super(parentShell);
		this.patches = patches;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		container.setLayoutData(gridData);
		
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		container.setLayout(gridLayout);
		
		this.createViewer(container);
		this.viewer.setInput(patches);
		
		this.setTitle("SCML file updated");
		this.setMessage("Below is a list of changes made in order to update the loaded file to the current schema version. By saving the file the old file will be overwritten!", 
				IMessageProvider.WARNING);
		
		return area;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		Button okButton = createButton(parent, IDialogConstants.OK_ID,
				IDialogConstants.OK_LABEL, true);
		okButton.addSelectionListener(new SelectionAdapter() {
			@Override public void widgetSelected(SelectionEvent e) {
				setReturnCode(Window.OK);
				close();
			}
		});
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(720, 480); 
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean isResizable() {
		return true;
	}
	
	/*
	 * 
	 */
	private void createViewer(Composite parent) {
		this.viewer = new TableViewer(parent);
		this.viewer.getTable().setHeaderVisible(true);
		this.viewer.setContentProvider(new ContentProvider());
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		this.viewer.getTable().setLayoutData(gridData);
		
		TableViewerColumn fromColumn = new TableViewerColumn(viewer, SWT.LEFT);
		fromColumn.getColumn().setText("From");
		fromColumn.getColumn().setWidth(50);
		fromColumn.setLabelProvider(new FromColumnLabelProvider());
		
		TableViewerColumn toColumn = new TableViewerColumn(viewer, SWT.LEFT);
		toColumn.getColumn().setText("To");
		toColumn.getColumn().setWidth(35);
		toColumn.setLabelProvider(new ToColumnLabelProvider());
		
		TableViewerColumn changeColumn = new TableViewerColumn(viewer, SWT.LEFT);
		changeColumn.getColumn().setText("Change");
		changeColumn.getColumn().setWidth(300);
		changeColumn.setLabelProvider(new ChangeColumnLabelProvider());
	}
}