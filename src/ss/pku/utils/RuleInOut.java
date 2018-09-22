package ss.pku.utils;

public class RuleInOut {
	String ruleName;
	PairRule pairRule;
	
	public RuleInOut() {
		this.ruleName = "ÔÝÎÞ";
		PairRule pairRule = new PairRule("ÔÝÎÞ", "ÔÝÎÞ");
		this.pairRule = pairRule;
	}

	public RuleInOut(String ruleName, PairRule pairRule) {
		this.ruleName = ruleName;
		this.pairRule = pairRule;
	}
	
	public RuleInOut(String ruleName, String inputCase, String outputCase) {
		this.ruleName = ruleName;
		PairRule pairRule = new PairRule(inputCase, outputCase);
		this.pairRule = pairRule;
	}
	
	public String getRuleName() {
		return ruleName;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}

	public PairRule getPairRule() {
		return pairRule;
	}

	public void setPairRule(PairRule pairRule) {
		this.pairRule = pairRule;
	}
}