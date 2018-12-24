package uk.gov.pay.card.service;

import uk.gov.pay.card.db.CardInformationStore;
import uk.gov.pay.card.model.CardInformation;

import java.util.Optional;

public class CardService {

    private final CardInformationStore cardInformationStore;

    public CardService(CardInformationStore cardInformationStore) {
        this.cardInformationStore = cardInformationStore;
    }

    public Optional<CardInformation> getCardInformation(String cardNumber) {
        return cardInformationStore.find(cardNumber);
    }
}
