package uk.gov.pay.card.db.loader;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pay.card.db.CardInformationStore;
import uk.gov.pay.card.db.loader.BinRangeDataLoader.BinRangeDataLoaderFactory;
import uk.gov.pay.card.model.CardInformation;
import uk.gov.pay.card.model.CardType;
import uk.gov.pay.card.model.PrepaidStatus;

import java.net.URL;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
public class BinRangeDataLoaderTest {
    
    @Captor
    private ArgumentCaptor<CardInformation> cardInformationArgumentCaptor; 

    @Test
    public void shouldLoadWorldpayBinRangesFromURL() throws Exception {

        URL url = this.getClass().getResource("/worldpay/worldpay-bin-ranges.csv");
        BinRangeDataLoader worldpayBinRangeLoader = BinRangeDataLoaderFactory.worldpay(url);

        CardInformationStore cardInformationStore = mock(CardInformationStore.class);
        worldpayBinRangeLoader.loadDataTo(cardInformationStore);

        verify(cardInformationStore, times(4)).put(any(CardInformation.class));
    }

    @Test
    public void shouldLoadDiscoverBinRangesFromURL() throws Exception {

        URL url = this.getClass().getResource("/discover/discover-bin-ranges.csv");
        BinRangeDataLoader discoverBinRangeLoader = BinRangeDataLoaderFactory.discover(url);

        CardInformationStore cardInformationStore = mock(CardInformationStore.class);
        discoverBinRangeLoader.loadDataTo(cardInformationStore);

        verify(cardInformationStore, times(3)).put(any(CardInformation.class));
    }

    @Test
    public void shouldLoadTestBinRangesFromURL() throws Exception {

        URL url = this.getClass().getResource("/card-id-resource-integration-test/test-bin-ranges.csv");
        BinRangeDataLoader worldpayBinRangeLoader = BinRangeDataLoaderFactory.testCards(url);

        CardInformationStore cardInformationStore = mock(CardInformationStore.class);
        worldpayBinRangeLoader.loadDataTo(cardInformationStore);

        verify(cardInformationStore, times(3)).put(cardInformationArgumentCaptor.capture());
        
        assertThat(cardInformationArgumentCaptor.getAllValues(), containsInAnyOrder(
                new CardInformation("discover", CardType.CREDIT_DEBIT, "DISCOVER", 712211110000000000L, 712214009999999999L, true, PrepaidStatus.PREPAID),
                new CardInformation("american-express", CardType.CREDIT, "AMEX", 371449635000000000L, 371449635999999999L, false, PrepaidStatus.NOT_PREPAID),
                new CardInformation("master-card", CardType.CREDIT, "MC", 222100000000000000L, 222100000999999999L, false, PrepaidStatus.NOT_PREPAID)
        ));
    }

    @Test
    public void shouldThrowExceptionWhenURLDoesNotExist() throws Exception {

        BinRangeDataLoader worldpayBinRangeLoader = BinRangeDataLoaderFactory.worldpay(new URL("file:///nonexistent/path"));

        CardInformationStore cardInformationStore = mock(CardInformationStore.class);

        assertThrows(BinRangeDataLoader.DataLoaderException.class, () -> worldpayBinRangeLoader.loadDataTo(cardInformationStore));

        verifyNoInteractions(cardInformationStore);
    }

    @Test
    public void shouldLoadBinRangeDataAsCardInformationFromURL() throws Exception {
        URL url = this.getClass().getResource("/worldpay-single/worldpay-bin-ranges-single.csv");
        BinRangeDataLoader worldpayBinRangeLoader = BinRangeDataLoaderFactory.worldpay(url);

        CardInformationStore cardInformationStore = mock(CardInformationStore.class);
        worldpayBinRangeLoader.loadDataTo(cardInformationStore);

        CardInformation expectedCardInformation = new CardInformation("master-card", CardType.DEBIT, "DEBIT MASTERCARD", 222345000000000000L, 222345009999999999L, true, PrepaidStatus.NOT_PREPAID);
        verify(cardInformationStore).put(expectedCardInformation);
    }
}
