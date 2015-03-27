package com.zhaijiong.crawler.domain;

import org.apache.solr.client.solrj.beans.Field;

public class News {

    /*
     * 文章的唯一标识，由md5(url)生成
     */
    @Field
    private String id;

    /*
     * 文章的网址
     */
    @Field(value="url_s")
    private String url;

    /*
     * 来源
     */
    @Field(value="source_s")
    private String source;

    /*
     * 文章分类
     */
    @Field(value="category_s")
    private String category;

    /*
     * 文章标题
    */
    @Field(value="title_s")
    private String title;

    /*
     * 文章正文内容，如果是网页则content存储网页正文
     */
    @Field(value="content_s")
    private String content;

    /*
     * 发布时间
     */
    @Field(value="date_s")
    private String date;

}
