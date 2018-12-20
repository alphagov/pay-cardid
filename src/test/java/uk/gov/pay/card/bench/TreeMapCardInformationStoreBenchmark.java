package uk.gov.pay.card.bench;

import uk.gov.pay.card.db.TreeMapCardInformationStore;

import static java.util.Collections.singletonList;

public class TreeMapCardInformationStoreBenchmark extends CardInformationStoreBenchmark {

    @Override
    public void setup() throws Exception {
        cardInformationStore = new TreeMapCardInformationStore(singletonList(getWorldpayBinRangeData()));
        cardInformationStore.initialiseCardInformation();
    }
}
