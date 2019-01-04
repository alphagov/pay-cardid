package uk.gov.pay.card.db;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.pay.card.db.loader.BinRangeDataLoader;
import uk.gov.pay.card.model.CardInformation;

import javax.smartcardio.Card;
import java.util.*;
import java.util.function.Predicate;

import static java.lang.String.format;

public class TreeMapCardInformationStore implements CardInformationStore {

    private final List<BinRangeDataLoader> binRangeLoaders;
    private final TreeMap<String,CardInformation> lowerMap;
    private final TreeMap<String,CardInformation> upperMap;
    private static final Logger logger = LoggerFactory.getLogger(TreeMapCardInformationStore.class);
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
        String lowerBound = padPAN(LOW_VALUE_TEMPLATE, cardInformation.getMin().toString());
        String upperBound = padPAN(HIGH_VALUE_TEMPLATE, cardInformation.getMax().toString());
        CardInformation overlap = getOverlappingEntry(cardInformation);
        if (overlap != null)
            logger.warn(format("Bin range %d-%d overlaps with existing range %d-%d!", cardInformation.getMin(), cardInformation.getMax(), overlap.getMin(), overlap.getMax()));
        
        lowerMap.put(lowerBound, cardInformation);
        upperMap.put(upperBound, cardInformation);
    }

    /*
    |  |  #  #
    
    #  |  #  | - upper of new within existing
    |  #  |  # - lower of new within existing
    |  #  #  | - existing contains new
    #  |  |  # - new contains existing
    
     */
    CardInformation getOverlappingEntry(final CardInformation cardInformation) {
        
        String lowerBound = padPAN(LOW_VALUE_TEMPLATE, cardInformation.getMin().toString());
        String upperBound = padPAN(HIGH_VALUE_TEMPLATE, cardInformation.getMax().toString());
        Map.Entry<String,CardInformation> lowMidEntry = lowerMap.floorEntry(upperBound);
        if (lowMidEntry != null && lowMidEntry.getValue().getMin() > Long.valueOf(upperBound)) return lowMidEntry.getValue();
        Map.Entry<String,CardInformation> highMidEntry = upperMap.ceilingEntry(lowerBound);
        if (highMidEntry != null && highMidEntry.getValue().getMax() > Long.valueOf(lowerBound)) return highMidEntry.getValue();
        
        Map.Entry<String,CardInformation> lowEntry = lowerMap.floorEntry(lowerBound);
        Map.Entry<String,CardInformation> highEntry = upperMap.ceilingEntry(upperBound);
        
        if (lowEntry == null || highEntry == null) return null;
        
        return Objects.equals(lowEntry.getValue(), highEntry.getValue()) ? lowEntry.getValue() : null;
    }

    @Override
    public Optional<CardInformation> find(String prefix) {
        if (prefix == null || prefix.length() > MAXIMUM_PAN_LENGTH)
            return Optional.empty();

        String prefixZeroPadded = padPAN(LOW_VALUE_TEMPLATE, prefix);
        Map.Entry<String,CardInformation> lowEntry = lowerMap.floorEntry(prefixZeroPadded);
        Map.Entry<String,CardInformation> highEntry = upperMap.ceilingEntry(prefixZeroPadded);
        
        if (lowEntry == null || highEntry == null)
            return Optional.empty();
            
        return Optional.ofNullable(lowEntry.getValue())
               .filter(Predicate.isEqual(highEntry.getValue()));
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
