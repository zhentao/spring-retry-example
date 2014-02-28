package com.zhentao.retry;

public class UploadException extends RuntimeException {
    private static final long serialVersionUID = 5104726300316178796L;
    private static final String BAD_STATUS_ERROR = "Failed to upload to uri: %s with status code %d and error: %s";
    private static final String UNEXPECTED_ERROR = "Failed to upload to uri: %s wtih unexpected error";

    public UploadException(String uri, int statusCode, String message) {
        super(String.format(BAD_STATUS_ERROR, uri, statusCode, message));
    }

    public UploadException(String uri, Throwable t) {
        super(String.format(UNEXPECTED_ERROR, uri), t);
    }
}
