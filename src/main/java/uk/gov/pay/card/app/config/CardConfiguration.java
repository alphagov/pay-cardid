package uk.gov.pay.card.app.config;

import io.dropwizard.Configuration;

import javax.validation.constraints.NotNull;
import java.net.URL;

public class CardConfiguration extends Configuration {

    @NotNull
    private URL worldpayDataLocation;

    @NotNull
    private URL discoverDataLocation;

    @NotNull
    private URL testCardDataLocation;

    @NotNull
    private String graphiteHost;

    @NotNull
    private Integer graphitePort;

    public URL getDiscoverDataLocation() {
        return discoverDataLocation;
    }

    public URL getWorldpayDataLocation() {
        return worldpayDataLocation;
    }

    public URL getTestCardDataLocation() {
        return testCardDataLocation;
    }

    public String getGraphiteHost() {
        return graphiteHost;
    }

    public Integer getGraphitePort() {
        return graphitePort;
    }
}
