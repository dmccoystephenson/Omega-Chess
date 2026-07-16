package com.omegaChess.protocol;

import com.omegaChess.protocol.messages.FailureResponse;

// Immutable POJO — Java records require JDK 16+; this project targets JDK 7/8.
public final class Result<S> {
    private final S success;
    private final FailureResponse failure;

    private Result(S success, FailureResponse failure) {
        this.success = success;
        this.failure = failure;
    }

    public static <S> Result<S> ok(S value) {
        return new Result<S>(value, null);
    }

    public static <S> Result<S> fail(FailureResponse failure) {
        return new Result<S>(null, failure);
    }

    public boolean isSuccess() { return success != null; }
    public S getSuccess() { return success; }
    public FailureResponse getFailure() { return failure; }

    /**
     * Get the reason string from a failure response, or null if this is a success.
     */
    public String getReason() {
        return failure != null ? failure.getReason() : null;
    }
}
