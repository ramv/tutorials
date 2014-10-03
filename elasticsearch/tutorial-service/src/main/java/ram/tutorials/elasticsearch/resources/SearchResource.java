package ram.tutorials.elasticsearch.resources;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import ram.tutorials.elasticsearch.config.TutorialConfiguration;
import org.elasticsearch.node.Node;
import org.elasticsearch.client.Client;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Map;

@Path("/api/v1/search")
public class SearchResource {

    private static final Logger LOG = LoggerFactory.getLogger(SearchResource.class);
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

    public SearchResource(TutorialConfiguration configuration, Client esClient, ObjectMapper mapper){
        this.configuration =configuration;
        this.esClient = esClient;
        //this.esNode = esNode;
        this.mapper = mapper;
    }
    private FieldSortBuilder getFilter(String sort){
        FieldSortBuilder fieldSortBuilder = null;
        switch(sort){
            case "oldest_first":
                fieldSortBuilder = new FieldSortBuilder("created_at");
                fieldSortBuilder.order(SortOrder.ASC);
                break;
            case "newest_first":
                fieldSortBuilder = new FieldSortBuilder("updated_at");
                fieldSortBuilder.order(SortOrder.DESC);
                break;
            case "highest_rated":
                fieldSortBuilder = new FieldSortBuilder("likes");
                fieldSortBuilder.order(SortOrder.DESC);
                break;
            case "lowest_rated":
                fieldSortBuilder = new FieldSortBuilder("likes");
                fieldSortBuilder.order(SortOrder.ASC);
                break;
            default:
                fieldSortBuilder = new FieldSortBuilder("updated_at");
                fieldSortBuilder.order(SortOrder.DESC);
        }

        return fieldSortBuilder;
    }
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Timed 
    public Response get(@Context UriInfo uriInfo, @Context HttpServletRequest request) {
        StringBuilder sb = new StringBuilder();
        sb.append("Received parameters:\n");
        MultivaluedMap<String, String> qp = uriInfo.getQueryParameters();
        String sort = qp.getFirst("sort");

        SearchRequestBuilder searchRequestBuilder = esClient.prepareSearch("products")
                .setTypes("product")
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setSize(20);

        if(sort!=null){
            FieldSortBuilder fsb = getFilter(sort);
            searchRequestBuilder.addSort(fsb);
        }

        SearchResponse response = searchRequestBuilder.execute().actionGet();
        SearchHits hits = response.getHits();

        ArrayList<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
        for (SearchHit hit : hits) {
            Map<String,Object> result = hit.getSource();
            list.add(result);
        }
        try {
            ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
            String json = ow.writeValueAsString(list);
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
