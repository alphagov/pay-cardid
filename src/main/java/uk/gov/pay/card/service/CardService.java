package uk.gov.pay.card.service;

import uk.gov.pay.card.db.CardInformationStore;
import uk.gov.pay.card.model.CardInformation;

import java.util.Optional;

import static java.lang.String.format;
import static uk.gov.pay.card.db.CardInformationStore.CARD_RANGE_LENGTH;

public class CardService {

    private final CardInformationStore cardInformationStore;

    public CardService(CardInformationStore cardInformationStore) {
        this.cardInformationStore = cardInformationStore;
    }

    public Optional<CardInformation> getCardInformation(Long cardNumber) {
        return cardInformationStore.find(Long.valueOf(format("%-" + CARD_RANGE_LENGTH + "d", cardNumber).replace(" ", "0")));
    }

}
