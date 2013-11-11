package net.sf.gazpachosurvey.rest.auth;

import java.util.HashMap;
import java.util.Map;

import net.sf.gazpachosurvey.rest.ApplicationConfig;
import net.sf.gazpachosurvey.rest.SurveysResource;
import net.sf.gazpachosurvey.security.LoginService;

import org.glassfish.jersey.client.filter.HttpBasicAuthFilter;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.glassfish.jersey.server.spring.scope.RequestContextFilter;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.Test;

public class SurveysResourceTest extends JerseyTest {

    @Override
    protected ResourceConfig configure() {
        enable(TestProperties.LOG_TRAFFIC);
        enable(TestProperties.DUMP_ENTITY);
        ResourceConfig config = new ApplicationConfig();
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("contextConfigLocation", "root-test-context.xml");
        config.setProperties(properties);
        return config;
    }

    @Test
    public void testHelloWorld() {
        // String output = super.target("/surveys").request().get(String.class);
        // super.client();
        client().register(
                new HttpBasicAuthFilter(LoginService.RESPONDENT_USER_NAME,
                        "7FNC3XT19I"));
        // client.addFilter(new LoggingFilter());

        System.out.println(getBaseUri());

        String output = client().target(getBaseUri() + "surveys").request()
                .get(String.class);
        System.out.println(output);
    }
}
