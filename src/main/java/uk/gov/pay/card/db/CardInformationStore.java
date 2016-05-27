package uk.gov.pay.card.db;


import uk.gov.pay.card.model.CardInformation;

import java.util.Optional;

public interface CardInformationStore {
    void initialiseCardInformation() throws Exception;

    int getStoreSize();

    void put(CardInformation cardInformation);

    Optional<CardInformation> find(String prefix);

    void destroy();
}
