package dk.itu.bigm.editors.rule;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bigraph.model.Bigraph;
import org.bigraph.model.Layoutable;
import org.bigraph.model.ReactionRule;
import org.bigraph.model.assistants.PropertyScratchpad;
import org.bigraph.model.changes.ChangeGroup;
import org.bigraph.model.changes.ChangeRejectedException;
import org.bigraph.model.changes.IChange;
import org.bigraph.model.changes.descriptors.BoundDescriptor;
import org.bigraph.model.changes.descriptors.ChangeCreationException;
import org.bigraph.model.changes.descriptors.ChangeDescriptorGroup;
import org.bigraph.model.changes.descriptors.IChangeDescriptor;
import org.bigraph.model.loaders.LoadFailedException;
import org.bigraph.model.savers.ReactionRuleXMLSaver;
import org.bigraph.model.savers.SaveFailedException;
import org.eclipse.core.resources.IFile;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.gef.KeyHandler;
import org.eclipse.gef.KeyStroke;
import org.eclipse.gef.MouseWheelHandler;
import org.eclipse.gef.MouseWheelZoomHandler;
import org.eclipse.gef.SnapToGeometry;
import org.eclipse.gef.SnapToGrid;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.commands.CommandStackEvent;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.editparts.ScalableRootEditPart;
import org.eclipse.gef.editparts.ZoomListener;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.ui.actions.DeleteAction;
import org.eclipse.gef.ui.actions.SelectAllAction;
import org.eclipse.gef.ui.actions.ToggleGridAction;
import org.eclipse.gef.ui.actions.ToggleSnapToGeometryAction;
import org.eclipse.gef.ui.actions.ZoomInAction;
import org.eclipse.gef.ui.actions.ZoomOutAction;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.views.properties.IPropertySource;
import org.la4j.Matrix;

import ss.pku.logic.MatrixSolution;
import dk.itu.bigm.editors.AbstractGEFEditor;
import dk.itu.bigm.editors.actions.TogglePropertyAction;
import dk.itu.bigm.editors.bigraph.BigraphEditor;
import dk.itu.bigm.editors.bigraph.BigraphEditorContextMenuProvider;
import dk.itu.bigm.editors.bigraph.actions.BigraphRelayoutAction;
import dk.itu.bigm.editors.bigraph.actions.ContainerCopyAction;
import dk.itu.bigm.editors.bigraph.actions.ContainerCutAction;
import dk.itu.bigm.editors.bigraph.actions.ContainerPasteAction;
import dk.itu.bigm.editors.bigraph.actions.ContainerPropertiesAction;
import dk.itu.bigm.editors.bigraph.commands.ChangeCommand;
import dk.itu.bigm.editors.bigraph.parts.PartFactory;
import dk.itu.bigm.utilities.resources.EclipseFileWrapper;
import dk.itu.bigm.utilities.resources.ResourceTreeSelectionDialog;
import dk.itu.bigm.utilities.ui.UI;
import dk.itu.bigm.utilities.resources.ResourceTreeSelectionDialog.Mode;
import ss.pku.utils.additionalValidator;

public class RuleEditor extends AbstractGEFEditor implements
		ISelectionChangedListener, ISelectionProvider {
	private static int deleteTagString = 0;
	private Button addFormerRule;
	private Button checkReasoningValid;
	private Button copyRule;
	private Button reverseRule;
	ChangeDescriptorGroup xuxu;

	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
		if (adapter == ZoomManager.class) {
			/* required by ZoomComboContributionItem */
			ScalableRootEditPart sep = getScalableRoot(redexViewer);
			return (sep != null ? sep.getZoomManager() : null);
		} else
			return super.getAdapter(adapter);
	}

	private ArrayList<ISelectionChangedListener> listeners = new ArrayList<ISelectionChangedListener>();

	private ISelection selection = null;

	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		listeners.add(listener);
	}

	@Override
	public ISelection getSelection() {
		return selection;
	}

	@Override
	public void removeSelectionChangedListener(
			ISelectionChangedListener listener) {
		listeners.remove(listener);
	}

	@Override
	public void setSelection(ISelection selection) {
		this.selection = selection;
		updateActions(getSelectionActions());

		if (listeners.size() == 0)
			return;
		SelectionChangedEvent e = new SelectionChangedEvent(this,
				getSelection());
		for (ISelectionChangedListener l : listeners)
			l.selectionChanged(e);
	}

	private ScrollingGraphicalViewer redexViewer, reactumViewer;
	public String conditionString = "condition";
	private Text condition, sysClk, exp, rand; // additionalValidator notice
	private Label conditionLabel, sysClkLabel, expLabel, randLabel;
	private Button isRandButton;
	private Text condition1;
	private Label conditionLabel1;

	// 用于导入输入和输出到 Excel 表格，Kevin Chan 增加于 2016/4/12
	private Text inputCase, outputCase;
	private Label inputCaseLabel, outputCaseLabel;
	
	//用于导出 xml 时的反应规则名
	private Text ruleName;
	private Label ruleNameLabel;
	
	private Text probability;
	private Label probabilityLabel;
	
	
	private boolean ignoringSelectionUpdates = false;

	/**
	 * Fired by the redex and reactum viewers when their selections change.
	 */
	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		if (ignoringSelectionUpdates)
			return;
		ignoringSelectionUpdates = true;

		if (!event.getSelection().isEmpty())
			(event.getSource() == reactumViewer ? redexViewer : reactumViewer)
					.deselectAll();

		setSelection(event.getSelection());
		ignoringSelectionUpdates = false;
	}
	
	@Override
	public void doActualSave(IFile f, OutputStream os)
			throws SaveFailedException {
		//notice: this following part has been moved to a special button called reasoning
		//接下来的部分被放到推理按钮中了
		// add by Kevin Chan 获得所有变量
//		if (getModel().isLogic()) {
//			Matrix matrix = getModel().changeMaterial2Matrix();
//			System.out.println(matrix.toCSV());
//			MatrixSolution solver = new MatrixSolution();
//			boolean result = solver.getSolution(matrix);
//			MessageDialog.openInformation(null, "推理结果", result?"推理成功":"推理失败");
//			
//			System.out.println(result);			
//		}
		
		// add ends
		String text2Focused = additionInfoValid(); 
		if (text2Focused == null) {
			ReactionRuleXMLSaver r = new ReactionRuleXMLSaver()
					.setModel(getModel());
			r.setFile(new EclipseFileWrapper(f)).setOutputStream(os).exportObject();
			// add by Kevin Chan 用于增加输入和输出
			// SaveInputAndOutput.saveBiRule();
			// ends
			super.setSavePoint(); // add by Kevin Chan paper
			getCommandStack().markSaveLocation();	
		} else {
			 MessageDialog.openInformation(null, "保存失败", "文本框 " + text2Focused + " 不符合规范");
			throw new SaveFailedException("文本框" + text2Focused + "不符合要求");
		}
	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		super.init(site, input);
	}

	// change condition
	private static final IChange changeCondition(ReactionRule rr, String s) {
		if (rr != null && s != null) {
			ChangeGroup cg = new ChangeGroup();

//			cg.add(new BoundDescriptor(c.getSignature(),
//					new NamedModelObject.ChangeNameDescriptor(
//							c.getIdentifier(), s)));
//			cg.add(ControlUtilities.changeLabel(c,
//					ControlUtilities.labelFor(s)));
			return cg;
		} else
			return null;
	}

	@Override
	public void createEditorControl(Composite parent) {
		SashForm splitter = new SashForm(parent, SWT.HORIZONTAL | SWT.SMOOTH);

		redexViewer = new ScrollingGraphicalViewer();
		reactumViewer = new ScrollingGraphicalViewer();

		createPaletteViewer(splitter);
		Composite c = new Composite(splitter, SWT.NONE);

		splitter.setWeights(BigraphEditor.INITIAL_SASH_WEIGHTS);

		GridLayout gl = new GridLayout(1, false);
		gl.marginTop = gl.marginLeft = gl.marginBottom = gl.marginRight = gl.horizontalSpacing = gl.verticalSpacing = 10;
		c.setLayout(gl);

		Composite ruleComposite = new Composite(c, SWT.NONE);
		ruleComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout ruleGridLayout = new GridLayout(3, false);
		//ruleGridLayout.marginTop = ruleGridLayout.marginLeft = ruleGridLayout.marginBottom = ruleGridLayout.marginRight = ruleGridLayout.horizontalSpacing = ruleGridLayout.verticalSpacing = 10;
		ruleComposite.setLayout(ruleGridLayout);
		
		redexViewer.createControl(ruleComposite);
		redexViewer.getControl().setLayoutData(
				new GridData(SWT.FILL, SWT.FILL, true, true));


		Label l = new Label(ruleComposite, SWT.NONE);
		l.setText("\n\u2192");//反应物和生成物之间的那个箭头
		l.setFont(UI.tweakFont(l.getFont(), 40, SWT.BOLD));
		l.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
		

		reactumViewer.createControl(ruleComposite);
		reactumViewer.getControl().setLayoutData(
				new GridData(SWT.FILL, SWT.FILL, true, true));

		addFormerRule = new Button(ruleComposite, SWT.NULL);
		addFormerRule.setText("Choose Former Rule");
//		addFormerRule.setBounds(100, 200, 100, 100);
		addFormerRule.setBounds(0, 0, 100, 100);
		addFormerRule.setSize(100, 100);
		addFormerRule.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ResourceTreeSelectionDialog rtsd = new ResourceTreeSelectionDialog(
						getSite().getShell(),
						((FileEditorInput) getEditorInput()).getFile()
								.getProject(), Mode.FILE,
						ReactionRule.CONTENT_TYPE);
				rtsd.setBlockOnOpen(true);
				if (rtsd.open() == Dialog.OK) {
					IFile f = (IFile) rtsd.getFirstResult();
					try {
						ReactionRule r = (ReactionRule) new EclipseFileWrapper(
								f).load();
						Bigraph reactum = r.getReactum();
//						ReactionRule rr = getModel();
						model.setRedex(reactum);
						//Model(model);
//						ReactionRule rr1 = getModel();
						redexViewer.setContents(getModel().getRedex());
						reactumViewer.setContents(getModel().getReactum());

					} catch (LoadFailedException ife) {
						ife.printStackTrace();
					}
				}
			}
		});
		
		copyRule = new Button(ruleComposite, SWT.NULL);
		copyRule.setText("copy a rule");
		copyRule.setBounds(0, 0, 100, 100);
		copyRule.setSize(100, 100);
		copyRule.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ResourceTreeSelectionDialog rtsd = new ResourceTreeSelectionDialog(
						getSite().getShell(),
						((FileEditorInput) getEditorInput()).getFile()
								.getProject(), Mode.FILE,
						ReactionRule.CONTENT_TYPE);
				rtsd.setBlockOnOpen(true);
				if (rtsd.open() == Dialog.OK) {
					IFile f = (IFile) rtsd.getFirstResult();
					try {
						ReactionRule r = (ReactionRule) new EclipseFileWrapper(
								f).load();
						Bigraph redex = r.getRedex();
						Bigraph reactum = r.getReactum();
						model.setRedex(redex);
						model.setReactum(reactum);
						redexViewer.setContents(getModel().getRedex());
						reactumViewer.setContents(getModel().getReactum());

					} catch (LoadFailedException ife) {
						ife.printStackTrace();
					}
				}
			}
		});
		
		reverseRule = new Button(ruleComposite, SWT.NULL);
		reverseRule.setText("reverseRule");
		reverseRule.setBounds(0, 0, 100, 100);
		reverseRule.setSize(100, 100);
		reverseRule.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				ReactionRule rr = getModel();
				Bigraph redex = rr.getRedex();
				Bigraph reactum = rr.getReactum();
				model.setRedex(reactum);
				model.setReactum(redex);
				redexViewer.setContents(getModel().getRedex());
				reactumViewer.setContents(getModel().getReactum());
			}
			
		});
		
		// add by Kevin Chan
		checkReasoningValid = new Button(ruleComposite, SWT.NULL);
		checkReasoningValid.setText("Reasoning");
		checkReasoningValid.setBounds(0, 0, 100, 100);
//		checkReasoningValid.setCursor(new Cursor(null, 1)); // 1 代表 swt.wait，14 代表 System resize north-east cursor
		checkReasoningValid.setSize(100, 100);
		checkReasoningValid.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (getModel().isFirstOrderLogic()) { //一阶逻辑
					Matrix matrix = getModel().changeFirstOrderMaterial2Matrix();
					
					
				} else if (getModel().isLogic()) { //命题逻辑
					Matrix matrix = getModel().changeMaterial2Matrix();
					System.out.println(matrix.toCSV());
					MatrixSolution solver = new MatrixSolution();
					boolean result = solver.getSolution(matrix);
					MessageDialog.openInformation(null, "推理结果", result?"推理成功":"推理失败");
					
					System.out.println(result);			
				} else {
					MessageDialog.openInformation(null, "警告", "无法推理");
				}
			}
			
		});
		// add by Kevin Chan ends
		
		// org.eclipse.swt.widgets.List list = new
		// org.eclipse.swt.widgets.List(c,
		// SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL);
		// // 最下面的空白区域……
		// GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false, 3, 1);
		// gd.heightHint = 100;
		// list.setLayoutData(gd);

		abstract class TextListener implements SelectionListener, FocusListener {
			abstract void go();
			
			@Override
			public void focusGained(FocusEvent e) {
				/* nothing */
			}

			@Override
			public void focusLost(FocusEvent e) {
//				if (shouldPropagateUI())
					go();
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				/* nothing */
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				//if (shouldPropagateUI())
					go();
			}
			
		}
		
		abstract class SetSavePoint implements ModifyListener, Listener {
			
			@Override
			public void modifyText(ModifyEvent e) {
				// nothing
			}
			
		}
		
		
		Composite ConditionComposite = new Composite(c, SWT.NONE);
		ConditionComposite.setLayoutData(new GridData(SWT.FILL, SWT.NULL, true, false));
		ConditionComposite.setLayout(new GridLayout(2, false));
		
		TextListener conditionListener = new TextListener() {
			@Override
			void go() {
//				if (!model.getCondition().equals(condition.getText()))
//					model.setCondition(condition.getText());
				if(!model.getExp().equals(exp.getText()))
					model.setExp(exp.getText());
//				if(!model.getSysClk().equals(sysClk.getText()))
//					model.setSysClk(sysClk.getText());
				if(!model.getInputCase().equals(inputCase.getText()))
					model.setInputCase(inputCase.getText());
				if(!model.getOutputCase().equals(outputCase.getText()))
					model.setOutputCase(outputCase.getText());
			}
		};
		
		final SetSavePoint savePointMaker = new SetSavePoint() {
			@Override
			public void modifyText(ModifyEvent e) {
				doChange(getModel().changeProperty("general", "", ""));
			}

			@Override
			public void handleEvent(Event event) {
				// TODO Auto-generated method stub
				
			}
		};
		
		(conditionLabel = new Label(ConditionComposite, SWT.NONE)).setText("condition:");
		condition = new Text(ConditionComposite, SWT.BORDER);
		GridData conData = new GridData();
		conData.verticalAlignment = GridData.FILL; //垂直方向充满 
		//conData.grabExcessVerticalSpace = true; //抢占垂直方向额外空间 
		conData.horizontalAlignment = GridData.FILL;//水平方向充满 
		conData.grabExcessHorizontalSpace = true;//抢占水平方向额外空间
		condition.setLayoutData(conData);
		condition.setMessage("请输入反应发生的条件，用逗号隔开，例如：“x==2, y==3”的格式");
		condition.addSelectionListener(conditionListener);
//		condition.addModifyListener(savePointMaker);
		condition.addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent e) {
				String oldValue = model.getCondition();
				String newValue = condition.getText();
				if (!oldValue.equals(newValue)) {
					model.setCondition(newValue);
					doChange(getModel().changeProperty("condition", oldValue, newValue));
				} else {
					return;
				}
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				Object obj = e.widget.getListeners(24);
				if (obj == null || ((Listener[])obj).length == 0) {
					((Text)e.widget).addModifyListener(savePointMaker);
				}				
			}
		});
		
		(expLabel = new Label(ConditionComposite, SWT.NONE)).setText("expression:");
		exp = new Text(ConditionComposite, SWT.BORDER);
		exp.setLayoutData(conData);
//		exp.addSelectionListener(conditionListener);
//		exp.addModifyListener(stringValidator);
		exp.addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent e) {
				String oldExp = model.getExp();
				String newExp = exp.getText();
				if (!oldExp.equals(newExp)) {
					model.setExp(newExp);
					if (e.widget.getListeners(24) == null) {
						e.widget.addListener(24, (Listener) savePointMaker);
					}
					doChange(getModel().changeProperty("exp", oldExp, newExp));
				}
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				Object obj = e.widget.getListeners(24);
				if (obj == null || ((Listener[])obj).length == 0) {
					e.widget.addListener(24, (Listener) savePointMaker);
				}				
			}
		});
		exp.setMessage("请输入表达式");
		
		//用于新增输入和输出，Kevin Chan 增加于 2016/4/12
		(inputCaseLabel = new Label(ConditionComposite, SWT.NONE)).setText("输入:");
		inputCase = new Text(ConditionComposite, SWT.BORDER);
		conData.minimumHeight = 200;
		inputCase.setLayoutData(conData);
		inputCase.addSelectionListener(conditionListener);
//		inputCase.addModifyListener(savePointMaker);
		inputCase.addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent e) {
				String oldInputCase = model.getInputCase();
				String newInputCase = inputCase.getText();
				if (!oldInputCase.equals(newInputCase)) {
					model.setInputCase(inputCase.getText());
					if (e.widget.getListeners(24) == null) {
						e.widget.addListener(24, (Listener) savePointMaker);
					}
					doChange(getModel().changeProperty("inputCase", oldInputCase, newInputCase));
				}				
			}

			@Override
			public void focusGained(FocusEvent e) {
				Object obj = e.widget.getListeners(24);
				if (obj == null || ((Listener[])obj).length == 0) {
					((Text)e.widget).addModifyListener(savePointMaker);
				}						
			}
		});
		inputCase.setMessage("输入情况");
		
		(outputCaseLabel = new Label(ConditionComposite, SWT.NONE)).setText("输出:");
		outputCase = new Text(ConditionComposite, SWT.BORDER);
		outputCase.setLayoutData(conData);
		outputCase.addSelectionListener(conditionListener);
//		outputCase.addModifyListener(savePointMaker);
		outputCase.addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent e) {
				String oldValue = model.getOutputCase();
				String newValue = outputCase.getText();
				if (!oldValue.equals(newValue)) {
					model.setOutputCase(newValue);
					doChange(getModel().changeProperty("outputCase", oldValue, newValue));
				}
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				Object obj = e.widget.getListeners(24);
				if (obj == null || ((Listener[])obj).length == 0) {
					((Text)e.widget).addModifyListener(savePointMaker);
				}		
			}
		});
		outputCase.setMessage("输出情况");

		
		(ruleNameLabel = new Label(ConditionComposite, SWT.NONE)).setText("规则名:");
		ruleName = new Text(ConditionComposite, SWT.BORDER);
		ruleName.setLayoutData(conData);
		ruleName.addSelectionListener(conditionListener);
//		ruleName.addModifyListener(savePointMaker);
		ruleName.addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent e) {
				String oldValue = model.getRuleName();
				String newValue = ruleName.getText();
				if (!oldValue.equals(newValue)) { 
					model.setRuleName(newValue);					
					doChange(getModel().changeProperty("ruleName", oldValue, newValue));
				}
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				Object obj = e.widget.getListeners(24);
				if (obj == null || ((Listener[])obj).length == 0) {
					((Text)e.widget).addModifyListener(savePointMaker);
				}						
			}
		});
		ruleName.setMessage("请输入规则名，以英文字母开头");
		
		// new text 发生概率 prohibit
		(probabilityLabel = new Label(ConditionComposite, SWT.NONE)).setText("Probability:");
		probability = new Text(ConditionComposite, SWT.BORDER);
		probability.setLayoutData(conData);
		probability.addSelectionListener(conditionListener);
//		probability.addModifyListener(savePointMaker);
		probability.addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent e) {
				String oldValue = model.getProbability();
				String newValue = probability.getText();
				if (!oldValue.equals(newValue)) { 
					model.setProbability(newValue);					
					doChange(getModel().changeProperty("probability", oldValue, newValue));
				}
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				Object obj = e.widget.getListeners(24);
				if (obj == null || ((Listener[])obj).length == 0) {
					((Text)e.widget).addModifyListener(savePointMaker);
				}
			}
		});
		probability.setMessage("请输入发生的概率，范围为0-100");		

		
		//add ends

		
		
		
		Composite timeRandComposite = new Composite(c, SWT.NONE);
		timeRandComposite.setLayoutData(new GridData(SWT.FILL, SWT.NULL, true, false));
		timeRandComposite.setLayout(new GridLayout(3, false));
		
		(sysClkLabel = new Label(timeRandComposite, SWT.NONE)).setText("time for reaction:");
		sysClk = new Text(timeRandComposite, SWT.BORDER);
		sysClk.setLayoutData(new GridData(SWT.FILL, SWT.NULL, true, false));
//		sysClk.addSelectionListener(conditionListener);
//		sysClk.addModifyListener(savePointMaker);
		sysClk.addFocusListener(new FocusListener() {
			
			/**
			 * 判断是否合法
			 * @author Kevin Chan
			 */
			@Override
			public void focusLost(FocusEvent e) {
				String oldValue = model.getSysClk();
				String newValue = sysClk.getText();
				if (!oldValue.equals(newValue)) {
					if (newValue != null && !newValue.isEmpty()) {
						if (newValue.matches("^\\d+$")) {
							model.setSysClk(newValue);							
						} else {
							sysClk.setText(oldValue);
						}
					} else {
						//TODO 新值为空
					}
					
					model.setSysClk(newValue);
					doChange(getModel().changeProperty("sysClk", oldValue, newValue));
				}				
			}

			@Override
			public void focusGained(FocusEvent e) {
				Object obj = e.widget.getListeners(24);
				if (obj == null || ((Listener[])obj).length == 0) {
					((Text)e.widget).addModifyListener(savePointMaker);
				}
			}
		});
		sysClk.setMessage("请输入数字");

		
		(new Label(timeRandComposite, SWT.NONE)).setText("       ");
		(randLabel = new Label(timeRandComposite, SWT.NONE)).setText("isRandom ");
		isRandButton = new Button(timeRandComposite, SWT.CHECK);
		isRandButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e){
				if(isRandButton.getSelection()){
					model.setRand("true");
				}else{
					model.setRand("false");
				}
				doChange(getModel().changeProperty("sysClk", "", ""));//TODO add by Kevin Chan
			}
		});
		
		(new Label(timeRandComposite, SWT.NONE)).setText("       ");
		(randLabel = new Label(timeRandComposite, SWT.NONE)).setText("canReverse ");
		isRandButton = new Button(timeRandComposite, SWT.CHECK);
		isRandButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e){
				if(isRandButton.getSelection()){
					model.setRand("true");
				}else{
					model.setRand("false");
				}
				doChange(getModel().changeProperty("sysClk", "", ""));//TODO add by Kevin Chan
			}
		});

		configureGraphicalViewer();
		
		redexViewer.getControl().setBackground(ColorConstants.listBackground);
		reactumViewer.getControl().setBackground(ColorConstants.listBackground);
	}

	protected void configureGraphicalViewer() {
		redexViewer.setEditDomain(getEditDomain());
		reactumViewer.setEditDomain(getEditDomain());

		redexViewer.setEditPartFactory(new PartFactory());
		reactumViewer.setEditPartFactory(new PartFactory() {
			@Override
			public IPropertySource getPropertySource(Object o) {
				return (o instanceof Layoutable ? new ReactumPropertySource(
						(Layoutable) o) : null);
			}
		});

		ScalableRootEditPart redexRoot = new ScalableRootEditPart(), reactumRoot = new ScalableRootEditPart();
		redexViewer.setRootEditPart(redexRoot);
		reactumViewer.setRootEditPart(reactumRoot);

		redexViewer.setContextMenu(new BigraphEditorContextMenuProvider(
				redexViewer, getActionRegistry()));
		reactumViewer.setContextMenu(new BigraphEditorContextMenuProvider(
				reactumViewer, getActionRegistry()));

		redexViewer.addSelectionChangedListener(this);
		reactumViewer.addSelectionChangedListener(this);
		getSite().setSelectionProvider(this);

		final ZoomManager redexZoom = redexRoot.getZoomManager(), reactumZoom = reactumRoot
				.getZoomManager();

		redexZoom.setZoomLevels(BigraphEditor.STOCK_ZOOM_LEVELS);
		reactumZoom.setZoomLevels(BigraphEditor.STOCK_ZOOM_LEVELS);
		redexZoom
				.setZoomLevelContributions(BigraphEditor.STOCK_ZOOM_CONTRIBUTIONS);
		reactumZoom
				.setZoomLevelContributions(BigraphEditor.STOCK_ZOOM_CONTRIBUTIONS);

		registerActions(null, new ZoomInAction(redexZoom), new ZoomOutAction(
				reactumZoom));

		final ZoomListener zoomSynchroniser = new ZoomListener() {
			private boolean lock = false;

			@Override
			public void zoomChanged(double zoom) {
				if (lock)
					return;
				lock = true;
				try {
					redexZoom.setZoom(zoom);
					reactumZoom.setZoom(zoom);
				} finally {
					lock = false;
				}
			}
		};

		redexZoom.addZoomListener(zoomSynchroniser);
		reactumZoom.addZoomListener(zoomSynchroniser);

		KeyHandler keyHandler = new KeyHandler();
		 keyHandler.put(KeyStroke.getPressed(SWT.DEL, SWT.DEL, 0),
		getActionRegistry().getAction(ActionFactory.DELETE.getId()));
		keyHandler.put(KeyStroke.getPressed(SWT.DEL, SWT.DEL, 0),
				getActionRegistry().getAction(ActionFactory.COPY.getId()));
		keyHandler.put(KeyStroke.getPressed('+', SWT.KEYPAD_ADD, SWT.NONE),
				new Action() {
					@Override
					public void run() {
						redexZoom.zoomIn();
					}
				});
		keyHandler.put(
				KeyStroke.getPressed('-', SWT.KEYPAD_SUBTRACT, SWT.NONE),
				new Action() {
					@Override
					public void run() {
						redexZoom.zoomOut();
					}
				});

		redexViewer.setKeyHandler(keyHandler);
		reactumViewer.setKeyHandler(keyHandler);

		String stateMask = MouseWheelHandler.KeyGenerator.getKey(SWT.CTRL);
		redexViewer.setProperty(stateMask, MouseWheelZoomHandler.SINGLETON);
		reactumViewer.setProperty(stateMask, MouseWheelZoomHandler.SINGLETON);

		registerActions(null,
				new ToggleGridAction(redexViewer) {
					@Override
					public void run() {
						super.run();
						boolean val = isChecked();
						reactumViewer.setProperty(
								SnapToGrid.PROPERTY_GRID_VISIBLE, val);
						reactumViewer.setProperty(
								SnapToGrid.PROPERTY_GRID_ENABLED, val);
					}
				}, new ToggleSnapToGeometryAction(redexViewer) {
					@Override
					public void run() {
						super.run();
						boolean val = isChecked();
						reactumViewer.setProperty(
								SnapToGeometry.PROPERTY_SNAP_ENABLED, val);
					}
				},
				new TogglePropertyAction(PROPERTY_DISPLAY_GUIDES, true,
						redexViewer) {
					@Override
					public void run() {
						super.run();
						reactumViewer.setProperty(PROPERTY_DISPLAY_GUIDES,
								isChecked());
					}
				});
	}

	private ReactionRule model;

	@Override
	public ReactionRule getModel() {
		return model;
	}
	public void setModel(ReactionRule model) {
		this.model = model;
	}
	@Override
	protected void loadModel() throws LoadFailedException {
		model = (ReactionRule) loadInput();
	}

	@Override
	protected void updateEditorControl() {
		if (getError() != null)
			return;
		getCommandStack().flush();
		updateNodePalette(getModel().getRedex().getSignature());
		redexViewer.setContents(model.getRedex());
		reactumViewer.setContents(model.getReactum());
		condition.setText(model.getCondition());// for conditon display
		sysClk.setText(model.getSysClk());
		exp.setText(model.getExp());
		// add by Kevin Chan starts
		inputCase.setText(model.getInputCase());
		outputCase.setText(model.getOutputCase());
		probability.setText(model.getProbability());
		if (null == model.getRuleName() || model.getRuleName().isEmpty()) {
			ruleName.setText("");
		} else {
			ruleName.setText(model.getRuleName());			
		}
//		ruleName.setText();
		// add by Kevin Chan ends
		if(model.getRand() == null && model.getRand().trim().equals("false")){
			isRandButton.setSelection(false);
		} else{
			isRandButton.setSelection(true);
		}
	}

	private Bigraph getRedex() {
		return getModel().getRedex();
	}

	private Bigraph getReactum() {
		return getModel().getReactum();
	}

	@Override
	public void stackChanged(CommandStackEvent event) {
//		FormRulesXMLLoader fr = new FormRulesXMLLoader();     
		int detail = event.getDetail() & CommandStack.PRE_MASK;
		if (detail != 0)
			_testConvert(event.getDetail(), event.getCommand());
		super.stackChanged(event);
	}

	private void _testConvert(int detail, Command c) {
		if (c instanceof ChangeCommand) {
			_testConvertChange(detail, (ChangeCommand) c);
		} else if (c instanceof CompoundCommand) {
			for (Object i : ((CompoundCommand) c).getCommands())
				if (i instanceof Command)
					_testConvert(detail, (Command) i);
		}
	}

	private Map<IChange, IChangeDescriptor> reactumChangeToDescriptor = new HashMap<IChange, IChangeDescriptor>();
	private Map<IChange, IChange> safeRedexToReactum = new HashMap<IChange, IChange>();

	private void _testConvertChange(int detail, ChangeCommand c) {
		IChange commandChange = c.getChange();
		Object target = c.getTarget();
		ChangeDescriptorGroup reactumChanges = getModel().getEdit().getDescriptors();
		// bug fix by Kevin Chan
		// unable to save the change of a link at right side when there is none relative link at left side
		if (null == target) {
			ChangeGroup changeGroup = (ChangeGroup)commandChange;
			BoundDescriptor boundDescriptor = (BoundDescriptor) changeGroup.get(0);
//			String des = boundDescriptor.getDescriptor().toString();
//			ChangeNameDescriptor cnd = (ChangeNameDescriptor) boundDescriptor.getDescriptor();
			target = boundDescriptor.getResolver();			
		}
		// Kevin Chan
 		ChangeDescriptorGroup cdg = new ChangeDescriptorGroup();
		if (target == null)   target = getRedex();
		if (target == getRedex()) {
			IChangeDescriptor cd = DescriptorUtilities
					.createDescriptor((detail != CommandStack.PRE_UNDO ? commandChange
							: commandChange.inverse()));
			ChangeDescriptorGroup lRedexCDs = DescriptorUtilities.linearise(cd);
			cdg = ReactionRule.performFixups(lRedexCDs,
					reactumChanges);
			String deleteTag = cd.toString().substring(18, 25);
			deleteTag = deleteTag.trim();
			if (deleteTag.equals("remove")) {
				cdg.clear();
			}

			/* Integrity check */
			try {
				PropertyScratchpad scratch = new PropertyScratchpad();
				if (detail != CommandStack.PRE_UNDO) {
					commandChange.simulate(scratch);
				} else
					commandChange.inverse().simulate(scratch);
				/*
				 * scratch now contains the prospective state of the redex after
				 * the change has been applied. Check that we can still get to
				 * the reactum from there
				 */
				getRedex().tryValidateChange(scratch,
						cdg.createChange(scratch, getRedex()));
			} catch (ChangeCreationException cce) {
				throw new Error("BUG: post-fixup reactum changes are "
						+ "completely inconsistent, don't save", cce);
			} catch (ChangeRejectedException cre) {
				throw new Error("BUG: post-fixup reactum changes are "
						+ "slightly inconsistent, don't save", cre);
			}

			/*
			 * cdg will be equal to reactumChanges if the fixup operations made
			 * no changes
			 */
			if (cdg != reactumChanges) {
				reactumChanges.clear();
				reactumChanges.addAll(cdg);
			}

			IChange instantiatedReactumChanges;
			/*
			 * Anything that's left in lRedexCDs after the fixups should be
			 * unrelated to the reactum changes, and so should be safe to apply
			 */
			try {
				if (detail != CommandStack.PRE_UNDO) {
					instantiatedReactumChanges = lRedexCDs.createChange(null,
							getReactum());
					if (!instantiatedReactumChanges.toString().equals("[null]")) {
						getReactum().tryApplyChange(instantiatedReactumChanges);
						safeRedexToReactum.put(commandChange,
								instantiatedReactumChanges);
					} else {
						deleteTagString = 1;
					}
				} else {
					if (deleteTagString==0) {
						instantiatedReactumChanges = safeRedexToReactum
								.remove(commandChange);

						getReactum().tryApplyChange(
								instantiatedReactumChanges.inverse());
					}
				}
			} catch (ChangeCreationException cce) {
				throw new Error("BUG: completely unsafe change slipped "
						+ "through the net", cce);
			} catch (ChangeRejectedException cre) {
				throw new Error("BUG: slightly unsafe change slipped "
						+ "through the net", cre);
			}
		} else if (target == getReactum()) {
			IChangeDescriptor cd;
			if (detail == CommandStack.PRE_UNDO) {
				cd = reactumChangeToDescriptor.remove(commandChange);
				reactumChanges.remove(cd);
			} else {
				cd = DescriptorUtilities.createDescriptor(commandChange);
				reactumChangeToDescriptor.put(commandChange, cd);
				reactumChanges.add(cd);
			}
		}
	}

	@Override
	protected void createActions() {
		registerActions(getSelectionActions(), new DeleteAction(
				(IWorkbenchPart) this), new ContainerPropertiesAction(this),
				new ContainerCutAction(this), new ContainerCopyAction(this),
				new BigraphRelayoutAction(this), new ContainerPasteAction(this));

		registerActions(null, new SelectAllAction(this));
	}

	@Override
	public void updateEditor() {
		try {
			loadModel();
		} catch (LoadFailedException e) {
			e.printStackTrace();
		}
		updateEditorControl();
	}

	

//	@Override
//	protected void tryApplyChange(IChange c) throws ChangeRejectedException {
//		// TODO Auto-generated method stub
//	}

	// below is added by Kevin Chan
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
	 * @throws SecurityException 
	 * @throws NoSuchFieldException 
	 */
	private String additionInfoValid() {
		String[] textNames = {"condition", "exp", "sysClk", 
				"inputCase", "outputCase", "ruleName", "probability"};
		
		for (int i = 0;i < textNames.length;i++) {
			String textName = textNames[i];
			String textString = getTextString(textName);			
			if (!additionalValidator.isValid(textString, textName)){
				boolean flag = setFocusByTextName(textName);
				return textName;
			}
		}
		return null;
	}
	
	private String getTextString(String textName) {
		if("condition".equals(textName)) {
			return condition.getText();
		} else if("exp".equals(textName)) {
			return exp.getText();
		} else if("sysClk".equals(textName)) {
			return sysClk.getText();
		} else if("inputCase".equals(textName)) {
			return inputCase.getText();
		} else if("outputCase".equals(textName)) {
			return outputCase.getText();
		} else if("ruleName".equals(textName)) {
			return ruleName.getText();
		} else if ("probability".equals(textName)) {
			return probability.getText();
		} else {
			return null;			
		}
	}
	
	private boolean setFocusByTextName(String textName) {
		if("condition".equals(textName)) {
			return condition.forceFocus();
		} else if("exp".equals(textName)) {
			return exp.forceFocus();
		} else if("sysClk".equals(textName)) {
			return sysClk.forceFocus();
		} else if("inputCase".equals(textName)) {
			return inputCase.forceFocus();
		} else if("outputCase".equals(textName)) {
			return outputCase.forceFocus();
		} else if("ruleName".equals(textName)) {
			return ruleName.forceFocus();
		} else if ("probability".equals(textName)) {
			return probability.forceFocus();
		} else {
			return false;
		}
	}
}