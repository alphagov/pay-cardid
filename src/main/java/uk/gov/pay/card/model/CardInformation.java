package uk.gov.pay.card.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static java.lang.String.format;

public class CardInformation {

    private final String brand;
    private final CardType cardType;
    private final String label;
    private Long min;
    private Long max;
    private final boolean corporate;
    private final PrepaidStatus prepaidStatus;

    private static final Map<String, String> brandMapping;

    static {
        Map<String, String> brands = new HashMap<>();
        brands.put("MC", "master-card");
        brands.put("MCI DEBIT", "master-card");
        brands.put("MCI CREDIT", "master-card");
        brands.put("MAESTRO", "maestro");
        brands.put("AMERICAN EXPRESS", "american-express");
        brands.put("DINERS CLUB", "diners-club");
        brands.put("VISA CREDIT", "visa");
        brands.put("VISA DEBIT", "visa");
        brands.put("ELECTRON", "visa");
        brandMapping = Collections.unmodifiableMap(brands);
    }

    public CardInformation(String brand, String type, String label, Long min, Long max, boolean corporate, PrepaidStatus prepaidStatus) {
        this.cardType = CardType.of(type);
        this.label = label;
        this.min = min;
        this.max = max;
        this.corporate = corporate;
        this.prepaidStatus = prepaidStatus;

        if (brand != null) {
            this.brand = brandMapping.getOrDefault(brand, brand.toLowerCase());
        } else {
            this.brand = null;
        }
    }

    public CardInformation(String brand, String type, String label, Long min, Long max) {
        this(brand, type, label, min, max, false, PrepaidStatus.UNKNOWN);
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

    public PrepaidStatus getPrepaidStatus() {
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
