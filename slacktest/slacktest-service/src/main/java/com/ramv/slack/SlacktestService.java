package com.ramv.slack;

import com.bazaarvoice.dropwizard.redirect.RedirectBundle;
import com.bazaarvoice.dropwizard.redirect.UriRedirect;
import com.ramv.slack.config.*;



import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.base.Optional;
import com.ramv.slack.resources.HtmlDetails;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import java.io.IOException;
import java.util.EnumSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SlacktestService extends Application<SlacktestConfiguration> {
    private static final Logger LOG = LoggerFactory.getLogger(SlacktestService.class);

    public static void main(String[] args) throws Exception {
        new SlacktestService().run(args);
    }

    private final HibernateBundle<SlacktestConfiguration> hibernateBundle = new HibernateBundle<SlacktestConfiguration>(
            
            Void.class
        ) {
        @Override
        public DataSourceFactory getDataSourceFactory(SlacktestConfiguration configuration) {
            return configuration.getDataSourceFactory();
        }
    };

    @Override
    public String getName() {
        return "slacktest";
    }

    @Override
    public void initialize(Bootstrap<SlacktestConfiguration> bootstrap) {

        bootstrap.addBundle(new AssetsBundle());
        //bootstrap.addBundle(hibernateBundle);
        //ObjectMapper mapper = bootstrap.getObjectMapper();
        // mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    public void run(SlacktestConfiguration configuration,
                    Environment environment) throws Exception {

        //register the first route
        environment.jersey().register(new HtmlDetails(configuration));

        configureCors(environment);
        
    }
    private void configureCors(Environment environment) {
        FilterRegistration.Dynamic filter = environment.servlets().addFilter("CORS", CrossOriginFilter.class);
        filter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
        filter.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "GET,PUT,POST,DELETE,OPTIONS");
        filter.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
        filter.setInitParameter(CrossOriginFilter.ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, "*");
        filter.setInitParameter("allowedHeaders", "Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin");
        filter.setInitParameter("allowCredentials", "true");
    }
}
