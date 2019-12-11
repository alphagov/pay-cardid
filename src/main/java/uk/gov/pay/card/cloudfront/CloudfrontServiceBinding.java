package uk.gov.pay.card.cloudfront;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.hibernate.validator.constraints.NotEmpty;

@JsonDeserialize(using = CloudfrontServiceBindingDeserializer.class)
public class CloudfrontServiceBinding {
    @JsonProperty @NotEmpty
    private String privateKey;
    
    @JsonProperty @NotEmpty
    private String keyName;
    
    @JsonProperty @NotEmpty
    private String keyProvider;

    public CloudfrontServiceBinding(String privateKey, String keyName, String keyProvider) {
        this.privateKey = privateKey;
        this.keyName = keyName;
        this.keyProvider = keyProvider;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public String getKeyName() {
        return keyName;
    }

    public String getKeyProvider() {
        return keyProvider;
    }
}
