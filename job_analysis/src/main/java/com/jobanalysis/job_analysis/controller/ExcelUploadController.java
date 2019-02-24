package com.jobanalysis.job_analysis.controller;

import com.jobanalysis.job_analysis.service.IDataTransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created By 朱立松 on 2019/2/20
 * Excel文件上传接口
 */
@RestController
@RequestMapping("/file")
public class ExcelUploadController {

    @Autowired
    private IDataTransferService dataTransferService;

    /**
     * Excel文件上传
     * @param file：文件
     * @param ：文件名
     * @param path:上传到的HDFS路径
     * @param id：所属分类
     * @return
     */
    @RequestMapping("/upload")
    public String uploadExcelFile(@RequestParam("file")MultipartFile file , String path, Integer id){
        if(file.isEmpty()){
            return "文件为空";
        }
        String reponse = dataTransferService.transferExcelToHDFS(file, path, id);
        return reponse;
    }

    //@RequestMapping(value = "/upload",method = RequestMethod.POST)
    public String uploadExcelFiles(@RequestParam("file") MultipartFile []file, String path, Integer id){
        if(file == null || file.length == 0){
            return "文件为空";
        }
        String response = new String();
        for(MultipartFile fi : file){
            response = dataTransferService.transferExcelToHDFS(fi, path, id);
        }
        return response;
    }
}
