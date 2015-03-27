package com.zhaijiong.crawler;

import com.google.common.collect.Lists;
import com.zhaijiong.crawler.utils.Constants;
import com.zhaijiong.crawler.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Config extends HashMap {

    public Config(String path) throws IOException {
        super(Utils.getConf(path));
    }

    public List<Template> getTemplates() {
        List<Map> lists = (List<Map>) get(Constants.KANON_TEMPLATE);
        List<Template> templates = Lists.newArrayList();
        for (Map conf : lists) {
            templates.add(new Template(conf));
        }
        return templates;
    }

    public Integer getInt(String key,int defaultValue){
        Object obj = get(key);
        if(obj !=null){
            return Integer.parseInt(String.valueOf(obj));
        }else{
            return defaultValue;
        }
    }

    public String getStr(String key,String defaultValue){
        Object obj = get(key);
        if(obj !=null){
            return String.valueOf(String.valueOf(obj));
        }else{
            return defaultValue;
        }
    }

    public String getValue(String key){
        return String.valueOf(get(key));
    }
}
