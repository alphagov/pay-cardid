package uk.gov.pay.card.db;


import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;
import uk.gov.pay.card.db.loader.BinRangeDataLoader;
import uk.gov.pay.card.model.CardInformation;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;


public class RangeSetCardInformationStore implements CardInformationStore {

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
        Range<Long> longRange = rangeSet.rangeContaining(Long.valueOf(prefix));
        if(longRange != null) {
            return Optional.ofNullable(store.get(longRange));
        }
        return Optional.empty();
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
