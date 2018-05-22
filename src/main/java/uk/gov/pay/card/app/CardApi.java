package uk.gov.pay.card.app;

import com.amazonaws.xray.AWSXRay;
import com.amazonaws.xray.AWSXRayRecorderBuilder;
import com.amazonaws.xray.javax.servlet.AWSXRayServletFilter;
import com.amazonaws.xray.plugins.EC2Plugin;
import com.amazonaws.xray.strategy.sampling.LocalizedSamplingStrategy;
import com.codahale.metrics.graphite.GraphiteReporter;
import com.codahale.metrics.graphite.GraphiteSender;
import com.codahale.metrics.graphite.GraphiteUDP;
import io.dropwizard.Application;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import uk.gov.pay.card.app.config.CardConfiguration;
import uk.gov.pay.card.db.CardInformationStore;
import uk.gov.pay.card.db.RangeSetCardInformationStore;
import uk.gov.pay.card.db.loader.BinRangeDataLoader;
import uk.gov.pay.card.filters.LoggingFilter;
import uk.gov.pay.card.healthcheck.Ping;
import uk.gov.pay.card.managed.CardInformationStoreManaged;
import uk.gov.pay.card.resources.CardIdResource;
import uk.gov.pay.card.resources.HealthCheckResource;
import uk.gov.pay.card.service.CardService;

import java.net.URL;
import java.util.concurrent.TimeUnit;

import static java.util.Arrays.asList;
import static java.util.EnumSet.of;
import static javax.servlet.DispatcherType.REQUEST;
import static uk.gov.pay.card.db.loader.BinRangeDataLoader.BinRangeDataLoaderFactory;
import static uk.gov.pay.card.resources.CardIdResource.*;

public class CardApi extends Application<CardConfiguration> {

    private static final String SERVICE_METRICS_NODE = "cardid";
    private static final int GRAPHITE_SENDING_PERIOD_SECONDS = 10;

    public static void main(String[] args) throws Exception {
        new CardApi().run(args);
    }

    @Override
    public void initialize(Bootstrap<CardConfiguration> bootstrap) {
        bootstrap.setConfigurationSourceProvider(
                new SubstitutingSourceProvider(
                        bootstrap.getConfigurationSourceProvider(),
                        new EnvironmentVariableSubstitutor(false)
                )
        );
    }

    @Override
    public void run(CardConfiguration configuration, Environment environment) throws Exception {
        environment.healthChecks().register("ping", new Ping());

        CardInformationStore store = initialiseCardInformationStore(configuration);
        initialiseMetrics(configuration, environment);
        initialiseXRay();

        environment.lifecycle().manage(new CardInformationStoreManaged(store));
        environment.jersey().register(new HealthCheckResource(environment));
        environment.jersey().register(new CardIdResource(new CardService(store)));

        environment.servlets().addFilter("LoggingFilter", new LoggingFilter(environment.metrics()))
                .addMappingForUrlPatterns(of(REQUEST), true, CARD_INFORMATION_PATH);
        environment.servlets().addFilter("AWSXRayServletFilter", new AWSXRayServletFilter("pay-cardid"))
                .addMappingForUrlPatterns(of(REQUEST), true, API_VERSION_PATH + "/*");
    }

    private void initialiseMetrics(CardConfiguration configuration, Environment environment) {
        GraphiteSender graphiteUDP = new GraphiteUDP(configuration.getGraphiteHost(), Integer.valueOf(configuration.getGraphitePort()));
        GraphiteReporter.forRegistry(environment.metrics())
                .prefixedWith(SERVICE_METRICS_NODE)
                .build(graphiteUDP)
                .start(GRAPHITE_SENDING_PERIOD_SECONDS, TimeUnit.SECONDS);

    }

    /**
     * @see <a href="http://docs.aws.amazon.com/xray/latest/devguide/xray-sdk-java-configuration.html">Configuring the X-Ray SDK for Java</a>
     */
    private void initialiseXRay() {
        AWSXRayRecorderBuilder builder = AWSXRayRecorderBuilder.standard().withPlugin(new EC2Plugin());
        URL ruleFile = CardApi.class.getResource("/aws-xray-sampling-rules.json");
        builder.withSamplingStrategy(new LocalizedSamplingStrategy(ruleFile));
        AWSXRay.setGlobalRecorder(builder.build());
    }

    private CardInformationStore initialiseCardInformationStore(CardConfiguration configuration) {
        BinRangeDataLoader worldPayBinRangeDataLoader = BinRangeDataLoaderFactory.worldpay(configuration.getWorldpayDataLocation());
        BinRangeDataLoader discoverBinRangeDataLoader = BinRangeDataLoaderFactory.discover(configuration.getDiscoverDataLocation());
        BinRangeDataLoader testCardsBinRangeDataLoader = BinRangeDataLoaderFactory.testCards(configuration.getTestCardDataLocation());

        return new RangeSetCardInformationStore(asList(worldPayBinRangeDataLoader, discoverBinRangeDataLoader, testCardsBinRangeDataLoader));
    }
}
