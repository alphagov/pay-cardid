package uk.gov.pay.card.service;

import org.junit.Before;
import org.junit.Test;
import uk.gov.pay.card.db.CardInformationStore;
import uk.gov.pay.card.model.CardInformation;

import java.util.Optional;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CardServiceTest {

    private final CardInformationStore cardInformationStore = mock(CardInformationStore.class);

    private CardService cardService;

    @Before
    public void setup() {
        cardService = new CardService(cardInformationStore);
    }

    @Test
    public void shouldStripTheCardNumberTo11DigitsForBingRangeLookup() {

        CardInformation expectedCardInformation = new CardInformation("visa", "D", "visa", 11000L, 13000L);
        when(cardInformationStore.find("12345678901")).thenReturn(Optional.of(expectedCardInformation));

        Optional<CardInformation> cardInformation = cardService.getCardInformation("1234567890123456");

        assertThat(cardInformation.isPresent(), is(true));
        assertThat(expectedCardInformation, is(cardInformation.get()));
        verify(cardInformationStore).find("12345678901");
    }
}
