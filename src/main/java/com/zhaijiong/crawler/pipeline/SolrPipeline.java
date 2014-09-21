package com.zhaijiong.crawler.pipeline;

import com.zhaijiong.crawler.dao.Report;
import com.zhaijiong.crawler.repository.Repository;
import com.zhaijiong.crawler.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.util.Map;

public class SolrPipeline implements Pipeline {
    private static final Logger LOG = LoggerFactory.getLogger(SolrPipeline.class);

    private Repository repository;

    public SolrPipeline(Repository repository){
        this.repository = repository;
    }

    @Override
    public void process(ResultItems resultItems, Task task) {
        Map<String, Object> all = resultItems.getAll();
        try {
            Report report = new Report();
            Utils.transMap2Bean(all, report);
            if(report.getUrl()!=null&&report.getUrl().length()!=0){
                LOG.info(String.format("fetch url:%s",report.getUrl()));
                if(!repository.save(report)){
                    LOG.error(String.format("failed.fetch url:%s",report.getUrl()));
                }
            }
        } catch (Exception e) {
            LOG.error("process failed.",e);
        }
    }
}
