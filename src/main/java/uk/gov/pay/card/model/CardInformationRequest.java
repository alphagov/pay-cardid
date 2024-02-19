package uk.gov.pay.card.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Size;

import static java.lang.String.format;
import static uk.gov.pay.card.db.CardInformationStore.CARD_RANGE_LENGTH;

public class CardInformationRequest {

    /**
     * PANs can be between 10 and 19 characters
     * @see <a href="https://www.ansi.org/news_publications/news_story?articleid=da7bcb04-0654-4e03-af54-0e55d50b93a8">ANSI IIN</a>
     */
    @JsonProperty("cardNumber") @NotEmpty @Size(min = CARD_RANGE_LENGTH, max = 19)
    private String cardNumber;

    public String getCardNumber() {
        return cardNumber;
    }

    @Override
    public String toString() {
        return format("CardInformation request, card number length %s", cardNumber.length());
    }
}
