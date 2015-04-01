package com.zhaijiong.crawler.processor;

import com.google.common.hash.Hashing;
import com.zhaijiong.crawler.Template;
import com.zhaijiong.crawler.utils.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.List;
import java.util.regex.Pattern;

public class BaseReportProcessor implements PageProcessor {
    private static final Logger LOG = LoggerFactory.getLogger(BaseReportProcessor.class);

    private final Site site;
    private final Template template;

    public BaseReportProcessor(Template template, Site site){
        super();
        this.template = template;
        this.site = site;
    }

    @Override
    public void process(Page page) {
        LOG.info("process page "+ page.getUrl());
        if(page.getUrl().toString().equals(template.getSeedUrl())){
            List<String> links = page.getHtml().links().regex(template.getListPageURLRule()).all();
//            LOG.info("seed:"+page.getUrl()+",links:"+links.size());
            page.addTargetRequests(links);

            links = page.getHtml().links().regex(template.getPostPageUrlRule()).all();
//            LOG.info("list:"+page.getUrl()+",links:"+links.size());
            page.addTargetRequests(links);

        }else if(Pattern.matches(template.getListPageURLRule(),page.getUrl().toString())){
            List<String> links = page.getHtml().links().regex(template.getPostPageUrlRule()).all();
//            LOG.info("list:"+page.getUrl()+",links:"+links.size());
            page.addTargetRequests(links);
        }else{
            LOG.info("post:"+page.getUrl());
            String url = page.getUrl().regex(template.getPostPageUrlRule()).toString();
            page.putField("id",Hashing.md5().hashBytes(page.getUrl().toString().getBytes()).toString());
            page.putField("url", url);
            List<Pair<String, String>> items = template.items();
            for(Pair<String,String> item:items){
                page.putField(item.getKey(), page.getHtml().getDocument().select(item.getVal()).text());
            }
            //添加属性字段，比如内容来源
            List<Pair<String, String>> properties = template.properties();
            for(Pair<String,String> property: properties){
                page.putField(property.getKey(),property.getVal());
            }

        }
    }

    @Override
    public Site getSite() {
        return site;
    }
}
