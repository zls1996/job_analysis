package com.jobanalysis.job_analysis.mapreduce.areaSalary;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

/**
 * 把数据切分，以地区为key，薪资和公司为value
 */
public class JobPredMap extends Mapper<Object, Text, Text, Text> {

    @Override
    protected void map(Object key, Text value, Context context) throws IOException, InterruptedException {

        String[] splitData = value.toString().split("\t");
        if ((splitData[1]).contains("address")){//城市名字为address，不是表中要处理的数据
            context.write(new Text(splitData[1].split(":")[1]), new Text(splitData[6].split(":")[1]));//key为城市，value为薪资
        }
    }
    }

