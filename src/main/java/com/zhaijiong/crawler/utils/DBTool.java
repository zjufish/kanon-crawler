package com.zhaijiong.crawler.utils;

import com.zhaijiong.crawler.Config;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class DBTool {
    private static final Logger LOG = LoggerFactory.getLogger(DBTool.class);

    private static HConnection conn;
    private Config config;

    public DBTool(Config config){
        this.config = config;
        try {
            conn = HConnectionManager.createConnection(Utils.getHBaseConf(config));
        } catch (ZooKeeperConnectionException e) {
            LOG.error("failed to create hbase connection.");
        }
    }

    public static void printRow(String tableName,String key) throws IOException {
        byte[] rowBytes = key.getBytes();
        Get get = new Get(Utils.withMD5Prefix(rowBytes));
        Result result = conn.getTable(tableName).get(get);
        System.out.println(result.toString());
    }

    public void createTable(String tableName) throws IOException {
        HBaseAdmin admin = new HBaseAdmin(Utils.getHBaseConf(config));
        if(admin.tableExists(tableName)){
            LOG.error(String.format("table %s is aleady exist.",tableName));
        }else{
            HTableDescriptor tableDescriptor = new HTableDescriptor(tableName);
            //TODO
        }
    }

    public void deleteTable(String tableName) throws IOException {
        HBaseAdmin admin = new HBaseAdmin(Utils.getHBaseConf(config));
        if(admin.tableExists(tableName)){
            admin.disableTable(tableName);
            admin.deleteTable(tableName);
            LOG.info(String.format("success to delete table %s",tableName));
        }else{
            LOG.info(String.format("table %s is not exist",tableName));
        }
    }

    public void cleanTable(String tableName) throws IOException {
        HBaseAdmin admin = new HBaseAdmin(Utils.getHBaseConf(config));
        HTableDescriptor tableDescriptor = admin.getTableDescriptor(tableName.getBytes());
        if(admin.tableExists(tableName)){
            admin.disableTable(tableName);
            admin.deleteTable(tableName);
            LOG.info(String.format("success to delete table %s",tableName));
            admin.createTable(tableDescriptor);
            LOG.info(String.format("success to create table %s",tableName));
        }else{
            LOG.info(String.format("table %s is not exist",tableName));
        }
    }

    public void clearupDatabase(){
        try {
            cleanTable(config.getStr(Constants.KANON_CRAWLER_URL_TABLE,Constants.KANON_CRAWLER_URL_TABLE_DEFAULT));
            cleanTable(config.getStr(Constants.KANON_CRAWLER_DATA_TABLE,Constants.KANON_CRAWLER_DATA_TABLE_DEFAULT));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) throws IOException {
        Config config = new Config(args[0]);
        DBTool tool = new DBTool(config);
        tool.clearupDatabase();
    }
}
