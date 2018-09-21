package uk.gov.pay.card.filters;

import com.codahale.metrics.MetricRegistry;
import com.google.common.base.Stopwatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

import static org.apache.commons.lang3.StringUtils.defaultString;

public class LoggingFilter implements Filter {

    static final String HEADER_REQUEST_ID = "X-Request-Id";
    private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);
    private final MetricRegistry metricsRegistry;

    public LoggingFilter(MetricRegistry metrics) {
        this.metricsRegistry = metrics;
    }


    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) {

        Stopwatch stopwatch = Stopwatch.createStarted();

        String requestURL = ((HttpServletRequest) servletRequest).getRequestURI();
        String requestMethod = ((HttpServletRequest) servletRequest).getMethod();
        String requestId = defaultString(((HttpServletRequest) servletRequest).getHeader(HEADER_REQUEST_ID));

        MDC.put(HEADER_REQUEST_ID, requestId);

        logger.info("[{}] - {} to {} began", requestId, requestMethod, requestURL);
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } catch (Exception e) {
            logger.error("Exception - cardid request - " + requestURL + " - exception - " + e.getMessage(), e);
        } finally {
            long elapsed = stopwatch.elapsed(TimeUnit.MILLISECONDS);
            logger.info("[{}] - {} to {} ended - total time {}ms", requestId, requestMethod, requestURL, elapsed);
            stopwatch.stop();
            metricsRegistry.histogram("response-times").update(elapsed);
        }
    }

    @Override
    public void destroy() {
        logger.warn("Destroying logging filter");
    }
}
