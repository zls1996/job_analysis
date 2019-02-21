package com.jobanalysis.job_analysis;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jobanalysis.job_analysis.util.ExcelUtil;
import com.jobanalysis.job_analysis.util.HBase2HDFSUtil;
import com.jobanalysis.job_analysis.util.HBaseUtil;
import com.jobanalysis.job_analysis.util.HDFSUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ExportExcelToHDFS {

	@Autowired
	private HBase2HDFSUtil hBase2HDFSUtil;

	@Autowired
	private HBaseUtil hBaseUtil;

	@Autowired
	private HDFSUtil hdfsUtil;

	private static Long begin;
	
	private static Long end;
	
	private static String excelPath = "F:\\job_excel\\data_lagou\\CDN.xlsx";
	
	private static String sheetName = "Sheet1";
	
	private static String hTableName = "CDN";
	
	private String hdfsFileName = "CDN";
	
	@BeforeClass
	public static void start() {
		begin = System.currentTimeMillis();
		System.out.println("start to execute !!!");
		
		
		
	
	}
	
	@AfterClass
	public static void end() {
		end = System.currentTimeMillis();
		System.out.println("end to execute !!!");
		System.out.println("Time costs: " + (end-begin)/1000 + "s");
	}
	
	public void printMap(Map<Integer, Map<String, String>> map) {
		for(Entry<Integer, Map<String, String>> entry : map.entrySet()) {
			System.out.print(entry.getKey() + "\t");
			Map<String , String> childMap = entry.getValue();
			for(Entry<String , String > childEntry: childMap.entrySet()) {
				String key = childEntry.getKey();
				System.out.print(key + ":" + childMap.get(key) + "\t");
			}
			System.out.println();
		}
	}
	


	public void transferToHBase() {
		//获得excel的map数据
		Map<Integer, Map<String, String>> map  = ExcelUtil.getExcelData(excelPath, sheetName);

		//printMap(map);
		HBaseUtil hbase = new HBaseUtil();
		
		String tableName = hTableName;
		
		String columnFamily = new String("job_info");
		
		hbase.createTable(tableName, false, columnFamily);
		
		hbase.insertData(tableName, columnFamily, map);
		
		//System.out.println("插入成功");
		
		
	}
	
	public void transferFromHBase2HDFS() {
		String hdfsFilePath = "hdfs://127.0.0.1:19000/jobs";
		
		String description = "exportFrom table :" + hTableName + " to hdfs";
		hBase2HDFSUtil.transferFromHBase2HDFS(hTableName, hdfsFilePath, hdfsFileName, description);
	}
	
	@Test
	public void excelToHDFS() throws InterruptedException, IOException, URISyntaxException {
//
//		Integer i = hdfsUtil.getElementCount("/dev/adv_creation");
//		System.out.println(i);
	}


	@Test
	public void exportAllExcelDataToHDFS() throws IOException {
		String folderPath = "F:\\job_excel\\data_lagou";

		File parentFile = new File(folderPath);

		File[] lists = parentFile.listFiles();

		for (File file : lists) {
			String excelFilePath = folderPath + "\\" + file.getName();
			String fileName = file.getName().split(".xlsx")[0];
			if(isContainChinese(fileName)){
				continue;
			}
			System.out.println(fileName);
			excelPath = excelFilePath;
			hTableName = fileName;
			//excelToHDFS();

		}
		//hdfsUtil.mkdir("/jobs");
	}

	public static boolean isContainChinese(String str) {

		Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
		Matcher m = p.matcher(str);
		if (m.find()) {
			return true;
		}
		return false;
	}

	
	
	

}
