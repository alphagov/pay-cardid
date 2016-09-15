package uk.gov.pay.card.db;


import uk.gov.pay.card.model.CardInformation;

import java.util.Optional;

public interface CardInformationStore {

    int CARD_RANGE_LENGTH = 11;

    void initialiseCardInformation() throws Exception;

    void put(CardInformation cardInformation);

    Optional<CardInformation> find(String prefix);

    void destroy();
}
