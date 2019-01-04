package uk.gov.pay.card.db;

import org.junit.After;
import org.junit.Test;
import uk.gov.pay.card.db.loader.BinRangeDataLoader;
import uk.gov.pay.card.model.CardInformation;
import uk.gov.pay.card.model.CardType;

import java.net.URL;
import java.util.Collections;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static uk.gov.pay.card.db.loader.BinRangeDataLoader.BinRangeDataLoaderFactory;

public class TreeMapCardInformationStoreTest {

    private CardInformationStore cardInformationStore;

    @After
    public void tearDown() {
        if (cardInformationStore != null) cardInformationStore.destroy();
    }

    @Test
    public void shouldUseLoadersToInitialiseData() throws Exception {
        BinRangeDataLoader mockBinRangeLoader = mock(BinRangeDataLoader.class);

        cardInformationStore = new TreeMapCardInformationStore(Collections.singletonList(mockBinRangeLoader));
        cardInformationStore.initialiseCardInformation();

        verify(mockBinRangeLoader).loadDataTo(cardInformationStore);
    }

    @Test
    public void shouldFindCardInformationForCardIdPrefix() throws Exception {

        URL url = this.getClass().getResource("/worldpay/");
        BinRangeDataLoader worldpayBinRangeLoader = BinRangeDataLoaderFactory.worldpay(url.getFile());
        cardInformationStore = new TreeMapCardInformationStore(Collections.singletonList(worldpayBinRangeLoader));
        cardInformationStore.initialiseCardInformation();

        Optional<CardInformation> cardInformation = cardInformationStore.find("51194812198");
        assertTrue(cardInformation.isPresent());
        assertEquals("visa", cardInformation.get().getBrand());
        assertEquals(CardType.DEBIT, cardInformation.get().getCardType());
        assertEquals("ELECTRON", cardInformation.get().getLabel());
        assertFalse(cardInformation.get().isCorporate());
    }

    @Test
    public void shouldFindCardInformationForCardIdPrefixWith11Digits() throws Exception {

        URL url = this.getClass().getResource("/worldpay/");
        BinRangeDataLoader worldpayBinRangeLoader = BinRangeDataLoaderFactory.worldpay(url.getFile());
        cardInformationStore = new TreeMapCardInformationStore(Collections.singletonList(worldpayBinRangeLoader));
        cardInformationStore.initialiseCardInformation();

        Optional<CardInformation> cardInformation = cardInformationStore.find("51194912333");
        assertTrue(cardInformation.isPresent());
        assertEquals("discover", cardInformation.get().getBrand());
        assertEquals(CardType.DEBIT, cardInformation.get().getCardType());
        assertEquals("DISCOVER", cardInformation.get().getLabel());
        assertFalse(cardInformation.get().isCorporate());
    }

    @Test
    public void shouldFindCardInformationWithRangeLengthLessThan9digits() throws Exception {
        URL url = this.getClass().getResource("/worldpay-6-digits/");
        BinRangeDataLoader worldpayBinRangeLoader = BinRangeDataLoaderFactory.worldpay(url.getFile());
        cardInformationStore = new TreeMapCardInformationStore(Collections.singletonList(worldpayBinRangeLoader));
        cardInformationStore.initialiseCardInformation();

        Optional<CardInformation> cardInformation = cardInformationStore.find("51122676499");
        assertTrue(cardInformation.isPresent());
        assertEquals("visa", cardInformation.get().getBrand());
        assertEquals(CardType.DEBIT, cardInformation.get().getCardType());
        assertEquals("ELECTRON", cardInformation.get().getLabel());
        assertFalse(cardInformation.get().isCorporate());
    }

    @Test
    public void shouldFindCardInformationWithRange6digitsWhenRangeMinAndMaxAreTheSame() throws Exception {
        URL url = this.getClass().getResource("/worldpay-6-digits/");
        BinRangeDataLoader worldpayBinRangeLoader = BinRangeDataLoaderFactory.worldpay(url.getFile());
        cardInformationStore = new TreeMapCardInformationStore(Collections.singletonList(worldpayBinRangeLoader));
        cardInformationStore.initialiseCardInformation();

        Optional<CardInformation> cardInformation = cardInformationStore.find("53333699999");
        assertTrue(cardInformation.isPresent());
        assertEquals("visa", cardInformation.get().getBrand());
        assertEquals(CardType.DEBIT, cardInformation.get().getCardType());
        assertEquals("ELECTRON", cardInformation.get().getLabel());
        assertFalse(cardInformation.get().isCorporate());
    }

    @Test
    public void shouldFindCardInformationWithRange9digitsWhenRangeMinAndMaxAreTheSame() throws Exception {

        URL url = this.getClass().getResource("/worldpay-9-digits/");
        BinRangeDataLoader worldpayBinRangeLoader = BinRangeDataLoaderFactory.worldpay(url.getFile());
        cardInformationStore = new TreeMapCardInformationStore(Collections.singletonList(worldpayBinRangeLoader));
        cardInformationStore.initialiseCardInformation();

        Optional<CardInformation> cardInformation = cardInformationStore.find("53333333699");
        assertTrue(cardInformation.isPresent());
        assertEquals("visa", cardInformation.get().getBrand());
        assertEquals(CardType.DEBIT, cardInformation.get().getCardType());
        assertEquals("ELECTRON", cardInformation.get().getLabel());
        assertFalse(cardInformation.get().isCorporate());
    }

    @Test
    public void shouldTransformMastercardBrand() throws Exception {
        URL url = this.getClass().getResource("/worldpay/");
        BinRangeDataLoader worldpayBinRangeLoader = BinRangeDataLoaderFactory.worldpay(url.getFile());

        cardInformationStore = new TreeMapCardInformationStore(Collections.singletonList(worldpayBinRangeLoader));
        worldpayBinRangeLoader.loadDataTo(cardInformationStore);

        Optional<CardInformation> cardInformation = cardInformationStore.find("51122666111");

        assertTrue(cardInformation.isPresent());
        assertEquals("master-card", cardInformation.get().getBrand());

    }

    @Test
    public void shouldFindCorporateCreditCardType() throws Exception {
        URL url = this.getClass().getResource("/worldpay/");
        BinRangeDataLoader worldpayBinRangeLoader = BinRangeDataLoaderFactory.worldpay(url.getFile());

        cardInformationStore = new TreeMapCardInformationStore(Collections.singletonList(worldpayBinRangeLoader));
        worldpayBinRangeLoader.loadDataTo(cardInformationStore);

        Optional<CardInformation> cardInformation = cardInformationStore.find("5101180000000007");

        assertTrue(cardInformation.isPresent());
        assertEquals("master-card", cardInformation.get().getBrand());
        assertEquals(CardType.DEBIT, cardInformation.get().getCardType());
        assertEquals("MCI CREDIT", cardInformation.get().getLabel());
        assertTrue(cardInformation.get().isCorporate());
    }

    @Test
    public void canPadPANsCorrectly() {
        assertEquals("1000000000000000000", TreeMapCardInformationStore.padPAN(TreeMapCardInformationStore.LOW_VALUE_TEMPLATE, "1"));
        assertEquals("1234000000000000000", TreeMapCardInformationStore.padPAN(TreeMapCardInformationStore.LOW_VALUE_TEMPLATE, "1234"));
        assertEquals("12345678901234567890", TreeMapCardInformationStore.padPAN(TreeMapCardInformationStore.LOW_VALUE_TEMPLATE, "12345678901234567890"));
        assertEquals("1999999999999999999", TreeMapCardInformationStore.padPAN(TreeMapCardInformationStore.HIGH_VALUE_TEMPLATE, "1"));
        assertEquals("1234999999999999999", TreeMapCardInformationStore.padPAN(TreeMapCardInformationStore.HIGH_VALUE_TEMPLATE, "1234"));
        assertEquals("12345678901234567890", TreeMapCardInformationStore.padPAN(TreeMapCardInformationStore.HIGH_VALUE_TEMPLATE, "12345678901234567890"));
    }

    @Test
    public void canStringify() throws BinRangeDataLoader.DataLoaderException {
        URL url = this.getClass().getResource("/worldpay/");
        BinRangeDataLoader worldpayBinRangeLoader = BinRangeDataLoaderFactory.worldpay(url.getFile());

        cardInformationStore = new TreeMapCardInformationStore(Collections.singletonList(worldpayBinRangeLoader));
        worldpayBinRangeLoader.loadDataTo(cardInformationStore);

        assertEquals("keys=5", cardInformationStore.toString());
    }

    private CardInformation createCardRange(long low, long high) {
        return new CardInformation("master-card", "C", "label", low, high);
    }

    @Test
    public void emptyStoreHasNoOverlappingEntries() {
        TreeMapCardInformationStore store = new TreeMapCardInformationStore(null);
        assertNull(store.getOverlappingEntry(createCardRange(0L, 9L)));
    }

    @Test
    public void ignoresNonOverlappingLowRange() {
        TreeMapCardInformationStore store = new TreeMapCardInformationStore(null);
        store.put(createCardRange(4L, 5L));
        assertNull(store.getOverlappingEntry(createCardRange(6L, 7L)));
    }

    @Test
    public void ignoresNonOverlappingHighRange() {
        TreeMapCardInformationStore store = new TreeMapCardInformationStore(null);
        store.put(createCardRange(4L, 5L));
        assertNull(store.getOverlappingEntry(createCardRange(2L, 3L)));
    }

    @Test
    public void canDetectLowSideOverlap() {
        TreeMapCardInformationStore store = new TreeMapCardInformationStore(null);
        store.put(createCardRange(4L, 6L));
        assertNotNull(store.getOverlappingEntry(createCardRange(5L, 7L)));
    }

    @Test
    public void canDetectHighSideOverlap() {
        TreeMapCardInformationStore store = new TreeMapCardInformationStore(null);
        store.put(createCardRange(6L, 7L));
        assertNotNull(store.getOverlappingEntry(createCardRange(5L, 7L)));
    }

    @Test
    public void canDetectEnclosedRange() {
        TreeMapCardInformationStore store = new TreeMapCardInformationStore(null);
        store.put(createCardRange(6L,8L));
        assertNotNull(store.getOverlappingEntry(createCardRange(5L, 6L)));
    }

    @Test
    public void canDetectEnclosedRangeBeingAdded() {
        TreeMapCardInformationStore store = new TreeMapCardInformationStore(null);
        store.put(createCardRange(6L,8L));
        assertNotNull(store.getOverlappingEntry(createCardRange(5L, 6L)));
    }

    @Test
    public void canDetectEnclosingRangeBeingAdded() {
        TreeMapCardInformationStore store = new TreeMapCardInformationStore(null);
        store.put(createCardRange(5L,6L));
        assertNotNull(store.getOverlappingEntry(createCardRange(4L, 8L)));
    }
}
