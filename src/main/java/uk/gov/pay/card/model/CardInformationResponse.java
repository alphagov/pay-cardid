package uk.gov.pay.card.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CardInformationResponse {

    @JsonProperty("brand")
    private String brand;

    @JsonProperty("type")
    private String type;

    @JsonProperty("label")
    private String label;

    public CardInformationResponse(CardInformation cardData) {
        this.brand = cardData.getBrand();
        this.label = cardData.getLabel();
        this.type = cardData.getType();
    }

    public String getBrand() {
        return brand;
    }

    public String getType() {
        return type;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CardInformationResponse that = (CardInformationResponse) o;

        if (brand != null ? !brand.equals(that.brand) : that.brand != null) return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        return label != null ? label.equals(that.label) : that.label == null;

    }

    @Override
    public int hashCode() {
        int result = brand != null ? brand.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (label != null ? label.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "CardInformationResponse{" +
                "brand='" + brand + '\'' +
                ", type='" + type + '\'' +
                ", label='" + label + '\'' +
                '}';
    }
}
