package uk.gov.pay.card.db.loader;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import uk.gov.pay.card.db.CardInformationStore;
import uk.gov.pay.card.db.loader.BinRangeDataLoader.BinRangeDataLoaderFactory;
import uk.gov.pay.card.model.CardInformation;
import uk.gov.pay.card.model.PrepaidStatus;

import java.net.URL;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

public class BinRangeDataLoaderTest {

    @Rule
    public final ExpectedException exception = ExpectedException.none();


    @Test
    public void shouldLoadWorldpayBinRangesFromFile() throws Exception {

        URL url = this.getClass().getResource("/worldpay/");
        BinRangeDataLoader worldpayBinRangeLoader = BinRangeDataLoaderFactory.worldpay(url.getFile());

        CardInformationStore cardInformationStore = mock(CardInformationStore.class);
        worldpayBinRangeLoader.loadDataTo(cardInformationStore);

        verify(cardInformationStore, times(5)).put(any(CardInformation.class));
    }

    @Test
    public void shouldLoadWorldpayBinRangesFromURL() throws Exception {

        URL url = this.getClass().getResource("/worldpay/worldpay-bin-ranges.csv");
        BinRangeDataLoader worldpayBinRangeLoader = BinRangeDataLoaderFactory.worldpay(url.toString());

        CardInformationStore cardInformationStore = mock(CardInformationStore.class);
        worldpayBinRangeLoader.loadDataTo(cardInformationStore);

        verify(cardInformationStore, times(5)).put(any(CardInformation.class));
    }

    @Test
    public void shouldLoadDiscoverBinRangesFromFile() throws Exception {

        URL url = this.getClass().getResource("/discover/");
        BinRangeDataLoader discoverBinRangeLoader = BinRangeDataLoaderFactory.discover(url.getFile());

        CardInformationStore cardInformationStore = mock(CardInformationStore.class);
        discoverBinRangeLoader.loadDataTo(cardInformationStore);

        verify(cardInformationStore, times(3)).put(any(CardInformation.class));
    }

    @Test
    public void shouldLoadDiscoverBinRangesFromURL() throws Exception {

        URL url = this.getClass().getResource("/discover/discover-bin-ranges.csv");
        BinRangeDataLoader discoverBinRangeLoader = BinRangeDataLoaderFactory.discover(url.toString());

        CardInformationStore cardInformationStore = mock(CardInformationStore.class);
        discoverBinRangeLoader.loadDataTo(cardInformationStore);

        verify(cardInformationStore, times(3)).put(any(CardInformation.class));
    }

    @Test
    public void shouldThrowExceptionWhenNoFileIsFound() throws Exception {

        URL url = this.getClass().getResource("/empty/");
        BinRangeDataLoader worldpayBinRangeLoader = BinRangeDataLoaderFactory.worldpay(url.getFile());

        CardInformationStore cardInformationStore = mock(CardInformationStore.class);
        exception.expect(BinRangeDataLoader.DataLoaderException.class);

        worldpayBinRangeLoader.loadDataTo(cardInformationStore);

        verifyZeroInteractions(cardInformationStore);
    }

    @Test
    public void shouldThrowExceptionWhenURLDoesNotExist() throws Exception {

        BinRangeDataLoader worldpayBinRangeLoader = BinRangeDataLoaderFactory.worldpay("file:///nonexistent/path");

        CardInformationStore cardInformationStore = mock(CardInformationStore.class);
        exception.expect(BinRangeDataLoader.DataLoaderException.class);

        worldpayBinRangeLoader.loadDataTo(cardInformationStore);

        verifyZeroInteractions(cardInformationStore);
    }

    @Test
    public void shouldThrowExceptionWhenMoreThanOneFileIsFound() throws Exception {

        URL url = this.getClass().getResource("/multiple-files/");
        BinRangeDataLoader worldpayBinRangeLoader = BinRangeDataLoaderFactory.worldpay(url.getFile());

        CardInformationStore cardInformationStore = mock(CardInformationStore.class);
        exception.expect(BinRangeDataLoader.DataLoaderException.class);

        worldpayBinRangeLoader.loadDataTo(cardInformationStore);

        verifyZeroInteractions(cardInformationStore);
    }

    @Test
    public void shouldLoadBinRangeDataAsCardInformationFromFile() throws Exception {
        URL url = this.getClass().getResource("/worldpay-single/");
        BinRangeDataLoader worldpayBinRangeLoader = BinRangeDataLoaderFactory.worldpay(url.getFile());

        CardInformationStore cardInformationStore = mock(CardInformationStore.class);
        worldpayBinRangeLoader.loadDataTo(cardInformationStore);

        CardInformation expectedCardInformation = new CardInformation("ELECTRON", "D", "ELECTRON", 511226111L, 511226200L, false, PrepaidStatus.PREPAID);
        verify(cardInformationStore).put(expectedCardInformation);
    }

    @Test
    public void shouldLoadBinRangeDataAsCardInformationFromURL() throws Exception {
        URL url = this.getClass().getResource("/worldpay-single/worldpay-bin-ranges-single.csv");
        BinRangeDataLoader worldpayBinRangeLoader = BinRangeDataLoaderFactory.worldpay(url.toString());

        CardInformationStore cardInformationStore = mock(CardInformationStore.class);
        worldpayBinRangeLoader.loadDataTo(cardInformationStore);

        CardInformation expectedCardInformation = new CardInformation("ELECTRON", "D", "ELECTRON", 511226111L, 511226200L, false, PrepaidStatus.PREPAID);
        verify(cardInformationStore).put(expectedCardInformation);
    }
}
