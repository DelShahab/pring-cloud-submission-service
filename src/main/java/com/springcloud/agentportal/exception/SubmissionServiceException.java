package com.springcloud.agentportal.exception;

/**
 * Custom exception for submission service related errors
 */
public class SubmissionServiceException extends RuntimeException {

    public SubmissionServiceException(String message) {
        super(message);
    }

    public SubmissionServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
