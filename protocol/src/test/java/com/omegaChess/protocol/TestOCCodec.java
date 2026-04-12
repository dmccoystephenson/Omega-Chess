package com.omegaChess.protocol;

import com.omegaChess.protocol.messages.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link OCCodec} — the primary encode/decode entry point
 * covering round-trip serialization, edge cases, and error handling.
 */
@DisplayName("OCCodec Tests")
public class TestOCCodec {

    // ===================== Round-trip: requests =====================

    @Test
    void testSquareRequestRoundTrip() {
        SquareRequest msg = new SquareRequest(0);
        Object decoded = OCCodec.decode(OCCodec.encode(msg));
        assertTrue(decoded instanceof SquareRequest);
        assertEquals(0, ((SquareRequest) decoded).getNumber());
    }

    @Test
    void testSquareRequestLargeNumber() {
        SquareRequest msg = new SquareRequest(999999);
        Object decoded = OCCodec.decode(OCCodec.encode(msg));
        assertTrue(decoded instanceof SquareRequest);
        assertEquals(999999, ((SquareRequest) decoded).getNumber());
    }

    @Test
    void testRegisterRequestRoundTrip() {
        RegisterRequest msg = new RegisterRequest("user@example.com", "testUser", "s3cur3P@ss");
        Object decoded = OCCodec.decode(OCCodec.encode(msg));
        assertTrue(decoded instanceof RegisterRequest);
        RegisterRequest d = (RegisterRequest) decoded;
        assertEquals("user@example.com", d.getEmail());
        assertEquals("testUser", d.getNickname());
        assertEquals("s3cur3P@ss", d.getPassword());
    }

    @Test
    void testLoginRequestRoundTrip() {
        LoginRequest msg = new LoginRequest("player1", "hunter2");
        Object decoded = OCCodec.decode(OCCodec.encode(msg));
        assertTrue(decoded instanceof LoginRequest);
        LoginRequest d = (LoginRequest) decoded;
        assertEquals("player1", d.getNickname());
        assertEquals("hunter2", d.getPassword());
    }

    @Test
    void testUnregisterRequestRoundTrip() {
        UnregisterRequest msg = new UnregisterRequest("userToRemove");
        Object decoded = OCCodec.decode(OCCodec.encode(msg));
        assertTrue(decoded instanceof UnregisterRequest);
        assertEquals("userToRemove", ((UnregisterRequest) decoded).getNickname());
    }

    @Test
    void testSendInviteRequestRoundTrip() {
        SendInviteRequest msg = new SendInviteRequest("alice", "bob");
        Object decoded = OCCodec.decode(OCCodec.encode(msg));
        assertTrue(decoded instanceof SendInviteRequest);
        SendInviteRequest d = (SendInviteRequest) decoded;
        assertEquals("alice", d.getInviter());
        assertEquals("bob", d.getInvitee());
    }

    @Test
    void testGetLegalMovesRequestRoundTrip() {
        GetLegalMovesRequest msg = new GetLegalMovesRequest(42, 7, 3);
        Object decoded = OCCodec.decode(OCCodec.encode(msg));
        assertTrue(decoded instanceof GetLegalMovesRequest);
        GetLegalMovesRequest d = (GetLegalMovesRequest) decoded;
        assertEquals(42, d.getMatchID());
        assertEquals(7, d.getRow());
        assertEquals(3, d.getColumn());
    }

    @Test
    void testMatchMoveRequestRoundTrip() {
        MatchMoveRequest msg = new MatchMoveRequest(100, 2, 3, 4, 5);
        Object decoded = OCCodec.decode(OCCodec.encode(msg));
        assertTrue(decoded instanceof MatchMoveRequest);
        MatchMoveRequest d = (MatchMoveRequest) decoded;
        assertEquals(100, d.getMatchID());
        assertEquals(2, d.getFromRow());
        assertEquals(3, d.getFromColumn());
        assertEquals(4, d.getToRow());
        assertEquals(5, d.getToColumn());
    }

    @Test
    void testEndMatchRequestRoundTrip() {
        EndMatchRequest msg = new EndMatchRequest(77, "winner", "loser");
        Object decoded = OCCodec.decode(OCCodec.encode(msg));
        assertTrue(decoded instanceof EndMatchRequest);
        EndMatchRequest d = (EndMatchRequest) decoded;
        assertEquals(77, d.getId());
        assertEquals("winner", d.getWinner());
        assertEquals("loser", d.getLoser());
    }

    @Test
    void testInviteResponseRequestRoundTrip() {
        InviteResponseRequest msg = new InviteResponseRequest("decline", "inviterX", "inviteeY");
        Object decoded = OCCodec.decode(OCCodec.encode(msg));
        assertTrue(decoded instanceof InviteResponseRequest);
        InviteResponseRequest d = (InviteResponseRequest) decoded;
        assertEquals("decline", d.getResponse());
        assertEquals("inviterX", d.getInviter());
        assertEquals("inviteeY", d.getInvitee());
    }

    @Test
    void testCheckCheckmateRequestRoundTrip() {
        CheckCheckmateRequest msg = new CheckCheckmateRequest(55);
        Object decoded = OCCodec.decode(OCCodec.encode(msg));
        assertTrue(decoded instanceof CheckCheckmateRequest);
        assertEquals(55, ((CheckCheckmateRequest) decoded).getId());
    }

    @Test
    void testCheckForfeitRequestRoundTrip() {
        CheckForfeitRequest msg = new CheckForfeitRequest(66);
        Object decoded = OCCodec.decode(OCCodec.encode(msg));
        assertTrue(decoded instanceof CheckForfeitRequest);
        assertEquals(66, ((CheckForfeitRequest) decoded).getId());
    }

    // ===================== Round-trip: responses =====================

    @Test
    void testSimpleSuccessResponseRoundTrip() {
        SimpleSuccessResponse msg = new SimpleSuccessResponse();
        Object decoded = OCCodec.decode(OCCodec.encode(msg));
        assertTrue(decoded instanceof SimpleSuccessResponse);
        assertTrue(((SimpleSuccessResponse) decoded).isSuccess());
    }

    @Test
    void testFailureResponseRoundTrip() {
        FailureResponse msg = new FailureResponse("Something went wrong");
        Object decoded = OCCodec.decode(OCCodec.encode(msg));
        assertTrue(decoded instanceof FailureResponse);
        FailureResponse d = (FailureResponse) decoded;
        assertFalse(d.isSuccess());
        assertEquals("Something went wrong", d.getReason());
    }

    @Test
    void testSquareSuccessResponseRoundTrip() {
        SquareSuccessResponse msg = new SquareSuccessResponse("Square of 5 is 25");
        Object decoded = OCCodec.decode(OCCodec.encode(msg));
        assertTrue(decoded instanceof SquareSuccessResponse);
        assertEquals("Square of 5 is 25", ((SquareSuccessResponse) decoded).getAnswer());
    }

    @Test
    void testTurnSuccessResponseRoundTrip() {
        TurnSuccessResponse msg = new TurnSuccessResponse(true, "player1", "Black");
        Object decoded = OCCodec.decode(OCCodec.encode(msg));
        assertTrue(decoded instanceof TurnSuccessResponse);
        TurnSuccessResponse d = (TurnSuccessResponse) decoded;
        assertTrue(d.isSuccess());
        assertEquals("player1", d.getUser());
        assertEquals("Black", d.getColor());
    }

    @Test
    void testLegalMovesWithEnPassant() {
        LegalMovesSuccessResponse msg = new LegalMovesSuccessResponse(true, "/e6/", true);
        Object decoded = OCCodec.decode(OCCodec.encode(msg));
        assertTrue(decoded instanceof LegalMovesSuccessResponse);
        LegalMovesSuccessResponse d = (LegalMovesSuccessResponse) decoded;
        assertEquals("/e6/", d.getLegalMoves());
        assertTrue(d.isEnPassant());
    }

    @Test
    void testLegalMovesWithoutEnPassant() {
        LegalMovesSuccessResponse msg = new LegalMovesSuccessResponse(true, "/a3/a4/", false);
        Object decoded = OCCodec.decode(OCCodec.encode(msg));
        assertTrue(decoded instanceof LegalMovesSuccessResponse);
        LegalMovesSuccessResponse d = (LegalMovesSuccessResponse) decoded;
        assertEquals("/a3/a4/", d.getLegalMoves());
        assertFalse(d.isEnPassant());
    }

    // ===================== Optional fields =====================

    @Test
    void testCheckmateSuccessResponseWithNulls() {
        CheckmateSuccessResponse msg = new CheckmateSuccessResponse(true, false, null, null);
        Object decoded = OCCodec.decode(OCCodec.encode(msg));
        assertTrue(decoded instanceof CheckmateSuccessResponse);
        CheckmateSuccessResponse d = (CheckmateSuccessResponse) decoded;
        assertFalse(d.isCheckmate());
        assertNull(d.getLoser());
        assertNull(d.getWinner());
    }

    @Test
    void testCheckmateSuccessResponseWithValues() {
        CheckmateSuccessResponse msg = new CheckmateSuccessResponse(true, true, "loser", "winner");
        Object decoded = OCCodec.decode(OCCodec.encode(msg));
        assertTrue(decoded instanceof CheckmateSuccessResponse);
        CheckmateSuccessResponse d = (CheckmateSuccessResponse) decoded;
        assertTrue(d.isCheckmate());
        assertEquals("loser", d.getLoser());
        assertEquals("winner", d.getWinner());
    }

    @Test
    void testInviteResponseSuccessWithNullMatchID() {
        InviteResponseSuccessResponse msg = new InviteResponseSuccessResponse(true, null);
        Object decoded = OCCodec.decode(OCCodec.encode(msg));
        assertTrue(decoded instanceof InviteResponseSuccessResponse);
        InviteResponseSuccessResponse d = (InviteResponseSuccessResponse) decoded;
        assertTrue(d.isSuccess());
        assertNull(d.getMatchID());
    }

    @Test
    void testInviteResponseSuccessWithMatchID() {
        InviteResponseSuccessResponse msg = new InviteResponseSuccessResponse(true, "12345");
        Object decoded = OCCodec.decode(OCCodec.encode(msg));
        assertTrue(decoded instanceof InviteResponseSuccessResponse);
        assertEquals("12345", ((InviteResponseSuccessResponse) decoded).getMatchID());
    }

    @Test
    void testEndMatchSuccessResponseWithNullArchiveID() {
        EndMatchSuccessResponse msg = new EndMatchSuccessResponse(true, null);
        Object decoded = OCCodec.decode(OCCodec.encode(msg));
        assertTrue(decoded instanceof EndMatchSuccessResponse);
        assertNull(((EndMatchSuccessResponse) decoded).getArchiveID());
    }

    @Test
    void testEndMatchSuccessResponseWithArchiveID() {
        EndMatchSuccessResponse msg = new EndMatchSuccessResponse(true, "arch-99");
        Object decoded = OCCodec.decode(OCCodec.encode(msg));
        assertTrue(decoded instanceof EndMatchSuccessResponse);
        assertEquals("arch-99", ((EndMatchSuccessResponse) decoded).getArchiveID());
    }

    // ===================== Empty lists =====================

    @Test
    void testBoardDataEmptyPieceList() {
        BoardDataSuccessResponse msg = new BoardDataSuccessResponse(true, new ArrayList<PieceEntry>());
        Object decoded = OCCodec.decode(OCCodec.encode(msg));
        assertTrue(decoded instanceof BoardDataSuccessResponse);
        BoardDataSuccessResponse d = (BoardDataSuccessResponse) decoded;
        assertTrue(d.isSuccess());
        assertTrue(d.getPieces().isEmpty());
    }

    @Test
    void testInviteListEmpty() {
        InviteListSuccessResponse msg = new InviteListSuccessResponse(true, 0, 0, 0,
                new ArrayList<InviteRecord>());
        Object decoded = OCCodec.decode(OCCodec.encode(msg));
        assertTrue(decoded instanceof InviteListSuccessResponse);
        InviteListSuccessResponse d = (InviteListSuccessResponse) decoded;
        assertTrue(d.getInvites().isEmpty());
        assertEquals(0, d.getAmount());
    }

    @Test
    void testNotificationsEmpty() {
        NotificationsSuccessResponse msg = new NotificationsSuccessResponse(true, 0,
                new ArrayList<NotificationRecord>());
        Object decoded = OCCodec.decode(OCCodec.encode(msg));
        assertTrue(decoded instanceof NotificationsSuccessResponse);
        assertEquals(0, ((NotificationsSuccessResponse) decoded).getCount());
        assertTrue(((NotificationsSuccessResponse) decoded).getNotifications().isEmpty());
    }

    @Test
    void testInProgressMatchesEmpty() {
        InProgressMatchesSuccessResponse msg = new InProgressMatchesSuccessResponse(true, 0,
                new ArrayList<MatchSummary>());
        Object decoded = OCCodec.decode(OCCodec.encode(msg));
        assertTrue(decoded instanceof InProgressMatchesSuccessResponse);
        assertEquals(0, ((InProgressMatchesSuccessResponse) decoded).getCount());
        assertTrue(((InProgressMatchesSuccessResponse) decoded).getMatches().isEmpty());
    }

    @Test
    void testGameRecordsEmpty() {
        GameRecordsSuccessResponse msg = new GameRecordsSuccessResponse(true, 0,
                new ArrayList<GameRecordEntry>());
        Object decoded = OCCodec.decode(OCCodec.encode(msg));
        assertTrue(decoded instanceof GameRecordsSuccessResponse);
        assertEquals(0, ((GameRecordsSuccessResponse) decoded).getNumber());
        assertTrue(((GameRecordsSuccessResponse) decoded).getRecords().isEmpty());
    }

    // ===================== Multi-element lists =====================

    @Test
    void testBoardDataMultiplePieces() {
        List<PieceEntry> pieces = new ArrayList<PieceEntry>();
        pieces.add(new PieceEntry("a1", "WhiteRook"));
        pieces.add(new PieceEntry("b1", "WhiteKnight"));
        pieces.add(new PieceEntry("c1", "WhiteBishop"));
        pieces.add(new PieceEntry("d1", "WhiteQueen"));
        pieces.add(new PieceEntry("e1", "WhiteKing"));

        BoardDataSuccessResponse msg = new BoardDataSuccessResponse(true, pieces);
        Object decoded = OCCodec.decode(OCCodec.encode(msg));
        assertTrue(decoded instanceof BoardDataSuccessResponse);
        BoardDataSuccessResponse d = (BoardDataSuccessResponse) decoded;
        assertEquals(5, d.getPieces().size());
        assertEquals("a1", d.getPieces().get(0).getPosition());
        assertEquals("WhiteRook", d.getPieces().get(0).getPiece());
        assertEquals("e1", d.getPieces().get(4).getPosition());
        assertEquals("WhiteKing", d.getPieces().get(4).getPiece());
    }

    @Test
    void testInviteListMultipleRecords() {
        List<InviteRecord> invites = new ArrayList<InviteRecord>();
        invites.add(new InviteRecord("alice", "bob", false, false));
        invites.add(new InviteRecord("carol", "dave", true, false));
        invites.add(new InviteRecord("eve", "frank", false, true));

        InviteListSuccessResponse msg = new InviteListSuccessResponse(true, 3, 3, 5, invites);
        Object decoded = OCCodec.decode(OCCodec.encode(msg));
        assertTrue(decoded instanceof InviteListSuccessResponse);
        InviteListSuccessResponse d = (InviteListSuccessResponse) decoded;
        assertEquals(3, d.getInvites().size());

        InviteRecord r0 = d.getInvites().get(0);
        assertEquals("alice", r0.getInviter());
        assertEquals("bob", r0.getInvitee());
        assertFalse(r0.isAccepted());
        assertFalse(r0.isDeclined());

        InviteRecord r1 = d.getInvites().get(1);
        assertTrue(r1.isAccepted());
        assertFalse(r1.isDeclined());

        InviteRecord r2 = d.getInvites().get(2);
        assertFalse(r2.isAccepted());
        assertTrue(r2.isDeclined());
    }

    @Test
    void testNotificationsMultipleRecords() {
        List<NotificationRecord> notifs = new ArrayList<NotificationRecord>();
        notifs.add(new NotificationRecord("NEW_MATCH", "Match started", "2024-01-15"));
        notifs.add(new NotificationRecord("MATCH_ENDED", "Match over", "2024-01-16"));
        notifs.add(new NotificationRecord("INVITE", "You got an invite", "2024-01-17"));

        NotificationsSuccessResponse msg = new NotificationsSuccessResponse(true, 3, notifs);
        Object decoded = OCCodec.decode(OCCodec.encode(msg));
        assertTrue(decoded instanceof NotificationsSuccessResponse);
        NotificationsSuccessResponse d = (NotificationsSuccessResponse) decoded;
        assertEquals(3, d.getCount());
        assertEquals(3, d.getNotifications().size());
        assertEquals("NEW_MATCH", d.getNotifications().get(0).getEvent());
        assertEquals("INVITE", d.getNotifications().get(2).getEvent());
    }

    @Test
    void testInProgressMatchesMultiple() {
        List<MatchSummary> matches = new ArrayList<MatchSummary>();
        matches.add(new MatchSummary("opponent1", 100, 0));
        matches.add(new MatchSummary("opponent2", 200, 1));

        InProgressMatchesSuccessResponse msg = new InProgressMatchesSuccessResponse(true, 2, matches);
        Object decoded = OCCodec.decode(OCCodec.encode(msg));
        assertTrue(decoded instanceof InProgressMatchesSuccessResponse);
        InProgressMatchesSuccessResponse d = (InProgressMatchesSuccessResponse) decoded;
        assertEquals(2, d.getCount());
        assertEquals("opponent1", d.getMatches().get(0).getOpponentNickname());
        assertEquals(100, d.getMatches().get(0).getMatchID());
        assertEquals(0, d.getMatches().get(0).getPlayerIndex());
        assertEquals(1, d.getMatches().get(1).getPlayerIndex());
    }

    @Test
    void testGameRecordsMultiple() {
        List<GameRecordEntry> records = new ArrayList<GameRecordEntry>();
        records.add(new GameRecordEntry("opp1", "win", 10));
        records.add(new GameRecordEntry("opp2", "loss", 45));
        records.add(new GameRecordEntry("opp3", "draw", 60));

        GameRecordsSuccessResponse msg = new GameRecordsSuccessResponse(true, 3, records);
        Object decoded = OCCodec.decode(OCCodec.encode(msg));
        assertTrue(decoded instanceof GameRecordsSuccessResponse);
        GameRecordsSuccessResponse d = (GameRecordsSuccessResponse) decoded;
        assertEquals(3, d.getNumber());
        assertEquals("opp1", d.getRecords().get(0).getOpponentNickname());
        assertEquals("win", d.getRecords().get(0).getResult());
        assertEquals(10, d.getRecords().get(0).getMoves());
        assertEquals(60, d.getRecords().get(2).getMoves());
    }

    // ===================== Unicode / special characters =====================

    @Test
    void testUnicodeNickname() {
        LoginRequest msg = new LoginRequest("caf\u00e9\u2603", "p\u00e4ss");
        Object decoded = OCCodec.decode(OCCodec.encode(msg));
        assertTrue(decoded instanceof LoginRequest);
        LoginRequest d = (LoginRequest) decoded;
        assertEquals("caf\u00e9\u2603", d.getNickname());
        assertEquals("p\u00e4ss", d.getPassword());
    }

    @Test
    void testSpecialCharsInFailureReason() {
        FailureResponse msg = new FailureResponse("Error: key=value, <xml>&amp;\"quotes\"");
        Object decoded = OCCodec.decode(OCCodec.encode(msg));
        assertTrue(decoded instanceof FailureResponse);
        assertEquals("Error: key=value, <xml>&amp;\"quotes\"", ((FailureResponse) decoded).getReason());
    }

    // ===================== Idempotence =====================

    @Test
    void testDoubleEncodeDecodePreservesData() {
        LoginRequest original = new LoginRequest("nick", "pass");

        String encoded1 = OCCodec.encode(original);
        Object decoded1 = OCCodec.decode(encoded1);
        assertTrue(decoded1 instanceof LoginRequest);

        String encoded2 = OCCodec.encode(decoded1);
        Object decoded2 = OCCodec.decode(encoded2);
        assertTrue(decoded2 instanceof LoginRequest);

        LoginRequest result = (LoginRequest) decoded2;
        assertEquals("nick", result.getNickname());
        assertEquals("pass", result.getPassword());

        // Same wire bytes
        assertEquals(encoded1, encoded2);
    }

    // ===================== Encode produces valid Base64 =====================

    @Test
    void testEncodedStringIsValidBase64() {
        SquareRequest msg = new SquareRequest(42);
        String encoded = OCCodec.encode(msg);
        assertNotNull(encoded);
        assertFalse(encoded.isEmpty());
        // Should not throw
        byte[] decoded = java.util.Base64.getDecoder().decode(encoded);
        assertTrue(decoded.length > 0);
    }

    @Test
    void testEncodedStringIsSingleLine() {
        // Important for TCP text-line transport
        RegisterRequest msg = new RegisterRequest("long.email.address@example.com",
                "aVeryLongNicknameThatCouldCauseLineWrapping", "s3cur3P@ssw0rd!#$%");
        String encoded = OCCodec.encode(msg);
        assertFalse(encoded.contains("\n"));
        assertFalse(encoded.contains("\r"));
    }

    // ===================== ForfeitSuccessResponse =====================

    @Test
    void testForfeitSuccessResponseTrue() {
        ForfeitSuccessResponse msg = new ForfeitSuccessResponse(true, true);
        Object decoded = OCCodec.decode(OCCodec.encode(msg));
        assertTrue(decoded instanceof ForfeitSuccessResponse);
        assertTrue(((ForfeitSuccessResponse) decoded).isForfeit());
    }

    @Test
    void testForfeitSuccessResponseFalse() {
        ForfeitSuccessResponse msg = new ForfeitSuccessResponse(true, false);
        Object decoded = OCCodec.decode(OCCodec.encode(msg));
        assertTrue(decoded instanceof ForfeitSuccessResponse);
        assertFalse(((ForfeitSuccessResponse) decoded).isForfeit());
    }

    // ===================== ProfileDataSuccessResponse =====================

    @Test
    void testProfileDataZeroStats() {
        ProfileDataSuccessResponse msg = new ProfileDataSuccessResponse(true, "newbie", 0, 0, 0);
        Object decoded = OCCodec.decode(OCCodec.encode(msg));
        assertTrue(decoded instanceof ProfileDataSuccessResponse);
        ProfileDataSuccessResponse d = (ProfileDataSuccessResponse) decoded;
        assertEquals("newbie", d.getNickname());
        assertEquals(0, d.getGamesWon());
        assertEquals(0, d.getGamesLost());
        assertEquals(0, d.getGamesTied());
    }

    @Test
    void testProfileDataLargeStats() {
        ProfileDataSuccessResponse msg = new ProfileDataSuccessResponse(true, "veteran", 5000, 3000, 1000);
        Object decoded = OCCodec.decode(OCCodec.encode(msg));
        assertTrue(decoded instanceof ProfileDataSuccessResponse);
        ProfileDataSuccessResponse d = (ProfileDataSuccessResponse) decoded;
        assertEquals(5000, d.getGamesWon());
        assertEquals(3000, d.getGamesLost());
        assertEquals(1000, d.getGamesTied());
    }

    // ===================== All request types produce distinct tags =====================

    @Test
    void testAllRequestTypesDecodeToCorrectType() {
        Object[] requests = new Object[] {
                new SquareRequest(1),
                new RegisterRequest("e@m", "n", "p"),
                new UnregisterRequest("n"),
                new LoginRequest("n", "p"),
                new GetProfileDataRequest("n"),
                new SendInviteRequest("a", "b"),
                new GetInvitesSentRequest("a"),
                new GetInvitesReceivedRequest("b"),
                new GetNotificationsRequest("n"),
                new InviteResponseRequest("accept", "a", "b"),
                new GetBoardDataRequest(1),
                new GetLegalMovesRequest(1, 2, 3),
                new MatchMoveRequest(1, 2, 3, 4, 5),
                new GetInProgressMatchesRequest("n"),
                new GetTurnRequest(1),
                new GetGameRecordsRequest("n"),
                new EndMatchRequest(1, "w", "l"),
                new CheckCheckmateRequest(1),
                new CheckForfeitRequest(1)
        };

        for (Object request : requests) {
            String encoded = OCCodec.encode(request);
            Object decoded = OCCodec.decode(encoded);
            assertEquals(request.getClass(), decoded.getClass(),
                    "Round-trip type mismatch for " + request.getClass().getSimpleName());
        }
    }

    @Test
    void testAllResponseTypesDecodeToCorrectType() {
        Object[] responses = new Object[] {
                new SimpleSuccessResponse(),
                new FailureResponse("reason"),
                new SquareSuccessResponse("answer"),
                new ProfileDataSuccessResponse(true, "n", 1, 2, 3),
                new InviteListSuccessResponse(true, 0, 0, 0, new ArrayList<InviteRecord>()),
                new NotificationsSuccessResponse(true, 0, new ArrayList<NotificationRecord>()),
                new InviteResponseSuccessResponse(true, "123"),
                new BoardDataSuccessResponse(true, new ArrayList<PieceEntry>()),
                new LegalMovesSuccessResponse(true, "/a3/", false),
                new InProgressMatchesSuccessResponse(true, 0, new ArrayList<MatchSummary>()),
                new TurnSuccessResponse(true, "p", "W"),
                new GameRecordsSuccessResponse(true, 0, new ArrayList<GameRecordEntry>()),
                new EndMatchSuccessResponse(true, "aid"),
                new CheckmateSuccessResponse(true, false, null, null),
                new ForfeitSuccessResponse(true, false)
        };

        for (Object response : responses) {
            String encoded = OCCodec.encode(response);
            Object decoded = OCCodec.decode(encoded);
            assertEquals(response.getClass(), decoded.getClass(),
                    "Round-trip type mismatch for " + response.getClass().getSimpleName());
        }
    }
}
