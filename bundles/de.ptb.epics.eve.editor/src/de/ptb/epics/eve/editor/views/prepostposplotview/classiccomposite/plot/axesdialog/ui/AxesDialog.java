package de.ptb.epics.eve.editor.views.prepostposplotview.classiccomposite.plot.axesdialog.ui;

import org.apache.log4j.Logger;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabFolder2Adapter;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import de.ptb.epics.eve.data.PlotModes;
import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.data.scandescription.PlotWindow;
import de.ptb.epics.eve.data.scandescription.YAxis;
import de.ptb.epics.eve.editor.views.DialogCellEditorDialog;

/**
 * @author Marcus Michalsky
 * @since 1.35
 */
public class AxesDialog extends DialogCellEditorDialog {
	private static final Logger LOGGER = Logger.getLogger(
			AxesDialog.class.getName());
	
	private PlotWindow plotWindow;
	
	private ComboViewer scaleTypeCombo;
	private CTabFolder yAxesTabFolder;
	private CTabItem emptyTabItem;

	private Composite composite;
	
	public AxesDialog(Shell shell, Control control, PlotWindow plotWindow) {
		super(shell, control);
		this.plotWindow = plotWindow;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void configureShell(Shell newShell) {
		newShell.setText(plotWindow.getName() + " (Id: " + plotWindow.getId() + ")");
		super.configureShell(newShell);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		this.composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData();
		gridData.minimumWidth = 400;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		this.composite.setLayoutData(gridData);
		GridLayout gridLayout = new GridLayout();
		gridLayout.marginWidth = 5;
		gridLayout.marginHeight = 5;
		gridLayout.numColumns = 2;
		this.composite.setLayout(gridLayout);
		
		Label motorAxisLabel = new Label(composite, SWT.NONE);
		motorAxisLabel.setText("Motor Axis:");
		ComboViewer motorAxisCombo = new ComboViewer(composite);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalIndent = 7;
		motorAxisCombo.getCombo().setLayoutData(gridData);
		final ControlDecoration axisComboDecoration = new ControlDecoration(
				motorAxisCombo.getCombo(), SWT.LEFT);
		motorAxisCombo.setContentProvider(ArrayContentProvider.getInstance());
		motorAxisCombo.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return ((Axis)element).getMotorAxis().getName();
			}
		});
		motorAxisCombo.setInput(this.plotWindow.getScanModule().getAxes());
		if (this.plotWindow.getXAxis() != null) {
			motorAxisCombo.getCombo().setText(
					this.plotWindow.getXAxis().getName());
		} else {
			axisComboDecoration.setImage(FieldDecorationRegistry.getDefault().
				getFieldDecoration(FieldDecorationRegistry.DEC_ERROR).getImage());
			axisComboDecoration.setDescriptionText("x-Axis is mandatory");
		}
		motorAxisCombo.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.
						getSelection();
				Axis selectedAxis = (Axis)selection.getFirstElement();
				AxesDialog.this.plotWindow.setXAxis(selectedAxis.
						getMotorAxis());
				axisComboDecoration.setImage(null);
				AxesDialog.this.scaleTypeCombo.getCombo().setEnabled(true);
			}
		});
		
		Label scaleTypeLabel = new Label(composite, SWT.NONE);
		scaleTypeLabel.setText("Scale Type:");
		scaleTypeCombo = new ComboViewer(composite);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalIndent = 7;
		scaleTypeCombo.getCombo().setLayoutData(gridData);
		scaleTypeCombo.setContentProvider(ArrayContentProvider.getInstance());
		scaleTypeCombo.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return PlotModes.modeToString((PlotModes)element);
			}
		});
		scaleTypeCombo.setInput(PlotModes.values());
		if (this.plotWindow.getXAxis() == null) {
			scaleTypeCombo.setSelection(null);
			scaleTypeCombo.getCombo().setEnabled(false);
		} else {
			scaleTypeCombo.getCombo().setEnabled(true);
		}
		scaleTypeCombo.setSelection(new StructuredSelection(
				this.plotWindow.getMode()));
		scaleTypeCombo.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.
						getSelection();
				AxesDialog.this.plotWindow.setMode((PlotModes)selection.
						getFirstElement());
			}
		});
		
		this.initYAxesTabFolder(composite);
		
		return composite;
	}
	
	private void initYAxesTabFolder(Composite parent) {
		this.yAxesTabFolder = new CTabFolder(parent, SWT.BORDER);
		GridData gridData = new GridData();
		gridData.horizontalSpan = 2;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		this.yAxesTabFolder.setLayoutData(gridData);
		
		// add a tab for each present y axis
		for (YAxis yAxis : this.plotWindow.getYAxes()) {
			this.addTabItem(yAxis);
		}
		// if less than two are present, add "+" tab
		if (this.plotWindow.getYAxes().size() < 2) {
			this.addEmptyTabItem();
		}
		
		// if a tab is closed, the y axis has to be removed and tabs rearranged
		this.yAxesTabFolder.addCTabFolder2Listener(new CTabFolder2Adapter() {
			@Override
			public void close(CTabFolderEvent event) {
				LOGGER.debug("closing tab");
				// remove the y axis that corresponds to the closed tab item
				if (event.item instanceof YAxisTabItem) {
					YAxisTabItem yAxisTabItem = (YAxisTabItem)event.item;
					YAxis yAxis = yAxisTabItem.getYAxis();
					AxesDialog.this.plotWindow.removeYAxis(yAxis);
				}
				// if an empty item is already present, do nothing
				for (CTabItem tabItem : AxesDialog.this.yAxesTabFolder.getItems()) {
					if (tabItem == AxesDialog.this.emptyTabItem) {
						return;
					}
				}
				// if no empty item is present and axes count < 2 add empty item
				if (AxesDialog.this.plotWindow.getYAxes().size() < 2) {
					AxesDialog.this.addEmptyTabItem();
				}
				AxesDialog.this.renameTabs();
			}
		});
		
		// if the "+" tab is clicked a new y axis and tab have to be created
		this.yAxesTabFolder.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				if (AxesDialog.this.yAxesTabFolder.getSelection() 
						!= AxesDialog.this.emptyTabItem) {
					return;
				}
				LOGGER.debug("clicked on \"+\" tab -> add new y-axis");
				// remove the empty tab
				AxesDialog.this.emptyTabItem.dispose();
				// create new y axis, add it to the model and add tab item
				YAxis newAxis = new YAxis(null);
				AxesDialog.this.plotWindow.addYAxis(newAxis);
				AxesDialog.this.addTabItem(newAxis);
				// if < 2 axes present, add empty tab
				if (AxesDialog.this.plotWindow.getYAxes().size() < 2) {
					AxesDialog.this.addEmptyTabItem();
				}
				AxesDialog.this.renameTabs();
			}
		});
		
		this.yAxesTabFolder.setSelection(0);
	}
	
	private void addTabItem(YAxis yAxis) {
		int count = this.yAxesTabFolder.getItemCount();
		final YAxisTabItem yAxisTabItem = new YAxisTabItem(yAxesTabFolder, 
				SWT.CLOSE, yAxis);
		yAxisTabItem.setText("y-Axis " + (count + 1));
		yAxisTabItem.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				AxesDialog.this.renameTabs();
			}
		});
		this.yAxesTabFolder.setSelection(yAxisTabItem);
	}
	
	private void addEmptyTabItem() {
		this.emptyTabItem = new CTabItem(yAxesTabFolder, SWT.NONE);
		this.emptyTabItem.setText("Add y-Axis");
		this.emptyTabItem.setImage(PlatformUI.getWorkbench().getSharedImages().
				getImage(ISharedImages.IMG_OBJ_ADD));
		Composite emptyComposite = new Composite(yAxesTabFolder, SWT.NONE);
		this.emptyTabItem.setControl(emptyComposite);
	}
	
	private void renameTabs() {
		int index = 1;
		for (CTabItem tabItem : this.yAxesTabFolder.getItems()) {
			if (tabItem instanceof YAxisTabItem) {
				if (tabItem.isDisposed()) {
					continue;
				}
				tabItem.setText("y-Axis " + index++);
			}
		}
	}
}
