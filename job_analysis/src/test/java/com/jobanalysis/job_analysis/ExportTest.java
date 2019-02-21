package com.jobanalysis.job_analysis;

import com.jobanalysis.job_analysis.util.ExcelUtil;
import com.jobanalysis.job_analysis.util.HBase2HDFSUtil;
import com.jobanalysis.job_analysis.util.HBaseUtil;
import com.jobanalysis.job_analysis.util.HDFSUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Created By 朱立松 on 2019/2/19
 */

public class ExportTest {



    private static final String sheetName = "Sheet1";


    public void transferToHBase(String excelFilePath, String hTableName) {
        //获得excel的map数据
        Map<Integer, Map<String, String>> map  = ExcelUtil.getExcelData(excelFilePath, sheetName);

        //printMap(map);
        HBaseUtil hbase = new HBaseUtil();

        String tableName = hTableName;

        String columnFamily = new String("job_info");

        hbase.createTable(tableName, true, columnFamily);

        hbase.insertData(tableName, columnFamily, map);

        //System.out.println("插入成功");


    }

    public void transferFromHBase2HDFS(String hTableName, String hdfsFileName) {
        String hdfsFilePath = "hdfs://127.0.0.1:19000/jobs";

        String description = "exportFrom table :" + hTableName + " to hdfs";
        HBase2HDFSUtil hBase2HDFSUtil = new HBase2HDFSUtil();
        hBase2HDFSUtil.transferFromHBase2HDFS(hTableName, hdfsFilePath, hdfsFileName, description);
    }


    @Test
    public void excelToHDFS() {
        String excelFilePath = "F:\\job_excel\\data_lagou\\COCOS2D-X.xlsx";
        String h = excelFilePath.substring(excelFilePath.lastIndexOf("\\") +1);
        String hTableName = h.split(".xlsx")[0];
        //String hTableName = "CFO";
        String hdfsFileName = hTableName;
        //transferToHBase(excelFilePath,hTableName);
        transferFromHBase2HDFS(hTableName, hdfsFileName);
    }

    //@Test
    public void exportAllExcelDataToHDFS() throws IOException {
        String folderPath = "F:\\job_excel\\data_lagou";

        File parentFile = new File(folderPath);

        File[] lists = parentFile.listFiles();

        for (File file : lists) {
            String excelFilePath = folderPath + "\\" + file.getName();
            String fileName = file.getName().split(".xlsx")[0];
            System.out.println(fileName);
            //excelToHDFS(excelFilePath, fileName, fileName);

        }
        //hdfsUtil.mkdir("/jobs");
    }
}
