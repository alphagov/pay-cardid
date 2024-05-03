package uk.gov.pay.card.db.loader;


import org.junit.jupiter.api.Test;
import uk.gov.pay.card.model.CardType;
import uk.gov.pay.card.model.PrepaidStatus;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static uk.gov.pay.card.db.CardInformationStore.CARD_RANGE_LENGTH;

class BinRangeParserTest {
    @Test
    void shouldTransformJcbBrand() {
        String brand = BinRangeParser.calculateCardBrand("JAPANESE CREDIT BUREAU");
        assertThat(brand, is("jcb"));
    }

    @Test
    void shouldTransformDinersBrand() {
        String brand = BinRangeParser.calculateCardBrand("DINERS CLUB");
        assertThat(brand, is("diners-club"));
    }

    @Test
    void shouldTransformMaestroBrand() {
        String brand = BinRangeParser.calculateCardBrand("MAESTRO");
        assertThat(brand, is("maestro"));
    }

    @Test
    void shouldTransformUnionPayBrand() {
        String brand = BinRangeParser.calculateCardBrand("UNIONPAY");
        assertThat(brand, is("unionpay"));
    }

    @Test
    void shouldTransformAmexBrand() {
        String brand = BinRangeParser.calculateCardBrand("AMEX");
        assertThat(brand, is("american-express"));
    }

    @Test
    void shouldTransformDinersClubBrand() {
        String brand = BinRangeParser.calculateCardBrand("DINERS DISCOVER");
        assertThat(brand, is("discover"));
    }

    @Test
    void shouldTransformMastercardBrand() {
        String brand = BinRangeParser.calculateCardBrand("MC");
        assertThat(brand, is("master-card"));
    }

    @Test
    void shouldTransformMCIDebitMastercardBrand() {
        String brand = BinRangeParser.calculateCardBrand("DEBIT MASTERCARD");
        assertThat(brand, is("master-card"));
    }

    @Test
    void shouldTransformMCICreditMastercardBrand() {
        String brand = BinRangeParser.calculateCardBrand("MASTERCARD CREDIT");
        assertThat(brand, is("master-card"));
    }

    @Test
    void shouldTransformVisaCreditBrand() {
        String brand = BinRangeParser.calculateCardBrand("VISA CREDIT");
        assertThat(brand, is("visa"));
    }

    @Test
    void shouldTransformVisaDebitBrand() {
        String brand = BinRangeParser.calculateCardBrand("VISA DEBIT");
        assertThat(brand, is("visa"));
    }

    @Test
    void shouldTransformVisaElectronBrand() {
        String brand = BinRangeParser.calculateCardBrand("ELECTRON");
        assertThat(brand, is("visa"));
    }

    @Test
    void shouldTransformTypeValueOfPToCardTypeOfDebit() {
        CardType cardType = BinRangeParser.calculateCardType("P");
        assertThat(cardType, is(CardType.DEBIT));
    }

    @Test
    void shouldTransformTypeValueOfPToPrepaidStatusOfPrepaid() {
        PrepaidStatus prepaidStatus = BinRangeParser.calculateWorldpayPrepaidStatus("P");
        assertThat(prepaidStatus, is(PrepaidStatus.PREPAID));
    }

    @Test
    void shouldTransformCardValueOfNotCToPrepaidStatusOfNotPrepaid() {
        PrepaidStatus prepaidStatus = BinRangeParser.calculateWorldpayPrepaidStatus("c");
        assertThat(prepaidStatus, is(PrepaidStatus.NOT_PREPAID));
    }

    @Test
    void shouldParseTestCardPrepaidStatusWhenPrepaid() {
        PrepaidStatus prepaidStatus = BinRangeParser.calculateTestCardPrepaidStatus("PREPAID");
        assertThat(prepaidStatus, is(PrepaidStatus.PREPAID));
    }

    @Test
    void shouldParseTestCardPrepaidStatusWhenNotPrepaid() {
        PrepaidStatus prepaidStatus = BinRangeParser.calculateTestCardPrepaidStatus("NOT_PREPAID");
        assertThat(prepaidStatus, is(PrepaidStatus.NOT_PREPAID));
    }

    @Test
    void shouldFindCardClass() {
        CardType cardType = BinRangeParser.calculateCardType("C");
        assertThat(cardType, is(CardType.CREDIT));
    }

    @Test
    void shouldThrowNullPointerException_WhenNullValuePassed_forCardClass() {
        var thrown = assertThrows(NullPointerException.class,
                () -> BinRangeParser.calculateCardType(null));
        assertThat(thrown.getMessage(), is("Value cannot be null for paymentGatewayRepresentation"));
    }

    @Test
    void shouldThrowIllegalArgumentException_WhenInvalidValuePassed_forCardClass() {
        var thrown = assertThrows(IllegalArgumentException.class,
                () -> BinRangeParser.calculateCardType("I do not exist"));
        assertThat(thrown.getMessage(), is("No enum found for value [I do not exist]"));
    }


    @Test
    void shouldCalculateCardInformationMinForCardRangeLengthOf18() {
        Long minCardDigit = BinRangeParser.calculateMinDigitForCardLength(1L, CARD_RANGE_LENGTH);
        assertThat(minCardDigit, is(100000000000000000L));
    }

    @Test
    void shouldCalculateCardInformationMaxForCardRangeLengthOf18() {
        Long minCardDigit = BinRangeParser.calculateMaxDigitForCardLength(19L, CARD_RANGE_LENGTH);
        assertThat(minCardDigit, is(199999999999999999L));
    }
}
