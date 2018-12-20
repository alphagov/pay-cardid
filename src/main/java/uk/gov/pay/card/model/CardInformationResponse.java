package uk.gov.pay.card.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class CardInformationResponse {

    @JsonProperty("brand")
    private final String brand;

    @JsonProperty("type")
    private final String type;

    @JsonProperty("label")
    private final String label;

    @JsonProperty("corporate")
    private final boolean corporate;

    @JsonProperty("prepaid")
    private final PrepaidStatus prepaidStatus;

    public CardInformationResponse(CardInformation cardData) {
        this.brand = cardData.getBrand();
        this.label = cardData.getLabel();
        this.type = cardData.getCardType().getGovUkPayRepresentation();
        this.corporate = cardData.isCorporate();
        this.prepaidStatus = cardData.getPrepaidStatus();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CardInformationResponse that = (CardInformationResponse) o;
        return corporate == that.corporate &&
                Objects.equals(brand, that.brand) &&
                Objects.equals(type, that.type) &&
                Objects.equals(label, that.label) &&
                Objects.equals(prepaidStatus, that.prepaidStatus);
    }

    @Override
    public int hashCode() {
        return Objects.hash(brand, type, label, corporate, prepaidStatus);
    }

    @Override
    public String toString() {
        return "CardInformationResponse{" +
                "brand='" + brand + '\'' +
                ", type='" + type + '\'' +
                ", label='" + label + '\'' +
                ", corporate=" + corporate + '\'' +
                ", prepaidStatus=" + prepaidStatus +
                '}';
    }
}
