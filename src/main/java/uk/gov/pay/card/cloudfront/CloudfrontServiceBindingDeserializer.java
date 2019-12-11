package uk.gov.pay.card.cloudfront;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

public class CloudfrontServiceBindingDeserializer extends StdDeserializer<CloudfrontServiceBinding> {
    public CloudfrontServiceBindingDeserializer() {
        this(null);
    }
    
    protected CloudfrontServiceBindingDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public CloudfrontServiceBinding deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, NoSuchElementException {
        JsonNode root = jsonParser.getCodec().readTree(jsonParser);
        List<JsonNode> serviceNodes = root.at("/user-provided").findParents("instance_name");
        JsonNode cloudfrontNode = serviceNodes.stream()
                .filter((node) -> node.get("instance_name").asText().equals("cloudfront"))
                .findFirst()
                .orElseThrow();
        
        return new CloudfrontServiceBinding(
                cloudfrontNode.at("/credentials/privateKey").asText(),
                cloudfrontNode.at("/credentials/keyName").asText(),
                cloudfrontNode.at("/credentials/keyProvider").asText()
        );
    }
}
