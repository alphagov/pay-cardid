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
        assertThat(cardInformation.getMax(), is(123457000L));

    }
}
