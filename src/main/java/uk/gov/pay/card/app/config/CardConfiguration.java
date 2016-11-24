package uk.gov.pay.card.app.config;

import io.dropwizard.Configuration;

import javax.validation.constraints.NotNull;

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

    public String getDiscoverDataLocation() {
        return discoverDataLocation;
    }

    public String getWorldpayDataLocation() {
        return worldpayDataLocation;
    }

    public String getTestCardDataLocation() {
        return testCardDataLocation;
    }

    public String getGraphiteHost() {
        return graphiteHost;
    }

    public String getGraphitePort() {
        return graphitePort;
    }
}
