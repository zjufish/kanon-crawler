package com.zhaijiong.crawler;

import com.google.common.base.Preconditions;
import com.zhaijiong.crawler.dao.ReportTemplate;
import com.zhaijiong.crawler.pipeline.SolrPipeline;
import com.zhaijiong.crawler.processor.BaseReportProcessor;
import com.zhaijiong.crawler.repository.Repository;
import com.zhaijiong.crawler.scheduler.RedisDuplicateRemover;
import com.zhaijiong.crawler.utils.Constants;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.scheduler.QueueScheduler;

import java.io.File;
import java.io.FilenameFilter;

public class CrawlerApplication{
    private Logger LOG = LoggerFactory.getLogger(CrawlerApplication.class);

    private static PropertiesConfiguration config;

    private Repository repository;

    public void run(String... args) throws Exception {
        config = new PropertiesConfiguration(args[0]);

        File[] files = listCrawlerTemplate(config.getString(Constants.KANON_TEMPLATE_PATH));
        Preconditions.checkNotNull(files, String.format("can't find any file in folder %s", config.getString(Constants.KANON_TEMPLATE_PATH)));

        repository = new Repository(config.getString(Constants.SOLR_HOST));

        for (File file : files) {
            LOG.info(String.format("start crawler %s",file.getName()));
            crawlerWithTemplate(file);
        }
    }

    private void crawlerWithTemplate(File file) {
        Site site = Site.me()
                .setRetryTimes(config.getInt(Constants.KANON_SITE_RETRYTIMES))
                .setSleepTime(config.getInt(Constants.KANON_SITE_SLEEPTIMEMS));
        ReportTemplate template = ReportTemplate.build(file.getAbsolutePath());
        Preconditions.checkNotNull(template, "build repost template fail.please check path and file.");

        BaseReportProcessor processor = new BaseReportProcessor(template, site);
        QueueScheduler scheduler = new QueueScheduler();
        scheduler.setDuplicateRemover(new RedisDuplicateRemover(template, config));

        SolrPipeline solrPipeline = new SolrPipeline(repository);

        Spider.create(processor)
                .addUrl(template.getSeedUrl())
                .thread(config.getInt(Constants.KANON_SPIDER_THREAD_COUNT))
                .addPipeline(solrPipeline)
                .setScheduler(scheduler)
                .run();

        if(repository.commit()){
           LOG.info(String.format("index successed,url=%s",template.getSeedUrl()));
        }
    }

    private File[] listCrawlerTemplate(String path) {
        return new File(path).listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                if (name.endsWith(Constants.CRAWLER_TEMPLATE_SUFFIX)) {
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * @param args 输入application.properties文件地址
     */
    public static void main(String[] args) throws Exception {
        CrawlerApplication application = new CrawlerApplication();
        application.run(args);
    }
}
