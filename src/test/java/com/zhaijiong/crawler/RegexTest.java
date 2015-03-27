package com.zhaijiong.crawler;

import org.junit.Assert;
import org.junit.Test;

import java.util.regex.Pattern;

public class RegexTest {

    @Test
    public void testRegex(){
        Assert.assertTrue(Pattern.matches("http://stock.10jqka.com.cn/tzjh_list/(index_\\d{0,2}.shtml)?","http://stock.10jqka.com.cn/tzjh_list/index_2.shtml"));
    }
}
