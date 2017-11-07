package de.ptb.epics.eve.viewer.views.playlistview;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableItem;
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
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createPartControl(final Composite parent) {
		createViewer(parent);
		
		if(Activator.getDefault().getEcp1Client().isRunning()) {
			this.tableViewer.getTable().setEnabled(true);
		} else {
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
		TableViewerColumn fileColumn = new TableViewerColumn(
				this.tableViewer, SWT.LEFT);
		fileColumn.getColumn().setText("File");
		fileColumn.getColumn().setWidth(200);
		
		TableViewerColumn authorColumn = new TableViewerColumn(
				this.tableViewer, SWT.LEFT);
		authorColumn.getColumn().setText("Author");
		authorColumn.getColumn().setWidth(60);
		
		TableViewerColumn dateColumn = new TableViewerColumn(
				this.tableViewer, SWT.LEFT);
		dateColumn.getColumn().setText("Added on");
		dateColumn.getColumn().setWidth(120);
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
		this.tableViewer.getTable().getDisplay().syncExec(new Runnable() {
			@Override public void run() {
				tableViewer.setInput(null);
				tableViewer.getTable().setEnabled(false);
				evaluateProperties();
			}
		});
	}

	private void evaluateProperties() {
		IWorkbenchWindow window = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();
		IEvaluationService evaluationService = (IEvaluationService) window
				.getService(IEvaluationService.class);
		if (evaluationService != null) {
			evaluationService
					.requestEvaluation(EngineConnected.PROPERTY_NAMESPACE + "."
							+ EngineConnected.PROPERTY_ENGINE_CONNECTED);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void autoPlayHasChanged(final IPlayListController playListController) {
		// The Playlist is not interested in the auto play property
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void playListHasChanged(final IPlayListController playListController) {
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
					final List<Integer> ids = new ArrayList<>();
					for(int i = 0; i < selected.length; ++i) {
						ids.add(((PlayListEntry)selected[i].getData()).getId());
					}
					tableViewer.setInput(playListController.getEntries());
					final List<PlayListEntry> selectedEntries = new ArrayList<>();
					final TableItem[] items = tableViewer.getTable().getItems();
					for(int i = 0; i < items.length; ++i) {
						if (ids.contains(((PlayListEntry)items[i].getData()).
								getId())) {
							selectedEntries.add((PlayListEntry)items[i].getData());
						}
					}
					tableViewer.setSelection(
							new StructuredSelection(selectedEntries), true);
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