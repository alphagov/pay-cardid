package uk.gov.pay.card.model;

import java.util.Objects;

public class CardInformation {

    private final String brand;
    private final CardType cardType;
    private final String label;
    private Long min;
    private Long max;
    private final boolean corporate;
    private final PrepaidStatus prepaidStatus;

    public CardInformation(String brand, CardType type, String label, Long min, Long max, boolean corporate, PrepaidStatus prepaidStatus) {
        this.cardType = type;
        this.label = label;
        this.min = min;
        this.max = max;
        this.corporate = corporate;
        this.brand = brand;
        this.prepaidStatus = prepaidStatus;
    }

    public CardInformation(String brand, CardType type, String label, Long min, Long max) {
        this(brand, type, label, min, max, false, PrepaidStatus.NOT_PREPAID);
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

    public void setMin(Long min) {
        this.min = min;
    }

    public void setMax(Long max) {
        this.max = max;
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
