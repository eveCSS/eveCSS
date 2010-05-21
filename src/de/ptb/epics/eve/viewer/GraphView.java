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

import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

import de.ptb.epics.eve.data.scandescription.Chain;
import de.ptb.epics.eve.data.scandescription.ScanModul;
import de.ptb.epics.eve.ecp1.client.interfaces.IConnectionStateListener;

/**
 * A simple view implementation, which only displays a label.
 * 
 * @author Sven Wende
 *
 */
public final class GraphView extends ViewPart implements IUpdateListener, IConnectionStateListener {

	private Text statusText;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createPartControl( final Composite parent ) {
		parent.setLayout( new FillLayout() );
		this.statusText = new Text( parent, SWT.MULTI );
		this.statusText.setEditable( false );
		Activator.getDefault().getChainStatusAnalyzer().addUpdateLisner( this );
		this.rebuildText();
		Activator.getDefault().getEcp1Client().addConnectionStateListener( this );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setFocus() {

	}

	public void updateOccured() {
		this.rebuildText();
		
	}
	
	private void rebuildText() {
		final StringBuffer stringBuffer = new StringBuffer();
		
		if( Activator.getDefault().getCurrentScanDescription() != null ) {
			final Iterator< Chain > it = Activator.getDefault().getCurrentScanDescription().getChains().iterator();
			while( it.hasNext() ) {
				final Chain currentChain = it.next();
				if( Activator.getDefault().getChainStatusAnalyzer().getRunningChains().contains( currentChain ) ) {
					stringBuffer.append( "Chain " + currentChain.getId() + " is running" );
				} else {
					stringBuffer.append( "Chain " + currentChain.getId() + " is idle" );
				}
				final List< ScanModul > scanModules = currentChain.getScanModuls();
			
				final List< ScanModul > initialized = Activator.getDefault().getChainStatusAnalyzer().getInitializingScanModules();
				for( final ScanModul scanModule : initialized ) {
					if( scanModules.contains( scanModule ) ) {
						stringBuffer.append( " with Scan Module " + scanModule.getId() + " initialized.\n" );
						break;
					}
				}
			
				final List< ScanModul > running = Activator.getDefault().getChainStatusAnalyzer().getExecutingScanModules();
				for( final ScanModul scanModule : running ) {
					if( scanModules.contains( scanModule ) ) {
						stringBuffer.append( " with Scan Module " + scanModule.getId() + " running.\n" );
						break;
					}
				}
			
				final List< ScanModul > paused = Activator.getDefault().getChainStatusAnalyzer().getPausedScanModules();
				for( final ScanModul scanModule : paused ) {
					if( scanModules.contains( scanModule ) ) {
						stringBuffer.append( " with paused Scan Module " + scanModule.getId() + ".\n" );
						break;
					}
				}
			
				final List< ScanModul > waiting = Activator.getDefault().getChainStatusAnalyzer().getWaitingScanModules();
				for( final ScanModul scanModule : waiting ) {
					if( scanModules.contains( scanModule ) ) {
						stringBuffer.append( " with Scan Module " + scanModule.getId() + " waiting for trigger.\n" );
						break;
					}
				}
			}
		}
		
		
		this.statusText.getDisplay().syncExec( new Runnable() {

			public void run() {
				statusText.setText( stringBuffer.toString() );
				
			}
			
			
		});
	}

	@Override
	public void stackConnected() {
		// TODO unsure if this is the correct way to do it
		ToolBarManager toolBarManager = (ToolBarManager) getViewSite().getActionBars().getToolBarManager();
		int index = toolBarManager.indexOf("de.ptb.epics.eve.viewer.connectCommand");
		if (index >= 0) toolBarManager.getControl().getItem(index).setEnabled(false);
		index = toolBarManager.indexOf("de.ptb.epics.eve.viewer.disconnectCommand");
		if (index >= 0) toolBarManager.getControl().getItem(index).setEnabled(true);
	}

	@Override
	public void stackDisconnected() {
		// TODO unsure if this is the correct way to do it
		if (!this.statusText.isDisposed()) this.statusText.getDisplay().syncExec( new Runnable() {

			public void run() {
				if (!statusText.isDisposed()) {
					ToolBarManager toolBarManager = (ToolBarManager) getViewSite().getActionBars().getToolBarManager();
					int index = toolBarManager.indexOf("de.ptb.epics.eve.viewer.connectCommand");
					if (index >= 0) toolBarManager.getControl().getItem(index).setEnabled(true);
					index = toolBarManager.indexOf("de.ptb.epics.eve.viewer.disconnectCommand");
					if (index >= 0) toolBarManager.getControl().getItem(index).setEnabled(false);
				}
			}
		});
		
	}

}
