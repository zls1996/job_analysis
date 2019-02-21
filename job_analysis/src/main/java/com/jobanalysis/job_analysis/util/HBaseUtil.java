package com.jobanalysis.job_analysis.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.NamespaceDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

/**
 * 
 * @author 朱立松
 *
 */
@Component
public class HBaseUtil {
private static Logger log = Logger.getLogger(DBHelperHBase.class);
	
	//HBase主节点
	private final static String MASTER_IP = "127.0.0.1";
	
	//HBase表的命名空间
	private final static String HBASE_TABLE_NAMESPACE = "hbase_tb";

	//默认列族
	public final static String DEFAULT_COLUMN_FAMILY = "job_info";

	//默认excel中的sheet名
	public final static String DEFAULT_SHEET_NAME = "Sheet1";

	//conf
	public static Configuration conf = null;
	
	//HBAseAdmin
	private HBaseAdmin hbaseAdmin = null;
	
	//HTable管理对象
	private HTable _hTableAdmin = null;
	
	private ResultScanner resultScanner = null;
	
	//删锁表
	@SuppressWarnings("unused")
	private Boolean lockDelTable = true;
	
	//删行锁
	@SuppressWarnings("unused")
	private Boolean lockDelRow = true;
	
	private static final String DEFAULT_ENDODING = "UTF-8";
	
	
	
	static {
		conf = HBaseConfiguration.create();
		//设置hbase的zookeeper
		conf.set("hbase.zookeeper.quorum", "127.0.0.1:2181");
		//conf.set("hbase.master", "192.168.220.132:60000");
		//conf.set("hbase.zookeeper.property.clientPort", "2181");
		
	}
	
	/**
	 * 获得连接
	 * @return
	 */
	private Connection getConnection() {
		try {
			Connection connection = ConnectionFactory.createConnection(conf);
			return connection;
		} catch (IOException e) {
			log.error("连接异常！！！");
			e.printStackTrace();
		}
		return null;
	}

	public HBaseUtil() {
		this.createHBaseNameSpace();
	}
	
	/**
	 * 创建HBase命名空间
	 */
	@SuppressWarnings("deprecation")
	private void createHBaseNameSpace() {
		
		try {
			this.hbaseAdmin = new HBaseAdmin(conf);
			if(! this.existsHBaseNameSpace()) {
				NamespaceDescriptor descriptor = NamespaceDescriptor.create(HBASE_TABLE_NAMESPACE).build();
				this.hbaseAdmin.createNamespace(descriptor);
				log.info(String.format("Create HBase Namespace [%s] success", HBASE_TABLE_NAMESPACE));
			}
		} catch (MasterNotRunningException e1) {
			log.error(e1);
			e1.printStackTrace();
		} catch (ZooKeeperConnectionException e1) {
			log.error(e1);
			e1.printStackTrace();
		} catch (IOException e1) {
			log.error(e1);
			e1.printStackTrace();
		}finally {
			this.closeHBaseAdmin("CreateHBaseNameSpace");
		}
		
		
	}
	
	/**
	 * 判断是否存在HBase命名空间
	 * @return
	 */
	@SuppressWarnings("deprecation")
	private boolean existsHBaseNameSpace() {
		Boolean result = false;
		try {
			this.hbaseAdmin = new HBaseAdmin(conf);
			
			//取得现有命名空间列表
			NamespaceDescriptor[] nds = this.hbaseAdmin.listNamespaceDescriptors();
			for(NamespaceDescriptor space: nds) {
				//判断是否存在此空间
				if(space.getName().equals(HBASE_TABLE_NAMESPACE)) {
					result = true;
					break;
				}
			}
			
			log.info(String.format("HBase NameSpace Exists[%s]!", result));
		} catch (MasterNotRunningException e) {
			e.printStackTrace();
		} catch (ZooKeeperConnectionException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	/**
	 * 删除HBase命名空间
	 */
	@SuppressWarnings("deprecation")
	public void deleteHBaseNameSpace() {
		try {
			this.hbaseAdmin = new HBaseAdmin(conf);
			if(this.existsHBaseNameSpace()) {
				this.hbaseAdmin.deleteNamespace(HBASE_TABLE_NAMESPACE);
				log.info(String.format("Delete HBase Namespace [%s] success", HBASE_TABLE_NAMESPACE));
			}
		} catch (MasterNotRunningException e) {
			e.printStackTrace();
		} catch (ZooKeeperConnectionException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			this.closeHBaseAdmin("deleteHBaseNamespace");
		}
		
	}
	
	/**
	 * 关闭HBase连接
	 * @param string
	 */
	private void closeHBaseAdmin(String info) {
		try {
			if(this.hbaseAdmin != null){
				this.hbaseAdmin.close();
				log.info(info + "(...):关闭与HBase的连接");
			}
		} catch (IOException e) {
			log.error(e);
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 表是否存在判断
	 * @param tableName
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public Boolean existsTable(String tableName) {
		Boolean result = false;
		try {
			this.hbaseAdmin = new HBaseAdmin(conf);
			result = this.hbaseAdmin.tableExists(tableName);
		} catch (MasterNotRunningException e) {
			e.printStackTrace();
		} catch (ZooKeeperConnectionException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			this.closeHBaseAdmin("ExistsTable");
		}
		return result;
	}
	
	
	public void createTable(String tableName , String ... cFamilyName) {
		this.createTable(tableName, false, cFamilyName);
	}
	
	/**
	 * 创建表
	 * @param tableName
	 * @param b
	 * @param cFamilyName
	 */
	@SuppressWarnings("deprecation")
	public void createTable(String tableName, boolean isCoverable, String ... cFamilyName) {
		log.info("start create table ...");
		
		try {
			this.hbaseAdmin = new HBaseAdmin(conf);
			tableName = HBASE_TABLE_NAMESPACE + ":" + tableName;
			
			/**
			 * 表是否存在判断
			 */
			if(this.existsTable(tableName)) {
				//是否强制新建表
				if(isCoverable) {
					this.hbaseAdmin.disableTable(tableName);//禁用表
					this.hbaseAdmin.deleteTable(tableName);//删除表
					log.info(tableName + "is exits, delete ... ");
					
					//开始建表
					TableName tname = TableName.valueOf(tableName);
					HTableDescriptor htd = new HTableDescriptor(tname);
					
					//加列族
					for(int i = 0 ; i < cFamilyName.length ; i++) {
						//列族
						HColumnDescriptor hcd = new HColumnDescriptor(cFamilyName[i]);
						htd.addFamily(hcd);
						
					}
					
					//建表
					this.hbaseAdmin.createTable(htd);
					
					log.info("Cover HBase Table [ " + tableName + " ] success !");
				}else {
					log.info(tableName + " is exit ....... no create table !");
				}
			}else {
				//当前不存在表，创建新表
				//开始建表
				TableName tname = TableName.valueOf(tableName);
				HTableDescriptor htd = new HTableDescriptor(tname);
				
				//加列族
				for(int i = 0 ; i <cFamilyName.length ; i++) {
					///列族
					HColumnDescriptor hcd = new HColumnDescriptor(cFamilyName[i]);
					htd.addFamily(hcd);
				}
				
				//建表
				this.hbaseAdmin.createTable(htd);
				
				log.info("Create new HBase Table [ " + tableName + " ] success !");
				
			}
		} catch (MasterNotRunningException e) {
			e.printStackTrace();
		} catch (ZooKeeperConnectionException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		log.info("end create table ...");
	}
	
	/**
	 * 添加一行多限定符的数据
	 * @param tableName
	 * @param columnFamily
	 * @param rowKey：行键
	 * @param cqAndValue：列名和值的键值对
	 */
	@SuppressWarnings("deprecation")
	public void addRowData(String tableName, String columnFamily, String rowKey, Map<String , Object> cqAndValue) {
		//hbase定义的rowkey不超过两到三个
		if(cqAndValue.isEmpty()) {
			return ;
		}
		tableName = HBASE_TABLE_NAMESPACE + ":" + tableName;
		try {
			List<Put> puts = new ArrayList<Put>();
			for(String cq :cqAndValue.keySet()) {
				
					this._hTableAdmin = new HTable(conf, tableName);
	
					Put put = new Put(Bytes.toBytes(rowKey));
					//写入行中的列块
					put.add(columnFamily.getBytes(DEFAULT_ENDODING), cq.getBytes(DEFAULT_ENDODING), 
							cqAndValue.get(cq).toString().getBytes(DEFAULT_ENDODING));
					puts.add(put);
			}
			_hTableAdmin.put(puts);
		} catch (IOException e) {
			log.error(e);
			e.printStackTrace();
		}finally {
			this.closeHBaseAdmin("addRowData");
		}
	}
	
	/**
	 * 根据表名、列族、map来插入数据
	 * @param tableName
	 * @param dataMap
	 */
	@SuppressWarnings("deprecation")
	public void insertData(String tableName, String columnFamily, Map<Integer, Map<String, String>> dataMap) {
		try {
			tableName = HBASE_TABLE_NAMESPACE + ":" + tableName;
			//TableName tablename = TableName.valueOf(tableName);
			List<Put> resultPuts = new ArrayList<Put>();
			this._hTableAdmin = new HTable(conf, tableName);
			for(Entry<Integer, Map<String , String>> dataEntry: dataMap.entrySet()) {
				Integer rowKey = dataEntry.getKey();
				Put put = new Put(rowKey.toString().getBytes(DEFAULT_ENDODING));
				Map<String , String > childMap = dataEntry.getValue();
				for(Entry<String , String> childEntry :childMap.entrySet()) {
					String columnName = childEntry.getKey();
					String columnValue = childEntry.getValue();
					//参数：1.列族名 2.列名 3.列值
					put.addColumn(columnFamily.getBytes(DEFAULT_ENDODING), columnName.getBytes(DEFAULT_ENDODING),
							columnValue.getBytes(DEFAULT_ENDODING));
				}
				resultPuts.add(put);
			}
			this._hTableAdmin.put(resultPuts);
			this.closeHBaseAdmin("insertData for table : " + tableName);
		} catch (IOException e) {
			log.error(e);
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 导出hbase数据
	 * @param tableName
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public Map<Integer, Map<String, String>> exportData(String tableName){
		Map<Integer, Map<String, String>> resultMap = new HashMap<Integer, Map<String, String>>();
		tableName = HBASE_TABLE_NAMESPACE + ":" + tableName;
		//获取TableName
		TableName tablename = TableName.valueOf(tableName);
		
		try {
			this._hTableAdmin = new HTable(conf, tableName);
			Table table = getConnection().getTable(tablename);
			Scan scan = new Scan();
			ResultScanner resultScanner = table.getScanner(scan);
			
			Map<String , String > cellMap = null;
			//遍历ResultScanner
			for(Result result : resultScanner) {
				cellMap = new HashMap<String, String>();
				String rowKey = null;
				for(Cell cell : result.rawCells()) {
					//获得rowKey
					rowKey = Bytes.toString(cell.getRowArray(), cell.getRowOffset(), cell.getRowLength());
					//获得列名
					String colName = Bytes.toString(cell.getQualifierArray(),
							cell.getQualifierOffset(),cell.getQualifierLength());
					
					String columnValue = Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength());
					cellMap.put(colName, columnValue);
					
				}
				resultMap.put(Integer.parseInt(rowKey), cellMap);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
 		return resultMap;
	}
	
	
	public void closeResultScanner(String methodName) {
		this.resultScanner.close();
		log.info(methodName  + "(...) :关闭与ResultScanner的连接！");
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
