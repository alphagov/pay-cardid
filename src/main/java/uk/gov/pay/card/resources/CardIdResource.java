package uk.gov.pay.card.resources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.pay.card.model.CardInformation;
import uk.gov.pay.card.model.CardInformationRequest;
import uk.gov.pay.card.model.CardInformationResponse;
import uk.gov.pay.card.service.CardService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import java.util.Optional;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;

@Path("/")
public class CardIdResource {
    private static final Logger logger = LoggerFactory.getLogger(CardIdResource.class);

    private final CardService cardService;

    public CardIdResource(CardService cardService) {
        this.cardService = cardService;
    }

    @POST
    @Path("/v1/api/card")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    public Response cardInformation(@NotNull @Valid CardInformationRequest cardInformationRequest) {
        logger.info("Card Information Request - {}", cardInformationRequest);
        
        return getCardNumber(cardInformationRequest.getCardNumber())
                .flatMap(cardService::getCardInformation)
                .map(CardIdResource::newCardInformationResponse)
                .map(Response::ok)
                .orElse(Response.status(NOT_FOUND))
                .build();
    }

    private Optional<Long> getCardNumber(String cardNumber) {
        try {
            return Optional.of(Long.valueOf(cardNumber));
        } catch (NumberFormatException e) {
            logger.info("Received card number that cannot be parsed into long");
            return Optional.empty();
        }
    }

    private static CardInformationResponse newCardInformationResponse(CardInformation cardInformation) {
        final CardInformationResponse cardInformationResponse = new CardInformationResponse(cardInformation);
        logger.info("Card Information Response - {}", cardInformationResponse);
        return cardInformationResponse;
    }
}
