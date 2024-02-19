package uk.gov.pay.card.it.resources;

import io.dropwizard.testing.junit5.DropwizardAppExtension;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import io.restassured.response.ValidatableResponse;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import uk.gov.pay.card.app.CardApi;
import uk.gov.pay.card.app.config.CardConfiguration;

import static io.dropwizard.testing.ConfigOverride.config;
import static io.dropwizard.testing.ResourceHelpers.resourceFilePath;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.core.Is.is;
import static uk.gov.pay.card.db.CardInformationStore.CARD_RANGE_LENGTH;

/**
 * @deprecated The usefulness of many of these tests is unclear - move them to pay-cardid-data?
 * <p>
 * These tests used to make assertions about data in pay-cardid-data, but that lives in a private repo.
 * <p>
 * We want to be able to test pay-cardid in a CI system that doesn't have access to private repos,
 * so the data needed for the tests to pass has been redacted to reduce its sensitivity and inlined
 * in src/test/resources/card-id-resource-integration-test
 * <p>
 * The overall effect is that a lot of these tests are now just checking that "the tests do what the tests do",
 * whereas they used to make some (fairly loose) assertions that the bin data in pay-cardid-data was correct.
 * <p>
 * If we care about testing pay-cardid-data we should set up CI for that repo separately in a system that
 * has access to private repos and move the tests that use card data there.
 * <p>
 * A test that checks the general success case and the tests for the error conditions without relying on specific
 * card details are probably still valid and should stay.
 */
@ExtendWith(DropwizardExtensionsSupport.class)
public class CardIdResourceITest {

    private static final DropwizardAppExtension<CardConfiguration> app = new DropwizardAppExtension<>(
            CardApi.class
            , resourceFilePath("config/config.yaml")
            , config("server.applicationConnectors[0].port", "0")
            , config("server.adminConnectors[0].port", "0")
            , config("worldpayDataLocation", "file://" + resourceFilePath("card-id-resource-integration-test/worldpay-bin-ranges.csv"))
            , config("discoverDataLocation", "file://" + resourceFilePath("card-id-resource-integration-test/discover-bin-ranges.csv"))
            , config("testCardDataLocation", "file://" + resourceFilePath("card-id-resource-integration-test/test-bin-ranges.csv")));

    @Test
    public void shouldFindDiscoverCardInformation() {
        getCardInformation("6221267457963485")
                .statusCode(200)
                .contentType(JSON)
                .body("brand", is("unionpay"))
                .body("label", is("UNIONPAY"))
                .body("type", is("CD"))
                .body("prepaid", is("NOT_PREPAID"))
                .body("corporate", is(false));
    }

    @Test
    public void shouldFindTestCardInformation() {
        getCardInformation("2221000000000009")
                .statusCode(200)
                .contentType(JSON)
                .body("brand", is("master-card"))
                .body("label", is("MC"))
                .body("type", is("C"))
                .body("prepaid", is("NOT_PREPAID"))
                .body("corporate", is(false));
    }

    @ParameterizedTest
    @ValueSource(ints = {CARD_RANGE_LENGTH - 2, CARD_RANGE_LENGTH - 1})
    public void shouldErrorWhenShortCardNumberProvided(int length) {
        getCardInformation(buildCardNumberFromPrefixAndLength("2221", length))
                .statusCode(422)
                .contentType(JSON)
                .body("errors[0]", is(String.format("cardNumber size must be between %s and 19", CARD_RANGE_LENGTH)));
    }

    @Test
    public void shouldFindTestCardInformationWithShortestAllowableCardNumber() {
        getCardInformation(buildCardNumberFromPrefixAndLength("2221", CARD_RANGE_LENGTH))
                .statusCode(200)
                .contentType(JSON)
                .body("brand", is("master-card"))
                .body("label", is("MC"))
                .body("type", is("C"))
                .body("prepaid", is("NOT_PREPAID"))
                .body("corporate", is(false));
    }

    @Test
    public void shouldFindWorldpayCardInformation() {
        getCardInformation("2225670000000000")
                .statusCode(200)
                .contentType(JSON)
                .body("brand", is("master-card"))
                .body("label", is("DEBIT MASTERCARD"))
                .body("type", is("D"))
                .body("prepaid", is("NOT_PREPAID"))
                .body("corporate", is(false));
    }

    @Test
    public void shouldFindAmexCardInformation() {
        getCardInformation("3714496353984311")
                .statusCode(200)
                .contentType(JSON)
                .body("brand", is("american-express"))
                .body("label", is("AMEX"))
                .body("type", is("C"))
                .body("prepaid", is("NOT_PREPAID"))
                .body("corporate", is(false));
    }

    @Test
    public void shouldFindMastercardCreditCorporateCardInformation() {
        getCardInformation("2223451044300001")
                .statusCode(200)
                .contentType(JSON)
                .body("brand", is("master-card"))
                .body("label", is("MASTERCARD CREDIT"))
                .body("type", is("C"))
                .body("prepaid", is("NOT_PREPAID"))
                .body("corporate", is(true));
    }

    @Test
    public void shouldFindPrepaidCard() {
        getCardInformation("4208933330200001")
                .statusCode(200)
                .contentType(JSON)
                .body("brand", is("visa"))
                .body("label", is("VISA CREDIT"))
                .body("type", is("D"))
                .body("prepaid", is("PREPAID"))
                .body("corporate", is(false));
    }

    @Test
    public void shouldReturn404WhenCardInformationNotFound() {
        getCardInformation("8282382383829393")
                .statusCode(404);
    }

    @Test
    public void shouldReturn422WhenCardNumberIsTooSmall() {
        getCardInformation("8")
                .statusCode(422);
    }

    @Test
    public void shouldReturn422WhenCardNumberIsTooBig() {
        getCardInformation("12345678901234567890")
                .statusCode(422);
    }

    @Test
    public void shouldReturn422WhenJSONIsEmpty() {
        given().port(app.getLocalPort())
                .contentType(JSON)
                .body("{}")
                .when()
                .post("/v1/api/card")
                .then().statusCode(422);
    }

    @Test
    public void shouldReturn422WhenJSONIsMissing() {
        given().port(app.getLocalPort())
                .contentType(JSON)
                .body("")
                .when()
                .post("/v1/api/card")
                .then().statusCode(422);
    }

    @NotNull
    private static String buildCardNumberFromPrefixAndLength(String cardStart, int cardLength) {
        StringBuilder cardNumberBuilder = new StringBuilder(cardStart);
        while (cardNumberBuilder.length() < cardLength) {
            cardNumberBuilder.append('0');
        }
        return cardNumberBuilder.toString();
    }

    private ValidatableResponse getCardInformation(String cardNumber) {
        return given().port(app.getLocalPort())
                .contentType(JSON)
                .body(String.format("{\"cardNumber\":%s}", cardNumber))
                .when()
                .post("/v1/api/card")
                .then();
    }
}
