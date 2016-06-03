package uk.gov.pay.card.db.loader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.pay.card.db.CardInformationStore;
import uk.gov.pay.card.model.CardInformation;

import java.io.*;

public class WorldpayBinRangeLoader implements BinRangeLoader {

    private static final Logger logger = LoggerFactory.getLogger(WorldpayBinRangeLoader.class);
    private static final String WORLDPAY_BIN_RANGE_FILE_DELIMITTER = "!";
    private static final String BIN_DETAILS_IDENTIFIER = "05";

    private final String fileWithBINRanges;

    public WorldpayBinRangeLoader(String fileWithBINRanges) {
        this.fileWithBINRanges = fileWithBINRanges;
    }

    @Override
    public void loadDataTo(CardInformationStore store) throws DataLoaderException {
        logger.info("Setting up card information store");

        try ( BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(fileWithBINRanges))))) {
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
            throw new DataLoaderException("Exception loading file at:"+ fileWithBINRanges, e);
        }

        logger.info("Finished initialising the card information store - {}", store);

    }
}
