package de.ptb.epics.eve.editor.views.batchupdateview.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

import de.ptb.epics.eve.editor.views.batchupdateview.BatchUpdater;
import de.ptb.epics.eve.editor.views.batchupdateview.FileEntry;
import de.ptb.epics.eve.editor.views.batchupdateview.FileStatus;
import de.ptb.epics.eve.editor.views.batchupdateview.UpdateStatus;
import de.ptb.epics.eve.resources.Activator;
import de.ptb.epics.eve.util.ui.jface.ViewerComparator;
import de.ptb.epics.eve.util.ui.swt.FontHelper;

/**
 * @author Marcus Michalsky
 * @since 1.30
 */
public class BatchUpdateView extends ViewPart implements PropertyChangeListener {
	private static final Logger LOGGER = Logger
			.getLogger(BatchUpdateView.class.getName());
	
	private static final int FILENAME_COLUMN_WIDTH = 250;
	private static final int VERSION_COLUMN_WIDTH = 80;
	private static final int STATUS_COLUMN_WIDTH = 100;
	private static final int ADDITIONAL_COLUMN_WIDTH = 20;
	
	private static final String NAME_SORT_STATE_MEMENTO = "nameSortState";
	private static final String STATUS_SORT_STATE_MEMENTO = "statusSortState";
	private static final String VERSION_SORT_STATE_MEMENTO = "versionSortState";
	
	private TableViewer fileTable;
	private Label statusLabel;
	
	private Text srcDirText;
	private Text destDirText;
	
	private Image ascending;
	private Image descending;
	private NameViewerComparator nameViewerComparator;
	private StatusViewerComparator statusViewerComparator;
	private VersionViewerComparator versionViewerComparator;
	private int nameSortState;
	private int statusSortState;
	private int versionSortState;
	
	private BatchUpdater batchUpdater;
	private Button updateButton;
	
	// saves/restores user defined settings
	private IMemento memento;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(IViewSite site, IMemento memento)
			throws PartInitException {
		this.memento = memento;
		super.init(site, memento);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new FillLayout());
		final ScrolledComposite sc = new ScrolledComposite(parent, 
				SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		sc.setExpandHorizontal(true);
		sc.setExpandVertical(true);
		final Composite top = new Composite(sc, SWT.NONE);
		sc.setContent(top);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		top.setLayout(gridLayout);
		
		Label srcDirLabel = new Label(top, SWT.NONE);
		srcDirLabel.setText("Source:");
		
		srcDirText = new Text(top, SWT.BORDER);
		srcDirText.setEditable(false);
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = SWT.FILL;
		srcDirText.setLayoutData(gridData);
		
		Button srcSearchButton = new Button(top, SWT.PUSH);
		srcSearchButton.setText("Search...");
		srcSearchButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dirDialog = new DirectoryDialog(sc.getShell());
				dirDialog.setMessage(
		"Select the directory containing the files that should be converted.");
				dirDialog.setText("Select Source Folder");
				if (batchUpdater.getSource() == null) {
					dirDialog.setFilterPath(
							Activator.getDefault().getFilterPath());
				} else {
					dirDialog.setFilterPath(
							batchUpdater.getSource().getAbsolutePath());
				}
				String path = dirDialog.open();
				if (path == null) {
					return;
				}
				if (new File(path).equals(batchUpdater.getTarget())) {
					MessageDialog.openError(getSite().getShell(), "", 
							"Source and Target must not be equal!");
					return;
				}
				batchUpdater.setSource(new File(path));
			}
		});
		
		Label destDirLabel = new Label(top, SWT.NONE);
		destDirLabel.setText("Target:");
		
		destDirText = new Text(top, SWT.BORDER);
		destDirText.setEditable(false);
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = SWT.FILL;
		destDirText.setLayoutData(gridData);
		
		Button destSearchButton = new Button(top, SWT.PUSH);
		destSearchButton.setText("Search...");
		destSearchButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dirDialog = new DirectoryDialog(sc.getShell());
				dirDialog.setMessage(
				"Select the directory the updated files should be saved to.");
				dirDialog.setText("Select Target Folder");
				if (batchUpdater.getTarget() == null) {
					dirDialog.setFilterPath(
							Activator.getDefault().getFilterPath());
				} else {
					dirDialog.setFilterPath(
							batchUpdater.getTarget().getAbsolutePath());
				}
				String path = dirDialog.open();
				if (path == null) {
					return;
				}
				if (new File(path).equals(batchUpdater.getSource())) {
					MessageDialog.openError(getSite().getShell(), "", 
							"Source and Target must not be equal!");
					return;
				}
				batchUpdater.setTarget(new File(path));
			}
		});
		
		this.statusLabel = new Label(top, SWT.NONE);
		this.statusLabel.setText("");
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = SWT.BEGINNING;
		this.statusLabel.setLayoutData(gridData);
		
		updateButton = new Button(top, SWT.PUSH);
		updateButton.setText("Update");
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		updateButton.setLayoutData(gridData);
		updateButton.setEnabled(false);
		updateButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				List<String> existingFiles = batchUpdater
						.checkForExistingFilesInTarget();
				if (!existingFiles.isEmpty()) {
					FilesExistWarningDialog warningDialog = 
						new FilesExistWarningDialog(
							getSite().getShell(), existingFiles);
					warningDialog.setBlockOnOpen(true);
					if (warningDialog.open() == IDialogConstants.CANCEL_ID) {
						LOGGER.info("update canceled (existing file warning)");
						return;
					}
				}
				batchUpdater.update();
			}
		});
		
		ascending = de.ptb.epics.eve.util.ui.Activator.getDefault()
				.getImageRegistry().get("SORT_ASCENDING");
		descending = de.ptb.epics.eve.util.ui.Activator.getDefault()
				.getImageRegistry().get("SORT_DESCENDING");
		nameSortState = 0;
		statusSortState = 0;
		versionSortState = 0;
		nameViewerComparator = new NameViewerComparator();
		statusViewerComparator = new StatusViewerComparator();
		versionViewerComparator = new VersionViewerComparator();
		
		this.batchUpdater = new BatchUpdater();
		this.batchUpdater.addPropertyChangeListener(this);
		this.createTable(top);
		this.createColumns(fileTable);
		this.bindValues();
		
		this.restoreState();
	}
	
	private void createTable(final Composite parent) {
		this.fileTable = new TableViewer(parent);
		this.fileTable.getTable().setHeaderVisible(true);
		this.fileTable.getTable().setLinesVisible(true);
		GridData gridData = new GridData();
		gridData.horizontalSpan = 3;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.minimumHeight = 100;
		gridData.horizontalAlignment = SWT.FILL;
		gridData.verticalAlignment = SWT.FILL;
		this.fileTable.getTable().setLayoutData(gridData);
		this.fileTable.setContentProvider(new FileListContentProvider());
	}
	
	private void createColumns(TableViewer viewer) {
		TableViewerColumn filenameCol = new TableViewerColumn(viewer, SWT.NONE);
		filenameCol.getColumn().setWidth(BatchUpdateView.FILENAME_COLUMN_WIDTH);
		filenameCol.getColumn().setText("Filename");
		filenameCol.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((FileEntry)element).getName();
			}
		});
		filenameCol.getColumn().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				switch (nameSortState) {
				case 0:
					nameViewerComparator
							.setDirection(ViewerComparator.ASCENDING);
					fileTable.setComparator(nameViewerComparator);
					fileTable.getTable().getColumn(0).setImage(ascending);
					break;
				case 1:
					nameViewerComparator
						.setDirection(ViewerComparator.DESCENDING);
					fileTable.setComparator(nameViewerComparator);
					fileTable.refresh();
					fileTable.getTable().getColumn(0).setImage(descending);
					break;
				case 2:
					fileTable.setComparator(null);
					fileTable.getTable().getColumn(0).setImage(null);
					break;
				}
				fileTable.getTable().getColumn(1).setImage(null);
				fileTable.getTable().getColumn(2).setImage(null);
				statusSortState = 0;
				versionSortState = 0;
				nameSortState = ++nameSortState % 3;
			}
		});
		
		TableViewerColumn versionColumn = new TableViewerColumn(viewer, SWT.NONE);
		versionColumn.getColumn().setWidth(BatchUpdateView.VERSION_COLUMN_WIDTH);
		versionColumn.getColumn().setText("Version");
		versionColumn.getColumn().setAlignment(SWT.CENTER);
		versionColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				FileEntry entry = (FileEntry)element;
				if (entry.getVersion() == null) {
					return Character.toString('\u2014');
				}
				return entry.getVersion().toString();
			}
		});
		versionColumn.getColumn().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				switch (versionSortState) {
				case 0:
					versionViewerComparator
							.setDirection(ViewerComparator.ASCENDING);
					fileTable.setComparator(versionViewerComparator);
					fileTable.getTable().getColumn(1).setImage(ascending);
					break;
				case 1:
					versionViewerComparator
							.setDirection(ViewerComparator.DESCENDING);
					fileTable.setComparator(versionViewerComparator);
					fileTable.refresh();
					fileTable.getTable().getColumn(1).setImage(descending);
					break;
				case 2:
					fileTable.setComparator(null);
					fileTable.getTable().getColumn(1).setImage(null);
					break;
				}
				fileTable.getTable().getColumn(0).setImage(null);
				fileTable.getTable().getColumn(2).setImage(null);
				nameSortState = 0;
				statusSortState = 0;
				versionSortState = ++versionSortState % 3;
			}
		});
		
		TableViewerColumn statusColumn = new TableViewerColumn(viewer, SWT.None);
		statusColumn.getColumn().setWidth(BatchUpdateView.STATUS_COLUMN_WIDTH);
		statusColumn.getColumn().setText("Status");
		statusColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				FileEntry entry = (FileEntry)element;
				if (entry.getStatus().equals(FileStatus.ERROR)) {
					return "Error: " + entry.getErrorMessage();
				} else if (entry.getStatus().equals(
						FileStatus.ERROR_DURING_UPDATE)) {
					return "Update Error: " + entry.getErrorMessage();
				}
				return entry.getStatus().toString();
			}
			
			@Override
			public Image getImage(Object element) {
				switch (((FileEntry)element).getStatus()) {
				case ERROR:
					return de.ptb.epics.eve.editor.Activator.getDefault()
							.getImageRegistry().get("FATAL");
				case ERROR_DURING_UPDATE:
					return de.ptb.epics.eve.editor.Activator.getDefault()
							.getImageRegistry().get("TESTERROR");
				case OUTDATED:
					return de.ptb.epics.eve.editor.Activator.getDefault()
							.getImageRegistry().get("TESTFAIL");
				case PENDING:
					return de.ptb.epics.eve.editor.Activator.getDefault()
							.getImageRegistry().get("WAITING");
				case UPDATED:
					return de.ptb.epics.eve.editor.Activator.getDefault()
							.getImageRegistry().get("TESTOK");
				case UPDATING:
					return de.ptb.epics.eve.editor.Activator.getDefault()
							.getImageRegistry().get("TESTRUN");
				case UPTODATE:
					return de.ptb.epics.eve.editor.Activator.getDefault()
							.getImageRegistry().get("TEST");
				default:
					return super.getImage(element);
				}
			}
		});
		statusColumn.getColumn().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				switch (statusSortState) {
				case 0:
					statusViewerComparator
							.setDirection(ViewerComparator.ASCENDING);
					fileTable.setComparator(statusViewerComparator);
					fileTable.getTable().getColumn(2).setImage(ascending);
					break;
				case 1:
					statusViewerComparator
						.setDirection(ViewerComparator.DESCENDING);
					fileTable.setComparator(statusViewerComparator);
					fileTable.refresh();
					fileTable.getTable().getColumn(2).setImage(descending);
					break;
				case 2:
					fileTable.setComparator(null);
					fileTable.getTable().getColumn(2).setImage(null);
					break;
				default:
					break;
				}
				fileTable.getTable().getColumn(0).setImage(null);
				fileTable.getTable().getColumn(1).setImage(null);
				nameSortState = 0;
				versionSortState = 0;
				statusSortState = ++statusSortState % 3;
			}
		});
	}

	private void bindValues() {
		DataBindingContext ctx = new DataBindingContext();
		
		IObservableValue srcDirTextTarget = WidgetProperties.text(SWT.Modify)
				.observe(srcDirText);
		IObservableValue srcDirTextModel = BeanProperties
				.value(BatchUpdater.class, BatchUpdater.SOURCE_PROP)
				.observe(batchUpdater);
		ctx.bindValue(srcDirTextTarget, srcDirTextModel);
		
		IObservableValue destDirTextTarget = WidgetProperties.text(SWT.Modify)
				.observe(destDirText);
		IObservableValue destDirTextModel = BeanProperties
				.value(BatchUpdater.class, BatchUpdater.TARGET_PROP)
				.observe(batchUpdater);
		ctx.bindValue(destDirTextTarget, destDirTextModel);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void saveState(IMemento memento) {
		super.saveState(memento);
		memento.putInteger(NAME_SORT_STATE_MEMENTO, nameSortState);
		memento.putInteger(STATUS_SORT_STATE_MEMENTO, statusSortState);
		memento.putInteger(VERSION_SORT_STATE_MEMENTO, versionSortState);
	}
	
	/*
	 * gets called at the end of createPartControl() to restore the state 
	 * saved in the memento.
	 */
	private void restoreState() {
		if(memento == null) {
			// nothing saved
			return;
		}
		Integer nameSortStateMemento = memento
				.getInteger(NAME_SORT_STATE_MEMENTO);
		this.nameSortState = (nameSortStateMemento == null)
				? 0
				: nameSortStateMemento;
		switch (this.nameSortState) {
		case 0:
			break;
		case 1:
			nameViewerComparator.setDirection(ViewerComparator.ASCENDING);
			fileTable.setComparator(nameViewerComparator);
			fileTable.getTable().getColumn(0).setImage(ascending);
			break;
		case 2:
			nameViewerComparator.setDirection(ViewerComparator.DESCENDING);
			fileTable.setComparator(nameViewerComparator);
			fileTable.getTable().getColumn(0).setImage(descending);
			break;
		default:
			break;
		}
		
		Integer versionSortStateMemento = memento
				.getInteger(VERSION_SORT_STATE_MEMENTO);
		this.versionSortState = (versionSortStateMemento == null)
				? 0
				: versionSortStateMemento;
		switch (this.versionSortState) {
		case 0:
			break;
		case 1:
			versionViewerComparator.setDirection(ViewerComparator.ASCENDING);
			fileTable.setComparator(versionViewerComparator);
			fileTable.getTable().getColumn(1).setImage(ascending);
			break;
		case 2:
			versionViewerComparator.setDirection(ViewerComparator.DESCENDING);
			fileTable.setComparator(versionViewerComparator);
			fileTable.getTable().getColumn(1).setImage(descending);
			break;
		default:
			break;
		}

		Integer statusSortStateMemento = memento
				.getInteger(STATUS_SORT_STATE_MEMENTO);
		this.statusSortState = (statusSortStateMemento == null)
				? 0
				: statusSortStateMemento;
		switch (this.statusSortState) {
		case 0:
			break;
		case 1:
			statusViewerComparator.setDirection(ViewerComparator.ASCENDING);
			fileTable.setComparator(statusViewerComparator);
			fileTable.getTable().getColumn(2).setImage(ascending);
			break;
		case 2:
			statusViewerComparator.setDirection(ViewerComparator.DESCENDING);
			fileTable.setComparator(statusViewerComparator);
			fileTable.getTable().getColumn(2).setImage(descending);
			break;
		default:
			break;
		}
		this.fileTable.refresh();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setFocus() {
		this.fileTable.getTable().setFocus();
	}

	/**
	 * resets the view to its initial state.
	 */
	public void reset() {
		this.batchUpdater.reset();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(BatchUpdater.FILES_PROP)) {
			List<FileEntry> files = this.batchUpdater.getFiles();
			this.fileTable.setInput(files);
			if (files.isEmpty()) {
				this.statusLabel.setText("0 files found.");
			} else if (files.size() == 1) {
				this.statusLabel.setText("1 file found.");
			} else {
				this.statusLabel.setText(files.size() + " files found.");
			}
			this.statusLabel.getParent().layout();
		} else if (evt.getPropertyName().equals(BatchUpdater.UPDATE_STATUS_PROP)) {
			switch ((UpdateStatus)evt.getNewValue()) {
			case IDLE:
				this.statusLabel.getDisplay().syncExec(new Runnable() {
					@Override public void run() {
						statusLabel.setText("");
						updateButton.setEnabled(false);
						statusLabel.getParent().layout();
					}
				});
				this.updateButton.setEnabled(false);
				fileTable.getTable().getColumn(0)
						.setWidth(FILENAME_COLUMN_WIDTH);
				fileTable.getTable().getColumn(1)
						.setWidth(VERSION_COLUMN_WIDTH);
				fileTable.getTable().getColumn(2).setWidth(STATUS_COLUMN_WIDTH);
				fileTable.refresh();
				break;
			case INITIALIZED:
				this.statusLabel.getDisplay().syncExec(new Runnable() {
					@Override public void run() {
				statusLabel.setText(batchUpdater.getOutdatedCount() +
						" out of " + batchUpdater.getFiles().size() +
						" files need to be updated.");
				updateButton.setEnabled(false);
				statusLabel.getParent().layout();
				adjustColumnWidth();
				fileTable.refresh();
					}
				});
				break;
			case INITIALIZING:
				this.statusLabel.getDisplay().syncExec(new Runnable() {
					@Override public void run() {
				statusLabel.setText("reading files...");
				updateButton.setEnabled(false);
				statusLabel.getParent().layout();
					}
				});
				break;
			case READY_FOR_UPDATE:
				this.statusLabel.getDisplay().syncExec(new Runnable() {
					@Override public void run() {
				statusLabel.setText(batchUpdater.getOutdatedCount() +
						" out of " + batchUpdater.getFiles().size() +
						" files need to be updated.");
				updateButton.setEnabled(true);
				statusLabel.getParent().layout();
				adjustColumnWidth();
				fileTable.refresh();
					}
				});
				break;
			case UPDATE_DONE:
				this.statusLabel.getDisplay().syncExec(new Runnable() {
					@Override public void run() {
						statusLabel.setText(
							"All files (except erroneous) are up to date.");
						updateButton.setEnabled(false);
						statusLabel.getParent().layout();
						adjustColumnWidth();
						fileTable.refresh();
					}
				});
				break;
			case UPDATING:
				this.statusLabel.getDisplay().syncExec(new Runnable() {
					@Override public void run() {
				statusLabel.setText("updating files...");
				updateButton.setEnabled(false);
				statusLabel.getParent().layout();
					}
				});
				break;
			default:
				break;
			}
		}
	}
	
	private void adjustColumnWidth() {
		String maxCharWidthString = "";
		int maxCharWidth = 0;
		
		String maxStatusWidthString = "";
		int maxStatusWidth = 0;
		
		for (FileEntry file : this.batchUpdater.getFiles()) {
			if (file.getName().length() > maxCharWidth) {
				maxCharWidth = file.getName().length();
				maxCharWidthString = file.getName();
			}
			if (file.getStatus().equals(FileStatus.ERROR) ||
					file.getStatus().equals(FileStatus.ERROR_DURING_UPDATE)) {
				if (file.getErrorMessage().length() > maxStatusWidth) {
					maxStatusWidthString = file.getErrorMessage();
					maxStatusWidth = file.getErrorMessage().length();
				}
			} else {
				if (file.getStatus().toString().length() > maxStatusWidth) {
					maxStatusWidthString = file.getStatus().toString();
					maxStatusWidth = file.getStatus().toString().length();
				}
			}
		}
		int nameColumnWidth = FontHelper.getCharWidth(
				new GC(this.fileTable.getTable()), maxCharWidthString);
		this.fileTable.getTable().getColumn(0).setWidth(nameColumnWidth +
				BatchUpdateView.ADDITIONAL_COLUMN_WIDTH);
		int statusColumnWidth = FontHelper.getCharWidth(
				new GC(this.fileTable.getTable()), maxStatusWidthString);
		this.fileTable.getTable().getColumn(2).setWidth(statusColumnWidth + 
				BatchUpdateView.ADDITIONAL_COLUMN_WIDTH);
	}
}