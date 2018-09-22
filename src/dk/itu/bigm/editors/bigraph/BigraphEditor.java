package dk.itu.bigm.editors.bigraph;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.script.ScriptException;

import org.bigraph.model.Bigraph;
import org.bigraph.model.FormRules;
import org.bigraph.model.FormationRule;
import org.bigraph.model.changes.ChangeRejectedException;
import org.bigraph.model.changes.IChange;
import org.bigraph.model.changes.descriptors.BoundDescriptor;
import org.bigraph.model.loaders.LoadFailedException;
import org.bigraph.model.savers.BigraphXMLSaver;
import org.bigraph.model.savers.SaveFailedException;
import org.eclipse.core.resources.IFile;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
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
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
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
import dk.itu.bigm.editors.bigraph.actions.BigraphRelayoutAction;
import dk.itu.bigm.editors.bigraph.actions.ContainerCopyAction;
import dk.itu.bigm.editors.bigraph.actions.ContainerCutAction;
import dk.itu.bigm.editors.bigraph.actions.ContainerPasteAction;
import dk.itu.bigm.editors.bigraph.actions.ContainerPropertiesAction;
import dk.itu.bigm.editors.bigraph.actions.FilePrintAction;
import dk.itu.bigm.editors.bigraph.parts.PartFactory;
import dk.itu.bigm.utilities.resources.EclipseFileWrapper;
import ss.pku.utils.SaveInputAndOutput;

public class BigraphEditor extends AbstractGEFEditor 
implements PropertyChangeListener {
	public static final String ID = "dk.itu.bigm.BigraphEditor";
	
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
	
	public static final int INITIAL_SASH_WEIGHTS[] = { 20, 120 };
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
		(new Label(expComposite, SWT.NONE)).setText("expression:");
		expressionText = new Text(expComposite, SWT.BORDER);
		expressionText.setLayoutData(new GridData(SWT.FILL, SWT.NULL, true, false));
		expressionText.setMessage("请输入表达式");
		expressionText.addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent arg0) {
				String oldExpression = model.getAgentExpression();
				String newExpression = expressionText.getText();
				if (!oldExpression.equals(newExpression)) {					
					model.setAgentExpression(expressionText.getText());
					
					doChange(new BoundDescriptor(getModel(), 
							new Bigraph.ChangeExpressionDescriptor(
									new Bigraph.Identifier(), getModel(), oldExpression, newExpression)));
				}
			}

			@Override
			public void focusGained(FocusEvent e) {
				
			}
		});
		
		expressionText.addMouseMoveListener(new MouseMoveListener() {
			
			@Override
			public void mouseMove(MouseEvent e) {
				
			}
		});
		
		//edit by Allen Chen 2016/4/8 starts
		(new Label(expComposite, SWT.NONE)).setText("初始状态:");
		initialStateText = new Text(expComposite, SWT.BORDER);
		initialStateText.setLayoutData(new GridData(SWT.FILL, SWT.NULL, true, false));
		initialStateText.setMessage("请输入初始状态");
		initialStateText.addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent e) {
				String oldInitState = model.getInitialState();
				String newInitState = initialStateText.getText();
				if (!oldInitState.equals(newInitState)) {					
					model.setInitialState(initialStateText.getText());
					
					doChange(new BoundDescriptor(getModel(), 
							new Bigraph.ChangeInitStateDescriptor(
									new Bigraph.Identifier(), getModel(), oldInitState, newInitState)));
				} else {
					// 不做任何事
				}
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				
			}
		});
		//edit by Allen Chen 2016/4/8 ends		
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
    		return new BigraphEditorOutlinePage(this);
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
	/* Provisionally */ public DefaultEditDomain getEditDomain() {
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

	@Override
	protected void doActualSave(IFile f, OutputStream os)
			throws SaveFailedException {
		//!TODO 20170408
		boolean isOpen = true;
		try {
			if (isOpen && !getModel().validBySortingLogic()) {
				FormRules formRule = getModel().getSignature().getFormRules();
				FormationRule rule = formRule.getSortingLogic();
				if (rule == null) {
					System.out.println("error: no sorting logic but not satisfied?");
				} else {
					System.out.println("notice: sorting_logic \'" + rule.getSort1() + "\' is not satisfied.");
					throw new SaveFailedException("notice: sorting_logic \'" + rule.getSort1() + "\' is not satisfied.");
				}
			}
		} catch (ScriptException e) {
			e.printStackTrace();
		}
		// ends
		
		Bigraph b = getModel();
		BigraphXMLSaver r = new BigraphXMLSaver().setModel(b);
		SaveInputAndOutput.saveInitialState(); // 用于导出 excel add by Kevin Chan 用于增加输入和输出
		r.setFile(new EclipseFileWrapper(f)).
			setOutputStream(os).exportObject();
		// ends
//		if (canUndo()) {
//		}
		super.setSavePoint();
		getCommandStack().markSaveLocation();
//		super.textRevert();//TODO 暂时的 patch
	}

	@Override
	protected void loadModel() throws LoadFailedException {
		Bigraph b = (Bigraph)loadInput();
		model = b;
	}
	
	@Override
	protected void updateEditorControl() {
		if (getError() != null)
			return;
//		getCommandStack().flush();//TODO cancel comment
		updateNodePalette(model.getSignature());
		getGraphicalViewer().setContents(model);
		expressionText.setText(model.getAgentExpression());
		// add by Kevin Chan
		initialStateText.setText(model.getInitialState());
		// ends
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
//			System.out.println("**********************in updateEditor");
			loadModel();
		} catch (LoadFailedException e) {
			e.printStackTrace();
		}
		updateNodePalette(model.getSignature());
	}
	
	/**
	 *
	 * paper
	 * @author Kevin Chan
	 */
	protected void tryApplyChange(IChange c)
			throws ChangeRejectedException {
		getModel().tryApplyChange(c);
	};
	
	/**
	 *
	 * paper
	 * @author Kevin Chan
	 */
	public boolean doChange(IChange c) {
		return super.doChange(c);
	}

	/**
	 *
	 * paper
	 * @author Kevin Chan
	 */
	@Override
	public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent) {
		
	}
}
