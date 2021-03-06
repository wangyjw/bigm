package dk.itu.bigm.model;

import java.util.List;

import org.bigraph.model.Control;
import org.bigraph.model.ModelObject.ChangeExtendedData;
import org.bigraph.model.ModelObject.ExtendedDataValidator;
import org.bigraph.model.PortSpec;
import org.bigraph.model.assistants.PropertyScratchpad;
import org.bigraph.model.assistants.BigmProperty;
import org.bigraph.model.changes.ChangeRejectedException;
import org.bigraph.model.changes.IChange;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;

import static org.bigraph.model.assistants.ExtendedDataUtilities.getProperty;
import static org.bigraph.model.assistants.ExtendedDataUtilities.setProperty;

public abstract class ControlUtilities {
	private ControlUtilities() {}

	@BigmProperty(fired = Integer.class, retrieved = Integer.class)
	public static final String SEGMENT =
			"eD!+dk.itu.bigm.model.PortSpec.segment";
	
	public static int getSegment(PortSpec p) {
		return getSegment(null, p);
	}

	public static int getSegment(PropertyScratchpad context, PortSpec p) {
		Integer i = getProperty(context, p, SEGMENT, Integer.class);
		if (i == null) {
			recalculatePosition(context, p);
			i = getProperty(context, p, SEGMENT, Integer.class);
		}
		return i;
	}

	public static void setSegment(PortSpec p, int i) {
		setSegment(null, p, i);
	}

	public static void setSegment(
			PropertyScratchpad context, PortSpec p, int i) {
		setProperty(context, p, SEGMENT, i);
	}

	public static IChange changeSegment(PortSpec p, int i) {
		return p.changeExtendedData(SEGMENT, i);
	}

	@BigmProperty(fired = Double.class, retrieved = Double.class)
	public static final String DISTANCE =
			"eD!+dk.itu.bigm.model.PortSpec.distance";
	
	private static void recalculatePosition(
			PropertyScratchpad context, PortSpec p) {
		int segment = 0;
		double distance = 0;
		
		Object shape = getShape(context, p.getControl(context));
		List<? extends PortSpec> l = p.getControl(context).getPorts(context);
		int index = l.indexOf(p) + 1;
		
		if (shape instanceof PointList) {
			PointList pl = (PointList)shape;
			int pls = pl.size();
			distance = (((double)pls) / l.size()) * index;
			
			segment = (int)Math.floor(distance);
			distance -= segment;
			segment %= pls;
		} else if (shape instanceof Ellipse) {
			segment = 0;
			distance = (1.0 / l.size()) * index;
		}
		
		setSegment(context, p, segment);
		setDistance(context, p, distance);
	}
	
	public static double getDistance(PortSpec p) {
		return getDistance(null, p);
	}

	public static double getDistance(
			PropertyScratchpad context, PortSpec p) {
		Double d = getProperty(context, p, DISTANCE, Double.class);
		if (d == null) {
			recalculatePosition(context, p);
			d = getProperty(context, p, DISTANCE, Double.class);
		}
		return d;
	}

	public static void setDistance(PortSpec p, double d) {
		setDistance(null, p, d);
	}

	public static void setDistance(
			PropertyScratchpad context, PortSpec p, double d) {
		setProperty(context, p, DISTANCE, d);
	}

	public static IChange changeDistance(PortSpec p, double d) {
		return p.changeExtendedData(DISTANCE, d);
	}

	@BigmProperty(fired = Object.class, retrieved = Object.class)
	public static final String SHAPE =
			"eD!+dk.itu.bigm.model.Control.shape";
	
	public static Object getShape(Control c) {
		return getShape(null, c);
	}

	public static Object getShape(PropertyScratchpad context, Control c) {
		Object o = getProperty(context, c, SHAPE, Object.class);
		if (!(o instanceof PointList || o instanceof Ellipse)) {
			o = new Ellipse(new Rectangle(0, 0, 30, 30)).
				getPolygon(Math.max(3, c.getPorts(context).size()));
			setShape(context, c, o);
			
			for (PortSpec p : c.getPorts(context))
				recalculatePosition(context, p);
		}
		return o;
	}

	public static void setShape(Control c, Object s) {
		setShape(null, c, s);
	}

	public static void setShape(
			PropertyScratchpad context, Control c, Object s) {
		if (s instanceof PointList || s instanceof Ellipse)
			setProperty(context, c, SHAPE, s);
	}

	public static IChange changeShape(Control c, Object s) {
		return c.changeExtendedData(SHAPE, s);
	}

	private static final ExtendedDataValidator labelValidator =
			new ExtendedDataValidator() {
		@Override
		public void validate(ChangeExtendedData c, PropertyScratchpad context)
				throws ChangeRejectedException {
			if (!(c.newValue instanceof String)) {
				throw new ChangeRejectedException(c, "Labels must be strings");
			} else if (((String)c.newValue).length() == 0) {
				throw new ChangeRejectedException(c,
						"Labels must not be empty");
			}
		}
	};
	
	@BigmProperty(fired = String.class, retrieved = String.class)
	public static final String LABEL =
			"eD!+dk.itu.bigm.model.Control.label";
	
	public static String getLabel(Control c) {
		return getLabel(null, c);
	}

	public static String labelFor(String s) {
		return (s != null ? (s.length() > 0 ? s.substring(0, 1) : s) : "?");
	}

	public static String getLabel(PropertyScratchpad context, Control c) {
		String s = getProperty(context, c, LABEL, String.class);
		if (s == null)
			setLabel(context, c, s = labelFor(c.getName(context)));
		return s;
	}

	public static void setLabel(Control c, String s) {
		setLabel(null, c, s);
	}

	public static void setLabel(
			PropertyScratchpad context, Control c, String s) {
		setProperty(context, c, LABEL, s);
	}

	public static IChange changeLabel(Control c, String s) {
		return c.changeExtendedData(LABEL, s, labelValidator);
	}
}
