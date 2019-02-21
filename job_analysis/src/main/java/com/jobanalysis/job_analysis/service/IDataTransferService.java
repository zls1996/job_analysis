package com.jobanalysis.job_analysis.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Created By 朱立松 on 2019/2/20
 */
public interface IDataTransferService {

    /**
     * 更新hdfs路径信息到mysql
     */
    void updateHDFSInfoToMysql(Integer jobSortId, String hdfsFolder);

    String transferExcelToHDFS(MultipartFile file, String hdfsPath, Integer id);
}
