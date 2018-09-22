package ss.pku.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class FileFormatter {

	public static void writeTextFile(List<String> inputs, String filePath) {
		File file = new File(filePath);
		if (!file.exists()) {
			System.out.println("文件不存在，新建文件");
			try {
				file.createNewFile();
			} catch (IOException e) {
				System.out.println("新建文件失败");
				e.printStackTrace();
			}
		}		
//		List<List<String>> liliStr = readTextFile(filePath);
		String strTemp = inputs.size() == 0?"输入为空":inputs.get(0);
		try {
			FileOutputStream fo = new FileOutputStream(file, true);
			
//			FileInputStream fw = new FileInputStream(file);
//			OutputStreamWriter osw = new OutputStreamWriter(fo,"UTF-8");			
			
			fo.write(strTemp.getBytes("UTF-8"));
			fo.write("\r\n".getBytes());
//			BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, true));
//			for (List<String> liStr: liliStr) {
//				for (String str: liStr) {
//					bw.write(str);
//					bw.newLine();
//				}
//			}
//			bw.write(strTemp);
//			bw.newLine();
//			bw.flush();
//			bw.close();
			fo.flush();
			fo.close();
//			fw.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public static List<List<String>> readTextFile (String inputFilePath) {
        try {
    		List<List<String>> l = new ArrayList<List<String>>();
    		List<String> textContent = new ArrayList<String>();
    		Boolean isInput = true;
    		
            String encoding="UTF-8";
//            String encoding="GBK";
            File file=new File(inputFilePath);
            if(file.isFile() && file.exists()){ //判断文件是否存在
                InputStreamReader read = new InputStreamReader(
                		new FileInputStream(file),encoding);//考虑到编码格式

                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                while((lineTxt = bufferedReader.readLine()) != null){
//                    System.out.println(lineTxt);
                    textContent.add(lineTxt);
                }
                // 默认输入和输出对应出现，各占一行
                for (Integer i = 0;i < textContent.size()-1;i+=2) {
                    List<String> listRowData = new ArrayList<String>();
                    listRowData.add(textContent.get(i));
                    listRowData.add(textContent.get(i+1));
                    l.add(listRowData);
                }
//                ModifyExcel.modifyNew("D:\\Documents\\template.xls", l);
                read.close();
                return l;
		    }else{
		        System.out.println("找不到指定的文件");
		    }
	    } catch (Exception e) {
	        System.out.println("读取文件内容出错");
	        e.printStackTrace();
	    }
		return null;
	}
	
}
