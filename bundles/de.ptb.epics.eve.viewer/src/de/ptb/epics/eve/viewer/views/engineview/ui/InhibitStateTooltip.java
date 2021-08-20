package de.ptb.epics.eve.viewer.views.engineview.ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
import de.ptb.epics.eve.ecp1.commands.PauseStatusCommand;
import de.ptb.epics.eve.ecp1.commands.PauseStatusEntry;
import de.ptb.epics.eve.editor.StringLabels;
import de.ptb.epics.eve.viewer.Activator;

/**
 * The Tooltip shows a timestamp label and a table of pause conditions.
 * Initialize with the widget of interest (tooltip shown when hovering it).
 * Set the content of the table with {@link #setPauseConditions(List)} and the 
 * states with {@link #setPauseStatus(PauseStatusCommand)}.
 * 
 * @author Marcus Michalsky
 * @since 1.36
 */
public class InhibitStateTooltip extends ToolTip {
	private static final String TIMESTAMP_PREFIX = "Message from: ";
	
	private Image redImage;
	private Image yellowImage;
	private Image greenImage;
	
	private List<PauseCondition> pauseConditions;
	private List<PauseStatusEntry> pauseStates;
	private String timeStampLabelString = StringLabels.LONG_DASH;
	
	private TableViewer viewer;
	
	public InhibitStateTooltip(Control control) {
		super(control, ToolTip.RECREATE, true);
		
		this.redImage = Activator.getDefault().getImageRegistry().get("ERROR");
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
		timestamp.setText(this.timeStampLabelString);
		
		this.createTable(top);

		return null;
	}
	
	private void createTable(Composite parent) {
		viewer = new TableViewer(parent, SWT.BORDER);
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
		viewer.setInput(this.pauseConditions);
	}
	
	private void createColumns(TableViewer viewer) {
		TableViewerColumn stateColumn = new TableViewerColumn(viewer, SWT.CENTER);
		stateColumn.getColumn().setText("");
		stateColumn.getColumn().setWidth(22);
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
			}
		});
		
		TableViewerColumn operatorColumn = new TableViewerColumn(viewer, SWT.CENTER);
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
	
	/**
	 * Sets the pause conditions which should be shown in the table.
	 * @param pauseConditions the pause conditions which should be shown in the 
	 * 		table or <code>null</code> to show an empty table
	 */
	public void setPauseConditions(List<PauseCondition> pauseConditions) {
		this.pauseConditions = pauseConditions;
	}
	
	/**
	 * Sets the pause status command containing the timestamp and the states of 
	 *    the defined pause conditions.
	 * @param pauseStatus the pause status command containing the timestamp and 
	 * 		the states of the defined pause conditions
	 */
	public void setPauseStatus(PauseStatusCommand pauseStatus) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(pauseStatus.getTimeStampSeconds() * 1000l);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		this.timeStampLabelString = TIMESTAMP_PREFIX + 
				formatter.format(calendar.getTime());
		this.pauseStates = pauseStatus.getPauseStatusList();
	}
}
