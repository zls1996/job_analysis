package com.jobanalysis.job_analysis.service.impl;

import com.jobanalysis.job_analysis.dao.JobDao;
import com.jobanalysis.job_analysis.entity.JobHdfs;
import com.jobanalysis.job_analysis.service.IDataTransferService;
import com.jobanalysis.job_analysis.util.ExcelUtil;
import com.jobanalysis.job_analysis.util.HBase2HDFSUtil;
import com.jobanalysis.job_analysis.util.HBaseUtil;
import com.jobanalysis.job_analysis.util.HDFSUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created By 朱立松 on 2019/2/20
 * 文件传输相关信息
 */
@Service
public class DataTransferService implements IDataTransferService {

    @Autowired
    private HDFSUtil hdfsUtil;

    @Autowired
    private JobDao jobDao;

    @Autowired
    private HBaseUtil hBaseUtil;

    @Autowired
    private HBase2HDFSUtil hBase2HDFSUtil;

    /**
     * 将hdfs路径以及文件相关信息转储到mysql(根据jobSortId来分类, 遍历hdfsFolder)
     */
    @Override
    public void updateHDFSInfoToMysql(Integer jobSortId, String hdfsFolder) {
        try {
            //获得hdfsPath列表
            List<String> hdfsPathList = hdfsUtil.getFilesFromFolder(hdfsFolder);
            for(String hdfsPath : hdfsPathList){
                //获得hdfs文件名
                String hdfsFileName = hdfsPath.substring(hdfsPath.lastIndexOf("/") +1);
                JobHdfs jobHdfs  = new JobHdfs(hdfsFileName, hdfsPath, null, jobSortId);
                jobDao.insert(jobHdfs);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 将前端传进来的文件存放到HDFS
     * @param hdfsPath ：要存放的HDFS路径
     * @param sourceFile ：文件
     * @param hdfsPath:hdfs文件路径
     * @param id ：所属分类id
     * @return
     */
    @Override
    //设置事务隔离级别
    @Transactional(propagation = Propagation.REQUIRED)
    public String transferExcelToHDFS(MultipartFile sourceFile, String hdfsPath, Integer id) {
        try {
            //从excel中获得map数据
            Map<Integer, Map<String , String>>  dataMap =
                    ExcelUtil.getMapDataByFileInputStream(sourceFile.getInputStream(), HBaseUtil.DEFAULT_SHEET_NAME);
            String originalFilename = sourceFile.getOriginalFilename();
            String fileName = originalFilename.substring(0, originalFilename.indexOf("."));
            /**
             * 创建hbase表，
             * fileName:用传入的文件名来作为hbase表名
             * isCoverable:是否覆盖hbase已存在同名的表
             * cFamilyName:指定hbase列族，此处写死为job_info
             */
            hBaseUtil.createTable(fileName, false, HBaseUtil.DEFAULT_COLUMN_FAMILY);
            //向该表的列族插入得到的map数据
            hBaseUtil.insertData(fileName,HBaseUtil.DEFAULT_COLUMN_FAMILY,  dataMap);

            //接着将hbase数据存储到hdfs
            hBase2HDFSUtil.transferFromHBase2HDFS(fileName, HDFSUtil.DEFAULT_HDFS_PATH +hdfsPath, fileName, "transfer hbase data to HDFS");


            JobHdfs jobHdfs =new JobHdfs(fileName,  hdfsPath+"/"+fileName, null, id);
            //同时将生成的JobHDFs信息传入到mysql
            jobDao.insert(jobHdfs);

            return "Excel文件上传成功，数据已经存储到HDFS";

        } catch (IOException e) {
            e.printStackTrace();
            return "Excel文件上传失败!!!";
        }

    }
}
