package de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes.file.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.fieldassist.ControlDecorationSupport;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
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
import de.ptb.epics.eve.data.scandescription.axismode.FileMode;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes.file.FileStats;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes.file.FilenameModelToTargetConverter;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes.file.FilenameTargetToModelConverter;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes.file.FilenameValidator;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.ui.DialogCellEditorDialog;
import de.ptb.epics.eve.util.io.FileUtil;
import de.ptb.epics.eve.util.io.StringUtil;

/**
 * @author Marcus Michalsky
 * @since 1.34
 */
public class PositionFileDialog extends DialogCellEditorDialog implements PropertyChangeListener {
	private static final Logger LOGGER = Logger.getLogger(
			PositionFileDialog.class.getName());
	
	private TableViewer viewer;
	private Text filenameInput;
	
	private Axis axis;
	private FileMode fileMode;
	
	public PositionFileDialog(Shell shell, Control control, Axis axis) {
		super(shell, control);
		this.axis = axis;
		this.fileMode = (FileMode)this.axis.getMode();
		this.fileMode.addPropertyChangeListener(FileMode.FILE_PROP, this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean close() {
		this.fileMode.removePropertyChangeListener(FileMode.FILE_PROP, this);
		return super.close();
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

		filenameInput = new Text(composite, SWT.BORDER);
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.horizontalIndent = 7;
		filenameInput.setLayoutData(gridData);
		if (axis.getFile() != null) {
			filenameInput.setText(axis.getFile().getAbsolutePath());
		} else {
			filenameInput.setText("");
		}
		
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
				LOGGER.debug("File '" + name + "' selected in dialog.");
				axis.setFile(new File(name));
			}
		});

		this.createViewer(composite);
		this.createColumns();
		this.viewer.setContentProvider(new ArrayContentProvider());
		this.viewer.setLabelProvider(new ITableLabelProvider() {
			@Override
			public void removeListener(ILabelProviderListener listener) {
				// not used
			}

			@Override
			public boolean isLabelProperty(Object element, String property) {
				return false;
			}

			@Override
			public void dispose() {
				// not used
			}

			@Override
			public void addListener(ILabelProviderListener listener) {
				// not used
			}

			@Override
			public String getColumnText(Object element, int columnIndex) {
				FileStats fileStats = (FileStats) element;
				Formatter formatter = new Formatter(new Locale(
						Locale.ENGLISH.getCountry()));
				final String FORMATTER_STRING = "%12.7g";
				String result = null;
				switch (columnIndex) {
				case 0:
					result = Integer.toString(fileStats.getValueCount());
					break;
				case 1:
					result = formatter.format(FORMATTER_STRING, 
							fileStats.getFirstValue()).toString();
					break;
				case 2:
					result = formatter.format(FORMATTER_STRING, 
							fileStats.getLastValue()).toString();
					break;
				case 3:
					result = formatter.format(FORMATTER_STRING, 
							fileStats.getMinimum()).toString();
					break;
				case 4:
					result = formatter.format(FORMATTER_STRING, 
							fileStats.getMaximum()).toString();
					break;
				default:
					result = null;
					break;
				}
				formatter.close();
				return result;
			}

			@Override
			public Image getColumnImage(Object element, int columnIndex) {
				return null;
			}
		});
		this.createBinding();
		this.refresh();
		return composite;
	}

	private void createViewer(final Composite parent) {
		this.viewer = new TableViewer(parent, SWT.BORDER);
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = GridData.FILL;
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
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(600, 220);
	}
	
	private void createBinding() {
		DataBindingContext context = new DataBindingContext();
		IObservableValue filenameInputTargetObservable = WidgetProperties.text(
				SWT.Modify).observeDelayed(500, this.filenameInput);
		IObservableValue filenameInputModelObservable = BeanProperties.value(
				FileMode.FILE_PROP).observe(this.fileMode);
		UpdateValueStrategy filenameTargetToModel = new UpdateValueStrategy(
				UpdateValueStrategy.POLICY_UPDATE).
				setConverter(new FilenameTargetToModelConverter()).
				setAfterGetValidator(new FilenameValidator());
		UpdateValueStrategy filenameModelToTarget = new UpdateValueStrategy(
				UpdateValueStrategy.POLICY_UPDATE).
				setConverter(new FilenameModelToTargetConverter()).
				setAfterConvertValidator(new FilenameValidator());
		Binding filenameBinding = context.bindValue(
				filenameInputTargetObservable, filenameInputModelObservable, 
				filenameTargetToModel, filenameModelToTarget);
		ControlDecorationSupport.create(filenameBinding, SWT.LEFT);
	}
	
	/*
	 * refreshes the content of the table
	 * - if the file exists, and the values are valid, statistics are generated
	 *   and shown in the table
	 * - otherwise table is disabled
	 */
	private void refresh() {
		if (this.axis.getFile() == null) {
			this.filenameInput.setText("");
			this.viewer.getTable().setEnabled(false);
			return;
		}
		String path = this.axis.getFile().getAbsolutePath();

		try {
			List<Double> values = StringUtil.getDoubleList(FileUtil.readLines(
					new File(path)));
			if (values == null) {
				this.viewer.setInput(null);
				this.viewer.getTable().setEnabled(false);
				return;
			}
			FileStats fileStats = new FileStats(values);
			List<FileStats> statsList = new ArrayList<>();
			statsList.add(fileStats);
			this.viewer.setInput(statsList);
			this.viewer.getTable().setEnabled(true);
		} catch (IOException e) {
			this.viewer.setInput(null);
			this.viewer.getTable().setEnabled(false);
		}
		this.viewer.refresh();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void propertyChange(PropertyChangeEvent e) {
		if (e.getPropertyName().equals(FileMode.FILE_PROP)) {
			LOGGER.debug("received FILE_PROP property change: " + 
					((File)e.getNewValue()).getAbsolutePath());
			this.refresh();
		}
	}
}
