package uk.gov.pay.card.resources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.pay.card.model.CardInformation;
import uk.gov.pay.card.model.CardInformationRequest;
import uk.gov.pay.card.model.CardInformationResponse;
import uk.gov.pay.card.service.CardService;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

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
    public Response cardInformation(CardInformationRequest cardInformationRequest) {
        logger.info("Card Information Request - {}", cardInformationRequest);

        return cardService.getCardInformation(cardInformationRequest.getCardNumber())
                .map(CardIdResource::newCardInformationResponse)
                .map(Response::ok)
                .orElse(Response.status(NOT_FOUND))
                .build();
    }

    private static CardInformationResponse newCardInformationResponse(CardInformation cardInformation) {
        final CardInformationResponse cardInformationResponse = new CardInformationResponse(cardInformation);
        logger.info("Card Information Response - {}", cardInformationResponse);
        return cardInformationResponse;
    }
}
