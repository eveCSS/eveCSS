package de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes.file.ui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogLabelKeys;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.ui.DialogCellEditorDialog;
import de.ptb.epics.eve.util.io.FileUtil;
import de.ptb.epics.eve.util.io.StringUtil;

/**
 * @author Marcus Michalsky
 * @since 1.34
 */
public class FileDialogCellEditor extends DialogCellEditor {
	private Axis axis;
	
	public FileDialogCellEditor(Composite parent, Axis axis) {
		super(parent, SWT.NONE);
		this.axis = axis;
	}
	
	/*@Override
	protected Control createContents(Composite cell) {
		Text text = new Text(cell, SWT.NONE);
		return text;
	}*/
	
	@Override
	protected void doSetFocus() {
		//openDialogBox(getControl());
		// TODO Auto-generated method stub
		super.doSetFocus();
		//openDialogBox(getControl());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Object openDialogBox(Control cellEditorWindow) {
		PositionFileDialog dialog = new PositionFileDialog(
				cellEditorWindow.getShell(), cellEditorWindow, axis);
		dialog.open();
		return axis.getFile().getAbsolutePath();
		//focusLost();
		// TODO Auto-generated method stub
		//return null;
	}
}
