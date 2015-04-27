package com.ramv.slack.test;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ramv.slack.util.ParseHtml;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class ParseHtmlTest {
    @Before
    public void setUp() throws Exception {

    }
    @After
    public void tearDown() throws Exception {

    }
    @Test
    public void testParse() throws Exception{
        String html = "<html><head><title>First parse</title></head>"
                + "<body><p>Parsed HTML into a doc.</p></body></html>";
        Map<String, Element> map  = ParseHtml.parseString(html);
        for(String xpath: map.keySet()){
            Element elem = map.get(xpath);
            assertEquals("The paths are not equal", xpath, elem.cssSelector());
        }
        assertEquals("Did not get the expected length",6, map.size());
    }
    @Test
    public void testTagNames() throws Exception{

        String html = "<html><head><title>First parse</title></head>"
                + "<body><p>Parsed HTML into a doc.</p><mytag></mytag<p>Test this para</p><div><div><div></div></body></html>";
        Document doc = Jsoup.parse(html);
        Map<String, ParseHtml.TagDetails> map = ParseHtml.countTags(doc);
        assertFalse("Map size is 0", map.size()==0);
        for(String tagName: map.keySet()){
            ParseHtml.TagDetails td = map.get(tagName);
            switch (td.getTagName()){
                case "#root":
                    assertEquals("tag count not correct", 1, td.getCount());
                    break;
                case "html":
                    assertEquals("tag count not correct", 1, td.getCount());
                    break;
                case "head":
                    assertEquals("tag count not correct", 1, td.getCount());
                    break;
                case "title":
                    assertEquals("tag count not correct", 1, td.getCount());
                    break;
                case "body":
                    assertEquals("tag count not correct", 1, td.getCount());
                    break;
                case "p":
                    assertEquals("tag count not correct", 2, td.getCount());
                    break;
                case "div":
                    assertEquals("tag count not correct", 3, td.getCount());
                    break;
                case "mytag":
                    assertEquals("tag count not correct", 1, td.getCount());
                    break;
                default:
                    assertFalse("Found an unknown tag: "+td.getTagName(), true);
            }
        }
    }
    @Test
    public void testToJson() throws Exception {

        String html = "<html><head><title>First parse</title></head>"
                + "<body><p arg=\"blah\">Parsed HTML into a doc.</p><mytag></mytag<p>Test this para</p><div><div><div></div></body></html>";
        Document doc = Jsoup.parse(html);
        Map<String, ParseHtml.TagDetails> map = ParseHtml.countTags(doc);
        String actual = ParseHtml.toTagsDetailsJson(doc, map);
        System.out.println(actual);
        String expected = "{\"tags\":[{\"name\":\"html\",\"count\": 1,\"selectors\":[\"html\"]},{\"name\":\"head\",\"count\": 1,\"selectors\":[\"html > head\"]},{\"name\":\"title\",\"count\": 1,\"selectors\":[\"html > head > title\"]},{\"name\":\"body\",\"count\": 1,\"selectors\":[\"html > body\"]},{\"name\":\"p\",\"count\": 2,\"selectors\":[\"html > body > p\",\"html > body > mytag > p\"]},{\"name\":\"mytag\",\"count\": 1,\"selectors\":[\"html > body > mytag\"]},{\"name\":\"div\",\"count\": 3,\"selectors\":[\"html > body > mytag > div\",\"html > body > mytag > div > div\",\"html > body > mytag > div > div > div\"]}],\"html\":\"PGh0bWw+CiA8aGVhZD4KICA8dGl0bGU+Rmlyc3QgcGFyc2U8L3RpdGxlPgogPC9oZWFkPgogPGJvZHk+CiAgPHAgYXJnPSJibGFoIj5QYXJzZWQgSFRNTCBpbnRvIGEgZG9jLjwvcD4KICA8bXl0YWc+CiAgIFRlc3QgdGhpcyBwYXJhCiAgIDxwPjwvcD4KICAgPGRpdj4KICAgIDxkaXY+CiAgICAgPGRpdj48L2Rpdj4KICAgIDwvZGl2PgogICA8L2Rpdj4KICA8L215dGFnPgogPC9ib2R5Pgo8L2h0bWw+\"}";
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(actual);
        assertNotNull(jsonNode);
        assertEquals("did not get the expected JSON", expected, actual);
    }
}