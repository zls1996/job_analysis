package com.jobanalysis.job_analysis.util;

import java.io.File;
import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * 将hbase的数据导入hdfs
 * @author 朱立松
 *
 */
@Component
public class HBase2HDFSUtil {

	@Autowired
	private HDFSUtil hdfsUtil;

	private static Configuration conf = HBaseConfiguration.create();
	
	//HBase表的命名空间
	private final static String HBASE_TABLE_NAMESPACE = "hbase_tb";
	
	
	@SuppressWarnings("unused")
	private static final String BYTES_ENCODING = "UTF-8";
	
	static {
		//设置zookeeper
		conf.set("hbase.zookeeper.quorum", "127.0.0.1:2181");
		//设置hdfs存储路径
		conf.set("fs.defaultFS", "hdfs://127.0.0.1:19000");
	}
	
	private static class TableMap extends TableMapper<Text,Text> {


		@Override
		protected void map(ImmutableBytesWritable key, Result value,
				Mapper<ImmutableBytesWritable, Result, Text, Text>.Context context)
				throws IOException, InterruptedException {
			//这里的key就是hbase的rowkey
			@SuppressWarnings("unused")
			String rowKey;
			StringBuilder sb = new StringBuilder();
			for(Cell cell : value.rawCells()) {
				//获得rowKey
				rowKey = Bytes.toString(cell.getRowArray(), cell.getRowOffset(), cell.getRowLength());
				//获得列名
				String colName = Bytes.toString(cell.getQualifierArray(),
						cell.getQualifierOffset(),cell.getQualifierLength());
				
				String columnValue = Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength());
				sb.append(colName).append(":").append(columnValue).append("\t");
			}
			context.write(new Text(key.get()), new Text(sb.toString()));
		}
		
	}
	
	
	private static class HDFSReducer extends Reducer<IntWritable, Text, IntWritable, Text>{
		private Text result = new Text();

		@Override
		protected void reduce(IntWritable key, Iterable<Text> values, Context context)
				throws IOException, InterruptedException {
			for(Text val : values) {
				result.set(val);
				context.write(key, val);
			}
		}
		
	}
	
	/**
	  *   将hbase数据传递到hdfs
	 * @param tableName : hbase表名
	 * @param hdfsFileName： hdfs文件名
	 * @param description：job描述
	 */
	public void transferFromHBase2HDFS(String tableName, String hdfsFilePath ,String hdfsFileName , String description) {
		createJob(tableName,hdfsFilePath, hdfsFileName, description);
	}
	
	/**
	 * 初始化Job
	 * @param tableName 
	 * @param hdfsFilePath
	 * @param description
	 * @param hdfsFileName 
	 */

	private  void createJob(String tableName,String hdfsFilePath, String hdfsFileName , String description) {
		try {
			Job job = new Job(conf);
			job.setJarByClass(HBase2HDFSUtil.class);
			//创建对Hbase的扫描类
			Scan scan = new Scan();
			//选择相应的hbase表作为map输入
			TableMapReduceUtil.initTableMapperJob(HBASE_TABLE_NAMESPACE + ":"+ tableName, scan, TableMap.class,
					Text.class,Text.class, job, false);

			String realHDFSFilePath =  hdfsFilePath + "/" + hdfsFileName;
			
			Path outPath = new Path(realHDFSFilePath);
			
			FileSystem.get(conf).delete(outPath, true);
			
			FileOutputFormat.setOutputPath(job, outPath);

			job.waitForCompletion(true);

			System.out.println(hdfsFileName + "作业完成");
		} catch (IOException | InterruptedException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	



}
		
			
		
	
	
	
	

