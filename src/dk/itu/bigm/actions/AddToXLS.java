package dk.itu.bigm.actions;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;

import dk.itu.bigm.editors.bigraph.BigraphEditor;
import dk.itu.bigm.editors.rule.RuleEditor;
import ss.pku.utils.CreateExcel;
import ss.pku.utils.GenerateResult;

public class AddToXLS implements IWorkbenchWindowActionDelegate{

	public static final String ID = "dk.itu.bigm.actions.AddToXLS";
		
	@Override
	public void run(IAction action) {
		GenerateResult.autoRunMass();
//		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(); 
//		String editClass = page.getActiveEditor().getClass().toString();
//		String fileNameSuffix = page.getActiveEditor().getTitle();
//		List<String> liStr = new ArrayList<String>();
//		List<List<String>> liliStr = new ArrayList<List<String>>();
//		if (editClass.endsWith("RuleEditor")) {
//			RuleEditor ruleEditor = (RuleEditor) page.getActiveEditor();
//			String inputCase = ruleEditor.getModel().getInputCase();
//			String outputCase = ruleEditor.getModel().getOutputCase();
//			if (inputCase != null && outputCase != null) {
//				liStr.add(inputCase);
//				liStr.add(outputCase);
//				liliStr.add(liStr);
//				CreateExcel.createExcel("D:\\"+ fileNameSuffix +".xls", 
//						liliStr,
//						this.getAttrs());				
//			}
//		} else {
//			BigraphEditor bigrahEditor = (BigraphEditor) page.getActiveEditor();
//			String remark = bigrahEditor.getModel().getAgentRemark();			
//			if (remark != null) {
//				liStr.add(remark);
//				CreateExcel.createExcel("D:\\"+ fileNameSuffix +".xls", 
//						this.getAttrs(), 
//						this.getAttrs());
//			}
//		}
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

	public List<List<String>> getAttrs() {
		List<List<String>> l = new ArrayList<List<String>>();

		List<String> liOne = new ArrayList<String>();
		liOne.add("ÐÕÃû");
		liOne.add("Kevin Chan");
		l.add(liOne);
		List<String> liTwo = new ArrayList<String>();
		liTwo.add("ÈÕÆÚ");
		Date now = new Date();
		liTwo.add(now.toString());
		l.add(liTwo);
		return l;
	}
	
}
