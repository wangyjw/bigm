package ss.pku.logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class FormulaValidation {
	private String formula; // 原公式
	private String replacedFormula; // 替换后的
	private static List<String> unaryOp = Arrays.asList("!", "G", "F", "X");
	private static List<String> binaryOp = Arrays.asList("&{2}", "\\|{2}", "->");
	private static List<String> specialBinaryOp = Arrays.asList("U", "R");
	
	public FormulaValidation(String formula) {
		this.formula = formula;
	}
	
	private String getReplaced(String replacedString) {
		replacedString = replacedString.replaceAll("[a-z0-9]+", "true");
		for (String op: unaryOp) {
			replacedString = replacedString.replaceAll(op, "!");
		}
		
		for (String op: binaryOp) {
			replacedString = replacedString.replaceAll(op, "&&");
		}
		
		for (String op: specialBinaryOp) {
			replacedString = replacedString.replaceAll(op, "&&");
		}		
		return replacedString;
	}
	
	private static List<Integer> getPositionList() {
		List<Integer> result = new ArrayList<Integer>();
		
		String demo1;
//		demo1 = "G(test1&&test2)||(test3Rtest4)R(test6)R(R)";
		demo1 = "RRRRRRRRR";
		int index = demo1.indexOf("R");
		if (index >= 0) {
			while(index >= 0) {
				result.add(index);
				index = demo1.indexOf("R", index + 1);
			}
			System.out.println(result);
			return result;
		} else {
			return null;
		}
	}		
	
	/**
	 * 检测括号是否合格
	 * formula 是经过预处理的，R 和 U 已经合并为 R
	 *  
	 * @author Kevin Chan 
	 */
	public boolean isSingleBracketValid(String formula, int position) {		
		int i = position, leftBracketPos = position, rightBracketPos = position;

		Stack<Character> left = new Stack<Character>();
		Stack<Character> right = new Stack<Character>();
		
		String subStr = formula.substring(0, i);
		if (subStr.lastIndexOf(")") < 
				subStr.lastIndexOf("(")) {
			leftBracketPos = subStr.lastIndexOf("(");
		} else {
			left.push(')');
			i = subStr.lastIndexOf(")") - 1;
			while (i >= 0) {
				if (formula.charAt(i) == ')') {
					left.push(')');
				} else if (formula.charAt(i) == '(') {
					if (left.size() > 0) {
						left.pop();						
					} else {
						break;
					}
				} else {
					//TODO 既不是 '(' 也不是 ')'
				}
				i--;
			}
		}
		leftBracketPos = i;
		
		i = position; // reset
		subStr = formula.substring(i + 1);
		if (subStr.lastIndexOf(")") < 
				subStr.lastIndexOf("(")) {
			rightBracketPos = subStr.lastIndexOf(")");
		} else {
			while ((i <= formula.length() - 1) && !(right.size() == 1 && right.peek() == ')')) {
				if (formula.charAt(i) == '(') {
					right.push('(');
				} else if (formula.charAt(i) == ')') {
					if (right.size() > 0) {
						right.pop();						
					} else {
						break;
					}
				} else {
					//TODO 既不是 '(' 也不是 ')'
				}
				i++;
			}
		}
		rightBracketPos = i;
		
		String relatedStr = formula.substring(leftBracketPos, rightBracketPos + 1);
		System.out.println(relatedStr);
//		if () {
//			
//		}
		
		return false;
	}
	
	/**
	 * 检测括号是否合格
	 * formula 是经过预处理的，R 和 U 已经合并为 R
	 * @author Kevin Chan
	 */
	private boolean isBracketValid(String formula) {
		List<Integer> positionList = getPositionList();
		for (int position: positionList) {
			if (!isSingleBracketValid(formula, position)) {
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * 检测 LTL 中的双目运算符，如 R 和 U
	 * 
	 * @author Kevin Chan 
	 */
	public boolean specialCheck() {
		String formula = this.formula;
		formula = formula.replaceAll("R", "U"); // R 都替换为 U
		
		for (String op: binaryOp) {
			formula = formula.replaceAll(op, "");
		}
		
		formula = formula.replaceAll("[a-z0-9]+", "");		
		
		if (formula.contains("UU")) {
			return false;
		} else {
			return true;
		}
		
//		if (isBracketValid(formula)) {
//			return true;
//		} else {
//			return false;
//		}
	}
	
	public void isValid() throws ScriptException {
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByName("js");
		
		String replacedString = getReplaced(this.formula);
				
		Object result = engine.eval(replacedString);
		System.out.println(result);
		this.replacedFormula = replacedString;
	}

	public String getFormula() {
		return formula;
	}

	public void setFormula(String formula) {
		this.formula = formula;
	}
}
