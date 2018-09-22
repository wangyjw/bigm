package ss.pku.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import dk.itu.bigm.editors.bigraph.BigraphEditor;
import dk.itu.bigm.editors.rule.RuleEditor;

public class ExportSaver {

	private final static String fileFolder = "D:\\BigData\\";
	private final static String fileName = "result";
	private final static String agentSheetName= "agentSheet";
	private final static String ruleSheetName= "ruleSheet";
	
	public static void saveInitialState() {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(); 
		if (null != page) {
			BigraphEditor bigraphEditor = (BigraphEditor) page.getActiveEditor();
			String agentName = page.getActiveEditor().getTitle();
			String initialState = bigraphEditor.getModel().getInitialState();
			appendInitialState(new AgentInitialState(agentName, initialState));
		}		
	}
	
	public static void saveRules() {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		if (null != page) {
			RuleEditor ruleEditor = (RuleEditor) page.getActiveEditor();
		}
	}
	
	public static void appendInitialState(AgentInitialState initialState) {
		String filePath = fileFolder + fileName + ".xls";
		File file = new File(filePath);
		if (!file.exists()) {
			createFile(filePath);
		}
	}
	
	public static void appendRule(RuleInOut rule) {
		
	}
	
	public static void createFile(String filePath) {
		try {
			HSSFWorkbook wb =new HSSFWorkbook();
			FileOutputStream fileOut = new FileOutputStream(filePath);
			wb.write(fileOut);
			fileOut.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void createSheet(String filePath, String sheetName) {
		HSSFWorkbook workbook;
		try {
			workbook = new HSSFWorkbook(new FileInputStream(
					filePath));
			HSSFSheet sheet = workbook.getSheet(sheetName);
			if (null == sheet) {
				sheet = workbook.createSheet(sheetName);
			} 		
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
