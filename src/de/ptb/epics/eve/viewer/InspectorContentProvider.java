package de.ptb.epics.eve.viewer;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class InspectorContentProvider implements IStructuredContentProvider {
	
	private List< String > myStrings;
	private int number;


	@Override
	public Object[] getElements(Object inputElement) {
		System.err.println("InspectorContentProvider getElements");
		return this.myStrings.toArray();
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

		System.err.println("InspectorContentProvider inputChanged");
		if (newInput == null) return;
		this.myStrings = new ArrayList< String >();
		for (int i = 0; i < 5; i++) {
			++ number;
			myStrings.add(new Integer(number).toString());
		}
	}
}
