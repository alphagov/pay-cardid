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
    private boolean loaded;

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
        loaded = true;
    }

    @Override public boolean isReady() {
        return loaded;
    }

    @Override
    public void put(CardInformation cardInformation) {
        Range<Long> range = Range.closed(cardInformation.getMin(), cardInformation.getMax());
        rangeSet.add(range);
        store.put(range, cardInformation);
    }

    @Override
    public Optional<CardInformation> find(Long prefix) {
        return Optional.ofNullable(rangeSet.rangeContaining(prefix)).map(store::get);
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
