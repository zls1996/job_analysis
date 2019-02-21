package com.jobanalysis.job_analysis.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;


/**
 * Excel文件操作工具类
 * @version：1.0
 * @error:Map会导致数据丢失(被覆盖)
 * @author 朱立松
 *
 */
@Component
public class ExcelPOIHelper {

	/**
	 * 创建一个新的xls文件
	 * @param filePath
	 * @param sheetName
	 * @param dataMap
	 */
	public static void create(String filePath, String sheetName, Map<String, List<Map<String, String>>> dataMap) {
		
		//创建一个HSSFWorkbook，对应一个Excel文件
		Workbook wb = new XSSFWorkbook();
		FileOutputStream fileOutputStream;
		
		try {
			fileOutputStream = new FileOutputStream(filePath);
			//新建一个sheet对象
			XSSFSheet sheet  = (XSSFSheet) wb.createSheet(sheetName);
			//数据非空则插入
			if(dataMap != null) {
				insertData(sheet, dataMap);
				wb.write(fileOutputStream);
			}
			fileOutputStream.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	/**
	 * 往sheet里面添加数据
	 * @param sheet
	 * @param dataMap
	 */
	private static void insertData(XSSFSheet sheet, Map<String, List<Map<String, String>>> dataMap) {
		//用于判定当前遍历的行数
		int index = 0;
		for(Entry<String , List<Map<String ,String>>> dataEntry : dataMap.entrySet()) {
			
			//当index为0，添加表头
			if(index == 0) {
				//创建行
				XSSFRow row = sheet.createRow(index);
				List<Map<String , String >> mapList = dataEntry.getValue();
				String rowKey = dataEntry.getKey();
				XSSFCell keyCell = row.createCell(0);
				//添加rowKey
				keyCell.setCellValue("ROWKEY");
				
				//遍历其他表头
				for(int colNum = 1 ; colNum <= mapList.size(); colNum ++) {
					//创建rowKey到cell中
					XSSFCell cell = row.createCell(colNum);
					
					Map<String , String> cellMap = mapList.get(colNum-1);
					for(Entry<String , String > cellEntry: cellMap.entrySet()) {
						//添加表头
						System.out.print(cellEntry.getKey() + "\t");
						cell.setCellValue(cellEntry.getKey());
					}
					
				}
			}
			//创建行
			XSSFRow row = sheet.createRow(++index);
			String rowKey = dataEntry.getKey();
			List<Map<String , String >> mapList = dataEntry.getValue();
			//遍历行中的所有列
			XSSFCell keyCell = row.createCell(0);
			//添加rowKey
			keyCell.setCellValue(rowKey);
			for(int colNum = 1 ; colNum <= mapList.size(); colNum ++) {
				//创建rowKey到cell中
				XSSFCell cell = row.createCell(colNum);
				//添加rowKey
				cell.setCellValue(rowKey);
				Map<String , String> cellMap = mapList.get(colNum-1);
				for(Entry<String , String > cellEntry: cellMap.entrySet()) {
					//添加cell的值
					cell.setCellValue(cellEntry.getValue());
				}
				
			}
		}
		
		
	}



	/**
	 * 取出Excel的所有工作簿名
	 * @param xlsFilePath
	 * @return
	 */
	public static List<String> getSheets(String xlsFilePath){
		List<String> result = new ArrayList<String>();
		
		try {
			FileInputStream fis = new FileInputStream(xlsFilePath);
			
			Workbook workbook = new XSSFWorkbook(fis);
			for(int i = 0 ; i < workbook.getNumberOfSheets(); i++) {
				//获得工作簿表名
				String sheetName = workbook.getSheetName(i);
				result.add(i, sheetName);
			}
			fis.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}
	
	/**
	 * 取得工作簿中的所有行
	 * @param xlsFilePath
	 * @param sheetName
	 * @return
	 */
	public static Map<String , List<Map<String , String>> > getRows(String xlsFilePath, String sheetName){
		Map<String , List<Map<String , String>>> resultMap = new HashMap<String, List<Map<String,String>>>();
		
		List<Map<String , String>> resultCells;
		Map<String ,String> resultCell;
		
		try {
			FileInputStream fis = new FileInputStream(xlsFilePath);
			//POIFSFileSystem ts = new POIFSFileSystem(fis);
			//新建一个Excel的处理类
			
			/**
			 * 如果office是2013版本或之前的，就用HSSFWorkbook;高于2013版本，就用XSSFWork
			 */
			Workbook workBook = new XSSFWorkbook(fis);
			
			//获得工作表名
			Sheet sheet = workBook.getSheet(sheetName);
			
			int rowCounts = sheet.getPhysicalNumberOfRows();//获得行数		
			
			int columnCounts = sheet.getRow(0).getPhysicalNumberOfCells();//获得列数
			 
			//从第二行开始，第一行为工作簿名
			for(int i = 1 ; i < rowCounts ; i++) {
				Row row  = sheet.getRow(i);
				row.getCell(0).setCellType(Cell.CELL_TYPE_STRING);
				
				resultCells = new ArrayList<Map<String,String>>();
				resultCell = new HashMap<String, String>();
				
				String rowKey = row.getCell(0).toString();
				for(int j = 1 ; j < columnCounts; j++) {
					Cell cell = row.getCell(j);
					if(null != cell) {
						cell.setCellType(Cell.CELL_TYPE_STRING);
						String columnName = sheet.getRow(0).getCell(j).toString();
						String cellValue = cell.toString();
						
						//put一个列中的cell
						resultCell.put(columnName, cellValue);
					}
				}
				
				resultCells.add(resultCell);
				//添加一行
				resultMap.put(rowKey, resultCells);
			}
			//关闭输入流
			fis.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return resultMap;
	}
	
	
	
	
	
	
	
	
	
	
	
	
}
