package dk.itu.bigm.utilities.resources;

import java.util.ArrayList;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import dk.itu.bigm.application.plugin.BigMPlugin;
import dk.itu.bigm.utilities.ui.jface.ContainerViewerFilter;
import dk.itu.bigm.utilities.ui.jface.FileTypeViewerFilter;

/**
 * ResourceTreeSelectionDialogs are reasonably customisable dialogs designed
 * for selecting a single resource from a tree.
 * @author alec
 */
public class ResourceTreeSelectionDialog extends ElementTreeSelectionDialog {
	private static Object[] IContain; 
	private static final IStatus
		OK_STATUS = new Status(Status.OK, BigMPlugin.PLUGIN_ID, ""),
		ERROR_STATUS = new Status(Status.ERROR, BigMPlugin.PLUGIN_ID, "");
	
	private static final ISelectionStatusValidator
	getSelectionValidator(final Class<? extends IResource> klass) {
		return new ISelectionStatusValidator() {
			@Override
			public IStatus validate(Object[] selection) {
				IContain = selection;
				return 
					(selection.length >= 1 && klass.isInstance(selection[0]) ?
							OK_STATUS : ERROR_STATUS);
			}
		};
	}
	
	private static final ISelectionStatusValidator
		fileValidator = getSelectionValidator(IFile.class),
		containerValidator = getSelectionValidator(IContainer.class);
	
	public static enum Mode {
		GENERIC,
		CONTAINER,
		FILE
	};
	
	public static int CLEAR = 0x98765432;
	
	public ResourceTreeSelectionDialog(Shell parent, IContainer input, Mode mode, String... contentTypes) {
		super(parent,
				WorkbenchLabelProvider.getDecoratingWorkbenchLabelProvider(),
				new WorkbenchContentProvider());
		setAllowMultiple(true);
		setInput(input);
		if (mode == Mode.FILE) {
			setValidator(fileValidator);
			addFilter(new FileTypeViewerFilter(contentTypes));
		} else if (mode == Mode.CONTAINER) {
			setValidator(containerValidator);
			addFilter(new ContainerViewerFilter());
		}
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);
		createButton(parent, IDialogConstants.DESELECT_ALL_ID, "Clear", false);
	}
	
	@Override
	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.DESELECT_ALL_ID) {
			setResult(null);
			setReturnCode(CLEAR);
			close();
		} else super.buttonPressed(buttonId);
	}
	
	@Override
	public IResource getFirstResult() {
		return (IResource)super.getFirstResult();
	}
	
	public ArrayList getResultxuxu() {
		ArrayList <IResource> result = new ArrayList();
		for (Object i : super.getResult()){
			result.add((IResource) i);
		}
		return result;
	}
	
	/**
	 * {@inheritDoc}
	 * <p>(This overridden version will do nothing if passed <code>null</code>.) 
	 */
	@Override
	public void setInitialSelection(Object selection) {
		if (selection != null) {
			super.setInitialSelection(selection);
		} else super.setInitialSelections(new Object[0]);
	}
}
