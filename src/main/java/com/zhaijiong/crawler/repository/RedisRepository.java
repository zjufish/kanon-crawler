package com.zhaijiong.crawler.repository;

import com.zhaijiong.crawler.Config;
import com.zhaijiong.crawler.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import us.codecraft.webmagic.ResultItems;

import java.util.List;
import java.util.Map;

import static com.zhaijiong.crawler.utils.Constants.*;

/**
 * Created by eryk on 15-3-30.
 * xuqi86@qq.com
 */
public class RedisRepository implements Repository {
    private static final Logger LOG = LoggerFactory.getLogger(RedisRepository.class);
    private Config config;
    private String tableName;
    private int maxQueueLength;
    private String ip;
    private int port;

    public RedisRepository(Config config) {
        this.config = config;
        tableName = config.getStr(KANON_REDIS_CONTENT_TABLE, KANON_REDIS_CONTENT_TABLE_DEFAULT);
        maxQueueLength = config.getInt(KANON_REDIS_QUEUE_LENGTH,KANON_REDIS_QUEUE_LENGTH_DEFAULT);
    }

    @Override
    public void init() {
        LOG.info(String.format("init redis success. conf=%s", Utils.getRedisConf(config)));
    }

    @Override
    public void close() {
        LOG.info(String.format("shutdown redis success. conf=%s", Utils.getRedisConf(config)));
    }

    public List<String> list(String tableName, int pageNum, int pageSize) {
        Jedis redis = RedisUtils.getRedis();
        long startPos = Utils.getPageRange(pageNum, pageSize)[0];
        long stopPos = Utils.getPageRange(pageNum, pageSize)[1];
        List<String> results = redis.lrange(tableName, startPos, stopPos);
        RedisUtils.returnRedis(redis);
        return results;
    }


    public void save(String tableName, String id) {
        Jedis redis = RedisUtils.getRedis();
        redis.lpush(tableName, id);
        RedisUtils.returnRedis(redis);
    }

    public void save(ResultItems resultItems) {
        Map<String, Object> items = resultItems.getAll();
        String url = resultItems.getRequest().getUrl();
        if(items.size() ==0){
            LOG.info(String.format("no items to insert,%s",url));
            return;
        }
        Jedis redis = RedisUtils.getRedis();
        //可能会有性能问题，日后改成cron程序定时检查queue长度
        int queueLength = redis.llen(tableName).intValue();
        while(queueLength>maxQueueLength){
            redis.rpop(tableName);
            queueLength = redis.llen(tableName).intValue();
        }
        redis.lpush(tableName, resultItems.getRequest().getUrl());
        RedisUtils.returnRedis(redis);
    }
}
