package de.ptb.epics.eve.editor.dialogs.csvimport;

import java.util.List;

import javafx.util.Pair;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISharedImages;

import de.ptb.epics.eve.editor.Activator;
import de.ptb.epics.eve.util.io.StringUtil;

/**
 * @author Marcus Michalsky
 * @since 1.20
 */
public class CSVImportDialog extends TitleAreaDialog {
	private List<Pair<String, List<String>>> present;
	private List<Pair<String, List<String>>> absent;
	private List<Pair<String, List<String>>> invalid;
	
	public CSVImportDialog(Shell parentShell,
			List<Pair<String, List<String>>> present,
			List<Pair<String, List<String>>> absent,
			List<Pair<String, List<String>>> invalid) {
		super(parentShell);
		this.present = present;
		this.absent = absent;
		this.invalid = invalid;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void create() {
		super.create();
		this.setTitle("Import Axes from CSV");
		this.setMessage("Problems occured while importing axes from CSV file:");
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		Composite area = new Composite(container, SWT.NONE);
		GridLayout layout = new GridLayout(1, false);
		area.setLayout(layout);
		area.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		if (!invalid.isEmpty()) {
			Group invalidGroup = new Group(area, SWT.BORDER);
			invalidGroup.setLayout(new GridLayout());
			invalidGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
					true));

			CLabel invalidText = new CLabel(invalidGroup, SWT.NONE);
			invalidText.setImage(Activator.getDefault().getWorkbench()
					.getSharedImages()
					.getImage(ISharedImages.IMG_DEC_FIELD_ERROR));
			invalidText.setText("The following axes could not be found and will not be imported!");

			this.createViewer(invalidGroup, invalid);
		}
		
		if (!present.isEmpty()) {
			Group presentGroup = new Group(area, SWT.BORDER);
			presentGroup.setLayout(new GridLayout());
			presentGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
					true));

			CLabel absentText = new CLabel(presentGroup, SWT.NONE);
			absentText.setImage(Activator.getDefault().getWorkbench()
					.getSharedImages()
					.getImage(ISharedImages.IMG_DEC_FIELD_WARNING));
			absentText.setText("The following axes are already present and will be overwritten!");

			this.createViewer(presentGroup, present);
		}
		
		return container;
	}
	
	private TableViewer createViewer(Composite composite, Object input) {
		TableViewer viewer = new TableViewer(composite, SWT.FULL_SELECTION | SWT.NO_FOCUS | SWT.HIDE_SELECTION);
		viewer.getTable().setHeaderVisible(true);
		viewer.getTable().setLayoutData(
				new GridData(SWT.FILL, SWT.FILL, true, true));

		TableViewerColumn nameColumn = new TableViewerColumn(viewer, 
				SWT.NONE);
		nameColumn.getColumn().setText("name");
		nameColumn.getColumn().setWidth(150);
		nameColumn.setLabelProvider(new ColumnLabelProvider() {
			@SuppressWarnings("unchecked")
			@Override
			public String getText(Object element) {
				return ((Pair<String, List<String>>)element).getKey();
			}
		});
		
		TableViewerColumn valueColumn = new TableViewerColumn(viewer, 
				SWT.NONE);
		valueColumn.getColumn().setText("values");
		valueColumn.getColumn().setWidth(150);
		valueColumn.setLabelProvider(new ColumnLabelProvider() {
			@SuppressWarnings("unchecked")
			@Override
			public String getText(Object element) {
				return StringUtil.buildCommaSeparatedString(
						((Pair<String, List<String>>) element).getValue());
			}
		});
		
		viewer.setContentProvider(new ContentProvider());
		viewer.setInput(input);
		
		return viewer;
	}
	

	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean isResizable() {
		return true;
	}
}