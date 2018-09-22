package ss.pku.logic;

public class BasicProp {
	private boolean isNegated = true;
	
	private String propName;

	
	
	public boolean isNegated() {
		return isNegated;
	}

	public void setNegated(boolean isNegated) {
		this.isNegated = isNegated;
	}

	public String getPropName() {
		return propName;
	}

	public void setPropName(String propName) {
		this.propName = propName;
	}
}
