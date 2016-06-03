package uk.gov.pay.card.db.loader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.pay.card.db.CardInformationStore;
import uk.gov.pay.card.model.CardInformation;

import java.io.*;
import java.util.Optional;
import java.util.function.Consumer;

public class WorldpayBinRangeLoader implements BinRangeLoader {

    private static final Logger logger = LoggerFactory.getLogger(WorldpayBinRangeLoader.class);
    private static final String WORLDPAY_BIN_RANGE_FILE_DELIMITTER = "!";
    private static final String BIN_DETAILS_IDENTIFIER = "05";

    private final String binRangesFilePath;
    private String binRangeFile;

    public WorldpayBinRangeLoader(String binRangesFilePath) {
        this.binRangesFilePath = binRangesFilePath;
    }

    @Override
    public void loadDataTo(CardInformationStore store) throws DataLoaderException {
        logger.info("Setting up card information store");

        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(getBinRangeFile())))) {
            bufferedReader
                    .lines()
                    .forEach(line -> {
                        String[] entry = line.split(WORLDPAY_BIN_RANGE_FILE_DELIMITTER);
                        if (BIN_DETAILS_IDENTIFIER.equals(entry[0])) {
                            CardInformation cardInformation = new CardInformation(
                                    entry[4], entry[8], entry[4], Long.valueOf(entry[1]), Long.valueOf(entry[2]));
                            store.put(cardInformation);
                        }

                    });

        } catch (Exception e) {
            throw new DataLoaderException("Exception loading file at:" + binRangesFilePath, e);
        }

        logger.info("Finished initialising the card information store - {}", store);

    }

    private File getBinRangeFile() throws DataLoaderException {
        File folder = new File(binRangesFilePath);
        File[] matchingFiles = folder.listFiles((dir, name) -> {
            return name.toLowerCase().endsWith("csv");
        });

        validateFiles(matchingFiles);

        return matchingFiles[0];
    }

    private void validateFiles(File[] matchingFiles) throws DataLoaderException {
        if (matchingFiles.length != 1) {
            String message = null;

            if (matchingFiles.length == 0) message = "No WorldPay CSV found at " + binRangesFilePath;
            if (matchingFiles.length > 1)  message = "More than one WorldPay CSV found at " + binRangesFilePath;

            logger.error(message);
            throw new DataLoaderException(message);
        }

    }
}
