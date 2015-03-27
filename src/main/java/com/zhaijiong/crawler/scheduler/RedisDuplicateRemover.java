package com.zhaijiong.crawler.scheduler;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import com.zhaijiong.crawler.Template;
import com.zhaijiong.crawler.utils.Constants;
import org.apache.hadoop.conf.Configuration;
import redis.clients.jedis.Jedis;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.scheduler.component.DuplicateRemover;

import java.nio.charset.Charset;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

public class RedisDuplicateRemover implements DuplicateRemover {

    private Jedis jedis;
    private Template template;

    private static final String HASH_PREFIX = "h_";

    private int expectedInsertions = 1000000;
    private double fpp = 0.01;
    private BloomFilter<CharSequence> bloomFilter;

    private AtomicInteger counter;

    public RedisDuplicateRemover(Template template, Configuration config) {
        this.template = template;
        this.bloomFilter = rebuildBloomFilter();
        jedis = new Jedis(config.get("kanon.jedis.address"),
                config.getInt("kanon.jedis.port",6379));
        jedis.connect();
    }

    @Override
    public boolean isDuplicate(Request request, Task task) {
        boolean isDuplicate = jedis.hexists(getHashKey(task), request.getUrl());
        //如果redis中不存在
        if (!isDuplicate) {
            //检查是否是post规则的url
            if (Pattern.matches(template.getPostPageUrlRule(), request.getUrl())) {
                //如果是，则加入redis，以后不再抓取
                jedis.hset(getHashKey(task), request.getUrl(), "");
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
        return isDuplicate;

    }

    @Override
    public void resetDuplicateCheck(Task task) {
        rebuildBloomFilter();
    }

    protected String getHashKey(Task task) {
        return Constants.PROJECT + HASH_PREFIX + task.getSite().getDomain();
    }

    @Override
    public int getTotalRequestsCount(Task task) {
        Long size = jedis.hlen(getHashKey(task));
        return size.intValue();
    }

    protected BloomFilter<CharSequence> rebuildBloomFilter() {
        counter = new AtomicInteger(0);
        return BloomFilter.create(Funnels.stringFunnel(Charset.defaultCharset()), expectedInsertions, fpp);
    }

    public void close() {
        jedis.disconnect();
    }
}
