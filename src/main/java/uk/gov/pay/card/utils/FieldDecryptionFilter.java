package uk.gov.pay.card.utils;

import com.fasterxml.jackson.databind.JsonNode;
import org.glassfish.jersey.server.ContainerRequest;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;

public class FieldDecryptionFilter implements ContainerRequestFilter {
    
    @Override
    public void filter(ContainerRequestContext requestContext) {
        if (isJson(requestContext)) {
            if (requestContext instanceof ContainerRequest && isJson(requestContext)) {
                ContainerRequest request = (ContainerRequest) requestContext;
                request.bufferEntity();
                JsonNode jsonData = request.readEntity(JsonNode.class);
                
            }
        }
    }
    
    private boolean isJson(ContainerRequestContext requestContext) {
        return requestContext.getMediaType().toString().equals("application/json");
    }
}
