package uk.gov.pay.card.db;

import org.junit.After;
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

    CardInformationStore cardInformationStore;

    @After
    public void tearDown() {
        cardInformationStore.destroy();
    }

    @Test
    public void shouldUseLoadersToInitialiseData() throws Exception {
        BinRangeLoader mockBinRangeLoader = mock(BinRangeLoader.class);

        cardInformationStore = new InfinispanCardInformationStore(mockBinRangeLoader);
        cardInformationStore.initialiseCardInformation();

        verify(mockBinRangeLoader).loadDataTo(cardInformationStore);
    }

    @Test
    public void shouldFindCardInformationForCardIdPrefix() throws Exception {
        URL url = this.getClass().getResource("/worldpay/");
        WorldpayBinRangeLoader worldpayBinRangeLoader = new WorldpayBinRangeLoader(url.getFile());
        cardInformationStore = new InfinispanCardInformationStore(worldpayBinRangeLoader);
        cardInformationStore.initialiseCardInformation();

        Optional<CardInformation> cardInformation = cardInformationStore.find("511948121");
        assertTrue(cardInformation.isPresent());
        assertThat(cardInformation.get().getBrand(), is("ELECTRON"));
        assertThat(cardInformation.get().getType(), is("D"));
        assertThat(cardInformation.get().getLabel(), is("ELECTRON"));
    }

    @Test
    public void shouldFindCardInformationWithRangeLengthLessThan9digits() throws Exception {
        URL url = this.getClass().getResource("/worldpay-6-digits/");
        WorldpayBinRangeLoader worldpayBinRangeLoader = new WorldpayBinRangeLoader(url.getFile());
        cardInformationStore = new InfinispanCardInformationStore(worldpayBinRangeLoader);
        cardInformationStore.initialiseCardInformation();

        Optional<CardInformation> cardInformation = cardInformationStore.find("511226764");
        assertTrue(cardInformation.isPresent());
        assertThat(cardInformation.get().getBrand(), is("ELECTRON"));
        assertThat(cardInformation.get().getType(), is("D"));
        assertThat(cardInformation.get().getLabel(), is("ELECTRON"));
    }

    @Test
    public void put_shouldUpdateRangeLengthTo9Digits() {
        BinRangeLoader mockBinRangeLoader = mock(BinRangeLoader.class);
        CardInformation cardInformation = mock(CardInformation.class);

        cardInformationStore = new InfinispanCardInformationStore(mockBinRangeLoader);
        cardInformationStore.put(cardInformation);

        verify(cardInformation).updateRangeLength(9);
    }
}
