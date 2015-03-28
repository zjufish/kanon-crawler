package com.zhaijiong.crawler.repository;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.UpdateResponse;

import java.io.IOException;

public class SolrRepository<T> {

    HttpSolrServer server;

    public SolrRepository(String url){
        server  = new HttpSolrServer(url);
    }

    public boolean save(T obj){
        try {
            UpdateResponse updateResponse = server.addBean(obj);
            return updateResponse.getStatus()==0;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SolrServerException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean commit(){
        try {
            UpdateResponse commit = server.commit();
            return commit.getStatus()==0;
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
