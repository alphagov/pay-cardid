package uk.gov.pay.card.it.resources;

import com.jayway.restassured.response.ValidatableResponse;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.junit.Rule;
import org.junit.Test;
import uk.gov.pay.card.app.CardApi;
import uk.gov.pay.card.app.config.CardConfiguration;

import java.io.IOException;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.http.ContentType.JSON;
import static io.dropwizard.testing.ConfigOverride.config;
import static io.dropwizard.testing.ResourceHelpers.resourceFilePath;
import static org.hamcrest.core.Is.is;


public class CardIdResourceITest {

    @Rule
    public DropwizardAppRule<CardConfiguration> app = new DropwizardAppRule<>(
            CardApi.class
            , resourceFilePath("config/config.yaml")
            , config("worldpayDataLocation", "data/sources/worldpay/")
            , config("discoverDataLocation", "data/sources/discover/"));

    @Test
    public void shouldFindDiscoverCardInformation() throws IOException {

        getCardInformation("6221267457963485")
                .statusCode(200)
                .contentType(JSON)
                .body("brand", is("UNIONPAY"))
                .body("label", is("UNIONPAY"))
                .body("type", is("CD"));

    }

    @Test
    public void shouldFindWorldpayCardInformation() throws IOException {

        getCardInformation("4000020004598361")
                .statusCode(200)
                .contentType(JSON)
                .body("brand", is("VISA CREDIT"))
                .body("label", is("VISA CREDIT"))
                .body("type", is("C"));

    }

    @Test
    public void shouldReturn404WhenCardInformationNotFoud() {
        getCardInformation("8282382383829393")
                .statusCode(404);
    }

    private ValidatableResponse getCardInformation(String cardNumber) {
        return given().port(app.getLocalPort())
                .get(String.format("/v1/api/card/%s", cardNumber))
                .then();
    }
}
