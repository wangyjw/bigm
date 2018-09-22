package dk.itu.bigm.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import dk.itu.bigm.editors.AbstractGEFEditor;

public class NewSet implements IWorkbenchWindowActionDelegate{
	
	private IWorkbenchWindow window;
	
	@Override
	public void run(IAction action) {

	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
	}

	@Override
	public void dispose() {
	}

	@Override
	public void init(IWorkbenchWindow window) {
		this.window = window;		
	}

}
