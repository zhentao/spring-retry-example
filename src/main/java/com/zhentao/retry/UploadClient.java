package com.zhentao.retry;

import java.io.File;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.util.MimeTypeUtils;

public class UploadClient {
    private static final Logger LOG = LoggerFactory.getLogger(UploadClient.class);
    private final CloseableHttpClient httpClient;
    private final String metaDataUrl;
    private final String fileUploadUrl;
    private final RetryTemplate template;

    public UploadClient(CloseableHttpClient httpClient, String metaDataUrl, String fileUploadUrl,
                                    final RetryTemplate template) {
        this.httpClient = httpClient;
        this.metaDataUrl = metaDataUrl;
        this.fileUploadUrl = fileUploadUrl;
        this.template = template;
    }

    public void upload(final File data, final File metadata) {
        try {
            template.execute(new RetryCallback<Boolean>() {
                @Override
                public Boolean doWithRetry(final RetryContext context) throws Exception {
                    return putDataSet(data);
                }
            });

            template.execute(new RetryCallback<Boolean>() {
                @Override
                public Boolean doWithRetry(final RetryContext context) throws Exception {
                    return postMetadata(metadata);
                }
            });
        } catch (Exception e) {
            LOG.error("error uploading file", e);
        }
    }

    Boolean putDataSet(File data) {
        String uri = fileUploadUrl;
        HttpUriRequest request = RequestBuilder.put().setUri(uri)
                                        .setHeader(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_XML_VALUE)
                                        .build();

        execute(uri, request);
        return Boolean.TRUE;
    }

    private void execute(String uri, HttpUriRequest request) {
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode >= HttpStatus.SC_BAD_REQUEST) {
                String content = EntityUtils.toString(response.getEntity());
                throw new UploadException(uri, statusCode, content);
            }
        } catch (Exception e) {
            throw new UploadException(uri, e);
        }
    }

    Boolean postMetadata(File metadata) {
        String uri = metaDataUrl;
        HttpUriRequest request = RequestBuilder.post().setUri(uri)
                                        .setHeader(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_XML_VALUE)
                                        .build();
        execute(uri, request);
        return Boolean.TRUE;
    }
}
