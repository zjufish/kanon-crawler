package com.zhaijiong.crawler.repository;

import com.zhaijiong.crawler.Config;
import com.zhaijiong.crawler.utils.Constants;
import com.zhaijiong.crawler.utils.Utils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.ResultItems;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class HBaseRepostiory implements Repository {
    private static final Logger LOG = LoggerFactory.getLogger(HBaseRepostiory.class);

    private Config config;
    private HConnection conn;
    private String tableName;
    private byte[] COLUMN_FAMILY = "f".getBytes();

    public HBaseRepostiory(Config config){
        this.config = config;
        tableName = config.getStr(Constants.KANON_CRAWLER_DATA_TABLE,Constants.KANON_CRAWLER_DATA_TABLE_DEFAULT);
    }

    @Override
    public void init() {
        Configuration conf = new Configuration();
        conf.set(HConstants.ZOOKEEPER_QUORUM,config.getValue(Constants.KANON_ZOOKEEPER_QUORUM));
        conf.set(HConstants.ZOOKEEPER_ZNODE_PARENT,config.getValue(Constants.KANON_ZOOKEEPER_ZNODE));
        try {
            conn = HConnectionManager.createConnection(conf);
        } catch (ZooKeeperConnectionException e) {
            LOG.error("failed to create");
        }
    }

    @Override
    public void close() {
        try {
            conn.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save(String tableName,Put put) throws IOException {
        HTableInterface table = getTable(tableName);
        table.put(put);
        table.close();
    }

    public void save(ResultItems resultItems) throws IOException {
        Map<String, Object> items = resultItems.getAll();
        String url = resultItems.getRequest().getUrl();
        if(items.size() ==0){
            LOG.info(String.format("no columns to insert,%s",url));
            return;
        }

        byte[] row = Utils.withMD5Prefix(url.getBytes());
        HTableInterface table = getTable(tableName);
        Put put = new Put(row);
        for(Map.Entry<String,Object> item:items.entrySet()){
            put.add(COLUMN_FAMILY, Bytes.toBytes(item.getKey()),Bytes.toBytes(String.valueOf(item.getValue())));
        }
        table.put(put);
        table.close();
    }

    private HTableInterface getTable(String tableName) throws IOException {
        return conn.getTable(tableName);
    }

    public void save(String tableName ,List<Put> puts) throws IOException {
        HTableInterface table = getTable(tableName);
        table.put(puts);
    }

}
