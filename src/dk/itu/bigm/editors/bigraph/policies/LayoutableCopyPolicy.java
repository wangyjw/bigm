package dk.itu.bigm.editors.bigraph.policies;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.requests.GroupRequest;

import dk.itu.bigm.editors.bigraph.CombinedCommandFactory;

public class LayoutableCopyPolicy {
	
	protected Command createCopyCommand(GroupRequest copyRequest) {
		return CombinedCommandFactory.createDeleteCommand(copyRequest);
	}
}
