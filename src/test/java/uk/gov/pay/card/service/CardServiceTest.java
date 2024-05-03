package uk.gov.pay.card.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pay.card.db.CardInformationStore;
import uk.gov.pay.card.model.CardInformation;
import uk.gov.pay.card.model.CardType;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CardServiceTest {
    @Mock
    CardInformationStore cardInformationStore;

    @ParameterizedTest
    @CsvSource({
            "10,100000000000000000", 
            "12345678901,123456789010000000", 
            "123456789012345,123456789012345000", 
            "1234567890123456,123456789012345600", 
    })
    public void testCardNumbers(String actual, String expected) {
        Optional<CardInformation> cardInfo = Optional.of(new CardInformation("dummy", CardType.CREDIT, "dummy", 0L, 0L));
        when(cardInformationStore.find(Long.valueOf(expected))).thenReturn(cardInfo);

        assertThat(new CardService(cardInformationStore).getCardInformation(Long.valueOf(actual)), is(cardInfo));
    }
}
