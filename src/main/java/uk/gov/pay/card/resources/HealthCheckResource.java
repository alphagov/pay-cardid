package uk.gov.pay.card.resources;

import com.codahale.metrics.health.HealthCheck;
import com.google.common.collect.ImmutableMap;
import io.dropwizard.core.setup.Environment;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import java.util.Collection;
import java.util.Map;
import java.util.SortedMap;
import java.util.stream.Collectors;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.OK;
import static jakarta.ws.rs.core.Response.Status.SERVICE_UNAVAILABLE;
import static jakarta.ws.rs.core.Response.status;

@Path("/")
public class HealthCheckResource {

    private final Environment environment;

    public HealthCheckResource(Environment environment) {
        this.environment = environment;
    }

    @GET
    @Path("healthcheck")
    @Produces(APPLICATION_JSON)
    public Response healthCheck() {
        SortedMap<String, HealthCheck.Result> results = environment.healthChecks().runHealthChecks();

        Map<String, Map<String, Boolean>> response = results.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        healthcheckResult -> ImmutableMap.of(
                        "healthy", healthcheckResult.getValue().isHealthy()
                        )
                ));

        Response.Status status = allHealthy(results.values()) ? OK : SERVICE_UNAVAILABLE;

        return status(status).entity(response).build();
    }

    private boolean allHealthy(Collection<HealthCheck.Result> results) {
        return results.stream().allMatch(HealthCheck.Result::isHealthy);
    }
}
