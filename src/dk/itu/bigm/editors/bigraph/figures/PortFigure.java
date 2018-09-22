package dk.itu.bigm.editors.bigraph.figures;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Rectangle;


public class PortFigure extends AbstractFigure {
	private Label portType = new Label();
	
	public void SetLable(String text) {
		portType.setText(text);
//		Image image = new Image(device, filename);
//		portType.setIcon(image);
	}
	
	public PortFigure(String portTypeText) {
		super();
		this.SetLable(portTypeText);
//		System.out.println(portTypeText+"test");
		portType.setForegroundColor(ColorConstants.black);
		add(portType, 0);
		portType.setSize(16, 16);
		setConstraint(portType, new Rectangle(1 , 1, -1, -1));
		setBackgroundColor(ColorConstants.white);
	}
	
	@Override
	protected void fillShape(Graphics graphics) {
		Rectangle a = start(graphics);
		Rectangle r = getConstraint();
		try {
			a.setSize(8, 8);
			a.setLocation(2, 2);
			graphics.fillOval(a);
			//graphics.drawOval(a);
		} finally {
			stop(graphics);
		}
	}
}
