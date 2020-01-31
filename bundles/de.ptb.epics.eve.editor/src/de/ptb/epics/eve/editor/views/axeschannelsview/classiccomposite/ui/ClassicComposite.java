package de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.ui;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IMemento;

import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.data.scandescription.ScanModuleTypes;
import de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes.AxesContentProvider;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes.AxesLabelProvider;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes.MainAxisEditingSupport;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes.PositionModeEditingSupport;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes.StepfunctionEditingSupport;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes.ValuesEditingSupport;
import de.ptb.epics.eve.editor.views.axeschannelsview.ui.AxesChannelsView;
import de.ptb.epics.eve.editor.views.axeschannelsview.ui.AxesChannelsViewComposite;

/**
 * @author Marcus Michalsky
 * @since 1.34
 */
public class ClassicComposite extends AxesChannelsViewComposite {
	private static final int TABLE_MIN_HEIGHT = 150;
	
	private ScanModule scanModule;
	private TableViewer axesTable;
	private TableViewer channelsTable;
	
	public ClassicComposite(AxesChannelsView parentView, Composite parent, int style) {
		super(parentView, parent, style);
		
		this.setLayout(new FillLayout());
		
		ScrolledComposite sc = new ScrolledComposite(this, SWT.V_SCROLL);
		sc.setExpandHorizontal(true);
		sc.setExpandVertical(true);
		
		SashForm sashForm = new SashForm(sc, SWT.VERTICAL);
		sashForm.setLayout(new FillLayout());
		sc.setContent(sashForm);
		
		Composite axesComposite = new Composite(sashForm, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		axesComposite.setLayout(gridLayout);
		Label axesLabel = new Label(axesComposite, SWT.NONE);
		axesLabel.setText("Motor Axes:");
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = SWT.LEFT;
		axesLabel.setLayoutData(gridData);
		createAxesTable(axesComposite);
		
		Composite channelsComposite = new Composite(sashForm, SWT.NONE);
		gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		channelsComposite.setLayout(gridLayout);
		Label channelsLabel = new Label(channelsComposite, SWT.NONE);
		channelsLabel.setText("Detector Channels:");
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = SWT.LEFT;
		channelsLabel.setLayoutData(gridData);
		createChannelsTable(channelsComposite);
		
		sashForm.setWeights(new int[] {50, 50});
		
		sc.setMinSize(sashForm.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}
	
	private void createAxesTable(Composite parent) {
		this.axesTable = new TableViewer(parent, SWT.BORDER);
		axesTable.getTable().setHeaderVisible(true);
		axesTable.getTable().setLinesVisible(true);
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.minimumHeight = TABLE_MIN_HEIGHT;
		axesTable.getTable().setLayoutData(gridData);
		
		createAxesTableColumns(axesTable);
		
		axesTable.setContentProvider(new AxesContentProvider());
		axesTable.setLabelProvider(new AxesLabelProvider());
	}
	
	private void createAxesTableColumns(TableViewer viewer) {
		TableViewerColumn delColumn = new TableViewerColumn(viewer, SWT.NONE);
		delColumn.getColumn().setText("");
		delColumn.getColumn().setWidth(22);
		// TODO set EditingSupport command: de.ptb.epics.eve.editor.command.removeaxis
		
		TableViewerColumn nameColumn = new TableViewerColumn(viewer, SWT.NONE);
		nameColumn.getColumn().setText("Name");
		nameColumn.getColumn().setWidth(220);
		// TODO add selection listener for sorting
		
		TableViewerColumn stepfunctionColumn = new TableViewerColumn(viewer, SWT.NONE);
		stepfunctionColumn.getColumn().setText("Stepfunction");
		stepfunctionColumn.getColumn().setWidth(110);
		stepfunctionColumn.setEditingSupport(new StepfunctionEditingSupport(viewer));
		
		TableViewerColumn mainAxisColumn = new TableViewerColumn(viewer, SWT.CENTER);
		mainAxisColumn.getColumn().setText("Main");
		mainAxisColumn.getColumn().setToolTipText("Main Axis");
		mainAxisColumn.getColumn().setWidth(50);
		mainAxisColumn.setEditingSupport(new MainAxisEditingSupport(viewer));
		
		TableViewerColumn positionModeColumn = new TableViewerColumn(viewer, SWT.NONE);
		positionModeColumn.getColumn().setText("Mode");
		positionModeColumn.getColumn().setWidth(50);
		positionModeColumn.setEditingSupport(new PositionModeEditingSupport(viewer));
		
		TableViewerColumn valuesColumn = new TableViewerColumn(viewer, SWT.NONE);
		valuesColumn.getColumn().setText("Values");
		valuesColumn.getColumn().setWidth(250);
		valuesColumn.setEditingSupport(new ValuesEditingSupport(viewer));
		
		TableViewerColumn posCntColumn = new TableViewerColumn(viewer, SWT.NONE);
		posCntColumn.getColumn().setText("# points");
		posCntColumn.getColumn().setWidth(80);
	}
	
	private void createChannelsTable(Composite parent) {
		this.channelsTable = new TableViewer(parent, SWT.BORDER);
		channelsTable.getTable().setHeaderVisible(true);
		channelsTable.getTable().setLinesVisible(true);
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.minimumHeight = TABLE_MIN_HEIGHT;
		channelsTable.getTable().setLayoutData(gridData);
		
		createChannelsTableColumns(channelsTable);
	}
	
	private void createChannelsTableColumns(TableViewer viewer) {
		TableViewerColumn delColumn = new TableViewerColumn(viewer, SWT.NONE);
		delColumn.getColumn().setText("");
		delColumn.getColumn().setWidth(22);
		// TODO set EditingSupport command: de.ptb.epics.eve.editor.command.removechannel ?
		
		TableViewerColumn nameColumn = new TableViewerColumn(viewer, SWT.NONE);
		nameColumn.getColumn().setText("Name");
		nameColumn.getColumn().setWidth(220);
		// TODO add selection listener for sorting
		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected ScanModuleTypes getType() {
		return ScanModuleTypes.CLASSIC;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void setScanModule(ScanModule scanModule) {
		if (this.scanModule != null) {
			this.scanModule.removeModelUpdateListener(this);
		}
		this.scanModule = scanModule;
		this.axesTable.setInput(scanModule);
		// this.channelsTable.setInput(scanModule);
		if (this.scanModule != null) {
			this.scanModule.addModelUpdateListener(this);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateEvent(ModelUpdateEvent modelUpdateEvent) {
		this.axesTable.refresh();
		this.channelsTable.refresh();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void saveState(IMemento memento) {
		// TODO Auto-generated method stub
		// sash weights, table sort states, ... ?
		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void restoreState(IMemento memento) {
		// TODO Auto-generated method stub
		
	}
}
