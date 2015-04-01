package com.zhaijiong.crawler.utils;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.hash.Hashing;
import com.zhaijiong.crawler.Config;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.zhaijiong.crawler.utils.Constants.*;

public class Utils {
    private static final Logger LOG = LoggerFactory.getLogger(Utils.class);

    private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    public static final Charset UTF8 = Charset.forName("UTF-8");

    // Map --> Bean 1: 利用Introspector,PropertyDescriptor实现 Map --> Bean
    public static void transMap2Bean(Map<String, Object> map, Object obj) {

        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();

            for (PropertyDescriptor property : propertyDescriptors) {
                String key = property.getName();
                if (map.containsKey(key)) {
                    Object value = map.get(key);
                    // 得到property对应的setter方法
                    Method setter = property.getWriteMethod();
                    setter.invoke(obj, (String) value);
                }

            }
        } catch (Exception e) {
            LOG.error("transMap2Bean Error ", e);
        }
        return;
    }

    // Bean --> Map 1: 利用Introspector和PropertyDescriptor 将Bean --> Map
    public static Map<String, Object> transBean2Map(Object obj) {

        if (obj == null) {
            return null;
        }
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor property : propertyDescriptors) {
                String key = property.getName();

                // 过滤class属性
                if (!key.equals("class")) {
                    // 得到property对应的getter方法
                    Method getter = property.getReadMethod();
                    Object value = getter.invoke(obj);

                    map.put(key, value);
                }

            }
        } catch (Exception e) {
            System.out.println("transBean2Map Error " + e);
        }
        return map;
    }

    public static Date get(String time) throws ParseException {
        return format.parse(time);
    }

    public static byte[] withMD5Prefix(byte[] key) {
        return Bytes.add(Bytes.head(Hashing.md5().hashBytes(key).asBytes(), 2), key);
    }

    public static Map readYamlConf(InputStream input) throws IOException {
        InputStreamReader reader = new InputStreamReader(input, UTF8);
        Yaml yaml = new Yaml();
        Map conf = (Map) yaml.load(reader);
        return conf == null ? new HashMap() : conf;
    }

    public static Map readYamlConf(String name, boolean asResource)
            throws IOException {
        InputStream input = null;
        try {
            if (asResource) {
                List<URL> urls = findResources(name);
                if (urls.isEmpty())
                    throw new IOException("Resource `" + name + "' not found");
                else if (urls.size() > 1)
                    throw new IOException("Multiple resources `" + name
                            + "' found");
                else
                    input = urls.get(0).openStream();
            } else
                input = new FileInputStream(name);
            return readYamlConf(input);
        } finally {
            if (input != null)
                input.close();
        }
    }

    // This function is almost the same as Storm's Utils.findResources
    // function; copying it here is for avoiding dependency on Storm.
    public static List<URL> findResources(String name) throws IOException {
        List<URL> urls = new ArrayList<URL>();
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        Enumeration<URL> res = cl.getResources(name);
        while (res.hasMoreElements())
            urls.add(res.nextElement());
        return urls;
    }

    public static Map<String, Object> getConf(String path) throws IOException {
        return readYamlConf(path, true);
    }

    public static Map<String, String> getProperties(Map<String, Object> conf) {
        Map<String, String> properties = Maps.newHashMap();
        for (Map.Entry<String, Object> item : conf.entrySet()) {
            String key = item.getKey();
            if (!SEED_URL.equals(key) &&
                    !LIST_PAGE_URL_RULE.equals(key) &&
                    !POST_PAGE_URL_RULE.equals(key) &&
                    !KANON_ITEM.equals(key)) {
                properties.put(key, String.valueOf(item.getValue()));
            }
        }
        return properties;
    }

    public static List<Pair<String, String>> map2List(Map<String, String> map) {
        List<Pair<String, String>> list = Lists.newArrayList();
        for (Map.Entry<String, String> kv : map.entrySet()) {
            list.add(new Pair(kv.getKey(), kv.getValue()));
        }
        return list;
    }

    public static void printList(List list) {
        System.out.println("size:" + list.size());
        System.out.println("------");
        for (Object obj : list) {
            System.out.println(list);
        }
        System.out.println("------");
    }

    public static void printMap(Map map) {
        System.out.println("size:" + map.size());
        System.out.println("------");
        for (Object obj : map.entrySet()) {
            Map.Entry entry = (Map.Entry) obj;
            System.out.println("{" +
                    "key=" + entry.getKey() +
                    ", val=" + entry.getValue() +
                    '}');
        }
        System.out.println("------");
    }

    public static Configuration getHBaseConf(Map conf) {
        Configuration config = new Configuration();
        config.set(HConstants.ZOOKEEPER_QUORUM, String.valueOf(conf.get(KANON_ZOOKEEPER_QUORUM)));
        config.set(HConstants.ZOOKEEPER_ZNODE_PARENT, String.valueOf(conf.get(KANON_ZOOKEEPER_ZNODE)));
        return config;
    }

    public static String getRedisConf(Config config) {
        return Joiner.on(":").join(config.getStr(KANON_REDIS_ADDRESS), config.getStr(KANON_REDIS_PORT));
    }

    public static Long[] getPageRange(long pageNum, long PageSize) {
        Long[] range = new Long[2];
        range[0] = (pageNum - 1) * PageSize;
        range[1] = pageNum * PageSize - 1;
        return range;
    }

}
