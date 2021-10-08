package uk.gov.pay.card.db.loader;

import org.junit.jupiter.api.Test;
import uk.gov.pay.card.db.CardInformationStore;
import uk.gov.pay.card.db.loader.BinRangeDataLoader.BinRangeDataLoaderFactory;
import uk.gov.pay.card.model.CardInformation;
import uk.gov.pay.card.model.PrepaidStatus;

import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

public class BinRangeDataLoaderTest {

    @Test
    public void shouldLoadWorldpayBinRangesFromURL() throws Exception {

        URL url = this.getClass().getResource("/worldpay/worldpay-bin-ranges.csv");
        BinRangeDataLoader worldpayBinRangeLoader = BinRangeDataLoaderFactory.worldpay(url);

        CardInformationStore cardInformationStore = mock(CardInformationStore.class);
        worldpayBinRangeLoader.loadDataTo(cardInformationStore);

        verify(cardInformationStore, times(5)).put(any(CardInformation.class));
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

        CardInformation expectedCardInformation = new CardInformation("ELECTRON", "D", "ELECTRON", 511226111L, 511226200L, false, PrepaidStatus.PREPAID);
        verify(cardInformationStore).put(expectedCardInformation);
    }
}
