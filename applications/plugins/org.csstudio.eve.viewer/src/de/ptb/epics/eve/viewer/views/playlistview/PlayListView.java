package de.ptb.epics.eve.viewer.views.playlistview;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

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
import de.ptb.epics.eve.viewer.messages.MessageTypes;
import de.ptb.epics.eve.viewer.messages.ViewerMessage;

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
		
		// Add file to play list - Action
		this.addAction = new AddFileToPlayListAction();
		this.addAction.setText("Add a file to the play list.");
		this.addAction.setImageDescriptor(PlatformUI.getWorkbench().
			getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJ_FILE));
		this.getViewSite().getActionBars().getToolBarManager().add(this.addAction);
		
		//
		this.moveUpAction = new MoveFileUpInPlayListAction(this);
		this.moveUpAction.setText("Move up");
		//this.moveUpAction.setImageDescriptor(PlatformUI.getWorkbench().
			//getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_FORWARD));
		this.moveUpAction.setImageDescriptor(new ImageDescriptor() {
			
			@Override
			public ImageData getImageData() {
				return Activator.getDefault().getImageRegistry().get("MOVEUP").getImageData();
			}
		});
		this.getViewSite().getActionBars().getToolBarManager().add(this.moveUpAction);
		
		// 
		this.moveDownAction = new MoveFileDownInPlayListAction(this);
		this.moveDownAction.setText("Move down");
		//this.moveDownAction.setImageDescriptor( PlatformUI.getWorkbench().
		//	getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_BACK));
		this.moveDownAction.setImageDescriptor(new ImageDescriptor() {
			
			@Override
			public ImageData getImageData() {
				return Activator.getDefault().getImageRegistry().get("MOVEDOWN").getImageData();
			}
		});
		this.getViewSite().getActionBars().getToolBarManager().add(this.moveDownAction);

		// Remove file from play list - Action
		this.removeAction = new RemoveFileFromPlayListAction(this);
		this.removeAction.setText("Remove");
		this.removeAction.setImageDescriptor(PlatformUI.getWorkbench().
			getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_DELETE));
		this.getViewSite().getActionBars().getToolBarManager().add(this.removeAction);
		
		// table viewer
		this.tableViewer = new TableViewer(parent, SWT.MULTI);
		
		// File column
		TableColumn column = 
			new TableColumn(this.tableViewer.getTable(), SWT.LEFT, 0);
	    column.setText("File");
	    column.setWidth(200);

	    // Author column
	    column = new TableColumn(this.tableViewer.getTable(), SWT.LEFT, 1);
	    column.setText("Author");
	    column.setWidth(60);
	    
	    this.tableViewer.getTable().setHeaderVisible(true);
	    this.tableViewer.getTable().setLinesVisible(true);
	    
	    this.tableViewer.setContentProvider(new PlayListTableContentProvider());
	    this.tableViewer.setLabelProvider(new PlayListTableLabelProvider());
		
	    MenuManager manager = new MenuManager();
	    Menu menu = manager.createContextMenu(this.tableViewer.getControl());
	    this.tableViewer.getControl().setMenu(menu);
	    manager.add(this.moveUpAction);
	    manager.add(this.moveDownAction);
	    manager.add(this.removeAction);

	    // connect Engine to see all Playlist entries
	    Activator.getDefault().connectEngine();
	    
		if(Activator.getDefault().getEcp1Client().isRunning()) {
			this.addAction.setEnabled(true);
			this.removeAction.setEnabled(true);
			this.moveUpAction.setEnabled(true);
			this.moveDownAction.setEnabled(true);
			this.tableViewer.getTable().setEnabled(true);
		} else {
			this.addAction.setEnabled(false);
			this.removeAction.setEnabled(false);
			this.moveUpAction.setEnabled(false);
			this.moveDownAction.setEnabled(false);
			this.tableViewer.getTable().setEnabled(false);
		}
		Activator.getDefault().getEcp1Client().addConnectionStateListener(this);
		Activator.getDefault().getEcp1Client().getPlayListController().
				addPlayListListener(this);
		
		// register the table viewer to the selection so that commands 
		// can decide whether they are active
		getSite().setSelectionProvider(tableViewer);
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
		
		this.addAction.setEnabled(true);
		this.removeAction.setEnabled(true);
		this.moveUpAction.setEnabled(true);
		this.moveDownAction.setEnabled(true);
		this.tableViewer.getTable().getDisplay().syncExec(new Runnable() {

			@Override
			public void run() {
				tableViewer.getTable().setEnabled(true);
			}
			
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stackDisconnected() {
		this.addAction.setEnabled(false);
		this.removeAction.setEnabled(false);
		this.moveUpAction.setEnabled(false);
		this.moveDownAction.setEnabled(false);
		this.tableViewer.getTable().getDisplay().syncExec(new Runnable() {

			@Override
			public void run() {
				tableViewer.setInput(null);
				tableViewer.getTable().setEnabled(false);
			}
			
		});
		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void autoPlayHasChanged(final IPlayListController playListController) {
		
		// TODO: die Methode autoPlayHasChanged wird aufgerufen, wenn
		// sich der auroPlay Wert geändert hat.
		// Bisher wurde bei reportAutoplay playListHasChanged aufgerufen. Was dort gemacht wird,
		// rufe ich hier erstmal nicht auf, da sich meiner Meinung nach an der playList ja nichts
		// geändert hat. Überprüfen, ob das so stimmt. (16.12.10)
		Activator.getDefault().getChainStatusAnalyzer().setAutoPlayStatus(playListController.isAutoplay());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void playListHasChanged(final IPlayListController playListController) {
		
		Activator.getDefault().getMessagesContainer().addMessage( new ViewerMessage( MessageSource.VIEWER, MessageTypes.INFO, "Got new play list with " + playListController.getEntries().size() + " entries." ) );
		final Iterator< PlayListEntry > it = playListController.getEntries().iterator();
		while( it.hasNext() ) {
			final PlayListEntry entry = it.next();
			Activator.getDefault().getMessagesContainer().addMessage( new ViewerMessage( MessageSource.VIEWER, MessageTypes.DEBUG, "PlayListEntry: id = " + entry.getId() + " name = " + entry.getName() + " author " + entry.getAuthor() + "." ) );
		}

		if (!this.tableViewer.getTable().isDisposed()) this.tableViewer.getTable().getDisplay().syncExec( new Runnable() {

			@Override
			public void run() {
				final TableItem[] selected = tableViewer.getTable().getSelection();
				final List< Integer > ids = new ArrayList< Integer >();
				for( int i = 0; i < selected.length; ++i ) {
					ids.add( ((PlayListEntry)selected[i].getData()).getId() );
				}
				
				tableViewer.setInput( playListController.getEntries() );
				
				final List< Integer > indexes = new ArrayList< Integer >();
				final TableItem[] items = tableViewer.getTable().getItems();
				for( int i = 0; i < items.length; ++i ) {
					if( ids.contains( ((PlayListEntry)items[ i ].getData()).getId() ) ) {
						indexes.add( i );
					}
				}
				int[] vals = new int[ indexes.size() ];
				for( int i = 0; i < vals.length; ++i ) {
					vals[ i ] = indexes.get( i );
				}
				tableViewer.getTable().setSelection( vals );
			}
			
		});
	
		
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