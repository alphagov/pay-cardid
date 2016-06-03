package uk.gov.pay.card.db.loader;

import uk.gov.pay.card.db.CardInformationStore;

public interface BinRangeLoader {

    void loadDataTo(CardInformationStore store) throws DataLoaderException;

    class DataLoaderException extends Exception {

        DataLoaderException(String message, Throwable throwable) {
            super(message, throwable);
        }

        DataLoaderException(String message) {
            super(message);
        }
    }

}
