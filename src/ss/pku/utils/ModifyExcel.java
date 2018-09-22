package ss.pku.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class ModifyExcel {

	public static void main(String[] args) {
//		List<List<String>> l = new ArrayList<List<String>>();
		List<List<String>> l = FileFormatter.readTextFile("D:\\Documents\\test.txt");
		
		modifyNew("D:\\Documents\\template.xls", l);
	}

	public static void modifyNew(String filePath, List<List<String>> l) {
        try {
            HSSFWorkbook workbook = new HSSFWorkbook(new FileInputStream(
            		filePath));
            HSSFSheet sheet = workbook.getSheet("Sheet1");

//            for (int i = 5; i <= sheet.getLastRowNum(); i++) {
            for (int i = 5; i <= l.size() + 4; i++) {
                HSSFRow row = sheet.getRow(i);
                if (null == row) {
                    continue;
                } else {
                	List<String> listRowData = l.get(i - 5);
                	if (null == listRowData) {
                		continue; 	
                	} else {
                		//输入
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
//
//                        System.out.println(cell.getNumericCellValue());
//                        int temp = (int) cell.getNumericCellValue();
//                        cell.setCellValue(temp + 1);
//
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
}
