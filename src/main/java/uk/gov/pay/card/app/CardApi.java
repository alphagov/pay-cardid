package uk.gov.pay.card.app;

import io.dropwizard.Application;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import uk.gov.pay.card.app.config.CardConfiguration;
import uk.gov.pay.card.db.InfinispanCardInformationStore;
import uk.gov.pay.card.db.loader.BinRangeDataLoader;
import uk.gov.pay.card.healthcheck.Ping;
import uk.gov.pay.card.managed.CardInformationStoreManaged;
import uk.gov.pay.card.model.CardInformation;
import uk.gov.pay.card.resources.CardIdResource;
import uk.gov.pay.card.resources.HealthCheckResource;

import java.util.function.Function;

import static java.util.Arrays.asList;
import static uk.gov.pay.card.db.loader.BinRangeDataLoader.*;

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

        initialiseBinRangeLoaders(environment, configuration);

        environment.jersey().register(new HealthCheckResource(environment));
        environment.jersey().register(new CardIdResource());
    }

    private void initialiseBinRangeLoaders(Environment environment, CardConfiguration configuration) {

        BinRangeDataLoader worldPayBinRangeDataLoader = BinRangeDataLoaderFactory.worldpay(configuration.getWorldpayDataLocation());

        BinRangeDataLoader discoverBinRangeDataLoader = BinRangeDataLoaderFactory.discover(configuration.getWorldpayDataLocation());

        InfinispanCardInformationStore store = new InfinispanCardInformationStore(asList(worldPayBinRangeDataLoader, discoverBinRangeDataLoader));

        environment.lifecycle().manage(new CardInformationStoreManaged(store));
    }
}
