package uk.gov.pay.card.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CardInformationTest {

    @Test
    public void shouldTransformJcbBrand() {
        CardInformation cardInformation = new CardInformation("JCB", "C", "JCB", 123456L, 123457L);
        assertEquals("jcb", cardInformation.getBrand());
    }

    @Test
    public void shouldTransformDiscoverBrand() {
        CardInformation cardInformation = new CardInformation("DISCOVER", "C", "DISCOVER", 123456L, 123457L);
        assertEquals("discover", cardInformation.getBrand());
    }

    @Test
    public void shouldTransformMaestroBrand() {
        CardInformation cardInformation = new CardInformation("MAESTRO", "D", "MAESTRO", 123456L, 123457L);
        assertEquals("maestro", cardInformation.getBrand());
    }

    @Test
    public void shouldTransformUnionpayBrand() {
        CardInformation cardInformation = new CardInformation("UNIONPAY", "C", "UNIONPAY", 123456L, 123457L);
        assertEquals("unionpay", cardInformation.getBrand());
    }

    @Test
    public void shouldTransformAmexBrand() {
        CardInformation cardInformation = new CardInformation("AMERICAN EXPRESS", "C", "AMERICAN EXPRESS", 123456L, 123457L);
        assertEquals("american-express", cardInformation.getBrand());
    }

    @Test
    public void shouldTransformDinersClubBrand() {
        CardInformation cardInformation = new CardInformation("DINERS CLUB", "C", "DINERS CLUB", 123456L, 123457L);
        assertEquals("diners-club", cardInformation.getBrand());
    }

    @Test
    public void shouldTransformMastercardBrand() {
        CardInformation cardInformation = new CardInformation("MC", "D", "MC", 123456L, 123457L);
        assertEquals("master-card", cardInformation.getBrand());
    }

    @Test
    public void shouldTransformMCIDebitMastercardBrand() {
        CardInformation cardInformation = new CardInformation("MCI DEBIT", "D", "MCI DEBIT", 123456L, 123457L);
        assertEquals("master-card", cardInformation.getBrand());
    }

    @Test
    public void shouldTransformMCICreditMastercardBrand() {
        CardInformation cardInformation = new CardInformation("MCI CREDIT", "C", "MCI CREDIT", 123456L, 123457L);
        assertEquals("master-card", cardInformation.getBrand());
    }

    @Test
    public void shouldTransformVisaCreditBrand() {
        CardInformation cardInformation = new CardInformation("VISA CREDIT", "C", "VISA CREDIT", 123456L, 123457L);
        assertEquals("visa", cardInformation.getBrand());
    }

    @Test
    public void shouldTransformVisaDebitBrand() {
        CardInformation cardInformation = new CardInformation("VISA DEBIT", "D", "VISA DEBIT", 123456L, 123457L);
        assertEquals("visa", cardInformation.getBrand());
    }

    @Test
    public void shouldTransformVisaElectronBrand() {
        CardInformation cardInformation = new CardInformation("ELECTRON", "D", "ELECTRON", 123456L, 123457L);
        assertEquals("visa", cardInformation.getBrand());
    }
}
