package uk.gov.pay.card.resources;

import uk.gov.pay.card.model.CardInformation;
import uk.gov.pay.card.model.CardInformationResponse;
import uk.gov.pay.card.service.CardService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.util.Optional;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/")
public class CardIdResource {

    CardService cardService;

    public CardIdResource(CardService cardService) {
        this.cardService = cardService;
    }

    @GET
    @Path("/v1/api/card/{card_number}")
    @Produces(APPLICATION_JSON)
    public Response cardInformation(@PathParam("card_number") String cardNumber) {
        Optional<CardInformation> cardInformation = cardService.getCardInformation(cardNumber);

        return cardInformation.map(cardData -> {
            CardInformationResponse cardInformationResponse = new CardInformationResponse(cardData);
            return Response.ok(cardInformationResponse).build();

        }).orElse(Response.status(404).build());

    }
}
