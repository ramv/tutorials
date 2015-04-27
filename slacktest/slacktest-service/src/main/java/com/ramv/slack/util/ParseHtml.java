package com.ramv.slack.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.NodeVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jsoup.*;

import java.util.*;


/**
 * Created by ram on 4/23/15.
 */
public class ParseHtml {
    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(ParseHtml.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();
    public static final Map<String, Element> parseUrl(String url) throws Exception{
        Document document = Jsoup.connect(url).get();
        return parse(document);
    }
    public static final Map<String, Element> parseString(String html) throws Exception{
        Document document = Jsoup.parse(html);
        return parse(document);
    }
    public static final class TagDetails{

        private String tagName="";
        private  int count=0;
        private List<String> list = new ArrayList<>();

        public String getTagName() {
            return tagName;
        }

        public void setTagName(String tagName) {
            this.tagName = tagName;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public List<String> getList() {
            return list;
        }

        public void setList(List<String> list) {
            this.list = list;
        }


    }

    public static final Map<String, Element> parse(Document doc){
        final Map<String, Element> map = new LinkedHashMap<String, Element>();
        doc.traverse(new NodeVisitor() {

            public void head(Node node, int depth) {
                System.out.println("Entering tag: " + node.nodeName());
                if (node instanceof Element) {
                    Element element = (Element) node;
                    System.out.println(" The CSS selector is: " + element.cssSelector());
                    map.put(element.cssSelector(), element);
                }
            }

            public void tail(Node node, int depth) {
                //System.out.println("Exiting tag: " + node.nodeName());
            }
        });
        return map;
    }
    public static final Map<String, TagDetails> countTags(Document doc){
        final Map<String, TagDetails> map = new LinkedHashMap<String, TagDetails>();
        doc.traverse(new NodeVisitor() {

            public void head(Node node, int depth) {
                System.out.println("Entering tag: " + node.nodeName());
                if (node instanceof Element) {
                    Element element = (Element) node;
                    String tagName = element.tagName();
                    if(tagName.equals("#root")){
                        return;
                    }
                    TagDetails details = map.get(tagName);
                    if(details == null){
                        details = new TagDetails();

                    }
                    details.count++;
                    details.tagName = tagName;
                    details.list.add(element.cssSelector());
                    map.put(tagName, details);
                }
            }

            public void tail(Node node, int depth) {
                //System.out.println("Exiting tag: " + node.nodeName());
            }
        });
        return map;
    }

    public static final String toTagsDetailsJson(Document doc, Map<String, TagDetails> detailsMap) throws Exception{
        StringBuilder builder = new StringBuilder();
        builder.append("{\"tags\":[");
        boolean appendComa = false;
        for(String tagName: detailsMap.keySet()) {
            if(appendComa){
                builder.append(",");
            }
            TagDetails details = detailsMap.get(tagName);
            builder.append("{\"name\":\""+tagName+"\",");
            builder.append(String.format("\"count\": %d,", details.getCount()));
            builder.append("\"selectors\":[" );
            List<String> list = details.getList();
            int i=0;
            for(String xpath : list){
                if(i>0){
                    builder.append(",");
                }
                builder.append(String.format("\"%s\"", xpath));
                i++;
            }
            builder.append("]}");
            appendComa = true;
        }
        builder.append("],\"html\":\"");
        String out = doc.outerHtml();
        out = Base64Coder.encodeString(out);
        builder.append(out);
        builder.append("\"");
        builder.append("}");
        return builder.toString();
    }
}
