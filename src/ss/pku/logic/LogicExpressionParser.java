package ss.pku.logic;

public class LogicExpressionParser {
	private static final String implyLabel = "->";
	
	private final String logicExpression;

	private String exp1;
	private String exp2;
	
	public LogicExpressionParser(String logicExpression) {
		super();
		this.logicExpression = logicExpression;
	}

	public String getLogicExpression() {
		return logicExpression;
	}
	
	public void generateSubExp() {
		String exp[] = logicExpression.split(implyLabel);
		this.exp1 = exp[0];
		this.exp2 = exp[1];		
	}	
}
