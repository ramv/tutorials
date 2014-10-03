package ram.tutorials.elasticsearch.resources;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.node.Node;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ram.tutorials.elasticsearch.config.TutorialConfiguration;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Path("/api/v1/multi-search")
public class MultiSearchResource {

    private static final Logger LOG = LoggerFactory.getLogger(MultiSearchResource.class);
    private TutorialConfiguration configuration;
    //private Node esNode;
    private Client esClient;
    private ObjectMapper mapper;

    static String getStackTrace(Throwable t){
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        return sw.toString();
    }

    public MultiSearchResource(TutorialConfiguration configuration, Client esClient, ObjectMapper mapper){
        this.configuration =configuration;
        this.esClient = esClient;
        //this.esNode = esNode;
        this.mapper = mapper;
    }
    private  HashMap<String,Object> getResultsMap(String index, String kws){

        ArrayList<Map<String,Object>> list = getResultsList(index, kws);
        HashMap<String,Object> map = new HashMap<>();
        map.put("index", index);
        map.put("results", list);
        return map;
    }
    private  ArrayList<Map<String, Object>> getResultsList(String index, String kws){
        SearchRequestBuilder searchRequestBuilder = esClient.prepareSearch(index)
                //.setTypes("product", "collection", "user")
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(QueryBuilders.matchQuery("_all", kws).operator(MatchQueryBuilder.Operator.AND))
                .setHighlighterRequireFieldMatch(true)
                .setSize(5);
        if(index.equals("collections")){
            FilterBuilder filterBuilder = FilterBuilders.rangeFilter("count").gt(1);
            searchRequestBuilder.setPostFilter(filterBuilder);
        }
        SearchResponse response = searchRequestBuilder.execute().actionGet();
        SearchHits hits = response.getHits();

        ArrayList<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
        for (SearchHit hit : hits) {
            Map<String,Object> result = hit.getSource();
            if(!result.containsKey("_id")){
                result.put("_id", hit.getId());
            }
            if(index.equals("users")){
                ArrayList<String> pics = (ArrayList<String>)result.get("pics");
                if(pics.size() == 0){
                    pics.add("http://www.gravatar.com/avatar/00000000");
                }

            }
            list.add(result);
        }
        return list;
    }
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Timed 
    public Response get(@Context UriInfo uriInfo, @Context HttpServletRequest request) {
        StringBuilder sb = new StringBuilder();
        sb.append("Received parameters:\n");
        MultivaluedMap<String, String> qp = uriInfo.getQueryParameters();
        String kws = qp.getFirst("q");
        /*
        ArrayList<HashMap<String, Object>> list = new ArrayList<>();

        //fetch from index products
        list.add(getResultsMap("products", kws));
        list.add(getResultsMap("collections", kws));
        list.add( getResultsMap("users", kws));
        list.add(getResultsMap("coupons", kws));
        */

        HashMap<String, ArrayList<Map<String, Object>>> map = new HashMap<>();
        map.put("products", getResultsList("products", kws));
        map.put("users", getResultsList("users", kws));
        map.put("collections", getResultsList("collections", kws));
        map.put("coupons", getResultsList("coupons", kws));
        try {
            ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
            //String json = ow.writeValueAsString(list);
            String json = ow.writeValueAsString(map);
            return Response.ok(json, MediaType.APPLICATION_JSON_TYPE).build();
        }catch(JsonProcessingException ex){
            //log the error
           LOG.error(String.format("Could not process the returned doc: %s. %s", ex.getMessage(), getStackTrace(ex)));
        }
        return Response.serverError().build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Timed 
    public Response post(@Context UriInfo uriInfo, @Context HttpServletRequest request) {
        StringBuilder sb = new StringBuilder();
        sb.append("Received parameters:\n");
        return Response.ok().build();
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Timed 
    public Response put(@Context UriInfo uriInfo, @Context HttpServletRequest request ) {
        StringBuilder sb = new StringBuilder();
        sb.append("Received parameters:\n");
        return Response.ok().build();
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Timed
    public Response delete(@Context UriInfo uriInfo, @Context HttpServletRequest request) {
        StringBuilder sb = new StringBuilder();
        sb.append("Received parameters:\n");
        return Response.ok().build();
    }

}
