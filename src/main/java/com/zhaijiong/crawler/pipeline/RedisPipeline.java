package com.zhaijiong.crawler.pipeline;

import com.zhaijiong.crawler.Config;
import com.zhaijiong.crawler.repository.RedisRepository;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

public class RedisPipeline implements Pipeline {

    private Config config;
    private Pipeline pipeline;
    private RedisRepository redisRepository;

    public RedisPipeline(Config config, Pipeline pipeline) {
        this.config = config;
        this.pipeline = pipeline;
        redisRepository = new RedisRepository(config);
        redisRepository.init();
    }

    public RedisPipeline(Config config) {
        this(config, null);
    }

    @Override
    public void process(ResultItems resultItems, Task task) {
        if (pipeline != null) {
            pipeline.process(resultItems, task);
        }
        redisRepository.save(resultItems);
    }
}
