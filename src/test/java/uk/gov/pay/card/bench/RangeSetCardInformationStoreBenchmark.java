package uk.gov.pay.card.bench;

import uk.gov.pay.card.db.RangeSetCardInformationStore;

import static java.util.Collections.singletonList;

public class RangeSetCardInformationStoreBenchmark extends CardInformationStoreBenchmark {
    public void setup() throws Exception {
        cardInformationStore = new RangeSetCardInformationStore(singletonList(getWorldpayBinRangeData()));
        cardInformationStore.initialiseCardInformation();
    }
}
