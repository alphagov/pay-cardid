package uk.gov.pay.card.db;


import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.pay.card.db.loader.BinRangeDataLoader;
import uk.gov.pay.card.model.CardInformation;
import uk.gov.pay.card.service.CardService;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;


public class RangeSetCardInformationStore implements CardInformationStore {

    private static final Logger LOGGER = LoggerFactory.getLogger(CardService.class);

    private final List<BinRangeDataLoader> binRangeLoaders;
    private final RangeSet<Long> rangeSet;
    private final ConcurrentHashMap<Range, CardInformation> store;

    public RangeSetCardInformationStore(List<BinRangeDataLoader> binRangeLoaders) {
        this.binRangeLoaders = binRangeLoaders;
        rangeSet = TreeRangeSet.create();
        store = new ConcurrentHashMap<>();
    }

    @Override
    public void initialiseCardInformation() throws Exception {
        for (BinRangeDataLoader loader : binRangeLoaders) {
            loader.loadDataTo(this);
        }
    }

    @Override
    public void put(CardInformation cardInformation) {
        cardInformation.updateRangeLength(CARD_RANGE_LENGTH);
        Range<Long> range = Range.closed(cardInformation.getMin(), cardInformation.getMax());
        rangeSet.add(range);
        store.put(range, cardInformation);
    }

    @Override
    public Optional<CardInformation> find(String prefix) {
        return validatePrefix(prefix)
                .map(rangeSet::rangeContaining)
                .map(store::get);
    }

    private Optional<Long> validatePrefix(String prefix) {
        try {
            return Optional.of(Long.valueOf(prefix));
        } catch (NumberFormatException e) {
            LOGGER.error("Received card number that cannot be parsed into long");
            return Optional.empty();
        }
    }

    @Override
    public void destroy() {
        rangeSet.clear();
        store.clear();
    }

    public String toString() {
        return "keys=" + store.size();
    }
}
