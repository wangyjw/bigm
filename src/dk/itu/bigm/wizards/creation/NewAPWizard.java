package dk.itu.bigm.wizards.creation;

import org.bigraph.model.Bigraph;
import org.bigraph.model.Signature;
import org.bigraph.model.assistants.FileData;
import org.bigraph.model.savers.BigraphXMLSaver;
import org.bigraph.model.savers.SaveFailedException;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PartInitException;

import dk.itu.bigm.utilities.io.IOAdapter;
import dk.itu.bigm.utilities.resources.EclipseFileWrapper;
import dk.itu.bigm.utilities.resources.Project;
import dk.itu.bigm.utilities.resources.Project.ModificationRunner.Callback;
import dk.itu.bigm.utilities.ui.UI;
import ss.pku.bigm.wizards.creation.assistants.WizardNewAPCreationPage;

/**
 * NewAPWizards are responsible for creating atomic proposition in the form of {@link Bigraph} files within a
 * project.
 * @author Kevin Chan
 */
public class NewAPWizard extends Wizard implements INewWizard {
	private WizardNewAPCreationPage page = null;
	
	protected static Signature getSyntheticSignature(IFile sigFile) {
		Signature s = new Signature();
		FileData.setFile(s, new EclipseFileWrapper(sigFile));
		return s;
	}

	@Override
	public boolean performFinish() {
		IContainer c = page.getFolder();
		if (c != null) {
			try {
				IFile sigFile = page.getSignature();
				final IFile bigFile = c.getFile(new Path(page.getFileName()));
				
				final IOAdapter io = new IOAdapter();
				Bigraph b = new Bigraph();
				
				b.setSignature(getSyntheticSignature(sigFile));
				BigraphXMLSaver r = new BigraphXMLSaver();
				r.setFile(new EclipseFileWrapper(bigFile)).setModel(b).
					setOutputStream(io.getOutputStream()).exportObject();
				Project.setContents(bigFile, io.getInputStream(),
						new Callback() {
					@Override
					public void onSuccess() {
						try {
							UI.openInEditor(bigFile);
						} catch (PartInitException pie) {
							/* ? */
							pie.printStackTrace();
						}
					}
				});
				
				return true;
			} catch (SaveFailedException e) {
				page.setErrorMessage(e.getLocalizedMessage());
			}
		}
		return false;

	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		page = new WizardNewAPCreationPage("newAPWizardPage", selection);
		
		page.setTitle("atomic proposition");
		page.setDescription("Create a new atomic proposition in an existing bigraphical reactive system.");
		
		addPage(page);
	}	
}
