package com.zhaijiong.crawler;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

/**
 * Created by eryk on 15-4-8.
 */
public class DictionaryBuilder {
    private static final Logger LOG = LoggerFactory.getLogger(DictionaryBuilder.class);

    private String tagsURL = "http://quote.eastmoney.com/center/BKList.html#notion_0_0?sortRule=0";
    private String stockURL = "http://quote.eastmoney.com/stocklist.html";
    private Config config;
    Map<String, List<String>> tags = Maps.newHashMap();

    public DictionaryBuilder(Config config) {
        this.config = config;
    }


    Map<String, List<String>> build(String url) throws IOException {
        Connection connect = Jsoup.connect(url);
        Document document = connect.get();
        Elements elements = document.select("li[class=node-sub-sub]");
        for (Element element : elements) {
            String html = new String(element.html().getBytes(Charset.forName("utf8")));
            Elements items = element.select("li span[class=text]");
            List<String> list = Lists.newArrayListWithCapacity(200);
            for (Element item : items) {
                String word = new String(item.text().getBytes(Charset.forName("utf8"))).replaceAll("_","");
                word = word.replaceAll("板块","");
                list.add(word);
            }
            if (html.contains("概念板块")) {
                tags.put("概念板块", list);
            } else if (html.contains("行业板块")) {
                tags.put("行业板块", list);
            } else if (html.contains("地域板块")) {
                tags.put("地域板块", list);
            }
        }
        LOG.info("概念:" + tags.get("概念板块").size() + ",行业:" + tags.get("行业板块").size() + ",地域:" + tags.get("地域板块").size());
        return tags;
    }

    public void getStockList() throws IOException {
        int zxb = 0;
        int sh = 0;
        int sz = 0;
        int cyb = 0;
        int other = 0;

        Document doc = Jsoup.connect(stockURL).get();
        Elements stockList = doc.select("div[id=quotesearch] li a");
        for(Element stock :stockList){
            String url = stock.attr("href");
            if(url.contains("sh600")){
                ++sh;
            }else if(url.contains("sz000")){
                ++sz;
            }else if(url.contains("sz002")){
                ++zxb;
            }else if(url.contains("sz300")){
                ++cyb;
            }else {
                ++other;
            }
            String[] stockArr = stock.text().split("\\(");
            System.out.println(stockArr[0] + "\t" +stockArr[1].replaceAll("\\)",""));
        }
        LOG.info("600:"+sh+",000:"+sz+",002:"+zxb+",300:"+cyb+",other:"+other);
        LOG.info("total:"+(sh+sz+zxb+cyb));
    }

    public void writeFile() throws IOException {
//        File file = new File(config.getStr());
        File file = new File("src/main/resources/stock.dic");
        BufferedWriter writer = Files.newWriter(file, Charset.forName("utf8"));
        Map<String, List<String>> tagMaps = build(tagsURL);
        for (Map.Entry<String, List<String>> tagList : tagMaps.entrySet()) {
            for (String word : tagList.getValue()) {
                writer.write(word);
                writer.newLine();
            }
        }
        writer.flush();
        writer.close();
    }

    public void putToHBase() {

    }

    public static void main(String[] args) throws IOException {
        Config config = new Config("crawler_local.yaml");
        DictionaryBuilder builder = new DictionaryBuilder(config);
//        builder.writeFile();
        builder.getStockList();
    }
}
