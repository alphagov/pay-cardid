package uk.gov.pay.card.db;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.pay.card.db.loader.BinRangeDataLoader;
import uk.gov.pay.card.model.CardType;
import uk.gov.pay.card.model.CardInformation;

import java.net.URL;
import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.pay.card.db.loader.BinRangeDataLoader.BinRangeDataLoaderFactory;

@RunWith(MockitoJUnitRunner.class)
public class RangeSetCardInformationStoreTest {

    private CardInformationStore cardInformationStore;

    @After
    public void tearDown() {
        cardInformationStore.destroy();
    }

    @Test
    public void shouldUseLoadersToInitialiseData() throws Exception {
        BinRangeDataLoader mockBinRangeLoader = mock(BinRangeDataLoader.class);

        cardInformationStore = new RangeSetCardInformationStore(Collections.singletonList(mockBinRangeLoader));
        cardInformationStore.initialiseCardInformation();

        verify(mockBinRangeLoader).loadDataTo(cardInformationStore);
    }

    @Test
    public void shouldFindCardInformationForCardIdPrefix() throws Exception {

        URL url = this.getClass().getResource("/worldpay/");
        BinRangeDataLoader worldpayBinRangeLoader = BinRangeDataLoaderFactory.worldpay(url.getFile());
        cardInformationStore = new RangeSetCardInformationStore(Collections.singletonList(worldpayBinRangeLoader));
        cardInformationStore.initialiseCardInformation();

        Optional<CardInformation> cardInformation = cardInformationStore.find("51194812198");
        assertTrue(cardInformation.isPresent());
        assertThat(cardInformation.get().getBrand(), is("visa"));
        assertThat(cardInformation.get().getCardType(), is(CardType.DEBIT));
        assertThat(cardInformation.get().getLabel(), is("ELECTRON"));
        assertThat(cardInformation.get().isCorporate(), is(false));
    }

    @Test
    public void shouldFindCardInformationForCardIdPrefixWith11Digits() throws Exception {

        URL url = this.getClass().getResource("/worldpay/");
        BinRangeDataLoader worldpayBinRangeLoader = BinRangeDataLoaderFactory.worldpay(url.getFile());
        cardInformationStore = new RangeSetCardInformationStore(Collections.singletonList(worldpayBinRangeLoader));
        cardInformationStore.initialiseCardInformation();

        Optional<CardInformation> cardInformation = cardInformationStore.find("51194912333");
        assertTrue(cardInformation.isPresent());
        assertThat(cardInformation.get().getBrand(), is("discover"));
        assertThat(cardInformation.get().getCardType(), is(CardType.DEBIT));
        assertThat(cardInformation.get().getLabel(), is("DISCOVER"));
        assertThat(cardInformation.get().isCorporate(), is(false));
    }

    @Test
    public void shouldFindCardInformationWithRangeLengthLessThan9digits() throws Exception {
        URL url = this.getClass().getResource("/worldpay-6-digits/");
        BinRangeDataLoader worldpayBinRangeLoader = BinRangeDataLoaderFactory.worldpay(url.getFile());
        cardInformationStore = new RangeSetCardInformationStore(Collections.singletonList(worldpayBinRangeLoader));
        cardInformationStore.initialiseCardInformation();

        Optional<CardInformation> cardInformation = cardInformationStore.find("51122676499");
        assertTrue(cardInformation.isPresent());
        assertThat(cardInformation.get().getBrand(), is("visa"));
        assertThat(cardInformation.get().getCardType(), is(CardType.DEBIT));
        assertThat(cardInformation.get().getLabel(), is("ELECTRON"));
        assertThat(cardInformation.get().isCorporate(), is(false));
    }

    @Test
    public void shouldFindCardInformationWithRange6digitsWhenRangeMinAndMaxAreTheSame() throws Exception {
        URL url = this.getClass().getResource("/worldpay-6-digits/");
        BinRangeDataLoader worldpayBinRangeLoader = BinRangeDataLoaderFactory.worldpay(url.getFile());
        cardInformationStore = new RangeSetCardInformationStore(Collections.singletonList(worldpayBinRangeLoader));
        cardInformationStore.initialiseCardInformation();

        Optional<CardInformation> cardInformation = cardInformationStore.find("53333699999");
        assertTrue(cardInformation.isPresent());
        assertThat(cardInformation.get().getBrand(), is("visa"));
        assertThat(cardInformation.get().getCardType(), is(CardType.DEBIT));
        assertThat(cardInformation.get().getLabel(), is("ELECTRON"));
        assertThat(cardInformation.get().isCorporate(), is(false));
    }

    @Test
    public void shouldFindCardInformationWithRange9digitsWhenRangeMinAndMaxAreTheSame() throws Exception {

        URL url = this.getClass().getResource("/worldpay-9-digits/");
        BinRangeDataLoader worldpayBinRangeLoader = BinRangeDataLoaderFactory.worldpay(url.getFile());
        cardInformationStore = new RangeSetCardInformationStore(Collections.singletonList(worldpayBinRangeLoader));
        cardInformationStore.initialiseCardInformation();

        Optional<CardInformation> cardInformation = cardInformationStore.find("53333333699");
        assertTrue(cardInformation.isPresent());
        assertThat(cardInformation.get().getBrand(), is("visa"));
        assertThat(cardInformation.get().getCardType(), is(CardType.DEBIT));
        assertThat(cardInformation.get().getLabel(), is("ELECTRON"));
        assertThat(cardInformation.get().isCorporate(), is(false));
    }

    @Test
    public void put_shouldUpdateRangeLengthTo11Digits() {

        BinRangeDataLoader mockBinRangeLoader = mock(BinRangeDataLoader.class);
        CardInformation cardInformation = mock(CardInformation.class);

        when(cardInformation.getMin()).thenReturn(1L);
        when(cardInformation.getMax()).thenReturn(19L);

        cardInformationStore = new RangeSetCardInformationStore(Collections.singletonList(mockBinRangeLoader));
        cardInformationStore.put(cardInformation);

        verify(cardInformation).updateRangeLength(11);
    }

    @Test
    public void shouldFindCorporateCreditCardType() throws Exception {
        URL url = this.getClass().getResource("/worldpay/");
        BinRangeDataLoader worldpayBinRangeLoader = BinRangeDataLoaderFactory.worldpay(url.getFile());

        cardInformationStore = new RangeSetCardInformationStore(Collections.singletonList(worldpayBinRangeLoader));
        worldpayBinRangeLoader.loadDataTo(cardInformationStore);

        Optional<CardInformation> cardInformation = cardInformationStore.find("5101180000000007");

        assertThat(cardInformation.isPresent(), is(true));
        assertThat(cardInformation.get().getBrand(), is("master-card"));
        assertThat(cardInformation.get().getCardType(), is(CardType.DEBIT));
        assertThat(cardInformation.get().getLabel(), is("MCI CREDIT"));
        assertThat(cardInformation.get().isCorporate(), is(true));
    }

}
