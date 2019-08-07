package uk.gov.pay.card.db.loader;

import uk.gov.pay.card.model.PrepaidStatus;

class WorldpayPrepaidParser {
    static PrepaidStatus parse(String value) {
        if (value == null) {
            return PrepaidStatus.UNKNOWN;
        }

        switch (value) {
            case "Y": return PrepaidStatus.PREPAID;
            case "N": return PrepaidStatus.NOT_PREPAID;
            default: return PrepaidStatus.UNKNOWN;
        }
    }
}
