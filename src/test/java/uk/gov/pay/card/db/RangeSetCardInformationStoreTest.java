package uk.gov.pay.card.db;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pay.card.db.loader.BinRangeDataLoader;
import uk.gov.pay.card.model.CardInformation;
import uk.gov.pay.card.model.CardType;

import java.net.URL;
import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static uk.gov.pay.card.db.loader.BinRangeDataLoader.BinRangeDataLoaderFactory;

@ExtendWith(MockitoExtension.class)
public class RangeSetCardInformationStoreTest {

    private CardInformationStore cardInformationStore;

    @AfterEach
    void tearDown() {
        cardInformationStore.destroy();
    }

    @Test
    void shouldUseLoadersToInitialiseData() throws Exception {
        BinRangeDataLoader mockBinRangeLoader = mock(BinRangeDataLoader.class);
        cardInformationStore = new RangeSetCardInformationStore(Collections.singletonList(mockBinRangeLoader));
        cardInformationStore.initialiseCardInformation();

        verify(mockBinRangeLoader).loadDataTo(cardInformationStore);
    }

    @Test
    void shouldFindCorporateCreditCardType() throws Exception {
        URL url = this.getClass().getResource("/worldpay/worldpay-bin-ranges.csv");
        BinRangeDataLoader worldpayBinRangeLoader = BinRangeDataLoaderFactory.worldpay(url);

        cardInformationStore = new RangeSetCardInformationStore(Collections.singletonList(worldpayBinRangeLoader));
        worldpayBinRangeLoader.loadDataTo(cardInformationStore);

        Optional<CardInformation> cardInformation = cardInformationStore.find("22234500001");

        assertThat(cardInformation.isPresent(), is(true));
        assertThat(cardInformation.orElseThrow().getBrand(), is("master-card"));
        assertThat(cardInformation.orElseThrow().getCardType(), is(CardType.DEBIT));
        assertThat(cardInformation.orElseThrow().getLabel(), is("MASTERCARD CREDIT"));
        assertThat(cardInformation.orElseThrow().isCorporate(), is(true));
    }

    @Test
    void shouldFindCardInformationForCardIdPrefix() throws Exception {
        URL url = this.getClass().getResource("/worldpay/worldpay-bin-ranges.csv");
        BinRangeDataLoader worldpayBinRangeLoader = BinRangeDataLoaderFactory.worldpay(url);
        cardInformationStore = new RangeSetCardInformationStore(Collections.singletonList(worldpayBinRangeLoader));
        cardInformationStore.initialiseCardInformation();

        Optional<CardInformation> cardInformation = cardInformationStore.find("22256712345");
        assertTrue(cardInformation.isPresent());
        assertThat(cardInformation.get().getBrand(), is("master-card"));
        assertThat(cardInformation.get().getCardType(), is(CardType.DEBIT));
        assertThat(cardInformation.get().getLabel(), is("DEBIT MASTERCARD"));
        assertThat(cardInformation.get().isCorporate(), is(false));
    }
}
