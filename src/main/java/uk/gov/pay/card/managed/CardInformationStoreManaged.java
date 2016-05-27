package uk.gov.pay.card.managed;

import io.dropwizard.lifecycle.Managed;
import uk.gov.pay.card.db.CardInformationStore;

public class CardInformationStoreManaged implements Managed {

    private final CardInformationStore cardInformationStore;

    public CardInformationStoreManaged(CardInformationStore cardInformationStore) {
        this.cardInformationStore = cardInformationStore;
    }

    @Override
    public void start() throws Exception {
        cardInformationStore.initialiseCardInformation();
    }

    @Override
    public void stop() throws Exception {
        cardInformationStore.destroy();
    }
}
