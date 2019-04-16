package de.ptb.epics.eve.editor.views.scanmoduleview.classiccomposite.plotcomposite;

import java.util.List;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;

import de.ptb.epics.eve.data.scandescription.AbstractBehavior;
import de.ptb.epics.eve.data.scandescription.PlotWindow;
import de.ptb.epics.eve.editor.views.DelColumnEditingSupport;
import de.ptb.epics.eve.editor.views.scanmoduleview.ScanModuleView;
import de.ptb.epics.eve.editor.views.scanmoduleview.classiccomposite.ActionComposite;

/**
 * <code>PlotComposite</code> is part of the 
 * {@link de.ptb.epics.eve.editor.views.scanmoduleview.ScanModuleView}.
 * 
 * @author Hartmut Scherr
 * @author Marcus Michalsky
 */
public class PlotComposite extends ActionComposite {
	private static final Image ERROR_IMG = PlatformUI.getWorkbench().
			getSharedImages().getImage(ISharedImages.IMG_OBJS_ERROR_TSK);
	
	/**
	 * Constructor.
	 * 
	 * @param parentView the parent view
	 * @param parent the parent
	 * @param style the style
	 */
	public PlotComposite(final ScanModuleView parentView, final Composite parent, 
						 final int style) {
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
		this.tableViewer.getTable().addFocusListener(
				new TableViewerFocusListener());
		
		// create context menu
		MenuManager menuManager = new MenuManager();
		menuManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		menuManager.setRemoveAllWhenShown(true);
		ColumnViewerToolTipSupport.enableFor(this.tableViewer);
		this.tableViewer.getTable().setMenu(
				menuManager.createContextMenu(this.tableViewer.getTable()));
		// register menu
		parentView.getSite().registerContextMenu(
			"de.ptb.epics.eve.editor.views.scanmoduleview.plotcomposite.popup", 
			menuManager, this.tableViewer);
	}
	
	/*
	 * 
	 */
	private void createColumns() {
		TableViewerColumn delColumn = new TableViewerColumn(
				this.tableViewer, SWT.CENTER);
		delColumn.getColumn().setText("");
		delColumn.getColumn().setWidth(22);
		delColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return null;
			}
			@Override
			public Image getImage(Object element) {
				return PlatformUI.getWorkbench().getSharedImages().getImage(
						ISharedImages.IMG_TOOL_DELETE);
			}
		});
		delColumn.setEditingSupport(new DelColumnEditingSupport(
				this.tableViewer,
				"de.ptb.epics.eve.editor.command.removeplotwindow"));
		
		TableViewerColumn idColumn = new TableViewerColumn(
				this.tableViewer, SWT.LEFT);
		idColumn.getColumn().setText("Id");
		idColumn.getColumn().setWidth(50);
		idColumn.setLabelProvider(new IdColumnLabelProvider());
		idColumn.setEditingSupport(new IdEditingSupport(this.tableViewer));
		
		TableViewerColumn nameColumn = new TableViewerColumn(
				this.tableViewer, SWT.LEFT);
		nameColumn.getColumn().setText("Name");
		nameColumn.getColumn().setWidth(120);
		nameColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((PlotWindow)element).getName();
			}
			@Override
			public String getToolTipText(Object element) {
				return this.getText(element);
			}
		});
		
		TableViewerColumn xColumn = new TableViewerColumn(
				this.tableViewer, SWT.LEFT);
		xColumn.getColumn().setText("x Axis");
		xColumn.getColumn().setWidth(100);
		xColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				PlotWindow pw = (PlotWindow)element;
				if (pw.getXAxis() != null && 
						pw.getXAxis().getName() != null) {
							return pw.getXAxis().getName();
				}
				return null;
			}
			@Override
			public Image getImage(Object element) {
				if (((PlotWindow)element).getYAxes().isEmpty()) {
					return PlotComposite.ERROR_IMG;
				}
				return null;
			}
		});
		
		TableViewerColumn y1Column = new TableViewerColumn(
				this.tableViewer, SWT.LEFT);
		y1Column.getColumn().setText("y Axis1");
		y1Column.getColumn().setWidth(170);
		y1Column.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				PlotWindow pw = (PlotWindow)element;
				if (pw.getYAxisAmount() == 0) {
					return null;
				}
				return pw.getYAxes().get(0).getNormalizedName();
			}
			@Override
			public Image getImage(Object element) {
				if (((PlotWindow)element).getYAxisAmount() == 0) {
					return PlotComposite.ERROR_IMG;
				}
				return null;
			}
			@Override
			public String getToolTipText(Object element) {
				return this.getText(element);
			}
		});
		
		TableViewerColumn y2Column = new TableViewerColumn(
				this.tableViewer, SWT.LEFT);
		y2Column.getColumn().setText("y Axis2");
		y2Column.getColumn().setWidth(170);
		y2Column.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				PlotWindow pw = (PlotWindow)element;
				if (pw.getYAxisAmount() < 2) {
					return null;
				}
				return pw.getYAxes().get(1).getNormalizedName();
			}
			@Override
			public Image getImage(Object element) {
				if (((PlotWindow)element).getYAxisAmount() == 0) {
					return PlotComposite.ERROR_IMG;
				}
				return null;
			}
			@Override
			public String getToolTipText(Object element) {
				return this.getText(element);
			}
		});
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<? extends AbstractBehavior> getModel() {
		return null;
	}
}