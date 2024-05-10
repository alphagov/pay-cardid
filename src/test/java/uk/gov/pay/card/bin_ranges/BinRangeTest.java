package uk.gov.pay.card.bin_ranges;


import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import uk.gov.pay.card.db.loader.BinRangeParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.lang.String.format;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.fail;

class BinRangeTest {

    private static final int CARD_RANGE_LENGTH = 18;
    private static List<Range<Long>> worldpayData;
    private static List<Range<Long>> testCardData;
    private static List<Range<Long>> discoverData;

    private static final Function<String[], Range<Long>> cardRangeExtractor = entry -> {
        Long minCardDigit = BinRangeParser.calculateMinDigitForCardLength(Long.valueOf(entry[1]), CARD_RANGE_LENGTH);
        Long maxCardDigit = BinRangeParser.calculateMaxDigitForCardLength(Long.valueOf(entry[2]), CARD_RANGE_LENGTH);
        return Range.closed(minCardDigit, maxCardDigit);
    };

    /**
     * In the cardid CI pipeline, real bin ranges files will be written to the data-sources directory
     */
    @BeforeAll
    static void beforeAll() {
        URL worldpayDataSource = Thread.currentThread().getContextClassLoader().getResource("data-sources/worldpay-v3.csv");
        URL testDataSource = Thread.currentThread().getContextClassLoader().getResource("data-sources/test-cards.csv");
        URL discoverDataSource = Thread.currentThread().getContextClassLoader().getResource("data-sources/discover.csv");

        worldpayData = loadData(worldpayDataSource, "01");
        testCardData = loadData(testDataSource, "02");
        discoverData = loadData(discoverDataSource, "02");
    }
    
    @Test
    void shouldHaveNoOverlapBetweenTestAndWorldpayBinRanges() {
        checkForBinRangeOverlaps("Worldpay", worldpayData, "Test Card", testCardData);
    }

    @Test
    void shouldHaveNoOverlapBetweenTestAndDiscoverBinRanges() {
        checkForBinRangeOverlaps("Discover", discoverData, "Test Card", testCardData);
    }

    @Test
    void shouldHaveNoOverlapBetweenDiscoverAndWorldpayBinRanges() {
        checkForBinRangeOverlaps("Worldpay", worldpayData, "Discover", discoverData);
    }

    private void checkForBinRangeOverlaps(String ranges1Type, List<Range<Long>> ranges1, String ranges2Type, List<Range<Long>> ranges2) {
        RangeSet<Long> rangeSet = TreeRangeSet.create();
        ranges1.forEach(rangeSet::add);

        List<String> overlappingRanges = new ArrayList<>();
        ranges2.forEach(cardRange -> {
            if (rangeSet.intersects(cardRange)) {
                overlappingRanges.add(String.format("%s range %s intersects %s ranges %s", 
                        ranges1Type, cardRange, ranges2Type, rangeSet.subRangeSet(cardRange)));
            }
        });

        if (!overlappingRanges.isEmpty()) {
            fail("Found overlaps in ranges:\n" + String.join("\n" , overlappingRanges));
        }
    }

    /**
      We need to check there is no overlap between ranges across all data sources because some ranges are associated
      with corporate card numbers, as identified by the 4th "CP" column in the example line entry from the Worldpay data source:

      01,511226661120000000,511226661121000000,CP,DINERS DISCOVER,.... etc

      Having no overlap ranges is essential to cardid figuring out with confidence if a card is a corporate card or not.
     */
    @Test
    void verifyNoOverlappingRanges() {
        RangeSet<Long> setOfBinRanges = TreeRangeSet.create();
        Map<Range<Long>, String> mapOfRanges = new HashMap<>();
        List<Range<Long>> rangesNotInHashMap = new ArrayList<>();
        worldpayData.forEach(cardRange -> {
            setOfBinRanges.add(cardRange);
            mapOfRanges.put(cardRange, "worldpay");
        });
        discoverData.forEach(cardRange -> {
            setOfBinRanges.add(cardRange);
            mapOfRanges.put(cardRange, "discover");
        });
        testCardData.forEach(cardRange -> {
            setOfBinRanges.add(cardRange);
            mapOfRanges.put(cardRange, "test");
        });
        setOfBinRanges.asRanges().forEach(range -> {
            String contains = mapOfRanges.get(range);
            if (contains == null) {
                rangesNotInHashMap.add(range);
            }
        });
        assertThat(rangesNotInHashMap.size(), is(0));
    }

    private static List<Range<Long>> loadData(URL source, String dataRowIdentifier) {
        List<Range<Long>> listOfRanges = new ArrayList<>();

        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(source.openStream()))) {
            bufferedReader.lines().forEach(line -> {
                String[] entry = line.split(",");
                if (dataRowIdentifier.equals(entry[0])) {
                    Range<Long> cardRange = cardRangeExtractor.apply(entry);
                    listOfRanges.add(cardRange);
                }
            });
            return listOfRanges;
        } catch (Exception e) {
            throw new RuntimeException(format("Exception loading file at: %s", source == null ? "(source is null)" : source.toString()), e);
        }
    }
}
