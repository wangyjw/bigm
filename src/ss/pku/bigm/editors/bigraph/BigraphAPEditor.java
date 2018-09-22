package ss.pku.bigm.editors.bigraph;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.bigraph.model.Bigraph;
import org.bigraph.model.Layoutable;
import org.bigraph.model.Node;
import org.bigraph.model.Root;
import org.bigraph.model.changes.ChangeRejectedException;
import org.bigraph.model.changes.IChange;
import org.bigraph.model.loaders.LoadFailedException;
import org.bigraph.model.savers.BigraphXMLSaver;
import org.bigraph.model.savers.SaveFailedException;
import org.eclipse.core.resources.IFile;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

import dk.itu.bigm.editors.AbstractGEFEditor;
import dk.itu.bigm.editors.actions.TogglePropertyAction;
import dk.itu.bigm.editors.bigraph.BigraphEditorContextMenuProvider;
import dk.itu.bigm.editors.bigraph.actions.BigraphRelayoutAction;
import dk.itu.bigm.editors.bigraph.actions.ContainerCopyAction;
import dk.itu.bigm.editors.bigraph.actions.ContainerCutAction;
import dk.itu.bigm.editors.bigraph.actions.ContainerPasteAction;
import dk.itu.bigm.editors.bigraph.actions.ContainerPropertiesAction;
import dk.itu.bigm.editors.bigraph.actions.FilePrintAction;
import dk.itu.bigm.editors.bigraph.parts.PartFactory;
import dk.itu.bigm.utilities.resources.EclipseFileWrapper;

import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.KeyHandler;
import org.eclipse.gef.KeyStroke;
import org.eclipse.gef.MouseWheelHandler;
import org.eclipse.gef.MouseWheelZoomHandler;
import org.eclipse.gef.editparts.ScalableRootEditPart;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.ui.actions.DeleteAction;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.gef.ui.actions.SelectAllAction;
import org.eclipse.gef.ui.actions.ToggleGridAction;
import org.eclipse.gef.ui.actions.ToggleSnapToGeometryAction;
import org.eclipse.gef.ui.actions.ZoomInAction;
import org.eclipse.gef.ui.actions.ZoomOutAction;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
import org.eclipse.gef.ui.parts.SelectionSynchronizer;
import org.eclipse.jface.dialogs.MessageDialog;

public class BigraphAPEditor extends AbstractGEFEditor {
	public static final String ID = "ss.pku.bigm.BigraphAPEditor";
	
	private Bigraph model;
	private KeyHandler keyHandler;
	
	@Override
	public void dispose() {
		getEditDomain().setActiveTool(null);
		super.dispose();
	}	

	public static final List<String> STOCK_ZOOM_CONTRIBUTIONS =
			new ArrayList<String>();
	static {
		STOCK_ZOOM_CONTRIBUTIONS.add(ZoomManager.FIT_ALL);
		STOCK_ZOOM_CONTRIBUTIONS.add(ZoomManager.FIT_HEIGHT);
		STOCK_ZOOM_CONTRIBUTIONS.add(ZoomManager.FIT_WIDTH);
	}
	
	public static final double[] STOCK_ZOOM_LEVELS = new double[] {
			0.25, 0.5, 0.75, 1.0, 1.5, 2.0, 2.5, 3.0, 4.0, 5.0, 10.0, 20.0 
	};

	
	protected void configureGraphicalViewer() {
    	getGraphicalViewer().getControl().setBackground(
				ColorConstants.listBackground);
    	
	    GraphicalViewer viewer = getGraphicalViewer();
	    viewer.setEditPartFactory(new PartFactory());
	    
	    ScalableRootEditPart rootEditPart = new ScalableRootEditPart();
	    viewer.setRootEditPart(rootEditPart);
	    
	    ZoomManager manager = rootEditPart.getZoomManager();
	    registerActions(null,
	    		new ZoomInAction(manager), new ZoomOutAction(manager));
	    manager.setZoomLevels(STOCK_ZOOM_LEVELS);
	    manager.setZoomLevelContributions(STOCK_ZOOM_CONTRIBUTIONS);
	     
	    keyHandler = new KeyHandler();
	    keyHandler.put(KeyStroke.getPressed(SWT.DEL, SWT.DEL, 0),
	    	getActionRegistry().getAction(ActionFactory.DELETE.getId()));
	    keyHandler.put(KeyStroke.getPressed('+', SWT.KEYPAD_ADD, 0),
	    	getActionRegistry().getAction(GEFActionConstants.ZOOM_IN));
	    keyHandler.put(KeyStroke.getPressed('-', SWT.KEYPAD_SUBTRACT, 0),
	    	getActionRegistry().getAction(GEFActionConstants.ZOOM_OUT));
	    viewer.setKeyHandler(keyHandler);
	    
	    viewer.setProperty(MouseWheelHandler.KeyGenerator.getKey(SWT.CTRL),
	    	MouseWheelZoomHandler.SINGLETON);
	     
	    viewer.setContextMenu(
	    	new BigraphEditorContextMenuProvider(viewer, getActionRegistry()));
	    
	    registerActions(null,
	    		new ToggleGridAction(getGraphicalViewer()),
	    		new ToggleSnapToGeometryAction(getGraphicalViewer()),
	    		new TogglePropertyAction(
	    				PROPERTY_DISPLAY_GUIDES, true, getGraphicalViewer()));
	}
	
	@Override
    public void createActions() {
    	registerActions(null, new SelectAllAction(this));
    	
    	/*
    	 * Note to self: actions which are conditionally enabled only when
    	 * certain items are selected must be registered with
    	 * getSelectionActions(), actions which are conditionally enabled when
    	 * the editor state changes must be registered with getStackActions(),
    	 * and I have no idea at all what ActionBarContributors do.
    	 */
    	
    	registerActions(getSelectionActions(),
    		new ContainerPropertiesAction(this), new ContainerCutAction(this),
    		new ContainerCopyAction(this), new ContainerPasteAction(this),
    		new BigraphRelayoutAction(this),
    		new DeleteAction((IWorkbenchPart)this));
    	
    	/*
    	 * Does this kind of action need to be registered in the
    	 * ActionRegistry? What does the ActionRegistry *do*, anyway? (Are most
    	 * Eclipse projects comprised primarily of comments saying "What does
    	 * the <insert name here> *do*, anyway?"?)
    	 */
    	setGlobalActionHandlers(registerActions(null,
    			new FilePrintAction(this)));
    }
	
	public static final int INITIAL_SASH_WEIGHTS[] = { 20, 80 };
	
	private Text expressionText;
	
	//add by Allen Chen 2016-04-08 16:47:00 starts
	private Text initialStateText;
	//add by Allen Chen 2016-04-08 16:47:00 ends
	
    @Override
	public void createEditorControl(Composite parent) {
		SashForm splitter = new SashForm(parent, SWT.HORIZONTAL | SWT.SMOOTH);
		createPaletteViewer(splitter);

		Composite c = new Composite(splitter, SWT.NONE);
		splitter.setWeights(INITIAL_SASH_WEIGHTS);
		
		GridLayout gl = new GridLayout(1, false);
		gl.marginTop = gl.marginLeft = gl.marginBottom = gl.marginRight = gl.horizontalSpacing = gl.verticalSpacing = 10;
		c.setLayout(gl);
		
		createGraphicalViewer(c);
		
		// expression
		Composite expComposite = new Composite(c, SWT.NONE);
		expComposite.setLayoutData(new GridData(SWT.FILL, SWT.NULL, true, false));
		expComposite.setLayout(new GridLayout(2, false));

		(new Label(expComposite, SWT.RIGHT)).setText("expression:");
		expressionText = new Text(expComposite, SWT.BORDER);
		expressionText.setLayoutData(new GridData(SWT.FILL, SWT.NULL, true, false));
		expressionText.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent arg0) {
				// TODO Auto-generated method stub
				//System.out.println("nahe++ expressionText has been modified! now is " + expressionText.getText());
				model.setAgentExpression(expressionText.getText());
			}
		});
		
		//edit by Allen Chen 2016/4/8 starts
		(new Label(expComposite, SWT.RIGHT)).setText("初始状态:");
		initialStateText = new Text(expComposite, SWT.BORDER|SWT.MULTI|SWT.V_SCROLL);
		initialStateText.setLayoutData(new GridData(SWT.FILL, SWT.NULL, true, false));
		initialStateText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent arg0) {
				
				model.setInitialState(initialStateText.getText());
			}
		});
		//edit by Allen Chen 2016/4/8 ends
		
		//!TODO
//		(new Label(expComposite, SWT.RIGHT)).setText("请确保是原子命题");
	}
    
    protected void createGraphicalViewer(Composite parent) {
		GraphicalViewer viewer = new ScrollingGraphicalViewer();
		viewer.createControl(parent);
		viewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		setGraphicalViewer(viewer);
		configureGraphicalViewer();
		
		getSelectionSynchronizer().addViewer(getGraphicalViewer());
		getSite().setSelectionProvider(getGraphicalViewer());
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Object getAdapter(Class type) {
    	if (type == ZoomManager.class) {
    		ScalableRootEditPart sep = getScalableRoot(getGraphicalViewer());
    		return (sep != null ? sep.getZoomManager() : null);
    	} else if (type == IContentOutlinePage.class && getModel() != null) {
    		return new BigraphAPEditorOutlinePage(this);
    	} else if (type == GraphicalViewer.class) {
			return getGraphicalViewer();
    	} else if (type == EditPart.class && getGraphicalViewer() != null) {
			return getGraphicalViewer().getRootEditPart();
    	} else if (type == IFigure.class && getGraphicalViewer() != null) {
    		ScalableRootEditPart sep = getScalableRoot(getGraphicalViewer());
			return (sep != null ? sep.getFigure() : null);
		} else return super.getAdapter(type);
    }
    
	@Override
	/* Provisionally */ 
	public DefaultEditDomain getEditDomain() {
		return super.getEditDomain();
	}
	
	@Override
	public Bigraph getModel() {
		return model;
	}
	
	private GraphicalViewer graphicalViewer;
	
	protected GraphicalViewer getGraphicalViewer() {
		return graphicalViewer;
	}
	
	protected void setGraphicalViewer(GraphicalViewer viewer) {
		getEditDomain().addViewer(viewer);
		graphicalViewer = viewer;
	}
	
	private SelectionSynchronizer selectionSynchronizer;
	
	protected SelectionSynchronizer getSelectionSynchronizer() {
		if (selectionSynchronizer == null)
			selectionSynchronizer = new SelectionSynchronizer();
		return selectionSynchronizer;
	}

	public boolean isAtomicPropValid() throws SaveFailedException {
		SaveFailedException exception = new SaveFailedException();
		Bigraph ap = getModel();
		if (ap.getChildren().size() > 2) {
			MessageDialog.openWarning(null, "Wrong", "This proposition must be atomic");
			throw new SaveFailedException("This proposition must be atomic");
		} else if (ap.getChildren().size() == 2) {
			for (Layoutable l : ap.getChildren()) {
				if (l instanceof Root) {
					Root r = (Root) l;
					if (r.getChildren().size() != 1) {
						MessageDialog.openWarning(null, "Wrong", "Not conform to the rules");
						throw new SaveFailedException("Not conform to the rules");
					}
				}
			}						
		} else {
			for (Layoutable l : ap.getChildren()) {
				if (l instanceof Root) {
					Root r = (Root) l;
					if (r.getChildren().size() != 1) {
						MessageDialog.openWarning(null, "Wrong", "This proposition can only cantain one Node or two Nodes");
						throw new SaveFailedException("This proposition can only cantain one Node or two Nodes");
					} else {
						Node parent = (Node) r.getChildren().get(0);
						if (parent == null || parent.getChildren().size() != 1) {
							MessageDialog.openWarning(null, "Wrong", "Not conform to the rules");
							throw new SaveFailedException("Not conform to the rules");							
						} else {
							Node son = (Node) parent.getChildren().get(0);
							if (son.getChildren().size() != 0) {
								MessageDialog.openWarning(null, "Wrong", "Not conform to the rules");
								throw new SaveFailedException("Not conform to the rules");															
							}
						}
					}
				}
			}			
		}
		return true;		
	}
	
	@Override
	protected void doActualSave(IFile f, OutputStream os)
			throws SaveFailedException {		

		model.getExpression();
//		getEx
//		if (!isAtomicPropValid()) {
//			return;
//		}
		
		BigraphXMLSaver r = new BigraphXMLSaver().setModel(getModel());
		r.setFile(new EclipseFileWrapper(f)).
			setOutputStream(os).exportObject();

		getCommandStack().markSaveLocation();
	}

	@Override
	protected void loadModel() throws LoadFailedException {
		model = (Bigraph)loadInput();
	}
	
	@Override
	protected void updateEditorControl() {
		if (getError() != null)
			return;
		getCommandStack().flush();
		updateNodePalette(model.getSignature());
		getGraphicalViewer().setContents(model);
		expressionText.setText(model.getAgentExpression());
		initialStateText.setText(model.getInitialState());
		//！TODO
	}
	
	@Override
	public void setFocus() {
		super.setFocus();
		GraphicalViewer gv = getGraphicalViewer();
		if (gv != null) {
			Control c = gv.getControl();
			if (c != null && !c.isDisposed())
				c.setFocus();
		}
	}
	
	@Override
	public void updateEditor() {
		try {
			System.out.println("**********************in updateEditor");
			loadModel();
		} catch (LoadFailedException e) {
			e.printStackTrace();
		}
		updateNodePalette(model.getSignature());
	}

	@Override
	protected void tryApplyChange(IChange c) throws ChangeRejectedException {
		// TODO Auto-generated method stub
		
	}
}
