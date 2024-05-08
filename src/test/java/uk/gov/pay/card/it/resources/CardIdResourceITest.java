package uk.gov.pay.card.it.resources;

import io.dropwizard.testing.junit5.DropwizardAppExtension;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import io.restassured.response.ValidatableResponse;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.pay.card.app.CardApi;
import uk.gov.pay.card.app.config.CardConfiguration;

import static io.dropwizard.testing.ConfigOverride.config;
import static io.dropwizard.testing.ResourceHelpers.resourceFilePath;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.core.Is.is;
import static uk.gov.pay.card.db.CardInformationStore.CARD_RANGE_LENGTH;

@ExtendWith(DropwizardExtensionsSupport.class)
public class CardIdResourceITest {

    private static final DropwizardAppExtension<CardConfiguration> appUsingExternalFiles = new DropwizardAppExtension<>(
            CardApi.class
            , resourceFilePath("config/config.yaml")
            , config("server.applicationConnectors[0].port", "0")
            , config("server.adminConnectors[0].port", "0")
            , config("worldpayDataLocation", "file://" + resourceFilePath("card-id-resource-integration-test/worldpay-bin-ranges.csv"))
            , config("discoverDataLocation", "file://" + resourceFilePath("card-id-resource-integration-test/discover-bin-ranges.csv"))
            , config("testCardDataLocation", "file://" + resourceFilePath("card-id-resource-integration-test/test-bin-ranges.csv")));

    private static final DropwizardAppExtension<CardConfiguration> appUsingDefaultFiles = new DropwizardAppExtension<>(
            CardApi.class
            , resourceFilePath("config/config.yaml")
            , config("server.applicationConnectors[0].port", "0")
            , config("server.adminConnectors[0].port", "0"));

    @Test
    public void shouldFindDiscoverCardInformation() {
        getCardInformation("6221267457963485", appUsingExternalFiles)
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
        getCardInformation("2221000000000009", appUsingExternalFiles)
                .statusCode(200)
                .contentType(JSON)
                .body("brand", is("master-card"))
                .body("label", is("MC"))
                .body("type", is("C"))
                .body("prepaid", is("NOT_PREPAID"))
                .body("corporate", is(false));
    }

    @Test
    public void shouldErrorWhenShortCardNumberProvided() {
        getCardInformation(buildCardNumberFromPrefixAndLength("2221", 7), appUsingExternalFiles)
                .statusCode(422)
                .contentType(JSON)
                .body("errors[0]", is("cardNumber size must be between 11 and 19"));
    }

    @Test
    public void shouldFindTestCardInformationWithShortestAllowableCardNumber() {
        getCardInformation(buildCardNumberFromPrefixAndLength("2221", CARD_RANGE_LENGTH), appUsingExternalFiles)
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
        getCardInformation("2225670000000000", appUsingExternalFiles)
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
        getCardInformation("3714496353984311", appUsingExternalFiles)
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
        getCardInformation("2223451044300001", appUsingExternalFiles)
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
        getCardInformation("4208933330200001", appUsingExternalFiles)
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
        getCardInformation("8282382383829393", appUsingExternalFiles)
                .statusCode(404);
    }

    @Test
    public void shouldReturn422WhenCardNumberIsTooSmall() {
        getCardInformation("8", appUsingExternalFiles)
                .statusCode(422);
    }

    @Test
    public void shouldReturn422WhenCardNumberIsTooBig() {
        getCardInformation("12345678901234567890", appUsingExternalFiles)
                .statusCode(422);
    }

    @Test
    public void shouldReturnInformationFromBuiltInDefaultBinRangeFiles() {
        getCardInformation("1234567890000000", appUsingDefaultFiles)
                .statusCode(200)
                .contentType(JSON)
                .body("brand", is("it test example"))
                .body("label", is("IT Test Example"))
                .body("type", is("D"))
                .body("prepaid", is("NOT_PREPAID"))
                .body("corporate", is(false));
    }

    @Test
    public void shouldNotReturnInformationFromBuiltInDefaultBinRangeFilesWhenUsingFiles() {
        getCardInformation("1234567890000000", appUsingExternalFiles)
                .statusCode(404);
    }

    @Test
    public void shouldReturn422WhenJSONIsEmpty() {
        given().port(appUsingExternalFiles.getLocalPort())
                .contentType(JSON)
                .body("{}")
                .when()
                .post("/v1/api/card")
                .then().statusCode(422);
    }

    @Test
    public void shouldReturn422WhenJSONIsMissing() {
        given().port(appUsingExternalFiles.getLocalPort())
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
    
    private ValidatableResponse getCardInformation(String cardNumber, DropwizardAppExtension<CardConfiguration> appToUse) {
        return given().port(appToUse.getLocalPort())
                .contentType(JSON)
                .body(String.format("{\"cardNumber\":%s}", cardNumber))
                .when()
                .post("/v1/api/card")
                .then();
    }
}
