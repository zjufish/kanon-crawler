package com.zhaijiong.crawler.domain;

import com.zhaijiong.crawler.Template;
import com.zhaijiong.crawler.utils.Utils;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Map;

public class TemplateTest {

    @Test
    public void test() throws Exception {
        Map conf = Utils.getConf("crawler_test.yaml");
        List<Map> lists = (List<Map>) conf.get("templates");
        Template template = new Template(lists.get(0));
        Assert.assertEquals("http://stock.10jqka.com.cn/tzjh_list/", template.getSeedUrl());
        Assert.assertEquals("http://stock.10jqka.com.cn/tzjh_list/(index_\\d{0,2}.html)?", template.getListPageURLRule());
        Assert.assertEquals("http://stock.10jqka.com.cn/\\d{8}/.*.shtml", template.getPostPageUrlRule());
        Assert.assertEquals(2, template.properties().size());
        Assert.assertEquals("source",template.properties().get(0).getKey());
        Assert.assertEquals("同花顺-投资机会", template.properties().get(0).getVal());
        Utils.printList(template.properties());
        Assert.assertEquals(3,template.items().size());
        Utils.printList(template.items());
        Utils.printMap(lists.get(0));
    }
}