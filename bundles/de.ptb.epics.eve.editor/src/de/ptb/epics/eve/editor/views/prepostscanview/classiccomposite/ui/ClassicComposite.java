package de.ptb.epics.eve.editor.views.prepostscanview.classiccomposite.ui;

import org.eclipse.gef.EditPart;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;

import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.data.scandescription.ScanModuleTypes;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener;
import de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent;
import de.ptb.epics.eve.editor.gef.editparts.ChainEditPart;
import de.ptb.epics.eve.editor.gef.editparts.ScanDescriptionEditPart;
import de.ptb.epics.eve.editor.gef.editparts.ScanModuleEditPart;

/**
 * @author Marcus Michalsky
 * @since 1.34
 */
public class ClassicComposite extends Composite implements ISelectionListener, 
		IModelUpdateListener {
	private static final int TABLE_MIN_HEIGHT = 150;
	
	private IViewPart parentView;
	
	private ScanModule scanModule;

	private TableViewer prePostscanTable;
	
	public ClassicComposite(IViewPart parentView, Composite parent, int style) {
		super(parent, style);
		this.setLayout(new FillLayout());
		this.parentView = parentView;
		this.parentView.getSite().getWorkbenchWindow().getSelectionService().
			addSelectionListener(this);
		
		
		this.createTable(parent);
		// TODO Auto-generated constructor stub
	}
	
	private void createTable(Composite parent) {
		prePostscanTable = new TableViewer(parent, SWT.BORDER);
		prePostscanTable.getTable().setHeaderVisible(true);
		prePostscanTable.getTable().setLinesVisible(true);
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.minimumHeight = TABLE_MIN_HEIGHT;
		prePostscanTable.getTable().setLayoutData(gridData);
		this.createColumns(prePostscanTable);
	}
	
	private void createColumns(TableViewer viewer) {
		TableViewerColumn deleteColumn = new TableViewerColumn(viewer, SWT.NONE);
		deleteColumn.getColumn().setWidth(22);
		
		TableViewerColumn prescanColumn = new TableViewerColumn(viewer, SWT.NONE);
		prescanColumn.getColumn().setWidth(80);
		prescanColumn.getColumn().setText("Prescan");
		
		TableViewerColumn nameColumn = new TableViewerColumn(viewer, SWT.NONE);
		nameColumn.getColumn().setWidth(400);
		nameColumn.getColumn().setText("Name");
		
		TableViewerColumn postscanColumn = new TableViewerColumn(viewer, SWT.NONE);
		postscanColumn.getColumn().setWidth(80);
		postscanColumn.getColumn().setText("Postscan");
		
		TableViewerColumn resetColumn = new TableViewerColumn(viewer, SWT.NONE);
		resetColumn.getColumn().setWidth(100);
		resetColumn.getColumn().setText("Reset Original");
	}

	private void setScanModule(ScanModule scanModule) {
		if (this.scanModule != null) {
			this.scanModule.removeModelUpdateListener(this);
		}
		this.scanModule = scanModule;
		// TODO set input to table ? model ?
		if (this.scanModule != null) {
			this.scanModule.addModelUpdateListener(this);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if (!(selection instanceof IStructuredSelection)
				|| ((IStructuredSelection) selection).size() == 0
				|| ((IStructuredSelection) selection).size() > 1) {
			return;
		}
		Object o = ((IStructuredSelection)selection).getFirstElement();
		if (o instanceof ScanModuleEditPart) {
			ScanModule selectedScanModule = (ScanModule)((EditPart)o).getModel();
			if (selectedScanModule.getType().equals(ScanModuleTypes.CLASSIC)) {
				this.setScanModule(selectedScanModule);
			} else {
				setScanModule(null);
			}
		} else if (o instanceof ChainEditPart) {
			// clicking empty space in the editor
			setScanModule(null);
		} else if (o instanceof ScanDescriptionEditPart) {
			setScanModule(null);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateEvent(ModelUpdateEvent modelUpdateEvent) {
		this.prePostscanTable.refresh();
	}
}
