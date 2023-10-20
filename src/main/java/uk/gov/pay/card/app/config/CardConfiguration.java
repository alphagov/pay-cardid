package uk.gov.pay.card.app.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.core.Configuration;

import javax.validation.constraints.NotNull;
import java.net.URI;
import java.net.URL;
import java.util.Optional;

public class CardConfiguration extends Configuration {

    @NotNull
    private URL worldpayDataLocation;

    @NotNull
    private URL discoverDataLocation;

    @NotNull
    private URL testCardDataLocation;

    @JsonProperty("ecsContainerMetadataUriV4")
    private URI ecsContainerMetadataUriV4;

    public URL getDiscoverDataLocation() {
        return discoverDataLocation;
    }

    public URL getWorldpayDataLocation() {
        return worldpayDataLocation;
    }

    public URL getTestCardDataLocation() {
        return testCardDataLocation;
    }

    public Optional<URI> getEcsContainerMetadataUriV4() {
        return Optional.ofNullable(ecsContainerMetadataUriV4);
    }
}
