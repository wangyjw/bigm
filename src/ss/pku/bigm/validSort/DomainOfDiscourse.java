package ss.pku.bigm.validSort;

import java.util.ArrayList;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.bigraph.model.Bigraph;
import org.bigraph.model.Container;
import org.bigraph.model.FormationRule;
import org.bigraph.model.Layoutable;
import org.bigraph.model.Node;

public class DomainOfDiscourse {
	
	private static ArrayList<Node> nodesInDomain;
	
	public static void getNodesByBigraph(Bigraph b) {
		for (Layoutable l:b.getChildren()) {
			if (l instanceof Node) {
				nodesInDomain.add((Node)l);
				getNodesByContainer(l);				
			}
		}
	}
	
	private static void getNodesByContainer(Layoutable parentLay) {
		for (Layoutable sonLay:((Container)parentLay).getChildren()) {
			if (sonLay instanceof Node) {
				nodesInDomain.add((Node)sonLay);
				getNodesByContainer(sonLay);				
			}
		}
	}
	
	public static boolean validSentence(String sentence, Map<String, Boolean> relatedRuleNameMap) throws ScriptException {
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByName("js");
		for (Map.Entry<String, Boolean> entry : relatedRuleNameMap.entrySet()) {  
			engine.put(entry.getKey(), entry.getValue());
		}  
		
		Object result = engine.eval(sentence);
		if (result.getClass().getName().equals("Boolean")) {
			return (Boolean)result;
		} else {
			return false;			
		}
	}
	
	public static ArrayList<FormationRule> replaceVarAsConstant(ArrayList<FormationRule> variabledRules, String varName, Node var) {
		ArrayList<FormationRule> constantRules = new ArrayList<FormationRule>();
		for (FormationRule formationRule:variabledRules) {
			FormationRule constantRule = new FormationRule();
			if (formationRule.getSort1().equals(varName)) {
				constantRule.setSort1(var.getName());
			} else {
				constantRule.setSort1(formationRule.getSort1());
			}
			if (formationRule.getSort2().equals(varName)) {
				constantRule.setSort1(var.getName());
			} else {
				constantRule.setSort2(formationRule.getSort1());
			}
			constantRule.setConstraint(formationRule.getConstraint());
			constantRule.setType(formationRule.getType());
			constantRules.add(constantRule);
		}
		return constantRules;
	}
}
