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
import java.util.Optional;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/")
public class CardIdResource {
    public static final String API_VERSION_PATH = "/v1";
    public static final String CARD_INFORMATION_PATH = API_VERSION_PATH + "/api/card";
    private static final Logger logger = LoggerFactory.getLogger(CardIdResource.class);

    CardService cardService;

    public CardIdResource(CardService cardService) {
        this.cardService = cardService;
    }

    @POST
    @Path(CARD_INFORMATION_PATH)
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    public Response cardInformation(CardInformationRequest cardInformationRequest) {
        logger.info("Card Information Request - {}", cardInformationRequest);

        Optional<CardInformation> cardInformation = cardService.getCardInformation(cardInformationRequest.getCardNumber());

        return cardInformation.map(cardData -> {
            CardInformationResponse cardInformationResponse = new CardInformationResponse(cardData);
            logger.info("Card Information Response - {}", cardInformationResponse);
            return Response.ok(cardInformationResponse).build();

        }).orElse(Response.status(404).build());

    }
}
