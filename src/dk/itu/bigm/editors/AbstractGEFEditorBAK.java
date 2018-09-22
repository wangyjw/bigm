package dk.itu.bigm.editors;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

import org.bigraph.model.Bigraph;
import org.bigraph.model.Control;
import org.bigraph.model.Edge;
import org.bigraph.model.InnerName;
import org.bigraph.model.NamedModelObject;
import org.bigraph.model.Node;
import org.bigraph.model.OuterName;
import org.bigraph.model.Root;
import org.bigraph.model.Signature;
import org.bigraph.model.Site;
import org.bigraph.model.assistants.BigmProperty;
import org.bigraph.model.changes.IChange;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.RootEditPart;
import org.eclipse.gef.SharedImages;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.commands.CommandStackEvent;
import org.eclipse.gef.commands.CommandStackEventListener;
import org.eclipse.gef.editparts.ScalableRootEditPart;
import org.eclipse.gef.palette.CombinedTemplateCreationEntry;
import org.eclipse.gef.palette.ConnectionCreationToolEntry;
import org.eclipse.gef.palette.MarqueeToolEntry;
import org.eclipse.gef.palette.PaletteContainer;
import org.eclipse.gef.palette.PaletteGroup;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.PaletteSeparator;
import org.eclipse.gef.palette.SelectionToolEntry;

import org.eclipse.gef.ui.palette.customize.DefaultEntryPage;

import org.eclipse.gef.ui.palette.PaletteViewer;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.INullSelectionListener;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.PropertySheetPage;

import dk.itu.bigm.application.plugin.BigMPlugin;
import dk.itu.bigm.editors.assistants.ControlImageDescriptor;
import dk.itu.bigm.editors.bigraph.ModelFactory;
import dk.itu.bigm.editors.bigraph.NodeFactory;
import dk.itu.bigm.editors.utilities.RedPropertySheetEntry;
import dk.itu.bigm.gef.MarqueeAndSelectionToolEntry;

public abstract class AbstractGEFEditorBAK extends AbstractEditor
		implements CommandStackEventListener, ISelectionListener,
		INullSelectionListener {
	public static final String PROPERTY_DISPLAY_GUIDES =
			"dk.itu.bigm.editors.AbstractGEFEditor.propertyDisplayGuides";
	
	private DefaultEditDomain editDomain;
	
	private List<String> selectionActions;
	
	private Signature sig;
	
	/**
	 * Returns the list of <i>selection actions</i>, those actions which want
	 * to be updated when the editor's selection changes.
	 * @return a list of action IDs
	 * @see AbstractEditor#updateActions(List)
	 */
	protected List<String> getSelectionActions() {
		if (selectionActions == null)
			selectionActions = new ArrayList<String>();
		return selectionActions;
	}
	
	/**
	 * Returns the {@link ScalableRootEditPart} of a given {@link
	 * GraphicalViewer}, if it has one.
	 * @param v a {@link GraphicalViewer}; must not be <code>null</code>
	 * @return a {@link ScalableRootEditPart}; can be <code>null</code>
	 */
	protected static ScalableRootEditPart getScalableRoot(GraphicalViewer v) {
		if (v != null) {
			RootEditPart r = v.getRootEditPart();
			return (r instanceof ScalableRootEditPart ?
					(ScalableRootEditPart)r : null);
		} else return null;
	}
	
	/**
	 * Assigns a {@link DefaultEditDomain} to this editor, and sets the
	 * editor's palette root into it.
	 * @param editDomain a {@link DefaultEditDomain}
	 */
	protected void setEditDomain(DefaultEditDomain editDomain) {
		this.editDomain = editDomain;
		getEditDomain().setPaletteRoot(getPaletteRoot());
	}
	
	/**
	 * Returns this editor's {@link DefaultEditDomain}.
	 * @return a {@link DefaultEditDomain}
	 */
	protected DefaultEditDomain getEditDomain() {
		return editDomain;
	}
	
	public AbstractGEFEditorBAK() {
		setEditDomain(new DefaultEditDomain(this));
	}
	
	/**
	 * Returns the {@link CommandStack} associated with this editor's edit
	 * domain.
	 * @return a {@link CommandStack}
	 */
	public CommandStack getCommandStack() {
		return getEditDomain().getCommandStack();
	}
	
	@Override
	public boolean isDirty() {
		return getCommandStack().isDirty();
	}
	
	/**
	 * Creates the {@link PaletteViewer} for this editor, creating its control
	 * and setting it into the edit domain.
	 * @param parent the {@link Composite} that should contain the {@link
	 * PaletteViewer}'s control
	 */
	public static final int INITIAL_SASH_WEIGHTS[] = { 10, 20 };
	protected void createPaletteViewer(Composite parent) {
		
		SashForm splitter = new SashForm(parent, SWT.VERTICAL | SWT.SMOOTH);
		
		
		final Text text1 = new Text(splitter, SWT.NULL);
		//splitter.setWeights(INITIAL_SASH_WEIGHTS);
		splitter.setSize(20, 20);
		PaletteViewer viewer = new PaletteViewer();
		setPaletteViewer(viewer);
	
		text1.addModifyListener(new ModifyListener() {  
            public void modifyText(ModifyEvent e) {  
            	
				String temp  =  text1.getText();
				
				if (temp.equals("") || temp.equals(null)) {
					updateNodePalette(sig);
				}
				
				Signature ss = sig; 
				ArrayList<Control> list = new ArrayList<Control>();
				for (Control c : ss.getControls()) {
					if (c.getName().toLowerCase().contains(temp)){
						list.add(c);

					}
				}
				getNodeGroup().getChildren().clear();
				for(int i = 0 ; i < list.size() ; i++ ){
					getNodeGroup().add(new CombinedTemplateCreationEntry((list.get(i)).getName(),"Node",
					Node.class, new NodeFactory(list.get(i)),
					new ControlImageDescriptor(list.get(i), 16, 16),
					new ControlImageDescriptor(list.get(i), 48, 48)));
				}

            }
 
        });
		

		//label  Text 
		
		viewer.createControl(splitter);
		getEditDomain().setPaletteViewer(getPaletteViewer());
	}
	
	private PaletteViewer paletteViewer;
	
	protected void setPaletteViewer(PaletteViewer paletteViewer) {
		this.paletteViewer = paletteViewer;
	}
    
	protected PaletteViewer getPaletteViewer() {
		return paletteViewer;
	}
	
	@Override
	public void stackChanged(CommandStackEvent event) {
		stateChanged();
	}
	
	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		super.init(site, input);
		getSite().getWorkbenchWindow().getSelectionService().
			addSelectionListener(this);
		getCommandStack().addCommandStackEventListener(this);
	}
	
	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if (equals(getSite().getPage().getActiveEditor())){
			//updateEditor();
			updateActions(getSelectionActions());			
		}
	}
	
//	public void textChaged
	
	@Override
	public void dispose() {
		getSite().getWorkbenchWindow().getSelectionService().
			removeSelectionListener(this);
		getCommandStack().removeCommandStackEventListener(this);
		super.dispose();
	}
	
	@Override
	public boolean canRedo() {
		return getCommandStack().canRedo();
	}
	
	@Override
	public void redo() {
		getCommandStack().redo();
	}
	
	@Override
	public boolean canUndo() {
		return getCommandStack().canUndo();
	}
	
	@Override
	public void undo() {
		getCommandStack().undo();
	}
	
	@Override
	public boolean canRevert() {
		return getCommandStack().isDirty();
	}
	
	@Override
	public void revert() {
		CommandStack cs = getCommandStack();
		while (cs.canUndo())
			cs.undo();
		cs.flush();
		stateChanged();
	}
	
	@Override
	@SuppressWarnings("rawtypes")
	public Object getAdapter(Class adapter) {
		if (adapter == CommandStack.class) {
			return getCommandStack();
		} else if (adapter == IPropertySheetPage.class) {
    		PropertySheetPage psp = new PropertySheetPage();
    		psp.setRootEntry(new RedPropertySheetEntry(getCommandStack()));
    		return psp;
    	} else return super.getAdapter(adapter);
	}
	
	public static <T extends PaletteContainer> T populatePalette(
			T container, PaletteGroup nodeGroup,
			SelectionToolEntry defaultTool) {
    	PaletteGroup selectGroup = new PaletteGroup("Object selection");
		selectGroup.setId("BigraphEditor.palet	te.selection");
		container.add(selectGroup);
		
		selectGroup.add((defaultTool != null ? defaultTool : new SelectionToolEntry()));
		
		// a new select
		selectGroup.add(new MarqueeAndSelectionToolEntry("test", "test", SharedImages.DESC_SELECTION_TOOL_16, SharedImages.DESC_SELECTION_TOOL_24));
		
		selectGroup.add(new MarqueeToolEntry());
		
		container.add(new PaletteSeparator());
		
		PaletteGroup creationGroup = new PaletteGroup("Object creation");
		creationGroup.setId("BigraphEditor.palette.creation");
		container.add(creationGroup);

		ImageDescriptor
			site = BigMPlugin.getImageDescriptor("resources/icons/bigraph-palette/site.png"),
			root = BigMPlugin.getImageDescriptor("resources/icons/bigraph-palette/root.png"),
			edge = BigMPlugin.getImageDescriptor("resources/icons/bigraph-palette/edge.png");
		
		creationGroup.add(new CombinedTemplateCreationEntry("Site", "Add a new site to the bigraph",
				Site.class, new ModelFactory(Site.class), site, site));
		creationGroup.add(new CombinedTemplateCreationEntry("Root", "Add a new root to the bigraph",
				Root.class, new ModelFactory(Root.class), root, root));
		creationGroup.add(new ConnectionCreationToolEntry("Link", "Connect two points with a link",
				new ModelFactory(Edge.class), edge, edge));
		
		ImageDescriptor
			inner = BigMPlugin.getImageDescriptor("resources/icons/bigraph-palette/inner.png"),
			outer = BigMPlugin.getImageDescriptor("resources/icons/bigraph-palette/outer.png");
		
		creationGroup.add(new CombinedTemplateCreationEntry("Inner name", "Add a new inner name to the bigraph",
				InnerName.class, new ModelFactory(InnerName.class), inner, inner));
		creationGroup.add(new CombinedTemplateCreationEntry("Outer name", "Add a new outer name to the bigraph",
				OuterName.class, new ModelFactory(OuterName.class), outer, outer));
		
		creationGroup.add(new PaletteSeparator());
		
		if (nodeGroup == null)
			nodeGroup = new PaletteGroup("Node...");
		nodeGroup.setId("BigraphEditor.palette.node-creation");
		creationGroup.add(nodeGroup);
		
    	return container;
    }
	
	private PaletteGroup nodeGroup;
    
	protected PaletteGroup getNodeGroup() {
		if (nodeGroup == null)
			nodeGroup = new PaletteGroup("Node...");
		return nodeGroup;
	}
	
	protected PaletteRoot createPaletteRoot() {
		PaletteRoot root = new PaletteRoot();
		SelectionToolEntry ste = new SelectionToolEntry();
		
		populatePalette(root, getNodeGroup(), ste);
		
		root.setDefaultEntry(ste);
		return root;
	}
	
	private PaletteRoot paletteRoot;
	
	protected PaletteRoot getPaletteRoot() {
		if (paletteRoot == null)
			paletteRoot = createPaletteRoot();
		return paletteRoot;
	}
	
	private static PaletteContainer sigPop(
			List<String> names, PaletteContainer pc, Signature signature) {
		pc.setLabel(signature.toString());
		for (Control c : signature.getControls()) {
			if (names.contains(c.getName()))
				continue;
			names.add(c.getName());
			pc.add(new CombinedTemplateCreationEntry(c.getName(),"Node",
					Node.class, new NodeFactory(c),
					new ControlImageDescriptor(c, 16, 16),
					new ControlImageDescriptor(c, 48, 48)));
		}
		
		for (Signature s : signature.getSignatures()) {
			PaletteContainer cpc = new PaletteGroup(null);
			sigPop(names, cpc, s);
			if (cpc.getChildren().size() != 0)
				pc.add(cpc);
		}
		return pc;
	}
	
	protected void updateNodePalette(Signature signature) {
		getNodeGroup().getChildren().clear();
		sigPop(new ArrayList<String>(), getNodeGroup(), signature);
		sig = signature;
	}
	
	public abstract void updateEditor();
}
