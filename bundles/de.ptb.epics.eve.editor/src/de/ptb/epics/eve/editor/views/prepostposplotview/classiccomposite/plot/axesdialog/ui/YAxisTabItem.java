package de.ptb.epics.eve.editor.views.prepostposplotview.classiccomposite.plot.axesdialog.ui;

import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import de.ptb.epics.eve.data.scandescription.YAxis;

/**
 * @author Marcus Michalsky
 * @since 1.35
 */
public class YAxisTabItem extends CTabItem {
	private YAxis yAxis;
	
	public YAxisTabItem(CTabFolder parent, int style, YAxis yAxis) {
		super(parent, style);
		this.yAxis = yAxis;
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
		
		ComboViewer channelCombo = new ComboViewer(top);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalIndent = 7;
		channelCombo.getCombo().setLayoutData(gridData);
		
		Label normalizeLabel = new Label(top, SWT.NONE);
		normalizeLabel.setText("Normalize Channel:");
		
		ComboViewer normalizeCombo = new ComboViewer(top);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalIndent = 7;
		normalizeCombo.getCombo().setLayoutData(gridData);
		
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
		
		ComboViewer predefinedColorsCombo = new ComboViewer(colorComposite);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalIndent = 7;
		predefinedColorsCombo.getCombo().setLayoutData(gridData);
		
		Composite colorFieldEditorWrapper = new Composite(colorComposite, 
				SWT.NONE);
		gridLayout = new GridLayout(2, false);
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		colorFieldEditorWrapper.setLayout(gridLayout);
		
		ColorFieldEditor colorFieldEditor = new ColorFieldEditor("", "", 
				colorFieldEditorWrapper);
		
		Label linestyleLabel = new Label(top, SWT.NONE);
		linestyleLabel.setText("Linestyle:");
		
		ComboViewer linestyleCombo = new ComboViewer(top);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalIndent = 7;
		linestyleCombo.getCombo().setLayoutData(gridData);
		
		Label markstyleLabel = new Label(top, SWT.NONE);
		markstyleLabel.setText("Markstyle:");
		
		ComboViewer markstyleCombo = new ComboViewer(top);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalIndent = 7;
		markstyleCombo.getCombo().setLayoutData(gridData);
		
		Label scaletypeLabel = new Label(top, SWT.NONE);
		scaletypeLabel.setText("Scaletype:");
		
		ComboViewer scaletypeCombo = new ComboViewer(top);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalIndent = 7;
		scaletypeCombo.getCombo().setLayoutData(gridData);
		
		this.setControl(top);
	}

	public YAxis getYAxis() {
		return this.yAxis;
	}
}
