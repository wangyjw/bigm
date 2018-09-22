package ss.pku.utils;

import java.util.ArrayDeque;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bigraph.model.changes.IChange;
import dk.itu.bigm.editors.rule.RuleEditor;

public class DemoTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		String[] result = "A¡úB¡ÅE¡ÄC".split("[¡ú,¡Ä,¡Å]");

		
//		String[] relatedRuleNames = "!stu&&book".split("[\\!,\\&\\&,\\|\\|,(,)]");		
//		System.out.println(relatedRuleNames.toString());
		
//		// add by Kevin Chan
//		private IChange savePoint = null;
//		private ArrayDeque<IChange>
//				undoBuffer = new ArrayDeque<IChange>(),
//				redoBuffer = new ArrayDeque<IChange>();
//		protected IChange getSavePoint() {
//			return savePoint;
//		}
//		
//		
//		
//		// ends
		
		
//		foo3();
		
		foo4("1");
		foo4("-1");
		foo4("test");
		foo4("101");
		foo4("75.5");
	}

	private static void foo3() {
		boolean isCon = isConditionValid("x1=2,x4=3");
		System.out.println(isCon);
	}
	
	private static boolean isProbabilityValid(String probability) {
		if (probability == null || probability.isEmpty()) {
			return true;
		} else {
		    Pattern pattern = Pattern.compile("^[-\\+]?[.\\d]*$");  
		    if (!pattern.matcher(probability).matches()) {
		    	return false;
		    } else {
		    	float f = Float.parseFloat(probability);
		    	return (0 <= f && f <= 100);		    	
		    }
		}
	}
	
	private static void foo4(String str) {
		boolean isPro = isProbabilityValid(str);

		System.out.println(isPro);
	}
	
	public static boolean isConditionValid(String condition) {
		String[] boolExprs = condition.split(",");
		String boolExpReg = "^[A-Za-z][A-Za-z1-9]+[=][A-Za-z1-9]+$";
		Pattern pattern = Pattern.compile(boolExpReg);
		for (int i = 0;i < boolExprs.length;i++) {
			Matcher matcher = pattern.matcher(boolExprs[i]);
			if (!matcher.matches()) {
				return false;
			}
		}
		return true;
	}
	
	private static void foo2() {
		String boolExp1 = "x1=1";
		String boolExp2 = "x23=y321";
		String boolExpReg = "^[A-Za-z][A-Za-z1-9]+[=][A-Za-z1-9]+$";
		Pattern pattern = Pattern.compile(boolExpReg);
		Matcher matcher1 = pattern.matcher(boolExp1);
		Matcher matcher2 = pattern.matcher(boolExp1);

		boolean rs1 = matcher1.matches();
		boolean rs2 = matcher2.matches();
		
		System.out.println(rs1);
		System.out.println(rs2);
	}
	
	private static void foo1() {
		String s1 = "103";
		String s2 = "-123";
		String s3 = "+2134";
		String s4 = "eaf";
		
		int i1 = Integer.parseInt(s1);
		int i2 = Integer.parseInt(s2);
		int i3 = Integer.parseInt(s3);
//		int i4 = Integer.parseInt(s4);
		
		String[] strArr = {s1, s2, s3};
		for (int i = 0;i < strArr.length;i++) {
			System.out.println(strArr[i].matches("^\\d+$"));
		}
		
		boolean b4 = s4.matches("[0-9]");
		
		System.out.println(i1 + i2 + i3);
		System.out.println(b4);
	}
}
