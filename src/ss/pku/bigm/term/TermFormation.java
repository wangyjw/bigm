package ss.pku.bigm.term;

import java.util.List;

import org.bigraph.model.Bigraph;
import org.bigraph.model.BigraphAP;
import org.bigraph.model.assistants.BigmProperty;
import org.bigraph.model.ModelObject;

public class TermFormation {
	private Bigraph model;
	
	/**
	 * @author Kevin Chan
	 * The property name fired when the model changes.
	 */
	@BigmProperty(fired = BigraphAP.class, retrieved = List.class)
	public static final String PROPERTY_PROPOSITION = "SimulationSpecProposition";	
	
	protected void setModel(Bigraph model) {
		Bigraph oldModel = this.model;
		this.model = model;
//		firePropertyChange(PROPERTY_PROPOSITION, oldModel, model);
	}
}
