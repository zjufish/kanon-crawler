package com.zhaijiong.crawler;

import com.google.common.base.Preconditions;
import com.zhaijiong.crawler.pipeline.HBasePipeline;
import com.zhaijiong.crawler.pipeline.RedisPipeline;
import com.zhaijiong.crawler.processor.BaseReportProcessor;
import com.zhaijiong.crawler.repository.RedisUtils;
import com.zhaijiong.crawler.scheduler.HBaseDuplicateRemover;
import com.zhaijiong.crawler.utils.Constants;
import com.zhaijiong.crawler.utils.DBTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.scheduler.QueueScheduler;

import java.io.IOException;
import java.util.List;

/**
 * Created by eryk on 15-3-25.
 */
public class Crawler {
    private Logger LOG = LoggerFactory.getLogger(Crawler.class);

    private static Config config;

    public void run(String... args) throws Exception {
        config = new Config(args[0]);

        DBTool tool = new DBTool(config);
        tool.clearupDatabase();

        List<Template> templates = config.getTemplates();

        for (Template template : templates) {
            LOG.info(String.format("start crawler %s", template));
            crawler(template);
        }
    }

    private void crawler(Template template) throws IOException {
        Site site = Site.me()
                .setRetryTimes(config.getInt(Constants.KANON_SITE_RETRYTIMES, 3))
                .setSleepTime(config.getInt(Constants.KANON_SITE_SLEEPTIMEMS, 1000));
        Preconditions.checkNotNull(template, "build repost template fail.please check path and file.");

        BaseReportProcessor processor = new BaseReportProcessor(template, site);
        QueueScheduler scheduler = new QueueScheduler();
        HBaseDuplicateRemover duplicatedRemover = new HBaseDuplicateRemover(template, config);
        scheduler.setDuplicateRemover(duplicatedRemover);

        RedisUtils.init(config);
        RedisPipeline redisPipeline = new RedisPipeline(config);
        HBasePipeline hBasePipeline = new HBasePipeline(config, redisPipeline);

        Spider.create(processor)
                .addUrl(template.getSeedUrl())
                .thread(config.getInt(Constants.KANON_SPIDER_THREAD_COUNT, 1))
                .addPipeline(hBasePipeline)
                .setScheduler(scheduler)
                .run();

//            if (repository.commit()) {
//                LOG.info(String.format("index successed,url=%s", template.getSeedUrl()));
//            }
    }

    /**
     * @param args 输入application.properties文件地址
     */
    public static void main(String[] args) throws Exception {
        Crawler crawler = new Crawler();
        crawler.run(args);
    }
}
