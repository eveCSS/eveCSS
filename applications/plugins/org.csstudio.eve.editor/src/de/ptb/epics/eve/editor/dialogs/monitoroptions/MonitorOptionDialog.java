package de.ptb.epics.eve.editor.dialogs.monitoroptions;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.jface.window.Window;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbenchActionConstants;

import de.ptb.epics.eve.data.measuringstation.Option;
import de.ptb.epics.eve.data.measuringstation.AbstractDevice;
import de.ptb.epics.eve.data.measuringstation.IMeasuringStation;
import de.ptb.epics.eve.data.measuringstation.MotorAxis;
import de.ptb.epics.eve.data.scandescription.ScanDescription;
import de.ptb.epics.eve.editor.Activator;

/**
 * 
 * @author Hartmut Scherr
 * @since 1.14
 */
public class MonitorOptionDialog extends TitleAreaDialog {

	private static Logger logger = 
			Logger.getLogger(MonitorOptionDialog.class.getName());

	private ScanDescription scanDescription;
	
	// the model visualized by the tree
	private IMeasuringStation measuringStation;

	// the tree viewer visualizing the model
	private TreeViewer treeViewer;

	// listener (for focus lost deselection)
	private TreeViewerFocusListener treeViewerFocusListener;

	private TreeViewerSelectionChangedListener treeViewerSelectionChangedListener;
	
	// flag indicating if a drag is in progress
	// (necessary to "block" the focus lost effect when dragging)
	private boolean dragInProgress;
	
	// the table of options
	private TableViewer optionsTable;
	private ContentProvider optionsTableContentProvider;
	
	// Liste der Optionen die in der Tabelle angezeigt werden
	private List<AbstractDevice> tableDevices;
	
	/**
	 * Constructor.
	 * 
	 * @param shell 
	 * @param scanDescriptionLoader
	 */
	public MonitorOptionDialog(final Shell shell,
			final ScanDescription scanDescription) {
		super(shell);
		this.scanDescription = scanDescription;
	}

	/**
	 * {@inheritDoc}
	 */
	protected Control createDialogArea(final Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);

		Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		container.setLayout(gridLayout);

		measuringStation = Activator.getDefault().getMeasuringStation();

		this.tableDevices = new ArrayList<AbstractDevice>();
		
		this.createViewer(container);
		
		this.setTitle("Monitored Devices: " + 
				scanDescription.getMonitorOption().name() + " is selected");
		this.setMessage("");

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
	
	/*
	 * 
	 */
	private void createViewer(Composite parent) {
		
		treeViewer = new TreeViewer(parent);
		treeViewer.setContentProvider(new TreeViewerContentProvider());
		treeViewer.setLabelProvider(new TreeViewerLabelProvider());
		treeViewer.getTree().setEnabled(false);
		treeViewer.setAutoExpandLevel(1);
	
		treeViewerFocusListener = new TreeViewerFocusListener();
		treeViewer.getTree().addFocusListener(treeViewerFocusListener);

		// create context menu
		MenuManager menuManager = new MenuManager();
		menuManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		menuManager.setRemoveAllWhenShown(true);
		treeViewer.getTree().setMenu(
				menuManager.createContextMenu(treeViewer.getTree()));
		// register menu

		treeViewer.setInput(measuringStation);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		treeViewer.getTree().setLayoutData(gridData);
		treeViewer.getTree().setEnabled(measuringStation != null);

		optionsTable = new TableViewer(parent);
		createColumns(parent, optionsTable);
		
		this.optionsTable.getTable().setHeaderVisible(true);
		this.optionsTable.getTable().setLinesVisible(true);
		this.optionsTableContentProvider = new ContentProvider();
		this.optionsTable.setContentProvider(optionsTableContentProvider);
		this.optionsTable.setInput(this.tableDevices);
		
// fehlt noch, noch kann die Table nicht sortiert werden
//		ascending =	 Activator.getDefault().getImageRegistry().get("ASCENDING");
//		descending = Activator.getDefault().getImageRegistry().get("DESCENDING");

		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		optionsTable.getTable().setLayoutData(gridData);

		// Listener hinzufügen
		this.treeViewerSelectionChangedListener =
				new TreeViewerSelectionChangedListener();
		treeViewer.addSelectionChangedListener(
				treeViewerSelectionChangedListener);
		
	}

	/*
	 * helper for createPartControl
	 */
	private void createColumns(final Composite parent, final TableViewer viewer) {
		
		// Enable tooltips
		ColumnViewerToolTipSupport.enableFor(
				optionsTable, ToolTip.NO_RECREATE);

		// check box column wird angelegt
		final TableViewerColumn selColumn =
				new TableViewerColumn(viewer, SWT.NONE);
		selColumn.getColumn().setText("Select");
		selColumn.getColumn().setWidth(50);
		selColumn.setEditingSupport(new SelColumnEditingSupport(
				viewer, scanDescription));
		selColumn.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				return "";
			}
			@Override
			public Image getImage(Object element) {
				Option o = (Option)element;

				if (scanDescription.getMonitors().contains(o)) {
					// Option wird schon gemonitort
					return Activator.getDefault().getImageRegistry().get("CHECKED");
				}
				else {
					return Activator.getDefault().getImageRegistry().get("UNCHECKED");
				}
			}
		});
		
		// column für option name wird angelegt
		final TableViewerColumn optionViewerColumn = 
				new TableViewerColumn(viewer, SWT.NONE);
		final TableColumn optionColumn = optionViewerColumn.getColumn();
		optionColumn.setText("Option Name");
		optionColumn.setWidth(100);
		optionColumn.setResizable(true);
		optionViewerColumn.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				return ((Option)element).getName();
			}
		});
		
		// column für device name wird angelegt
		final TableViewerColumn deviceViewerColumn = 
				new TableViewerColumn(viewer, SWT.NONE);
		final TableColumn deviceColumn = deviceViewerColumn.getColumn();
		deviceColumn.setText("Device Name");
		deviceColumn.setWidth(100);
		deviceColumn.setResizable(true);
		deviceViewerColumn.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				return ((Option)element).getParent().getName();
			}
		});

	}
	
	/**
	 * 
	 * @author Marcus Michalsky
	 */
	private class TreeViewerFocusListener implements FocusListener {
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void focusGained(FocusEvent e) {
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void focusLost(FocusEvent e) {
			if(!dragInProgress) {
				treeViewer.getTree().deselectAll();
			}
		}
	}

	/**
	 * <code>TreeViewerSelectionListener</code>.
	 * 
	 * @author Hartmut Scherr
	 * @since 1.14
	 */
	private class TreeViewerSelectionChangedListener implements ISelectionChangedListener {
		
		@Override
		public void selectionChanged(SelectionChangedEvent event) {
			
			ISelection sel = event.getSelection();
			
			// Liste der AbstractDevices wird geleert bevor sie wieder mit den
			// selektierten Einträgen gefüllt wird
			tableDevices.clear();

			Iterator<Object> obj = ((IStructuredSelection)sel).toList().iterator();

			while (obj.hasNext()) {
				Object o = obj.next();
				if (o instanceof AbstractDevice) {
					tableDevices.add((AbstractDevice)o);
				}
				else {
					// Selektion gehört zu einer Klasse und wird bisher nicht beachtet
					// TODO: später sollen die Klassen auch beachtet werden
				}
			}
			optionsTable.setInput(tableDevices);
		}
	}	
	
}