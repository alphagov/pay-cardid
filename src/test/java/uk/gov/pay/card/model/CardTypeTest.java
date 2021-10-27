package uk.gov.pay.card.model;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CardTypeTest {

    @Test
    void shouldFindCardClass() {
        var cardInformation = new CardInformation("A", "C", "A", 1L, 2L);
        assertEquals(CardType.CREDIT, cardInformation.getCardType());
    }

    @Test
    void shouldThrowNullPointerException_WhenNullValuePassed_forCardClass() {
        var thrown = assertThrows(NullPointerException.class,
                () -> new CardInformation("A", null, "A", 1L, 2L));
        assertThat(thrown.getMessage(), is("Value cannot be null for paymentGatewayRepresentation"));
    }

    @Test
    void shouldThrowIllegalArgumentException_WhenInvalidValuePassed_forCardClass() {
        var thrown = assertThrows(IllegalArgumentException.class,
                () -> new CardInformation("A", "I do not exist", "A", 1L, 2L));
        assertThat(thrown.getMessage(), is("No enum found for value [I do not exist]"));
    }

}
