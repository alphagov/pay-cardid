package uk.gov.pay.card.db.loader;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import uk.gov.pay.card.model.PrepaidStatus;

import static org.junit.Assert.assertEquals;

public class WorldpayPrepaidParserTest {

    @Rule
    public final ExpectedException exception = ExpectedException.none();

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
