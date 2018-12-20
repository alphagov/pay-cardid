package uk.gov.pay.card.model;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class CardTypeTest {

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void shouldFindCardClass() {
        CardInformation cardInformation = new CardInformation("A", "C", "A", 1L, 2L);
        assertThat(cardInformation.getCardType(), is(CardType.CREDIT));
    }

    @Test
    public void shouldThrowNullPointerException_WhenNullValuePassed_forCardClass() {
        thrown.expect(java.lang.NullPointerException.class);
        thrown.expectMessage("Value cannot be null for paymentGatewayRepresentation");
        new CardInformation("A", null, "A", 1L, 2L);
    }

    @Test
    public void shouldThrowIllegalArgumentException_WhenInvalidValuePassed_forCardClass() {
        thrown.expect(java.lang.IllegalArgumentException.class);
        thrown.expectMessage("No enum found for value [I do not exist]");
        new CardInformation("A", "I do not exist", "A", 1L, 2L);
    }
}
