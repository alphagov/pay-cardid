package uk.gov.pay.card.app;

import com.codahale.metrics.health.HealthCheck;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.core.Application;
import io.dropwizard.core.setup.Bootstrap;
import io.dropwizard.core.setup.Environment;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.dropwizard.DropwizardExports;
import io.prometheus.client.exporter.MetricsServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.pay.card.app.config.CardConfiguration;
import uk.gov.pay.card.db.CardInformationStore;
import uk.gov.pay.card.db.RangeSetCardInformationStore;
import uk.gov.pay.card.managed.CardInformationStoreManaged;
import uk.gov.pay.card.resources.CardIdResource;
import uk.gov.pay.card.resources.HealthCheckResource;
import uk.gov.pay.card.service.CardService;
import uk.gov.service.payments.logging.GovUkPayDropwizardRequestJsonLogLayoutFactory;
import uk.gov.service.payments.logging.LoggingFilter;
import uk.gov.service.payments.logging.LogstashConsoleAppenderFactory;
import uk.gov.service.payments.logging.SentryAppenderFactory;

import java.util.List;

import static java.util.EnumSet.of;
import static javax.servlet.DispatcherType.REQUEST;
import static uk.gov.pay.card.db.loader.BinRangeDataLoader.BinRangeDataLoaderFactory;

public class CardApi extends Application<CardConfiguration> {

    private static final Logger logger = LoggerFactory.getLogger(CardApi.class);
    
    private static final String SERVICE_METRICS_NODE = "cardid";

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

        bootstrap.getObjectMapper().getSubtypeResolver().registerSubtypes(LogstashConsoleAppenderFactory.class);
        bootstrap.getObjectMapper().getSubtypeResolver().registerSubtypes(SentryAppenderFactory.class);
        bootstrap.getObjectMapper().getSubtypeResolver().registerSubtypes(GovUkPayDropwizardRequestJsonLogLayoutFactory.class);
    }

    @Override
    public void run(CardConfiguration configuration, Environment environment) {
        CardInformationStore store = initialiseCardInformationStore(configuration);
        environment.healthChecks().register("informationstore", new HealthCheck() {
            @Override
            protected Result check() {
                return store.isReady() ? Result.healthy() : Result.unhealthy("Card information not yet loaded");
            }
        });

        CollectorRegistry collectorRegistry = CollectorRegistry.defaultRegistry;
        collectorRegistry.register(new DropwizardExports(environment.metrics()));
        environment.admin().addServlet("prometheusMetrics", new MetricsServlet(collectorRegistry)).addMapping("/metrics");

        environment.jersey().register(new HealthCheckResource(environment));
        environment.lifecycle().manage(new CardInformationStoreManaged(store));
        environment.jersey().register(new CardIdResource(new CardService(store)));

        environment.servlets().addFilter("LoggingFilter", new LoggingFilter(environment.metrics()))
                .addMappingForUrlPatterns(of(REQUEST), true, "/v1/api/card");
    }

    private CardInformationStore initialiseCardInformationStore(CardConfiguration configuration) {
        return new RangeSetCardInformationStore(List.of(
                BinRangeDataLoaderFactory.worldpay(configuration.getWorldpayDataLocation()),
                BinRangeDataLoaderFactory.discover(configuration.getDiscoverDataLocation()),
                BinRangeDataLoaderFactory.testCards(configuration.getTestCardDataLocation())
        ));
    }
}
