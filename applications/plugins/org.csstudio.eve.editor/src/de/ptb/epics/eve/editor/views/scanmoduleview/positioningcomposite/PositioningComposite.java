package de.ptb.epics.eve.editor.views.scanmoduleview.positioningcomposite;

import java.util.List;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchActionConstants;

import de.ptb.epics.eve.data.scandescription.AbstractBehavior;
import de.ptb.epics.eve.editor.views.DelColumnEditingSupport;
import de.ptb.epics.eve.editor.views.scanmoduleview.ScanModuleView;
import de.ptb.epics.eve.editor.views.scanmoduleview.ActionComposite;

/**
 * <code>PositioningComposite</code> is part of the 
 * {@link de.ptb.epics.eve.editor.views.scanmoduleview.ScanModuleView}.
 * 
 * @author ?
 * @author Marcus Michalsky
 */
public class PositioningComposite extends ActionComposite {

	/**
	 * Constructs a <code>PositioningComposite</code>.
	 * 
	 * @param parentView the parent view
	 * @param parent the parent
	 * @param style the style
	 */
	public PositioningComposite(final ScanModuleView parentView, 
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
		this.tableViewer.getTable().addFocusListener(
				new TableViewerFocusListener());
		
		// create context menu
		MenuManager menuManager = new MenuManager();
		menuManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		menuManager.setRemoveAllWhenShown(true);
		this.tableViewer.getTable().setMenu(
				menuManager.createContextMenu(this.tableViewer.getTable()));
		// register menu
		parentView.getSite().registerContextMenu(
			"de.ptb.epics.eve.editor.views.scanmoduleview.positioningcomposite.popup", 
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
		delColumn.setEditingSupport(new DelColumnEditingSupport(
				this.tableViewer,
				"de.ptb.epics.eve.editor.command.removepositioning"));
		
		TableViewerColumn axisColumn = new TableViewerColumn(
				this.tableViewer, SWT.LEFT);
		axisColumn.getColumn().setText("Motor Axis");
		axisColumn.getColumn().setWidth(140);
		
		TableViewerColumn pluginColumn = new TableViewerColumn(
				this.tableViewer, SWT.LEFT);
		pluginColumn.getColumn().setText("Plugin");
		pluginColumn.getColumn().setWidth(60);
		pluginColumn.setEditingSupport(
				new PluginEditingSupport(this.tableViewer));
		
		TableViewerColumn channelColumn = new TableViewerColumn(
				this.tableViewer, SWT.LEFT);
		channelColumn.getColumn().setText("Detector Channel");
		channelColumn.getColumn().setWidth(140);
		channelColumn.setEditingSupport(
				new ChannelEditingSupport(this.tableViewer));
		
		TableViewerColumn normColumn = new TableViewerColumn(
				this.tableViewer, SWT.LEFT);
		normColumn.getColumn().setText("Normalize Channel");
		normColumn.getColumn().setWidth(140);
		normColumn.setEditingSupport(
				new NormalizeEditingSupport(this.tableViewer));
		
		TableViewerColumn paramColumn = new TableViewerColumn(
				this.tableViewer, SWT.LEFT);
		paramColumn.getColumn().setText("Parameters");
		paramColumn.getColumn().setWidth(100);
		paramColumn.setEditingSupport(
				new ParamEditingSupport(this.tableViewer));
		
		TableViewerColumn emptyColumn = new TableViewerColumn(
				this.tableViewer, SWT.LEFT);
		emptyColumn.getColumn().setText("");
		emptyColumn.getColumn().setWidth(1);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<? extends AbstractBehavior> getModel() {
		// TODO Auto-generated method stub
		return null;
	}
}