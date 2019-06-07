package uk.gov.pay.card.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.pay.card.db.CardInformationStore;
import uk.gov.pay.card.model.CardInformation;

import java.util.Optional;

public class CardService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CardService.class);

    private final CardInformationStore cardInformationStore;

    public CardService(CardInformationStore cardInformationStore) {
        this.cardInformationStore = cardInformationStore;
    }

    public Optional<CardInformation> getCardInformation(String cardNumber) {
        if (cardNumber.length() < CardInformationStore.CARD_RANGE_LENGTH) {
            LOGGER.error("Received card number with fewer than {} characters", CardInformationStore.CARD_RANGE_LENGTH);
        }
        return cardInformationStore.find(cardNumber.substring(0, CardInformationStore.CARD_RANGE_LENGTH));
    }
}
