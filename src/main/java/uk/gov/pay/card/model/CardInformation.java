package uk.gov.pay.card.model;

import java.util.Map;
import java.util.Objects;

import static java.lang.String.format;
import static java.util.Map.entry;

public class CardInformation {

    private static final Map<String, String> BRAND_MAPPING = Map.ofEntries(
            entry("MC", "master-card"),
            entry("DEBIT MASTERCARD", "master-card"),
            entry("MASTERCARD CREDIT", "master-card"),
            entry("MAESTRO", "maestro"),
            entry("AMEX", "american-express"),
            entry("DINERS CLUB", "diners-club"),
            entry("DINERS DISCOVER", "diners-club"),
            entry("JAPANESE CREDIT BUREAU", "jcb"),
            entry("VISA CREDIT", "visa"),
            entry("VISA DEBIT", "visa"),
            entry("ELECTRON", "visa")
    );

    private final String brand;
    private final CardType cardType;
    private final String label;
    private Long min;
    private Long max;
    private final boolean corporate;
    private final PrepaidStatus prepaidStatus;

    public CardInformation(String brand, String type, String label, Long min, Long max, boolean corporate) {
        this.cardType = CardType.of(type);
        this.label = label;
        this.min = min;
        this.max = max;
        this.corporate = corporate;
        this.brand = brand != null ? BRAND_MAPPING.getOrDefault(brand, brand.toLowerCase()) : null;
        this.prepaidStatus = "P".equals(type) ? PrepaidStatus.PREPAID : PrepaidStatus.NOT_PREPAID;
    }

    public CardInformation(String brand, String type, String label, Long min, Long max) {
        this(brand, type, label, min, max, false);
    }

    public String getBrand() {
        return brand;
    }

    public CardType getCardType() {
        return cardType;
    }

    public String getLabel() {
        return label;
    }

    public Long getMax() {
        return max;
    }

    public Long getMin() {
        return min;
    }

    public boolean isCorporate() {
        return corporate;
    }

    PrepaidStatus getPrepaidStatus() {
        return prepaidStatus;
    }

    public void updateRangeLength(int numLength) {
        min = Long.valueOf(format("%-" + numLength + "d", min).replace(" ", "0"));
        max = Long.valueOf(format("%-" + numLength + "d", max).replace(" ", "9"));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CardInformation that = (CardInformation) o;
        return corporate == that.corporate &&
                Objects.equals(brand, that.brand) &&
                cardType == that.cardType &&
                Objects.equals(label, that.label) &&
                Objects.equals(min, that.min) &&
                Objects.equals(max, that.max) &&
                Objects.equals(prepaidStatus, that.prepaidStatus);
    }

    @Override
    public int hashCode() {
        return Objects.hash(brand, cardType, label, min, max, corporate);
    }

    @Override
    public String toString() {
        return "CardInformation{" +
                "brand='" + brand + '\'' +
                ", cardType=" + cardType +
                ", label='" + label + '\'' +
                ", prepaidStatus='" + prepaidStatus + '\'' +
                ", min=" + min +
                ", max=" + max +
                ", corporate=" + corporate +
                '}';
    }
}
