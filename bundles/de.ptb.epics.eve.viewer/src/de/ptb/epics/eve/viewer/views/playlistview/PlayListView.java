package de.ptb.epics.eve.viewer.views.playlistview;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.services.IEvaluationService;

import de.ptb.epics.eve.ecp1.client.interfaces.IConnectionStateListener;
import de.ptb.epics.eve.ecp1.client.interfaces.IPlayListController;
import de.ptb.epics.eve.ecp1.client.interfaces.IPlayListListener;
import de.ptb.epics.eve.ecp1.client.model.PlayListEntry;
import de.ptb.epics.eve.viewer.Activator;
import de.ptb.epics.eve.viewer.MessageSource;
import de.ptb.epics.eve.viewer.actions.AddFileToPlayListAction;
import de.ptb.epics.eve.viewer.actions.MoveFileDownInPlayListAction;
import de.ptb.epics.eve.viewer.actions.MoveFileUpInPlayListAction;
import de.ptb.epics.eve.viewer.actions.RemoveFileFromPlayListAction;
import de.ptb.epics.eve.viewer.propertytester.EngineConnected;
import de.ptb.epics.eve.viewer.views.messagesview.Levels;
import de.ptb.epics.eve.viewer.views.messagesview.ViewerMessage;

/**
 * <code>PlayListView</code>.
 * 
 * @author ?
 * @author Marcus Michalsky
 */
public final class PlayListView extends ViewPart 
					implements IConnectionStateListener, IPlayListListener {

	/**
	 * the unique identifier of this view
	 */
	public static final String ID = "PlayListView";
	
	// the table viewer containing scan descriptions
	private TableViewer tableViewer;
	
	// actions to add, remove and move scan descriptions in the table
	private AddFileToPlayListAction addAction;
	private RemoveFileFromPlayListAction removeAction;
	private MoveFileUpInPlayListAction moveUpAction;
	private MoveFileDownInPlayListAction moveDownAction;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createPartControl(final Composite parent) {
		createViewer(parent);
		buildActions();
		
		MenuManager manager = new MenuManager();
		Menu menu = manager.createContextMenu(this.tableViewer.getControl());
		this.tableViewer.getControl().setMenu(menu);
		manager.add(this.moveUpAction);
		manager.add(this.moveDownAction);
		manager.add(this.removeAction);
		
		if(Activator.getDefault().getEcp1Client().isRunning()) {
			actionsEnabled(true);
			this.tableViewer.getTable().setEnabled(true);
		} else {
			actionsEnabled(false);
			this.tableViewer.getTable().setEnabled(false);
		}
		
		// event handling
		Activator.getDefault().getEcp1Client().addConnectionStateListener(this);
		Activator.getDefault().getEcp1Client().getPlayListController().
				addPlayListListener(this);
		
		// register the table viewer to the selection so that commands 
		// can decide whether they are active
		getSite().setSelectionProvider(tableViewer);
	}

	/*
	 * 
	 */
	private void createViewer(Composite parent) {
		this.tableViewer = new TableViewer(parent, SWT.MULTI);
		
		createColumns();
		
		this.tableViewer.getTable().setHeaderVisible(true);
		this.tableViewer.getTable().setLinesVisible(true);
		
		this.tableViewer.setContentProvider(new PlayListTableContentProvider());
		this.tableViewer.setLabelProvider(new PlayListTableLabelProvider());
	}

	/*
	 * 
	 */
	private void createColumns() {
		// file column
		TableViewerColumn fileColumn = new TableViewerColumn(
				this.tableViewer, SWT.LEFT);
		fileColumn.getColumn().setText("File");
		fileColumn.getColumn().setWidth(200);
		
		// author column
		TableViewerColumn authorColumn = new TableViewerColumn(
				this.tableViewer, SWT.LEFT);
		authorColumn.getColumn().setText("Author");
		authorColumn.getColumn().setWidth(60);
	}
	
	/*
	 * // TODO convert to commands
	 */
	private void buildActions() {
		// Add
		this.addAction = new AddFileToPlayListAction();
		this.addAction.setText("Add a file to the play list.");
		this.addAction.setImageDescriptor(new ImageDescriptor() {
			@Override public ImageData getImageData() {
				return Activator.getDefault().getImageRegistry().get("ADDFILE").getImageData();
			}
		});
		this.getViewSite().getActionBars().getToolBarManager().add(
				this.addAction);
		
		// Move Up
		this.moveUpAction = new MoveFileUpInPlayListAction(this);
		this.moveUpAction.setText("Move up");
		this.moveUpAction.setImageDescriptor(new ImageDescriptor() {
			@Override public ImageData getImageData() {
				return Activator.getDefault().getImageRegistry().get("MOVEUP").
						getImageData();
			}
		});
		this.getViewSite().getActionBars().getToolBarManager().add(
				this.moveUpAction);
		
		// Move down
		this.moveDownAction = new MoveFileDownInPlayListAction(this);
		this.moveDownAction.setText("Move down");
		this.moveDownAction.setImageDescriptor(new ImageDescriptor() {
			@Override public ImageData getImageData() {
				return Activator.getDefault().getImageRegistry().get("MOVEDOWN").
						getImageData();
			}
		});
		this.getViewSite().getActionBars().getToolBarManager().add(this.moveDownAction);

		// Remove
		this.removeAction = new RemoveFileFromPlayListAction(this);
		this.removeAction.setText("Remove");
		this.removeAction.setImageDescriptor(PlatformUI.getWorkbench().
			getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_DELETE));
		this.getViewSite().getActionBars().getToolBarManager().add(
				this.removeAction);
		
		this.getViewSite().getActionBars().getToolBarManager().add(new Separator("additions"));
	}
	
	/*
	 * 
	 */
	private void actionsEnabled(boolean enabled) {
		this.addAction.setEnabled(enabled);
		this.removeAction.setEnabled(enabled);
		this.moveUpAction.setEnabled(enabled);
		this.moveDownAction.setEnabled(enabled);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setFocus() {
		this.tableViewer.getTable().setFocus();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {
		Activator.getDefault().getEcp1Client().
				removeConnectionStateListener(this);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stackConnected() {
		actionsEnabled(true);
		this.tableViewer.getTable().getDisplay().syncExec(new Runnable() {
			@Override public void run() {
				tableViewer.getTable().setEnabled(true);
				evaluateProperties();
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stackDisconnected() {
		actionsEnabled(false);
		this.tableViewer.getTable().getDisplay().syncExec(new Runnable() {
			@Override public void run() {
				tableViewer.setInput(null);
				tableViewer.getTable().setEnabled(false);
				evaluateProperties();
			}
		});
	}

	private void evaluateProperties() {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IEvaluationService evaluationService = (IEvaluationService)window.getService(IEvaluationService.class);
		if (evaluationService != null) {
			evaluationService.requestEvaluation(EngineConnected.PROPERTY_NAMESPACE + "." 
					+ EngineConnected.PROPERTY_ENGINE_CONNECTED);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void autoPlayHasChanged(final IPlayListController playListController) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void playListHasChanged(final IPlayListController playListController) {
		// Message View
		Activator.getDefault().getMessageList().add(
				new ViewerMessage(MessageSource.VIEWER, Levels.INFO, 
						"Got new play list with " + 
						playListController.getEntries().size() + 
						" entries."));
		for(PlayListEntry entry : playListController.getEntries()) {
			Activator.getDefault().getMessageList().add(
					new ViewerMessage(MessageSource.VIEWER, Levels.DEBUG, 
							"PlayListEntry: id = " + entry.getId() + 
							" name = " + entry.getName() + 
							" author " + entry.getAuthor() + "."));
		}
		
		if (!this.tableViewer.getTable().isDisposed()) {
			this.tableViewer.getTable().getDisplay().syncExec(new Runnable() {
				@Override public void run() {
					final TableItem[] selected = tableViewer.getTable().
							getSelection();
					final List<Integer> ids = new ArrayList<Integer>();
					for(int i = 0; i < selected.length; ++i) {
						ids.add(((PlayListEntry)selected[i].getData()).getId());
					}
					tableViewer.setInput(playListController.getEntries());
					final List<Integer> indexes = new ArrayList<Integer>();
					final TableItem[] items = tableViewer.getTable().getItems();
					for(int i = 0; i < items.length; ++i) {
						if (ids.contains(((PlayListEntry)items[i].getData()).
								getId())) {
							indexes.add(i);
						}
					}
					int[] vals = new int[indexes.size()];
					for(int i = 0; i < vals.length; ++i) {
						vals[i] = indexes.get(i);
					}
					tableViewer.getTable().setSelection(vals);
				}
			});
		}
	}

	/**
	 * Returns the contained table viewer.
	 * 
	 * @return the contained table viewer
	 */
	public TableViewer getTableViewer() {
		return this.tableViewer;
	}
}