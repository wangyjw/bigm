package ss.pku.utils;

public class PairRule {
	private String inputCase;
	private String outputCase;
	
	public PairRule() {
		super();
	}

	public PairRule(String inputCase, String outputCase) {
		super();
		this.inputCase = inputCase;
		this.outputCase = outputCase;
	}
	
	public String getInputCase() {
		return inputCase;
	}
	
	public void setInputCase(String inputCase) {
		this.inputCase = inputCase;
	}
	
	public String getOutputCase() {
		return outputCase;
	}
	
	public void setOutputCase(String outputCase) {
		this.outputCase = outputCase;
	}
}
