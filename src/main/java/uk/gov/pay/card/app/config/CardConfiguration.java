package uk.gov.pay.card.app.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.core.Configuration;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Optional;

public class CardConfiguration extends Configuration {

    private String worldpayDataLocation;
    private String discoverDataLocation;
    private String testCardDataLocation;

    @JsonProperty("ecsContainerMetadataUriV4")
    private URI ecsContainerMetadataUriV4;

    public URL getDiscoverDataLocation() {
        return getUrlFromString(discoverDataLocation);
    }

    public URL getWorldpayDataLocation() {
        return getUrlFromString(worldpayDataLocation);
    }

    public URL getTestCardDataLocation() {
        return getUrlFromString(testCardDataLocation);
    }

    public Optional<URI> getEcsContainerMetadataUriV4() {
        return Optional.ofNullable(ecsContainerMetadataUriV4);
    }

    private URL getUrlFromString(String input) {
        String classpathPrefix = "classpath:";
        String filePrefix = "file://";
        if (input.startsWith(filePrefix)) {
            try {
                String substring = input.substring(filePrefix.length());
                return new File(substring).toURI().toURL();
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        } else if (input.startsWith(classpathPrefix)) {
            return getClass().getResource(input.substring(classpathPrefix.length()));
        } else {
            throw new RuntimeException(String.format("File configuration needs to start with 'file:' or 'classpath:' %s", input));
        }
    }
}
