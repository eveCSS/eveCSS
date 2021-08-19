package de.ptb.epics.eve.viewer.views.engineview.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;

import de.ptb.epics.eve.data.scandescription.PauseCondition;
import de.ptb.epics.eve.ecp1.commands.PauseStatusEntry;
import de.ptb.epics.eve.editor.StringLabels;
import de.ptb.epics.eve.viewer.Activator;

/**
 * @author Marcus Michalsky
 * @since 1.36
 */
public class InhibitStateTooltip extends ToolTip {
	private static final String TIMESTAMP_PREFIX = "Message from: ";
	
	private Image redImage;
	private Image yellowImage;
	private Image greenImage;
	
	private List<PauseStatusEntry> pauseStates;
	
	public InhibitStateTooltip(Control control) {
		super(control, ToolTip.RECREATE, true); // TODO true no effect ? if activate ? recreate necessary ?
		
		this.redImage = Activator.getDefault().getImageRegistry().get("FATAL");
		this.yellowImage = Activator.getDefault().getImageRegistry().get("WARNING2");
		this.greenImage = Activator.getDefault().getImageRegistry().get("RUN");
		
		this.pauseStates = new ArrayList<>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Composite createToolTipContentArea(Event event, Composite parent) {
		Composite top = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout(1, false);
		// gridLayout.marginWidth =
		top.setLayout(gridLayout);
		
		Label timestamp = new Label(top, SWT.NONE);
		timestamp.setText(TIMESTAMP_PREFIX + " ---");
		
		this.createTable(top);

		return null;
	}
	
	private void createTable(Composite parent) {
		TableViewer viewer = new TableViewer(parent, SWT.BORDER);
		viewer.getTable().setHeaderVisible(true);
		viewer.getTable().setLinesVisible(true);
		
		this.createColumns(viewer);
		
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalSpan = GridData.FILL;
		gridData.verticalSpan = GridData.FILL;
		viewer.getTable().setLayoutData(gridData);
		
		viewer.setContentProvider(ArrayContentProvider.getInstance());
	}
	
	private void createColumns(TableViewer viewer) {
		TableViewerColumn stateColumn = new TableViewerColumn(viewer, SWT.NONE);
		stateColumn.getColumn().setText("State");
		stateColumn.getColumn().setWidth(50);
		stateColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public Image getImage(Object element) {
				PauseCondition pauseCondition = (PauseCondition)element;
				for (PauseStatusEntry entry : pauseStates) {
					if (entry.getId() == pauseCondition.getId()) {
						switch (entry.getPauseStatus()) {
						case PAUSE_ACTIVE_NOT_OVERRIDDEN:
							return redImage;
						case PAUSE_ACTIVE_OVERRIDDEN:
							return yellowImage;
						case PAUSE_INACTIVE_NOT_OVERRIDDEN:
							return greenImage;
						default:
							super.getImage(element);
						}
					}
				}
				return super.getImage(element);
			}
			
			@Override
			public String getText(Object element) {
				return null;
			}
		});
		
		TableViewerColumn nameColumn = new TableViewerColumn(viewer, SWT.NONE);
		nameColumn.getColumn().setText("Name");
		nameColumn.getColumn().setWidth(280);
		nameColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((PauseCondition)element).toString();
			};
		});
		
		TableViewerColumn operatorColumn = new TableViewerColumn(viewer, SWT.NONE);
		operatorColumn.getColumn().setText("Operator");
		operatorColumn.getColumn().setWidth(80);
		operatorColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				switch (((PauseCondition)element).getOperator()) {
				case EQ:
					return StringLabels.EQUAL;
				case GT:
					return StringLabels.GREATER_THAN;
				case LT:
					return StringLabels.LESS_THAN;
				case NE:
					return StringLabels.NOT_EQUAL;
				default:
					return StringLabels.LONG_DASH;
				}
			}
		});
		
		TableViewerColumn pauseColumn = new TableViewerColumn(viewer, SWT.NONE);
		pauseColumn.getColumn().setText("Pause Limit");
		pauseColumn.getColumn().setWidth(100);
		pauseColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((PauseCondition)element).getPauseLimit();
			}
		});
		
		TableViewerColumn continueColumn = new TableViewerColumn(viewer, SWT.NONE);
		continueColumn.getColumn().setText("Continue Limit");
		continueColumn.getColumn().setWidth(100);
		continueColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				PauseCondition pauseCondition = (PauseCondition)element;
				if (pauseCondition.hasContinueLimit() && 
						pauseCondition.getContinueLimit() != null) {
					return pauseCondition.getContinueLimit();
				} else {
					return StringLabels.LONG_DASH;
				}
			}
		});
	}
}
