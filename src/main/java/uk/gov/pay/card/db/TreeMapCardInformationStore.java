package uk.gov.pay.card.db;


import uk.gov.pay.card.db.loader.BinRangeDataLoader;
import uk.gov.pay.card.model.CardInformation;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Predicate;

import static java.lang.String.format;

public class TreeMapCardInformationStore implements CardInformationStore {

    private final List<BinRangeDataLoader> binRangeLoaders;
    private final TreeMap<String,CardInformation> lowerMap;
    private final TreeMap<String,CardInformation> upperMap;
    private static final int MAXIMUM_PAN_LENGTH = 19;
    static final String LOW_VALUE_TEMPLATE;
    static final String HIGH_VALUE_TEMPLATE;

    static {
        char[] zeros = new char[MAXIMUM_PAN_LENGTH];
        Arrays.fill(zeros, '0');
        LOW_VALUE_TEMPLATE = new String(zeros);

        char[] nines = new char[MAXIMUM_PAN_LENGTH];
        Arrays.fill(nines, '9');
        HIGH_VALUE_TEMPLATE = new String(nines);
    }

    public TreeMapCardInformationStore(List<BinRangeDataLoader> binRangeLoaders) {
        this.binRangeLoaders = binRangeLoaders;
        lowerMap = new TreeMap<>();
        upperMap = new TreeMap<>();
    }

    @Override
    public synchronized void initialiseCardInformation() throws Exception {
        for (BinRangeDataLoader loader : binRangeLoaders) {
            loader.loadDataTo(this);
        }
    }

    /**
     * Pad a PAN to length. Given a base of e.g. 9999999 and a prefix of 123, return 1239999
     *
     * @param base The base to pad with (either all zeros or all 9s)
     * @param prefix The PAN prefix to overlay onto 'base'
     * @return prefix padded to the length of 'base' with characters from 'base'
     */
    static String padPAN(String base, String prefix) {
        if (prefix == null || base == null) return null;
        if (prefix.length() > base.length()) return prefix;

        StringBuilder buf = new StringBuilder(base);
        buf.replace(0, prefix.length(), prefix);
        return buf.toString();
    }

    @Override
    public void put(CardInformation cardInformation) {
        lowerMap.put(padPAN(LOW_VALUE_TEMPLATE, cardInformation.getMin().toString()), cardInformation);
        upperMap.put(padPAN(HIGH_VALUE_TEMPLATE, cardInformation.getMax().toString()), cardInformation);
    }

    @Override
    public Optional<CardInformation> find(String prefix) {
        if (prefix == null || prefix.length() > MAXIMUM_PAN_LENGTH)
            return Optional.empty();

        String prefixZeroPadded = padPAN(LOW_VALUE_TEMPLATE, prefix);
        return Optional.ofNullable(lowerMap.floorEntry(prefixZeroPadded).getValue())
               .filter(Predicate.isEqual(upperMap.ceilingEntry(prefixZeroPadded).getValue()));
    }

    @Override
    public synchronized void destroy() {
        lowerMap.clear();
        upperMap.clear();
    }

    @Override
    public String toString() {
        return format("keys=%d", lowerMap.size());
    }
}
