package dk.itu.bigm.editors.simulation_spec;

import java.beans.PropertyChangeEvent;

import org.bigraph.model.BigraphAP;
import org.bigraph.model.SimulationSpec;
import org.eclipse.jface.viewers.AbstractListViewer;

import dk.itu.bigm.utilities.ui.jface.ModelObjectListContentProvider;

/**
 * ´¢´æ AP µÄ content provider
 * 
 * @author Kevin Chan 
 */
public class SimulationSpecAPContentProvider extends ModelObjectListContentProvider{
	public SimulationSpecAPContentProvider(AbstractListViewer alv) {
		super(alv);
	}

	@Override
	public Object[] getElements(Object inputElement) {
		System.out.println(inputElement.toString()); // add by Kevin Chan paper
		return ((SimulationSpec)inputElement).getPropositions().toArray();
	}

	@Override
	protected SimulationSpec getInput() {
		Object o = super.getInput();
		return (o instanceof SimulationSpec ? (SimulationSpec)o : null);
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (!evt.getSource().equals(getInput()))
			return;
		String propertyName = evt.getPropertyName();
		if (SimulationSpec.PROPERTY_PROPOSITION.equals(propertyName)) { // add by Kevin Chan 2017 paper
			BigraphAP 
				oldValue = (BigraphAP)evt.getOldValue(),
				newValue = (BigraphAP)evt.getNewValue();
			if (oldValue == null && newValue != null) { // add
				AbstractListViewer viewer = getViewer(); // test
				System.out.println(viewer.toString());
				viewer.insert(newValue, 
						getInput().getPropositions().indexOf(newValue));
			} else if (oldValue != null && newValue == null) { // remove
				getViewer().remove(oldValue);
			}
		}
	}
}
