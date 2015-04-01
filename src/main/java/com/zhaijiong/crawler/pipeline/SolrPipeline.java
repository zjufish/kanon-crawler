package com.zhaijiong.crawler.pipeline;

import com.zhaijiong.crawler.Config;
import com.zhaijiong.crawler.domain.Report;
import com.zhaijiong.crawler.repository.SolrRepository;
import com.zhaijiong.crawler.utils.Constants;
import com.zhaijiong.crawler.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.util.Map;

public class SolrPipeline implements Pipeline {
    private static final Logger LOG = LoggerFactory.getLogger(SolrPipeline.class);

    private Pipeline pipeline;
    private SolrRepository solrRepository;

    public SolrPipeline(Config config,Pipeline pipeline){
        this.pipeline = pipeline;
        solrRepository = new SolrRepository(config.getStr(Constants.KANON_SOLR_ADDRESS));
    }

    public SolrPipeline(Config config){
        this(config,null);
    }

    @Override
    public void process(ResultItems resultItems, Task task) {
        if (pipeline != null) {
            pipeline.process(resultItems, task);
        }
        solrRepository.save(resultItems);
    }
}
