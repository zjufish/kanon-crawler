package com.zhaijiong.crawler.utils;

public class Constants {
    public static final String PROJECT = "kanon_";

    public static final String SOLR_HOST= "solr.host";

    public static final String SEED_URL = "seed.url";
    public static final String POST_PAGE_URL_RULE = "post.url.rule";
    public static final String LIST_PAGE_URL_RULE = "list.url.rule";
    public static final String KANON_TEMPLATE = "templates";
    public static final String KANON_ITEM = "items";

    public static final String CRAWLER_TEMPLATE_SUFFIX = "properties";

    public static final String KANON_TEMPLATE_PATH = "kanon.template.path";

    public static final String KANON_SITE_RETRYTIMES = "kanon.site.retrytimes";

    public static final String KANON_SITE_SLEEPTIMEMS = "kanon.site.sleeptimeMS";

    public static final String KANON_SPIDER_THREAD_COUNT = "kanon.spider.thread.count";

    /**
     * crawler url list table
     */
    public static final String KANON_CRAWLER_TABLE = "kanon.crawler.url.table";

    public static final String KANON_CRAWLER_TABLE_DEFAULT = "kanon_crawler_url";

    public static final String KANON_CRAWLER_DATA_TABLE = "kanon.crawler.data.table";

    public static final String KANON_CRAWLER_DATA_TABLE_DEFAULT = "kanon_crawler_data";

    public static final String KANON_ZOOKEEPER_QUORUM = "kanon.zookeeper.quorum";

    public static final String KANON_ZOOKEEPER_ZNODE = "kanon.zookeeper.znode";

    public static final byte[] KANON_HBASE_CF = "f".getBytes();
    public static final byte[] KANON_NON_BYTES = "".getBytes();

}
