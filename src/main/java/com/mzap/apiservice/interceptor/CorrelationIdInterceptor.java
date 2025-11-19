package com.mzap.apiservice.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

@Component
public class CorrelationIdInterceptor implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(CorrelationIdInterceptor.class);
    public static final String CORRELATION_ID_HEADER = "X-Correlation-ID";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String correlationId = request.getHeader(CORRELATION_ID_HEADER);

        if(correlationId == null || correlationId.isEmpty()) {
            correlationId = UUID.randomUUID().toString();
            logger.debug("Generated new {}: {}", CORRELATION_ID_HEADER, correlationId);
        } else {
            logger.debug("Used existing {}: {}", CORRELATION_ID_HEADER, correlationId);
        }


        MDC.put(CORRELATION_ID_HEADER, correlationId);
        response.setHeader(CORRELATION_ID_HEADER, correlationId);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        MDC.remove(CORRELATION_ID_HEADER);
    }
}
