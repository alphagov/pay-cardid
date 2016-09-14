package uk.gov.pay.card.model;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class CardInformationTest {

    @Test
    public void shouldAdjustRangesLegnth() {

        CardInformation cardInformation = new CardInformation("visa", "D", "visa", 123456L, 123457L);
        cardInformation.updateRangeLength(9);
        assertThat(cardInformation.getMin(), is(123456000L));
        assertThat(cardInformation.getMax(), is(123457999L));

    }

    @Test
    public void shouldTransformJcbBrand() {
        CardInformation cardInformation = new CardInformation("JCB", "C", "JCB", 123456L, 123457L);
        cardInformation.transformBrand();
        assertThat(cardInformation.getBrand(), is("jcb"));
    }

    @Test
    public void shouldTransformDiscoverBrand() {
        CardInformation cardInformation = new CardInformation("DISCOVER", "C", "DISCOVER", 123456L, 123457L);
        cardInformation.transformBrand();
        assertThat(cardInformation.getBrand(), is("discover"));
    }

    @Test
    public void shouldTransformMaestroBrand() {
        CardInformation cardInformation = new CardInformation("MAESTRO", "C", "MAESTRO", 123456L, 123457L);
        cardInformation.transformBrand();
        assertThat(cardInformation.getBrand(), is("maestro"));
    }

    @Test
    public void shouldTransformUnionpayBrand() {
        CardInformation cardInformation = new CardInformation("UNIONPAY", "C", "UNIONPAY", 123456L, 123457L);
        cardInformation.transformBrand();
        assertThat(cardInformation.getBrand(), is("unionpay"));
    }

    @Test
    public void shouldTransformAmexBrand() {
        CardInformation cardInformation = new CardInformation("AMERICAN EXPRESS", "C", "AMERICAN EXPRESS", 123456L, 123457L);
        cardInformation.transformBrand();
        assertThat(cardInformation.getBrand(), is("american-express"));
    }

    @Test
    public void shouldTransformDinersClubBrand() {
        CardInformation cardInformation = new CardInformation("DINERS CLUB", "C", "DINERS CLUB", 123456L, 123457L);
        cardInformation.transformBrand();
        assertThat(cardInformation.getBrand(), is("diners-club"));
    }

    @Test
    public void shouldTransformMastercardBrand() {
        CardInformation cardInformation = new CardInformation("MC", "D", "MC", 123456L, 123457L);
        cardInformation.transformBrand();
        assertThat(cardInformation.getBrand(), is("master-card"));
    }

    @Test
    public void shouldTransformMCIDebitMastercardBrand() {
        CardInformation cardInformation = new CardInformation("MCI DEBIT", "D", "MCI DEBIT", 123456L, 123457L);
        cardInformation.transformBrand();
        assertThat(cardInformation.getBrand(), is("master-card"));
    }

    @Test
    public void shouldTransformMCICreditMastercardBrand() {
        CardInformation cardInformation = new CardInformation("MCI CREDIT", "C", "MCI CREDIT", 123456L, 123457L);
        cardInformation.transformBrand();
        assertThat(cardInformation.getBrand(), is("master-card"));
    }

    @Test
    public void shouldTransformVisaCreditBrand() {
        CardInformation cardInformation = new CardInformation("VISA CREDIT", "C", "VISA CREDIT", 123456L, 123457L);
        cardInformation.transformBrand();
        assertThat(cardInformation.getBrand(), is("visa"));
    }

    @Test
    public void shouldTransformVisaDebitBrand() {
        CardInformation cardInformation = new CardInformation("VISA DEBIT", "C", "VISA DEBIT", 123456L, 123457L);
        cardInformation.transformBrand();
        assertThat(cardInformation.getBrand(), is("visa"));
    }
}
