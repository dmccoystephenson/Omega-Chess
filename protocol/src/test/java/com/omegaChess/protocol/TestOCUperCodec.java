package com.omegaChess.protocol;

import com.omegaChess.protocol.messages.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link OCUperCodec} — tag assignment, type-name mapping,
 * and field-level encode/decode via {@code encodeFields}/{@code decodeFields}.
 */
@DisplayName("OCUperCodec Tests")
public class TestOCUperCodec {

    // ===================== tagOf() =====================

    @Test
    void testTagOfSquareRequest() {
        assertEquals(0, OCUperCodec.tagOf(new SquareRequest(1)));
    }

    @Test
    void testTagOfRegisterRequest() {
        assertEquals(1, OCUperCodec.tagOf(new RegisterRequest("e", "n", "p")));
    }

    @Test
    void testTagOfSimpleSuccessResponse() {
        assertEquals(19, OCUperCodec.tagOf(new SimpleSuccessResponse()));
    }

    @Test
    void testTagOfFailureResponse() {
        assertEquals(20, OCUperCodec.tagOf(new FailureResponse("r")));
    }

    @Test
    void testTagOfForfeitSuccessResponse() {
        assertEquals(33, OCUperCodec.tagOf(new ForfeitSuccessResponse(true, false)));
    }

    @Test
    void testTagOfAllRequestTypes() {
        // Verify all request tags are in range 0..18
        Object[] requests = {
                new SquareRequest(0),
                new RegisterRequest("e", "n", "p"),
                new UnregisterRequest("n"),
                new LoginRequest("n", "p"),
                new GetProfileDataRequest("n"),
                new SendInviteRequest("a", "b"),
                new GetInvitesSentRequest("a"),
                new GetInvitesReceivedRequest("b"),
                new GetNotificationsRequest("n"),
                new InviteResponseRequest("r", "a", "b"),
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
        for (int i = 0; i < requests.length; i++) {
            int tag = OCUperCodec.tagOf(requests[i]);
            assertEquals(i, tag, "Request tag mismatch at index " + i);
        }
    }

    @Test
    void testTagOfUnknownTypeThrows() {
        assertThrows(RuntimeException.class, new org.junit.jupiter.api.function.Executable() {
            public void execute() { OCUperCodec.tagOf("not a message"); }
        });
    }

    // ===================== typeNameOf() =====================

    @Test
    void testTypeNameOfTag0() {
        assertEquals("SquareRequest", OCUperCodec.typeNameOf(0));
    }

    @Test
    void testTypeNameOfTag3() {
        assertEquals("LoginRequest", OCUperCodec.typeNameOf(3));
    }

    @Test
    void testTypeNameOfTag19() {
        assertEquals("SimpleSuccessResponse", OCUperCodec.typeNameOf(19));
    }

    @Test
    void testTypeNameOfTag33() {
        assertEquals("ForfeitSuccessResponse", OCUperCodec.typeNameOf(33));
    }

    @Test
    void testTypeNameOfInvalidTagThrows() {
        assertThrows(RuntimeException.class, new org.junit.jupiter.api.function.Executable() {
            public void execute() { OCUperCodec.typeNameOf(34); }
        });
        assertThrows(RuntimeException.class, new org.junit.jupiter.api.function.Executable() {
            public void execute() { OCUperCodec.typeNameOf(-1); }
        });
    }

    // ===================== encodeFields / decodeFields round-trip =====================

    @Test
    void testEncodeDecodeFieldsSquareRequest() {
        SquareRequest original = new SquareRequest(42);
        byte[] fields = OCUperCodec.encodeFields(original);
        Object decoded = OCUperCodec.decodeFields(0, fields);
        assertTrue(decoded instanceof SquareRequest);
        assertEquals(42, ((SquareRequest) decoded).getNumber());
    }

    @Test
    void testEncodeDecodeFieldsLoginRequest() {
        LoginRequest original = new LoginRequest("user", "pwd");
        byte[] fields = OCUperCodec.encodeFields(original);
        Object decoded = OCUperCodec.decodeFields(3, fields);
        assertTrue(decoded instanceof LoginRequest);
        LoginRequest d = (LoginRequest) decoded;
        assertEquals("user", d.getNickname());
        assertEquals("pwd", d.getPassword());
    }

    @Test
    void testEncodeDecodeFieldsRegisterRequest() {
        RegisterRequest original = new RegisterRequest("em@il", "nick", "pass");
        byte[] fields = OCUperCodec.encodeFields(original);
        Object decoded = OCUperCodec.decodeFields(1, fields);
        assertTrue(decoded instanceof RegisterRequest);
        RegisterRequest d = (RegisterRequest) decoded;
        assertEquals("em@il", d.getEmail());
        assertEquals("nick", d.getNickname());
        assertEquals("pass", d.getPassword());
    }

    @Test
    void testEncodeDecodeFieldsSimpleSuccessResponse() {
        SimpleSuccessResponse original = new SimpleSuccessResponse();
        byte[] fields = OCUperCodec.encodeFields(original);
        Object decoded = OCUperCodec.decodeFields(19, fields);
        assertTrue(decoded instanceof SimpleSuccessResponse);
    }

    @Test
    void testEncodeDecodeFieldsFailureResponse() {
        FailureResponse original = new FailureResponse("oops");
        byte[] fields = OCUperCodec.encodeFields(original);
        Object decoded = OCUperCodec.decodeFields(20, fields);
        assertTrue(decoded instanceof FailureResponse);
        assertEquals("oops", ((FailureResponse) decoded).getReason());
    }

    @Test
    void testEncodeDecodeFieldsMatchMoveRequest() {
        MatchMoveRequest original = new MatchMoveRequest(10, 1, 2, 3, 4);
        byte[] fields = OCUperCodec.encodeFields(original);
        Object decoded = OCUperCodec.decodeFields(12, fields);
        assertTrue(decoded instanceof MatchMoveRequest);
        MatchMoveRequest d = (MatchMoveRequest) decoded;
        assertEquals(10, d.getMatchID());
        assertEquals(1, d.getFromRow());
        assertEquals(2, d.getFromColumn());
        assertEquals(3, d.getToRow());
        assertEquals(4, d.getToColumn());
    }

    @Test
    void testEncodeDecodeFieldsBoardDataWithPieces() {
        List<PieceEntry> pieces = new ArrayList<PieceEntry>();
        pieces.add(new PieceEntry("a1", "Rook"));
        pieces.add(new PieceEntry("h8", "King"));
        BoardDataSuccessResponse original = new BoardDataSuccessResponse(true, pieces);
        byte[] fields = OCUperCodec.encodeFields(original);
        Object decoded = OCUperCodec.decodeFields(26, fields);
        assertTrue(decoded instanceof BoardDataSuccessResponse);
        BoardDataSuccessResponse d = (BoardDataSuccessResponse) decoded;
        assertEquals(2, d.getPieces().size());
        assertEquals("a1", d.getPieces().get(0).getPosition());
        assertEquals("King", d.getPieces().get(1).getPiece());
    }

    @Test
    void testEncodeDecodeFieldsCheckmateWithOptionals() {
        CheckmateSuccessResponse original = new CheckmateSuccessResponse(true, true, "loser", "winner");
        byte[] fields = OCUperCodec.encodeFields(original);
        Object decoded = OCUperCodec.decodeFields(32, fields);
        assertTrue(decoded instanceof CheckmateSuccessResponse);
        CheckmateSuccessResponse d = (CheckmateSuccessResponse) decoded;
        assertTrue(d.isCheckmate());
        assertEquals("loser", d.getLoser());
        assertEquals("winner", d.getWinner());
    }

    @Test
    void testEncodeDecodeFieldsCheckmateWithNulls() {
        CheckmateSuccessResponse original = new CheckmateSuccessResponse(true, false, null, null);
        byte[] fields = OCUperCodec.encodeFields(original);
        Object decoded = OCUperCodec.decodeFields(32, fields);
        assertTrue(decoded instanceof CheckmateSuccessResponse);
        CheckmateSuccessResponse d = (CheckmateSuccessResponse) decoded;
        assertFalse(d.isCheckmate());
        assertNull(d.getLoser());
        assertNull(d.getWinner());
    }

    @Test
    void testEncodeDecodeFieldsInviteListMultiple() {
        List<InviteRecord> invites = new ArrayList<InviteRecord>();
        invites.add(new InviteRecord("a", "b", true, false));
        invites.add(new InviteRecord("c", "d", false, true));
        InviteListSuccessResponse original = new InviteListSuccessResponse(true, 2, 2, 1, invites);
        byte[] fields = OCUperCodec.encodeFields(original);
        Object decoded = OCUperCodec.decodeFields(23, fields);
        assertTrue(decoded instanceof InviteListSuccessResponse);
        InviteListSuccessResponse d = (InviteListSuccessResponse) decoded;
        assertEquals(2, d.getInvites().size());
        assertTrue(d.getInvites().get(0).isAccepted());
        assertTrue(d.getInvites().get(1).isDeclined());
    }

    @Test
    void testEncodeDecodeFieldsGameRecords() {
        List<GameRecordEntry> records = new ArrayList<GameRecordEntry>();
        records.add(new GameRecordEntry("opp", "win", 25));
        GameRecordsSuccessResponse original = new GameRecordsSuccessResponse(true, 1, records);
        byte[] fields = OCUperCodec.encodeFields(original);
        Object decoded = OCUperCodec.decodeFields(30, fields);
        assertTrue(decoded instanceof GameRecordsSuccessResponse);
        GameRecordsSuccessResponse d = (GameRecordsSuccessResponse) decoded;
        assertEquals(1, d.getNumber());
        assertEquals("opp", d.getRecords().get(0).getOpponentNickname());
        assertEquals(25, d.getRecords().get(0).getMoves());
    }

    // ===================== encodeFields produces deterministic output =====================

    @Test
    void testEncodeFieldsDeterministic() {
        LoginRequest msg = new LoginRequest("nick", "pass");
        byte[] first = OCUperCodec.encodeFields(msg);
        byte[] second = OCUperCodec.encodeFields(msg);
        assertArrayEquals(first, second);
    }

    // ===================== All tags are unique =====================

    @Test
    void testAllTagsAreUnique() {
        Object[] allMessages = {
                new SquareRequest(0),
                new RegisterRequest("e", "n", "p"),
                new UnregisterRequest("n"),
                new LoginRequest("n", "p"),
                new GetProfileDataRequest("n"),
                new SendInviteRequest("a", "b"),
                new GetInvitesSentRequest("a"),
                new GetInvitesReceivedRequest("b"),
                new GetNotificationsRequest("n"),
                new InviteResponseRequest("r", "a", "b"),
                new GetBoardDataRequest(1),
                new GetLegalMovesRequest(1, 2, 3),
                new MatchMoveRequest(1, 2, 3, 4, 5),
                new GetInProgressMatchesRequest("n"),
                new GetTurnRequest(1),
                new GetGameRecordsRequest("n"),
                new EndMatchRequest(1, "w", "l"),
                new CheckCheckmateRequest(1),
                new CheckForfeitRequest(1),
                new SimpleSuccessResponse(),
                new FailureResponse("r"),
                new SquareSuccessResponse("a"),
                new ProfileDataSuccessResponse(true, "n", 1, 2, 3),
                new InviteListSuccessResponse(true, 0, 0, 0, new ArrayList<InviteRecord>()),
                new NotificationsSuccessResponse(true, 0, new ArrayList<NotificationRecord>()),
                new InviteResponseSuccessResponse(true, "1"),
                new BoardDataSuccessResponse(true, new ArrayList<PieceEntry>()),
                new LegalMovesSuccessResponse(true, "/a/", false),
                new InProgressMatchesSuccessResponse(true, 0, new ArrayList<MatchSummary>()),
                new TurnSuccessResponse(true, "p", "W"),
                new GameRecordsSuccessResponse(true, 0, new ArrayList<GameRecordEntry>()),
                new EndMatchSuccessResponse(true, "a"),
                new CheckmateSuccessResponse(true, false, null, null),
                new ForfeitSuccessResponse(true, false)
        };

        java.util.Set<Integer> tags = new java.util.HashSet<Integer>();
        for (Object msg : allMessages) {
            int tag = OCUperCodec.tagOf(msg);
            assertTrue(tags.add(tag), "Duplicate tag: " + tag + " for " + msg.getClass().getSimpleName());
        }
        assertEquals(34, tags.size());
    }
}
