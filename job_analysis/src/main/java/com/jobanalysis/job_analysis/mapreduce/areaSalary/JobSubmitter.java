package com.jobanalysis.job_analysis.mapreduce.areaSalary;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * 用于提交mapreduce job的客户端程序
 * 功能：
 *   1、封装本次job运行时所需要的必要参数
 *   2、跟yarn进行交互，将mapreduce程序成功的启动、运行
 * @author ThinkPad
 *
 */
@Component
public class JobSubmitter {

	//@Value("${hadoop.hdfs.path}")
	private static final String DEFAULT_HDFS_HEARDER = "hdfs://127.0.0.1:19000";

	public void createMapReduceJob(String inputPath, String outputPath) {
		Configuration conf = new Configuration();
		Job job = null;
		try {
			job = Job.getInstance(conf);
		} catch (IOException e) {
			e.printStackTrace();
		}
		job.setJarByClass(JobSubmitter.class);
		job.setMapperClass(JobPredMap.class);
		job.setReducerClass(JobpredReduce.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		//"hdfs://127.0.0.1:19000/test/city_salary"
		Path output = new Path(DEFAULT_HDFS_HEARDER + outputPath);
		FileSystem fs = null;
		try {
			fs = FileSystem.get(new URI(DEFAULT_HDFS_HEARDER),conf);
			if(fs.exists(output)){
				fs.delete(output, true);
			}

			//"hdfs://127.0.0.1:19000/market/*/part-r-00000"
			FileInputFormat.setInputPaths(job, new Path(DEFAULT_HDFS_HEARDER +inputPath));
			FileOutputFormat.setOutputPath(job, output);  // 注意：输出路径必须不存在

			job.setOutputFormatClass(TextOutputFormat.class);

			job.setNumReduceTasks(1);

			job.waitForCompletion(true);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}


	}
	
	public static void main(String[] args) throws Exception {

		
	}
	
	

}
