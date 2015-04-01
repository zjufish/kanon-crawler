package com.zhaijiong.crawler.utils;

public class Constants {
    public static final String PROJECT = "kanon_";

    public static final String SEED_URL = "seed.url";
    public static final String POST_PAGE_URL_RULE = "post.url.rule";
    public static final String LIST_PAGE_URL_RULE = "list.url.rule";
    public static final String KANON_TEMPLATE = "templates";
    public static final String KANON_ITEM = "items";

    /**
     * webmagic constants
     */
    public static final String KANON_SITE_RETRYTIMES = "kanon.site.retrytimes";

    public static final String KANON_SITE_SLEEPTIMEMS = "kanon.site.sleeptimeMS";

    public static final String KANON_SPIDER_THREAD_COUNT = "kanon.spider.thread.count";

    /**
     * crawler url list table
     */
    public static final String KANON_CRAWLER_URL_TABLE = "kanon.crawler.url.table";

    public static final String KANON_CRAWLER_URL_TABLE_DEFAULT = "kanon_crawler_url";

    public static final String KANON_CRAWLER_DATA_TABLE = "kanon.crawler.data.table";

    public static final String KANON_CRAWLER_DATA_TABLE_DEFAULT = "kanon_crawler_data";

    /**
     * hbase constants
     */
    public static final String KANON_ZOOKEEPER_QUORUM = "kanon.zookeeper.quorum";

    public static final String KANON_ZOOKEEPER_ZNODE = "kanon.zookeeper.znode";

    public static final byte[] KANON_HBASE_CF = "f".getBytes();

    public static final byte[] KANON_NON_BYTES = "".getBytes();

    /**
     * redis constants
     */
    public static final String KANON_REDIS_ADDRESS = "kanon.redis.address";

    public static final String KANON_REDIS_PORT = "kanon.redis.port";

    // 按新旧爬去的整体内容列表，使用queue存储文章ID
    public static final String KANON_REDIS_CONTENT_TABLE = "kanon.redis.content";

    public static final String KANON_REDIS_CONTENT_TABLE_DEFAULT = "kanon_redis_content";

    public static final String KANON_REDIS_QUEUE_LENGTH = "kanon.redis.queue.length";

    public static final int KANON_REDIS_QUEUE_LENGTH_DEFAULT = 100;

    public static final String KANON_REDIS_POOL_MAXSIZE = "kanon.redis.pool.maxsize";

    public static final String KANON_REDIS_POOL_MAXIDLE = "kanon.redis.pool.maxidle";

    public static final String KANON_REDIS_POOL_MAXWAIT = "kanon.redis.pool.maxwait";

    public static final String KANON_REDIS_TIMEOUT = "kanon.redis.timeout";

    /**
     * solr constants
     */
    public static final String KANON_SOLR_ADDRESS = "kanon.solr.address";

}
