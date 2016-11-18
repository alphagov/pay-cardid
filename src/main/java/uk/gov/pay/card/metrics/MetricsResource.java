package uk.gov.pay.card.metrics;

import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.SharedMetricRegistries;
import com.codahale.metrics.json.MetricsModule;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import io.dropwizard.setup.Environment;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.StringWriter;
import java.util.concurrent.TimeUnit;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/")
public class MetricsResource {
    private ObjectMapper mapper;
    private Environment environment;

    public MetricsResource(Environment environment) {

        this.mapper = new ObjectMapper().registerModule(new MetricsModule(TimeUnit.SECONDS,
                TimeUnit.SECONDS,
                true,
                MetricFilter.ALL));
        this.environment = environment;
    }

    @GET
    @Path("metrics")
    @Produces(APPLICATION_JSON)
    public Response metrics() throws IOException {
        // Merge cardid registry
        return Response.ok().entity(mapper.writeValueAsString(environment.metrics())).build();
    }

}
