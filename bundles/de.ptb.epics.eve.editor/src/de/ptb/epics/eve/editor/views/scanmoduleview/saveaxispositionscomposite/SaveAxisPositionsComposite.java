package de.ptb.epics.eve.editor.views.scanmoduleview.saveaxispositionscomposite;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;

import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.data.scandescription.ScanModuleTypes;
import de.ptb.epics.eve.editor.views.DelColumnEditingSupport;
import de.ptb.epics.eve.editor.views.scanmoduleview.ScanModuleView;
import de.ptb.epics.eve.editor.views.scanmoduleview.ScanModuleViewComposite;
import de.ptb.epics.eve.editor.views.scanmoduleview.classiccomposite.motoraxiscomposite.ContentProvider;

/**
 * @author Marcus Michalsky
 * @since 1.31
 */
public class SaveAxisPositionsComposite extends ScanModuleViewComposite {
	private TableViewer tableViewer;
	
	public SaveAxisPositionsComposite(ScanModuleView parentView, Composite parent, 
			int style) {
		super(parentView, parent, style);

		this.setLayout(new GridLayout());
		Label label = new Label(this, SWT.NONE);
		label.setText("Snapshot Axes:");
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		label.setLayoutData(gridData);
		
		tableViewer = new TableViewer(this, SWT.FULL_SELECTION | SWT.BORDER);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		tableViewer.getTable().setLayoutData(gridData);
		tableViewer.getTable().setHeaderVisible(true);
		tableViewer.getTable().setLinesVisible(true);
		
		TableViewerColumn delColumn = new TableViewerColumn(tableViewer, 
				SWT.NONE);
		delColumn.getColumn().setText("");
		delColumn.getColumn().setWidth(22);
		delColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public Image getImage(Object element) {
				return PlatformUI.getWorkbench().getSharedImages().getImage(
						ISharedImages.IMG_TOOL_DELETE);
			}
		});
		delColumn.setEditingSupport(new DelColumnEditingSupport(
				this.tableViewer,
				"de.ptb.epics.eve.editor.command.removeaxis"));
		
		TableViewerColumn nameColumn = new TableViewerColumn(tableViewer, 
				SWT.NONE);
		nameColumn.getColumn().setText("Name");
		nameColumn.getColumn().setWidth(200);
		nameColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((Axis)element).getMotorAxis().getName();
			}
		});
		// a little "hack": using the same content provider as the classic axis table
		this.tableViewer.setContentProvider(new ContentProvider());
		this.tableViewer.getTable().addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				getParentView().setSelectionProvider(tableViewer);
				super.focusGained(e);
			}
		});
		this.tableViewer.setSorter(new ViewerSorter() {
			@Override
			public int compare(Viewer viewer, Object e1, Object e2) {
				Axis m1 = (Axis) e1;
				Axis m2 = (Axis) e2;
				return m1.getMotorAxis().getName().toLowerCase().compareTo(
						m2.getMotorAxis().getName().toLowerCase());
			}
		});
		
		// create context menu
		MenuManager menuManager = new MenuManager();
		menuManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		menuManager.setRemoveAllWhenShown(true);
		this.tableViewer.getTable().setMenu(
				menuManager.createContextMenu(this.tableViewer.getTable()));
		// register menu
		parentView.getSite().registerContextMenu(
			"de.ptb.epics.eve.editor.views.scanmoduleview.saveaxispositionscomposite.popup", 
			menuManager, this.tableViewer);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected ScanModuleTypes getType() {
		return ScanModuleTypes.SAVE_AXIS_POSITIONS;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void setScanModule(ScanModule scanModule) {
		if (scanModule == null) {
			this.tableViewer.setInput(null);
			return;
		}
		this.tableViewer.setInput(scanModule);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void saveState(IMemento memento) {
		// nothing to save for now
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void restoreState(IMemento memento) {
		// nothing to restore for now
	}
}
