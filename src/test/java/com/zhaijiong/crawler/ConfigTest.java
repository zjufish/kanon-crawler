package com.zhaijiong.crawler;

import com.zhaijiong.crawler.utils.Utils;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class ConfigTest {

    @Test
    public void test() throws Exception {
        Config conf = new Config("crawler_test.yaml");
        Assert.assertEquals(3,conf.getTemplates().size());
        Assert.assertEquals("localhost:8983/solr",conf.getValue("kanon.solr.address"));

    }
}