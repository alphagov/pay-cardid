package uk.gov.pay.card.resources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.pay.card.model.CardInformation;
import uk.gov.pay.card.model.CardInformationRequest;
import uk.gov.pay.card.model.CardInformationResponse;
import uk.gov.pay.card.service.CardService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;

import java.util.Optional;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.NOT_FOUND;

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
