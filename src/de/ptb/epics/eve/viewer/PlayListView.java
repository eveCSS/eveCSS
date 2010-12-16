/* 
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton, 
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. 
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED 
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND 
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE 
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR 
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. 
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, 
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION, 
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY 
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package de.ptb.epics.eve.viewer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
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
import de.ptb.epics.eve.viewer.actions.AddFileToPlayListAction;
import de.ptb.epics.eve.viewer.actions.MoveFileDownInPlayListAction;
import de.ptb.epics.eve.viewer.actions.MoveFileUpInPlayListAction;
import de.ptb.epics.eve.viewer.actions.RemoveFileFromPlayListAction;

public final class PlayListView extends ViewPart implements IConnectionStateListener, IPlayListListener {

	private TableViewer tableViewer;
	private AddFileToPlayListAction addAction;
	private RemoveFileFromPlayListAction removeAction;
	private MoveFileUpInPlayListAction moveUpAction;
	private MoveFileDownInPlayListAction moveDownAction;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createPartControl( final Composite parent ) {
		
		this.addAction = new AddFileToPlayListAction();
		this.addAction.setText( "Add a file to the playlist." );
		this.addAction.setImageDescriptor( PlatformUI.getWorkbench().getSharedImages().getImageDescriptor( ISharedImages.IMG_OBJ_FILE ) );
		this.getViewSite().getActionBars().getToolBarManager().add( this.addAction );
		
		this.removeAction = new RemoveFileFromPlayListAction( this );
		this.removeAction.setText( "Remove" );
		this.removeAction.setImageDescriptor( PlatformUI.getWorkbench().getSharedImages().getImageDescriptor( ISharedImages.IMG_TOOL_DELETE ) );
		this.getViewSite().getActionBars().getToolBarManager().add( this.removeAction );
		
		this.moveDownAction = new MoveFileDownInPlayListAction( this );
		this.moveDownAction.setText( "Move backward" );
		this.moveDownAction.setImageDescriptor( PlatformUI.getWorkbench().getSharedImages().getImageDescriptor( ISharedImages.IMG_TOOL_BACK ) );
		this.getViewSite().getActionBars().getToolBarManager().add( this.moveDownAction );
		
		this.moveUpAction = new MoveFileUpInPlayListAction( this );
		this.moveUpAction.setText( "Move forward" );
		this.moveUpAction.setImageDescriptor( PlatformUI.getWorkbench().getSharedImages().getImageDescriptor( ISharedImages.IMG_TOOL_FORWARD ) );
		this.getViewSite().getActionBars().getToolBarManager().add( this.moveUpAction );

		this.tableViewer = new TableViewer( parent, SWT.MULTI );
		
		TableColumn column = new TableColumn( this.tableViewer.getTable(), SWT.LEFT, 0 );
	    column.setText( "File" );
	    column.setWidth( 140 );

	    column = new TableColumn( this.tableViewer.getTable(), SWT.LEFT, 1 );
	    column.setText( "Author" );
	    column.setWidth( 60 );
	    
	    this.tableViewer.getTable().setHeaderVisible( true );
	    this.tableViewer.getTable().setLinesVisible( true );
	    
	    this.tableViewer.setContentProvider( new PlayListTableContentProvider() );
	    this.tableViewer.setLabelProvider( new PlayListTableLabelProvider() );
		
	    MenuManager manager = new MenuManager();
	    Menu menu = manager.createContextMenu( this.tableViewer.getControl() );
	    this.tableViewer.getControl().setMenu( menu );
	    manager.add( this.removeAction );
	    manager.add( this.moveDownAction );
	    manager.add( this.moveUpAction );
	    
		if( Activator.getDefault().getEcp1Client().isRunning() ) {
			this.addAction.setEnabled( true );
			this.removeAction.setEnabled( true );
			this.moveUpAction.setEnabled( true );
			this.moveDownAction.setEnabled( true );
			this.tableViewer.getTable().setEnabled( true );
		} else {
			this.addAction.setEnabled( false );
			this.removeAction.setEnabled( false );
			this.moveUpAction.setEnabled( false );
			this.moveDownAction.setEnabled( false );
			this.tableViewer.getTable().setEnabled( false );
		}
		Activator.getDefault().getEcp1Client().addConnectionStateListener( this );
		Activator.getDefault().getEcp1Client().getPlayListController().addPlayListListener( this );
		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setFocus() {

	}

	
	public void dispose() {
		Activator.getDefault().getEcp1Client().removeConnectionStateListener( this );
	}
	public void stackConnected() {
		
		this.addAction.setEnabled( true );
		this.removeAction.setEnabled( true );
		this.moveUpAction.setEnabled( true );
		this.moveDownAction.setEnabled( true );
		this.tableViewer.getTable().getDisplay().syncExec( new Runnable() {

			public void run() {
				tableViewer.getTable().setEnabled( true );
			}
			
		});
			
		
	}

	public void stackDisconnected() {
		this.addAction.setEnabled( false );
		this.removeAction.setEnabled( false );
		this.moveUpAction.setEnabled( false );
		this.moveDownAction.setEnabled( false );
		this.tableViewer.getTable().getDisplay().syncExec( new Runnable() {

			public void run() {
				tableViewer.setInput( null );
				tableViewer.getTable().setEnabled( false );
			}
			
		});
	}

	public void autoPlayHasChanged( final IPlayListController playListController ) {
		
		// TODO: die Methode autoPlayHasChanged wird aufgerufen, wenn
		// sich der auroPlay Wert geändert hat.
		// Bisher wurde bei reportAutoplay playListHasChanged aufgerufen. Was dort gemacht wird,
		// rufe ich hier erstmal nicht auf, da sich meiner Meinung nach an der playList ja nichts
		// geändert hat. Überprüfen, ob das so stimmt. (16.12.10)
		Activator.getDefault().getChainStatusAnalyzer().setAutoPlayStatus(playListController.isAutoplay());
	}


	
	public void playListHasChanged( final IPlayListController playListController ) {
		
		Activator.getDefault().getMessagesContainer().addMessage( new ViewerMessage( MessageSource.VIEWER, MessageTypes.INFO, "Got new play list with " + playListController.getEntries().size() + " entries." ) );
		final Iterator< PlayListEntry > it = playListController.getEntries().iterator();
		while( it.hasNext() ) {
			final PlayListEntry entry = it.next();
			Activator.getDefault().getMessagesContainer().addMessage( new ViewerMessage( MessageSource.VIEWER, MessageTypes.DEBUG, "PlayListEntry: id = " + entry.getId() + " name = " + entry.getName() + " author " + entry.getAuthor() + "." ) );
		}

		if (!this.tableViewer.getTable().isDisposed()) this.tableViewer.getTable().getDisplay().syncExec( new Runnable() {

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

	public TableViewer getTableViewer() {
		return this.tableViewer;
	}

}
