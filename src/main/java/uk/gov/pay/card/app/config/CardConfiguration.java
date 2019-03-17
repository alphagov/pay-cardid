package uk.gov.pay.card.app.config;

import io.dropwizard.Configuration;

import javax.validation.constraints.NotNull;
import java.net.MalformedURLException;
import java.net.URL;

public class CardConfiguration extends Configuration {

    @NotNull
    private String worldpayDataLocation;

    @NotNull
    private String discoverDataLocation;

    @NotNull
    private String testCardDataLocation;

    @NotNull
    private String graphiteHost;

    @NotNull
    private String graphitePort;

    public URL getDiscoverDataLocation() {
        try {
            return new URL(discoverDataLocation);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public URL getWorldpayDataLocation() {
        try {
            return new URL(worldpayDataLocation);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public URL getTestCardDataLocation() {
        try {
            return new URL(testCardDataLocation);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public String getGraphiteHost() {
        return graphiteHost;
    }

    public String getGraphitePort() {
        return graphitePort;
    }
}
