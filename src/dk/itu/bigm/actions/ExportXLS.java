package dk.itu.bigm.actions;

import org.bigraph.model.SimulationSpec;
import org.bigraph.model.assistants.PropertyScratchpad;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;

public class ExportXLS implements IWorkbenchWindowActionDelegate {

	public static final String ID = "dk.itu.bigm.actions.ExportXLS";
	
	public static final String resultFolderPath = "D:\\BigMRulesData\\";
	
	public static final String resultFileName = "dictInfos.xls";
	
	public static final String agentPath = "D:\\download\\SmartJigWarehouse\\agents";
	
	public static final String rulePath = "D:\\download\\SmartJigWarehouse\\rules";
	
	@Override
	public void run(IAction action) {

	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(IWorkbenchWindow window) {
		// TODO Auto-generated method stub
		
	}
}
