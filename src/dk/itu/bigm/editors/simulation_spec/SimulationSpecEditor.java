package dk.itu.bigm.editors.simulation_spec;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;

import org.bigraph.model.Bigraph;
import org.bigraph.model.BigraphAP;
import org.bigraph.model.ModelObject;
import org.bigraph.model.ReactionRule;
import org.bigraph.model.Signature;
import org.bigraph.model.SimulationSpec;
import org.bigraph.model.assistants.FileData;
import org.bigraph.model.assistants.PropertyScratchpad;
import org.bigraph.model.changes.ChangeGroup;
import org.bigraph.model.changes.ChangeRejectedException;
import org.bigraph.model.changes.IChange;
import org.bigraph.model.changes.descriptors.BoundDescriptor;
import org.bigraph.model.loaders.LoadFailedException;
import org.bigraph.model.resources.IFileWrapper;
import org.bigraph.model.savers.SaveFailedException;
import org.bigraph.model.savers.Saver;
import org.bigraph.model.savers.SimulationSpecXMLSaver;
import org.bigraph.model.wrapper.SaverUtilities;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.RegistryFactory;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.FileEditorInput;

import dk.itu.bigm.editors.AbstractNonGEFEditor;
import dk.itu.bigm.editors.assistants.IFactory;
import dk.itu.bigm.interaction_managers.IInteractionManager;
import dk.itu.bigm.interaction_managers.InteractionManager;
import dk.itu.bigm.utilities.resources.EclipseFileWrapper;
import dk.itu.bigm.utilities.resources.ResourceTreeSelectionDialog;
import dk.itu.bigm.utilities.resources.ResourceTreeSelectionDialog.Mode;
import dk.itu.bigm.utilities.ui.ResourceSelector;
import dk.itu.bigm.utilities.ui.StockButton;
import dk.itu.bigm.utilities.ui.ResourceSelector.ResourceListener;

public class SimulationSpecEditor extends AbstractNonGEFEditor
		implements PropertyChangeListener {
	private Text formulaText;
	
	private static class ExportInteractionManagerFactory
			extends ConfigurationElementInteractionManagerFactory {
		public ExportInteractionManagerFactory(IConfigurationElement ice) {
			super(ice);
		}
		
		@Override
		public IInteractionManager newInstance() {
			Saver s;
			try {
				if(getCE().createExecutableExtension("class") instanceof Saver){
					s = (Saver)getCE().createExecutableExtension("class");
					return new BasicCommandLineInteractionManager(s);
				} else if(getCE().createExecutableExtension("class") instanceof InteractionManager){
					return (InteractionManager)getCE().createExecutableExtension("class");
				}
			} catch (CoreException e) {
				return null;
			}
			return null;
		}
	}
	
	@Override
	protected void tryApplyChange(IChange c) throws ChangeRejectedException {
		getModel().tryApplyChange(c);
	}
	
	@Override
	public void doActualSave(IFile f, OutputStream os)
			throws SaveFailedException {
    	SimulationSpecXMLSaver r = new SimulationSpecXMLSaver().setModel(getModel());
		r.setFile(new EclipseFileWrapper(f)).
    		setOutputStream(os).exportObject();
    	setSavePoint();
	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		super.init(site, input);
		firePropertyChange(PROP_INPUT);
	}
	
	private SimulationSpec model = null;
	
	@Override
	protected SimulationSpec getModel() {
		return model;
	}
	
	private boolean uiUpdateInProgress = false;
	
	private static final IFile getFileFrom(ModelObject m) {
		IFileWrapper fw = FileData.getFile(m);
		return (fw instanceof EclipseFileWrapper ?
				((EclipseFileWrapper)fw).getResource() : null);
	}
	
	@Override
	protected void loadModel() throws LoadFailedException {
		model = (SimulationSpec)loadInput();
	}
	
	@Override
	protected void updateEditorControl() {
		if (getError() != null)
			return;
		clearUndo();
		rules.setInput(model);
		props.setInput(model); // add by Kevin Chan
		model.addPropertyChangeListener(this);
		modelToControls();
	}
	
	private void modelToControls() {
		uiUpdateInProgress = true;
		
		modelSelector.setResource(getFileFrom(model.getModel()));
		signatureSelector.setResource(getFileFrom(model.getSignature()));
		
		// add by Kevin Chan
		patternSelector.setResource(getFileFrom(model.getPattern()));
		errorSelector.setResource(getFileFrom(model.getError()));
		// ends
		
		recalculateExportEnabled();
		uiUpdateInProgress = false;
	}
	
	private static ArrayList<IFactory<IInteractionManager>> getIMFactories() {
		ArrayList<IFactory<IInteractionManager>> factories =
				new ArrayList<IFactory<IInteractionManager>>();
		
		IExtensionRegistry r = RegistryFactory.getRegistry();
		for (IConfigurationElement ce :
				r.getConfigurationElementsFor(
						IInteractionManager.EXTENSION_POINT))
			factories.add(new ConfigurationElementInteractionManagerFactory(ce));
		
		for (IConfigurationElement ce :
				r.getConfigurationElementsFor(
						SaverUtilities.EXTENSION_POINT)) {
			String exports = ce.getAttribute("exports");
			if (SimulationSpec.class.getCanonicalName().equals(exports))
				factories.add(new ExportInteractionManagerFactory(ce));
		}
		return factories;
	}
	
	private ResourceSelector signatureSelector, modelSelector, patternSelector, errorSelector;
	private ListViewer rules;
	
	/**
	 * @author Kevin Chan 
	 */
	private ListViewer props;
	
	
	public ListViewer getProps() {
		return props;
	}
	// ends
	private Button export;
	
	private void recalculateExportEnabled() {
		export.setEnabled(
			getModel().getModel() != null &&
			getModel().getSignature() != null);
	}
	
	@Override
	public void createEditorControl(Composite parent) {
		Composite self = new Composite(parent, SWT.NONE);
		GridLayout gl = new GridLayout(3, false);
		gl.marginTop = gl.marginLeft = gl.marginBottom = gl.marginRight = 
			gl.horizontalSpacing = gl.verticalSpacing = 10;
		self.setLayout(gl);
		
		Label l;
		(l = new Label(self, SWT.RIGHT)).setText("Signature:");
		l.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		signatureSelector = new ResourceSelector(self,
			((FileEditorInput)getEditorInput()).getFile().getProject(),
			Mode.FILE, Signature.CONTENT_TYPE);
		signatureSelector.getButton().setLayoutData(
			new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
		signatureSelector.addListener(new ResourceListener() {
			@Override
			public void resourceChanged(IResource oldValue, IResource newValue) {
				if (uiUpdateInProgress)
					return;
				try {
					Signature s = (newValue != null ?
						(Signature)new EclipseFileWrapper((IFile)newValue).load() : null);
					doChange(new BoundDescriptor(getModel(),
							new SimulationSpec.ChangeSetSignatureDescriptor(
									new SimulationSpec.Identifier(),
									getModel().getSignature(), s)));
					recalculateExportEnabled();
				} catch (LoadFailedException ife) {
					ife.printStackTrace();
				}
			}
		});
		
		
		/**
		 * @author Kevin Chan 
		 */
		(l = new Label(self, SWT.RIGHT)).setText("Props:");
		l.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false));
		props = new ListViewer(self);
		props.setContentProvider(new SimulationSpecAPContentProvider(props));
		props.setLabelProvider(new LabelProvider() {
		  @Override
		  public String getText(Object element) {
		    IFile f = getFileFrom((ModelObject)element);
		    if (f != null) {
		      return f.getProjectRelativePath().toString();
		    } else {
		    	return "prop error";		    	
		    }
		  }
		});
		
		// add by Kevin Chan
		props.getList().setLayoutData(
		    new GridData(SWT.FILL, SWT.FILL, true, true));

		Composite br2 = new Composite(self, SWT.NONE);
		br2.setLayoutData(new GridData(SWT.END, SWT.BOTTOM, false, false));
		RowLayout brl2 = new RowLayout(SWT.VERTICAL);
		brl2.marginBottom = brl2.marginLeft = brl2.marginRight =
		    brl2.marginTop = 0;
		brl2.pack = false;
		br2.setLayout(brl2);

		StockButton.ADD.create(br2, SWT.NONE, true).addSelectionListener(
		    new SelectionAdapter() {
		  @Override
		  public void widgetSelected(SelectionEvent e) {
		    ResourceTreeSelectionDialog rtsd =
		      new ResourceTreeSelectionDialog(
		        getSite().getShell(),
		        ((FileEditorInput)getEditorInput()).getFile().getProject(),
		        Mode.FILE, BigraphAP.CONTENT_TYPE);
			    rtsd.setBlockOnOpen(true);
			    if (rtsd.open() == Dialog.OK) {
			      ArrayList it = rtsd.getResultxuxu();
			      ChangeGroup cg = new ChangeGroup();
			      PropertyScratchpad scratch = new PropertyScratchpad();
			      try {
			        int i = 0;
			        while (i < it.size() && it.get(i) != null ) {
			          
			          if (it.get(i) instanceof IResource ){
			            System.out.println("Kevin Chan 20170403");
			          }
			          //String xuxu = typeof(it.get(i));
			          IFile f = (IFile)(it.get(i));
			          //!TODO 怎么求出文件
			          Bigraph b = (Bigraph)new EclipseFileWrapper(f).load();
			          BigraphAP r = b.getBigraphAP();
	
			          IChange ch = new BoundDescriptor(model,
			                  new SimulationSpec.ChangeAddPropDescriptor(
			                    new SimulationSpec.Identifier(),
			                    -1, 
			                    r)
			                  );
	
			          cg.add(scratch.executeChange(ch));
			          ++i;
			        }
			        if (!cg.isEmpty())
			          doChange(cg);
	
			      } catch (LoadFailedException ife) {
			        ife.printStackTrace();
			      }
			    }
		  }
		});

		StockButton.REMOVE.create(br2).addSelectionListener(
		    new SelectionAdapter() {
		  @Override
		  public void widgetSelected(SelectionEvent e) {
		    Iterator<?> it =
		      ((IStructuredSelection)props.getSelection()).iterator();
		    ChangeGroup cg = new ChangeGroup();
		    PropertyScratchpad scratch = new PropertyScratchpad();
		    while (it.hasNext()) {
		      BigraphAP rr = (BigraphAP)it.next();
		      IChange ch = new BoundDescriptor(getModel(),
		          new SimulationSpec.ChangeRemovePropDescriptor(
		              new SimulationSpec.Identifier(),
		              getModel().getPropositions(scratch).indexOf(rr),
		              rr));
		      cg.add(scratch.executeChange(ch));
		    }
		    if (!cg.isEmpty())
		      doChange(cg);
		  }
		});		
		// Kevin Chan ends
		
		
		
		// reaction rule
		(l = new Label(self, SWT.RIGHT)).setText("Reaction rules:");
		l.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false));
		rules = new ListViewer(self);
		rules.setContentProvider(new SimulationSpecRRContentProvider(rules));
		rules.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				IFile f = getFileFrom((ModelObject)element);
				if (f != null) {
					return f.getProjectRelativePath().toString();
				} else return "(embedded rule)";
			}
		});
		rules.getList().setLayoutData(
				new GridData(SWT.FILL, SWT.FILL, true, true));
		
		Composite br = new Composite(self, SWT.NONE);
		br.setLayoutData(new GridData(SWT.END, SWT.BOTTOM, false, false));
		RowLayout brl = new RowLayout(SWT.VERTICAL);
		brl.marginBottom = brl.marginLeft = brl.marginRight =
				brl.marginTop = 0;
		brl.pack = false;
		br.setLayout(brl);
		
		StockButton.ADD.create(br, SWT.NONE, true).addSelectionListener(
				new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ResourceTreeSelectionDialog rtsd =
					new ResourceTreeSelectionDialog(
						getSite().getShell(),
						((FileEditorInput)getEditorInput()).getFile().getProject(),
						Mode.FILE, ReactionRule.CONTENT_TYPE);
				rtsd.setBlockOnOpen(true);
				if (rtsd.open() == Dialog.OK) {
					ArrayList it = rtsd.getResultxuxu();
					ChangeGroup cg = new ChangeGroup();
					PropertyScratchpad scratch = new PropertyScratchpad();
					try {
						int i = 0;
						while (i < it.size() && it.get(i) != null ) {
							
							if (it.get(i) instanceof IResource ){
								System.out.println("xuxu");
							}
							//String xuxu = typeof(it.get(i));
							IFile f = (IFile)(it.get(i));
							ReactionRule r = (ReactionRule)new EclipseFileWrapper(f).load();
							IChange ch = new BoundDescriptor(model,
											new SimulationSpec.ChangeAddRuleDescriptor(
												new SimulationSpec.Identifier(),
												-1, 
												r)
											);

							cg.add(scratch.executeChange(ch));
							++i;
						}
						if (!cg.isEmpty())
							doChange(cg);

					} catch (LoadFailedException ife) {
						ife.printStackTrace();
					}
				}
			}
		});
		
		StockButton.REMOVE.create(br).addSelectionListener(
				new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Iterator<?> it =
					((IStructuredSelection)rules.getSelection()).iterator();
				ChangeGroup cg = new ChangeGroup();
				PropertyScratchpad scratch = new PropertyScratchpad();
				while (it.hasNext()) {
					ReactionRule rr = (ReactionRule)it.next();
					IChange ch = new BoundDescriptor(getModel(),
							new SimulationSpec.ChangeRemoveRuleDescriptor(
									new SimulationSpec.Identifier(),
									getModel().getRules(scratch).indexOf(rr),
									rr));
					cg.add(scratch.executeChange(ch));
				}
				if (!cg.isEmpty())
					doChange(cg);
			}
		});		
		
		
		
		
		
		/**
		 * @author Kevin Chan 
		 */
		(l = new Label(self, SWT.RIGHT)).setText("LTL Formula:");
		formulaText = new Text(self, SWT.BORDER|SWT.MULTI);
		formulaText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
		formulaText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent arg0) {
				System.out.println("Formula");
				model.setLTL_Formula(formulaText.getText());
			}
		});
		
		
		(l = new Label(self, SWT.RIGHT)).setText("Model:");
		l.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		//!TODO use this to get atomic
		modelSelector = new ResourceSelector(self,
			((FileEditorInput)getEditorInput()).getFile().getProject(),
			Mode.FILE, Bigraph.CONTENT_TYPE);
		modelSelector.getButton().setLayoutData(
			new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
		modelSelector.addListener(new ResourceListener() {
			@Override
			public void resourceChanged(IResource oldValue, IResource newValue) {
				if (uiUpdateInProgress)
					return;
				try {
					Bigraph b = (newValue != null ?
						(Bigraph)new EclipseFileWrapper((IFile)newValue).load() : null);
					doChange(new BoundDescriptor(getModel(),
							new SimulationSpec.ChangeSetModelDescriptor(
									new SimulationSpec.Identifier(),
									getModel().getModel(), b)));
					recalculateExportEnabled();
				} catch (LoadFailedException ife) {
					ife.printStackTrace();
				}
			}
		});
		
		// add by Kevin Chan
		(l = new Label(self, SWT.RIGHT)).setText("Pattern:");
		l.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		//!TODO use this to get atomic
		patternSelector = new ResourceSelector(self,
			((FileEditorInput)getEditorInput()).getFile().getProject(),
			Mode.FILE, Bigraph.CONTENT_TYPE);
		patternSelector.getButton().setLayoutData(
			new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
		patternSelector.addListener(new ResourceListener() {
			@Override
			public void resourceChanged(IResource oldValue, IResource newValue) {
				if (uiUpdateInProgress)
					return;
				try {
					Bigraph b = (newValue != null ?
						(Bigraph)new EclipseFileWrapper((IFile)newValue).load() : null);
					doChange(new BoundDescriptor(getModel(),
							new SimulationSpec.ChangeSetPatternDescriptor(
									new SimulationSpec.Identifier(),
									getModel().getModel(), b)));
					recalculateExportEnabled();
				} catch (LoadFailedException ife) {
					ife.printStackTrace();
				}
			}
		});	
		
		(l = new Label(self, SWT.RIGHT)).setText("Error:");
		l.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		//!TODO use this to get atomic
		errorSelector = new ResourceSelector(self,
			((FileEditorInput)getEditorInput()).getFile().getProject(),
			Mode.FILE, Bigraph.CONTENT_TYPE);
		errorSelector.getButton().setLayoutData(
			new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
		errorSelector.addListener(new ResourceListener() {
			@Override
			public void resourceChanged(IResource oldValue, IResource newValue) {
				if (uiUpdateInProgress)
					return;
				try {
					Bigraph b = (newValue != null ?
						(Bigraph)new EclipseFileWrapper((IFile)newValue).load() : null);
					doChange(new BoundDescriptor(getModel(),
							new SimulationSpec.ChangeSetErrorDescriptor(
									new SimulationSpec.Identifier(),
									getModel().getModel(), b)));
					recalculateExportEnabled();
				} catch (LoadFailedException ife) {
					ife.printStackTrace();
				}
			}
		});
		// ends
		
		new Label(self, SWT.HORIZONTAL | SWT.SEPARATOR).setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 3, 1));
		
		(l = new Label(self, SWT.RIGHT)).setText("Tool:");
		l.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		final ComboViewer cv = new ComboViewer(self);
		cv.setContentProvider(new ArrayContentProvider());
		cv.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return ((IFactory<?>)element).getName();
			}
		});
		cv.getCombo().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		ArrayList<IFactory<IInteractionManager>> exporters = getIMFactories();
		cv.setInput(exporters);
		cv.setSelection(new StructuredSelection(exporters.get(0)));
		
		(export = new Button(self, SWT.NONE)).setText("&Export...");
		export.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		export.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IInteractionManager im =
					(IInteractionManager)((IFactory<?>)
						((IStructuredSelection)cv.getSelection()).
							getFirstElement()).newInstance();
				im.setSimulationSpec(getModel());
				im.run(getEditorSite().getShell());
			}
		});
		export.setEnabled(false);
	}
	
	@Override
	protected void createActions() {
	}
	
	@Override
	public void setFocus() {
		super.setFocus();
		if (signatureSelector != null) {
			Button b = signatureSelector.getButton();
			if (b != null && !b.isDisposed() && b.isVisible())
				b.setFocus();
		}
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() != getModel() || uiUpdateInProgress)
			return;
		uiUpdateInProgress = true;
		try {
			String propertyName = evt.getPropertyName();
			Object newValue = evt.getNewValue();
			if (SimulationSpec.PROPERTY_SIGNATURE.equals(propertyName)) {
				Signature s = (Signature)newValue;
				signatureSelector.setResource(getFileFrom(s));
			} else if (SimulationSpec.PROPERTY_MODEL.equals(propertyName)) {
				Bigraph b = (Bigraph)newValue;
				modelSelector.setResource(getFileFrom(b));
			} else if (SimulationSpec.PROPERTY_PATTERN.equals(propertyName)) { 
				Bigraph b = (Bigraph)newValue;
				patternSelector.setResource(getFileFrom(b));
			} else if (SimulationSpec.PROPERTY_ERROR.equals(propertyName)) {
				Bigraph b = (Bigraph)newValue;
				errorSelector.setResource(getFileFrom(b));				
			}
		} finally {
			uiUpdateInProgress = false;
			recalculateExportEnabled();
		}
	}
}
