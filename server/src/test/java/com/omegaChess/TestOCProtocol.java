package com.omegaChess;

import com.omegaChess.protocol.OCCodec;
import com.omegaChess.protocol.messages.*;
import com.omegaChess.server.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JUnit OCProtocol Class Test")
public class TestOCProtocol {

    @Test
    public void testSquareInput() {
        OCServerData data = new OCServerData();
        OCProtocol protocol = new OCProtocol(data);

        String input = OCCodec.encode(new SquareRequest(10));
        String output = protocol.processInput(input);

        Object response = OCCodec.decode(output);
        assertTrue(response instanceof SquareSuccessResponse);
        SquareSuccessResponse squareResponse = (SquareSuccessResponse) response;
        assertEquals("Square of 10 is 100", squareResponse.getAnswer());
    }

    @Test
    public void testRegisterUser() {
        OCServerData data = new OCServerData();
        OCProtocol protocol = new OCProtocol(data);

        String input = OCCodec.encode(new RegisterRequest("test@gmail.com", "testGuy", "pass"));
        String output = protocol.processInput(input);

        Object response = OCCodec.decode(output);
        assertTrue(response instanceof SimpleSuccessResponse);

        // assert existence
        assertTrue(data.profileExists("testGuy"));
    }

    @Test
    public void testUnregisterUser() {
        OCServerData data = new OCServerData();
        OCProtocol protocol = new OCProtocol(data);

        data.createProfile("Daniel", "pass", "daniel@gmail.com");
        data.createProfile("John", "word", "john@omegachess.com");

        // Send invites between the users
        protocol.processInput(OCCodec.encode(new SendInviteRequest("Daniel", "John")));
        protocol.processInput(OCCodec.encode(new SendInviteRequest("John", "Daniel")));

        // Create a test match between the users
        data.addMatch(new Match("Daniel", "john"));

        // Create a test archive between the users
        data.addToArchive(new GameRecord("Daniel", "John", 45, false));

        // unregister profile
        String output = protocol.processInput(OCCodec.encode(new UnregisterRequest("Daniel")));

        Object response = OCCodec.decode(output);
        assertTrue(response instanceof SimpleSuccessResponse);

        // assert non-existence in all states
        assertFalse(data.profileExists("Daniel"));
        assertEquals(0, data.getProfile("John").getMailbox().getReceived().size(), "Failed to remove invite from Daniel.");
        assertEquals(0, data.getProfile("John").getMailbox().getSent().size(), "Failed to remove invite to Daniel.");
        assertEquals(0, data.getMatches().size(), "Failed to end match between users.");
        assertEquals("[deleted]", data.getArchive().get(0).getWinner(), "Failed to remove Daniel as winner from archive.");
    }

    @Test
    public void testLoginUser() {
        OCServerData data = new OCServerData();
        OCProtocol protocol = new OCProtocol(data);

        data.createProfile("Daniel", "pass", "daniel@gmail.com");

        // login
        String input = OCCodec.encode(new LoginRequest("Daniel", "pass"));
        String output = protocol.processInput(input);

        Object response = OCCodec.decode(output);
        assertTrue(response instanceof SimpleSuccessResponse);
    }

    @Test
    void testInvite() {
        OCServerData data = new OCServerData();
        OCProtocol protocol = new OCProtocol(data);

        data.createProfile("sweetfire", "T0asty!!", "SweetFireSauce@omegacess.net");
        data.createProfile("PawPatrol", "Pupp!e5!", "PawPatrolPuppySquad@omegachess.net");

        // Invite
        String output = protocol.processInput(OCCodec.encode(new SendInviteRequest("pawpatrol", "sweetfire")));

        Object response = OCCodec.decode(output);
        assertTrue(response instanceof SimpleSuccessResponse, "Invite was not sent!");
        assertFalse(data.getProfile("sweetfire").getMailbox().getReceived().isEmpty(),
                "Failed to add invite to mailbox received!");
        assertFalse(data.getProfile("pawpatrol").getMailbox().getSent().isEmpty(),
                "Failed to add invite to mailbox sent!");

        // test to not send an invite to yourself
        output = protocol.processInput(OCCodec.encode(new SendInviteRequest("sweetfire", "sweetfire")));
        response = OCCodec.decode(output);
        assertTrue(response instanceof FailureResponse, "Invite was sent to yourself");

        // test to not send an invite to a user that you already invited/ have been invited
        output = protocol.processInput(OCCodec.encode(new SendInviteRequest("sweetfire", "pawpatrol")));
        response = OCCodec.decode(output);
        assertTrue(response instanceof FailureResponse, "Sent an invite to someone who is already in mailbox");

        // test to not send an invite to someone you are in a match in
        data.addMatch(new Match("sweetfire", "pawpatrol"));
        output = protocol.processInput(OCCodec.encode(new SendInviteRequest("sweetfire", "pawpatrol")));
        response = OCCodec.decode(output);
        assertTrue(response instanceof FailureResponse, "Sent an invite to someone you are in a match with.");
    }

    @Test
    void testGetInvites() {
        OCServerData data = new OCServerData();
        OCProtocol protocol = new OCProtocol(data);

        data.createProfile("eatmyshorts","B@rts1mps0n","bartsimpsonininc@omegachess.net");
        data.createProfile("italian","!talianHandshak3","needsmorecheese@omegachess.net");

        // Send invite
        protocol.processInput(OCCodec.encode(new SendInviteRequest("italian", "eatmyshorts")));

        // get sent invites
        String output = protocol.processInput(OCCodec.encode(new GetInvitesSentRequest("italian")));

        Object response = OCCodec.decode(output);
        assertTrue(response instanceof InviteListSuccessResponse);
        InviteListSuccessResponse inviteListResp = (InviteListSuccessResponse) response;
        assertFalse(inviteListResp.getInvites().isEmpty());
        InviteRecord invite = inviteListResp.getInvites().get(0);
        assertEquals("italian", invite.getInviter(), "Failed to get sent invite");

        // get received invites
        output = protocol.processInput(OCCodec.encode(new GetInvitesReceivedRequest("eatmyshorts")));

        response = OCCodec.decode(output);
        assertTrue(response instanceof InviteListSuccessResponse);
        inviteListResp = (InviteListSuccessResponse) response;
        assertFalse(inviteListResp.getInvites().isEmpty());
        invite = inviteListResp.getInvites().get(0);
        assertEquals("eatmyshorts", invite.getInvitee(), "Failed to get sent invite");
    }

    @Test
    public void testGetProfileData() {
        OCServerData data = new OCServerData();
        OCProtocol protocol = new OCProtocol(data);

        data.createProfile("Daniel", "pass", "daniel@gmail.com");
        data.getProfile("Daniel").setGamesWon(1);
        data.getProfile("Daniel").setGamesLost(2);
        data.getProfile("Daniel").setGamesTied(3);

        String input = OCCodec.encode(new GetProfileDataRequest("Daniel"));
        String output = protocol.processInput(input);

        Object response = OCCodec.decode(output);
        assertTrue(response instanceof ProfileDataSuccessResponse);
        ProfileDataSuccessResponse profileResp = (ProfileDataSuccessResponse) response;
        assertEquals(1, profileResp.getGamesWon());
        assertEquals(2, profileResp.getGamesLost());
        assertEquals(3, profileResp.getGamesTied());
    }

    @Test
    public void testGetNotifications() {
        OCServerData data = new OCServerData();
        OCProtocol protocol = new OCProtocol(data);

        data.createProfile("Daniel", "pass", "daniel@gmail.com");

        data.getProfile("Daniel").getMailbox().addNotification(Notification.NotificationType.NEW_MATCH, "Message 1");
        data.getProfile("Daniel").getMailbox().addNotification(Notification.NotificationType.MATCH_ENDED, "Message 2");

        // get notifications
        String input = OCCodec.encode(new GetNotificationsRequest("Daniel"));
        String output = protocol.processInput(input);

        Object response = OCCodec.decode(output);
        assertTrue(response instanceof NotificationsSuccessResponse);
        NotificationsSuccessResponse notifResp = (NotificationsSuccessResponse) response;

        assertEquals(2, notifResp.getCount());
        List<NotificationRecord> notifications = notifResp.getNotifications();
        assertEquals("NEW_MATCH", notifications.get(0).getEvent());
        assertEquals("Message 1", notifications.get(0).getMessage());
        assertNotNull(notifications.get(0).getDateString());
        assertEquals("MATCH_ENDED", notifications.get(1).getEvent());
        assertEquals("Message 2", notifications.get(1).getMessage());
        assertNotNull(notifications.get(1).getDateString());
    }

    @Test
    public void testInviteResponse(){
        OCServerData data = new OCServerData();
        OCProtocol protocol = new OCProtocol(data);

        data.createProfile("jae", "wing", "adskfjlhasd@omegachess.com");
        data.createProfile("Shing", "shaw", "asdfasdwes@omegachess.com");

        // Send an invite between the users
        protocol.processInput(OCCodec.encode(new SendInviteRequest("shing", "jae")));

        System.out.println("Testing accepting an invite between two users");

        // Testing accept
        String out = protocol.processInput(OCCodec.encode(new InviteResponseRequest("accept", "shing", "jae")));

        Object response = OCCodec.decode(out);
        assertTrue(response instanceof InviteResponseSuccessResponse, "Something went wrong");
        InviteResponseSuccessResponse inviteRespResp = (InviteResponseSuccessResponse) response;
        assertTrue(inviteRespResp.isSuccess(), "Something went wrong");
        assertEquals(0, data.getProfile("jae").getMailbox().getReceived().size(), "Failed to remove invite from mailbox.");
        assertEquals(0, data.getProfile("shing").getMailbox().getSent().size(), "Failed to remove invite from mailbox.");
        assertEquals(1, data.getMatches().size(), "Failed to add a new match to the server.");

        // Send an invite between the users
        protocol.processInput(OCCodec.encode(new SendInviteRequest("shing", "jae")));

        System.out.println("Testing declining an invite between two users");

        // Testing decline
        out = protocol.processInput(OCCodec.encode(new InviteResponseRequest("decline", "shing", "jae")));

        response = OCCodec.decode(out);
        assertTrue(response instanceof InviteResponseSuccessResponse, "Something went wrong");
        inviteRespResp = (InviteResponseSuccessResponse) response;
        assertTrue(inviteRespResp.isSuccess(), "Something went wrong");
        assertEquals(0, data.getProfile("jae").getMailbox().getReceived().size(), "Failed to remove invite from mailbox.");
        assertEquals(0, data.getProfile("shing").getMailbox().getSent().size(), "Failed to remove invite from mailbox.");
    }

    @Test
    public void testGetBoard(){
        OCServerData data = new OCServerData();
        OCProtocol protocol = new OCProtocol(data);

        data.createProfile("this", "one", "thisOne@omegachess.com");
        data.createProfile("that", "one", "thatOne@omegachess.com");

        // Test no match available
        String out = protocol.processInput(OCCodec.encode(new GetBoardDataRequest(345)));
        Object response = OCCodec.decode(out);
        assertTrue(response instanceof FailureResponse, "There should not be any matches in the data right now.");

        Match match = new Match("this", "that");
        data.addMatch(match);

        // Test match ID is invalid
        out = protocol.processInput(OCCodec.encode(new GetBoardDataRequest(123)));
        response = OCCodec.decode(out);
        assertTrue(response instanceof FailureResponse, "The match ID 123 shouldn't exist");

        // Test that the protocol returns board data
        out = protocol.processInput(OCCodec.encode(new GetBoardDataRequest(match.getMatchID())));
        response = OCCodec.decode(out);
        assertTrue(response instanceof BoardDataSuccessResponse, "Failed to get board data");
        BoardDataSuccessResponse boardResp = (BoardDataSuccessResponse) response;
        assertTrue(boardResp.isSuccess(), "Failed to get board data");
        boolean foundW1 = false;
        for (PieceEntry entry : boardResp.getPieces()) {
            if (entry.getPosition().equals("w1")) {
                foundW1 = true;
                break;
            }
        }
        assertTrue(foundW1, "failed to return info for w1");
    }

    @Test
    public void testGetLegalMoves() {
        OCServerData data = new OCServerData();
        OCProtocol protocol = new OCProtocol(data);

        // create profiles
        data.createProfile("pete", "zoop", "asdf@mail.com");
        data.createProfile("kyle", "zoop", "fdsa@mail.com");

        // send invite
        protocol.processInput(OCCodec.encode(new SendInviteRequest("pete", "kyle")));

        // accept invite and get matchID
        String acceptString = protocol.processInput(OCCodec.encode(new InviteResponseRequest("accept", "pete", "kyle")));
        Object acceptObj = OCCodec.decode(acceptString);
        assertTrue(acceptObj instanceof InviteResponseSuccessResponse);
        int matchID = Integer.parseInt(((InviteResponseSuccessResponse) acceptObj).getMatchID());

        // test white pawn in starting position
        String out = protocol.processInput(OCCodec.encode(new GetLegalMovesRequest(matchID, 2, 1)));
        Object response = OCCodec.decode(out);
        assertTrue(response instanceof LegalMovesSuccessResponse);
        LegalMovesSuccessResponse legalResp = (LegalMovesSuccessResponse) response;
        assertTrue(legalResp.isSuccess());
        assertEquals("/a3/a4/a5/", legalResp.getLegalMoves());

        // test black pawn in starting position
        out = protocol.processInput(OCCodec.encode(new GetLegalMovesRequest(matchID, 9, 10)));
        response = OCCodec.decode(out);
        assertTrue(response instanceof LegalMovesSuccessResponse);
        legalResp = (LegalMovesSuccessResponse) response;
        assertTrue(legalResp.isSuccess());
        assertEquals("/j8/j7/j6/", legalResp.getLegalMoves());

        // test knight in starting position
        out = protocol.processInput(OCCodec.encode(new GetLegalMovesRequest(matchID, 1, 8)));
        response = OCCodec.decode(out);
        assertTrue(response instanceof LegalMovesSuccessResponse);
        legalResp = (LegalMovesSuccessResponse) response;
        assertTrue(legalResp.isSuccess());
        assertEquals("/g3/i3/", legalResp.getLegalMoves());

        // test blank square
        out = protocol.processInput(OCCodec.encode(new GetLegalMovesRequest(matchID, 5, 5)));
        response = OCCodec.decode(out);
        assertTrue(response instanceof FailureResponse);
    }

    @Test
    public void testGetLongestNickname()
    {
        OCServerData data = new OCServerData();
        OCProtocol protocol = new OCProtocol(data);

        data.createProfile("jae", "wing", "adskfjlhasd@omegachess.com");
        data.createProfile("Shing", "shaw", "asdfasdwes@omegachess.com");

        assertEquals(data.getLongestNickname(), 5);
    }

    public void testMatchMove() {
        OCServerData data = new OCServerData();
        OCProtocol protocol = new OCProtocol(data);

        // create profiles
        data.createProfile("pete", "zoop", "asdf@mail.com");
        data.createProfile("kyle", "zoop", "fdsa@mail.com");

        // send invite
        protocol.processInput(OCCodec.encode(new SendInviteRequest("pete", "kyle")));

        // accept invite and get matchID
        String acceptString = protocol.processInput(OCCodec.encode(new InviteResponseRequest("accept", "pete", "kyle")));
        Object acceptObj = OCCodec.decode(acceptString);
        assertTrue(acceptObj instanceof InviteResponseSuccessResponse);
        int matchID = Integer.parseInt(((InviteResponseSuccessResponse) acceptObj).getMatchID());

        // test moving white wizard
        String output = protocol.processInput(OCCodec.encode(new MatchMoveRequest(matchID, 0, 0, 3, 1)));
        Object response = OCCodec.decode(output);
        assertTrue(response instanceof SimpleSuccessResponse);

        // test moving black pawn
        output = protocol.processInput(OCCodec.encode(new MatchMoveRequest(matchID, 9, 5, 6, 5)));
        response = OCCodec.decode(output);
        assertTrue(response instanceof SimpleSuccessResponse);

        // test moving white knight
        output = protocol.processInput(OCCodec.encode(new MatchMoveRequest(matchID, 1, 8, 3, 9)));
        response = OCCodec.decode(output);
        assertTrue(response instanceof SimpleSuccessResponse);
    }

    @Test
    public void testInProgressMatchRequest() {
        OCServerData data = new OCServerData();
        OCProtocol protocol = new OCProtocol(data);

        // create profiles
        data.createProfile("pete", "zoop", "asdf@mail.com");
        data.createProfile("kyle", "zoop", "fdsa@mail.com");

        // test no matches available
        String out = protocol.processInput(OCCodec.encode(new GetInProgressMatchesRequest("pete")));
        Object response = OCCodec.decode(out);
        assertTrue(response instanceof InProgressMatchesSuccessResponse);
        InProgressMatchesSuccessResponse matchesResp = (InProgressMatchesSuccessResponse) response;
        assertEquals(0, matchesResp.getCount());

        // send invite
        protocol.processInput(OCCodec.encode(new SendInviteRequest("pete", "kyle")));

        // accept invite
        protocol.processInput(OCCodec.encode(new InviteResponseRequest("accept", "pete", "kyle")));

        // test match available
        out = protocol.processInput(OCCodec.encode(new GetInProgressMatchesRequest("pete")));
        response = OCCodec.decode(out);
        assertTrue(response instanceof InProgressMatchesSuccessResponse);
        matchesResp = (InProgressMatchesSuccessResponse) response;
        ArrayList<String> opponents = new ArrayList<>(), IDs = new ArrayList<>();
        for (int i = 0; i < matchesResp.getMatches().size(); i++) {
            MatchSummary summary = matchesResp.getMatches().get(i);
            opponents.add(summary.getOpponentNickname());
            IDs.add(String.valueOf(summary.getMatchID()));
        }
        assertEquals(opponents.size(), matchesResp.getCount());
        assertEquals(IDs.size(), matchesResp.getCount());
    }

    @Test
    private void testGetTurn(){
        OCServerData data = new OCServerData();
        OCProtocol protocol = new OCProtocol(data);

        data.createProfile("J", "Jonah", "JJJameson@omegachess.com");
        data.createProfile("Peter", "Parker", "definitelynotspidey@omegachess.com");

        // Test no match available
        String out = protocol.processInput(OCCodec.encode(new GetTurnRequest(345)));
        Object response = OCCodec.decode(out);
        assertTrue(response instanceof FailureResponse, "There should not be any matches in the data right now.");

        Match match = new Match("J", "Peter");
        data.addMatch(match);

        out = protocol.processInput(OCCodec.encode(new GetTurnRequest(match.getMatchID())));
        response = OCCodec.decode(out);

        // Player returned equals the first player
        assertTrue(response instanceof TurnSuccessResponse, "Failed to retrieve current turn");
        TurnSuccessResponse turnResp = (TurnSuccessResponse) response;
        assertTrue(turnResp.isSuccess(), "Failed to retrieve current turn");
        assertEquals("J", turnResp.getUser(), "The player's name doesn't match");
        assertEquals("White", turnResp.getColor(), "The current turn color is incorrect");

        // Match ID is invalid
        out = protocol.processInput(OCCodec.encode(new GetTurnRequest(123)));
        response = OCCodec.decode(out);
        assertTrue(response instanceof FailureResponse, "The match ID 123 shouldn't exist");
    }

    @Test
    public void testEndMatch(){
        OCServerData data = new OCServerData();
        OCProtocol protocol = new OCProtocol(data);

        data.createProfile("this", "that", "thishat@omegachess.com");
        data.createProfile("shoe", "cat", "shoecat@omegachess.com");

        Match match = new Match("this", "shoe");
        data.addMatch(match);

        String input = OCCodec.encode(new EndMatchRequest(match.getMatchID(), "this", "shoe"));
        String out = protocol.processInput(input);

        Object response = OCCodec.decode(out);
        assertTrue(response instanceof EndMatchSuccessResponse, "The match was unable to end");
        EndMatchSuccessResponse endResp = (EndMatchSuccessResponse) response;
        assertTrue(endResp.isSuccess(), "The match was unable to end");
        protocol.processInput(input);
        assertEquals(0, data.getMatches().size(), "Failed to remove ended match");
    }

    @Test
    public void testGetArchives()
    {
        OCServerData data = new OCServerData();
        OCProtocol protocol = new OCProtocol(data);

        data.createProfile("darla", "cat", "shoecat@omegachess.com");

        // add game records to archive
        GameRecord r1 = new GameRecord("darla", "nathan", 12, false);
        GameRecord r2 = new GameRecord("player1", "darla", 55, false);
        GameRecord r3 = new GameRecord("darla", "player2", 60, false);
        data.addToArchive(r1);
        data.addToArchive(r2);
        data.addToArchive(r3);

        String out = protocol.processInput(OCCodec.encode(new GetGameRecordsRequest("darla")));

        Object response = OCCodec.decode(out);
        assertTrue(response instanceof GameRecordsSuccessResponse, "Error getting archives");
        GameRecordsSuccessResponse recordsResp = (GameRecordsSuccessResponse) response;
        assertTrue(recordsResp.isSuccess(), "Error getting archives");
        assertEquals(3, recordsResp.getNumber(), "Failed to get correct number of archives");
    }

    @Test
    public void testRoundTripSerialization() {
        // SquareRequest
        SquareRequest sq = new SquareRequest(42);
        String xml = OCCodec.encode(sq);
        Object parsed = OCCodec.decode(xml);
        assertTrue(parsed instanceof SquareRequest);
        assertEquals(42, ((SquareRequest) parsed).getNumber());

        // LoginRequest
        LoginRequest lr = new LoginRequest("nick", "pass");
        xml = OCCodec.encode(lr);
        parsed = OCCodec.decode(xml);
        assertTrue(parsed instanceof LoginRequest);
        assertEquals("nick", ((LoginRequest) parsed).getNickname());
        assertEquals("pass", ((LoginRequest) parsed).getPassword());

        // RegisterRequest
        RegisterRequest rr = new RegisterRequest("e@mail.com", "user1", "pw");
        xml = OCCodec.encode(rr);
        parsed = OCCodec.decode(xml);
        assertTrue(parsed instanceof RegisterRequest);
        assertEquals("e@mail.com", ((RegisterRequest) parsed).getEmail());
        assertEquals("user1", ((RegisterRequest) parsed).getNickname());
        assertEquals("pw", ((RegisterRequest) parsed).getPassword());

        // UnregisterRequest
        UnregisterRequest ur = new UnregisterRequest("user1");
        xml = OCCodec.encode(ur);
        parsed = OCCodec.decode(xml);
        assertTrue(parsed instanceof UnregisterRequest);
        assertEquals("user1", ((UnregisterRequest) parsed).getNickname());

        // GetProfileDataRequest
        GetProfileDataRequest gpdr = new GetProfileDataRequest("nick");
        xml = OCCodec.encode(gpdr);
        parsed = OCCodec.decode(xml);
        assertTrue(parsed instanceof GetProfileDataRequest);
        assertEquals("nick", ((GetProfileDataRequest) parsed).getNickname());

        // SendInviteRequest
        SendInviteRequest sir = new SendInviteRequest("alice", "bob");
        xml = OCCodec.encode(sir);
        parsed = OCCodec.decode(xml);
        assertTrue(parsed instanceof SendInviteRequest);
        assertEquals("alice", ((SendInviteRequest) parsed).getInviter());
        assertEquals("bob", ((SendInviteRequest) parsed).getInvitee());

        // GetInvitesSentRequest
        GetInvitesSentRequest gisr = new GetInvitesSentRequest("alice");
        xml = OCCodec.encode(gisr);
        parsed = OCCodec.decode(xml);
        assertTrue(parsed instanceof GetInvitesSentRequest);
        assertEquals("alice", ((GetInvitesSentRequest) parsed).getUser());

        // GetInvitesReceivedRequest
        GetInvitesReceivedRequest girr = new GetInvitesReceivedRequest("bob");
        xml = OCCodec.encode(girr);
        parsed = OCCodec.decode(xml);
        assertTrue(parsed instanceof GetInvitesReceivedRequest);
        assertEquals("bob", ((GetInvitesReceivedRequest) parsed).getUser());

        // GetNotificationsRequest
        GetNotificationsRequest gnr = new GetNotificationsRequest("nick");
        xml = OCCodec.encode(gnr);
        parsed = OCCodec.decode(xml);
        assertTrue(parsed instanceof GetNotificationsRequest);
        assertEquals("nick", ((GetNotificationsRequest) parsed).getNickname());

        // InviteResponseRequest
        InviteResponseRequest irr = new InviteResponseRequest("accept", "alice", "bob");
        xml = OCCodec.encode(irr);
        parsed = OCCodec.decode(xml);
        assertTrue(parsed instanceof InviteResponseRequest);
        assertEquals("accept", ((InviteResponseRequest) parsed).getResponse());
        assertEquals("alice", ((InviteResponseRequest) parsed).getInviter());
        assertEquals("bob", ((InviteResponseRequest) parsed).getInvitee());

        // GetBoardDataRequest
        GetBoardDataRequest gbdr = new GetBoardDataRequest(99);
        xml = OCCodec.encode(gbdr);
        parsed = OCCodec.decode(xml);
        assertTrue(parsed instanceof GetBoardDataRequest);
        assertEquals(99, ((GetBoardDataRequest) parsed).getId());

        // GetLegalMovesRequest
        GetLegalMovesRequest glmr = new GetLegalMovesRequest(1, 2, 3);
        xml = OCCodec.encode(glmr);
        parsed = OCCodec.decode(xml);
        assertTrue(parsed instanceof GetLegalMovesRequest);
        assertEquals(1, ((GetLegalMovesRequest) parsed).getMatchID());
        assertEquals(2, ((GetLegalMovesRequest) parsed).getRow());
        assertEquals(3, ((GetLegalMovesRequest) parsed).getColumn());

        // MatchMoveRequest
        MatchMoveRequest mmr = new MatchMoveRequest(10, 1, 2, 3, 4);
        xml = OCCodec.encode(mmr);
        parsed = OCCodec.decode(xml);
        assertTrue(parsed instanceof MatchMoveRequest);
        assertEquals(10, ((MatchMoveRequest) parsed).getMatchID());
        assertEquals(1, ((MatchMoveRequest) parsed).getFromRow());
        assertEquals(2, ((MatchMoveRequest) parsed).getFromColumn());
        assertEquals(3, ((MatchMoveRequest) parsed).getToRow());
        assertEquals(4, ((MatchMoveRequest) parsed).getToColumn());

        // GetInProgressMatchesRequest
        GetInProgressMatchesRequest gipmr = new GetInProgressMatchesRequest("nick");
        xml = OCCodec.encode(gipmr);
        parsed = OCCodec.decode(xml);
        assertTrue(parsed instanceof GetInProgressMatchesRequest);
        assertEquals("nick", ((GetInProgressMatchesRequest) parsed).getNickname());

        // GetTurnRequest
        GetTurnRequest gtr = new GetTurnRequest(7);
        xml = OCCodec.encode(gtr);
        parsed = OCCodec.decode(xml);
        assertTrue(parsed instanceof GetTurnRequest);
        assertEquals(7, ((GetTurnRequest) parsed).getId());

        // GetGameRecordsRequest
        GetGameRecordsRequest ggrr = new GetGameRecordsRequest("user");
        xml = OCCodec.encode(ggrr);
        parsed = OCCodec.decode(xml);
        assertTrue(parsed instanceof GetGameRecordsRequest);
        assertEquals("user", ((GetGameRecordsRequest) parsed).getUser());

        // EndMatchRequest
        EndMatchRequest emr = new EndMatchRequest(5, "winner", "loser");
        xml = OCCodec.encode(emr);
        parsed = OCCodec.decode(xml);
        assertTrue(parsed instanceof EndMatchRequest);
        assertEquals(5, ((EndMatchRequest) parsed).getId());
        assertEquals("winner", ((EndMatchRequest) parsed).getWinner());
        assertEquals("loser", ((EndMatchRequest) parsed).getLoser());

        // CheckCheckmateRequest
        CheckCheckmateRequest ccr = new CheckCheckmateRequest(11);
        xml = OCCodec.encode(ccr);
        parsed = OCCodec.decode(xml);
        assertTrue(parsed instanceof CheckCheckmateRequest);
        assertEquals(11, ((CheckCheckmateRequest) parsed).getId());

        // CheckForfeitRequest
        CheckForfeitRequest cfr = new CheckForfeitRequest(12);
        xml = OCCodec.encode(cfr);
        parsed = OCCodec.decode(xml);
        assertTrue(parsed instanceof CheckForfeitRequest);
        assertEquals(12, ((CheckForfeitRequest) parsed).getId());

        // SimpleSuccessResponse
        SimpleSuccessResponse ssr = new SimpleSuccessResponse();
        xml = OCCodec.encode(ssr);
        parsed = OCCodec.decode(xml);
        assertTrue(parsed instanceof SimpleSuccessResponse);
        assertTrue(((SimpleSuccessResponse) parsed).isSuccess());

        // FailureResponse
        FailureResponse fr = new FailureResponse("test reason");
        xml = OCCodec.encode(fr);
        parsed = OCCodec.decode(xml);
        assertTrue(parsed instanceof FailureResponse);
        assertEquals("test reason", ((FailureResponse) parsed).getReason());
        assertFalse(((FailureResponse) parsed).isSuccess());

        // SquareSuccessResponse
        SquareSuccessResponse sqsr = new SquareSuccessResponse("answer text");
        xml = OCCodec.encode(sqsr);
        parsed = OCCodec.decode(xml);
        assertTrue(parsed instanceof SquareSuccessResponse);
        assertEquals("answer text", ((SquareSuccessResponse) parsed).getAnswer());

        // ProfileDataSuccessResponse
        ProfileDataSuccessResponse pdsr = new ProfileDataSuccessResponse(true, "nick", 10, 5, 2);
        xml = OCCodec.encode(pdsr);
        parsed = OCCodec.decode(xml);
        assertTrue(parsed instanceof ProfileDataSuccessResponse);
        ProfileDataSuccessResponse pdsrParsed = (ProfileDataSuccessResponse) parsed;
        assertEquals("nick", pdsrParsed.getNickname());
        assertEquals(10, pdsrParsed.getGamesWon());
        assertEquals(5, pdsrParsed.getGamesLost());
        assertEquals(2, pdsrParsed.getGamesTied());

        // InviteListSuccessResponse
        List<InviteRecord> invites = new ArrayList<>();
        invites.add(new InviteRecord("a", "b", false, false));
        InviteListSuccessResponse ilsr = new InviteListSuccessResponse(true, 1, 1, 5, invites);
        xml = OCCodec.encode(ilsr);
        parsed = OCCodec.decode(xml);
        assertTrue(parsed instanceof InviteListSuccessResponse);
        InviteListSuccessResponse ilsrParsed = (InviteListSuccessResponse) parsed;
        assertEquals(1, ilsrParsed.getInvites().size());
        assertEquals("a", ilsrParsed.getInvites().get(0).getInviter());
        assertEquals("b", ilsrParsed.getInvites().get(0).getInvitee());

        // NotificationsSuccessResponse
        List<NotificationRecord> notifs = new ArrayList<>();
        notifs.add(new NotificationRecord("EVENT", "msg", "2024-01-01"));
        NotificationsSuccessResponse nsr = new NotificationsSuccessResponse(true, 1, notifs);
        xml = OCCodec.encode(nsr);
        parsed = OCCodec.decode(xml);
        assertTrue(parsed instanceof NotificationsSuccessResponse);
        NotificationsSuccessResponse nsrParsed = (NotificationsSuccessResponse) parsed;
        assertEquals(1, nsrParsed.getCount());
        assertEquals("EVENT", nsrParsed.getNotifications().get(0).getEvent());
        assertEquals("msg", nsrParsed.getNotifications().get(0).getMessage());

        // InviteResponseSuccessResponse
        InviteResponseSuccessResponse irsr = new InviteResponseSuccessResponse(true, "42");
        xml = OCCodec.encode(irsr);
        parsed = OCCodec.decode(xml);
        assertTrue(parsed instanceof InviteResponseSuccessResponse);
        assertEquals("42", ((InviteResponseSuccessResponse) parsed).getMatchID());

        // BoardDataSuccessResponse
        List<PieceEntry> pieces = new ArrayList<>();
        pieces.add(new PieceEntry("a1", "Rook"));
        BoardDataSuccessResponse bdsr = new BoardDataSuccessResponse(true, pieces);
        xml = OCCodec.encode(bdsr);
        parsed = OCCodec.decode(xml);
        assertTrue(parsed instanceof BoardDataSuccessResponse);
        BoardDataSuccessResponse bdsrParsed = (BoardDataSuccessResponse) parsed;
        assertEquals(1, bdsrParsed.getPieces().size());
        assertEquals("a1", bdsrParsed.getPieces().get(0).getPosition());
        assertEquals("Rook", bdsrParsed.getPieces().get(0).getPiece());

        // LegalMovesSuccessResponse
        LegalMovesSuccessResponse lmsr = new LegalMovesSuccessResponse(true, "/a3/a4/", false);
        xml = OCCodec.encode(lmsr);
        parsed = OCCodec.decode(xml);
        assertTrue(parsed instanceof LegalMovesSuccessResponse);
        assertEquals("/a3/a4/", ((LegalMovesSuccessResponse) parsed).getLegalMoves());
        assertFalse(((LegalMovesSuccessResponse) parsed).isEnPassant());

        // InProgressMatchesSuccessResponse
        List<MatchSummary> matchSummaries = new ArrayList<>();
        matchSummaries.add(new MatchSummary("opp", 77, 1));
        InProgressMatchesSuccessResponse ipmsr = new InProgressMatchesSuccessResponse(true, 1, matchSummaries);
        xml = OCCodec.encode(ipmsr);
        parsed = OCCodec.decode(xml);
        assertTrue(parsed instanceof InProgressMatchesSuccessResponse);
        InProgressMatchesSuccessResponse ipmsrParsed = (InProgressMatchesSuccessResponse) parsed;
        assertEquals(1, ipmsrParsed.getCount());
        assertEquals("opp", ipmsrParsed.getMatches().get(0).getOpponentNickname());
        assertEquals(77, ipmsrParsed.getMatches().get(0).getMatchID());

        // TurnSuccessResponse
        TurnSuccessResponse tsr = new TurnSuccessResponse(true, "player1", "White");
        xml = OCCodec.encode(tsr);
        parsed = OCCodec.decode(xml);
        assertTrue(parsed instanceof TurnSuccessResponse);
        assertEquals("player1", ((TurnSuccessResponse) parsed).getUser());
        assertEquals("White", ((TurnSuccessResponse) parsed).getColor());

        // GameRecordsSuccessResponse
        List<GameRecordEntry> records = new ArrayList<>();
        records.add(new GameRecordEntry("opp", "win", 30));
        GameRecordsSuccessResponse grsr = new GameRecordsSuccessResponse(true, 1, records);
        xml = OCCodec.encode(grsr);
        parsed = OCCodec.decode(xml);
        assertTrue(parsed instanceof GameRecordsSuccessResponse);
        GameRecordsSuccessResponse grsrParsed = (GameRecordsSuccessResponse) parsed;
        assertEquals(1, grsrParsed.getNumber());
        assertEquals("opp", grsrParsed.getRecords().get(0).getOpponentNickname());
        assertEquals("win", grsrParsed.getRecords().get(0).getResult());
        assertEquals(30, grsrParsed.getRecords().get(0).getMoves());

        // EndMatchSuccessResponse
        EndMatchSuccessResponse emsr = new EndMatchSuccessResponse(true, "archiveId");
        xml = OCCodec.encode(emsr);
        parsed = OCCodec.decode(xml);
        assertTrue(parsed instanceof EndMatchSuccessResponse);
        assertEquals("archiveId", ((EndMatchSuccessResponse) parsed).getArchiveID());

        // CheckmateSuccessResponse
        CheckmateSuccessResponse cmsr = new CheckmateSuccessResponse(true, true, "loser", "winner");
        xml = OCCodec.encode(cmsr);
        parsed = OCCodec.decode(xml);
        assertTrue(parsed instanceof CheckmateSuccessResponse);
        CheckmateSuccessResponse cmsrParsed = (CheckmateSuccessResponse) parsed;
        assertTrue(cmsrParsed.isCheckmate());
        assertEquals("loser", cmsrParsed.getLoser());
        assertEquals("winner", cmsrParsed.getWinner());

        // ForfeitSuccessResponse
        ForfeitSuccessResponse fsr = new ForfeitSuccessResponse(true, true);
        xml = OCCodec.encode(fsr);
        parsed = OCCodec.decode(xml);
        assertTrue(parsed instanceof ForfeitSuccessResponse);
        assertTrue(((ForfeitSuccessResponse) parsed).isForfeit());
    }
}
