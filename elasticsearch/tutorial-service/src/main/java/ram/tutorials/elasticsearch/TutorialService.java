package ram.tutorials.elasticsearch;

import com.bazaarvoice.dropwizard.redirect.RedirectBundle;
import com.bazaarvoice.dropwizard.redirect.UriRedirect;
import io.dropwizard.lifecycle.Managed;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration.Dynamic;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import ram.tutorials.elasticsearch.config.*;


import ram.tutorials.elasticsearch.resources.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.elasticsearch.client.Client;

import java.util.EnumSet;

public class TutorialService extends Application<TutorialConfiguration> implements Managed {
    private static final Logger LOG = LoggerFactory.getLogger(TutorialService.class);
    //private Node esNode;
    private Client esClient;
    private ObjectMapper mapper;
    public static void main(String[] args) throws Exception {
        new TutorialService().run(args);
    }

    private final HibernateBundle<TutorialConfiguration> hibernateBundle = new HibernateBundle<TutorialConfiguration>(
            
            Void.class
        ) {
        @Override
        public DataSourceFactory getDataSourceFactory(TutorialConfiguration configuration) {
            return configuration.getDataSourceFactory();
        }
    };

    @Override
    public String getName() {
        return "tutorial";
    }

    @Override
    public void initialize(Bootstrap<TutorialConfiguration> bootstrap) {
        bootstrap.addBundle(new RedirectBundle(
                new UriRedirect("/favicon.ico", "/assets/app/favicon.ico"),
                new UriRedirect("/",  "/app/"),
                new UriRedirect("/index.html",  "/app/index.html")
        ));
        bootstrap.addBundle(new AssetsBundle("/assets/app/", "/app"));
        bootstrap.addBundle(hibernateBundle);
        mapper = bootstrap.getObjectMapper();
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        //this.esNode = nodeBuilder().client(true).node();
        this.esClient = new TransportClient().addTransportAddress(new InetSocketTransportAddress("localhost", 9300));
    }

    @Override
    public void run(TutorialConfiguration configuration,
                    Environment environment) throws Exception {

        //environment.jersey().setUrlPattern("/tutorial/*");
        
        environment.jersey().register(new SearchResource(configuration,  esClient, mapper));
        environment.jersey().register(new MultiSearchResource(configuration,  esClient, mapper));
        configureCors(environment);
        
    }
    private void configureCors(Environment environment) {
        Dynamic filter = environment.servlets().addFilter("CORS", CrossOriginFilter.class);
        filter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
        filter.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "GET,PUT,POST,DELETE,OPTIONS");
        filter.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
        filter.setInitParameter(CrossOriginFilter.ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, "*");
        filter.setInitParameter("allowedHeaders", "Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin");
        filter.setInitParameter("allowCredentials", "true");
    }
    @Override
    public void stop(){
        esClient.close();
    }
    @Override
    public void start() throws Exception{

    }
}
