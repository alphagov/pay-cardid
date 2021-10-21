package uk.gov.pay.card.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CardInformationTest {

    @Test
    void shouldAdjustRangesLength() {
        CardInformation cardInformation = new CardInformation("visa", "D", "visa", 123456L, 123457L);
        cardInformation.updateRangeLength(11);
        assertEquals(12345600000L, cardInformation.getMin().longValue());
        assertEquals(12345799999L, cardInformation.getMax().longValue());
    }

    @Test
    void shouldTransformJcbBrand() {
        CardInformation cardInformation = new CardInformation("JCB", "C", "JCB", 123456L, 123457L);
        assertEquals("jcb", cardInformation.getBrand());
    }

    @Test
    void shouldTransformDinersBrand() {
        CardInformation cardInformation = new CardInformation("DINERS CLUB", "C", "DINERS CLUB", 123456L, 123457L);
        assertEquals("diners-club", cardInformation.getBrand());
    }

    @Test
    void shouldTransformMaestroBrand() {
        CardInformation cardInformation = new CardInformation("MAESTRO", "D", "MAESTRO", 123456L, 123457L);
        assertEquals("maestro", cardInformation.getBrand());
    }

    @Test
    void shouldTransformUnionpayBrand() {
        CardInformation cardInformation = new CardInformation("UNIONPAY", "C", "UNIONPAY", 123456L, 123457L);
        assertEquals("unionpay", cardInformation.getBrand());
    }

    @Test
    void shouldTransformAmexBrand() {
        CardInformation cardInformation = new CardInformation("AMEX", "C", "AMEX", 123456L, 123457L);
        assertEquals("american-express", cardInformation.getBrand());
    }

    @Test
    void shouldTransformDinersClubBrand() {
        CardInformation cardInformation = new CardInformation("DINERS DISCOVER", "C", "DINERS DISCOVER", 123456L, 123457L);
        assertEquals("diners-club", cardInformation.getBrand());
    }

    @Test
    void shouldTransformMastercardBrand() {
        CardInformation cardInformation = new CardInformation("MC", "D", "MC", 123456L, 123457L);
        assertEquals("master-card", cardInformation.getBrand());
    }

    @Test
    void shouldTransformMCIDebitMastercardBrand() {
        CardInformation cardInformation = new CardInformation("DEBIT MASTERCARD", "D", "DEBIT MASTERCARD", 123456L, 123457L);
        assertEquals("master-card", cardInformation.getBrand());
    }

    @Test
    void shouldTransformMCICreditMastercardBrand() {
        CardInformation cardInformation = new CardInformation("MASTERCARD CREDIT", "C", "CREDIT MASTERCARD", 123456L, 123457L);
        assertEquals("master-card", cardInformation.getBrand());
    }

    @Test
    void shouldTransformVisaCreditBrand() {
        CardInformation cardInformation = new CardInformation("VISA CREDIT", "C", "VISA CREDIT", 123456L, 123457L);
        assertEquals("visa", cardInformation.getBrand());
    }

    @Test
    void shouldTransformVisaDebitBrand() {
        CardInformation cardInformation = new CardInformation("VISA DEBIT", "D", "VISA DEBIT", 123456L, 123457L);
        assertEquals("visa", cardInformation.getBrand());
    }

    @Test
    void shouldTransformVisaElectronBrand() {
        CardInformation cardInformation = new CardInformation("ELECTRON", "D", "ELECTRON", 123456L, 123457L);
        assertEquals("visa", cardInformation.getBrand());
    }

    @Test
    void shouldTransformTypeValueOfPToCardTypeOfDebit() {
        CardInformation cardInformation = new CardInformation("ELECTRON", "P", "ELECTRON", 123456L, 123457L);
        assertEquals(CardType.DEBIT, cardInformation.getCardType());
    }

    @Test
    void shouldTransformTypeValueOfPToPrepaidStatusOfPrepaid() {
        CardInformation cardInformation = new CardInformation("ELECTRON", "P", "ELECTRON", 123456L, 123457L);
        assertEquals(PrepaidStatus.PREPAID, cardInformation.getPrepaidStatus());
    }

    @Test
    void shouldTransformCardValueOfNotCToPrepaidStatusOfNotPrepaid() {
        CardInformation cardInformation = new CardInformation("ELECTRON", "C", "ELECTRON", 123456L, 123457L);
        assertEquals(PrepaidStatus.NOT_PREPAID, cardInformation.getPrepaidStatus());
    }
}
