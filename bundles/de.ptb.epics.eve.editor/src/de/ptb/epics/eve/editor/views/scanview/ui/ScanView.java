package de.ptb.epics.eve.editor.views.scanview.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;

import org.apache.log4j.Logger;
import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.ChangeEvent;
import org.eclipse.core.databinding.observable.IChangeListener;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.databinding.fieldassist.ControlDecorationSupport;
import org.eclipse.jface.databinding.swt.ISWTObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ExpandEvent;
import org.eclipse.swt.events.ExpandListener;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import de.ptb.epics.eve.data.measuringstation.Option;
import de.ptb.epics.eve.data.measuringstation.PluginParameter;
import de.ptb.epics.eve.data.scandescription.MonitorOption;
import de.ptb.epics.eve.data.scandescription.ScanDescription;
import de.ptb.epics.eve.editor.Activator;
import de.ptb.epics.eve.editor.dialogs.monitoroptions.MonitorOptionsDialog;
import de.ptb.epics.eve.editor.gef.editparts.ChainEditPart;
import de.ptb.epics.eve.editor.gef.editparts.ScanDescriptionEditPart;
import de.ptb.epics.eve.editor.gef.editparts.ScanModuleEditPart;
import de.ptb.epics.eve.editor.gef.editparts.StartEventEditPart;
import de.ptb.epics.eve.editor.views.DelColumnEditingSupport;
import de.ptb.epics.eve.editor.views.EditorViewPerspectiveListener;
import de.ptb.epics.eve.editor.views.IEditorView;
import de.ptb.epics.eve.editor.views.scanview.DeviceColumnComparator;
import de.ptb.epics.eve.editor.views.scanview.OptionColumnComparator;
import de.ptb.epics.eve.editor.views.scanview.RepeatCountConverter;
import de.ptb.epics.eve.editor.views.scanview.RepeatCountValidator;
import de.ptb.epics.eve.util.ui.swt.FontHelper;
import de.ptb.epics.eve.util.ui.swt.TextSelectAllFocusListener;
import de.ptb.epics.eve.util.ui.swt.TextSelectAllMouseListener;

/**
 * @author ?
 * @author Marcus Michalsky
 * @author Hartmut Scherr
 */
public class ScanView extends ViewPart implements IEditorView,
		ISelectionListener, PropertyChangeListener {
	/** the unique identifier of the view */
	public static final String ID = "de.ptb.epics.eve.editor.views.ScanView";

	private static final Logger LOGGER = Logger.getLogger(
			ScanView.class.getName());
	
	private static final int DEL_COLUMN_WIDTH = 22;
	private static final String MACRO_TOOLTIP = "The following macros can be used:\n"
			+ "${WEEK} : calendar week\n" 
			+ "${YEAR} : year as yyyy\n"
			+ "${YR} : year as yy\n"
			+ "${MONTH} : month as MM\n" 
			+ "${MONTHSTR} : month as MMM (e.g., Jul)\n"
			+ "${DAY} : day as dd\n"
			+ "${DAYSTR} : day as ddd (e.g., Mon)\n"
			+ "${DATE} : date as yyyyMMdd (e.g., 20111231)\n"
			+ "${DATE-} : date as yyyy-MM-dd (e.g., 2011-12-31)\n"
			+ "${TIME} : time as HHmmss\n"
			+ "${TIME-} : time as HH-mm-ss\n"
			+ "${PV:<pvname>} : replace with value of pvname";
	
	private static final String MEMENTO_PROPERTIES_EXPANDED = "propertiesExpanded";
	private static final String MEMENTO_MONITORS_EXPANDED = "monitorsExpanded";
	
	private ScanDescription currentScanDescription;

	private ScrolledComposite sc = null;
	private Composite top;
	
	private Text commentText;
	private Text filenameText;
	private Label filenameResolvedLabel;
	private Button browseButton;
	private Label repeatCountLabel;
	private Text repeatCountText;
	private Button saveSCMLCheckbox;
	private Button confirmSaveCheckbox;
	private Button autoIncrementCheckbox;

	private Button editButton;
	private ComboViewer monitorsCombo;
	private TableViewerColumn delColumn;

	private TableViewer monitorOptionsTable;
	private OptionColumnSelectionListener optionColumnSelectionListener;
	private DeviceColumnSelectionListener deviceColumnSelectionListener;
	private OptionColumnComparator optionColumnComparator;
	private DeviceColumnComparator deviceColumnComparator;
	
	private Image ascending;
	private Image descending;
	
	private int optionColumnSortState; // 0 no sort, 1 asc, 2 desc
	private int deviceColumnSortState; // 0 no sort, 1 asc, 2 desc
	
	private DataBindingContext context;
	private ISelectionProvider selectionProvider;
	private IObservableValue selectionObservable;

	private Binding repeatCountBinding;
	
	// Delegates
	private EditorViewPerspectiveListener perspectiveListener;

	private IMemento memento;

	private ExpandItem propertiesItem;
	private Composite propertiesComposite;
	
	private ExpandItem monitorsItem;
	private Composite monitorsComposite;

	/**
	 * {@inheritDoc}
	 * @since 1.33
	 */
	@Override
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		init(site);
		this.memento = memento;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createPartControl(final Composite parent) {
		LOGGER.debug("createPartControl");

		parent.setLayout(new FillLayout());

		// if no measuring station is loaded -> show error and do nothing
		if (Activator.getDefault().getMeasuringStation() == null) {
			final Label errorLabel = new Label(parent, SWT.NONE);
			errorLabel.setText("No Measuring Station has been loaded. "
					+ "Please check Preferences!");
			return;
		}

		this.sc = new ScrolledComposite(parent, SWT.V_SCROLL);

		// top composite
		this.top = new Composite(sc, SWT.NONE);
		this.top.setLayout(new FillLayout());

		this.sc.setExpandHorizontal(true);
		this.sc.setExpandVertical(true);
		this.sc.setContent(this.top);

		ExpandBar expandBar = new ExpandBar(this.top, SWT.V_SCROLL);
		
		propertiesItem = new ExpandItem(expandBar, SWT.NONE);
		propertiesItem.setText("Properties");
		
		propertiesComposite = new Composite(expandBar, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		gridLayout.horizontalSpacing = 5;
		propertiesComposite.setLayout(gridLayout);
		propertiesItem.setControl(propertiesComposite);
		propertiesItem.setExpanded(true);
		
		Label commentLabel = new Label(propertiesComposite, SWT.NONE);
		commentLabel.setText("Comment:");
		GridData gridData = new GridData();
		gridData.verticalAlignment = SWT.TOP;
		commentLabel.setLayoutData(gridData);
		
		commentText = new Text(propertiesComposite, 
				SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.V_SCROLL);
		commentText.setToolTipText("Scan comment. " + ScanView.MACRO_TOOLTIP);
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.horizontalSpan = 2;
		gridData.heightHint = 60;
		commentText.setLayoutData(gridData);
		
		Label filenameLabel = new Label(propertiesComposite, SWT.NONE);
		filenameLabel.setText("Filename:");
		gridData = new GridData();
		gridData.verticalAlignment = GridData.CENTER;
		gridData.verticalSpan = 2;
		filenameLabel.setLayoutData(gridData);
		
		filenameText = new Text(propertiesComposite, SWT.BORDER);
		filenameText.setToolTipText(
				"The filename where the data should be saved.\n" + 
						ScanView.MACRO_TOOLTIP);
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		filenameText.setLayoutData(gridData);
		
		browseButton = new Button(propertiesComposite, SWT.NONE);
		browseButton.setText("Browse...");
		gridData = new GridData();
		gridData.verticalSpan = 2;
		gridData.verticalAlignment = GridData.FILL;
		browseButton.setLayoutData(gridData);
		browseButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				int lastSeparatorIndex;
				final String filePath;
				
				if (currentScanDescription.getSaveFilename() != null && 
						!currentScanDescription.getSaveFilename().isEmpty()) {
					// filename present -> show the path
					lastSeparatorIndex = currentScanDescription.getSaveFilename().lastIndexOf(File.separatorChar);
					filePath = currentScanDescription.getSaveFilename().substring(0, lastSeparatorIndex + 1);
				} else {
					// no filename -> set path to defaults or <rootDir>/daten/ or <rootDir>
					File workDir = de.ptb.epics.eve.resources.Activator.
						getDefault().getDefaultsManager().getWorkingDirectory();
					if (workDir.isDirectory()) {
						filePath = workDir.getAbsolutePath();
					} else {
						String rootDir = Activator.getDefault().getRootDirectory();
						File file = new File(rootDir + "daten/");
						if (file.exists()) {
							filePath = rootDir + "daten/";
						} else {
							filePath = rootDir;
						}
					}
				}
				
				FileDialog fileWindow = new FileDialog(getSite().getShell(), SWT.SAVE);
				fileWindow.setFilterPath(filePath);
				String fileName = fileWindow.open();
				if (fileName != null) {
					// try to get suffix parameter of the selected plugin
					PluginParameter pluginParameter = null;
					for (PluginParameter param : currentScanDescription.
							getSavePluginController().getPlugin().getParameters()) {
						if (param.getName().equals("suffix")) {
							pluginParameter = param;
							break;
						}
					}
					if (pluginParameter != null) {
						// suffix found -> replace
						String suffix = currentScanDescription.getSavePluginController().get("suffix").toString();
						// remove old suffix
						final int lastPoint = fileName.lastIndexOf('.');
						final int lastSep = fileName.lastIndexOf('/');
						
						if ((lastPoint > 0) && (lastPoint > lastSep)) {
							currentScanDescription.setSaveFilename(
									fileName.substring(0, lastPoint) + 
									"." + suffix);
						} else {
						currentScanDescription.setSaveFilename(fileName + "." + suffix);
						}
					} else {
						currentScanDescription.setSaveFilename(fileName);
					}
					filenameText.setSelection(filenameText.getText().length());
				}
			}
		});
		
		filenameResolvedLabel = new Label(propertiesComposite, SWT.NONE);
		filenameResolvedLabel.setText("");
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = GridData.CENTER;
		gridData.horizontalAlignment = GridData.FILL;
		filenameResolvedLabel.setLayoutData(gridData);

		this.repeatCountLabel = new Label(propertiesComposite, SWT.NONE);
		this.repeatCountLabel.setText("Repeat Count:");

		Composite repeatCountComposite = new Composite(propertiesComposite, SWT.NONE);
		gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		gridLayout.marginWidth = 0;
		gridLayout.horizontalSpacing = 0;
		repeatCountComposite.setLayout(gridLayout);
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.LEFT;
		gridData.grabExcessHorizontalSpace = true;
		repeatCountComposite.setLayoutData(gridData);

		this.repeatCountText = new Text(repeatCountComposite, SWT.BORDER);
		this.repeatCountText.setToolTipText("number of times the scan will be repeated");
		this.repeatCountText.addFocusListener(new TextSelectAllFocusListener(
				this.repeatCountText));
		this.repeatCountText.addFocusListener(new TextFocusListener(
				this.repeatCountText));
		this.repeatCountText.addMouseListener(new TextSelectAllMouseListener(
				this.repeatCountText));
		
		Composite checkboxComposite = new Composite(propertiesComposite, SWT.NONE);
		RowLayout checkboxLayout = new RowLayout();
		checkboxLayout.wrap = true;
		checkboxLayout.pack = true;
		checkboxLayout.justify = false;
		checkboxComposite.setLayout(checkboxLayout);
		gridData = new GridData();
		gridData.horizontalSpan = 3;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		checkboxComposite.setLayoutData(gridData);
		
		saveSCMLCheckbox = new Button(checkboxComposite, SWT.CHECK);
		saveSCMLCheckbox.setText("Save SCML");
		saveSCMLCheckbox.setToolTipText(
				"Save scan description within HDF file " +
				"(or separate in case if ASCII)");
		
		confirmSaveCheckbox = new Button(checkboxComposite, SWT.CHECK);
		confirmSaveCheckbox.setText("Confirm Save");
		confirmSaveCheckbox.setToolTipText(
				"Check if saving the datafile should be confirmed");
		
		autoIncrementCheckbox = new Button(checkboxComposite, SWT.CHECK);
		autoIncrementCheckbox.setText("Append Autoincrementing Number");
		
		propertiesItem.setHeight(propertiesComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		
		monitorsItem = new ExpandItem(expandBar, SWT.NONE);
		monitorsItem.setText("Monitors");
		
		monitorsComposite = new Composite(expandBar, SWT.NONE);
		gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		gridLayout.horizontalSpacing = 5;
		monitorsComposite.setLayout(gridLayout);
		monitorsItem.setControl(monitorsComposite);
		monitorsItem.setExpanded(true);
		
		Label monitorsLabel = new Label(monitorsComposite, SWT.NONE);
		monitorsLabel.setText("Monitored Devices:");
		
		monitorsCombo = new ComboViewer(monitorsComposite, SWT.READ_ONLY);
		monitorsCombo.setContentProvider(ArrayContentProvider.getInstance());
		monitorsCombo.setInput(MonitorOption.values());
		
		this.editButton = new Button(monitorsComposite, SWT.NONE);
		this.editButton.setText("Edit");
		this.editButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MonitorOptionsDialog dialog = new MonitorOptionsDialog(PlatformUI
						.getWorkbench().getActiveWorkbenchWindow().getShell(),
						currentScanDescription);
				currentScanDescription.setMonitorOption(MonitorOption.CUSTOM);
				dialog.setBlockOnOpen(true);
				dialog.open();
			}
		});
		
		ascending = de.ptb.epics.eve.util.ui.Activator.getDefault()
				.getImageRegistry().get("SORT_ASCENDING");
		descending = de.ptb.epics.eve.util.ui.Activator.getDefault()
				.getImageRegistry().get("SORT_DESCENDING");
		
		this.createTable(monitorsComposite);
		
		monitorsItem.setHeight(monitorsComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		
		expandBar.addExpandListener(new ExpandListener() {
			@Override
			public void itemExpanded(ExpandEvent e) {
				if (e.item.equals(propertiesItem)) {
					setPropertiesExpandItemText(true);
				} else if (e.item.equals(monitorsItem)) {
					setMonitorsExpandItemText(true);
				}
			}
			@Override
			public void itemCollapsed(ExpandEvent e) {
				if (e.item.equals(propertiesItem)) {
					setPropertiesExpandItemText(false);
				} else if (e.item.equals(monitorsItem)) {
					setMonitorsExpandItemText(false);
				}
			}
		});
		
		this.top.setVisible(false);
		
		// listen to selection changes (if a chain (or one of its scan modules)
		// is selected, its attributes are made available for editing)
		getSite().getWorkbenchWindow().getSelectionService()
				.addSelectionListener(this);

		perspectiveListener = new EditorViewPerspectiveListener(this);
		PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.addPerspectiveListener(perspectiveListener);

		this.bindValues();
		this.restoreState();
		this.refreshExpandItemTexts();
	}
	
	private void createTable(final Composite parent) {
		this.monitorOptionsTable = new TableViewer(parent, SWT.BORDER);
		this.monitorOptionsTable.getTable().setHeaderVisible(true);
		this.monitorOptionsTable.getTable().setLinesVisible(true);
		this.optionColumnComparator = new OptionColumnComparator();
		this.deviceColumnComparator = new DeviceColumnComparator();
		this.optionColumnSortState = 0;
		this.deviceColumnSortState = 0;
		this.optionColumnSelectionListener = new OptionColumnSelectionListener();
		this.deviceColumnSelectionListener = new DeviceColumnSelectionListener();
		
		this.createColumns(this.monitorOptionsTable);
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.minimumHeight = 110;
		gridData.horizontalAlignment = SWT.FILL;
		gridData.verticalAlignment = SWT.FILL;
		gridData.horizontalSpan = 3;
		this.monitorOptionsTable.getTable().setLayoutData(gridData);
		
		this.monitorOptionsTable.setContentProvider(
				ArrayContentProvider.getInstance());
		this.getSite().setSelectionProvider(this.monitorOptionsTable);
		
		// create context menu
		MenuManager menuManager = new MenuManager();
		menuManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		menuManager.setRemoveAllWhenShown(true);
		this.monitorOptionsTable.getTable().setMenu(
			menuManager.createContextMenu(this.monitorOptionsTable.getTable()));
		// register menu
		this.getSite().registerContextMenu(
			"de.ptb.epics.eve.editor.views.scanview.monitoroptions.popup", 
			menuManager, this.monitorOptionsTable);
	}

	private void createColumns(final TableViewer viewer) {
		delColumn = new TableViewerColumn(viewer, SWT.NONE);
		delColumn.getColumn().setWidth(ScanView.DEL_COLUMN_WIDTH);
		delColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return "";
			}
			@Override
			public Image getImage(Object element) {
				return PlatformUI.getWorkbench().getSharedImages().
						getImage(ISharedImages.IMG_TOOL_DELETE);
			}
		});
		delColumn.setEditingSupport(new DelColumnEditingSupport(viewer, 
				"org.eclipse.ui.edit.delete"));
		TableViewerColumn oNameCol = new TableViewerColumn(viewer, SWT.NONE);
		oNameCol.getColumn().setText("Option Name");
		oNameCol.getColumn().setWidth(140);
		oNameCol.getColumn().addSelectionListener(optionColumnSelectionListener);
		oNameCol.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((Option)element).getName();
			}
		});
		TableViewerColumn dNameCol = new TableViewerColumn(viewer, SWT.NONE);
		dNameCol.getColumn().setText("Device Name");
		dNameCol.getColumn().setWidth(140);
		dNameCol.getColumn().addSelectionListener(deviceColumnSelectionListener);
		dNameCol.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((Option)element).getParent().getName();
			}
		});
	}

	private void bindValues() {
		this.context = new DataBindingContext();

		this.selectionProvider = new ScanSelectionProvider();
		this.selectionObservable = ViewersObservables
				.observeSingleSelection(selectionProvider);
		
		IObservableValue commentTargetObservable = SWTObservables.
				observeText(this.commentText, SWT.Modify);
		IObservableValue commentTargetDelayedObservable = SWTObservables.
				observeDelayedValue(1000, 
						(ISWTObservableValue) commentTargetObservable);
		IObservableValue commentModelObservable = BeansObservables.
				observeDetailValue(selectionObservable, ScanDescription.class, 
						ScanDescription.COMMENT_PROP, String.class);
		this.context.bindValue(commentTargetDelayedObservable, 
				commentModelObservable, 
				new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE), 
				new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE));
		
		IObservableValue filenameTextTargetObservable = SWTObservables.
				observeText(this.filenameText, SWT.Modify);
		IObservableValue filenameTextDelayedObservable = SWTObservables.
				observeDelayedValue(500, 
						(ISWTObservableValue) filenameTextTargetObservable);
		IObservableValue filenameTextModelObservable = BeansObservables.
				observeDetailValue(selectionObservable,
						ScanDescription.class,
						ScanDescription.SAVE_FILE_NAME_PROP, 
						String.class);
		UpdateValueStrategy filenameTargetStrategy = new UpdateValueStrategy();
		filenameTargetStrategy.setAfterGetValidator(new IValidator() {
			@Override
			public IStatus validate(Object value) {
				if (filenameText.getText().isEmpty()) {
					return ValidationStatus.error("Filename must not be empty!");
				}
				File file = new File(filenameText.getText());
				if (file.isFile() && file.exists()) {
					return ValidationStatus.warning("File already exists!");
				} else if (file.isFile()) {
					return ValidationStatus.error("Given filename/path is not valid!");
				}
				return ValidationStatus.ok();
			}
		});
		final Binding filenameTextBinding = this.context.bindValue(
				filenameTextDelayedObservable,
				filenameTextModelObservable,
				filenameTargetStrategy,
				new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE));
		ControlDecorationSupport.create(filenameTextBinding, SWT.LEFT);
		filenameText.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				filenameTextBinding.updateModelToTarget();
			}
		});
		
		IObservableValue filenameLabelTargetObservable = SWTObservables.
				observeText(this.filenameResolvedLabel);
		IObservableValue filenameLabelModelObservable = BeansObservables.
				observeDetailValue(selectionObservable, ScanDescription.class,
						ScanDescription.RESOLVED_FILENAME_PROP, String.class);
		filenameLabelModelObservable.addChangeListener(new IChangeListener() {
			@Override
			public void handleChange(ChangeEvent event) {
				filenameResolvedLabel.setToolTipText(
						currentScanDescription.getResolvedFilename());
			}
		});
		this.context.bindValue(filenameLabelTargetObservable, 
				filenameLabelModelObservable, 
				new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE), 
				new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE));
		
		IObservableValue saveSCMLTargetObservable = SWTObservables.
				observeSelection(saveSCMLCheckbox);
		IObservableValue saveSCMLModelObservable = BeansObservables.
				observeDetailValue(selectionObservable, 
						ScanDescription.class, 
						ScanDescription.SAVE_SCAN_DESCRIPTION_PROP, 
						Boolean.class);
		this.context.bindValue(saveSCMLTargetObservable, 
				saveSCMLModelObservable, 
				new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE), 
				new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE));
		
		IObservableValue confirmSaveTargetObservable = SWTObservables.
				observeSelection(confirmSaveCheckbox);
		IObservableValue confirmSaveModelObservable = BeansObservables.
				observeDetailValue(selectionObservable, 
						ScanDescription.class, 
						ScanDescription.CONFIRM_SAVE_PROP, 
						Boolean.class);
		this.context.bindValue(confirmSaveTargetObservable, 
				confirmSaveModelObservable, 
				new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE), 
				new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE));
		
		IObservableValue autoIncrementTargetObservable = SWTObservables.
				observeSelection(autoIncrementCheckbox);
		IObservableValue autoImcrementModelObservable = BeansObservables.
				observeDetailValue(selectionObservable, 
						ScanDescription.class, 
						ScanDescription.AUTO_INCREMENT_PROP, 
						Boolean.class);
		this.context.bindValue(autoIncrementTargetObservable, 
				autoImcrementModelObservable, 
				new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE), 
				new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE));
		
		IObservableValue repeatCountTargetObservable = SWTObservables.
				observeText(this.repeatCountText,SWT.Modify);
		IObservableValue repeatCountModelObservable = BeansObservables.
				observeDetailValue(selectionObservable, 
						ScanDescription.REPEAT_COUNT_PROP, 
						Integer.class);
		UpdateValueStrategy targetToModelStrategy = new UpdateValueStrategy(
				UpdateValueStrategy.POLICY_UPDATE);
		targetToModelStrategy.setAfterGetValidator(new RepeatCountValidator());
		targetToModelStrategy.setConverter(new RepeatCountConverter());
		this.repeatCountBinding = this.context.bindValue(
				repeatCountTargetObservable, repeatCountModelObservable,
				targetToModelStrategy, new UpdateValueStrategy(
						UpdateValueStrategy.POLICY_UPDATE));
		ControlDecorationSupport.create(repeatCountBinding, SWT.LEFT);

		IObservableValue monitorsComboTargetObservable = ViewersObservables.
				observeSingleSelection(monitorsCombo);
		IObservableValue monitorsComboModelObservable = BeansObservables.
				observeDetailValue(selectionObservable, 
						ScanDescription.MONITOR_OPTION_PROP, 
						MonitorOption.class);
		this.context.bindValue(
				monitorsComboTargetObservable,
				monitorsComboModelObservable, 
				new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE),
				new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE));
	}

	private void setCurrentScanDescription(ScanDescription scanDescription) {
		if (this.currentScanDescription != null) {
			this.currentScanDescription.removePropertyChangeListener(
					ScanDescription.FILE_NAME_PROP, this);
			this.currentScanDescription.removePropertyChangeListener(
					ScanDescription.MONITOR_OPTION_PROP, this);
			this.currentScanDescription.removePropertyChangeListener(
					ScanDescription.MONITOR_OPTIONS_LIST_PROP, this);
			this.monitorOptionsTable.setInput(null);
		}
		this.currentScanDescription = scanDescription;
		if (this.currentScanDescription == null) {
			this.setPartName("Scan View");
			this.top.setVisible(false);
		} else {
			if (PlatformUI.getWorkbench().getActiveWorkbenchWindow()
					.getActivePage() != null) {
				this.setPartName("Scan: "
						+ PlatformUI.getWorkbench().getActiveWorkbenchWindow()
								.getActivePage().getActiveEditor().getTitle());
			}
			this.currentScanDescription.addPropertyChangeListener(
					ScanDescription.FILE_NAME_PROP, this);
			this.currentScanDescription.addPropertyChangeListener(
					ScanDescription.MONITOR_OPTION_PROP, this);
			this.currentScanDescription.addPropertyChangeListener(
					ScanDescription.MONITOR_OPTIONS_LIST_PROP, this);
			this.monitorOptionsTable.setInput(
					this.currentScanDescription.getMonitors());
			this.adjustColumnWidths();
			this.top.setVisible(true);
		}
		this.refreshExpandItemTexts();
	}

	/**
	 * Returns the currently active scan description.
	 * 
	 * @return the scan description
	 */
	public ScanDescription getCurrentScanDescription() {
		return currentScanDescription;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		LOGGER.debug("selection changed");

		if (selection instanceof IStructuredSelection) {
			if (((IStructuredSelection) selection).size() == 0) {
				return;
			}
		}
		Object o = ((IStructuredSelection) selection).toList().get(0);
		if (o instanceof ScanModuleEditPart) {
			// a scan module belongs to a chain -> show chain's scan description
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("ScanModule "
						+ ((ScanModuleEditPart) o).getModel() + " selected.");
			}
			setCurrentScanDescription(((ScanModuleEditPart) o).getModel()
					.getChain().getScanDescription());
		} else if (o instanceof StartEventEditPart) {
			// a start event belongs to a chain -> show chain's scan description
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("ScanDescription "
						+ (((StartEventEditPart) o).getModel()).getChain()
								.getScanDescription() + " selected.");
			}
			setCurrentScanDescription((((StartEventEditPart) o).getModel())
					.getChain().getScanDescription());
		} else if (o instanceof ChainEditPart) {
			LOGGER.debug("selection is ChainEditPart: " + o);
			setCurrentScanDescription(((ChainEditPart) o).getModel()
					.getScanDescription());
		} else if (o instanceof ScanDescriptionEditPart) {
			LOGGER.debug("selection is ScanDescriptionEditPart: " + o);
			setCurrentScanDescription(((ScanDescriptionEditPart) o).getModel());
		} else {
			LOGGER.debug("selection other than ScanDescription -> ignore: " + o);
		}
	}

	/**
	 * {@inheritDoc}
	 * @since 1.33
	 */
	@Override
	public void saveState(IMemento memento) {
		memento.putBoolean(ScanView.MEMENTO_PROPERTIES_EXPANDED, 
				this.propertiesItem.getExpanded());
		memento.putBoolean(ScanView.MEMENTO_MONITORS_EXPANDED, 
				this.monitorsItem.getExpanded());
	}
	
	/*
	 * @since 1.33
	 */
	private void restoreState() {
		if (this.memento.getBoolean(ScanView.MEMENTO_PROPERTIES_EXPANDED) != null) {
			this.propertiesItem.setExpanded(
				this.memento.getBoolean(ScanView.MEMENTO_PROPERTIES_EXPANDED));
		}
		if (this.memento.getBoolean(ScanView.MEMENTO_MONITORS_EXPANDED) != null) {
			this.monitorsItem.setExpanded(
				this.memento.getBoolean(ScanView.MEMENTO_MONITORS_EXPANDED));
		}
	}
	
	/*
	 * Expand Items should show more information when they are collapsed. The 
	 * contents has to be refreshed if 
	 * - a new scan is loaded
	 * - a new scan is created
	 * - the user switches between two open scans
	 * - when the expand bar is collapsed (or expanded)
	 * @since 1.33
	 */
	private void refreshExpandItemTexts() {
		this.setPropertiesExpandItemText(this.propertiesItem.getExpanded());
		this.setMonitorsExpandItemText(this.monitorsItem.getExpanded());
	}
	
	private void setPropertiesExpandItemText(boolean expanded) {
		if (this.currentScanDescription == null) {
			this.propertiesItem.setText("Properties");
			return;
		}
		if (expanded) {
			this.propertiesItem.setText("Properties");
		} else {
			StringBuilder builder = new StringBuilder();
			builder.append("Properties");
			builder.append(" (" + this.getFilteredFilename());
			if (!currentScanDescription.getComment().isEmpty()) {
				builder.append(", 'comment'");
			}
			if (currentScanDescription.getRepeatCount() > 0) {
				builder.append(", repeat: " + currentScanDescription.getRepeatCount());
			}
			if (currentScanDescription.isSaveScanDescription()) {
				builder.append(", save SCML");
			}
			if (currentScanDescription.isConfirmSave()) {
				builder.append(", confirm");
			}
			if (currentScanDescription.isAutoNumber()) {
				builder.append(", autoinc");
			}
			builder.append(")");
			this.propertiesItem.setText(builder.toString());
		}
	}
	
	/*
	 * removes "/messung/<anything>/daten/" from filename, if found
	 * @since 1.33
	 */
	private String getFilteredFilename() {
		String fileName = this.currentScanDescription.getResolvedFilename();
		PathMatcher matcher = FileSystems.getDefault().getPathMatcher(
				"glob:/messung/*/daten/**");
		if (matcher.matches(Paths.get(fileName))) {
			return fileName.replaceFirst("/messung/.*/daten/", ".../");
		}
		return fileName;
	}
	
	private void setMonitorsExpandItemText(boolean expanded) {
		if (this.currentScanDescription == null) {
			this.monitorsItem.setText("Monitors");
			return;
		}
		if (expanded) {
			this.monitorsItem.setText("Monitors");
		} else {
			StringBuilder builder = new StringBuilder();
			builder.append("Monitors");
			builder.append(" (" + 
					this.currentScanDescription.getMonitorOption().toString());
			if (!this.currentScanDescription.getMonitors().isEmpty()) {
				int count = this.currentScanDescription.getMonitors().size();
				builder.append(", " + count); 
				if (count == 1) {
					builder.append(" monitor");
				} else {
					builder.append(" monitors");
				}
			}
			builder.append(")");
			this.monitorsItem.setText(builder.toString());
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void reset() {
		this.setCurrentScanDescription(null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setFocus() {
		this.top.setFocus();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void propertyChange(PropertyChangeEvent e) {
		if (e.getPropertyName().equals(ScanDescription.FILE_NAME_PROP)) {
			this.setPartName("Scan: " + e.getNewValue());
			LOGGER.debug("new file name: " + e.getNewValue());
		} else if (e.getPropertyName().equals(ScanDescription.RESOLVED_FILENAME_PROP)) { 
			this.refreshExpandItemTexts();
		} else if (e.getPropertyName().equals(ScanDescription.MONITOR_OPTION_PROP)) {
			switch ((MonitorOption)e.getNewValue()) {
			case AS_IN_DEVICE_DEFINITION:
				this.hideDelColumn();
				break;
			case CUSTOM:
				this.showDelColumn();
				break;
			case NONE:
				this.hideDelColumn();
				break;
			case USED_IN_SCAN:
				this.hideDelColumn();
				break;
			default:
				break;
			}
			this.monitorOptionsTable.refresh();
			this.adjustColumnWidths();
		} else if (e.getPropertyName().equals(
				ScanDescription.MONITOR_OPTIONS_LIST_PROP)) {
			this.monitorOptionsTable.refresh();
			this.adjustColumnWidths();
			LOGGER.debug("monitor option list changed");
		}
	}
	
	private void hideDelColumn() {
		this.delColumn.getColumn().setWidth(0);
		this.delColumn.getColumn().setResizable(false);
		LOGGER.debug("hiding del column");
	}

	private void showDelColumn() {
		this.delColumn.getColumn().setWidth(ScanView.DEL_COLUMN_WIDTH);
		this.delColumn.getColumn().setResizable(true);
		this.adjustColumnWidths();
		LOGGER.debug("showing del column");
	}

	private void adjustColumnWidths() {
		GC gc = new GC(this.monitorOptionsTable.getTable());
		int optionColumnWidth = 120;
		int deviceColumnWidth = 120;
		for (Option o : this.currentScanDescription.getMonitors()) {
			int deviceNameWidth = FontHelper.getCharWidth(gc, o.getParent()
					.getName()) + FontHelper.MARGIN_WIDTH;
			if (deviceNameWidth > deviceColumnWidth) {
				deviceColumnWidth = deviceNameWidth;
			}
			int optionNameWidth = FontHelper.getCharWidth(gc, o.getName())
					+ FontHelper.MARGIN_WIDTH;
			if (optionNameWidth > optionColumnWidth) {
				optionColumnWidth = optionNameWidth;
			}
		}
		this.monitorOptionsTable.getTable().getColumn(1)
				.setWidth(optionColumnWidth);
		this.monitorOptionsTable.getTable().getColumn(2)
				.setWidth(deviceColumnWidth);
	}
	
	/**
	 * @author Marcus Michalsky
	 * @since 1.10
	 */
	private class TextFocusListener extends FocusAdapter {
		private Text widget;

		/**
		 * @param widget
		 *            the widget to observe
		 */
		public TextFocusListener(Text widget) {
			this.widget = widget;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void focusLost(FocusEvent e) {
			if (this.widget == repeatCountText) {
				repeatCountBinding.updateModelToTarget();
			}
		}
	}
	
	private class OptionColumnSelectionListener extends SelectionAdapter {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void widgetSelected(SelectionEvent e) {
			LOGGER.debug("option column clicked");
			LOGGER.debug("old option table sort state: " + optionColumnSortState);
			switch(optionColumnSortState) {
				case 0: // was no sorting -> now ascending
						optionColumnComparator.setDirection(
								OptionColumnComparator.ASCENDING);
						monitorOptionsTable.setComparator(optionColumnComparator);
						monitorOptionsTable.getTable().getColumn(1).
								setImage(ascending);
						break;
				case 1: // was ascending -> now descending
						optionColumnComparator.setDirection(
								OptionColumnComparator.DESCENDING);
						monitorOptionsTable.setComparator(optionColumnComparator);
						monitorOptionsTable.refresh();
						monitorOptionsTable.getTable().getColumn(1).
								setImage(descending);
						break;
				case 2: // was descending -> now no sorting
						monitorOptionsTable.setComparator(null);
						monitorOptionsTable.getTable().getColumn(1).setImage(null);
						break;
			}
			// reset device column sort state
			monitorOptionsTable.getTable().getColumn(2).setImage(null);
			deviceColumnSortState = 0;
			// set is {0,1,2}
			// if it becomes 3 it has to be 0 again
			// but before the state has to be increased to the new state
			optionColumnSortState = ++optionColumnSortState % 3;
			LOGGER.debug("new options table sort state: " + optionColumnSortState);
		}
	}
	
	private class DeviceColumnSelectionListener extends SelectionAdapter {
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void widgetSelected(SelectionEvent e) {
			LOGGER.debug("device name column clicked");
			LOGGER.debug("old device table sort state: " + deviceColumnSortState);
			switch(deviceColumnSortState) {
				case 0: // was no sorting -> now ascending
						deviceColumnComparator.setDirection(
								OptionColumnComparator.ASCENDING);
						monitorOptionsTable.setComparator(deviceColumnComparator);
						monitorOptionsTable.getTable().getColumn(2).setImage(ascending);
						break;
				case 1: // was ascending -> now descending
						deviceColumnComparator.setDirection(
								OptionColumnComparator.DESCENDING);
						monitorOptionsTable.setComparator(deviceColumnComparator);
						monitorOptionsTable.refresh();
						monitorOptionsTable.getTable().getColumn(2).setImage(descending);
						break;
				case 2: // was descending -> now no sorting
						monitorOptionsTable.setComparator(null);
						monitorOptionsTable.getTable().getColumn(2).setImage(null);
						break;
			}
			// no sorting of option name column
			monitorOptionsTable.getTable().getColumn(1).setImage(null);
			optionColumnSortState = 0;
			// set is {0,1,2}
			// if it becomes 3 it has to be 0 again
			// but before the state has to be increased to the new state
			deviceColumnSortState = ++deviceColumnSortState % 3;
			LOGGER.debug("new device table sort state: " + deviceColumnSortState);
		}
	}
}