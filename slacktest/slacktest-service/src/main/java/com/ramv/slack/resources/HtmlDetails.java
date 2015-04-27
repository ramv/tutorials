package com.ramv.slack.resources;

import com.ramv.slack.config.SlacktestConfiguration;
import com.ramv.slack.util.ParseHtml;
import org.apache.commons.lang3.StringEscapeUtils;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import javax.ws.rs.core.*;

/**
 * Created by ram on 4/23/15.
 */
@Path("/v1/html_details")
@Produces(MediaType.APPLICATION_JSON)
public class HtmlDetails {

    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(HtmlDetails.class);
    private Map<String, String> urlDetailsMap = Collections.synchronizedMap(new WeakHashMap<String, String>());
    private SlacktestConfiguration configuration;
    private static final String URL_KEY = "url";
    private static final String USER_AGENT = "user_agent";
    public HtmlDetails(SlacktestConfiguration configuration){
        this.configuration = configuration;
    }

    @GET
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response details(@Context UriInfo uriInfo, @Context HttpServletRequest request) throws Exception {
        MultivaluedMap<String, String> qp = uriInfo.getQueryParameters(true);
        String url = qp.getFirst(URL_KEY);
        String ua = qp.getFirst(USER_AGENT);
        String json;

        if(!urlDetailsMap.containsKey(url)) {
            try {
                url = URLDecoder.decode(url, "UTF-8");
                //prepend the scheme
                if(!url.startsWith("http://") && !url.startsWith("https://")){
                    url = "http://"+url;
                }
                Document doc = Jsoup.connect(url).get();
                Map<String, ParseHtml.TagDetails> map = ParseHtml.countTags(doc);
                json = ParseHtml.toTagsDetailsJson(doc, map);
                LOGGER.info(String.format("returning data: %s", json));

                urlDetailsMap.put(url, json);
            }catch(HttpStatusException ex){
                LOGGER.error(String.format("Could not load the URL %s. Error: %s", url, ex.getMessage()),ex);
                return Response.status(ex.getStatusCode()).build();
            }
        }else{
            json = urlDetailsMap.get(url);
        }
        return Response.ok(json).build();
    }

}
