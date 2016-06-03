package uk.gov.pay.card.db;


import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.query.Search;
import org.infinispan.query.dsl.Query;
import org.infinispan.query.dsl.QueryFactory;
import uk.gov.pay.card.db.loader.BinRangeLoader;
import uk.gov.pay.card.model.CardInformation;

import java.util.List;
import java.util.Optional;


public class InfinispanCardInformationStore implements CardInformationStore {

    private final BinRangeLoader binRangeLoader;
    Cache<String, CardInformation> cardIdStore;
    DefaultCacheManager cacheManager;

    public InfinispanCardInformationStore(BinRangeLoader binRangeLoader) {
        this.binRangeLoader = binRangeLoader;
        cacheManager = new DefaultCacheManager();
        this.cardIdStore = cacheManager.getCache();
    }

    @Override
    public void initialiseCardInformation() throws Exception {
        binRangeLoader.loadDataTo(this);
    }

    @Override
    public int getStoreSize() {
        return cardIdStore.size();
    }

    @Override
    public void put(CardInformation cardInformation) {
        String key = cardInformation.getMin() + "-" + cardInformation.getMax();
        cardIdStore.putIfAbsent(key, cardInformation);
    }

    @Override
    public Optional<CardInformation> find(String prefix) {
        QueryFactory qf = Search.getQueryFactory(cardIdStore);

        Query q = qf.from(CardInformation.class)
                .having("min").lte(Long.valueOf(prefix))
                .and()
                .having("max").gte(Long.valueOf(prefix))
                .toBuilder().build();

        List<CardInformation> cardInformationList = q.list();
        return cardInformationList.stream().findFirst();
    }

    @Override
    public void destroy() {
        cacheManager.stop();
    }

    public String toString() {
        return "keys=" + cardIdStore.size();
    }
}
