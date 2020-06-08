package de.ptb.epics.eve.editor.views.prepostposplotview.classiccomposite.ui;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchActionConstants;

import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.data.scandescription.ScanModuleTypes;
import de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent;
import de.ptb.epics.eve.editor.views.AbstractScanModuleViewComposite;
import de.ptb.epics.eve.editor.views.prepostposplotview.ui.PrePostPosPlotView;

/**
 * @author Marcus Michalsky
 * @since 1.34
 */
public class ClassicComposite extends AbstractScanModuleViewComposite {
	private static final int TABLE_MIN_HEIGHT = 150;
	
	private IViewPart parentView;
	
	private ScanModule scanModule;

	private TableViewer prePostscanTable;
	
	public ClassicComposite(IViewPart parentView, Composite parent, int style) {
		super(parentView, parent, style);
		GridLayout gridLayout = new GridLayout();
		// TODO set Border to zero ?
		this.setLayout(gridLayout);
		this.parentView = parentView;
		
		this.createTable(this);
		
		MenuManager menuManager = new MenuManager();
		menuManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		menuManager.setRemoveAllWhenShown(true);
		this.prePostscanTable.getTable().setMenu(
				menuManager.createContextMenu(this.prePostscanTable.getTable()));
		this.parentView.getSite().registerContextMenu(
			"de.ptb.epics.eve.editor.views.prepostposplotview.classiccomposite.popup", 
			menuManager, this.prePostscanTable);
		
		this.parentView.getSite().getWorkbenchWindow().getSelectionService().
		addSelectionListener(this);
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PrePostPosPlotView getParentView() {
		return this.getParentView();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ScanModuleTypes getType() {
		return ScanModuleTypes.CLASSIC;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setScanModule(ScanModule scanModule) {
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
	public void updateEvent(ModelUpdateEvent modelUpdateEvent) {
		this.prePostscanTable.refresh();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void saveState(IMemento memento) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void restoreState(IMemento memento) {
		// TODO Auto-generated method stub
		
	}
}
