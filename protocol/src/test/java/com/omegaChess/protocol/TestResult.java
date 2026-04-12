package com.omegaChess.protocol;

import com.omegaChess.protocol.messages.FailureResponse;
import com.omegaChess.protocol.messages.SimpleSuccessResponse;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link Result} — the typed success/failure wrapper.
 */
@DisplayName("Result Tests")
public class TestResult {

    @Test
    void testOkResult() {
        SimpleSuccessResponse value = new SimpleSuccessResponse();
        Result<SimpleSuccessResponse> result = Result.ok(value);

        assertTrue(result.isSuccess());
        assertNotNull(result.getSuccess());
        assertNull(result.getFailure());
        assertNull(result.getReason());
    }

    @Test
    void testFailResult() {
        FailureResponse failure = new FailureResponse("bad input");
        Result<SimpleSuccessResponse> result = Result.fail(failure);

        assertFalse(result.isSuccess());
        assertNull(result.getSuccess());
        assertNotNull(result.getFailure());
        assertEquals("bad input", result.getReason());
    }

    @Test
    void testFailResultReasonFromFailure() {
        FailureResponse failure = new FailureResponse("detailed error message");
        Result<SimpleSuccessResponse> result = Result.fail(failure);

        assertEquals("detailed error message", result.getReason());
        assertEquals("detailed error message", result.getFailure().getReason());
    }

    @Test
    void testOkResultGetSuccessReturnsCorrectType() {
        SimpleSuccessResponse value = new SimpleSuccessResponse();
        Result<SimpleSuccessResponse> result = Result.ok(value);

        SimpleSuccessResponse retrieved = result.getSuccess();
        assertSame(value, retrieved);
    }

    @Test
    void testFailResultGetReasonIsNull() {
        Result<SimpleSuccessResponse> result = Result.ok(new SimpleSuccessResponse());
        assertNull(result.getReason());
    }

    @Test
    void testOkWithDifferentTypes() {
        // Result can wrap any success type
        Result<FailureResponse> result = Result.ok(new FailureResponse("not actually a failure here"));
        assertTrue(result.isSuccess());
        assertEquals("not actually a failure here", result.getSuccess().getReason());
    }
}
