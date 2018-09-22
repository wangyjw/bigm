package dk.itu.bigm.editors.simulation_spec;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

public class TestProp {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Properties pps = new Properties();
		try {
			pps.load(new FileInputStream("dot.properties"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String tempDir = pps.getProperty("tempDir");
		System.out.println(tempDir);
//		Enumeration enum1 = pps.propertyNames();//得到配置文件的名字
//		while(enum1.hasMoreElements()) {
//		    String strKey = (String) enum1.nextElement();
//		    String strValue = pps.getProperty(strKey);
//		    System.out.println(strKey + "=" + strValue);
//		}
	}

}
