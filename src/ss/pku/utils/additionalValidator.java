package ss.pku.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 附件信息的校验
 * @author Kevin Chan 
 */
public final class additionalValidator {

	public static boolean isValid(String str, String textName) {
		if (textName.equals("condition")) {
			return isConditionValid(str);
		} else if (textName.equals("exp")) {
			return isExpValid(str);
		} else if (textName.equals("sysClk")) {
			return isSysClkValid(str);
		} else if (textName.equals("inputCase")) {
			return isInputCaseValid(str);
		} else if (textName.equals("outputCase")) {
			return isOutputCaseValid(str);
		} else if (textName.equals("ruleName")) {
			return isRuleNameValid(str);
		} else if (textName.equals("probability")) {
			return isProbabilityValid(str);
		} else {
			return true;			
		}
	}
	
	/**
	 * 
	 * @author Kevin Chan 
	 */
	private static boolean isConditionValid(String condition) {
		if (condition == null || condition.isEmpty()) {
			return true;			
		} else {
			String[] boolExprs = condition.split(",");
			String boolExpReg = "^[A-Za-z][A-Za-z1-9]*[=]{2}[A-Za-z1-9]+$";
			Pattern pattern = Pattern.compile(boolExpReg);
			for (int i = 0;i < boolExprs.length;i++) {
				Matcher matcher = pattern.matcher(boolExprs[i]);
				if (!matcher.matches()) {
					return false;
				}
			}
			return true;
		}
	}
	
	private static boolean isExpValid(String exp) {
		if (exp == null || exp.isEmpty()) {
			return true;			
		} else {
			String[] boolExprs = exp.split(",");
			String boolExpReg = "^[A-Za-z][A-Za-z1-9]*[=][A-Za-z1-9]+$";
			Pattern pattern = Pattern.compile(boolExpReg);
			for (int i = 0;i < boolExprs.length;i++) {
				Matcher matcher = pattern.matcher(boolExprs[i]);
				if (!matcher.matches()) {
					return false;
				}
			}
			return true;
		}
	}
	
	private static boolean isSysClkValid(String sys) {
		return sys == null || sys.isEmpty() || sys.matches("^\\d+$");
	}
	
	private static boolean isInputCaseValid(String inputCase) {
		//TODO
		return true;
	}
	
	private static boolean isOutputCaseValid(String outputCase) {
		//TODO		
		return true;
	}
	
	private static boolean isRuleNameValid(String ruleName) {
		if (ruleName == null || ruleName.isEmpty()) { 
			return true;
		} else {			
			char c = ruleName.charAt(0);
			if ((65 <= c && c <= 90)
					|| (97 <= c && c <= 122)) {
				return true;
			} else {
				return false;
			}					
		}
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
}
