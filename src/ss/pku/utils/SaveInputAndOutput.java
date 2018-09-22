package ss.pku.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import dk.itu.bigm.editors.bigraph.BigraphEditor;
import dk.itu.bigm.editors.rule.RuleEditor;

public class SaveInputAndOutput {
	
	private final static String fileFolder = "D:\\BigMRulesData\\";
	private final static String fileNameOne = "ruleInOutS.xls";
	private final static String fileNameTwo = "agents.xls";
	private final static String sheetNameOne= "ruleInOuts";
	private final static String sheetNameTwo= "agents";
	
	public static void saveBiRule() {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(); 
		if (null != page) {
			RuleInOut rule = new RuleInOut();
			RuleEditor ruleEditor = (RuleEditor) page.getActiveEditor();
			String ruleName = page.getActiveEditor().getTitle();
			rule.setRuleName(ruleName);
			PairRule pairRule = new PairRule(ruleEditor.getModel().getInputCase(), ruleEditor.getModel().getOutputCase());
			rule.setPairRule(pairRule);
			Map<String, String> mapInfos = getBaseInfo();
			List<RuleInOut> liRules = new ArrayList<RuleInOut>();
			liRules.add(rule);
			setMassiveRules(fileFolder + fileNameOne, mapInfos, liRules);
		}
	}
	
	public static void saveInitialState() {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(); 
		if (null != page) {
			BigraphEditor ruleEditor = (BigraphEditor) page.getActiveEditor();
			String agentName = page.getActiveEditor().getTitle();
			AgentInitialState ag = new AgentInitialState(agentName, ruleEditor.getModel().getAgentExpression());
			addAgentExpression(fileFolder + fileNameTwo, ag);
		}
	}	
	
	private static boolean addAgentExpression(String filePath, AgentInitialState ag) {
		CreateExcel.addAgent(filePath, ag, sheetNameTwo);
		return true;		
	}

	public static boolean setMassiveRules(String filePath, Map<String, String>  mapInfos, List<RuleInOut> liRuleInOuts) {
		try {
			CreateExcel.saveRuleInOut(filePath, mapInfos, liRuleInOuts, sheetNameOne);
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}		
	}
	
	public static Map<String, String> getBaseInfo() {
		Map<String, String> baseInfo = new HashMap<String, String>();
		baseInfo.put("创建人", "AllenChen");
		baseInfo.put("备注", "暂无备注");
		baseInfo.put("创建日期", CreateExcel.getMiSeconds());
		baseInfo.put("初始状态", "机器运行正常");
		return baseInfo;
	}
}
