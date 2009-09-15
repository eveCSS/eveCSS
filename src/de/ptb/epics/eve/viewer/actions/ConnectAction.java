package de.ptb.epics.eve.viewer.actions;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.SubContributionItem;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.jface.dialogs.InputDialog;

import de.ptb.epics.eve.ecp1.client.interfaces.IConnectionStateListener;
import de.ptb.epics.eve.viewer.Activator;
import de.ptb.epics.eve.viewer.MessageSource;
import de.ptb.epics.eve.viewer.MessageTypes;
import de.ptb.epics.eve.viewer.ViewerMessage;

/**
 * Our sample action implements workbench action delegate.
 * The action proxy will be created by the workbench and
 * shown in the UI. When the user tries to use the action,
 * this delegate will be created and execution will be 
 * delegated to it.
 * @see IWorkbenchWindowActionDelegate
 */
public class ConnectAction implements IWorkbenchWindowActionDelegate, IConnectionStateListener {
	
	private IWorkbenchWindow window;
	
	/**
	 * The constructor.
	 */
	public ConnectAction() {
		Activator.getDefault().getEcp1Client().addConnectionStateListener( this );
	}

	/**
	 * The action has been activated. The argument of the
	 * method represents the 'real' action sitting
	 * in the workbench UI.
	 * @see IWorkbenchWindowActionDelegate#run
	 */
	public void run( final IAction action ) {
		InputDialog inputDialog = new InputDialog( window.getShell(), "Connect", "Please enter the address of the EVE-engine!", "", null );
		if( InputDialog.OK == inputDialog.open() ) {
			String input = inputDialog.getValue();
			Activator.getDefault().getMessagesContainer().addMessage( new ViewerMessage( MessageSource.APPLICATION, MessageTypes.INFO, "Trying to connect to: " + input + "." ) );
			int port = 31107;
			int index  = input.lastIndexOf( ":" );
			if( index != -1 ) {
				port = Integer.parseInt( input.substring( index + 1) );
				input = input.substring( 0, index );
			}
			try {
				Activator.getDefault().getEcp1Client().connect( new InetSocketAddress( input, port ), "" );
				action.setEnabled( false );
				Activator.getDefault().getMessagesContainer().addMessage( new ViewerMessage( MessageSource.APPLICATION, MessageTypes.INFO, "Connection established to: " + input + "." ) );
			} catch( final IOException e ) {
				Activator.getDefault().getMessagesContainer().addMessage( new ViewerMessage( MessageSource.APPLICATION, MessageTypes.ERROR, "Cannot establish connection! Reasion: " + e.getMessage() + "." ) );
				e.printStackTrace();
			}
		}
		
	}

	/**
	 * Selection in the workbench has been changed. We 
	 * can change the state of the 'real' action here
	 * if we want, but this can only happen after 
	 * the delegate has been created.
	 * @see IWorkbenchWindowActionDelegate#selectionChanged
	 */
	public void selectionChanged( final IAction action, final ISelection selection ) {
		if( Activator.getDefault().getEcp1Client().isRunning() ) {
			action.setEnabled( false );
		} else {
			action.setEnabled( true );
		}
	}

	/**
	 * We can use this method to dispose of any system
	 * resources we previously allocated.
	 * @see IWorkbenchWindowActionDelegate#dispose
	 */
	public void dispose() {
	}

	/**
	 * We will cache window object in order to
	 * be able to provide parent shell for the message dialog.
	 * @see IWorkbenchWindowActionDelegate#init
	 */
	public void init( final IWorkbenchWindow window ) {
		this.window = window;
	}

	public void stackConnected() {
		Menu menubar = this.window.getShell().getMenuBar();
		if( menubar != null ) {
			MenuItem[] items = menubar.getItems();
			for( int i = 0; i < items.length; ++i ) {
				if( items[i].getText().equals( "Engine Connection" ) ) {
					MenuItem[] subItems = items[i].getMenu().getItems();
					for( int j = 0; j < subItems.length; ++j ) {
						if( subItems[ j ].getText().equals( "Disconnect" ) ) {
							Object menuItemData = subItems[ j ].getData();
							if( menuItemData != null && menuItemData instanceof SubContributionItem ) {
								 IContributionItem conItems = ((SubContributionItem)menuItemData).getInnerItem();
								 if( conItems instanceof ActionContributionItem ) {
									 ActionContributionItem aItems = (ActionContributionItem)conItems;
									 aItems.getAction().setEnabled( true );
								 }
							}
							break;
						}
					}
					break;
				}
			}
		}
	}

	public void stackDisconnected() {
		Menu menubar = this.window.getShell().getMenuBar();
		if( menubar != null ) {
			MenuItem[] items = menubar.getItems();
			for( int i = 0; i < items.length; ++i ) {
				if( items[i].getText().equals( "Engine Connection" ) ) {
					MenuItem[] subItems = items[i].getMenu().getItems();
					for( int j = 0; j < subItems.length; ++j ) {
						if( subItems[ j ].getText().equals( "Connect" ) ) {
							Object menuItemData = subItems[ j ].getData();
							if( menuItemData != null && menuItemData instanceof SubContributionItem ) {
								 IContributionItem conItems = ((SubContributionItem)menuItemData).getInnerItem();
								 if( conItems instanceof ActionContributionItem ) {
									 ActionContributionItem aItems = (ActionContributionItem)conItems;
									 aItems.getAction().setEnabled( true );
								 }
							}
							break;
						}
					}
					break;
				}
			}
		}
		
	}
	
}

