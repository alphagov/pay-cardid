package uk.gov.pay.card.db.loader;

import uk.gov.pay.card.model.CardType;
import uk.gov.pay.card.model.PrepaidStatus;

import java.util.Map;

import static java.lang.String.format;
import static java.util.Map.entry;

public class BinRangeParser {
    
    private static final Map<String, String> BRAND_MAPPING = Map.ofEntries(
            entry("MC", "master-card"),
            entry("DEBIT MASTERCARD", "master-card"),
            entry("MASTERCARD CREDIT", "master-card"),
            entry("MAESTRO", "maestro"),
            entry("AMEX", "american-express"),
            entry("DINERS CLUB", "diners-club"),
            entry("DINERS DISCOVER", "discover"),
            entry("JAPANESE CREDIT BUREAU", "jcb"),
            entry("VISA CREDIT", "visa"),
            entry("VISA DEBIT", "visa"),
            entry("ELECTRON", "visa")
    );

    public static Long calculateMinDigitForCardLength(Long minCardDigit, int numLength) {
        return Long.valueOf(format("%-" + numLength + "d", minCardDigit).replace(" ", "0"));
    }

    public static Long calculateMaxDigitForCardLength(Long maxCardDigit, int numLength) {
        return Long.valueOf(format("%-" + numLength + "d", maxCardDigit).replace(" ", "9"));
    }

    public static PrepaidStatus calculateWorldpayPrepaidStatus(String entry) {
        return "P".equals(entry) ? PrepaidStatus.PREPAID : PrepaidStatus.NOT_PREPAID;
    }

    public static String calculateCardBrand(String entry) {
        return entry != null ? BRAND_MAPPING.getOrDefault(entry, entry.toLowerCase()) : null;
    }

    public static CardType calculateCardType(String entry) {
        return CardType.of(entry);
    }
}
