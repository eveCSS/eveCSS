package de.ptb.epics.eve.editor.views.scanview;

import org.apache.log4j.Logger;
import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

import de.ptb.epics.eve.data.scandescription.Chain;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener;
import de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent;
import de.ptb.epics.eve.editor.Activator;
import de.ptb.epics.eve.editor.views.IEditorView;
import de.ptb.epics.eve.util.swt.TextSelectAllFocusListener;
import de.ptb.epics.eve.util.swt.TextSelectAllMouseListener;

/**
 * <code>ScanView</code> is the graphical representation of a 
 * {@link de.ptb.epics.eve.data.scandescription.Chain}. It shows the elements 
 * of its underlying model and allows editing them.
 * <p>
 * To define which chain should be presented use {@link #setCurrentChain(Chain)}. 
 * 
 * @author ?
 * @author Marcus Michalsky
 * @author Hartmut Scherr
 */
public class ScanView extends ViewPart implements IEditorView, 
					ISelectionListener, IModelUpdateListener {

	/** the unique identifier of the view */
	public static final String ID = "de.ptb.epics.eve.editor.views.ScanView";

	// logging
	private static Logger logger = Logger.getLogger(ScanView.class.getName());
	
	// the utmost composite (which contains all elements)
	private Composite top;
	
	private Label repeatCountLabel;
	private Text repeatCountText;
	
	private ISelectionProvider selectionProvider;

	private IObservableValue selectionObservable;

	private IObservableValue repeatCountTargetObservable;
	private IObservableValue repeatCountModelObservable;
	private Binding repeatCountBinding;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createPartControl(final Composite parent) {
		logger.debug("createPartControl");

		parent.setLayout(new FillLayout());

		// if no measuring station is loaded -> show error and do nothing
		if (Activator.getDefault().getMeasuringStation() == null) {
			final Label errorLabel = new Label(parent, SWT.NONE);
			errorLabel.setText("No Measuring Station has been loaded. "
					+ "Please check Preferences!");
			return;
		}
		
		// top composite
		this.top = new Composite(parent, SWT.NONE);
		this.top.setLayout(new GridLayout(2, false));
		
		this.repeatCountLabel = new Label(this.top, SWT.NONE);
		this.repeatCountLabel.setText("Repeat Count:");
		this.repeatCountText = new Text(this.top, SWT.BORDER);
		this.repeatCountText
				.setToolTipText("the number of times the scan will be repeated");
		
		this.repeatCountText.addFocusListener(new TextSelectAllFocusListener(
				this.repeatCountText));
		this.repeatCountText.addMouseListener(new TextSelectAllMouseListener(
				this.repeatCountText));
	}

	@Override
	public void updateEvent(ModelUpdateEvent modelUpdateEvent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * @author Marcus Michalsky
	 * @since 1.10
	 */
	private class TextFocusListener extends FocusAdapter {

		private Text widget;

		/**
		 * @param widget
		 *            the widget to observe
		 */
		public TextFocusListener(Text widget) {
			this.widget = widget;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void focusLost(FocusEvent e) {
			if (this.widget == repeatCountText) {
				repeatCountBinding.updateModelToTarget();
			}
		}
	}
}