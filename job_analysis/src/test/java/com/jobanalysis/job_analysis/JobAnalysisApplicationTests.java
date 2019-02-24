package com.jobanalysis.job_analysis;

import com.jobanalysis.job_analysis.dto.ProfessionDto;
import com.jobanalysis.job_analysis.entity.JobInfo;
import com.jobanalysis.job_analysis.service.IDataTransferService;
import com.jobanalysis.job_analysis.service.IJobService;
import com.jobanalysis.job_analysis.util.HBaseUtil;
import com.jobanalysis.job_analysis.util.HDFSUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import sun.management.snmp.jvmmib.JvmOSMBean;

import java.io.IOException;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class JobAnalysisApplicationTests {

    @Autowired
    private HDFSUtil hdfsUtil;

    @Autowired
    private IJobService jobService;

    @Autowired
    private IDataTransferService dataTransferService;


    @Test
    public void contextLoads() throws IOException {
        hdfsUtil.mkdir("/test");

    }

    @Test
    public void testHBase(){
        dataTransferService.updateHDFSInfoToMysql(2,"/market");
    }


    //@Test
    public void testHDFS() throws IOException {
        //hdfsUtil.ls("hdfs://127.0.0.1:19000/jobs");
    }

}
