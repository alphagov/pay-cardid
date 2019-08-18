package uk.gov.pay.card.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.pay.card.db.CardInformationStore;
import uk.gov.pay.card.model.CardInformation;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(MockitoJUnitRunner.class)
public class CardServiceTest {
    @Mock
    CardInformationStore cardInformationStore;

    @Test
    public void testShortCardNumber() {
        Optional<CardInformation> cardInfo = Optional.of(new CardInformation("dummy", "C", "dummy", 0L, 0L));
        when(cardInformationStore.find("00")).thenReturn(cardInfo);

        assertThat(new CardService(cardInformationStore).getCardInformation("00"), is(cardInfo));
    }

    @Test
    public void test11DigitCardNumber() {
        Optional<CardInformation> cardInfo = Optional.of(new CardInformation("dummy", "C", "dummy", 0L, 0L));
        when(cardInformationStore.find("12345678901")).thenReturn(cardInfo);

        assertThat(new CardService(cardInformationStore).getCardInformation("12345678901"), is(cardInfo));
    }

    @Test
    public void test12DigitCardNumberIsTruncated() {
        Optional<CardInformation> cardInfo = Optional.of(new CardInformation("dummy", "C", "dummy", 0L, 0L));
        when(cardInformationStore.find("12345678901")).thenReturn(cardInfo);

        assertThat(new CardService(cardInformationStore).getCardInformation("123456789012"), is(cardInfo));
    }
}
