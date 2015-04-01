package com.zhaijiong.crawler.repository;

import com.zhaijiong.crawler.Config;
import com.zhaijiong.crawler.utils.Utils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.zhaijiong.crawler.utils.Constants.*;
import static com.zhaijiong.crawler.utils.Constants.KANON_REDIS_POOL_MAXWAIT;
import static com.zhaijiong.crawler.utils.Constants.KANON_REDIS_TIMEOUT;

/**
 * Created by eryk on 15-4-1.
 */
public class RedisUtils {
    private static JedisPool pools;
    private static Object obj = new Object();

    /**
     * 获取连接池.
     *
     * @return 连接池实例
     */
    public static Jedis getRedis() {
        return pools.getResource();
    }

    public static void init(Config config){
        if (pools == null) {
            pools = createPool(config);
        }
    }

    public static void returnRedis(Jedis jedis) {
        pools.returnResource(jedis);
    }

    private static JedisPool createPool(Config config) {
        String ip = config.getStr(KANON_REDIS_ADDRESS);
        int port = config.getInt(KANON_REDIS_PORT, 6379);
        JedisPool pool = null;
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pool;
    }
}
