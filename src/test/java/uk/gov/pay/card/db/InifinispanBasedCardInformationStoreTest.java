package uk.gov.pay.card.db;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.pay.card.db.loader.BinRangeLoader;
import uk.gov.pay.card.db.loader.WorldpayBinRangeLoader;
import uk.gov.pay.card.model.CardInformation;

import java.net.URL;
import java.util.Optional;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class InifinispanBasedCardInformationStoreTest {


    @Test
    public void shouldUseLoadersToInitialiseData() throws Exception {
        BinRangeLoader mockBinRangeLoader = mock(BinRangeLoader.class);

        CardInformationStore cardInformationStore = new InfinispanCardInformationStore(mockBinRangeLoader);
        cardInformationStore.initialiseCardInformation();

        verify(mockBinRangeLoader).loadDataTo(cardInformationStore);

        cardInformationStore.destroy();
    }

    @Test
    public void shouldFindCardInformationForCardIdPrefix() throws Exception {
        URL url = this.getClass().getResource("/worldpay-bin-ranges.csv");
        WorldpayBinRangeLoader worldpayBinRangeLoader = new WorldpayBinRangeLoader(url.getFile());
        CardInformationStore cardInformationStore = new InfinispanCardInformationStore(worldpayBinRangeLoader);
        cardInformationStore.initialiseCardInformation();

        Optional<CardInformation> cardInformation = cardInformationStore.find("511948121");
        assertTrue(cardInformation.isPresent());
        assertThat(cardInformation.get().getBrand(), is("ELECTRON"));
        assertThat(cardInformation.get().getType(), is("D"));
        assertThat(cardInformation.get().getLabel(), is("ELECTRON"));

        cardInformationStore.destroy();
    }

}
