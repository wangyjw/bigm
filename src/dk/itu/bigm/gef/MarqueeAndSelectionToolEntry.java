package dk.itu.bigm.gef;

import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.gef.tools.MarqueeSelectionTool;
import org.eclipse.jface.resource.ImageDescriptor;

import dk.itu.bigm.application.plugin.BigMPlugin;

import org.eclipse.gef.tools.PanningSelectionTool;
import org.eclipse.gef.tools.MarqueeSelectionTool;
import org.eclipse.gef.palette.SelectionToolEntry;
import org.eclipse.gef.palette.MarqueeToolEntry;

public class MarqueeAndSelectionToolEntry 
	extends ToolEntry 
{
//	public MarqueeAndSelectionToolEntry()
//	{
//		ImageDescriptor img = BigMPlugin.getImageDescriptor("resources/icons/bigraph-palette/site.png");
//		this(null, null, img, img);
//	}
	
	public MarqueeAndSelectionToolEntry(String label, String description, ImageDescriptor iconSmall,
			ImageDescriptor iconLarge) {
		super(label, description, iconSmall, iconLarge, PanningSelectionTool.class);
		if ((label == null) || (label.length() == 0))
			setLabel("Marquee and Selection");
		setUserModificationPermission(1);
	}
	
	
//	public MarqueeAndSelectionToolEntry()
//	{
//		this(null, null);
//	}
}
