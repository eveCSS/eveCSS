package de.ptb.epics.eve.editor.views.scanview;

import org.apache.log4j.Logger;
import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.fieldassist.ControlDecorationSupport;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
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
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import de.ptb.epics.eve.data.scandescription.ScanDescription;
import de.ptb.epics.eve.editor.Activator;
import de.ptb.epics.eve.editor.gef.editparts.ChainEditPart;
import de.ptb.epics.eve.editor.gef.editparts.ScanDescriptionEditPart;
import de.ptb.epics.eve.editor.gef.editparts.ScanModuleEditPart;
import de.ptb.epics.eve.editor.gef.editparts.StartEventEditPart;
import de.ptb.epics.eve.editor.views.EditorViewPerspectiveListener;
import de.ptb.epics.eve.editor.views.IEditorView;
import de.ptb.epics.eve.util.swt.TextSelectAllFocusListener;
import de.ptb.epics.eve.util.swt.TextSelectAllMouseListener;

/**
 * @author ?
 * @author Marcus Michalsky
 * @author Hartmut Scherr
 */
public class ScanView extends ViewPart implements IEditorView,
		ISelectionListener {

	/** the unique identifier of the view */
	public static final String ID = "de.ptb.epics.eve.editor.views.ScanView";

	// logging
	private static Logger logger = Logger.getLogger(ScanView.class.getName());
	
	private ScanDescription currentScanDescription;
	
	// the utmost composite (which contains all elements)
	private Composite top;
	
	private Label repeatCountLabel;
	private Text repeatCountText;
	
	private DataBindingContext context;
	private ISelectionProvider selectionProvider;
	private IObservableValue selectionObservable;

	private IObservableValue repeatCountTargetObservable;
	private IObservableValue repeatCountModelObservable;
	private Binding repeatCountBinding;
	
	// Delegates
	private EditorViewPerspectiveListener perspectiveListener;
	
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
		this.repeatCountText.addFocusListener(new TextFocusListener(
				this.repeatCountText));
		this.repeatCountText.addMouseListener(new TextSelectAllMouseListener(
				this.repeatCountText));
		GridData gridData = new GridData();
		gridData.horizontalIndent = 7;
		this.repeatCountText.setLayoutData(gridData);
		
		this.top.setVisible(false);
		
		// listen to selection changes (if a chain (or one of its scan modules)
		// is selected, its attributes are made available for editing)
		getSite().getWorkbenchWindow().getSelectionService()
				.addSelectionListener(this);
		
		perspectiveListener = new EditorViewPerspectiveListener(this);
		PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.addPerspectiveListener(perspectiveListener);
		
		this.bindValues();
	}

	private void bindValues() {
		this.context = new DataBindingContext();

		this.selectionProvider = new ScanSelectionProvider();
		this.selectionObservable = ViewersObservables
				.observeSingleSelection(selectionProvider);
		
		this.repeatCountTargetObservable = SWTObservables
				.observeText(this.repeatCountText, SWT.Modify);
		this.repeatCountModelObservable = BeansObservables.observeDetailValue(
				selectionObservable, ScanDescription.REPEAT_COUNT_PROP,
				Integer.class);
		UpdateValueStrategy targetToModelStrategy = new UpdateValueStrategy(
				UpdateValueStrategy.POLICY_UPDATE);
		targetToModelStrategy.setAfterGetValidator(new RepeatCountValidator());
		this.repeatCountBinding = this.context.bindValue(
				repeatCountTargetObservable, repeatCountModelObservable,
				targetToModelStrategy, new UpdateValueStrategy(
						UpdateValueStrategy.POLICY_UPDATE));
		ControlDecorationSupport.create(repeatCountBinding, SWT.LEFT);
	}
	
	/*
	 * 
	 */
	private void setCurrentScanDescription(ScanDescription scanDescription) {
		this.currentScanDescription = scanDescription;
		if (this.currentScanDescription == null) {
			this.setPartName("Scan View");
			this.top.setVisible(false);
		} else {
			this.setPartName("Scan: " + PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getActivePage()
					.getActiveEditor().getTitle());
			this.top.setVisible(true);
		}
		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		logger.debug("selection changed");

		if (selection instanceof IStructuredSelection) {
			if (((IStructuredSelection) selection).size() == 0) {
				return;
			}
		}
		Object o = ((IStructuredSelection) selection).toList().get(0);
		if (o instanceof ScanModuleEditPart) {
			// a scan module belongs to a chain -> show chain's scan description
			if (logger.isDebugEnabled()) {
				logger.debug("ScanModule "
						+ ((ScanModuleEditPart) o).getModel() + " selected.");
			}
			setCurrentScanDescription(((ScanModuleEditPart) o).getModel()
					.getChain().getScanDescription());
		} else if (o instanceof StartEventEditPart) {
			// a start event belongs to a chain -> show chain's scan description
			if (logger.isDebugEnabled()) {
				logger.debug("ScanDescription "
						+ (((StartEventEditPart) o).getModel()).getChain()
								.getScanDescription() + " selected.");
			}
			setCurrentScanDescription((((StartEventEditPart) o).getModel())
					.getChain().getScanDescription());
		} else if (o instanceof ChainEditPart) {
			logger.debug("selection is ScanDescriptionEditPart: " + o);
			setCurrentScanDescription(((ChainEditPart) o).getModel()
					.getScanDescription());
		} else if (o instanceof ScanDescriptionEditPart) {
			setCurrentScanDescription(((ScanDescriptionEditPart) o).getModel());
		}else {
			logger.debug("selection other than ScanDescription -> ignore: " + o);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void reset() {
		this.setCurrentScanDescription(null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setFocus() {
		this.top.setFocus();
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