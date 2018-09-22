package ss.pku.logic;

import java.util.ArrayList;
import java.util.List;

import org.la4j.Matrix;
import org.la4j.Vector;

public class MatrixSolution {
	
    class ColumnPair {
        private int first;

        private int second;
        
		public ColumnPair(int firstColumn, int secondColumn) {
			this.first = firstColumn;
			this.second = secondColumn;
		}
		
		public ColumnPair reset() {
			this.first = -1;
			this.second = -1;
			return this;
		}
		
		@Override
		public String toString() {
			return "ColumnPair [first=" + first + ", second=" + second + "]";
		}

		public ColumnPair setColumnPair(int i, int j) {
			this.first = i;
			this.second = j;
			return this;
		}
		
		public int getFirst() {
			return first;
		}

		public void setFirst(int first) {
			this.first = first;
		}

		public int getSecond() {
			return second;
		}

		public void setSecond(int second) {
			this.second = second;
		}
    }
	
	/**
	 * @author Kevin Chan 
	 * @deprecated
	 */
	@SuppressWarnings("unused")
	private Vector nextVector(Vector vector, int column, int maxPoss,
			int index, int value) {
		if (index <= column && value < maxPoss) {
			int newValue = (int) vector.get(index) + 1;
			vector.set(index, newValue);
		} else if (index > column) {
			System.out.println("MatrixSolution@nextVector error1");
		} else if (value >= maxPoss) {
			System.out.println("MatrixSolution@nextVector error2");			
		}
		return vector;
	}
	
	/**
	 * 检查单次消解是否符合消解规则 
	 * @author Kevin Chan 
	 */
	private static boolean isSingleDigestionValid(Vector factOne, Vector factTwo) {
		int count = 0;
		for (int i = 0;i < factOne.length();i++) {
			if ((factOne.get(i) == 1.0 && factTwo.get(i) == -1.0)
					|| (factOne.get(i) == -1.0 && factTwo.get(i) == 1.0)) {
				count++;
				if (count >= 2) {
					return false;
				}
			}
		}
		return count == 1 ? true : false;
	}
	
	private static boolean isFinished(Vector usedTimeVector) {
		for (int i = 0;i < usedTimeVector.length();i++) {
			if (usedTimeVector.get(i) > 0.0) {
				return false;
			}
		}
		return true;
	}
	
	private static Vector usedTimesDelPair(Vector usedTimesVector, ColumnPair colPair) {
		int firstUsed = (int) usedTimesVector.get(colPair.getFirst()) - 1;
		int secondUsed = (int) usedTimesVector.get(colPair.getSecond()) - 1;
		usedTimesVector.set(colPair.getFirst(), firstUsed);
		usedTimesVector.set(colPair.getSecond(), secondUsed);
		if (firstUsed < 0 || secondUsed < 0) {
			System.out.println("something wrong MatrixSolution@vectorDelPair");
		} else {
			return usedTimesVector;
		}
		return usedTimesVector;
	}
	
	/**
	 * 返回新增的一列（将两列“求和”）
	 * 具体文字描述请参考袁崇义老师 Petri 网原理与应用，规则 8-4 消解规则
	 * @author Kevin Chan 
	 */
	private static Vector getDigestedVector(Matrix matrix, ColumnPair colPair) {
		Vector newVector = Vector.zero(matrix.rows());
		Vector firstV = matrix.getColumn(colPair.getFirst());
		Vector secondV = matrix.getColumn(colPair.getSecond());
		for (int i = 0;i < newVector.length();i++) {
			if (firstV.get(i) == secondV.get(i)
				|| secondV.get(i) == 0.0) {
				newVector.set(i, firstV.get(i));
			} else if (firstV.get(i) == 0.0) {
				newVector.set(i, secondV.get(i));
			} else {
				newVector.set(i, 0);
			}
		}
		
		return newVector;
	}
	
	/**
	 * 获取最初进行消解的两列序号（从 0 开始）
	 * @author Kevin Chan 
	 */
	private ColumnPair getInitialPair(Matrix matrix, Vector usedTimesVector) {
		ColumnPair colPair = new ColumnPair(-1, -1);
		for (int i = 0;i < matrix.columns();i++) {
			for (int j = i + 1;j < matrix.columns();j++) {
				//在解中 i 和 j 对应的位置的值应该大于等于 1（否则不能进行消解运算），同时检测 isSingleDigestionValid
				if (usedTimesVector.get(i) >= 1 && usedTimesVector.get(j) >= 1
						&& isSingleDigestionValid(matrix.getColumn(i), matrix.getColumn(j))
						) {
					colPair.setColumnPair(i, j);
					return colPair;					
				}				
			}
		}
		
		return colPair;
	}
	
	/**
	 * 取得单个列所代表的 vector
	 * @author Kevin Chan
	 */
	private Vector countBySinlgeNewColumnIndex(int columnIndex, 
			List<ColumnPair> appendedColPairList, Matrix matrix, int originalLen) {
		ColumnPair colPair = appendedColPairList.get(columnIndex - originalLen);
		Vector result1 = Vector.zero(originalLen);
		Vector result2 = Vector.zero(originalLen);
		
		if (colPair.getFirst() > originalLen - 1) {
			result1 = countBySinlgeNewColumnIndex(colPair.getFirst(), appendedColPairList, matrix, originalLen);
		} else {
			result1.set(colPair.getFirst(), 1);
		}
		
		if (colPair.getSecond() > originalLen - 1) {
			result2 = countBySinlgeNewColumnIndex(colPair.getSecond(), appendedColPairList, matrix, originalLen);
		} else {
			result2.set(colPair.getSecond(), 1);			
		}
		
		return result1.add(result2);
	}
	
	/***
	 * 递归处理解方程时出现的列号
	 * @author Kevin Chan
	 */
	@SuppressWarnings("unused")
	private Vector countByColPair(ColumnPair colPair, List<ColumnPair> appendedColPairList, 
			int originalLen, Matrix matrix) {
		Vector result1 = countBySinlgeNewColumnIndex(colPair.getFirst(), appendedColPairList, matrix, originalLen);
		Vector result2 = countBySinlgeNewColumnIndex(colPair.getSecond(), appendedColPairList, matrix, originalLen);
		
		return result1.add(result2);
	}
	
	/**
	 * 通过检测最后一个增加的 Column 倒推回去验证是否已经结束 
	 * @author Kevin Chan
	 */
	public boolean isDigestionFinished(List<ColumnPair> appendedColPairList, Vector usedTimesVector, Matrix matrix) {
		int originalLen = usedTimesVector.length();
		int colPairListLen = appendedColPairList.size();
		Vector countVector = countBySinlgeNewColumnIndex(colPairListLen - 1 + originalLen, 
				appendedColPairList, matrix, originalLen);
		if (countVector.equals(usedTimesVector)) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 是否超额使用
	 * @author Kevin Chan 
	 */
	private boolean overUsed(Vector usedTimesVector, Vector countVector) {
		for (int i = 0;i < usedTimesVector.length();i++) {
			if (usedTimesVector.get(i) < countVector.get(i)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 获取下两个进行消解的列的序号（从 0 开始）
	 * 这里默认两个列序号之中必有一个 matrix 里最后一列的序号
	 * @author Kevin Chan 
	 * @param matrix, usedTimesVector.length(), usedTimesVector, appendedColPairList
	 */
	private ColumnPair getNextPair(Matrix matrix, int originalLen, Vector usedTimesVector, final List<ColumnPair> appendedColPairList) {
		ColumnPair colPair = new ColumnPair(-1, -1);		
		
		for (int i = 0;i < matrix.columns() - 1;i++) {
			// 循环判断单次消解是否符合规则，其中第二个固定为 matrix 中的最后一列
//			usedTimesVector.get(i) >= 1 &&
			if (isSingleDigestionValid(matrix.getColumn(i), 
					matrix.getColumn(matrix.columns() - 1))) {
				colPair.setColumnPair(i, matrix.columns() - 1);
				
				// 暂时加到 appendedColPairList 里，然后检验是否符合
				appendedColPairList.add(colPair);
				
				Vector countVector = countBySinlgeNewColumnIndex(appendedColPairList.size() - 1 + originalLen, 
						appendedColPairList, matrix, originalLen);
				appendedColPairList.remove(appendedColPairList.size() - 1); // 超额使用则在 appendColPairList 中删除该 ColPair
				if (overUsed(usedTimesVector, countVector)) {
					//超额使用
					colPair.reset();
				} else {
					return colPair;					
				}
				
			}
		}
		return colPair.reset();
	}
	
	/**
	 * 检查求解齐次线性方程组的过称中是否使用初等变换，并且
	 * 初等变换是否符合消解规则（新版本）
	 * @author Kevin Chan 
	 */
	private boolean isDigestionValid(Matrix matrix, Vector usedTimesVector) {
		Vector newUsedTimesVector = usedTimesVector.copy();
		
		ColumnPair colPair = getInitialPair(matrix, usedTimesVector); // 首先获得最初的 col
		if (colPair.getFirst() == -1 || colPair.getSecond() == -1) {
			System.out.println("something wrong MatrixSolution@isDigestionValid");
			return false;
		} else {
			newUsedTimesVector = usedTimesDelPair(newUsedTimesVector, colPair); // 获得删除 colPair 后的 usedTimesVector
			List<ColumnPair> appendedColPairList = new ArrayList<ColumnPair>();
			appendedColPairList.add(colPair);

			matrix = matrix.insertColumn(matrix.columns() - 1, getDigestedVector(matrix, colPair)); // 将新增的一列插入到最后
			matrix.swapColumns(matrix.columns() - 1, matrix.columns() - 2); // insert 无法直接插入到最后，还需要人工转换
			while (!isDigestionFinished(appendedColPairList, usedTimesVector, matrix)) {
				
				colPair = getNextPair(matrix, usedTimesVector.length(), usedTimesVector, appendedColPairList); // 获得下一组 col
//				newUsedTimesVector = usedTimesDelPair(newUsedTimesVector, colPair); // 获得删除 colPair 后的 usedTimesVector
				
				appendedColPairList.add(colPair);
				if (colPair.getFirst() == -1 || colPair.getSecond() == -1) {
					// 如果没找到合适的 colPair 则返回 false
					return false;
				} else {
					// 如果找到了合适的 colPair 则继续处理
					Vector vector2Insert = getDigestedVector(matrix, colPair);
					matrix = matrix.insertColumn(matrix.columns() - 1, vector2Insert);
					matrix.swapColumns(matrix.columns() - 1, matrix.columns() - 2); // insert 无法直接插入到最后，还需要人工转换
				}
			}
		}
		return true;
	}
	
	/**
	 * 检查求解齐次线性方程组的过称中是否使用初等变换，并且
	 * 初等变换是否符合消解规则（旧版本）
	 * @author Kevin Chan 
	 * @deprecated
	 */
	@SuppressWarnings("unused")
	private boolean isValid(Matrix matrix, Vector usedTimesVector) {
		int maxUesdColumn = -1;
		int maxUesdTime = 0;
		int secondMaxUsedColumn = -1;
		int secondMaxUsedTime = 0;
		for (int i = 0;i < usedTimesVector.length();i++) {
			if (usedTimesVector.get(i) > maxUesdTime) {
				secondMaxUsedTime = maxUesdTime;
				maxUesdTime = (int) usedTimesVector.get(i);
				secondMaxUsedColumn = maxUesdColumn;
				maxUesdTime = (int) usedTimesVector.get(i);
			} else {
				
			}
		}

		ColumnPair colPair = new ColumnPair(maxUesdColumn, secondMaxUsedColumn);
		List<ColumnPair> colPairList = new ArrayList<ColumnPair>(); 
		while (!isFinished(usedTimesVector)) {
			if (isSingleDigestionValid(matrix.getColumn(
					colPair.getFirst()), matrix.getColumn(colPair.getSecond()))) {
				usedTimesVector = usedTimesDelPair(usedTimesVector, colPair);  
				matrix.insertColumn(matrix.columns() - 1, getDigestedVector(matrix, colPair));
//				colPair = getNextPair(matrix, usedTimesVector.length(), usedTimesVector, appendedColPairList);
			} else {
				return false;
			}			
		}
		return true;
	}
	
	/**
	 * 检测是否是阶梯形矩阵 
	 * @author Kevin Chan
	 */
	public boolean isStepMatrix(Matrix matrix) {
		for (int i = 0;i < matrix.rows();i++) {			
			for (int j = 0;j < matrix.rows();j++) {
				if ((i != j) && !(matrix.get(j, i) == 0.0)) {
					return false;
				} else if ((i == j) && !(matrix.get(i, i) == 1.0)) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * 初等行变换中，行首变为 1
	 * @author Kevin Chan 
	 */
	public Matrix set1stOfLine2One(final Matrix matrix, final int rowIndex) {
		Matrix newMatrix = matrix.copy();
		
//		int row2Swap = rowIndex; // 需要换到第 rowIndex 行的那一行号
		for (int i = rowIndex;i < matrix.rows();i++) {
			double val = newMatrix.get(i, rowIndex);
			if (val != 0.0) {
				newMatrix.swapRows(rowIndex, i);

				Vector row = newMatrix.getRow(rowIndex);
				row = row.multiply(1 / (row.get(rowIndex)));
				newMatrix.setRow(rowIndex, row);
				break;
			}
		}
		
		//将 row2Swap 下面的行的第 rowIndex 列变为 0
		for (int i = rowIndex + 1;i < matrix.rows();i++) {
			Vector row = newMatrix.getRow(rowIndex);
			Vector iRow = newMatrix.getRow(i);
			if (iRow.get(rowIndex) != 0.0) {				
				Vector iRow2Add = row.multiply((-1)/iRow.get(rowIndex));
				newMatrix.setRow(i, iRow.add(iRow2Add));
			}
		}
		
		return newMatrix;
	}
	
	/**
	 * @author Kevin Chan 
	 * 
	 */
	public Matrix setTopRightCorner2Zero(final Matrix matrix, final int colIndex) {
		Matrix newMatrix = matrix.copy();
		
		for (int i = 0;i < colIndex;i++) {
			double val = matrix.get(i, colIndex);
			if (val != 0) {
				Vector row = matrix.getRow(colIndex);
				Vector iRow = matrix.getRow(i);
				if (row.get(colIndex) == 1.0) {
					Vector iRow2Add = row.multiply((0-val));
					newMatrix.setRow(i, iRow.add(iRow2Add));
				} else {
					System.out.println("wrong MatrixSolution@setTopRightCorner2Zero");
				}
			}
		}
		
		return newMatrix;
	}
	
	/**
	 * 检查单个矩阵是否由非负整数构成 
	 */
	private boolean isNature(Vector vector) {
		boolean anyNotZero = false;
		
		for (int i = 0;i < vector.length();i++) {
			double value = vector.get(i);
			if ((value != (int)value) || value < 0) {
				return false; // 非整数则返回 false
			} else if (value > 0) {
				anyNotZero = true;
			}
		}
		
		return anyNotZero;
	}
	
	/**
	 * 根据阶梯型矩阵求解方程组 
	 * @author Kevin Chan
	 */
	private Vector getFreeVector(final Matrix stepMatrix) {
 		Matrix freeMatrix = Matrix.zero(stepMatrix.rows(), stepMatrix.columns() - stepMatrix.rows());
		for (int i = stepMatrix.rows();i < stepMatrix.columns();i++) {
			freeMatrix.setColumn(i - stepMatrix.rows(), stepMatrix.getColumn(i).multiply(-1));
		}
		
		Vector totalVector = Vector.zero(stepMatrix.columns());
		Vector freeVector = Vector.zero(stepMatrix.columns() - stepMatrix.rows());
		
		return freeVector;
	}
	
	/**
	 * 利用转化为阶梯型的方法求解 
	 * 
	 * @author Kevin Chan
	 */
	public Vector getResultByStepMatrix(Matrix matrix) {
		Vector result = Vector.zero(matrix.columns());
		
        MatrixSolution solver = new MatrixSolution();
        Matrix stepMatrix = solver.getStepMatrix(matrix);
		
		// 如果是阶梯型了
		List<Vector> solution = new ArrayList<Vector>();
		if (isStepMatrix(stepMatrix)) {
			for (int i = matrix.rows();i < matrix.columns();i++) {
				Vector vec = (stepMatrix.getColumn(i)).multiply(-1);
				solution.add(vec);
			}
		} else {
			//!TODO
			System.out.println(stepMatrix);
		}
		result = getFreeVector(stepMatrix);
        System.out.println(solution.toString());
		
        return result;
	}
	
	/**
	 * 求齐次线性方程组系数矩阵的阶梯型
	 * @author Kevin Chan 
	 */
	public Matrix getStepMatrix(Matrix matrix) {
		Matrix stepMatrix = matrix.copy();
		// 左下角全为 0
		for (int i = 0;i < matrix.rows();i++) {
			stepMatrix = set1stOfLine2One(stepMatrix, i);
		}
		
		// 右上角改为 0
		for (int i = 1;i < matrix.rows();i++) {
			stepMatrix = setTopRightCorner2Zero(stepMatrix, i);
		}
		
//		while (!isStepMatrix(stepMatrix)) {
//			
//		}
		
		return stepMatrix;
	}
	
	
	/**
	 * @author Kevin Chan
	 * 使用穷举法，求解，需要注意的是矩阵和解都是整数 
	 */
	private boolean traverse(Matrix matrix) {
		int row = matrix.rows();
		int column = matrix.columns();
		Vector zeroVector = Vector.zero(row);
		Vector result = Vector.zero(column);
		int maxPoss = 9;
		// 初始状态
		result.set(0, 1);
		
		// 当前列数
		int numIndex = 0;
		// 待增加的值，即个位数
		int value = 1;
		
		// 遍历求解
		while (numIndex < column) {
			if (zeroVector.equals(matrix.multiply(result))) {
				System.out.println("一个解是：" + result);
				if (this.isDigestionValid(matrix, result)) {
					return true; // 如果满足消解规则，则返回 true
				} else {
					return false; //!TODO 如果不满足消解规则，那么进一步处理
				}
			} else {
				if (result.get(0) < maxPoss) {
					value++;
					result.set(0, value);
					if (value == maxPoss) {
						value = 0;
						result.set(0, 0);
						boolean carry = true; // 是否进位
						int i = 1;
//						numIndex++;
						while(carry && i < result.length()) {
							int temp = (int) result.get(i);
							if ((temp + 1) == maxPoss) {
								carry = true;
								result.set(i, 0);
							} else if ((temp + 1) < maxPoss) {
								carry = false;
								result.set(i, temp + 1);
							} else {
								System.out.println("MatrixSolution@traverse error2");
								carry = false;
							}
							numIndex = i;
							i++;
						}
					}
				} else if (value == maxPoss) {
					System.out.println("MatrixSolution@traverse error1");
				}
			}
		}
		return false;
	}
	
	
	public boolean getSolution(Matrix matrix) {
		int rank = matrix.rank();
		int column = matrix.columns();
		if (rank < column) {
			while (true) {
				if (this.traverse(matrix)) {
					return true;
				} else {
					//!TODO 使用扩展规则，即合理更改 matrix 的值。需要注意的是，matrix 任意一列的 0 都可以更改为 1 或 -1，且得到的新的一列仍然是事实
					return false;
				}				
			}
		} else if (rank == column) {
			return false; // 只有零解
		} else {
			System.out.println("wrong MatrixSolution@getSolution"); // rank > column

		}		
		return false;
	}
}
