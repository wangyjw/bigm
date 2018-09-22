package ss.pku.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.ScriptException;

import org.la4j.Matrix;
import org.la4j.Vector;
import org.la4j.test.V;

public class TestDemo {

	private static void testSpecialOp() {
		
//	    String str = "service@xsoftlab.net";
//	    // 邮箱验证规则
//	    String regEx = "[a-zA-Z_]{1,}[0-9]{0,}@(([a-zA-z0-9]-*){1,}\\.){1,3}[a-zA-z\\-]{1,}";
//	    // 编译正则表达式
//	    Pattern pattern = Pattern.compile(regEx);
//	    // 忽略大小写的写法
//	    // Pattern pat = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);
//	    Matcher matcher = pattern.matcher(str);
//	    // 字符串是否与正则表达式相匹配
//	    boolean rs = matcher.matches();
//	    System.out.println(rs);
		
		
		
		
		
		
		
		
		
		
		String replacedString = "(a)G(test1&&test2)||(test3Rtest4)R(test6)";		
		String str2 = "baike.xsoftlab.net";
		
//		Pattern pattern1 = Pattern.compile("\\(\\)");
//		Matcher m1 = pattern1.matcher(replacedString);
//		m1.group();
//		System.out.println(m1.toString());
				
		Pattern pattern2 = Pattern.compile("\\(.*?\\)");
		Matcher m2 = pattern2.matcher(replacedString);
		m2.group();
		System.out.println(m2.toString());
	}
	
	
	private static boolean foo3() {
		String demo1;
//		demo1 = "G(test1&&test2)||(test3Rtest4)R(test6)";
		demo1 = "G(test1&&test2)||(test3Rtest4)R(test6)";
		boolean result = false;
		
		FormulaValidation fomVa1 = new FormulaValidation(demo1);
		try {
			fomVa1.isValid();
			result = true;
		} catch (ScriptException e) {
			e.printStackTrace();
			result = false;
		}
		System.out.println("isValid: " + result);
		return result;
	}
	
	private static void foo1() {
        String str2 = "prop1&&prop2";
        //将字符串中的.替换成_，因为.是特殊字符，所以要用\.表达，又因为\是特殊字符，所以要用\\.来表达.
        str2 = str2.replaceAll("[a-z0-9]+", "true");
        
        System.out.println(str2);    
        
        str2 = str2.replaceAll("&&", "…………");
        
        System.out.println(str2);    
	}
	
	private static void foo2() {
    	double a[][] = new double[][] {
            { 1.0, 0.0, -1.0, 0.0, 0.0},
            { 0.0, 0.0, 1.0, -1.0, -1.0},
            { 0.0, -1.0, 0.0, 0.0, 1.0},
            { 0.0, -1.0, 0.0, 1.0, 0.0}
        };
		
//    	double a[][] = new double[][] {
//            { 1.0, -1.0, -1.0, 0.0, 1.0, -1.0},
//            { 0.0, 0.0, 1.0, -1.0, -1.0, 1.0},
//            { 0.0, 1.0, 0.0, 0.0, 1.0, -1.0},
//            { 1.0, -1.0, 0.0, 1.0, 0.0, 0.0}
//        };
		
//		double a[][] = new double[][] {
//			{1.0, 0.0, 0.0, 1.0},			
//			{1.0, 0.0, 0.0, -1.0},
//			{0.0, 1.0, 0.0, 0.0},
//			{0.0, 0.0, 1.0, 0.0}
//		};
		
		
        Matrix m = Matrix.from2DArray(a);
//        double v0[] = new double[] {2,2,1,0,0,2};
//        System.out.println("result: " + m.multiply(Vector.fromArray(v0)));

		MatrixSolution solver = new MatrixSolution();

        Vector result1 = solver.getResultByStepMatrix(m);        
        
//        boolean isStepMatrix = solver.isStepMatrix(m);
//        System.out.println(isStepMatrix);
        
//        boolean result2 = solver.getSolution(m);
        System.out.println(result1);

		
//		Vector v1 = Vector.constant(4, 1);
//		v1.set(3, 2.0); // 1 1 1 2
//
//		Vector v2 = Vector.constant(4, 6);
//		v2.set(2, 7.0); // 6 6 7 6
//		Vector v3 =  v1.add(v2);
//		System.out.println(v3);
//		
//		double[] arr = new double[] {1, 1, 1, 2};
//		Vector v4 = Vector.fromArray(arr);
//		if (v1 == v4) {
//			System.out.println("1");
//		}
//		if (v1.equals(v4)) {
//			System.out.println("2");	
//		}
		
//		Vector v1 = Vector.zero(20);
//		v1.set(1, 2);
//		System.out.println(v1.toCSV());
//		Vector v2 = v1.copy();
//		v2.set(3, 56);
//		System.out.println(v1.toCSV());
//		System.out.println(v2.toCSV());
	}
	
	private static void foo4() {
		String demo1 = "G(test1&&test2Utest3Rtest4)||(test3Rtest4Rtest2&&(test1&&test2))R(test6)";
		FormulaValidation forVa = new FormulaValidation(demo1);

		demo1 = "((((())R())))";
		
//		forVa.isSingleBracketValid(demo1, 7);
		forVa.specialCheck();
	}
	
	public static void main(String[] args) {
//		getPositionList();
		foo4();
//		testSpecialOp();
	}		
}
