package ss.pku.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.poi.hssf.dev.HSSF;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellRangeAddress;
import org.apache.poi.ss.usermodel.Cell;

import com.sun.glass.ui.Application;

import scala.annotation.target.getter;

public class CreateExcel {
	
	private final static Integer rulePartLen = 1;
	private final static Integer ruleTitleLen = 1;
//	private final static String 
	
	@SuppressWarnings("deprecation")
	public static void createExcel(String filePath, List<List<String>> l, List<List<String>> attrs) {
		File file = new File(filePath);
		if (!file.exists()) {
			try {
				HSSFWorkbook wb =new HSSFWorkbook();
				FileOutputStream fileOut;
				fileOut = new FileOutputStream(filePath);
				wb.write(fileOut);
				fileOut.close();            	
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
        try {
            HSSFWorkbook workbook = new HSSFWorkbook(new FileInputStream(
            		filePath));
            HSSFSheet sheet = workbook.createSheet();
//            HSSFSheet sheet = workbook.createSheet("Sheet1");
            //新建一行
            for (Integer i = 0;i < attrs.size();i++) {
            	HSSFRow row = sheet.createRow(i);
            	row.createCell(0).setCellValue(attrs.get(i).get(0));
            	sheet.addMergedRegion(new CellRangeAddress(i,i,1,4));
            	row.createCell(1).setCellValue(attrs.get(i).get(1));
            }
                        
        	HSSFRow rowInOutTitle = sheet.createRow(attrs.size());
        	rowInOutTitle.createCell(0).setCellValue("序号");
        	rowInOutTitle.createCell(1).setCellValue("输入");
        	sheet.addMergedRegion(new CellRangeAddress(attrs.size(),attrs.size(),2,5));
        	rowInOutTitle.createCell(2).setCellValue("输出");
        	
            for (int i = attrs.size() + 1; i <= l.size() + attrs.size(); i++) {
                HSSFRow row = sheet.createRow(i);
                if (null == row) {
                    continue;
                } else {
                	List<String> listRowData = l.get(i - attrs.size() - 1);
                	if (null == listRowData) {
                		continue; 	
                	} else {
                		//输入
                		row.createCell(0).setCellValue((i - attrs.size()));
                		HSSFCell cellInput = row.getCell(1);
                		if (null == cellInput) {
                			row.createCell(1).setCellValue(listRowData.get(0));
                		} else {
                			cellInput.setCellValue(listRowData.get(0));  			
                		}
                		
                		//输出
                		HSSFCell cellOutput = row.getCell(3);
                		if (null == cellOutput) {
                			row.createCell(3).setCellValue(listRowData.get(1));
                		} else {
                			cellOutput.setCellValue(listRowData.get(1));                		                			
                		}
                	}
                }
            }
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(filePath);
                workbook.write(out);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }		
	}
	
	public static void modify(String fileToBeRead) {

        int coloum = 1; // 比如你要获取第1列
        try {
            HSSFWorkbook workbook = new HSSFWorkbook(new FileInputStream(
                    fileToBeRead));
            HSSFSheet sheet = workbook.getSheet("Sheet1");
 
            for (int i = 5; i <= sheet.getLastRowNum(); i++) {
                HSSFRow row = sheet.getRow(i);
                if (null == row) {
                    continue;
                } else {
                    HSSFCell cell = row.getCell(coloum);
                    if (null == cell) {
                        continue;
                    } else {
                        System.out.println(cell.getStringCellValue());
                        int temp = (int) cell.getNumericCellValue();
                        cell.setCellValue(temp + 1);
                    }
                }
            }
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(fileToBeRead);
                workbook.write(out);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }		
	}
	
	public static String getMiSeconds() {
		SimpleDateFormat formatter  = new SimpleDateFormat ("yyyy_MM_dd_HH_mm_ss"); 
		Date now = new Date();
	    return formatter.format(now); 
	}

	public static HSSFSheet initSheet(HSSFWorkbook workbook, Map<String, String> mapInfos, String sheetName) {
			Integer baseInfoCount = mapInfos.size();
	        //找到或者创建一个名为 ruleInOuts 的 sheet
	        HSSFSheet sheet = workbook.getSheet(sheetName);
	        if (null == sheet) {
	        	sheet = workbook.createSheet(sheetName);
	        } 
	        
	        // i 用来代表当前行数
	        Integer i = 0;
	        //遍历备注信息
	        HSSFRow row1 = sheet.createRow(0);
	        HSSFRow row2 = sheet.createRow(1);
	        // j 用来代表个数
	        Integer j = 0;
	        for (Entry<String, String> entry: mapInfos.entrySet()) {
	            String strTitle = entry.getKey();
	            String strInfo = entry.getValue();
//	            HSSFRow row = sheet.createRow(i);
//	            sheet.addMergedRegion(new CellRangeAddress(i, i, 0, 2));
	            row1.createCell(j).setCellValue(strTitle);
//	            sheet.addMergedRegion(new CellRangeAddress(i, i, 3, 8));
	            row2.createCell(j).setCellValue(strInfo);
	            j++;
	        }
	        i = 0;
	        // 创建初始行
//	        HSSFRow rowInOutTitle = sheet.createRow(i);
	        HSSFRow rowInOutTitle = sheet.getRow(i);
	    	rowInOutTitle.createCell(baseInfoCount + 0).setCellValue("序号");
//	    	sheet.addMergedRegion(new CellRangeAddress(i, i, 1, ruleTitleLen));
	    	rowInOutTitle.createCell(baseInfoCount + 1).setCellValue("规则名");
//	    	sheet.addMergedRegion(new CellRangeAddress(i, i,ruleTitleLen + 1, ruleTitleLen + rulePartLen));
	    	rowInOutTitle.createCell(baseInfoCount + ruleTitleLen + 1).setCellValue("输入");
//	    	sheet.addMergedRegion(new CellRangeAddress(i, i,ruleTitleLen + rulePartLen + 1, ruleTitleLen + 2*rulePartLen));
	    	rowInOutTitle.createCell(baseInfoCount + ruleTitleLen + rulePartLen + 1).setCellValue("输出");
	    	i++;	          
	    	return sheet;
	}
	
	public static boolean initBaseInfo(String filePath, Map<String, String> mapInfos, String sheetName) {
		File file = new File(filePath);
		if (!file.exists()) {
			try {
				HSSFWorkbook wb =new HSSFWorkbook();
				FileOutputStream fileOut;
				fileOut = new FileOutputStream(filePath);
				wb.write(fileOut);
				fileOut.close();
				
				HSSFWorkbook workbook = new HSSFWorkbook(new FileInputStream(
						filePath));

				initSheet(workbook, mapInfos, sheetName);
				
	            FileOutputStream out = null;
	            try {
	                out = new FileOutputStream(filePath);
	                workbook.write(out);
	                out.close();
	                return true;
	            } catch (IOException e) {
	                e.printStackTrace();
	                return false;
	            }
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		} else {
			try {
				HSSFWorkbook workbook = new HSSFWorkbook(new FileInputStream(
						filePath));
				if (null == workbook.getSheet(sheetName)) {
					initSheet(workbook, mapInfos, sheetName);
		            FileOutputStream out = null;
		            try {
		                out = new FileOutputStream(filePath);
		                workbook.write(out);
		                out.close();
		                return true;
		            } catch (IOException e) {
		                e.printStackTrace();
		                return false;
		            }				
				}			
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return false;
		}
	}

	public static Map<String, PairRule> getRules(String filePath, String sheetName) {
		File file = new File(filePath);
		if (!file.exists()) {
			System.out.println("文件不存在");
			return null;
		} else {
			try {
				HSSFWorkbook workbook = new HSSFWorkbook(new FileInputStream(filePath));
	            //找到一个名为 sheetName 的 sheet
	            HSSFSheet sheet = workbook.getSheet(sheetName);
	        	//i 用来代表当前行数
            	Integer i = 0;
	            //遍历备注信息
	            HSSFRow firstEmptyRow = sheet.getRow(i);
	            while (null != firstEmptyRow && !firstEmptyRow.getCell(0).getStringCellValue().equals("序号")) {
	            	i++;
	            	firstEmptyRow = sheet.getRow(i);
	            }
	            i++;
	            //此时 i 所指向的行就是 rule 开始的行
	            HSSFRow row = sheet.getRow(i);
	            Map<String, PairRule> result = new HashMap<String, PairRule>();
	            while (null != row) {
	            	HSSFCell nameCell = row.getCell(1);
	            	if (null != nameCell) {
	            		String ruleName = row.getCell(1).getStringCellValue();
	            		HSSFCell inputCell = row.getCell(ruleTitleLen + 1);
	            		HSSFCell outputCell = row.getCell(ruleTitleLen + rulePartLen + 1);
	            		String inputCase = (null == inputCell)?"空":row.getCell(ruleTitleLen + 1).getStringCellValue();
	            		String outputCase = (null == outputCell)?"空":row.getCell(ruleTitleLen + rulePartLen + 1).getStringCellValue();
	            		PairRule pair = new PairRule(inputCase, outputCase);
	            		result.put(ruleName, pair);
	            	} 
	            	i++;	            	
	                row = sheet.getRow(i);
	            }
	            printMapRule(result);
	            return result;
			} catch (FileNotFoundException e) {
				System.out.println("workbook 获取失败");
				e.printStackTrace();
				return null;
			} catch (IOException e) {
				System.out.println("文件 IO 异常");
				e.printStackTrace();
				return null;
			}
		}
	}
	
	public static void printMapRule(Map<String, PairRule> map) {
		for (Entry<String, PairRule> entry: map.entrySet()) {
		    String key = entry.getKey();
		    String inputCase = entry.getValue().getInputCase();
		    String outputCase = entry.getValue().getOutputCase();
		    System.out.println(key + " " + inputCase + " "  + outputCase);
		}
	}
	
	public static boolean saveRuleInOut(String filePath, Map<String, String> mapInfos, List<RuleInOut> liRuleInOuts, String sheetName) throws IOException {
		Integer baseInfoCount = mapInfos.size();
		File file = new File(filePath);
		if (!file.exists()) {
			boolean initResult = initBaseInfo(filePath, mapInfos, sheetName);		
			if (!initResult) {
				System.out.println("初始化文件失败");
			} else {
				System.out.println("初始化文件成功");
			}
		}
        try {
            HSSFWorkbook workbook = new HSSFWorkbook(new FileInputStream(
            		filePath));
            //找到一个名为 ruleInOuts 的 sheet
            HSSFSheet sheet = workbook.getSheet(sheetName);
        	//i 用来代表当前行数
            Integer i = 0;
            //遍历备注信息
            HSSFRow firstEmptyRow = sheet.getRow(i);
            while (null != firstEmptyRow) {
            	i++;
            	firstEmptyRow = sheet.getRow(i);
            }
            for (RuleInOut rule: liRuleInOuts) {
                HSSFRow row = sheet.createRow(i);
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
                out = new FileOutputStream(filePath);
                workbook.write(out);
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }		
		return false;
	}
	
	public static void main(String[] args) {
		Map<String, PairRule> result = getRules("D:\\BigMRulesData\\ruleInOutS.xls", "ruleInOuts");
	}
	
	public static void addAgent(String filePath, AgentInitialState ag, String sheetNameTwo) {
        try {
            HSSFWorkbook workbook = new HSSFWorkbook(new FileInputStream(
            		filePath));
            //找到一个名为 ruleInOuts 的 sheet
            HSSFSheet sheet = workbook.getSheet(sheetNameTwo);
        	//i 用来代表当前行数
            Integer i = 0;
            //遍历备注信息
            HSSFRow firstEmptyRow = sheet.getRow(i);
            while (null != firstEmptyRow) {
            	i++;
            	firstEmptyRow = sheet.getRow(i);
            }
            
            // 此时 i 正是需要加的那一行
            HSSFRow row = sheet.createRow(i);

            //输入
			row.createCell(0).setCellValue(i);
			row.createCell(1).setCellValue(ag.getAgentName());
			row.createCell(2).setCellValue(ag.getInitialState());

    		FileOutputStream out = null;
            try {
                out = new FileOutputStream(filePath);
                workbook.write(out);
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }				
	}	
}
