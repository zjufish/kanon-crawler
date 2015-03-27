package com.zhaijiong.crawler.scheduler;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import com.zhaijiong.crawler.Config;
import com.zhaijiong.crawler.Template;
import com.zhaijiong.crawler.utils.Constants;
import com.zhaijiong.crawler.utils.Utils;
import org.apache.hadoop.hbase.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.scheduler.component.DuplicateRemover;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

public class HBaseDuplicateRemover implements DuplicateRemover {
    Logger LOG = LoggerFactory.getLogger(HBaseDuplicateRemover.class);
    private Template template;

    private AtomicInteger counter = new AtomicInteger();

    private static String TABLENAME;
    private HConnection conn;

    private int expectedInsertions = 1000000;
    private double fpp = 0.01;
    private BloomFilter<CharSequence> bloomFilter;

    public HBaseDuplicateRemover(Template template, Config config) throws IOException {
        this.template = template;
        conn = HConnectionManager.createConnection(Utils.getHBaseConf(config));
        this.bloomFilter = rebuildBloomFilter();
        TABLENAME = config.getStr(Constants.KANON_CRAWLER_TABLE, Constants.KANON_CRAWLER_TABLE_DEFAULT);
    }

    @Override
    public boolean isDuplicate(Request request, Task task) {
        byte[] row = Utils.withMD5Prefix(request.getUrl().getBytes());
        boolean isDuplicate = true;
        try {
            HTableInterface crawlerDB = conn.getTable(TABLENAME);
            isDuplicate = crawlerDB.exists(new Get(row));
            //如果hbase中不存在
            if (!isDuplicate) {
                //检查是否是post规则的url
                if (Pattern.matches(template.getPostPageUrlRule(), request.getUrl())) {
                    //如果是，则加入hbase，以后不再抓取
                    Put put = new Put(row);
                    put.add(Constants.KANON_HBASE_CF, Constants.KANON_NON_BYTES, Constants.KANON_NON_BYTES);
                    crawlerDB.put(put);
                } else {
                    //如果不是，则判断是是否已经加入到bloomfilter中
                    isDuplicate = bloomFilter.mightContain(request.getUrl());
                    if (!isDuplicate) {
                        //如果bloomfilter不存在，则加入其中
                        bloomFilter.put(request.getUrl());
                    }
                }
                counter.incrementAndGet();
            }
        } catch (IOException e) {
            LOG.error("failed to check request url:"+request.getUrl(),e);
        }
        LOG.info(String.format("%s, check %s, url=%s",counter.get(),isDuplicate,request.getUrl()));
        return isDuplicate;
    }

    @Override
    public void resetDuplicateCheck(Task task) {
        rebuildBloomFilter();
    }

    @Override
    public int getTotalRequestsCount(Task task) {
        return counter.get();
    }

    protected BloomFilter<CharSequence> rebuildBloomFilter() {
        counter = new AtomicInteger(0);
        return BloomFilter.create(Funnels.stringFunnel(Charset.defaultCharset()), expectedInsertions, fpp);
    }
}
