package uk.gov.pay.card.db;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pay.card.db.loader.BinRangeDataLoader;
import uk.gov.pay.card.model.CardType;
import uk.gov.pay.card.model.CardInformation;

import java.net.URL;
import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.pay.card.db.loader.BinRangeDataLoader.BinRangeDataLoaderFactory;

@ExtendWith(MockitoExtension.class)
public class RangeSetCardInformationStoreTest {

    private CardInformationStore cardInformationStore;

    @AfterEach
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

        URL url = this.getClass().getResource("/worldpay/worldpay-bin-ranges.csv");
        BinRangeDataLoader worldpayBinRangeLoader = BinRangeDataLoaderFactory.worldpay(url);
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

        URL url = this.getClass().getResource("/worldpay/worldpay-bin-ranges.csv");
        BinRangeDataLoader worldpayBinRangeLoader = BinRangeDataLoaderFactory.worldpay(url);
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
        URL url = this.getClass().getResource("/worldpay-6-digits/worldpay-bin-ranges-with-6digit-ranges.csv");
        BinRangeDataLoader worldpayBinRangeLoader = BinRangeDataLoaderFactory.worldpay(url);
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
        URL url = this.getClass().getResource("/worldpay-6-digits/worldpay-bin-ranges-with-6digit-ranges.csv");
        BinRangeDataLoader worldpayBinRangeLoader = BinRangeDataLoaderFactory.worldpay(url);
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

        URL url = this.getClass().getResource("/worldpay-9-digits/worldpay-bin-ranges-with-9digit-ranges.csv");
        BinRangeDataLoader worldpayBinRangeLoader = BinRangeDataLoaderFactory.worldpay(url);
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
        URL url = this.getClass().getResource("/worldpay/worldpay-bin-ranges.csv");
        BinRangeDataLoader worldpayBinRangeLoader = BinRangeDataLoaderFactory.worldpay(url);

        cardInformationStore = new RangeSetCardInformationStore(Collections.singletonList(worldpayBinRangeLoader));
        worldpayBinRangeLoader.loadDataTo(cardInformationStore);

        Optional<CardInformation> cardInformation = cardInformationStore.find("5101180000000007");

        assertThat(cardInformation.isPresent(), is(true));
        assertThat(cardInformation.orElseThrow().getBrand(), is("master-card"));
        assertThat(cardInformation.orElseThrow().getCardType(), is(CardType.DEBIT));
        assertThat(cardInformation.orElseThrow().getLabel(), is("MCI CREDIT"));
        assertThat(cardInformation.orElseThrow().isCorporate(), is(true));
    }

}
