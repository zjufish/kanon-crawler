package com.zhaijiong.crawler;

import com.zhaijiong.crawler.utils.Constants;
import com.zhaijiong.crawler.utils.Pair;
import com.zhaijiong.crawler.utils.Utils;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Template {

    /*
     * 起始地址
     */
    private final String seedUrl;

    /*
     * 列表页的url正则表达式
     */
    private final String listPageURLRule;

    /*
     * 正文页的url正则表达式
     */
    private final String postPageUrlRule;

    /*
     * 附加属性
     */
    private Map<String, String> prop;

    /*
     * 存储需要抓取的名称和内容
     */
    private Map<String, String> items;

    public Template(Map conf) {
        this.seedUrl = String.valueOf(conf.get(Constants.SEED_URL));
        this.listPageURLRule = String.valueOf(conf.get(Constants.LIST_PAGE_URL_RULE));
        this.postPageUrlRule = String.valueOf(conf.get(Constants.POST_PAGE_URL_RULE));
        this.prop = Utils.getProperties(conf);
        this.items = (Map<String, String>) conf.get(Constants.KANON_ITEM);
    }

    public String getSeedUrl() {
        return seedUrl;
    }

    public String getPostPageUrlRule() {
        return postPageUrlRule;
    }

    public String getListPageURLRule() {
        return listPageURLRule;
    }

    public List<Pair<String, String>> properties() {
        return Utils.map2List(prop);
    }

    public List<Pair<String, String>> items() {
        return Utils.map2List(items);
    }

    @Override
    public String toString() {
        return "Template{" +
                "seedUrl='" + seedUrl + '\'' +
                ", listPageURLRule='" + listPageURLRule + '\'' +
                ", postPageUrlRule='" + postPageUrlRule + '\'' +
                ", prop=" + prop +
                ", items=" + items +
                '}';
    }
}
