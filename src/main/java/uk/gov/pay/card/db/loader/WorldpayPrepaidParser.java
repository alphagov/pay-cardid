package uk.gov.pay.card.db.loader;

import uk.gov.pay.card.model.PrepaidStatus;

public class WorldpayPrepaidParser {

    public static PrepaidStatus parse(String value) {
        if ("Y".equals(value)) {
            return PrepaidStatus.PREPAID;
        }

        if ("N".equals(value)) {
            return PrepaidStatus.NOT_PREPAID;
        }

        return PrepaidStatus.UNKNOWN;
    }
}
