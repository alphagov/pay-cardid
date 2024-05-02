package uk.gov.pay.card.sandbox;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

class BinRange {
    long lowerBound;
    long upperBound;
    String productType;
    String cardClass;
    String brand;

    public BinRange(long lowerBound, long upperBound, String productType, String cardClass, String brand) {
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.productType = productType;
        this.brand = brand;
        this.cardClass = cardClass;
    }

    @Override
    public String toString() {
        return "BinRange{" +
                "lowerBound=" + lowerBound +
                ", upperBound=" + upperBound +
                ", productType='" + productType + '\'' +
                ", brand='" + brand + '\'' +
                ", cardClass='" + cardClass + '\'' +
                '}';
    }
}

public class BinRangeCheckerBinarySearch {

    private static final Logger logger = LoggerFactory.getLogger(BinRangeCheckerBinarySearch.class);
    private final List<BinRange> binRanges;

    public BinRangeCheckerBinarySearch(String csvFilePath) {
        binRanges = Collections.synchronizedList(new ArrayList<>());
        loadBinRanges(csvFilePath);
    }

    private void loadBinRanges(String csvFilePath) {
        InputStream inputStream = getClass().getResourceAsStream(csvFilePath);

        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            List<String> lines = br.lines().collect(Collectors.toList());
            lines.remove(0); // strip header record
            lines.remove(lines.size() - 1); // strip trailer record

            lines.parallelStream().forEach(line -> {
                String[] parts = line.split(",");
                long lowerBound = Long.parseLong(parts[1]);
                long upperBound = Long.parseLong(parts[2]);
                String productType = parts[3];
                String brand = parts[4];
                String cardClass = parts[9];
                binRanges.add(new BinRange(lowerBound, upperBound, productType, cardClass, brand));

            });
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        binRanges.sort(Comparator.comparingLong(b -> b.lowerBound));
    }

    public boolean isWithinBinRange(long pan) {
        int left = 0;
        int right = binRanges.size() - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            BinRange current = binRanges.get(mid);

            if (pan >= current.lowerBound && pan <= current.upperBound) {
                logger.info("matched: {}", current);
                return true;
            } else if (pan < current.lowerBound) {
                right = mid - 1;
            } else {
                left = mid + 1;
            }
        }
        return false;
    }

    private void search(Long pan) {
        logger.info("searching PAN: {}", pan);
        var found = isWithinBinRange(pan);
        if (!found) {
            logger.info("PAN not found");
        }
    }

    public static void main(String[] args) {
        BinRangeCheckerBinarySearch binRangeChecker = new BinRangeCheckerBinarySearch("/data-sources/complete-worldpay-v3.csv");

//        List<Long> pans = List.of(); put some PANs in here
//        pans.forEach(binRangeChecker::search);
    }
}
