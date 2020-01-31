package de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes.file.ui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes.file.FileStats;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.ui.DialogCellEditorDialog;
import de.ptb.epics.eve.util.io.FileUtil;
import de.ptb.epics.eve.util.io.StringUtil;

/**
 * @author Marcus Michalsky
 * @since 1.34
 */
public class PositionFileDialog extends DialogCellEditorDialog {
	private TableViewer viewer;
	private Text filenameInput;
	private Axis axis;
	
	public PositionFileDialog(Shell shell, Control control, Axis axis) {
		super(shell, control);
		this.axis = axis;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.marginWidth = 5;
		gridLayout.marginHeight = 5;
		gridLayout.numColumns = 3;
		composite.setLayout(gridLayout);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		composite.setLayoutData(gridData);

		Label filenameLabel = new Label(composite, SWT.NONE);
		filenameLabel.setText("Filename:");

		filenameInput = new Text(composite, SWT.READ_ONLY | SWT.BORDER);
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		filenameInput.setLayoutData(gridData);
		filenameInput.setText(axis.getFile().getAbsolutePath());

		Button filenameButton = new Button(composite, SWT.PUSH);
		filenameButton.setText("Search...");
		filenameButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int lastSeperatorIndex;
				final String filePath;

				// Ticket #1481
				String axisPath = axis.getScanModule().getAxisPath(axis);
				if (axisPath != null) {
					filePath = axisPath;
				} else if (axis.getFile() == null || axis.getFile().getAbsolutePath().isEmpty()) {
					filePath = de.ptb.epics.eve.resources.Activator.getDefault().getDefaultsManager()
							.getWorkingDirectory().getAbsolutePath();
				} else {
					lastSeperatorIndex = axis.getFile().getAbsolutePath().lastIndexOf(File.separatorChar);
					filePath = axis.getFile().getAbsolutePath().substring(0, lastSeperatorIndex + 1);
				}
				FileDialog fileWindow = new FileDialog(getShell(), SWT.SAVE);
				fileWindow.setFilterPath(filePath);
				String name = fileWindow.open();
				if (name == null) {
					return;
				}
				setFile(name);
			}
		});

		this.createViewer(composite);
		this.createColumns();
		this.viewer.setContentProvider(new ArrayContentProvider());
		this.viewer.setLabelProvider(new ITableLabelProvider() {

			@Override
			public void removeListener(ILabelProviderListener listener) {
			}

			@Override
			public boolean isLabelProperty(Object element, String property) {
				return false;
			}

			@Override
			public void dispose() {
			}

			@Override
			public void addListener(ILabelProviderListener listener) {
			}

			@Override
			public String getColumnText(Object element, int columnIndex) {
				FileStats fileStats = (FileStats) element;
				switch (columnIndex) {
				case 0:
					return fileStats.getValueCount();
				case 1:
					return fileStats.getFirstValue();
				case 2:
					return fileStats.getLastValue();
				case 3:
					return fileStats.getMinimum();
				case 4:
					return fileStats.getMaximum();
				}
				return null;
			}

			@Override
			public Image getColumnImage(Object element, int columnIndex) {
				return null;
			}
		});
		setFile(axis.getFile().getAbsolutePath());
		return composite;
	}

	private void createViewer(final Composite parent) {
		this.viewer = new TableViewer(parent, SWT.BORDER);
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.horizontalSpan = 3;
		gridData.heightHint = 40;
		this.viewer.getTable().setLayoutData(gridData);
		this.viewer.getTable().setHeaderVisible(true);
	}

	private void createColumns() {
		TableViewerColumn countColumn = new TableViewerColumn(this.viewer, SWT.CENTER);
		countColumn.getColumn().setText("# points");
		countColumn.getColumn().setWidth(80);

		TableViewerColumn firstColumn = new TableViewerColumn(this.viewer, SWT.RIGHT);
		firstColumn.getColumn().setText("1st value");
		firstColumn.getColumn().setWidth(120);

		TableViewerColumn lastColumn = new TableViewerColumn(this.viewer, SWT.RIGHT);
		lastColumn.getColumn().setText("n-th value");
		lastColumn.getColumn().setWidth(120);

		TableViewerColumn minColumn = new TableViewerColumn(this.viewer, SWT.RIGHT);
		minColumn.getColumn().setText("Minimum");
		minColumn.getColumn().setWidth(120);

		TableViewerColumn maxColumn = new TableViewerColumn(this.viewer, SWT.RIGHT);
		maxColumn.getColumn().setText("Maximum");
		maxColumn.getColumn().setWidth(120);

		TableViewerColumn emptyColumn = new TableViewerColumn(this.viewer, SWT.NONE);
		emptyColumn.getColumn().setText("");
		emptyColumn.getColumn().setWidth(10);
	}

	private void setFile(String path) {
		axis.setFile(new File(path));
		filenameInput.setText(path);

		try {
			List<Double> values = StringUtil.getDoubleList(FileUtil.readLines(new File(path)));
			if (values == null) {
				return;
			}
			FileStats fileStats = new FileStats(values);
			List<FileStats> statsList = new ArrayList<>();
			statsList.add(fileStats);
			this.viewer.setInput(statsList);
			this.viewer.getTable().setEnabled(true);
		} catch (IOException e) {
			this.viewer.getTable().setEnabled(false);
		}
		this.viewer.refresh();
	}
}
