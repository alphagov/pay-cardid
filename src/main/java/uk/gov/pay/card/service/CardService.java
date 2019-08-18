package uk.gov.pay.card.service;

import uk.gov.pay.card.db.CardInformationStore;
import uk.gov.pay.card.model.CardInformation;

import java.util.Optional;

import static uk.gov.pay.card.db.CardInformationStore.CARD_RANGE_LENGTH;

public class CardService {

    private final CardInformationStore cardInformationStore;

    public CardService(CardInformationStore cardInformationStore) {
        this.cardInformationStore = cardInformationStore;
    }

    public Optional<CardInformation> getCardInformation(String cardNumber) {
        return cardInformationStore.find(cardNumber.length() > CARD_RANGE_LENGTH
                ? cardNumber.substring(0, CARD_RANGE_LENGTH)
                : cardNumber);
    }

}
