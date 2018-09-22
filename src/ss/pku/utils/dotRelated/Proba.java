package ss.pku.utils.dotRelated;

import java.io.File;

public class Proba
{
	public static void main(String[] args)
	{
		Proba p = new Proba();
//		p.start();
		p.start2();
	}

	/**
	 * Construct a DOT graph in memory, convert it
	 * to image and store the image in the file system.
	 */
	private void start()
	{
		GraphViz gv = new GraphViz();
		gv.addln(gv.start_graph());
		gv.addln("A -> B;");
		gv.addln("A -> C;");
		gv.addln(gv.end_graph());
		System.out.println(gv.getDotSource());

		gv.increaseDpi();   // 106 dpi

//		String type = "gif";
		//      String type = "dot";
		//      String type = "fig";    // open with xfig
		//      String type = "pdf";
		//      String type = "ps";
		//      String type = "svg";    // open with inkscape
		      String type = "png";
		//      String type = "plain";

		String representationType= "dot";
		//		String repesentationType= "neato";
		//		String repesentationType= "fdp";
		//		String repesentationType= "sfdp";
		// 		String repesentationType= "twopi";
		// 		String repesentationType= "circo";

//		File out = new File("/tmp/out"+gv.getImageDpi()+"."+ type);   // Linux
		File out = new File("d:/bigraphtools/out." + type);    // Windows
		gv.writeGraphToFile( gv.getGraph(gv.getDotSource(), type, representationType), out );
	}

	/**
	 * Read the DOT source from a file,
	 * convert to image and store the image in the file system.
	 */
	private void start2()
	{
//		String dir = "/home/jabba/Dropbox/git.projects/laszlo.own/graphviz-java-api";     // Linux
//		String input = dir + "/sample/simple.dot";
//		D:\Documents\BigSim-v0.1\Examples\Airport\results\BusinessDisable.dot
		String dir = "D:/Documents/BigSim-v0.1/Examples/Airport/results";
 	    String input = dir + "/BusinessDisable.dot";    // Windows

		GraphViz gv = new GraphViz();
		gv.readSource(input);
		System.out.println(gv.getDotSource());

		//	  String type = "gif";
		//    String type = "dot";
		//    String type = "fig";    // open with xfig
		//    String type = "pdf";
		//    String type = "ps";
		//    String type = "svg";    // open with inkscape
		    String type = "png";
		//      String type = "plain";


		String repesentationType= "dot";
		//		String repesentationType= "neato";
		//		String repesentationType= "fdp";
		//		String repesentationType= "sfdp";
		// 		String repesentationType= "twopi";
		//		String repesentationType= "circo";

//		File out = new File("/tmp/simple." + type);   // Linux
		File out = new File("d:/bigraphtools/out." + type);   // Windows
		gv.writeGraphToFile( gv.getGraph(gv.getDotSource(), type, repesentationType), out );
	}
}
