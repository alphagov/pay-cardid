package uk.gov.pay.card.db.loader;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import uk.gov.pay.card.db.CardInformationStore;
import uk.gov.pay.card.model.CardInformation;

import java.net.URL;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class WorldpayBinRangeLoaderTest {

    @Rule
    public final ExpectedException exception = ExpectedException.none();


    @Test
    public void shouldLoadWorldpayBinRangesFromFile() throws Exception {

        URL url = this.getClass().getResource("/worldpay/");
        WorldpayBinRangeLoader worldpayBinRangeLoader = new WorldpayBinRangeLoader(url.getFile());

        CardInformationStore cardInformationStore = mock(CardInformationStore.class);
        worldpayBinRangeLoader.loadDataTo(cardInformationStore);

        verify(cardInformationStore, times(3)).put(any(CardInformation.class));
    }

    @Test
    public void shouldThrowExceptionWhenNoFileIsFound() throws Exception {

        URL url = this.getClass().getResource("/empty/");
        WorldpayBinRangeLoader worldpayBinRangeLoader = new WorldpayBinRangeLoader(url.getFile());

        CardInformationStore cardInformationStore = mock(CardInformationStore.class);
        exception.expect(BinRangeLoader.DataLoaderException.class);

        worldpayBinRangeLoader.loadDataTo(cardInformationStore);

        verifyZeroInteractions(cardInformationStore);
    }

    @Test
    public void shouldThrowExceptionWhenMoreThanOneFileIsFound() throws Exception {

        URL url = this.getClass().getResource("/multiple-files/");
        WorldpayBinRangeLoader worldpayBinRangeLoader = new WorldpayBinRangeLoader(url.getFile());

        CardInformationStore cardInformationStore = mock(CardInformationStore.class);
        exception.expect(BinRangeLoader.DataLoaderException.class);

        worldpayBinRangeLoader.loadDataTo(cardInformationStore);

        verifyZeroInteractions(cardInformationStore);
    }


    @Test
    public void shouldLoadBinRangeDataAsCardInformation() throws Exception {
        URL url = this.getClass().getResource("/worldpay-single/");
        WorldpayBinRangeLoader worldpayBinRangeLoader = new WorldpayBinRangeLoader(url.getFile());

        CardInformationStore cardInformationStore = mock(CardInformationStore.class);
        worldpayBinRangeLoader.loadDataTo(cardInformationStore);

        CardInformation expectedCardInformation = new CardInformation("ELECTRON","D","ELECTRON",511226111L, 511226200L);
        verify(cardInformationStore).put(expectedCardInformation);

    }
}
