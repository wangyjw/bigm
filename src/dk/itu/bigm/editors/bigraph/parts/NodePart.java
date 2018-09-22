package dk.itu.bigm.editors.bigraph.parts;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

import org.bigraph.model.Control;
import org.bigraph.model.Layoutable;
import org.bigraph.model.Node;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.gef.EditPolicy;

import dk.itu.bigm.editors.bigraph.figures.NodeFigure;
import dk.itu.bigm.editors.bigraph.policies.LayoutableDeletePolicy;
import dk.itu.bigm.editors.bigraph.policies.LayoutableLayoutPolicy;
import dk.itu.bigm.model.ColourUtilities;
import dk.itu.bigm.model.ControlUtilities;
import dk.itu.bigm.model.Ellipse;
import dk.itu.bigm.model.LayoutUtilities;

import org.bigraph.extensions.param.ParameterUtilities;

/**
 * NodeParts represent {@link Node}s, the basic building block of bigraphs.
 * 
 * @see Node
 * @author alec
 */
public class NodePart extends ContainerPart {
	@Override
	public Node getModel() {
		return (Node) super.getModel();
	}

	@Override
	protected IFigure createFigure() {
		return new NodeFigure();
	}

	@Override
	protected void createEditPolicies() {
		super.createEditPolicies();

		installEditPolicy(EditPolicy.LAYOUT_ROLE, new LayoutableLayoutPolicy());
		installEditPolicy(EditPolicy.COMPONENT_ROLE,
				new LayoutableDeletePolicy());

	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		String name = evt.getPropertyName();
		
		if (LayoutUtilities.LAYOUT.equals(name)){
			fittedPolygon = null;
		}
		super.propertyChange(evt);
		if (ColourUtilities.FILL.equals(name)
				|| ColourUtilities.OUTLINE.equals(name)
				|| ParameterUtilities.PARAMETER.equals(name)) {
			refreshVisuals();
		}
	}

	private PointList fittedPolygon;

	@Override
	protected void refreshVisuals() {
		super.refreshVisuals();

		NodeFigure figure = (NodeFigure) getFigure();
		Node model = getModel();
		Control control = model.getControl();

		Object shape = ControlUtilities.getShape(control);
		if (shape instanceof PointList && fittedPolygon == null)
			fittedPolygon = LayoutUtilities.fitPolygon((PointList) shape,
					LayoutUtilities.getLayout(model));
		figure.setShape(shape instanceof PointList ? fittedPolygon
				: Ellipse.SINGLETON);

		// ��ʾ������
		String modelName = model.getName();
		String label = "";
		String parameter = "";
		// �����￪ʼ�ǶԼ������͵�control��name���д���
		// class
		if (modelName.startsWith("_class_")) {
			// ���name��_class_��ͷ ��ʾ���control��������� ������ĳʵ��
			label = " " + ControlUtilities.getLabel(control);
		}else if(modelName.startsWith("_anonymousObj_")){// ��������anonymousObj
			label = " :" + ControlUtilities.getLabel(control);
		} else {// ��ͨʵ��
			label = " " + model.getName() + ":" + ControlUtilities.getLabel(control);
		}
		parameter = ParameterUtilities.getParameter(model);
		if (parameter != null)
			label = parameter + " : " + label;
		figure.setLabel(label);
		figure.setToolTip(getToolTip());

		figure.setBackgroundColor(getFill(ColourUtilities.getFill(model)));
		figure.setForegroundColor(getOutline(ColourUtilities.getOutline(model)));
		
		figure.repaint();
	}

	@Override
	public List<Layoutable> getModelChildren() {
		ArrayList<Layoutable> children = new ArrayList<Layoutable>(getModel()
				.getChildren());
		children.addAll(getModel().getPorts());
		return children;
	}

	@Override
	public String getToolTip() {
		String tip = "";
		String modelName = getModel().getName();
		if (modelName.startsWith("_class_")) {
			// ���name��_class_��ͷ ��ʾ���control��������� ������ĳʵ��
			tip = getModel().getControl().getName();
		}else if(modelName.startsWith("_anonymousObj_")){// ��������
			tip = getModel().getControl().getName();
		} else {
			tip = getModel().getControl().getName() + " "
					+ getModel().getName();
		}
		return tip;
	}
}
