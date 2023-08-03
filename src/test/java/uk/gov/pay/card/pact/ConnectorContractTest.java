package uk.gov.pay.card.pact;


import au.com.dius.pact.provider.junit5.HttpTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.IgnoreNoPactsToVerify;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.loader.PactBroker;
import au.com.dius.pact.provider.junitsupport.loader.PactBrokerAuth;
import au.com.dius.pact.provider.junitsupport.loader.PactFolder;
import au.com.dius.pact.provider.junitsupport.loader.VersionSelector;
import au.com.dius.pact.provider.junitsupport.target.Target;
import au.com.dius.pact.provider.junitsupport.target.TestTarget;
import io.dropwizard.testing.junit5.DropwizardAppExtension;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.pay.card.app.CardApi;
import uk.gov.pay.card.app.config.CardConfiguration;

import static io.dropwizard.testing.ConfigOverride.config;
import static io.dropwizard.testing.ResourceHelpers.resourceFilePath;

@ExtendWith(DropwizardExtensionsSupport.class)
@Provider("cardid")
@PactBroker(url = "https://${PACT_BROKER_HOST:pact-broker.deploy.payments.service.gov.uk}",
        authentication = @PactBrokerAuth(username = "${PACT_BROKER_USERNAME}", password = "${PACT_BROKER_PASSWORD}"),
        consumerVersionSelectors = @VersionSelector(consumer = "connector", tag = "${PACT_CONSUMER_TAG:}", fallbackTag = "test-fargate"))
@IgnoreNoPactsToVerify
@Tag("contract")
class ConnectorContractTest {

    private static final DropwizardAppExtension<CardConfiguration> app = new DropwizardAppExtension<>(
            CardApi.class
            , resourceFilePath("config/config.yaml")
            , config("server.applicationConnectors[0].port", "0")
            , config("server.adminConnectors[0].port", "0")
            , config("worldpayDataLocation", "file://" + resourceFilePath("card-id-resource-integration-test/worldpay-bin-ranges.csv"))
            , config("discoverDataLocation", "file://" + resourceFilePath("card-id-resource-integration-test/discover-bin-ranges.csv"))
            , config("testCardDataLocation", "file://" + resourceFilePath("card-id-resource-integration-test/test-bin-ranges.csv")));

    @BeforeEach
    void before(PactVerificationContext context) {
        if (context != null) {
            context.setTarget(new HttpTestTarget("localhost", app.getLocalPort(), "/"));
        }
    }

    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider.class)
    void pactVerificationTestTemplate(PactVerificationContext context) {
        if (context != null) {
            context.verifyInteraction();
        }
    }
}
