package uk.gov.pay.card.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class CardTypeTest {

    @Test
    public void shouldFindCardClass() {
        CardInformation cardInformation = new CardInformation("A", "C", "A", 1L, 2L);
        assertEquals(CardType.CREDIT, cardInformation.getCardType());
    }

    @Test
    public void shouldThrowNullPointerException_WhenNullValuePassed_forCardClass() {
        try {
            new CardInformation("A", null, "A", 1L, 2L);
            fail("The constructor was expected to throw an exception");
        } catch (NullPointerException e) {
            assertEquals("Value cannot be null for paymentGatewayRepresentation", e.getMessage());
        } catch (Throwable e) {
            fail("An unexpected exception was thrown by the constructor: " + e);
        }
    }

    @Test
    public void shouldThrowIllegalArgumentException_WhenInvalidValuePassed_forCardClass() {
        try {
            new CardInformation("A", "I do not exist", "A", 1L, 2L);
            fail("The constructor was expected to throw an exception");
        } catch (IllegalArgumentException e) {
            assertEquals("No enum found for value [I do not exist]", e.getMessage());
        } catch (Throwable e) {
            fail("An unexpected exception was thrown by the constructor: " + e);
        }
    }
}
