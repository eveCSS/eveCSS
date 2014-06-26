package de.ptb.epics.eve.editor.dialogs.lostdevices;

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

import de.ptb.epics.eve.data.scandescription.processors.ScanDescriptionLoader;

/**
 * 
 * @author Marcus Michalsky
 * @since 1.5
 */
public class LostDevicesDialog extends TitleAreaDialog {
	private ScanDescriptionLoader scanDescriptionLoader;
	private TableViewer viewer;
	
	/**
	 * Constructor.
	 * 
	 * @param shell 
	 * @param scanDescriptionLoader
	 */
	public LostDevicesDialog(final Shell shell,
			final ScanDescriptionLoader scanDescriptionLoader) {
		super(shell);
		this.scanDescriptionLoader = scanDescriptionLoader;
	}

	/**
	 * {@inheritDoc}
	 */
	protected Control createDialogArea(final Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		container.setLayout(gridLayout);
		
		this.createViewer(container);
		this.viewer.setInput(scanDescriptionLoader.getLostDevices());
		
		this.setTitle("Error while loading file: " + 
				scanDescriptionLoader.getFileToLoad().getName());
		this.setMessage("Below is a list of problems encountered while loading the above mentioned scml file", 
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
		return new Point(this.viewer.getTable().computeSize(
				SWT.DEFAULT, SWT.DEFAULT).x + 20, 350); 
		// getButtonBar().getBounds().y + 20);
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @since 1.19
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
		gridData.minimumHeight = 150;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = SWT.TOP;
		this.viewer.getTable().setLayoutData(gridData);
		
		TableViewerColumn typeColumn = new TableViewerColumn(viewer, SWT.LEFT);
		typeColumn.getColumn().setText("Type");
		typeColumn.getColumn().setWidth(255);
		typeColumn.setLabelProvider(new TypeColumnLabelProvider());
		
		TableViewerColumn actionColumn = new TableViewerColumn(viewer, SWT.LEFT);
		actionColumn.getColumn().setText("");
		actionColumn.getColumn().setWidth(300);
		actionColumn.setLabelProvider(new ActionColumnLabelProvider());
	}
}