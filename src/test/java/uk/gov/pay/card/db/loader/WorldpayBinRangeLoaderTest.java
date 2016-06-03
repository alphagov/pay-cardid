package uk.gov.pay.card.db.loader;

import org.junit.Test;
import uk.gov.pay.card.db.CardInformationStore;
import uk.gov.pay.card.model.CardInformation;

import java.net.URL;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class WorldpayBinRangeLoaderTest {

    @Test
    public void shouldLoadWorldpayBinRangesFromFile() throws Exception {

        URL url = this.getClass().getResource("/worldpay-bin-ranges.csv");
        WorldpayBinRangeLoader worldpayBinRangeLoader = new WorldpayBinRangeLoader(url.getFile());

        CardInformationStore trieBasedCardInformationStore = mock(CardInformationStore.class);
        worldpayBinRangeLoader.loadDataTo(trieBasedCardInformationStore);

        verify(trieBasedCardInformationStore, times(3)).put(any(CardInformation.class));
    }


    @Test
    public void shouldLoadBinRangeDataAsCardInformation() throws Exception {
        URL url = this.getClass().getResource("/worldpay-bin-ranges-single.csv");
        WorldpayBinRangeLoader worldpayBinRangeLoader = new WorldpayBinRangeLoader(url.getFile());

        CardInformationStore trieBasedCardInformationStore = mock(CardInformationStore.class);
        worldpayBinRangeLoader.loadDataTo(trieBasedCardInformationStore);

        CardInformation expectedCardInformation = new CardInformation("ELECTRON","D","ELECTRON",511226111L, 511226200L);
        verify(trieBasedCardInformationStore).put(expectedCardInformation);

    }
}
