package ss.pku.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellRangeAddress;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class GenerateResult {
	private static final String rulesFilePath = "D:\\BigMRulesData\\demo.txt";
	private static final String rulesListFilePath = "D:\\BigMRulesData\\SmartJigWarehouseWireDisconnected.txt";
	private static final String ruleNamesFilePath = "D:\\BigMRulesData\\rules.xls";
	private static final String ruleNamesSheetName = "sheet1";
	private static final String xlsPath = "D:\\BigMRulesData\\result.xls";
	private static final String sheetNameDefault = "Sheet1";
	//!TODO 
	private final static Integer rulePartLen = 1;
	private final static Integer ruleTitleLen = 1;
	
	public static void main(String[] args) {
//		autoRun();
//		getRuleNamesAsLists("D:\\BigMRulesData\\demo1.txt");
		autoRunMass();
	}	

	public static void autoRun() {
		List<String> ruleNames = getRuleNamesAsList(rulesFilePath);
		new CreateExcel();
		Map<String, PairRule> rules = CreateExcel.getRules(ruleNamesFilePath, ruleNamesSheetName);
		List<RuleInOut> listRules = getRules(ruleNames, rules);
		generateXLS(listRules, sheetNameDefault);
	}
		
	public static void autoRunMass() {
		List<List<String>> ruleNamesListList = getRuleNamesAsLists(rulesListFilePath);
		new CreateExcel();
		Map<String, PairRule> rules = CreateExcel.getRules(ruleNamesFilePath, ruleNamesSheetName);
		Integer i = 1;
		for (List<String> ruleNames: ruleNamesListList) {
			List<RuleInOut> listRules = getRules(ruleNames, rules);
			generateXLS(listRules, "测试用例" + i.toString());
			i++;
		}
	}
	
	public static void generateXLS(List<RuleInOut> li, String sheetName) {
		try {
			new SaveInputAndOutput();
			Map<String, String> baseInfos = SaveInputAndOutput.getBaseInfo();
			CreateExcel.initBaseInfo(xlsPath, baseInfos, sheetName);
			Integer baseInfoCount = baseInfos.size();
			HSSFWorkbook workbook = new HSSFWorkbook(new FileInputStream(xlsPath));
			sheetName = (null == sheetName)?sheetNameDefault:sheetName;
			HSSFSheet sheet = null;
			if (null != workbook.getSheet(sheetName)) {
				sheet = workbook.getSheet(sheetName);				
			} else {
				sheet = workbook.createSheet(sheetName);
			}
			new CreateExcel();
        	//i 用来代表当前行数
            Integer i = 0;
            //遍历备注信息
            HSSFRow firstEmptyRow = sheet.getRow(i);
            while (null != firstEmptyRow) {
            	i++;
            	firstEmptyRow = sheet.getRow(i);
            }
            i = 1;
            for (RuleInOut rule: li) {
            	HSSFRow row = null;
            	if (null == sheet.getRow(i)) {
            		row = sheet.createRow(i);            		
            	} else {
            		row = sheet.getRow(i);
            	}
            	if (null == rule) {
            		continue; 	
            	} else {
            		//输入
            		row.createCell(baseInfoCount + 0).setCellValue(i);
//            		sheet.addMergedRegion(new CellRangeAddress(i, i, 1, ruleTitleLen));
            		row.createCell(baseInfoCount + 1).setCellValue(rule.getRuleName());
//            		sheet.addMergedRegion(new CellRangeAddress(i, i,ruleTitleLen + 1, ruleTitleLen + rulePartLen));
            		row.createCell(baseInfoCount + ruleTitleLen + 1).setCellValue(rule.getPairRule().getInputCase());
//            		sheet.addMergedRegion(new CellRangeAddress(i, i,ruleTitleLen + rulePartLen + 1, ruleTitleLen + 2*rulePartLen));
            		row.createCell(baseInfoCount + ruleTitleLen + rulePartLen + 1).setCellValue(rule.getPairRule().getOutputCase());            		
            	}
            	i++;
            }
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(xlsPath);
                workbook.write(out);
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }			
			
		} catch (FileNotFoundException e) {
			System.out.println("文件不存在");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("文件 IO 错误");
			e.printStackTrace();
		}
		
	}
	
	public static List<RuleInOut> getRules(List<String> ruleNames, Map<String, PairRule> rules) {
		List<RuleInOut> result = new ArrayList<RuleInOut>();
		for(String ruleName:ruleNames) {
			RuleInOut rule = new RuleInOut();
			rule.setRuleName(ruleName);
			PairRule pairRule = rules.get(ruleName);
			rule.setPairRule(pairRule);
			result.add(rule);
		}
		return result;
	}
	
	public static List<List<String>> getRuleNamesAsLists(String rulesFilePath) {
		File file = new File(rulesFilePath);
		List<List<String>> result = new ArrayList<List<String>>();
		String encoding = "UTF-8";
		if (file.isFile() && file.exists()) {
			try {
				InputStreamReader read = new InputStreamReader(
						new FileInputStream(file), encoding);
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				List<String> liRules = new ArrayList<String>();
				while ((lineTxt = bufferedReader.readLine()) != null) {
					if (lineTxt.endsWith("{")) {
						continue;
					} else if (lineTxt.startsWith("}")) {
						if (!liRules.isEmpty()) {
							List<String> liTemp = new ArrayList<String>();
							for (String ruleName: liRules) {
								liTemp.add(ruleName);
							}
							result.add(liTemp);
							liRules.clear();
						}
						continue;
					} else {
						liRules.add(lineTxt);
					}
				}
                read.close();
				return result;
			} catch (UnsupportedEncodingException e) {
				System.out.println("不支持的文件格式！");
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				System.out.println("文件不存在！");
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println("文件输入输出错误");
				e.printStackTrace();
			}
		}
		return null;
	}

	public static List<String> getRuleNamesAsList(String rulesFilePath) {
		File file = new File(rulesFilePath);
		List<String> result = new ArrayList<String>();
		String encoding = "UTF-8";
		
		if (file.isFile() && file.exists()) {
			try {
				InputStreamReader read = new InputStreamReader(
						new FileInputStream(file), encoding);
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				while ((lineTxt = bufferedReader.readLine()) != null) {
//					System.out.println(lineTxt);
					result.add(lineTxt);
				}
                read.close();
				return result;
			} catch (UnsupportedEncodingException e) {
				System.out.println("不支持的文件格式！");
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				System.out.println("文件不存在！");
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println("文件输入输出错误");
				e.printStackTrace();
			}
		}
		System.out.println("error");
		return null;
	}
}
