package de.ptb.epics.eve.editor.views.scanmoduleview.prescancomposite;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchActionConstants;

import de.ptb.epics.eve.editor.views.DelColumnEditingSupport;
import de.ptb.epics.eve.editor.views.scanmoduleview.ScanModuleView;
import de.ptb.epics.eve.editor.views.scanmoduleview.ActionComposite;

/**
 * <code>PrescanComposite</code> is part of the 
 * {@link de.ptb.epics.eve.editor.views.scanmoduleview.ScanModuleView}. 
 * 
 * @author ?
 * @author Marcus Michalsky
 * @author Hartmut Scherr
 */
public class PrescanComposite extends ActionComposite {

	/**
	 * Constructs a <code>PrescanComposite</code>.
	 * 
	 * @param parentView the parent view
	 * @param parent the parent composite
	 * @param style the style
	 */
	public PrescanComposite(final ScanModuleView parentView, 
							final Composite parent, final int style) {
		super(parentView, parent, style);
		this.setLayout(new GridLayout());
		createViewer();
	}

	/*
	 * 
	 */
	private void createViewer() {
		this.tableViewer = new TableViewer(this, SWT.NONE);
		GridData gridData = new GridData();
		gridData.minimumHeight = 50;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		this.tableViewer.getTable().setLayoutData(gridData);
		createColumns();
		this.tableViewer.getTable().setHeaderVisible(true);
		this.tableViewer.getTable().setLinesVisible(true);
		this.tableViewer.setContentProvider(new ContentProvider());
		this.tableViewer.setLabelProvider(new LabelProvider());
		this.tableViewer.getTable().addFocusListener(new 
				TableViewerFocusListener());
		
		// create context menu
		MenuManager menuManager = new MenuManager();
		menuManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		menuManager.setRemoveAllWhenShown(true);
		this.tableViewer.getTable().setMenu(
				menuManager.createContextMenu(this.tableViewer.getTable()));
		// register menu
		parentView.getSite().registerContextMenu(
			"de.ptb.epics.eve.editor.views.scanmoduleview.prescancomposite.popup", 
			menuManager, this.tableViewer);
	}
	
	/*
	 * 
	 */
	private void createColumns() {
		TableViewerColumn delColumn = new TableViewerColumn(
				this.tableViewer, SWT.LEFT);
		delColumn.getColumn().setText("");
		delColumn.getColumn().setWidth(22);
		delColumn.setEditingSupport(new DelColumnEditingSupport(
				this.tableViewer, 
				"de.ptb.epics.eve.editor.command.removeprescan"));
		
		TableViewerColumn deviceColumn = new TableViewerColumn(
				this.tableViewer, SWT.LEFT);
		deviceColumn.getColumn().setText("Device");
		deviceColumn.getColumn().setWidth(400);
		
		TableViewerColumn valueColumn = new TableViewerColumn(
				this.tableViewer, SWT.LEFT);
		valueColumn.getColumn().setText("Value");
		valueColumn.getColumn().setWidth(80);
		valueColumn.setEditingSupport(new ValueEditingSupport(this.tableViewer));
	}
}