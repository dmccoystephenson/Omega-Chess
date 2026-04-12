package com.omegaChess.protocol;

import com.omegaChess.protocol.messages.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link OCMessageFactory} — the XER XML serialization/
 * deserialization codec used as the intermediate format for the native
 * asn1c codec path and for debugging/logging.
 */
@DisplayName("OCMessageFactory XER XML Tests")
public class TestOCMessageFactory {

    // ===================== Request round-trips =====================

    @Test
    void testSquareRequestXerRoundTrip() {
        SquareRequest msg = new SquareRequest(42);
        String xml = OCMessageFactory.toXml(msg);
        assertNotNull(xml);
        assertTrue(xml.contains("42"));

        Object parsed = OCMessageFactory.fromXml(xml);
        assertTrue(parsed instanceof SquareRequest);
        assertEquals(42, ((SquareRequest) parsed).getNumber());
    }

    @Test
    void testLoginRequestXerRoundTrip() {
        LoginRequest msg = new LoginRequest("testUser", "testPass");
        String xml = OCMessageFactory.toXml(msg);
        assertTrue(xml.contains("testUser"));
        assertTrue(xml.contains("testPass"));

        Object parsed = OCMessageFactory.fromXml(xml);
        assertTrue(parsed instanceof LoginRequest);
        LoginRequest d = (LoginRequest) parsed;
        assertEquals("testUser", d.getNickname());
        assertEquals("testPass", d.getPassword());
    }

    @Test
    void testRegisterRequestXerRoundTrip() {
        RegisterRequest msg = new RegisterRequest("e@mail.com", "nick", "pw");
        String xml = OCMessageFactory.toXml(msg);

        Object parsed = OCMessageFactory.fromXml(xml);
        assertTrue(parsed instanceof RegisterRequest);
        RegisterRequest d = (RegisterRequest) parsed;
        assertEquals("e@mail.com", d.getEmail());
        assertEquals("nick", d.getNickname());
        assertEquals("pw", d.getPassword());
    }

    @Test
    void testUnregisterRequestXerRoundTrip() {
        UnregisterRequest msg = new UnregisterRequest("removeMe");
        String xml = OCMessageFactory.toXml(msg);

        Object parsed = OCMessageFactory.fromXml(xml);
        assertTrue(parsed instanceof UnregisterRequest);
        assertEquals("removeMe", ((UnregisterRequest) parsed).getNickname());
    }

    @Test
    void testSendInviteRequestXerRoundTrip() {
        SendInviteRequest msg = new SendInviteRequest("from", "to");
        String xml = OCMessageFactory.toXml(msg);

        Object parsed = OCMessageFactory.fromXml(xml);
        assertTrue(parsed instanceof SendInviteRequest);
        SendInviteRequest d = (SendInviteRequest) parsed;
        assertEquals("from", d.getInviter());
        assertEquals("to", d.getInvitee());
    }

    @Test
    void testMatchMoveRequestXerRoundTrip() {
        MatchMoveRequest msg = new MatchMoveRequest(10, 1, 2, 3, 4);
        String xml = OCMessageFactory.toXml(msg);

        Object parsed = OCMessageFactory.fromXml(xml);
        assertTrue(parsed instanceof MatchMoveRequest);
        MatchMoveRequest d = (MatchMoveRequest) parsed;
        assertEquals(10, d.getMatchID());
        assertEquals(1, d.getFromRow());
        assertEquals(2, d.getFromColumn());
        assertEquals(3, d.getToRow());
        assertEquals(4, d.getToColumn());
    }

    @Test
    void testEndMatchRequestXerRoundTrip() {
        EndMatchRequest msg = new EndMatchRequest(5, "w", "l");
        String xml = OCMessageFactory.toXml(msg);

        Object parsed = OCMessageFactory.fromXml(xml);
        assertTrue(parsed instanceof EndMatchRequest);
        EndMatchRequest d = (EndMatchRequest) parsed;
        assertEquals(5, d.getId());
        assertEquals("w", d.getWinner());
        assertEquals("l", d.getLoser());
    }

    // ===================== Response round-trips =====================

    @Test
    void testSimpleSuccessResponseXerRoundTrip() {
        SimpleSuccessResponse msg = new SimpleSuccessResponse();
        String xml = OCMessageFactory.toXml(msg);

        Object parsed = OCMessageFactory.fromXml(xml);
        assertTrue(parsed instanceof SimpleSuccessResponse);
        assertTrue(((SimpleSuccessResponse) parsed).isSuccess());
    }

    @Test
    void testFailureResponseXerRoundTrip() {
        FailureResponse msg = new FailureResponse("something failed");
        String xml = OCMessageFactory.toXml(msg);

        Object parsed = OCMessageFactory.fromXml(xml);
        assertTrue(parsed instanceof FailureResponse);
        FailureResponse d = (FailureResponse) parsed;
        assertFalse(d.isSuccess());
        assertEquals("something failed", d.getReason());
    }

    @Test
    void testProfileDataXerRoundTrip() {
        ProfileDataSuccessResponse msg = new ProfileDataSuccessResponse(true, "p1", 10, 5, 2);
        String xml = OCMessageFactory.toXml(msg);

        Object parsed = OCMessageFactory.fromXml(xml);
        assertTrue(parsed instanceof ProfileDataSuccessResponse);
        ProfileDataSuccessResponse d = (ProfileDataSuccessResponse) parsed;
        assertEquals("p1", d.getNickname());
        assertEquals(10, d.getGamesWon());
        assertEquals(5, d.getGamesLost());
        assertEquals(2, d.getGamesTied());
    }

    @Test
    void testBoardDataXerRoundTrip() {
        List<PieceEntry> pieces = new ArrayList<PieceEntry>();
        pieces.add(new PieceEntry("a1", "Rook"));
        pieces.add(new PieceEntry("b1", "Knight"));
        BoardDataSuccessResponse msg = new BoardDataSuccessResponse(true, pieces);
        String xml = OCMessageFactory.toXml(msg);

        Object parsed = OCMessageFactory.fromXml(xml);
        assertTrue(parsed instanceof BoardDataSuccessResponse);
        BoardDataSuccessResponse d = (BoardDataSuccessResponse) parsed;
        assertEquals(2, d.getPieces().size());
        assertEquals("a1", d.getPieces().get(0).getPosition());
        assertEquals("Rook", d.getPieces().get(0).getPiece());
    }

    @Test
    void testInviteListXerRoundTrip() {
        List<InviteRecord> invites = new ArrayList<InviteRecord>();
        invites.add(new InviteRecord("alice", "bob", true, false));
        InviteListSuccessResponse msg = new InviteListSuccessResponse(true, 1, 1, 5, invites);
        String xml = OCMessageFactory.toXml(msg);

        Object parsed = OCMessageFactory.fromXml(xml);
        assertTrue(parsed instanceof InviteListSuccessResponse);
        InviteListSuccessResponse d = (InviteListSuccessResponse) parsed;
        assertEquals(1, d.getInvites().size());
        assertEquals("alice", d.getInvites().get(0).getInviter());
        assertTrue(d.getInvites().get(0).isAccepted());
    }

    @Test
    void testNotificationsXerRoundTrip() {
        List<NotificationRecord> notifs = new ArrayList<NotificationRecord>();
        notifs.add(new NotificationRecord("EVENT", "msg", "2024-01-01"));
        NotificationsSuccessResponse msg = new NotificationsSuccessResponse(true, 1, notifs);
        String xml = OCMessageFactory.toXml(msg);

        Object parsed = OCMessageFactory.fromXml(xml);
        assertTrue(parsed instanceof NotificationsSuccessResponse);
        NotificationsSuccessResponse d = (NotificationsSuccessResponse) parsed;
        assertEquals(1, d.getCount());
        assertEquals("EVENT", d.getNotifications().get(0).getEvent());
    }

    @Test
    void testCheckmateWithOptionalsXerRoundTrip() {
        CheckmateSuccessResponse msg = new CheckmateSuccessResponse(true, true, "loser", "winner");
        String xml = OCMessageFactory.toXml(msg);

        Object parsed = OCMessageFactory.fromXml(xml);
        assertTrue(parsed instanceof CheckmateSuccessResponse);
        CheckmateSuccessResponse d = (CheckmateSuccessResponse) parsed;
        assertTrue(d.isCheckmate());
        assertEquals("loser", d.getLoser());
        assertEquals("winner", d.getWinner());
    }

    @Test
    void testCheckmateWithNullsXerRoundTrip() {
        CheckmateSuccessResponse msg = new CheckmateSuccessResponse(true, false, null, null);
        String xml = OCMessageFactory.toXml(msg);

        Object parsed = OCMessageFactory.fromXml(xml);
        assertTrue(parsed instanceof CheckmateSuccessResponse);
        CheckmateSuccessResponse d = (CheckmateSuccessResponse) parsed;
        assertFalse(d.isCheckmate());
        assertNull(d.getLoser());
        assertNull(d.getWinner());
    }

    @Test
    void testGameRecordsXerRoundTrip() {
        List<GameRecordEntry> records = new ArrayList<GameRecordEntry>();
        records.add(new GameRecordEntry("opp1", "win", 30));
        records.add(new GameRecordEntry("opp2", "loss", 45));
        GameRecordsSuccessResponse msg = new GameRecordsSuccessResponse(true, 2, records);
        String xml = OCMessageFactory.toXml(msg);

        Object parsed = OCMessageFactory.fromXml(xml);
        assertTrue(parsed instanceof GameRecordsSuccessResponse);
        GameRecordsSuccessResponse d = (GameRecordsSuccessResponse) parsed;
        assertEquals(2, d.getNumber());
        assertEquals(2, d.getRecords().size());
        assertEquals("win", d.getRecords().get(0).getResult());
    }

    @Test
    void testInProgressMatchesXerRoundTrip() {
        List<MatchSummary> matches = new ArrayList<MatchSummary>();
        matches.add(new MatchSummary("opp", 42, 1));
        InProgressMatchesSuccessResponse msg = new InProgressMatchesSuccessResponse(true, 1, matches);
        String xml = OCMessageFactory.toXml(msg);

        Object parsed = OCMessageFactory.fromXml(xml);
        assertTrue(parsed instanceof InProgressMatchesSuccessResponse);
        InProgressMatchesSuccessResponse d = (InProgressMatchesSuccessResponse) parsed;
        assertEquals(1, d.getCount());
        assertEquals("opp", d.getMatches().get(0).getOpponentNickname());
        assertEquals(42, d.getMatches().get(0).getMatchID());
    }

    // ===================== XML structure checks =====================

    @Test
    void testXmlIsSingleLine() {
        LoginRequest msg = new LoginRequest("nick", "pass");
        String xml = OCMessageFactory.toXml(msg);
        assertFalse(xml.contains("\n"), "XER XML should be single-line for TCP transport");
    }

    @Test
    void testXmlContainsRootElement() {
        SquareRequest msg = new SquareRequest(10);
        String xml = OCMessageFactory.toXml(msg);
        assertTrue(xml.contains("<SquareRequest>"));
        assertTrue(xml.contains("</SquareRequest>"));
    }
}
