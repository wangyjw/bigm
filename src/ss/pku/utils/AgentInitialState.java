package ss.pku.utils;

public class AgentInitialState {
	private String agentName;
	private String initialState;
	
	public AgentInitialState(String agentName, String initialState) {
		this.agentName = agentName;
		this.initialState = initialState;
	}

	public String getAgentName() {
		return agentName;
	}

	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}

	public String getInitialState() {
		return initialState;
	}

	public void setInitialState(String initialState) {
		this.initialState = initialState;
	}
}
