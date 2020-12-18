package de.ptb.epics.eve.editor.views.prepostposplotview.classiccomposite.plot.axesdialog.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.csstudio.swt.xygraph.figures.Trace.PointStyle;
import org.csstudio.swt.xygraph.figures.Trace.TraceType;
import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.viewers.ViewerProperties;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import de.ptb.epics.eve.data.PlotModes;
import de.ptb.epics.eve.data.scandescription.Channel;
import de.ptb.epics.eve.data.scandescription.PlotWindow;
import de.ptb.epics.eve.data.scandescription.YAxis;
import de.ptb.epics.eve.data.scandescription.YAxisModifier;
import de.ptb.epics.eve.editor.views.prepostposplotview.classiccomposite.plot.axesdialog.PredefinedColors;

/**
 * @author Marcus Michalsky
 * @since 1.35
 */
public class YAxisTabItem extends CTabItem implements PropertyChangeListener {	
	private final YAxis yAxis;
	private final PlotWindow plotWindow;
	
	private ComboViewer channelCombo;
	private ComboViewer normalizeCombo;
	private ComboViewer predefinedColorsCombo;
	private ColorFieldEditor colorFieldEditor;
	private ComboViewer linestyleCombo;
	private ComboViewer markstyleCombo;
	private ComboViewer scaletypeCombo;
	private ComboViewer modifierCombo;
	
	private DataBindingContext context;
	private Binding colorComboBinding;
	
	private boolean inColorSelectionLoop;
	
	public YAxisTabItem(CTabFolder parent, int style, final YAxis yAxis, 
			PlotWindow plotWindow) {
		super(parent, style);
		this.yAxis = yAxis;
		this.plotWindow = plotWindow;
		this.yAxis.addPropertyChangeListener(YAxis.COLOR_PROP, this);
		Composite top = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		top.setLayout(gridLayout);
		
		Label channelLabel = new Label(top, SWT.NONE);
		channelLabel.setText("Detector Channel:");
		
		channelCombo = new ComboViewer(top);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalIndent = 7;
		channelCombo.getCombo().setLayoutData(gridData);
		ControlDecoration channelDecoration = new ControlDecoration(
				channelCombo.getCombo(), SWT.LEFT);
		channelDecoration.setImage(FieldDecorationRegistry.getDefault().
			getFieldDecoration(FieldDecorationRegistry.DEC_ERROR).getImage());
		channelDecoration.setDescriptionText("At least one y axis has to be set");
		channelDecoration.hide();
		channelCombo.setContentProvider(ArrayContentProvider.getInstance());
		channelCombo.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return ((Channel)element).getDetectorChannel().getName();
			}
		});
		channelCombo.setInput(plotWindow.getScanModule().getChannels());
		if (yAxis.getDetectorChannel() != null) {
			for (Channel channel : plotWindow.getScanModule().getChannelList()) {
				if (channel.getDetectorChannel().equals(yAxis.getDetectorChannel())) {
					channelCombo.setSelection(new StructuredSelection(channel));
				}
			}
		}
		channelCombo.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection)event.
						getSelection();
				yAxis.setDetectorChannel(((Channel)selection.getFirstElement()).
						getDetectorChannel());
				YAxisTabItem.this.setEnabledStates();
			}
		});
		
		Label normalizeLabel = new Label(top, SWT.NONE);
		normalizeLabel.setText("Normalize Channel:");
		
		normalizeCombo = new ComboViewer(top);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalIndent = 7;
		normalizeCombo.getCombo().setLayoutData(gridData);
		normalizeCombo.setContentProvider(ArrayContentProvider.getInstance());
		normalizeCombo.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				if (element == null) {
					return "none";
				}
				return ((Channel)element).getDetectorChannel().getName();
			}
		});
		normalizeCombo.setInput(plotWindow.getScanModule().getChannels());
		normalizeCombo.insert(null, normalizeCombo.getCombo().getItemCount());
		normalizeCombo.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				if (event.getSelection().equals(StructuredSelection.EMPTY)) {
					normalizeCombo.getCombo().select(
							normalizeCombo.getCombo().getItemCount()-1);
				}
			}
		});
		
		Label colorLabel = new Label(top, SWT.NONE);
		colorLabel.setText("Color:");
		
		Composite colorComposite = new Composite(top, SWT.NONE);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.grabExcessHorizontalSpace = true;
		colorComposite.setLayoutData(gridData);
		gridLayout = new GridLayout(2, false);
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		colorComposite.setLayout(gridLayout);
		
		predefinedColorsCombo = new ComboViewer(colorComposite);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalIndent = 7;
		predefinedColorsCombo.getCombo().setLayoutData(gridData);
		predefinedColorsCombo.setContentProvider(ArrayContentProvider.getInstance());
		predefinedColorsCombo.setLabelProvider(new LabelProvider());
		predefinedColorsCombo.setInput(PredefinedColors.values());
		
		Composite colorFieldEditorWrapper = new Composite(colorComposite, 
				SWT.NONE);
		gridLayout = new GridLayout(2, false);
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		colorFieldEditorWrapper.setLayout(gridLayout);
		
		colorFieldEditor = new ColorFieldEditor("", "", 
				colorFieldEditorWrapper);
		colorFieldEditor.getColorSelector().setColorValue(yAxis.getColor());
		colorFieldEditor.getColorSelector().addListener(new IPropertyChangeListener() {
			@Override
			public void propertyChange(org.eclipse.jface.util.PropertyChangeEvent event) {
				YAxisTabItem.this.inColorSelectionLoop = true;
				YAxisTabItem.this.yAxis.setColor(YAxisTabItem.this.
						colorFieldEditor.getColorSelector().getColorValue());
			}
		});
		
		Label linestyleLabel = new Label(top, SWT.NONE);
		linestyleLabel.setText("Linestyle:");
		
		linestyleCombo = new ComboViewer(top);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalIndent = 7;
		linestyleCombo.getCombo().setLayoutData(gridData);
		linestyleCombo.setContentProvider(ArrayContentProvider.getInstance());
		linestyleCombo.setLabelProvider(new LabelProvider());
		linestyleCombo.setInput(TraceType.values());
		linestyleCombo.setSelection(new StructuredSelection(yAxis.getLinestyle()));
		linestyleCombo.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection)event.
						getSelection();
				YAxisTabItem.this.yAxis.setLinestyle((TraceType)selection.
						getFirstElement());
			}
		});
		
		Label markstyleLabel = new Label(top, SWT.NONE);
		markstyleLabel.setText("Markstyle:");
		
		markstyleCombo = new ComboViewer(top);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalIndent = 7;
		markstyleCombo.getCombo().setLayoutData(gridData);
		markstyleCombo.setContentProvider(ArrayContentProvider.getInstance());
		markstyleCombo.setLabelProvider(new LabelProvider());
		markstyleCombo.setInput(PointStyle.values());
		markstyleCombo.setSelection(new StructuredSelection(yAxis.getMarkstyle()));
		markstyleCombo.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection)event.
						getSelection();
				YAxisTabItem.this.yAxis.setMarkstyle((PointStyle)selection.
						getFirstElement());
			}
		});
		
		Label scaletypeLabel = new Label(top, SWT.NONE);
		scaletypeLabel.setText("Scaletype:");
		
		scaletypeCombo = new ComboViewer(top);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalIndent = 7;
		scaletypeCombo.getCombo().setLayoutData(gridData);
		scaletypeCombo.setContentProvider(ArrayContentProvider.getInstance());
		scaletypeCombo.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return PlotModes.modeToString((PlotModes)element);
			}
		});
		scaletypeCombo.setInput(PlotModes.values());
		scaletypeCombo.setSelection(new StructuredSelection(
				this.yAxis.getMode()));
		scaletypeCombo.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection)
						event.getSelection();
				YAxisTabItem.this.yAxis.setMode((PlotModes)selection.
						getFirstElement());
			}
		});
		
		Label modifierLabel = new Label(top, SWT.NONE);
		modifierLabel.setText("Modifier:");
		
		modifierCombo = new ComboViewer(top);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalIndent = 7;
		modifierCombo.getCombo().setLayoutData(gridData);
		modifierCombo.setContentProvider(ArrayContentProvider.getInstance());
		modifierCombo.setLabelProvider(new LabelProvider());
		modifierCombo.setInput(YAxisModifier.values());
		
		modifierCombo.setSelection(new StructuredSelection(yAxis.getModifier()));
		modifierCombo.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				if (yAxis != null && modifierCombo.getCombo().getSelectionIndex() != -1) {
					yAxis.setModifier(YAxisModifier.values()[modifierCombo.getCombo().getSelectionIndex()]);
				}
			}
		});
		
		this.setControl(top);
		
		this.bindValues();
		
		this.setEnabledStates();
		
		this.inColorSelectionLoop = false;
		
		predefinedColorsCombo.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				if (inColorSelectionLoop) {
					YAxisTabItem.this.inColorSelectionLoop = false;
					return;
				}
				IStructuredSelection selection = (IStructuredSelection) event.
						getSelection();
				PredefinedColors color = (PredefinedColors)selection.
						getFirstElement();
				if (color.equals(PredefinedColors.CUSTOM)) {
					YAxisTabItem.this.inColorSelectionLoop = true;
					RGB oldColor = yAxis.getColor();
					YAxisTabItem.this.colorFieldEditor.getColorSelector().open();
					RGB newColor = yAxis.getColor();
					if (oldColor.equals(newColor)) {
						colorComboBinding.updateModelToTarget();
					}
				}
			}
		});
	}

	private void bindValues() {
		context = new DataBindingContext();
		
		IObservableValue normalizeComboTargetObservable = ViewerProperties.
				singleSelection().observe(normalizeCombo);
		IObservableValue normalizeComboModelObservable = BeanProperties.
				value(YAxis.class, YAxis.NORMALIZE_PROP).observe(yAxis);
		UpdateValueStrategy normalizeComboTargetToModelStrategy = 
				new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE).
				setConverter(new NormalizeComboTargetToModelConverter());
		UpdateValueStrategy normalizeComboModelToTargetStrategy = 
				new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE).
				setConverter(new NormalizeComboModelToTargetConverter(yAxis, 
						plotWindow));
		context.bindValue(normalizeComboTargetObservable, 
				normalizeComboModelObservable, 
				normalizeComboTargetToModelStrategy, 
				normalizeComboModelToTargetStrategy);

		IObservableValue colorComboTargetObservable = ViewerProperties.
				singleSelection().observe(predefinedColorsCombo);
		IObservableValue colorComboModelObservable = BeanProperties.
				value(YAxis.class, YAxis.COLOR_PROP).observe(yAxis);
		UpdateValueStrategy colorComboTargetToModelStrategy = 
				new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE).
				setConverter(new ColorComboTargetToModelConverter(yAxis));
		UpdateValueStrategy colorComboModelToTargetStrategy = 
				new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE).
				setConverter(new ColorComboModelToTargetConverter());
		colorComboBinding = context.bindValue(colorComboTargetObservable, 
				colorComboModelObservable, 
				colorComboTargetToModelStrategy, 
				colorComboModelToTargetStrategy);
	}
	
	public YAxis getYAxis() {
		return this.yAxis;
	}

	private void setEnabledStates() {
		if (this.yAxis.getDetectorChannel() == null) {
			this.normalizeCombo.getCombo().setEnabled(false);
			this.predefinedColorsCombo.getCombo().setEnabled(false);
			this.colorFieldEditor.getColorSelector().setEnabled(false);
			this.linestyleCombo.getCombo().setEnabled(false);
			this.markstyleCombo.getCombo().setEnabled(false);
			this.scaletypeCombo.getCombo().setEnabled(false);
			this.modifierCombo.getCombo().setEnabled(false);
		} else {
			this.normalizeCombo.getCombo().setEnabled(true);
			this.predefinedColorsCombo.getCombo().setEnabled(true);
			this.colorFieldEditor.getColorSelector().setEnabled(true);
			this.linestyleCombo.getCombo().setEnabled(true);
			this.markstyleCombo.getCombo().setEnabled(true);
			this.scaletypeCombo.getCombo().setEnabled(true);
			this.modifierCombo.getCombo().setEnabled(true);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(YAxis.COLOR_PROP)) {
			colorFieldEditor.getColorSelector().setColorValue(yAxis.getColor());
		}
	}

	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {
		this.yAxis.removePropertyChangeListener(YAxis.COLOR_PROP, this);
		this.context.dispose();
		super.dispose();
	}
}
