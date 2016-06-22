package uk.gov.pay.card.db.loader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.pay.card.db.CardInformationStore;
import uk.gov.pay.card.model.CardInformation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

public class BinRangeDataLoader {
    private final long LOG_COUNT_EVERY_MS = 5000;

    private static final Logger logger = LoggerFactory.getLogger(BinRangeDataLoader.class);
    private String name;
    private String filePath;
    private String delimeter;
    private String dataRowIdentifier;
    private Function<String[], CardInformation> cardInformationExtractor;

    private static final String WORLDPAY_DELIMITER = "!";
    private static final String DISCOVER_DELIMITER = ",";
    private static final String TEST_CARD_DATA_DELIMITER = ",";
    private static final String WORLDPAY_ROW_IDENTIFIER = "05";
    private static final String DISCOVER_ROW_IDENTIFIER = "02";
    private static final String TEST_CARD_DATA_ROW_IDENTIFIER = "02";

    private static final Function<String[], CardInformation> WORLDPAY_CARD_INFORMATION_EXTRACTOR = entry -> new CardInformation(
            entry[4], entry[8], entry[4], Long.valueOf(entry[1]), Long.valueOf(entry[2]));

    private static final Function<String[], CardInformation> DISCOVER_CARD_INFORMATION_EXTRACTOR = entry -> new CardInformation(
            entry[4], entry[3], entry[4], Long.valueOf(entry[1]), Long.valueOf(entry[2]));

    private static final Function<String[], CardInformation> TEST_CARD_DATA_INFORMATION_EXTRACTOR = entry -> new CardInformation(
            entry[4], entry[3], entry[4], Long.valueOf(entry[1]), Long.valueOf(entry[2]));

    public static class BinRangeDataLoaderFactory {

        public static BinRangeDataLoader worldpay(String filePath) {
           return new BinRangeDataLoader("Worldpay", filePath, WORLDPAY_DELIMITER, WORLDPAY_ROW_IDENTIFIER, WORLDPAY_CARD_INFORMATION_EXTRACTOR);
        }

        public static BinRangeDataLoader discover(String filePath) {
            return new BinRangeDataLoader("Discover", filePath, DISCOVER_DELIMITER, DISCOVER_ROW_IDENTIFIER, DISCOVER_CARD_INFORMATION_EXTRACTOR);
        }

        public static BinRangeDataLoader testCards(String filePath) {
            return new BinRangeDataLoader("Test Cards", filePath, TEST_CARD_DATA_DELIMITER, TEST_CARD_DATA_ROW_IDENTIFIER, TEST_CARD_DATA_INFORMATION_EXTRACTOR);
        }
    }

    private BinRangeDataLoader(String name, String filePath, String delimeter, String dataRowIdentifier, Function<String[], CardInformation> cardInformationExtractor) {
        this.name = name;
        this.filePath = filePath;
        this.delimeter = delimeter;
        this.dataRowIdentifier = dataRowIdentifier;
        this.cardInformationExtractor = cardInformationExtractor;
    }

    public void loadDataTo(CardInformationStore cardInformationStore) throws DataLoaderException {
        logger.info("Loading " + name + " data in to card information store");
        final AtomicLong lastPrintedCount = new AtomicLong(System.currentTimeMillis());
        final AtomicLong count = new AtomicLong(0);

        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(getBinRangeFile())))) {
            bufferedReader
                    .lines()
                    .forEach(line -> {
                        String[] entry = line.split(this.delimeter);
                        if (this.dataRowIdentifier.equals(entry[0])) {
                            CardInformation cardInformation = cardInformationExtractor.apply(entry);
                            cardInformationStore.put(cardInformation);
                        }

                        long records = count.incrementAndGet();
                        if ((System.currentTimeMillis() - lastPrintedCount.get()) > LOG_COUNT_EVERY_MS) {
                            logger.info(records + " records loaded...");
                            lastPrintedCount.set(System.currentTimeMillis());
                        }
                    });

            logger.info(count + " records loaded... DONE");

        } catch (Exception e) {
            throw new DataLoaderException("Exception loading file at:" + filePath, e);
        }

        logger.info("Finished initialising the card information store - {}", cardInformationStore);

    }

    private File getBinRangeFile() throws DataLoaderException {
        File folder = new File(filePath);
        File[] matchingFiles = folder.listFiles((dir, name) -> {
            return name.toLowerCase().endsWith("csv");
        });

        validateFiles(matchingFiles);

        return matchingFiles[0];
    }

    private void validateFiles(File[] matchingFiles) throws DataLoaderException {
        if (matchingFiles.length != 1) {
            String message = null;

            if (matchingFiles.length == 0) message = "No CSV found at " + filePath;
            if (matchingFiles.length > 1)  message = "More than one CSV found at " + filePath;

            logger.error(message);
            throw new DataLoaderException(message);
        }

    }

    class DataLoaderException extends Exception {

        DataLoaderException(String message, Throwable throwable) {
            super(message, throwable);
        }

        DataLoaderException(String message) {
            super(message);
        }
    }

}
