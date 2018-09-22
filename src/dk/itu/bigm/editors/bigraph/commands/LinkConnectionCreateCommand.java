package dk.itu.bigm.editors.bigraph.commands;

import java.util.ArrayList;
import java.util.List;

import org.bigraph.model.Bigraph;
import org.bigraph.model.Control;
import org.bigraph.model.Edge;
import org.bigraph.model.FormRules;
import org.bigraph.model.FormationRule;
import org.bigraph.model.InnerName;
import org.bigraph.model.Link;
import org.bigraph.model.LinkSort;
import org.bigraph.model.Node;
import org.bigraph.model.OuterName;
import org.bigraph.model.Point;
import org.bigraph.model.Port;
import org.bigraph.model.Signature;
import org.bigraph.model.changes.ChangeGroup;
import org.bigraph.model.changes.ChangeRejectedException;
import org.eclipse.jface.dialogs.MessageDialog;

import dk.itu.bigm.editors.bigraph.parts.LinkPart;
import scala.util.parsing.combinator.testing.Str;

/**
 * A LinkConnectionCreateCommand is in charge of creating and updating {@link
 * Link}s on a {@link Bigraph} in response to user input. It can either join a
 * {@link Point} to an existing {@link Link}, or join two {@link Point}s
 * together, creating a new {@link Edge} in the process.
 * </ul>
 * @author alec
 */
public class LinkConnectionCreateCommand extends ChangeCommand {
	private ChangeGroup cg = new ChangeGroup();
	private Object first = null, second = null;
	
	public LinkConnectionCreateCommand() {
		setChange(cg);
	}

	public void setFirst(Object e) {
		if (!(e instanceof LinkPart.Connection)) {
			first = e;
		} else first = ((LinkPart.Connection)e).getLink();
	}
	
	public void setSecond(Object e) {
		if (!(e instanceof LinkPart.Connection)) {
			second = e;
		} else second = ((LinkPart.Connection)e).getLink();
	}
	
	/**
	 * @return if this link is validated in formation rules
	 */
	@SuppressWarnings("unchecked")
	private boolean validateSorts(String firstSort, String secondSort, Signature signature) {
		//!TODO If it's not clearly defined that two port cannot be connected, they should be connected. 		
		/**
		 * changed by Kevin Chan
		 * @author quantus@live.cn
		 */
		boolean result = true;

		if (firstSort != null && !firstSort.contains("_") &&
				secondSort != null && !secondSort.contains("_")) {
			String fSort = firstSort.split(":")[0];
			String sSort = secondSort.split(":")[0];
			FormRules frs = signature.getFormRules();
//			System.out.println(fSort + " " + sSort);
			if (frs != null && frs.getSortSet() != null
					&& frs.getSortSet().getLinkSorts() != null) {
				ArrayList<LinkSort> pss = (ArrayList<LinkSort>) frs
						.getSortSet().getLinkSorts();
				ArrayList<String> psString = new ArrayList<String>();
				for (LinkSort s : pss) {
					psString.add(s.getName());
				}
				boolean sortValid = (psString.indexOf(fSort) >= 0)
						&& (psString.indexOf(sSort) >= 0);
				if (sortValid) {
					for (FormationRule fr : frs.getFormationRules()) {
						if (fr.getType() != null
								&& fr.getType().contains("Link Sorting")) {
							if ((fSort.equals(fr.getSort1()) && sSort.equals(fr
									.getSort2()))
									|| (fSort.equals(fr.getSort2()) && sSort
											.equals(fr.getSort1()))) {
								if ("link".equals(fr.getConstraint())) {
									return true;
								} else if ("not link".equals(fr.getConstraint())) {
									return false;
								} else {
									return true;
								}
							}
						}
					}
					return true;
				}
			} else {
				return true;				
			}
		} else {
			if(firstSort.contains(":") && secondSort.contains(":")){
				if(firstSort.split(":")[0].equals(secondSort.split(":")[0])){
					if((firstSort.split(":")[1].equals("start") && secondSort.split(":")[1].equals("end"))
							|| (firstSort.split(":")[1].equals("end") && secondSort.split(":")[1].equals("start"))
							|| (firstSort.split(":")[1].equals("none") && secondSort.split(":")[1].equals("none"))){
						result = true;
					}
				}
			}else if(!firstSort.contains(":") && secondSort.contains(":")){
				if(firstSort.equals(secondSort.split(":")[0]))
					result = true;
			}else if(firstSort.contains(":") && !secondSort.contains(":")){
				if(secondSort.equals(firstSort.split(":")[0]))
					result = true;
			}else if(!firstSort.contains(":") && !secondSort.contains(":")){
				if(firstSort.equals(secondSort))
					result = true;
			}
		}
		return result;
	}

	/**
	 * @author Kevin Chan
	 * return if the ports satisfies the Node Port Constraint 
	 */
	public boolean validateNodePortSort(Port first, Port second) {
		String firstSort = first.getNodeNameAtIndex();
		String secondSort = second.getNodeNameAtIndex();
		
		Signature signature = first.getBigraph().getSignature();
		FormRules frs = signature.getFormRules();
		List<FormationRule> relatedNodePortRule = new ArrayList<FormationRule>();

		for (FormationRule fr : frs.getFormationRules()) {
			if (fr.getType() != null
				&& fr.getType().contains("Node Port")) {
				relatedNodePortRule.add(fr);
			}
		}
		
		for (FormationRule fr : relatedNodePortRule) {
			if ((firstSort.equals(fr.getSort1()) && secondSort.equals(fr.getSort2()))
				|| (firstSort.equals(fr.getSort2()) && secondSort.equals(fr.getSort1()))) {
//				System.out.println(firstSort + " and " + secondSort + " result: " + fr.getConstraint().equals("link"));
				return !fr.getConstraint().equals("not link");
			} else {
				//!TODO
			}
		}
		
		return true;
	}
	
	/**
	 * @author Kevin Chan
	 * return the Node of the Port
	 */
	@SuppressWarnings("unused")
	private String getNodeName(Port port) {
		return port.getParent().getName();
		
	}
	
	/**
	 * @author Kevin Chan
	 * return the index of the Port of the Node 
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	private int getIndex(Port port) {
		Node node = port.getParent();
		List<Port> ports = (List<Port>) node.getPorts();
		for (int i = 0;i < ports.size();i++) {
			if (ports.get(i).equals(port)) {
				return i;
			}
		}		
		return -1;
	}
	
	/**
	 * @author Kevin Chan
	 * return if a closed link has sort s 
	 */
	@SuppressWarnings("unused")
	private boolean ifEdgeHasSortS(Edge edge) {
		@SuppressWarnings("unchecked")
		List<Point> points =  (List<Point>) edge.getPoints();
		List<String> sortOfPorts = new ArrayList<String>();
		for (Point point:points) {
			Port port = (Port) point;
			sortOfPorts.add(port.getSpec().getRealPortSort());
		}
		return isSLink(sortOfPorts);
	}
	
	
	/**
	 * @author Kevin Chan
	 */
	private boolean isSLink(List<String> sortsOfPorts) {
		int sPointCount = 0;
		for (String sort: sortsOfPorts) {
			if (sort != null && !sort.isEmpty() && sort.equals("s-point")) {
				sPointCount++;
			}
		}
		return (sPointCount == 1)?true:false;
	}
	
	/**
	 * @author Kevin 
	 * deciding the real sort of link (instead of port)
	 */
	public String getEdgeSortInConditionEventPetriNet(Port first, Port second) {
		String firstSort = first.getSpec().getRealPortSort();
		String secondSort = second.getSpec().getRealPortSort();
		List<String> sortsOfPorts = new ArrayList<String> ();
		sortsOfPorts.add(firstSort);
		sortsOfPorts.add(secondSort);
		if (isSLink(sortsOfPorts)) {
			return LinkSort.S_LINK;
		} else {
			return LinkSort.UNKNOWN_LINK;
		}
	}
	
	/**
	 * @author Kevin Chan
	 * return if the connection is valid 
	 */
	public boolean validateNodePortSortByPortAndEdge(Port first, Edge edge) {
		@SuppressWarnings("unchecked")
		List<Port> ports = ((List<Port>) edge.getPoints());

		for (Port second : ports) {
			if (!validateNodePortSort(first, second)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void prepare() {
		cg.clear();
		if (first instanceof Port && second instanceof Port) {
			if (true
					&& validateNodePortSort((Port)first, (Port)second)
					&& validateSorts(((Port)first).getSpec().getRealPortSort(), ((Port)second).getSpec().getRealPortSort(), ((Port)second).getBigraph().getSignature())
//					&& validateSorts(((Port)first).getSpec().getPortSort(), ((Port)second).getSpec().getPortSort(), ((Port)second).getBigraph().getSignature())
					) {
				Bigraph b = ((Port)first).getBigraph();
				setTarget(b);
				//!TODO edit by Kevin in naming link for Conditon Event Petri Nets
				Control conditon = b.getBigraph().getSignature().getControl("condition");
				Control event_1_1 = b.getBigraph().getSignature().getControl("event_1_1");
				String newLinkSort = "";
				if (conditon != null && event_1_1 != null) {
					// new
					newLinkSort = this.getEdgeSortInConditionEventPetriNet((Port)first, (Port)second);
				} else {
					// old 
					newLinkSort = ((Port)first).getSpec().getPortSort().split(":")[0];
				}
				//!TODO ends
				
				Edge ed = new Edge(newLinkSort);
				//!TODO start 
				//!TODO ends
				
				cg.add(b.changeAddChild(ed, b.getFirstUnusedName(ed)));
				cg.add(((Port)first).changeConnect(ed));
				cg.add(((Port)second).changeConnect(ed));
//				System.out.println(ed.getName());
//				ifEdgeHasSortS(ed);
			} else {
				//!TODO about port connection
//				System.out.println(((Port)first).getName() + "    and     " + ((Port)second).getName());
//				MessageDialog.openWarning(null, "conflict on Link Sort", "Please check the link sort of the port");
			}
		}
		else if (first instanceof Port && second instanceof InnerName
				&& validateSorts(((Port)first).getSpec().getPortSort(), ((InnerName)second).getInnerSort(), ((Port)first).getBigraph().getSignature()))
		{
			Bigraph b = ((Port)first).getBigraph();
			setTarget(b);
			Edge ed = new Edge(((Port)first).getSpec().getPortSort().split(":")[0]);
			cg.add(b.changeAddChild(ed, b.getFirstUnusedName(ed)));
			cg.add(((Port)first).changeConnect(ed));
			cg.add(((InnerName)second).changeConnect(ed));
		} 
		else if (first instanceof InnerName && second instanceof Port
				&& validateSorts(((InnerName)first).getInnerSort(), ((Port)second).getSpec().getPortSort(), ((Port)second).getBigraph().getSignature()))
		{
			Bigraph b = ((InnerName)first).getBigraph();
			setTarget(b);
			Edge ed = new Edge(((InnerName)first).getInnerSort().split(":")[0]);
			cg.add(b.changeAddChild(ed, b.getFirstUnusedName(ed)));
			cg.add(((InnerName)first).changeConnect(ed));
			cg.add(((Port)second).changeConnect(ed));
		} 
		else if (first instanceof InnerName && second instanceof InnerName
				&& validateSorts(((InnerName)first).getInnerSort(), ((InnerName)second).getInnerSort(), ((InnerName)second).getBigraph().getSignature()))
		{
			Bigraph b = ((InnerName)first).getBigraph();
			setTarget(b);
			Edge ed = new Edge(((InnerName)first).getInnerSort().split(":")[0]);
			cg.add(b.changeAddChild(ed, b.getFirstUnusedName(ed)));
			cg.add(((InnerName)first).changeConnect(ed));
			cg.add(((InnerName)second).changeConnect(ed));
		} 
		else if (first instanceof Port && second instanceof Edge
				/**
				 * @author Kevin Chan
				 */
				&& validateNodePortSortByPortAndEdge((Port)first, (Edge)second)
				// ends
				&& validateSorts(((Port)first).getSpec().getPortSort(), ((Edge)second).getLinkSort(), ((Port)first).getBigraph().getSignature()))
		{
			setTarget(((Port)first).getBigraph());
			cg.add(((Port)first).changeConnect((Edge)second));
		} 
		else if (first instanceof Edge && second instanceof Port
				/**
				 * @author Kevin Chan
				 */
				&& validateNodePortSortByPortAndEdge((Port)second, (Edge)first)
				// ends
				&& validateSorts(((Port)second).getSpec().getPortSort(), ((Edge)first).getLinkSort(), ((Port)second).getBigraph().getSignature()))
		{
			setTarget(((Port)second).getBigraph());
			cg.add(((Port)second).changeConnect((Edge)first));
		} 
		else if (first instanceof Port && second instanceof OuterName
				&& (((OuterName)second).getLinkSort() == null 
				|| ((OuterName)second).getLinkSort().equals("")
				|| ((OuterName)second).getPoints().size() == 0))
		{
			setTarget(((Port)first).getBigraph());
			((OuterName)second).setLinkSort(((Port)first).getSpec().getPortSort());
			cg.add(((Port)first).changeConnect((OuterName)second));
		} 
		else if (first instanceof Port && second instanceof OuterName
				&& validateSorts(((OuterName)second).getLinkSort(), ((Port)first).getSpec().getPortSort(), ((Port)first).getBigraph().getSignature()))
		{
			setTarget(((Port)first).getBigraph());
			cg.add(((Port)first).changeConnect((OuterName)second));
		} 
		else if (first instanceof OuterName && second instanceof Port
				&& validateSorts(((OuterName)first).getLinkSort(), ((Port)second).getSpec().getPortSort(), ((Port)second).getBigraph().getSignature()))
		{
			setTarget(((Port)second).getBigraph());
			cg.add(((Port)second).changeConnect((OuterName)first));
		} 
		else if (first instanceof OuterName && second instanceof Port
				&& (((OuterName)first).getLinkSort() == null 
				|| ((OuterName)first).getLinkSort().equals("")
				|| ((OuterName)first).getPoints().size() == 0))
		{
			setTarget(((Port)second).getBigraph());
			((OuterName)first).setLinkSort(((Port)second).getSpec().getPortSort());
			cg.add(((Port)second).changeConnect((OuterName)first));
		}
		else if (first instanceof InnerName && second instanceof Edge
				&& validateSorts(((InnerName)first).getInnerSort(), ((Edge)second).getLinkSort(), ((Edge)second).getBigraph().getSignature()))
		{
			setTarget(((InnerName)first).getBigraph());
			((InnerName)first).setInnerSort(((Edge)second).getLinkSort() + ":none");
			cg.add(((InnerName)first).changeConnect((Edge)second));
		} 
		else if (first instanceof Edge && second instanceof InnerName
				&& validateSorts(((InnerName)second).getInnerSort(), ((Edge)first).getLinkSort(), ((InnerName)second).getBigraph().getSignature()))
		{
			setTarget(((InnerName)second).getBigraph());
			((InnerName)second).setInnerSort(((Edge)first).getLinkSort() + ":none");
			cg.add(((InnerName)second).changeConnect((Edge)first));
		} 
		else if (first instanceof InnerName && second instanceof OuterName
				&& (((OuterName)second).getLinkSort() == null 
				|| ((OuterName)second).getLinkSort().equals("")
				|| ((OuterName)second).getPoints().size() == 0))
		{
			setTarget(((InnerName)first).getBigraph());
			((OuterName)second).setLinkSort(((InnerName)first).getInnerSort());
			cg.add(((InnerName)first).changeConnect((OuterName)second));
		} 
		else if (first instanceof InnerName && second instanceof OuterName
				&& validateSorts(((OuterName)second).getLinkSort(), ((InnerName)first).getInnerSort(), ((OuterName)second).getBigraph().getSignature()))
		{
			setTarget(((InnerName)first).getBigraph());
			((InnerName)first).setInnerSort(((InnerName)first).getInnerSort().split(":")[0] + ":none");
			cg.add(((InnerName)first).changeConnect((OuterName)second));
		} 
		else if (first instanceof OuterName && second instanceof InnerName
				&& validateSorts(((OuterName)first).getLinkSort(), ((InnerName)second).getInnerSort(), ((InnerName)second).getBigraph().getSignature()))
		{
			setTarget(((InnerName)second).getBigraph());
			((InnerName)second).setInnerSort(((InnerName)second).getInnerSort().split(":")[0] + ":none");
			cg.add(((InnerName)second).changeConnect((OuterName)first));
		} 
		else if (first instanceof OuterName && second instanceof InnerName
				&& (((OuterName)first).getLinkSort() == null 
				|| ((OuterName)first).getLinkSort().equals("")
				|| ((OuterName)first).getPoints().size() == 0))
		{
			setTarget(((InnerName)second).getBigraph());
			((OuterName)first).setLinkSort(((InnerName)second).getInnerSort());
			cg.add(((InnerName)second).changeConnect((OuterName)first));
		} else {
//			System.out.println(first.toString());
//			System.out.println(second.toString());
//			System.out.println("Something wrong has just happened @dk.itu.bigm.editors.bigraph.commands LinkConnectionCreateCommand 263");
		}
	}
}
