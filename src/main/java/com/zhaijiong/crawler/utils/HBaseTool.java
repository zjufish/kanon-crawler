package com.zhaijiong.crawler.utils;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTablePool;
import org.apache.hadoop.hbase.client.Result;

import java.io.IOException;

public class HBaseTool {
    private static Configuration config;
    private static HTablePool pool;

    static{
        config = new Configuration();
        config.set(HConstants.ZOOKEEPER_QUORUM,"127.0.0.1:2181");
        config.set(HConstants.ZOOKEEPER_ZNODE_PARENT,"/hbase");
        pool = new HTablePool(config,10);
    }

    public static void printRow(String tableName,String key) throws IOException {
        byte[] rowBytes = key.getBytes();
        Get get = new Get(Utils.withMD5Prefix(rowBytes));
        Result result = pool.getTable(tableName).get(get);
        System.out.println(result.toString());
    }

    public static void main(String[] args) throws IOException {
        String table = Constants.KANON_CRAWLER_TABLE_DEFAULT;
        String url = "http://stock.stockstar.com/JC2014091100001864.shtml";
        HBaseTool.printRow(table,url);
    }
}
