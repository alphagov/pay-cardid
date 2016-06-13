package uk.gov.pay.card.resources;

import uk.gov.pay.card.model.CardInformation;
import uk.gov.pay.card.model.CardInformationRequest;
import uk.gov.pay.card.model.CardInformationResponse;
import uk.gov.pay.card.service.CardService;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.Optional;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/")
public class CardIdResource {

    CardService cardService;

    public CardIdResource(CardService cardService) {
        this.cardService = cardService;
    }

    @POST
    @Path("/v1/api/card")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    public Response cardInformation(CardInformationRequest cardInformationRequest) {
        Optional<CardInformation> cardInformation = cardService.getCardInformation(cardInformationRequest.getCardNumber());

        return cardInformation.map(cardData -> {
            CardInformationResponse cardInformationResponse = new CardInformationResponse(cardData);
            return Response.ok(cardInformationResponse).build();

        }).orElse(Response.status(404).build());

    }
}
