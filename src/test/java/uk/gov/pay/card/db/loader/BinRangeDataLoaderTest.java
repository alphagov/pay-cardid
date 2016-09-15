package uk.gov.pay.card.db.loader;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import uk.gov.pay.card.db.CardInformationStore;
import uk.gov.pay.card.model.CardInformation;

import java.net.URL;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static uk.gov.pay.card.db.loader.BinRangeDataLoader.*;

public class BinRangeDataLoaderTest {

    @Rule
    public final ExpectedException exception = ExpectedException.none();


    @Test
    public void shouldLoadWorldpayBinRangesFromFile() throws Exception {

        URL url = this.getClass().getResource("/worldpay/");
        BinRangeDataLoader worldpayBinRangeLoader = BinRangeDataLoaderFactory.worldpay(url.getFile());

        CardInformationStore cardInformationStore = mock(CardInformationStore.class);
        worldpayBinRangeLoader.loadDataTo(cardInformationStore);

        verify(cardInformationStore, times(4)).put(any(CardInformation.class));
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
    public void shouldThrowExceptionWhenNoFileIsFound() throws Exception {

        URL url = this.getClass().getResource("/empty/");
        BinRangeDataLoader worldpayBinRangeLoader = BinRangeDataLoaderFactory.worldpay(url.getFile());

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
    public void shouldLoadBinRangeDataAsCardInformation() throws Exception {
        URL url = this.getClass().getResource("/worldpay-single/");
        BinRangeDataLoader worldpayBinRangeLoader = BinRangeDataLoaderFactory.worldpay(url.getFile());

        CardInformationStore cardInformationStore = mock(CardInformationStore.class);
        worldpayBinRangeLoader.loadDataTo(cardInformationStore);

        CardInformation expectedCardInformation = new CardInformation("ELECTRON","D","ELECTRON",511226111L, 511226200L);
        verify(cardInformationStore).put(expectedCardInformation);

    }
}
