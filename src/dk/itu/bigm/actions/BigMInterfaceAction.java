package dk.itu.bigm.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import dk.itu.bigm.action_bigminterface.BigMInterfaceBaseOnConfigDialog;

/**
 * Our sample action implements workbench action delegate.
 * The action proxy will be created by the workbench and
 * shown in the UI. When the user tries to use the action,
 * this delegate will be created and execution will be 
 * delegated to it.
 * @see IWorkbenchWindowActionDelegate
 */
public class BigMInterfaceAction implements IWorkbenchWindowActionDelegate{

	private IWorkbenchWindow window;

	public void run(IAction action) {
		BigMInterfaceBaseOnConfigDialog dialog = new BigMInterfaceBaseOnConfigDialog(window.getShell());
		dialog.open();			
	}
	
	public void selectionChanged(IAction action, ISelection selection) {
		
	}

	public void dispose() {
	}

	public void init(IWorkbenchWindow window) {
		this.window = window;
	}
}
