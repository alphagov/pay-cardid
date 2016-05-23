package uk.gov.pay.card.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CardInformation {

    @JsonProperty("scheme")
    private String cardScheme;

    @JsonProperty("type")
    private String cardType;

    public CardInformation(String cardScheme, String cardType) {
        this.cardScheme = cardScheme;
        this.cardType = cardType;
    }

    public String getCardScheme() {
        return cardScheme;
    }

    public String getCardType() {
        return cardType;
    }
}
