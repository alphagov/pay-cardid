package uk.gov.pay.card.filters;

import com.codahale.metrics.Histogram;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SharedMetricRegistries;
import com.google.common.base.Stopwatch;
import org.apache.commons.lang3.StringUtils;
import io.dropwizard.setup.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.*;

public class LoggingFilter implements Filter {

    public static final String HEADER_REQUEST_ID = "X-Request-Id";
    private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);
    private Histogram histogram;

    public LoggingFilter(Environment environment) {
        histogram = environment.metrics().histogram("request-times");
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        Stopwatch stopwatch = Stopwatch.createStarted();

        String requestURL = ((HttpServletRequest) servletRequest).getRequestURI();
        String requestMethod = ((HttpServletRequest) servletRequest).getMethod();
        String requestId = defaultString(((HttpServletRequest) servletRequest).getHeader(HEADER_REQUEST_ID));


        logger.info(format("[%s] - %s to %s began", requestId, requestMethod, requestURL));
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } catch (Throwable throwable) {
            logger.error("Exception - cardid request - " + requestURL + " - exception - " + throwable.getMessage(), throwable);
        } finally {
            long elapsed = stopwatch.elapsed(TimeUnit.MILLISECONDS);
            logger.info(format("[%s] - %s to %s ended - total time %dms", requestId, requestMethod, requestURL,
                    elapsed));
            stopwatch.stop();
            histogram.update(elapsed);
        }
    }

    @Override
    public void destroy() {
        logger.warn("Destroying logging filter");
    }
}
