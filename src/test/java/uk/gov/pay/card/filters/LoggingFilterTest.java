package uk.gov.pay.card.filters;


import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Appender;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.MetricRegistry;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.LoggerFactory;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static java.lang.String.format;
import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static uk.gov.pay.card.filters.LoggingFilter.HEADER_REQUEST_ID;

@RunWith(MockitoJUnitRunner.class)
public class LoggingFilterTest {

    private LoggingFilter loggingFilter;

    @Mock
    HttpServletRequest mockRequest;

    @Mock
    HttpServletResponse mockResponse;

    @Mock
    FilterChain mockFilterChain;

    @Mock
    MetricRegistry mockMetricRegistry;

    @Mock
    private Histogram mockHistogram;

    private Appender<ILoggingEvent> mockAppender;

    @Captor
    ArgumentCaptor<LoggingEvent> loggingEventArgumentCaptor;


    @Before
    public void setup() {
        loggingFilter = new LoggingFilter(mockMetricRegistry);
        Logger root = (Logger) LoggerFactory.getLogger(LoggingFilter.class);
        mockAppender = mockAppender();
        root.addAppender(mockAppender);

        when(mockMetricRegistry.histogram(anyString())).thenReturn(mockHistogram);
    }

    @Test
    public void shouldLogEntryAndExitPointsOfEndPoints() throws Exception {

        String requestId = UUID.randomUUID().toString();
        String requestUrl = "/cardid-request";
        String requestMethod = "POST";

        when(mockRequest.getRequestURI()).thenReturn(requestUrl);
        when(mockRequest.getMethod()).thenReturn(requestMethod);
        when(mockRequest.getHeader(HEADER_REQUEST_ID)).thenReturn(requestId);

        loggingFilter.doFilter(mockRequest, mockResponse, mockFilterChain);

        verify(mockAppender, times(2)).doAppend(loggingEventArgumentCaptor.capture());
        List<LoggingEvent> loggingEvents = loggingEventArgumentCaptor.getAllValues();

        assertEquals(format("[%s] - %s to %s began", requestId, requestMethod, requestUrl), loggingEvents.get(0).getFormattedMessage());
        String endLogMessage = loggingEvents.get(1).getFormattedMessage();
        assertThat(endLogMessage, startsWith(format("[%s] - %s to %s ended - total time ", requestId, requestMethod, requestUrl)));
        String[] timeTaken = StringUtils.substringsBetween(endLogMessage, "total time ", "ms");
        assertTrue(NumberUtils.isCreatable(timeTaken[0]));
        verify(mockFilterChain).doFilter(mockRequest, mockResponse);
    }

    @Test
    public void shouldLogEntryAndExitPointsEvenIfRequestIdDoesNotExist() throws Exception {

        String requestUrl = "/cardid-request";
        String requestMethod = "POST";

        when(mockRequest.getRequestURI()).thenReturn(requestUrl);
        when(mockRequest.getMethod()).thenReturn(requestMethod);

        loggingFilter.doFilter(mockRequest, mockResponse, mockFilterChain);

        verify(mockAppender, times(2)).doAppend(loggingEventArgumentCaptor.capture());
        List<LoggingEvent> loggingEvents = loggingEventArgumentCaptor.getAllValues();

        assertEquals(format("[%s] - %s to %s began", "", requestMethod, requestUrl), loggingEvents.get(0).getFormattedMessage());
        String endLogMessage = loggingEvents.get(1).getFormattedMessage();
        assertThat(endLogMessage, startsWith(format("[%s] - %s to %s ended - total time ", "", requestMethod, requestUrl)));
        verify(mockFilterChain).doFilter(mockRequest, mockResponse);
    }

    @Test
    public void shouldLogEntryAndExitPointsEvenWhenFilterChainingThrowsException() throws Exception {
        String requestUrl = "/cardid-url-with-exception";
        String requestMethod = "POST";
        String requestId = UUID.randomUUID().toString();

        when(mockRequest.getRequestURI()).thenReturn(requestUrl);
        when(mockRequest.getMethod()).thenReturn(requestMethod);
        when(mockRequest.getHeader(HEADER_REQUEST_ID)).thenReturn(requestId);

        IOException exception = new IOException("Failed request");
        doThrow(exception).when(mockFilterChain).doFilter(mockRequest, mockResponse);

        loggingFilter.doFilter(mockRequest, mockResponse, mockFilterChain);

        verify(mockAppender, times(3)).doAppend(loggingEventArgumentCaptor.capture());
        List<LoggingEvent> loggingEvents = loggingEventArgumentCaptor.getAllValues();

        assertEquals(format("[%s] - %s to %s began", requestId, requestMethod, requestUrl), loggingEvents.get(0).getFormattedMessage());
        assertEquals(format("Exception - cardid request - %s - exception - %s", requestUrl, exception.getMessage()), loggingEvents.get(1).getFormattedMessage());
        assertEquals(Level.ERROR, loggingEvents.get(1).getLevel());
        assertEquals("Failed request", loggingEvents.get(1).getThrowableProxy().getMessage());
        String endLogMessage = loggingEvents.get(2).getFormattedMessage();
        assertThat(endLogMessage, startsWith(format("[%s] - %s to %s ended - total time ", requestId, requestMethod, requestUrl)));
        String[] timeTaken = StringUtils.substringsBetween(endLogMessage, "total time ", "ms");
        assertTrue(NumberUtils.isCreatable(timeTaken[0]));
    }

    @Test
    public void shouldLogRequestTimesAsMetrics() {
        String requestUrl = "/cardid-request";
        String requestMethod = "POST";

        when(mockRequest.getRequestURI()).thenReturn(requestUrl);
        when(mockRequest.getMethod()).thenReturn(requestMethod);

        loggingFilter.doFilter(mockRequest, mockResponse, mockFilterChain);

        verify(mockHistogram).update(anyLong());
    }

    @SuppressWarnings("unchecked")
    private <T> Appender<T> mockAppender() {
        return mock(Appender.class);
    }

}
