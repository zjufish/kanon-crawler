package com.zhaijiong.crawler;

import com.chenlb.mmseg4j.Dictionary;
import com.chenlb.mmseg4j.analysis.MMSegAnalyzer;
import com.chenlb.mmseg4j.analysis.SimpleAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.CharArraySet;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.IOException;
import java.io.StringReader;

/**
 * Created by eryk on 15-4-5.
 */
public class AnalyzerTest {

    private String keyword = "丰东股份（002530）股权变动，存医疗健康直销巨头借壳预期";

    @Test
    public void test() throws IOException {
//        CharArraySet set = new CharArraySet(10000,false);
//        set.add("丰东股份");
//        set.add("002530");
//        set.add("医疗");
//        System.getProperties().setProperty("mmseg.dic.path","/home/eryk/workspaces/kanon-crawler/src/main/resources/data/stock.dic");
//        Dictionary dic = Dictionary.getInstance("/home/eryk/workspaces/kanon-crawler/src/main/resources/data");
//        Analyzer analyzer = new SimpleAnalyzer(dic); //丰 东 股份 002530 股权 变动 存 医疗 健康 直销 巨头 借 壳 预期
//        Analyzer analyzer = new ComplexAnalyzer();
//        Analyzer analyzer = new SimpleAnalyzer();
//        Analyzer analyzer = new MaxWordAnalyzer();


        Analyzer analyzer = new IKAnalyzer();

        try {
            TokenStream tokenStream = analyzer.tokenStream("content",
                    new StringReader(keyword));
            tokenStream.addAttribute(CharTermAttribute.class);
            tokenStream.reset();//必须先调用reset方法，否则会报下面的错，可以参考TokenStream的API说明
/* java.lang.IllegalStateException: TokenStream contract violation: reset()/close() call missing, reset() called multiple times, or subclass does not call super.reset(). Please see Javadocs of TokenStream class for more information about the correct consuming workflow.*/
            System.out.print("结果：");
            while (tokenStream.incrementToken()) {
                CharTermAttribute charTermAttribute = tokenStream
                        .getAttribute(CharTermAttribute.class);
                System.out.print(charTermAttribute.toString() + " ");
            }
            tokenStream.end();
            tokenStream.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
