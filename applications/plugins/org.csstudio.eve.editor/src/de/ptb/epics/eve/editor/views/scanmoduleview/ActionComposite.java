package de.ptb.epics.eve.editor.views.scanmoduleview;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.widgets.Composite;

import de.ptb.epics.eve.data.scandescription.ScanModule;

/**
 * Prototype for Composites used in the actions tab.
 * <p>
 * The composite is notified of scan module changes of its parent view via 
 * {@link #setScanModule(ScanModule)}. Composites extending this class 
 * should add the contained {@link TableViewerFocusListener} to the viewer 
 * after initialization.
 * 
 * @author Marcus Michalsky
 * @since 1.2
 */
public abstract class ActionComposite extends Composite {

	private static Logger logger = 
			Logger.getLogger(ActionComposite.class.getName());
	
	protected TableViewer tableViewer;
	protected ScanModuleView parentView;

	/**
	 * Constructor.
	 * 
	 * @param parentView the view the composite is contained in
	 * @param parent the parent
	 * @param style the style
	 */
	public ActionComposite(final ScanModuleView parentView, Composite parent, 
			int style) {
		super(parent, style);
		this.parentView = parentView;
	}

	/**
	 * Sets the {@link de.ptb.epics.eve.data.scandescription.ScanModule}.
	 * 
	 * @param scanModule the 
	 * 		  {@link de.ptb.epics.eve.data.scandescription.ScanModule} that 
	 * 		  should be set
	 */
	public void setScanModule(final ScanModule scanModule) {
		logger.debug(this.getClass().getName());
		
		this.tableViewer.setInput(scanModule);
		
		// if there are any entries ... 
		if(tableViewer.getTable().getItems().length > 0) {
			// ... and none is selected ...
			if(tableViewer.getTable().getSelectionCount() == 0) {
				// ... select the first one
				tableViewer.getTable().select(0);
			}
		}
		parentView.setSelectionProvider(this.tableViewer);
	}

	// ************************************************************************
	// **************************** Listener **********************************
	// ************************************************************************
	
	/**
	 * {@link org.eclipse.swt.events.FocusListener} of <code>tableViewer</code>.
	 */
	protected class TableViewerFocusListener implements FocusListener {
		
		/**
		 * Constructor.
		 */
		public TableViewerFocusListener() {
			super();
		}
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void focusGained(FocusEvent e) {
			logger.debug("focusGained");
			parentView.setSelectionProvider(tableViewer);
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void focusLost(FocusEvent e) {
		}
	}
}