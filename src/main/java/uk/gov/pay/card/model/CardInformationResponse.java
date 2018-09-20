package uk.gov.pay.card.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class CardInformationResponse {

    @JsonProperty("brand")
    private String brand;

    @JsonProperty("type")
    private String type;

    @JsonProperty("label")
    private String label;

    @JsonProperty("corporate")
    private boolean corporate;

    public CardInformationResponse(CardInformation cardData) {
        this.brand = cardData.getBrand();
        this.label = cardData.getLabel();
        this.type = cardData.getCardType().getGovUkPayRepresentation();
        this.corporate = cardData.isCorporate();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CardInformationResponse that = (CardInformationResponse) o;
        return corporate == that.corporate &&
                Objects.equals(brand, that.brand) &&
                Objects.equals(type, that.type) &&
                Objects.equals(label, that.label);
    }

    @Override
    public int hashCode() {
        return Objects.hash(brand, type, label, corporate);
    }

    @Override
    public String toString() {
        return "CardInformationResponse{" +
                "brand='" + brand + '\'' +
                ", type='" + type + '\'' +
                ", label='" + label + '\'' +
                ", corporate=" + corporate +
                '}';
    }
}
