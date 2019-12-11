package uk.gov.pay.card.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Size;

import static java.lang.String.format;

public class CardInformationRequest {

    /**
     * PANs can be between 10 and 19 characters
     * @see <a href="https://www.ansi.org/news_publications/news_story?articleid=da7bcb04-0654-4e03-af54-0e55d50b93a8">ANSI IIN</a>
     */
    @JsonProperty("cardNumber") @NotEmpty
    private String cardNumber;
    
    public CardInformationRequest(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    @Override
    public String toString() {
        return format("CardInformation request, card number length %s", cardNumber.length());
    }
}
