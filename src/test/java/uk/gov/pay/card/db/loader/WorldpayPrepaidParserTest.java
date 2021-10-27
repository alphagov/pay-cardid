package uk.gov.pay.card.db.loader;

import org.junit.jupiter.api.Test;
import uk.gov.pay.card.model.PrepaidStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class WorldpayPrepaidParserTest {

    @Test
    public void shouldReturnPrepaidForY() {
        assertEquals(WorldpayPrepaidParser.parse("Y"), PrepaidStatus.PREPAID);
    }

    @Test
    public void shouldReturnPrepaidForN() {
        assertEquals(WorldpayPrepaidParser.parse("N"), PrepaidStatus.NOT_PREPAID);
    }

    @Test
    public void shouldReturnUnknownForU() {
        assertEquals(WorldpayPrepaidParser.parse("U"), PrepaidStatus.UNKNOWN);
    }

    @Test
    public void shouldReturnUnknownForNull() {
        assertEquals(WorldpayPrepaidParser.parse(null), PrepaidStatus.UNKNOWN);
    }

    @Test
    public void shouldReturnUnknownForEmptyString() {
        assertEquals(WorldpayPrepaidParser.parse(""), PrepaidStatus.UNKNOWN);
    }

    @Test
    public void shouldReturnUnknownForYES() {
        assertEquals(WorldpayPrepaidParser.parse("YES"), PrepaidStatus.UNKNOWN);
    }

    @Test
    public void shouldReturnUnknownForNO() {
        assertEquals(WorldpayPrepaidParser.parse("NO"), PrepaidStatus.UNKNOWN);
    }
}
