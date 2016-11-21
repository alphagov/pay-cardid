package uk.gov.pay.card.app;

import com.readytalk.metrics.StatsDReporter;
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
import uk.gov.pay.card.metrics.MetricsResource;
import uk.gov.pay.card.resources.CardIdResource;
import uk.gov.pay.card.resources.HealthCheckResource;
import uk.gov.pay.card.service.CardService;

import java.util.concurrent.TimeUnit;

import static java.util.Arrays.asList;
import static java.util.EnumSet.of;
import static javax.servlet.DispatcherType.REQUEST;
import static uk.gov.pay.card.db.loader.BinRangeDataLoader.BinRangeDataLoaderFactory;
import static uk.gov.pay.card.resources.CardIdResource.*;

public class CardApi extends Application<CardConfiguration> {

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

        environment.lifecycle().manage(new CardInformationStoreManaged(store));
        environment.jersey().register(new HealthCheckResource(environment));
        environment.jersey().register(new CardIdResource(new CardService(store)));
        environment.jersey().register(new MetricsResource(environment));
        environment.servlets().addFilter("LoggingFilter", new LoggingFilter(environment))
                .addMappingForUrlPatterns(of(REQUEST), true, CARD_INFORMATION_PATH);

        StatsDReporter.forRegistry(environment.metrics())
                .build("localhost", 8125)
                .start(10, TimeUnit.SECONDS);
    }

    private CardInformationStore initialiseCardInformationStore(CardConfiguration configuration) {

        BinRangeDataLoader worldPayBinRangeDataLoader = BinRangeDataLoaderFactory.worldpay(configuration.getWorldpayDataLocation());

        BinRangeDataLoader discoverBinRangeDataLoader = BinRangeDataLoaderFactory.discover(configuration.getDiscoverDataLocation());

        BinRangeDataLoader testCardsBinRangeDataLoader = BinRangeDataLoaderFactory.testCards(configuration.getTestCardDataLocation());

        return new RangeSetCardInformationStore(asList(worldPayBinRangeDataLoader, discoverBinRangeDataLoader, testCardsBinRangeDataLoader));
    }
}
