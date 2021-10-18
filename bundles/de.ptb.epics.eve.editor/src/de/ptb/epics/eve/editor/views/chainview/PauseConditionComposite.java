package de.ptb.epics.eve.editor.views.chainview;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;

import de.ptb.epics.eve.data.ComparisonTypes;
import de.ptb.epics.eve.data.scandescription.Chain;
import de.ptb.epics.eve.data.scandescription.PauseCondition;
import de.ptb.epics.eve.editor.StringLabels;
import de.ptb.epics.eve.editor.handler.pauseconditions.RemovePauseConditionsDefaultHandler;
import de.ptb.epics.eve.editor.views.DelColumnEditingSupport;
import de.ptb.epics.eve.editor.views.IEditorViewCompositeMemento;
import de.ptb.epics.eve.editor.views.ViewerTableDragSourceListener;
import de.ptb.epics.eve.editor.views.ViewerTableDropTargetListener;
import de.ptb.epics.eve.util.ui.jface.viewercomparator.alphabetical.AlphabeticalTableViewerSorter;
import de.ptb.epics.eve.util.ui.jface.viewercomparator.alphabetical.SortOrder;

/**
 * @author Marcus Michalsky
 * @since 1.36
 */
public class PauseConditionComposite extends Composite implements 
		IEditorViewCompositeMemento, PropertyChangeListener {
	private static final Logger LOGGER = Logger.getLogger(
			PauseConditionComposite.class.getName());
	private static final Image DELETE_IMG = PlatformUI.getWorkbench().
			getSharedImages().getImage(ISharedImages.IMG_TOOL_DELETE);
	
	private static final String MEMENTO_PAUSE_CONDITIONS_SORT_ORDER = 
			"pauseConditionsSortState";
	
	private IViewPart parentView;
	private TableViewer tableViewer;
	private AlphabeticalTableViewerSorter tableSorter;
	
	private Chain currentChain;
	
	public PauseConditionComposite(Composite parent, int style, 
			final IViewPart parentView) {
		super(parent, style);
		
		this.setLayout(new FillLayout());
		
		this.parentView = parentView;

		this.createViewer();
	}
	
	private void createViewer() {
		tableViewer = new TableViewer(this, 
				SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
		this.createColumns(tableViewer);
		tableViewer.getTable().setHeaderVisible(true);
		tableViewer.getTable().setLinesVisible(true);
		tableViewer.setContentProvider(ArrayContentProvider.getInstance());
		tableViewer.setInput(null);
		
		tableViewer.getTable().addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				((ChainView)parentView).setSelectionProvider(tableViewer);
			}
		});
		ColumnViewerToolTipSupport.enableFor(tableViewer);
		
		this.tableSorter = new AlphabeticalTableViewerSorter(tableViewer, 
				tableViewer.getTable().getColumn(1), 
				new PauseConditionTableComparator());
		
		this.initDragAndDrop();
		
		MenuManager menuManager = new MenuManager();
		menuManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		menuManager.setRemoveAllWhenShown(true);
		this.tableViewer.getTable().setMenu(
				menuManager.createContextMenu(this.tableViewer.getTable()));
		parentView.getSite().registerContextMenu(
			"de.ptb.epics.eve.editor.views.chainview.pauseconditioncomposite.tablepopup",
			menuManager, this.tableViewer);
	}
	
	private void createColumns(TableViewer viewer) {
		TableViewerColumn delColumn = new TableViewerColumn(viewer, SWT.CENTER);
		delColumn.getColumn().setText("");
		delColumn.getColumn().setWidth(22);
		delColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public Image getImage(Object element) {
				return PauseConditionComposite.DELETE_IMG;
			}
			@Override
			public String getText(Object element) {
				return "";
			}
		});
		delColumn.setEditingSupport(new DelColumnEditingSupport(tableViewer, 
				RemovePauseConditionsDefaultHandler.ID));
		
		TableViewerColumn nameColumn = new TableViewerColumn(viewer, SWT.LEFT);
		nameColumn.getColumn().setText("Name");
		nameColumn.getColumn().setWidth(280);
		nameColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return element.toString();
			}
		});
		
		TableViewerColumn operatorColumn = new TableViewerColumn(viewer, SWT.LEFT);
		operatorColumn.getColumn().setText("Operator");
		operatorColumn.getColumn().setWidth(80);
		operatorColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((PauseCondition)element).getOperator().toString();
			}
		});
		operatorColumn.setEditingSupport(new OperatorEditingSupport(viewer));
		
		TableViewerColumn pauseColumn = new TableViewerColumn(viewer, SWT.LEFT);
		pauseColumn.getColumn().setText("Pause Limit");
		pauseColumn.getColumn().setWidth(100);
		pauseColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((PauseCondition)element).getPauseLimit();
			}
		});
		pauseColumn.setEditingSupport(new PauseLimitEditingSupport(viewer));
		
		TableViewerColumn continueColumn = new TableViewerColumn(viewer, SWT.LEFT);
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
			@Override
			public String getToolTipText(Object element) {
				PauseCondition pauseCondition = (PauseCondition)element;
				if (!(pauseCondition.hasContinueLimit())) {
					if (pauseCondition.isDiscrete()) {
						return "continue limit not available (device is discrete)";
					}
					if (pauseCondition.getOperator().equals(ComparisonTypes.EQ) ||
							pauseCondition.getOperator().equals(ComparisonTypes.NE)) {
						return "continue limit not available for operator EQ/NE";
					}
				}
				return null;
			}
		});
		continueColumn.setEditingSupport(new ContinueLimitEditingSupport(viewer));
	}
	
	public void setChain(Chain chain) {
		if (this.currentChain != null) {
			this.currentChain.removePropertyChangeListener(
					Chain.PAUSE_CONDITION_PROP, this);
		}
		this.currentChain = chain;
		if (this.currentChain != null) {
			this.tableViewer.setInput(chain.getPauseConditions());
			this.currentChain.addPropertyChangeListener(
					Chain.PAUSE_CONDITION_PROP, this);
		} else {
			this.tableViewer.setInput(null);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void propertyChange(PropertyChangeEvent e) {
		if (e.getPropertyName().equals(Chain.PAUSE_CONDITION_PROP)) {
			this.tableViewer.refresh();
		}
	}

	private void initDragAndDrop() {
		Transfer[] pauseConditionTransfers = 
				new Transfer[] {TextTransfer.getInstance()};
		this.tableViewer.addDragSupport(DND.DROP_MOVE, pauseConditionTransfers, 
				new ViewerTableDragSourceListener(tableViewer) {

			@Override
			public Logger getLogger() {
				return LOGGER;
			}

			@Override
			public String getModelName(TableItem item) {
				if (item.getData() instanceof PauseCondition) {
					PauseCondition pauseCondition = 
						(PauseCondition)item.getData();
					return pauseCondition.toString();
				}
				return "";
			}

			@Override
			public String getModelId(TableItem item) {
				if (item.getData() instanceof PauseCondition) {
					PauseCondition pauseCondition = 
							(PauseCondition)item.getData();
					return pauseCondition.getDevice().getID();
				}
				return "";
			}
		});
		
		this.tableViewer.addDropSupport(DND.DROP_MOVE, pauseConditionTransfers, 
				new ViewerTableDropTargetListener(tableViewer) {
			
			@Override
			public String getModelName(Object item) {
				if (item instanceof PauseCondition) {
					PauseCondition pauseCondition = (PauseCondition)item;
					return pauseCondition.toString();
				}
				return "";
			}
			
			@Override
			public List<? extends Object> getModel() {
				return currentChain.getPauseConditions();
			}
			
			@Override
			public Logger getLogger() {
				return LOGGER;
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void saveState(IMemento memento) {
		// table sort state
		memento.putInteger(MEMENTO_PAUSE_CONDITIONS_SORT_ORDER,
				tableSorter.getSortOrder().ordinal());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void restoreState(IMemento memento) {
		// restore table sort state
		if (memento.getInteger(MEMENTO_PAUSE_CONDITIONS_SORT_ORDER) != null) {
			this.tableSorter.setSortOrder(SortOrder.values()
					[memento.getInteger(MEMENTO_PAUSE_CONDITIONS_SORT_ORDER)]);
		}
	}
}
