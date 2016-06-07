package uk.gov.pay.card.db.loader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.pay.card.db.CardInformationStore;
import uk.gov.pay.card.model.CardInformation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.function.Function;

public class BinRangeDataLoader {

    private static final Logger logger = LoggerFactory.getLogger(BinRangeDataLoader.class);
    private String filePath;
    private String delimeter;
    private String dataRowIdentifier;
    private Function<String[], CardInformation> cardInformationExtractor;

    private static final String WORLDPAY_DELIMITER = "!";
    private static final String DISCOVER_DELIMITER = ",";
    private static final String WORLDPAY_ROW_IDENTIFIER = "05";
    private static final String DISCOVER_ROW_IDENTIFIER = "02";

    private static final Function<String[], CardInformation> WORLDPAY_CARD_INFORMATION_EXTRACTOR = entry -> new CardInformation(
            entry[4], entry[8], entry[4], Long.valueOf(entry[1]), Long.valueOf(entry[2]));

    private static final Function<String[], CardInformation> DISCOVER_CARD_INFORMATION_EXTRACTOR = entry -> new CardInformation(
            entry[4], entry[3], entry[4], Long.valueOf(entry[1]), Long.valueOf(entry[2]));


    public static class BinRangeDataLoaderFactory {

        public static BinRangeDataLoader worldpay(String filePath) {
           return new BinRangeDataLoader(filePath, WORLDPAY_DELIMITER, WORLDPAY_ROW_IDENTIFIER, WORLDPAY_CARD_INFORMATION_EXTRACTOR);
        }

        public static BinRangeDataLoader discover(String filePath) {
            return new BinRangeDataLoader(filePath, DISCOVER_DELIMITER, DISCOVER_ROW_IDENTIFIER, DISCOVER_CARD_INFORMATION_EXTRACTOR);
        }
    }

    private BinRangeDataLoader(String filePath, String delimeter, String dataRowIdentifier, Function<String[], CardInformation> cardInformationExtractor) {

        this.filePath = filePath;
        this.delimeter = delimeter;
        this.dataRowIdentifier = dataRowIdentifier;
        this.cardInformationExtractor = cardInformationExtractor;
    }

    public void loadDataTo(CardInformationStore cardInformationStore) throws DataLoaderException {
        logger.info("Setting up card information store");

        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(getBinRangeFile())))) {
            bufferedReader
                    .lines()
                    .forEach(line -> {
                        String[] entry = line.split(this.delimeter);
                        if (this.dataRowIdentifier.equals(entry[0])) {
                            CardInformation cardInformation = cardInformationExtractor.apply(entry);
                            cardInformationStore.put(cardInformation);
                        }

                    });

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
