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

    public String getDiscoverDataLocation() {
        return discoverDataLocation;
    }

    public String getWorldpayDataLocation() {
        return worldpayDataLocation;
    }

    public String getTestCardDataLocation() {
        return testCardDataLocation;
    }
}
