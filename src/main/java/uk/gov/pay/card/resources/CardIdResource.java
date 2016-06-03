package uk.gov.pay.card.resources;

import uk.gov.pay.card.model.CardInformationResponse;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/")
public class CardIdResource {

    @GET
    @Path("/v1/api/card/{card_number}")
    @Produces(APPLICATION_JSON)
    public Response cardInformation(@PathParam("card_number") String cardNumber) {
        return Response.ok(new CardInformationResponse("visa", "debit", "visa")).build();
    }
}
