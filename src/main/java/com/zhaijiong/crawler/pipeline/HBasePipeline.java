package com.zhaijiong.crawler.pipeline;

import com.zhaijiong.crawler.Config;
import com.zhaijiong.crawler.repository.HBaseRepostiory;
import com.zhaijiong.crawler.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.io.IOException;

public class HBasePipeline  implements Pipeline {
    private Logger LOG = LoggerFactory.getLogger(HBasePipeline.class);

    Pipeline pipeline;
    HBaseRepostiory repostiory;

    public HBasePipeline(Config config,Pipeline pipeline){
        this.pipeline = pipeline;
        try {
            repostiory = new HBaseRepostiory(config);
        } catch (IOException e) {
            LOG.error("failed to create hbase repostiory",e);
        }
    }

    public HBasePipeline(Config config){
        this(config,null);
    }

    @Override
    public void process(ResultItems resultItems, Task task) {
        if(pipeline !=null){
            pipeline.process(resultItems,task);
        }
        try {
            repostiory.save(resultItems);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
