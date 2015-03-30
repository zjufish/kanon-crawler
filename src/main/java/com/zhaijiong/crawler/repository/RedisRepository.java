package com.zhaijiong.crawler.repository;

import com.zhaijiong.crawler.Config;
import com.zhaijiong.crawler.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import us.codecraft.webmagic.ResultItems;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.zhaijiong.crawler.utils.Constants.*;

/**
 * Created by eryk on 15-3-30.
 * xuqi86@qq.com
 */
public class RedisRepository implements Repository {
    private static final Logger LOG = LoggerFactory.getLogger(RedisRepository.class);
    private static Map<String, JedisPool> maps = new HashMap<String, JedisPool>();
    private Config config;
    Jedis redis;
    private String redisConfStr;
    private String tableName;

    public RedisRepository(Config config) {
        this.config = config;
        tableName = config.getStr(KANON_REDIS_CONTENT_TABLE, KANON_REDIS_CONTENT_TABLE_DEFAULT);
    }

    /**
     * 获取连接池.
     *
     * @return 连接池实例
     */
    private JedisPool getPool() {
        redisConfStr = Utils.getRedisConf(config);
        String ip = config.getStr(KANON_REDIS_ADDRESS);
        int port = config.getInt(KANON_REDIS_PORT, 6379);
        JedisPool pool = null;
        if (!maps.containsKey(redisConfStr)) {
            JedisPoolConfig redisConf = new JedisPoolConfig();
            redisConf.setMaxTotal(config.getInt(KANON_REDIS_POOL_MAXSIZE, 8));
            redisConf.setMaxIdle(config.getInt(KANON_REDIS_POOL_MAXIDLE, 8));
            redisConf.setMaxWaitMillis(config.getInt(KANON_REDIS_POOL_MAXWAIT, -1));
            redisConf.setTestOnBorrow(true);
            redisConf.setTestOnReturn(true);
            try {
                /**
                 *如果你遇到 java.net.SocketTimeoutException: Read timed out exception的异常信息
                 *请尝试在构造JedisPool的时候设置自己的超时值. JedisPool默认的超时时间是2秒(单位毫秒)
                 */
                pool = new JedisPool(redisConf, ip, port, config.getInt(KANON_REDIS_TIMEOUT, 2000));
                maps.put(redisConfStr, pool);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            pool = maps.get(redisConfStr);
        }
        return pool;
    }

    @Override
    public void init() {
        redis = getPool().getResource();
        LOG.info(String.format("init redis success. conf=%s", redisConfStr));
    }

    @Override
    public void close() {
        redis.close();
        redis.shutdown();
        LOG.info(String.format("shutdown redis success. conf=%s", redisConfStr));
    }

    public List<String> list(String tableName, int pageNum, int pageSize) {
        long startPos = Utils.getPageRange(pageNum, pageSize)[0];
        long stopPos = Utils.getPageRange(pageNum, pageSize)[1];
        List<String> results = redis.lrange(tableName, startPos, stopPos);
        return results;
    }

    public void save(String tableName, String id) {
        redis.lpush(tableName, id);
    }

    public void save(ResultItems resultItems) {
        Map<String, Object> items = resultItems.getAll();
        String url = resultItems.getRequest().getUrl();
        if(items.size() ==0){
            LOG.info(String.format("no items to insert,%s",url));
            return;
        }
        redis.lpush(tableName, resultItems.getRequest().getUrl());
    }
}
