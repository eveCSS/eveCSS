package de.ptb.epics.eve.editor.views.batchupdateview.ui;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import de.ptb.epics.eve.editor.Activator;

/**
 * @author Marcus Michalsky
 * @since 1.30
 */
public class FilesExistWarningDialog extends Dialog {
	private List<String> existingFileNames;
	
	protected FilesExistWarningDialog(Shell parentShell,
			List<String> existingFileNames) {
		super(parentShell);
		this.existingFileNames = existingFileNames;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite top = (Composite) super.createDialogArea(parent);
		Label description = new Label(top, SWT.NONE);
		description.setText("The following files already exist at the target " + 
				"location. They will be overwritten! Continue?");
		GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		description.setLayoutData(gridData);
		TableViewer viewer = new TableViewer(top, SWT.BORDER);
		TableViewerColumn nameColumn = new TableViewerColumn(viewer, SWT.NONE);
		nameColumn.getColumn().setText("Filename");
		nameColumn.getColumn().setWidth(400);
		viewer.getTable().setHeaderVisible(true);
		viewer.getTable().setLinesVisible(true);
		viewer.setLabelProvider(new LabelProvider());
		viewer.setContentProvider(new ArrayContentProvider());
		viewer.setInput(existingFileNames);
		viewer.setSelection(null);
		viewer.setComparator(new ViewerComparator());
		gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		viewer.getTable().setLayoutData(gridData);
		return top;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(800,600);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setImage(
				Activator.getDefault().getImageRegistry().get("WARNING"));
		newShell.setText("Files will be overwritten!");
	}
}