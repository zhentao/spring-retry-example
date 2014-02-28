package com.zhentao.retry;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;


@Configuration
@PropertySource(value = { "file:${runtime.properties}" })
public class SpringConfig {
    @Value("${retry.max.attempts}")
    private int retryMaxAttempts;

    @Value("${retry.initial.interval}")
    private long retryInitialInterval;

    @Value("${socket.timeout}")
    private int socketTimeout;

    @Value("${connect.timeout}")
    private int connectTimeout;

    @Value("${connection.request.timeout}")
    private int connectionRequestTimeout;

    @Value("${meta.data.url}")
    private String metaDataUrl;

    @Value("${file.upload.url}")
    private String fileUploadUrl;

    @Bean
    public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
        // search local properties last by default
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public RetryTemplate retryTemplate() {
        final RetryTemplate template = new RetryTemplate();

        final Map<Class<? extends Throwable>, Boolean> retryableExceptions = new HashMap<>();
        retryableExceptions.put(UploadException.class, true);
        template.setRetryPolicy(new SimpleRetryPolicy(retryMaxAttempts, retryableExceptions));

        final ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(retryInitialInterval);
        template.setBackOffPolicy(backOffPolicy);

        return template;
    }

    @Bean(destroyMethod = "close")
    public CloseableHttpClient httpClient() {
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(socketTimeout)
                                        .setConnectTimeout(connectTimeout)
                                        .setConnectionRequestTimeout(connectionRequestTimeout)
                                        .setStaleConnectionCheckEnabled(true).build();
        return HttpClients.custom().setDefaultRequestConfig(requestConfig).build();
    }

    @Bean
    public UploadClient uploadClient() {
        return new UploadClient(httpClient(), metaDataUrl, fileUploadUrl, retryTemplate());
    }
}
