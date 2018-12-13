package uk.gov.pay.card.db.loader;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import uk.gov.pay.card.db.loader.WorldpayPrepaidParser;
import uk.gov.pay.card.model.PrepaidStatus;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;

public class WorldpayPrepaidParserTest {

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void shouldReturnPrepaidForY() throws Exception {
        assertThat(WorldpayPrepaidParser.parse("Y"), equalTo(PrepaidStatus.PREPAID));
    }

    @Test
    public void shouldReturnPrepaidForN() throws Exception {
        assertThat(WorldpayPrepaidParser.parse("N"), equalTo(PrepaidStatus.NOT_PREPAID));
    }

    @Test
    public void shouldReturnUnknownForU() throws Exception {
        assertThat(WorldpayPrepaidParser.parse("U"), equalTo(PrepaidStatus.UNKNOWN));
    }

    @Test
    public void shouldReturnUnknownForNull() throws Exception {
        assertThat(WorldpayPrepaidParser.parse(null), equalTo(PrepaidStatus.UNKNOWN));
    }

    @Test
    public void shouldReturnUnknownForEmptyString() throws Exception {
        assertThat(WorldpayPrepaidParser.parse(""), equalTo(PrepaidStatus.UNKNOWN));
    }

    @Test
    public void shouldReturnUnknownForYES() throws Exception {
        assertThat(WorldpayPrepaidParser.parse("YES"), equalTo(PrepaidStatus.UNKNOWN));
    }

    @Test
    public void shouldReturnUnknownForNO() throws Exception {
        assertThat(WorldpayPrepaidParser.parse("NO"), equalTo(PrepaidStatus.UNKNOWN));
    }
}
