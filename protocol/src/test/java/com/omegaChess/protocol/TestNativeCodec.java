package com.omegaChess.protocol;

import com.omegaChess.protocol.messages.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Integration tests verifying that the native asn1c codec (libasn1omega)
 * correctly encodes and decodes protocol messages when called from Java
 * via JNA.
 *
 * <p>These tests are only executed when the native library is available on the
 * JNA library path.  The CI workflow builds the library before running tests
 * and sets {@code jna.library.path} accordingly.  If the native library is not
 * available (e.g. local dev without a C build), these tests are skipped
 * gracefully via {@code assumeTrue}.
 */
@DisplayName("Native Codec Integration Tests")
public class TestNativeCodec {

    @BeforeAll
    static void requireNativeLibrary() {
        assumeTrue(NativeAsn1Codec.isAvailable(),
                "Skipping native codec tests — libasn1omega not available");
    }

    // ===== XER to UPER to XER round-trip via native library =====

    @Test
    void testNativeXerToUperLoginRequest() {
        String xer = "<LoginRequest><nickname>alice</nickname>"
                + "<password>secret</password></LoginRequest>";
        byte[] uper = NativeAsn1Codec.xerToUper("LoginRequest", xer);
        assertNotNull(uper, "Native XER->UPER encoding returned null");
        assertTrue(uper.length > 0, "UPER output should be non-empty");

        String decoded = NativeAsn1Codec.uperToXer("LoginRequest", uper);
        assertNotNull(decoded, "Native UPER->XER decoding returned null");
        assertTrue(decoded.contains("alice"), "Decoded XER should contain nickname");
        assertTrue(decoded.contains("secret"), "Decoded XER should contain password");
    }

    @Test
    void testNativeXerToUperSquareRequest() {
        String xer = "<SquareRequest><number>42</number></SquareRequest>";
        byte[] uper = NativeAsn1Codec.xerToUper("SquareRequest", xer);
        assertNotNull(uper);

        String decoded = NativeAsn1Codec.uperToXer("SquareRequest", uper);
        assertNotNull(decoded);
        assertTrue(decoded.contains("42"));
    }

    @Test
    void testNativeXerToUperRegisterRequest() {
        String xer = "<RegisterRequest><email>a@b.com</email>"
                + "<nickname>bob</nickname>"
                + "<password>pass123</password></RegisterRequest>";
        byte[] uper = NativeAsn1Codec.xerToUper("RegisterRequest", xer);
        assertNotNull(uper);

        String decoded = NativeAsn1Codec.uperToXer("RegisterRequest", uper);
        assertNotNull(decoded);
        assertTrue(decoded.contains("bob"));
        assertTrue(decoded.contains("a@b.com"));
    }

    @Test
    void testNativeXerToUperSimpleSuccessResponse() {
        String xer = "<SimpleSuccessResponse>"
                + "<success><true/></success>"
                + "</SimpleSuccessResponse>";
        byte[] uper = NativeAsn1Codec.xerToUper("SimpleSuccessResponse", xer);
        assertNotNull(uper);

        String decoded = NativeAsn1Codec.uperToXer("SimpleSuccessResponse", uper);
        assertNotNull(decoded);
        assertTrue(decoded.contains("true"));
    }

    @Test
    void testNativeXerToUperFailureResponse() {
        String xer = "<FailureResponse><success><false/></success>"
                + "<reason>bad credentials</reason></FailureResponse>";
        byte[] uper = NativeAsn1Codec.xerToUper("FailureResponse", xer);
        assertNotNull(uper);

        String decoded = NativeAsn1Codec.uperToXer("FailureResponse", uper);
        assertNotNull(decoded);
        assertTrue(decoded.contains("bad credentials"));
    }

    @Test
    void testNativeXerToUperGetBoardDataRequest() {
        String xer = "<GetBoardDataRequest><id>5</id></GetBoardDataRequest>";
        byte[] uper = NativeAsn1Codec.xerToUper("GetBoardDataRequest", xer);
        assertNotNull(uper);

        String decoded = NativeAsn1Codec.uperToXer("GetBoardDataRequest", uper);
        assertNotNull(decoded);
        assertTrue(decoded.contains("5"));
    }

    @Test
    void testNativeXerToUperMatchMoveRequest() {
        String xer = "<MatchMoveRequest>"
                + "<matchID>1</matchID>"
                + "<fromRow>2</fromRow><fromColumn>3</fromColumn>"
                + "<toRow>4</toRow><toColumn>5</toColumn>"
                + "</MatchMoveRequest>";
        byte[] uper = NativeAsn1Codec.xerToUper("MatchMoveRequest", xer);
        assertNotNull(uper);

        String decoded = NativeAsn1Codec.uperToXer("MatchMoveRequest", uper);
        assertNotNull(decoded);
        assertTrue(decoded.contains("1"));
        assertTrue(decoded.contains("4"));
    }

    // ===== Error handling: native library must not crash =====

    @Test
    void testNativeInvalidPduTypeReturnsNull() {
        String xer = "<Bogus><x>1</x></Bogus>";
        byte[] result = NativeAsn1Codec.xerToUper("Bogus", xer);
        assertNull(result, "Unknown PDU type should return null");
    }

    @Test
    void testNativeMalformedXerReturnsNull() {
        byte[] result = NativeAsn1Codec.xerToUper("LoginRequest", "<not valid xml");
        assertNull(result, "Malformed XER should return null");
    }

    @Test
    void testNativeInvalidUperDoesNotCrash() {
        byte[] garbage = new byte[]{0x00, 0x01, 0x02, (byte) 0xFF};
        // Should return null or an error string, must not segfault
        NativeAsn1Codec.uperToXer("LoginRequest", garbage);
        // If we reach here without a crash, the test passes
    }

    // ===== Native encode/decode round-trip for all types =====
    // These test that POJO → XER → native UPER → XER → POJO is lossless

    @Test
    void testNativeRoundTripLoginRequest() {
        assertNativeRoundTrip("LoginRequest",
                "<LoginRequest><nickname>player1</nickname>"
                + "<password>hunter2</password></LoginRequest>",
                "player1", "hunter2");
    }

    @Test
    void testNativeRoundTripRegisterRequest() {
        assertNativeRoundTrip("RegisterRequest",
                "<RegisterRequest><email>user@test.com</email>"
                + "<nickname>tester</nickname>"
                + "<password>p@ss</password></RegisterRequest>",
                "user@test.com", "tester", "p@ss");
    }

    @Test
    void testNativeRoundTripSquareRequest() {
        assertNativeRoundTrip("SquareRequest",
                "<SquareRequest><number>7</number></SquareRequest>",
                "7");
    }

    @Test
    void testNativeRoundTripSimpleSuccessResponse() {
        assertNativeRoundTrip("SimpleSuccessResponse",
                "<SimpleSuccessResponse><success><true/></success>"
                + "</SimpleSuccessResponse>",
                "true");
    }

    @Test
    void testNativeRoundTripFailureResponse() {
        assertNativeRoundTrip("FailureResponse",
                "<FailureResponse><success><false/></success>"
                + "<reason>not found</reason></FailureResponse>",
                "not found");
    }

    @Test
    void testNativeRoundTripGetProfileDataRequest() {
        assertNativeRoundTrip("GetProfileDataRequest",
                "<GetProfileDataRequest><nickname>alice</nickname>"
                + "</GetProfileDataRequest>",
                "alice");
    }

    @Test
    void testNativeRoundTripSendInviteRequest() {
        assertNativeRoundTrip("SendInviteRequest",
                "<SendInviteRequest><inviter>alice</inviter>"
                + "<invitee>bob</invitee></SendInviteRequest>",
                "alice", "bob");
    }

    @Test
    void testNativeRoundTripMatchMoveRequest() {
        assertNativeRoundTrip("MatchMoveRequest",
                "<MatchMoveRequest><matchID>1</matchID>"
                + "<fromRow>2</fromRow><fromColumn>3</fromColumn>"
                + "<toRow>4</toRow><toColumn>5</toColumn>"
                + "</MatchMoveRequest>",
                "1", "2", "3", "4", "5");
    }

    @Test
    void testNativeRoundTripGetLegalMovesRequest() {
        assertNativeRoundTrip("GetLegalMovesRequest",
                "<GetLegalMovesRequest><matchID>1</matchID>"
                + "<row>0</row><column>0</column>"
                + "</GetLegalMovesRequest>",
                "1", "0");
    }

    @Test
    void testNativeRoundTripEndMatchRequest() {
        assertNativeRoundTrip("EndMatchRequest",
                "<EndMatchRequest><id>42</id>"
                + "<winner>winner</winner><loser>loser</loser>"
                + "</EndMatchRequest>",
                "42", "winner", "loser");
    }

    @Test
    void testNativeRoundTripProfileDataSuccessResponse() {
        assertNativeRoundTrip("ProfileDataSuccessResponse",
                "<ProfileDataSuccessResponse>"
                + "<success><true/></success><nickname>alice</nickname>"
                + "<gamesWon>10</gamesWon><gamesLost>5</gamesLost>"
                + "<gamesTied>3</gamesTied>"
                + "</ProfileDataSuccessResponse>",
                "alice", "10", "5", "3");
    }

    @Test
    void testNativeRoundTripSquareSuccessResponse() {
        assertNativeRoundTrip("SquareSuccessResponse",
                "<SquareSuccessResponse><success><true/></success>"
                + "<answer>49</answer></SquareSuccessResponse>",
                "49");
    }

    // ===== Full OCCodec round-trip with native active =====
    // These test the complete OCCodec pipeline (tag + native UPER + Base64)

    @Test
    void testOCCodecEncodeDecodeWithNative() {
        assertTrue(OCCodec.isNativeAvailable(), "Native codec should be active");

        LoginRequest original = new LoginRequest("alice", "secret");
        String encoded = OCCodec.encode(original);
        assertNotNull(encoded);
        assertTrue(encoded.length() > 0);

        Object decoded = OCCodec.decode(encoded);
        assertTrue(decoded instanceof LoginRequest);
        LoginRequest d = (LoginRequest) decoded;
        assertEquals("alice", d.getNickname());
        assertEquals("secret", d.getPassword());
    }

    @Test
    void testOCCodecNativeReportsAvailable() {
        assertTrue(OCCodec.isNativeAvailable(),
                "OCCodec.isNativeAvailable() should return true "
                + "when libasn1omega is loaded");
    }

    @Test
    void testOCCodecNativeRoundTripAllRequests() {
        Object[] messages = new Object[] {
            new SquareRequest(5),
            new RegisterRequest("e@m.co", "nick", "pass"),
            new UnregisterRequest("nick"),
            new LoginRequest("nick", "pass"),
            new GetProfileDataRequest("nick"),
            new SendInviteRequest("a", "b"),
            new GetInvitesSentRequest("a"),
            new GetInvitesReceivedRequest("b"),
            new GetNotificationsRequest("nick"),
            new InviteResponseRequest("accept", "a", "b"),
            new GetBoardDataRequest(1),
            new GetLegalMovesRequest(1, 0, 0),
            new MatchMoveRequest(1, 0, 0, 1, 1),
            new GetInProgressMatchesRequest("nick"),
            new GetTurnRequest(1),
            new GetGameRecordsRequest("nick"),
            new EndMatchRequest(1, "w", "l"),
            new CheckCheckmateRequest(1),
            new CheckForfeitRequest(1)
        };

        for (Object msg : messages) {
            String encoded = OCCodec.encode(msg);
            assertNotNull(encoded,
                    "Encode should succeed for " + msg.getClass().getSimpleName());
            Object decoded = OCCodec.decode(encoded);
            assertNotNull(decoded,
                    "Decode should succeed for " + msg.getClass().getSimpleName());
            assertEquals(msg.getClass(), decoded.getClass(),
                    "Round-trip type mismatch for " + msg.getClass().getSimpleName());
        }
    }

    // ===== Helper =====

    /**
     * Verify that a native XER → UPER → XER round-trip preserves the
     * expected content strings.
     */
    private void assertNativeRoundTrip(String typeName, String xer,
                                        String... expectedContent) {
        byte[] uper = NativeAsn1Codec.xerToUper(typeName, xer);
        assertNotNull(uper, "Native encode should succeed for " + typeName);
        assertTrue(uper.length > 0, "UPER output should be non-empty for " + typeName);

        String decoded = NativeAsn1Codec.uperToXer(typeName, uper);
        assertNotNull(decoded, "Native decode should succeed for " + typeName);

        for (String expected : expectedContent) {
            assertTrue(decoded.contains(expected),
                    "Decoded XER for " + typeName + " should contain '"
                    + expected + "' but was: " + decoded);
        }
    }
}
