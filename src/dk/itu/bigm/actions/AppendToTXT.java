package dk.itu.bigm.actions;

import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.palette.CombinedTemplateCreationEntry;
import org.eclipse.gef.requests.SimpleFactory;
import org.eclipse.gef.tools.CreationTool;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;

import com.sun.media.jfxmediaimpl.platform.Platform;

import dk.itu.bigm.application.plugin.BigMPlugin;
import dk.itu.bigm.editors.bigraph.BigraphEditor;
import dk.itu.bigm.editors.bigraph.ModelFactory;

import java.util.ArrayList;
import java.util.List;

import org.bigraph.model.Control;
import org.bigraph.model.Edge;
import org.bigraph.model.InnerName;
import org.bigraph.model.NamedModelObject;
import org.bigraph.model.Node;
import org.bigraph.model.OuterName;
import org.bigraph.model.Root;
import org.bigraph.model.Signature;
import org.bigraph.model.Site;

import ss.pku.utils.FileFormatter;;

/**
 * 
 * 
 * 
 */
public class AppendToTXT implements IWorkbenchWindowActionDelegate {

	public static final String ID = "dk.itu.bigm.actions.AppendToTXT";
	
	private EditPartViewer viewer;
	
	private IWorkbenchWindow window;
	
	
	@Override
	public void run(IAction action) {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(); 
		BigraphEditor bigrahEditor = (BigraphEditor) page.getActiveEditor();
		String fileNameSuffix = bigrahEditor.getTitle();
		String remark = bigrahEditor.getModel().getInitialState();
		if (remark != null) {
			List<String> liStr = new ArrayList<String>();
			liStr.add(remark);
			FileFormatter.writeTextFile(liStr, "D:\\"+ fileNameSuffix +".txt");
		}
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
