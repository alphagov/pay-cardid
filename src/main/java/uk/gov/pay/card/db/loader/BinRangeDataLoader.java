package uk.gov.pay.card.db.loader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.pay.card.db.CardInformationStore;
import uk.gov.pay.card.model.CardInformation;
import uk.gov.pay.card.model.CardType;
import uk.gov.pay.card.model.PrepaidStatus;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

import static java.lang.String.format;
import static uk.gov.pay.card.db.CardInformationStore.CARD_RANGE_LENGTH;

public class BinRangeDataLoader {
    private static final long LOG_COUNT_EVERY_MS = 5000;

    private static final Logger logger = LoggerFactory.getLogger(BinRangeDataLoader.class);
    private final String name;
    private final URL source;
    private final String delimiter;
    private final String dataRowIdentifier;
    private final Function<String[], CardInformation> cardInformationExtractor;

    private static final String WORLDPAY_DELIMITER = ",";
    private static final String DISCOVER_DELIMITER = ",";
    private static final String TEST_CARD_DATA_DELIMITER = ",";
    private static final String WORLDPAY_ROW_IDENTIFIER = "01";
    private static final String DISCOVER_ROW_IDENTIFIER = "02";
    private static final String TEST_CARD_DATA_ROW_IDENTIFIER = "02";
    private static final String WORLDPAY_CORPORATE_CARD_MARKER = "CP";
    private static final String TEST_CARD_CORPORATE_CARD_MARKER = "CORPORATE";

    private static final Function<String[], CardInformation> WORLDPAY_CARD_INFORMATION_EXTRACTOR = entry -> {
        String label = entry[4];
        String brand = BinRangeParser.calculateCardBrand(label);
        CardType type = BinRangeParser.calculateCardType(entry[9]);
        Long minCardDigit = BinRangeParser.calculateMinDigitForCardLength(Long.valueOf(entry[1].substring(0, CARD_RANGE_LENGTH)), CARD_RANGE_LENGTH);
        Long maxCardDigit = BinRangeParser.calculateMaxDigitForCardLength(Long.valueOf(entry[2].substring(0, CARD_RANGE_LENGTH)), CARD_RANGE_LENGTH);
        PrepaidStatus prepaidStatus = BinRangeParser.calculateWorldpayPrepaidStatus(entry[9]);
        boolean corporate = WORLDPAY_CORPORATE_CARD_MARKER.equals(entry[3]);

        return new CardInformation(brand, type, label, minCardDigit, maxCardDigit, corporate, prepaidStatus);
    };

    private static final Function<String[], CardInformation> DISCOVER_CARD_INFORMATION_EXTRACTOR = entry -> {
        String label = entry[4];
        String brand = BinRangeParser.calculateCardBrand(label);
        CardType type = BinRangeParser.calculateCardType(entry[3]);
        Long minCardDigit = BinRangeParser.calculateMinDigitForCardLength(Long.valueOf(entry[1]), CARD_RANGE_LENGTH);
        Long maxCardDigit = BinRangeParser.calculateMaxDigitForCardLength(Long.valueOf(entry[2]), CARD_RANGE_LENGTH);

        return new CardInformation(brand, type, label, minCardDigit, maxCardDigit);
    };

    private static final Function<String[], CardInformation> TEST_CARD_DATA_INFORMATION_EXTRACTOR = entry -> {
        String label = entry[4];
        String brand = BinRangeParser.calculateCardBrand(label);
        CardType type = BinRangeParser.calculateCardType(entry[3]);
        Long minCardDigit = BinRangeParser.calculateMinDigitForCardLength(Long.valueOf(entry[1]), CARD_RANGE_LENGTH);
        Long maxCardDigit = BinRangeParser.calculateMaxDigitForCardLength(Long.valueOf(entry[2]), CARD_RANGE_LENGTH);
        PrepaidStatus prepaidStatus = BinRangeParser.calculateTestCardPrepaidStatus(entry[5]);
        boolean corporate = TEST_CARD_CORPORATE_CARD_MARKER.equals(entry[6]);

        return new CardInformation(brand, type, label, minCardDigit, maxCardDigit, corporate, prepaidStatus);
    };

    public static class BinRangeDataLoaderFactory {

        public static BinRangeDataLoader worldpay(URL source) {
            return new BinRangeDataLoader("Worldpay", source, WORLDPAY_DELIMITER, WORLDPAY_ROW_IDENTIFIER, WORLDPAY_CARD_INFORMATION_EXTRACTOR);
        }

        public static BinRangeDataLoader discover(URL source) {
            return new BinRangeDataLoader("Discover", source, DISCOVER_DELIMITER, DISCOVER_ROW_IDENTIFIER, DISCOVER_CARD_INFORMATION_EXTRACTOR);
        }

        public static BinRangeDataLoader testCards(URL source) {
            return new BinRangeDataLoader("Test Cards", source, TEST_CARD_DATA_DELIMITER, TEST_CARD_DATA_ROW_IDENTIFIER, TEST_CARD_DATA_INFORMATION_EXTRACTOR);
        }
    }

    private BinRangeDataLoader(String name, URL source, String delimiter, String dataRowIdentifier, Function<String[], CardInformation> cardInformationExtractor) {
        this.name = name;
        this.source = source;
        this.delimiter = delimiter;
        this.dataRowIdentifier = dataRowIdentifier;
        this.cardInformationExtractor = cardInformationExtractor;
    }

    public void loadDataTo(CardInformationStore cardInformationStore) throws DataLoaderException {
        logger.info("Loading {} data in to card information store", name);
        final AtomicLong lastPrintedCount = new AtomicLong(System.currentTimeMillis());
        final AtomicLong count = new AtomicLong(0);

        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(source.openStream()))) {
            bufferedReader
                    .lines()
                    .forEach(line -> {
                        String[] entry = line.split(this.delimiter);
                        if (this.dataRowIdentifier.equals(entry[0])) {
                            CardInformation cardInformation = cardInformationExtractor.apply(entry);
                            cardInformationStore.put(cardInformation);
                        }

                        long records = count.incrementAndGet();
                        if ((System.currentTimeMillis() - lastPrintedCount.get()) > LOG_COUNT_EVERY_MS) {
                            logger.info("{} records loaded...", records);
                            lastPrintedCount.set(System.currentTimeMillis());
                        }
                    });

            logger.info("{} records loaded... DONE", count);
        } catch (Exception e) {
            throw new DataLoaderException(format("Exception loading file at: %s", source == null ? "(source is null)" : source.toString()), e);
        }

        logger.info("Finished initialising the card information store - {}", cardInformationStore);
    }

    static class DataLoaderException extends Exception {
        DataLoaderException(String message, Throwable throwable) {
            super(message, throwable);
        }
    }
}
