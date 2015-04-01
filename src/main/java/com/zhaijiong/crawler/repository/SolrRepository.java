package com.zhaijiong.crawler.repository;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.ResultItems;

import java.io.IOException;
import java.util.Map;

public class SolrRepository {
    private static final Logger LOG = LoggerFactory.getLogger(SolrRepository.class);

    HttpSolrServer server;

    public SolrRepository(String url) {
        server = new HttpSolrServer(url);
    }

    public void save(ResultItems resultItems) {
        Map<String, Object> items = resultItems.getAll();
        String url = resultItems.getRequest().getUrl();
        if (items.size() == 0) {
            LOG.info(String.format("no items to index,%s", url));
            return;
        }
        try {
            SolrInputDocument document = new SolrInputDocument();
            for (Map.Entry<String, Object> item : items.entrySet()) {
                document.addField(item.getKey(),item.getValue());
            }
            server.add(document);
            server.commit();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SolrServerException e) {
            e.printStackTrace();
        }
    }

}
