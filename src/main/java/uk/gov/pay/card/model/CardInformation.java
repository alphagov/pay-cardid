package uk.gov.pay.card.model;

import java.util.Map;
import java.util.Objects;

import static java.lang.String.format;

public class CardInformation {

    private static final Map<String, String> BRAND_MAPPING = Map.of(
            "MC", "master-card",
            "MCI DEBIT", "master-card",
            "MCI CREDIT", "master-card",
            "MAESTRO", "maestro",
            "AMERICAN EXPRESS", "american-express",
            "DINERS CLUB", "diners-club",
            "VISA CREDIT", "visa",
            "VISA DEBIT", "visa",
            "ELECTRON", "visa"
    );

    private final String brand;
    private final CardType cardType;
    private final String label;
    private Long min;
    private Long max;
    private final boolean corporate;
    private final PrepaidStatus prepaidStatus;

    public CardInformation(String brand, String type, String label, Long min, Long max, boolean corporate, PrepaidStatus prepaidStatus) {
        this.cardType = CardType.of(type);
        this.label = label;
        this.min = min;
        this.max = max;
        this.corporate = corporate;
        this.prepaidStatus = prepaidStatus;

        if (brand != null) {
            this.brand = BRAND_MAPPING.getOrDefault(brand, brand.toLowerCase());
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
