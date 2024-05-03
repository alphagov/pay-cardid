package uk.gov.pay.card.db;


import uk.gov.pay.card.model.CardInformation;

import java.util.Optional;

public interface CardInformationStore {

    int CARD_RANGE_LENGTH = 18;

    void initialiseCardInformation() throws Exception;

    boolean isReady();

    void put(CardInformation cardInformation);

    Optional<CardInformation> find(Long prefix);

    void destroy();
}
