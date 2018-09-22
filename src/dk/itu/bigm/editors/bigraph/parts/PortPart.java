package dk.itu.bigm.editors.bigraph.parts;

import java.beans.PropertyChangeEvent;

import org.bigraph.model.Link;
import org.bigraph.model.Port;
import org.bigraph.model.PortSpec;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPolicy;

import dk.itu.bigm.editors.bigraph.figures.PortFigure;
import dk.itu.bigm.editors.bigraph.figures.assistants.FixedPointAnchor.Orientation;
import dk.itu.bigm.editors.bigraph.policies.EdgeCreationPolicy;
import dk.itu.bigm.model.ColourUtilities;
import dk.itu.bigm.model.LayoutUtilities;

/**
 * PortParts represent {@link Port}s, sites on {@link Node}s which can be
 * connected to {@link Edge}s.
 * @see Port
 * @author alec
 */
public class PortPart extends PointPart {
	private static String multiple = "abs";
	private static String single = "concrete";

	@Override
	public void activate() {
		super.activate();
		getModel().getParent().addPropertyChangeListener(this);
	}

	@Override
	public void deactivate() {
		getModel().getParent().removePropertyChangeListener(this);
		super.deactivate();
	}
	
	@Override
	protected IFigure createFigure() {
		String portTypeText = getPortTypeText();
		return new PortFigure(portTypeText);
	}
	
	@Override
	public Port getModel() {
		return (Port)super.getModel();
	}
	
	@Override
	public void installEditPolicy(Object key, EditPolicy editPolicy) {
		super.installEditPolicy(key, editPolicy);
	}
	
	@Override
	protected void createEditPolicies() {
		super.createEditPolicies();
		
		installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE, new EdgeCreationPolicy());
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		super.propertyChange(evt);
		String prop = evt.getPropertyName();
		Object source = evt.getSource();
		if (source == getModel().getParent() &&
				LayoutUtilities.LAYOUT.equals(prop))
			refreshVisuals();
	}
	
	@Override
	protected void refreshVisuals() {
		// TODO Auto-generated method stub
		super.refreshVisuals();
		
		PortFigure figure = (PortFigure) getFigure();
		String portTypeText = getPortTypeText();
//		portTypeText = "test517";//TODO add port index Kevin Chan paper
		figure.SetLable(portTypeText);
		figure.setToolTip(getToolTip());

		
		figure.setBackgroundColor(getFill(ColourUtilities.getFill(getModel())));
		figure.setForegroundColor(getOutline(ColourUtilities.getOutline(getModel())));

		figure.repaint();
	}
	
	@Override
	public Orientation getAnchorOrientation() {
		return Orientation.CENTER;
	}
	
	@Override
	public String getTypeName() {
		return "Port";
	}
	
	public String getPortTypeText() {
		//TODO
//		Port model = (Port) this.getModel();
		String result = "";
//		if (null != model) {
//			PortSpec spec = (PortSpec) model.getSpec();
//			if (null != spec) {
//				String portSort = spec.getPortSort();		
//				if (null == portSort) {
//					result = "";			
//				} else if (portSort.indexOf(PortPart.single) >= 0) {
//					result = "1";			
//				} else if (portSort.indexOf(PortPart.multiple) >= 0) {
//					result = "+";			
//				} else {
//					result = "";		
//				}
//			}
//			
//			if (!result.equals("")) {
//				result += (":"+model.getIndex());
//			} else {
//				result = (""+model.getIndex());
//			}
//			
//		} else {
//			//do nothing
//		}
//		

		return result;
	}
}
