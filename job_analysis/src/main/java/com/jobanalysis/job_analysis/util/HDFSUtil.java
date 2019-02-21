package com.jobanalysis.job_analysis.util;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.jobanalysis.job_analysis.entity.JobHdfs;
import com.jobanalysis.job_analysis.entity.JobInfo;
import com.jobanalysis.job_analysis.entity.ProfeArea;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.mapred.JobConf;


import org.apache.hadoop.fs.BlockLocation;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.springframework.stereotype.Component;

@Component
public class HDFSUtil {
	//这里的path写成hadoop中core-site.xml中的fs.default.nameֵ
	public static final String DEFAULT_HDFS_PATH = "hdfs://127.0.0.1:19000";
	
	private static String hdfsPath = DEFAULT_HDFS_PATH;

	private static final String FILE_SUFFIX = "/part-r-00000";
	
	private static Configuration conf = new Configuration();
	
	public static JobConf config() {
		JobConf jobConf = new JobConf();
		jobConf.setJobName("hdfsDao");
		return jobConf;
	}
	
	private  FileSystem getFileSystem() throws IOException {
		return FileSystem.get(URI.create(hdfsPath), conf);
	}
	
	/**
	 * 创建目录
	 * @param folder
	 * @throws IOException
	 */
	public  void mkdir(String folder) throws IOException {
		Path path = new Path(DEFAULT_HDFS_PATH+folder);
		FileSystem fs = getFileSystem();
		if(! fs.exists(path)) {
			fs.mkdirs(path);
			System.out.println("Create directory:" + folder);
		}
		fs.close();
	}
	
	/**
	 * 删除目录或文件
	 * @param folder
	 * @throws IOException
	 */
	public  void rm(String folder){
		Path path = new Path(DEFAULT_HDFS_PATH + folder);
		FileSystem fs;
		try {
			fs = getFileSystem();
			fs.deleteOnExit(path);
			System.out.println("Delete file or directory: " + folder);
			fs.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/**
	 *遍历目录文件
	 * @param folder
	 * @throws IOException
	 */
	@SuppressWarnings("deprecation")
	public  void ls(String folder) throws IOException {
		folder = DEFAULT_HDFS_PATH+folder;
		Path path = new Path(folder);
		FileSystem fs = getFileSystem();
		FileStatus[] list = getFileStatusByPath(path);
		System.out.println("ls : " + folder);
		System.out.println("========================================");
		for(FileStatus status : list) {
			System.out.printf("name: %s, folder : %s , size : %d\n", status.getPath(), status.isDir(), status.getLen());
			
		}
		System.out.println("========================================");
		fs.close();
	}

	/**
	 * 根据path获得FileStatus
	 * @param path
	 * @return
	 * @throws IOException
	 */
	private FileStatus[] getFileStatusByPath(Path path) throws IOException {
		FileSystem fs = getFileSystem();
		List<String> resultList = new ArrayList<>();
		FileStatus[] list = fs.listStatus(path);
		return list;
	}

	/**
	 * 得到一个hdfs文件夹中的所有文件名
	 * @param folder
	 * @return
	 */
	public List<String> getFilesFromFolder(String folder) throws IOException {
		folder = DEFAULT_HDFS_PATH + folder;
		Path path = new Path(folder);
		List<String> resultList = new ArrayList<>();
		FileStatus[] list = getFileStatusByPath(path);
		System.out.println("ls : " + folder);
		System.out.println("========================================");

		for(FileStatus status : list) {
			String hdfsPath = status.getPath().toString();
			resultList.add(hdfsPath.split(DEFAULT_HDFS_PATH)[1]);

		}
		return resultList;
	}



	/**
	 * 创建文件
	 * @param filename
	 * @param content
	 * @throws IOException
	 */
	public  void createFile(String filename , String content) throws IOException {
		FileSystem fs = getFileSystem();
		byte[] buffer = content.getBytes();
		FSDataOutputStream fsos = null;
		try {
			fsos = fs.create(new Path(filename));
			fsos.write(buffer, 0, buffer.length);
			System.out.println("Create new File: " + filename);
		} finally {
			if(null != fsos) {
				fsos.close();
			}
		}
		fs.close();
		
	}
	
	/**
	 * 将本地文件复制到HDFS
	 * @param localPath
	 * @param hdfsPath
	 * @throws IOException
	 */
	public  void copyFromLocalToHDFS(String localPath, String hdfsPath){
		FileSystem fs;
		try {
			fs = getFileSystem();
			fs.copyFromLocalFile(new Path(localPath), new Path(hdfsPath));
			System.out.println("copy from localPath:" + localPath + " to HDFSPath: "+ hdfsPath);
			fs.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	
	}
	
	/**
	 * 从HDFS目录下载文件到本地
	 * @param hdfsPath
	 * @param localPath
	 * @throws IOException
	 */
	public  void downloadFromHDFS(String hdfsPath, String localPath) throws IOException {
		Path path = new Path(hdfsPath);
		FileSystem fs = getFileSystem();
		fs.copyToLocalFile(path, new Path(localPath));
		
		System.out.println("download file from HDFSPath : " + hdfsPath + " to localPath : " + localPath);
		fs.close();
	}
	
	/**
	 * 查看文件内容
	 * @param hdfsPath
	 * @return
	 * @throws IOException
	 */
	public  String cat(String hdfsPath) throws IOException {
		Path path = new Path(hdfsPath);
		FileSystem fs = getFileSystem();
		FSDataInputStream fsis = null;
		System.out.println("cat : "+ hdfsPath);
		
		OutputStream baos = new ByteArrayOutputStream();
		String str = null;
		try {
			fsis = fs.open(path);
			IOUtils.copyBytes(fsis, baos, 4096, false);
			str = baos.toString();
		} finally {
			IOUtils.closeStream(fsis);
			fs.close();
		}
		System.out.println(str);
		
		return str;
	}
	
	/**
	 * 返回给定文件的位置
	 * @throws IOException
	 */
	public  void location() throws IOException {
		String folder  = hdfsPath + "/";
		String file = "sample.txt";
		FileSystem fs = FileSystem.get(URI.create(hdfsPath), new Configuration());
		
		FileStatus status = fs.getFileStatus(new Path(folder + file));
		BlockLocation[] list = fs.getFileBlockLocations(status, 0, status.getLen());
		
		System.out.println("File Location : " + folder +file);
		for(BlockLocation bl : list) {
			String [] hosts = bl.getHosts();
			for(String host : hosts){
				System.out.println("host :" + host);
			}
		}
		fs.close();
		
	}


	/**
	 * 重命名文件
	 * @param oldPath
	 * @param newPath
	 */
	public void rename(String oldPath, String newPath) throws URISyntaxException, IOException, InterruptedException {
		oldPath = DEFAULT_HDFS_PATH + oldPath;
		newPath = DEFAULT_HDFS_PATH + newPath;
		FileSystem fs = FileSystem.get(new URI(DEFAULT_HDFS_PATH + "/"), conf);
		fs.rename(new Path(oldPath), new Path(newPath));
		System.out.println("rename file :" + oldPath + " to new name: " + newPath);
	}

	/**
	 * 读取hdfs文件内容
	 * @param filePath
	 */
	public List<JobInfo> readHDFSFileContent(String filePath){
		BufferedReader bufferedReader = getBufferedReader(filePath);
		String lineContent = null;
		List<JobInfo> resultList = new ArrayList<JobInfo>();
		try {

			while((lineContent = bufferedReader.readLine()) != null){
				JobInfo jobInfo = handleLineData2Object(lineContent.split("\t"));
				resultList.add(jobInfo);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			closeBufferedReader(bufferedReader);
		}

		return resultList;


	}

	/**
	 * 将hdfs的一行数据转成java Object (JobInfo)
	 * @param lineData
	 * @return
	 */
	private JobInfo handleLineData2Object(String[] lineData) {
		List<String> container = new ArrayList<>();
		for(String column : lineData){
			if(column.contains(":")) {
				container.add(column.split(":")[1]);
			}else{
				container.add(column);
			}
		}
		int i = 0;
		return new JobInfo(Integer.parseInt(container.get(i++)),container.get(i++),
				container.get(i++),container.get(i++),container.get(i++),
				container.get(i++),container.get(i++),	container.get(i++));
	}

	/**
	 * 获得hdfs文件中数据的记录数
	 * @param hdfsPath
	 * @return
	 */
	public Integer getElementCount(String hdfsPath) {

		int count = 0;
		BufferedReader bufferedReader = getBufferedReader(hdfsPath);
		try {
			String lineContent = null;
			while((lineContent = bufferedReader.readLine()) != null){
				count++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			closeBufferedReader(bufferedReader);
		}

		return count;
	}

	/**
	 * 得到HDFS的BufferedReader
	 * @return
	 */
	private BufferedReader getBufferedReader(String hdfsPath){
		StringBuffer buffer = new StringBuffer();

		FSDataInputStream fsDataInputStream = null;

		BufferedReader bufferedReader = null;
		FileSystem fs = null;

		try {
			fs = FileSystem.get(URI.create(DEFAULT_HDFS_PATH+hdfsPath+FILE_SUFFIX), conf);
			fsDataInputStream = fs.open(new Path(DEFAULT_HDFS_PATH+hdfsPath + FILE_SUFFIX));
			bufferedReader = new BufferedReader(new InputStreamReader(fsDataInputStream));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bufferedReader;

	}

	/**
	 * 关闭BufferedReader
	 * @param bufferedReader
	 */
	private void closeBufferedReader(BufferedReader bufferedReader){
		if(bufferedReader != null){
			try {
				bufferedReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 通过hdfs文件路径来对不同城市在该文件中的职位数量
	 * @param hdfsPath
	 * @return
	 */
	public List<ProfeArea> sortJobCountByCity(String hdfsPath) {
		BufferedReader bufferedReader = getBufferedReader(hdfsPath);
		List<ProfeArea> resultList = new ArrayList<>();
		try {
			String lineContent = null;
			Map<String , Integer> cityMap = new HashMap<>();
			while((lineContent = bufferedReader.readLine()) != null){
				//从一行数据中提取城市
				String city = lineContent.split("\t")[1].split(":")[1];
				if(cityMap.containsKey(city)){
					cityMap.put(city, cityMap.get(city) +1);
				}else{
					cityMap.put(city, 1);
				}

			}

			for(Map.Entry<String , Integer> entry: cityMap.entrySet()){
				ProfeArea profeArea = new ProfeArea(entry.getKey(), entry.getValue());
				resultList.add(profeArea);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			if(bufferedReader != null){
				try {
					bufferedReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return resultList;

	}

	/**
	 * 根据特定信息来获得相应的工作职位列表
	 * @param hdfsPath
	 * @param jobname:职位名称
	 * @param area:地区
	 * @param edu:学历
	 * @return
	 */
	public List<JobInfo> getJobByNameAndAreaAndEdu(String hdfsPath, String jobname, String area, String edu){
		BufferedReader bufferedReader = getBufferedReader(hdfsPath);
		List<JobInfo> resultList = new ArrayList<>();
		String lineContent = null;
		try{
			while ((lineContent = bufferedReader.readLine()) != null){
				//获得一行中的职位名称
				String lineJobName = lineContent.split("\t")[5].split(":")[1];
				//获得一行中的城市地区
				String lineArea = lineContent.split("\t")[1].split(":")[1];
				//获得一行中的学历
				String lineEdu = lineContent.split("\t")[3].split(":")[1];

				//如果该行中包含所给定满足的条件
				if(lineJobName.contains(jobname) &&
				lineArea.contains(area) && lineEdu.contains(edu)){
					//将该行数据封装成JobInfo
					JobInfo jobInfo = handleLineData2Object(lineContent.split("\t"));
					resultList.add(jobInfo);
				}
			}
		}catch (IOException e){
			e.printStackTrace();
		}
		return resultList;
	}
}
