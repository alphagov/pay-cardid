package uk.gov.pay.card.metrics;

import com.codahale.metrics.*;
import com.codahale.metrics.json.MetricsModule;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.common.collect.Maps;
import io.dropwizard.setup.Environment;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/")
public class MetricsResource {
    private ObjectMapper mapper;
    private Environment environment;
    private MetricRegistry reportingRegistry;

    public MetricsResource(Environment environment) {

        this.mapper = new ObjectMapper().registerModule(new MetricsModule(TimeUnit.SECONDS,
                TimeUnit.SECONDS,
                true,
                MetricFilter.ALL));
        this.environment = environment;
        this.reportingRegistry = new MetricRegistry();
    }

    @GET
    @Path("metrics-app")
    @Produces(APPLICATION_JSON)
    public Response metrics() throws IOException {
        MetricSet metricsSet = new MetricSet() {
            @Override
            public Map<String, Metric> getMetrics() {
                Map<String, Metric> dropwizardMetrics = environment.metrics().getMetrics();
                Map<String, Metric> applicationMetrics = SharedMetricRegistries.getOrCreate("cardid").getMetrics();
                Map<String, Metric> metrics = Maps.newHashMap();
                metrics.putAll(dropwizardMetrics);
                metrics.putAll(applicationMetrics);
                return metrics;
            }
        };

        String json = "{\n" +
                "  \"request-times\": {\n" +
                "    \"metric-name\": \"request-times\",\n" +
                "    \"count\": 0,\n" +
                "    \"snapshot\": {\n" +
                "      \"values\": [],\n" +
                "      \"max\": 0,\n" +
                "      \"min\": 0,\n" +
                "      \"mean\": 0,\n" +
                "      \"stdDev\": 0,\n" +
                "      \"median\": 0,\n" +
                "      \"75thPercentile\": 0,\n" +
                "      \"95thPercentile\": 0,\n" +
                "      \"98thPercentile\": 0,\n" +
                "      \"99thPercentile\": 0,\n" +
                "      \"999thPercentile\": 0\n" +
                "    }\n" +
                "  },\n" +
                "  \"response-times\": {\n" +
                "    \"metric-name\": \"request-times\",\n" +
                "    \"count\": 0,\n" +
                "    \"snapshot\": {\n" +
                "      \"values\": [],\n" +
                "      \"max\": 0,\n" +
                "      \"min\": 0,\n" +
                "      \"mean\": 0,\n" +
                "      \"stdDev\": 0,\n" +
                "      \"median\": 0,\n" +
                "      \"75thPercentile\": 0,\n" +
                "      \"95thPercentile\": 0,\n" +
                "      \"98thPercentile\": 0,\n" +
                "      \"99thPercentile\": 0,\n" +
                "      \"999thPercentile\": 0\n" +
                "    }\n" +
                "  }\n" +
                "}";
        return Response.ok().entity(json).build();
    }



}
