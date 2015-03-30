package com.zhaijiong.crawler.utils;

import com.zhaijiong.crawler.Config;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class HBaseTool {
    private static final Logger LOG = LoggerFactory.getLogger(HBaseTool.class);

    private static HConnection conn;
    private Config config;

    public HBaseTool(Config config){
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

    public void clearupDatabase(){
        try {
            deleteTable(config.getStr(Constants.KANON_CRAWLER_URL_TABLE,Constants.KANON_CRAWLER_URL_TABLE_DEFAULT));
            deleteTable(config.getStr(Constants.KANON_CRAWLER_DATA_TABLE,Constants.KANON_CRAWLER_DATA_TABLE_DEFAULT));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) throws IOException {
//        String table = Constants.KANON_CRAWLER_URL_TABLE_DEFAULT;
//        String url = "http://stock.stockstar.com/JC2014091100001864.shtml";
//        HBaseTool.printRow(table,url);

        Config config = new Config(args[0]);
        HBaseTool tool = new HBaseTool(config);
        tool.clearupDatabase();

        //create 'kanon_crawler_url',{NAME=>'f',VERSIONS=>1,TTL =>604800}
        //create 'kanon_crawler_data',{NAME=>'f',VERSIONS=>1}
    }
}
