package de.ptb.epics.eve.editor.views.chainview;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;

import de.ptb.epics.eve.data.scandescription.Chain;
import de.ptb.epics.eve.data.scandescription.PauseCondition;
import de.ptb.epics.eve.editor.StringLabels;
import de.ptb.epics.eve.editor.handler.pauseconditions.RemovePauseConditionsDefaultHandler;
import de.ptb.epics.eve.editor.views.DelColumnEditingSupport;

/**
 * @author Marcus Michalsky
 * @since 1.36
 */
public class PauseConditionComposite extends Composite implements PropertyChangeListener {
	private static final Image DELETE_IMG = PlatformUI.getWorkbench().
			getSharedImages().getImage(ISharedImages.IMG_TOOL_DELETE);
	
	private IViewPart parentView;
	private TableViewer tableViewer;
	
	private Chain currentChain;
	
	public PauseConditionComposite(Composite parent, int style, 
			final IViewPart parentView) {
		super(parent, style);
		this.parentView = parentView;
		
		this.setLayout(new FillLayout());
		
		this.createViewer();
	}
	
	private void createViewer() {
		tableViewer = new TableViewer(this, SWT.MULTI);
		tableViewer.getTable().setHeaderVisible(true);
		tableViewer.getTable().setLinesVisible(true);
		tableViewer.setContentProvider(ArrayContentProvider.getInstance());
		
		this.createColumns(tableViewer);
		
		tableViewer.getTable().addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				((ChainView)parentView).setSelectionProvider(tableViewer);
			}
		});
		
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
		
		TableViewerColumn pauseColumn = new TableViewerColumn(viewer, SWT.LEFT);
		pauseColumn.getColumn().setText("Pause Limit");
		pauseColumn.getColumn().setWidth(100);
		pauseColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((PauseCondition)element).getPauseLimit();
			}
		});
		
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
		});
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
}
