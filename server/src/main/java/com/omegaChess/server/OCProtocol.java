package com.omegaChess.server;

import com.omegaChess.board.ChessBoard;
import com.omegaChess.exceptions.IllegalMoveException;
import com.omegaChess.exceptions.IllegalPositionException;
import com.omegaChess.pieces.ChessPiece;
import com.omegaChess.pieces.LegalMoves;
import com.omegaChess.protocol.OCCodec;
import com.omegaChess.protocol.messages.*;

import java.util.ArrayList;
import java.util.List;

// this class is responsible for actually processing any input from a client
public class OCProtocol {

    private final OCServerData serverData;

    public OCProtocol(OCServerData data) {
        serverData = data;
    }

    public String processInput(String input) {
        String toReturn = "";
        try {
            Object request = OCCodec.decode(input);

            if (request instanceof SquareRequest) {
                toReturn = squareInput((SquareRequest) request);
            } else if (request instanceof RegisterRequest) {
                toReturn = registerUser((RegisterRequest) request);
            } else if (request instanceof UnregisterRequest) {
                toReturn = unregisterUser((UnregisterRequest) request);
            } else if (request instanceof LoginRequest) {
                toReturn = loginUser((LoginRequest) request);
            } else if (request instanceof GetProfileDataRequest) {
                toReturn = getProfileData((GetProfileDataRequest) request);
            } else if (request instanceof SendInviteRequest) {
                toReturn = sendInvite((SendInviteRequest) request);
            } else if (request instanceof GetInvitesSentRequest) {
                toReturn = getSentInvites((GetInvitesSentRequest) request);
            } else if (request instanceof GetInvitesReceivedRequest) {
                toReturn = getReceivedInvites((GetInvitesReceivedRequest) request);
            } else if (request instanceof GetNotificationsRequest) {
                toReturn = getNotifications((GetNotificationsRequest) request);
            } else if (request instanceof InviteResponseRequest) {
                toReturn = inviteResponse((InviteResponseRequest) request);
            } else if (request instanceof GetLegalMovesRequest) {
                toReturn = getLegalMoves((GetLegalMovesRequest) request);
            } else if (request instanceof GetBoardDataRequest) {
                toReturn = getBoardData((GetBoardDataRequest) request);
            } else if (request instanceof MatchMoveRequest) {
                toReturn = matchMove((MatchMoveRequest) request);
            } else if (request instanceof GetInProgressMatchesRequest) {
                toReturn = resumeMatchesListResponse((GetInProgressMatchesRequest) request);
            } else if (request instanceof GetTurnRequest) {
                toReturn = getTurn((GetTurnRequest) request);
            } else if (request instanceof EndMatchRequest) {
                toReturn = endMatch((EndMatchRequest) request);
            } else if (request instanceof GetGameRecordsRequest) {
                toReturn = getGameRecords((GetGameRecordsRequest) request);
            } else if (request instanceof CheckCheckmateRequest) {
                toReturn = checkCheckmate((CheckCheckmateRequest) request);
            } else if (request instanceof CheckForfeitRequest) {
                toReturn = checkForfeit((CheckForfeitRequest) request);
            } else {
                toReturn = OCCodec.encode(new FailureResponse("process not recognized"));
            }
        } catch (Exception e) {
            toReturn = OCCodec.encode(new FailureResponse("Something went wrong when processing input."));
            e.printStackTrace();
            System.out.println("Something went wrong when processing input.");
        }

        return toReturn;
    }

    private String squareInput(SquareRequest request) {
        int number = request.getNumber();

        System.out.println("Attempting to square " + number + "...");

        int square = number * number;
        System.out.println("Square: " + square);

        return OCCodec.encode(new SquareSuccessResponse("Square of " + number + " is " + square));
    }

    private String registerUser(RegisterRequest request) {

        String email = request.getEmail();
        String nickname = request.getNickname();
        String password = request.getPassword();

        System.out.println("Attempting to register new user: " + nickname);

        Boolean success = serverData.createProfile(nickname, password, email);

        if (success) {
            System.out.println("Registered!");
            return OCCodec.encode(new SimpleSuccessResponse());
        }
        else {
            System.out.println("Nickname or email was taken.");
            return OCCodec.encode(new FailureResponse("nickname/email was taken"));
        }
    }

    private String unregisterUser(UnregisterRequest request) {

        String nickname = request.getNickname();

        System.out.println("Attempting to unregister user: " + nickname);

        Boolean success = serverData.removeProfile(nickname);
        for (GameRecord game : serverData.getArchive()){
            if (game.getLoser().equalsIgnoreCase(nickname))
                game.setLoser("[deleted]");
            if (game.getWinner().equalsIgnoreCase(nickname))
                game.setWinner("[deleted]");
        }
        for (Match match : serverData.getMatches()){
            if (match.getProfile1().equalsIgnoreCase(nickname)){
                match.endMatch("[deleted]", match.getProfile2(), match.getBoard().getMoves().size());
                serverData.removeMatch(match);
                serverData.getProfile(match.getProfile2()).getMailbox().addNotification(Notification.NotificationType.MATCH_ENDED,
                        "Other user deleted their account before the game ended.");
            }
            if (match.getProfile2().equalsIgnoreCase(nickname)){
                match.endMatch("[deleted]", match.getProfile1(), match.getBoard().getMoves().size());
                serverData.removeMatch(match);
                serverData.getProfile(match.getProfile1()).getMailbox().addNotification(Notification.NotificationType.MATCH_ENDED,
                        "Other user deleted their account before the game ended.");
            }
            if (serverData.getMatches().size() == 0)
                break;
        }
        for (UserProfile player : serverData.getProfiles()){
            Mailbox mail = player.getMailbox();
            for (Invite invite : mail.getReceived()){
                if (invite.getInviter().equalsIgnoreCase(nickname)) {
                    invite.Decline();
                    mail.removeFromReceived(invite);
                    mail.addNotification(Notification.NotificationType.INVITE_CANCELLED,
                            "Other user deleted their account before a response was made.");
                }
                if (mail.getReceived().size() == 0)
                    break;
            }
            for (Invite invite: mail.getSent()){
                if (invite.getInvitee().equalsIgnoreCase(nickname)) {
                    invite.Decline();
                    mail.removeFromSent(invite);
                    mail.addNotification(Notification.NotificationType.DECLINED_INVITE,
                            "Other user deleted their account before responding.");
                }
                if (mail.getSent().size() == 0)
                    break;
            }
        }

        if (success) {
            System.out.println("Unregistered!");
            return OCCodec.encode(new SimpleSuccessResponse());
        }
        else {
            System.out.println("Nickname wasn't found.");
            return OCCodec.encode(new FailureResponse("nickname wasn't found"));
        }
    }

    private String loginUser(LoginRequest request) {

        String nickname = request.getNickname();
        String password = request.getPassword();

        System.out.println("Attempting to login user: " + nickname);

        if (!serverData.profileExists(nickname)) {
            // profile doesn't exist
            System.out.println("Nickname wasn't found.");
            return OCCodec.encode(new FailureResponse("nickname wasn't found"));
        }

        Boolean success = serverData.checkPassword(nickname, password);

        if (success) {
            System.out.println("Logged in!");
            return OCCodec.encode(new SimpleSuccessResponse());
        }
        else {
            System.out.println("Wrong password.");
            return OCCodec.encode(new FailureResponse("wrong password"));
        }
    }

    private String getProfileData(GetProfileDataRequest request) {

        String nickname = request.getNickname();

        System.out.println("Attempting to get profile data for user: " + nickname);

        if (!serverData.profileExists(nickname)) {
            // profile doesn't exist
            System.out.println("Nickname wasn't found.");
            return OCCodec.encode(new FailureResponse("nickname wasn't found"));
        }

        ProfileDataSuccessResponse response = new ProfileDataSuccessResponse(
                true,
                nickname,
                serverData.getProfile(nickname).getGamesWon(),
                serverData.getProfile(nickname).getGamesLost(),
                serverData.getProfile(nickname).getGamesTied()
        );

        return OCCodec.encode(response);

    }

   private String sendInvite(SendInviteRequest request){

        String inviter = request.getInviter();
        String invitee = request.getInvitee();

        if (invitee.equalsIgnoreCase(inviter)){
            // Can't send invite to yourself
            System.out.println("Can't send an invite to yourself");
            return OCCodec.encode(new FailureResponse("Can't send an invite to yourself"));
        }

       System.out.println("Attempting to send invite from " + inviter + " to " + invitee);

       if (!serverData.profileExists(invitee)){
           // Invitee doesn't exist
           System.out.println("Target user doesn't exist");
           return OCCodec.encode(new FailureResponse("input user doesn't exist"));
       }

       UserProfile player1 = serverData.getProfile(inviter);
       UserProfile player2 = serverData.getProfile(invitee);
       if (lookForMatch(inviter, invitee) != null){
           String reason = "Already in a match with " + invitee;
           System.out.println(reason);
           return OCCodec.encode(new FailureResponse(reason));
       }
       if (lookForInvite(inviter, invitee, player1.getMailbox(), true) != null){
           String reason = "Already sent an invite to " + invitee;
           System.out.println(reason);
           return OCCodec.encode(new FailureResponse(reason));
       }else if (lookForInvite(inviter, invitee, player1.getMailbox(), false) != null){
           String reason = "Already have an invite from " + inviter;
           System.out.println(reason);
           return OCCodec.encode(new FailureResponse(reason));
       }
       Invite invite = new Invite(inviter, invitee);
       player1.getMailbox().addToSent(invite);
       player2.getMailbox().addToReceived(invite);
       player2.getMailbox().addNotification(Notification.NotificationType.INVITE_REQUEST,
               "You have been invited to play OmegaChess by user: " + inviter +
               ". Go to your mailbox to accept/decline this invite.");

       System.out.println("Invite has been sent");
       return OCCodec.encode(new SimpleSuccessResponse());

   }

   private String getSentInvites(GetInvitesSentRequest request){

        String user = request.getUser();

        System.out.println("Attempting to recover sent invites from " + user);

        if (!serverData.profileExists(user)){
            // target user doesn't exist
            System.out.println("Target user doesn't exist");
            return OCCodec.encode(new FailureResponse("target user doesn't exist"));
        }

        UserProfile profile = serverData.getProfile(user);
        ArrayList<Invite> sent = profile.getMailbox().getSent();
        List<InviteRecord> inviteRecords = new ArrayList<InviteRecord>();
        for (Invite invite : sent) {
            InviteRecord record = new InviteRecord(
                    invite.getInviter(),
                    invite.getInvitee(),
                    invite.isAccepted(),
                    invite.isDeclined()
            );
            inviteRecords.add(record);
        }

       InviteListSuccessResponse response = new InviteListSuccessResponse(
               true,
               sent.size(),
               inviteRecords.size(),
               serverData.getLongestNickname(),
               inviteRecords
       );

       System.out.println("Recovered sent invites!");
       return OCCodec.encode(response);
   }

   private String getReceivedInvites(GetInvitesReceivedRequest request){

        String user = request.getUser();

        System.out.println("Attempting to recover received invites from " + user);

        if (!serverData.profileExists(user)){
            // target user doesn't exist
            System.out.println("Target user doesn't exist");
            return OCCodec.encode(new FailureResponse("target user doesn't exist"));
        }

        UserProfile profile = serverData.getProfile(user);
        ArrayList<Invite> received = profile.getMailbox().getReceived();
        List<InviteRecord> inviteRecords = new ArrayList<InviteRecord>();
        for (Invite invite : received) {
            InviteRecord record = new InviteRecord(
                    invite.getInviter(),
                    invite.getInvitee(),
                    invite.isAccepted(),
                    invite.isDeclined()
            );
            inviteRecords.add(record);
        }

        InviteListSuccessResponse response = new InviteListSuccessResponse(
                true,
                received.size(),
                inviteRecords.size(),
                serverData.getLongestNickname(),
                inviteRecords
        );

       System.out.println("Recovered received invites!");
       return OCCodec.encode(response);
   }

   private String getNotifications(GetNotificationsRequest request) {
       String user = request.getNickname();

       if (!serverData.profileExists(user)){
           // target user doesn't exist
           System.out.println("Target user doesn't exist");
           return OCCodec.encode(new FailureResponse("target user doesn't exist"));
       }

       ArrayList<Notification> notifications = serverData.getProfile(user).getMailbox().getNotifications();

       List<NotificationRecord> notificationRecords = new ArrayList<NotificationRecord>();
       for (int i = 0; i < notifications.size(); i++) {
           notificationRecords.add(new NotificationRecord(
                   notifications.get(i).getEvent().name(),
                   notifications.get(i).getMessage(),
                   notifications.get(i).getDateString()
           ));
       }

       NotificationsSuccessResponse response = new NotificationsSuccessResponse(
               true,
               notifications.size(),
               notificationRecords
       );

       return OCCodec.encode(response);
   }

    private String inviteResponse(InviteResponseRequest request){
        String response = request.getResponse();
        String inviter = request.getInviter();
        String invitee = request.getInvitee();

        System.out.println("Attempting to " + response + " invite from " + inviter + " to " + invitee);

        if (response.equals("accept")) {
            for (UserProfile profile : serverData.getProfiles()){
                Mailbox mail = profile.getMailbox();
                for (Invite invite : mail.getSent()){
                    if (invite.getInviter().equalsIgnoreCase(inviter) && invite.getInvitee().equalsIgnoreCase(invitee)){
                        Invite inviteF = lookForInvite(inviter, invitee, serverData.getProfile(invitee).getMailbox(), false);
                        invite.Accept();
                        mail.removeFromSent(invite);
                        serverData.getProfile(invitee).getMailbox().removeFromReceived(inviteF);
                        Match match = invite.makeMatch();
                        int matchID = match.getMatchID();
                        serverData.addMatch(match);
                        mail.addNotification(Notification.NotificationType.ACCEPTED_INVITE,
                                invitee + " accepted your invite request. Go to the Resume Game screen to enter the match.");
                        return OCCodec.encode(new InviteResponseSuccessResponse(true, Integer.toString(matchID)));
                    }
                }
            }
        }else if (response.equals("decline")) {
            for (UserProfile profile : serverData.getProfiles()){
                Mailbox mail = profile.getMailbox();
                for (Invite invite : mail.getSent()){
                    if (invite.getInviter().equalsIgnoreCase(inviter) && invite.getInvitee().equalsIgnoreCase(invitee)){
                        Invite inviteF = lookForInvite(inviter, invitee, serverData.getProfile(invitee).getMailbox(), false);
                        invite.Decline();
                        mail.removeFromSent(invite);
                        serverData.getProfile(invitee).getMailbox().removeFromReceived(inviteF);
                        mail.addNotification(Notification.NotificationType.DECLINED_INVITE,
                                invitee + " declined your invite request.");
                        return OCCodec.encode(new InviteResponseSuccessResponse(true, null));
                    }
                }
            }
        }
        return OCCodec.encode(new InviteResponseSuccessResponse(true, null));
    }

    public String getBoardData(GetBoardDataRequest request){
        int ID = request.getId();

        System.out.println("Attempting to get board for match " + ID);

        Match match = null;
        if (serverData.getMatches().size() == 0){
            return OCCodec.encode(new FailureResponse("There are no matches available"));
        }
        for (Match mat : serverData.getMatches()){
            if (mat.getMatchID() == ID) {
                match = mat;
                break;
            }
        }
        if (match == null) {
            return OCCodec.encode(new FailureResponse("No match found that has ID=" + ID));
        }

        List<PieceEntry> pieceList = new ArrayList<PieceEntry>();
        for (ChessPiece piece : match.getBoard().white_pieces) {
            pieceList.add(new PieceEntry(piece.getPosition(), piece.toString()));
        }
        for (ChessPiece piece : match.getBoard().black_pieces) {
            pieceList.add(new PieceEntry(piece.getPosition(), piece.toString()));
        }

        return OCCodec.encode(new BoardDataSuccessResponse(true, pieceList));
    }

    private String getLegalMoves(GetLegalMovesRequest request) {
        int matchID = request.getMatchID();
        int row = request.getRow();
        int column = request.getColumn();

        // get correct match and board

        Match match = serverData.getMatch(matchID);
        ChessBoard board = match.getBoard();

        // get piece at specified position on board
        String position = board.reverseParse(row, column);
        ChessPiece piece = null;
        try {
            piece = board.getPiece(position);
        } catch (IllegalPositionException e) {
            e.printStackTrace();
        }

        // get legal moves for that piece
        LegalMoves moves;
        if (piece == null) {
            return OCCodec.encode(new FailureResponse("no piece at specified position"));
        } else {
            moves = piece.getNormalOrCheckMoves();
        }
        String legalMoves = "/";
        for (String move : moves.getListOfMoves()) {
            legalMoves += move;
            legalMoves += "/";
        }

        System.out.println("Sending legal moves: " + legalMoves);

        return OCCodec.encode(new LegalMovesSuccessResponse(true, legalMoves, moves.isEnPessant()));
    }

    private String matchMove(MatchMoveRequest request) {
        int matchID = request.getMatchID();
        int[] fromArray = new int[2];
        int[] toArray = new int[2];
        fromArray[0] = request.getFromRow();
        fromArray[1] = request.getFromColumn();
        toArray[0] = request.getToRow();
        toArray[1] = request.getToColumn();

        // get correct match and board
        Match match = serverData.getMatch(matchID);
        ChessBoard board = match.getBoard();
        String fromPosition = board.reverseParse(fromArray[0], fromArray[1]);
        String toPosition = board.reverseParse(toArray[0], toArray[1]);

        // make move
        boolean moveMade = false;
        try {
            board.move(fromPosition, toPosition);
            moveMade = true;
        } catch (IllegalMoveException e) {
            e.printStackTrace();
        }

        if (moveMade) {
            System.out.println("Move was successful!");
            return OCCodec.encode(new SimpleSuccessResponse());
        } else {
            System.out.println("Invalid move!");
            return OCCodec.encode(new FailureResponse("invalid move"));
        }
    }

    public String resumeMatchesListResponse(GetInProgressMatchesRequest request) {
        String user = request.getNickname();
        int count = 0;
        ArrayList<Match> matches = serverData.getMatches();

        List<MatchSummary> matchSummaries = new ArrayList<MatchSummary>();
        // This allows us to figure out if the user requesting the match to resume is the first or second player
        for (Match m : matches) {
            if (m.getProfile1().equalsIgnoreCase(user)) {
                count++;
                matchSummaries.add(new MatchSummary(m.getProfile2(), m.getMatchID(), 1));
            }
            else if (m.getProfile2().equalsIgnoreCase(user)) {
                count++;
                matchSummaries.add(new MatchSummary(m.getProfile1(), m.getMatchID(), 2));
            }
        }

        return OCCodec.encode(new InProgressMatchesSuccessResponse(true, count, matchSummaries));
    }

    public String getTurn(GetTurnRequest request){
        int ID = request.getId();

        TurnTracker turn = null;
        if (serverData.getMatches().size() == 0){
            return OCCodec.encode(new FailureResponse("There are no matches available"));
        }
        for (Match match : serverData.getMatches()){
            if (match.getMatchID() == ID) {
                turn = match.getBoard().getTurn();
                break;
            }
        }
        if (turn == null) {
            return OCCodec.encode(new FailureResponse("No match found that has ID=" + ID));
        }

        return OCCodec.encode(new TurnSuccessResponse(true, turn.getCurrentTurnPlayer(), turn.getCurrentTurnColor().toString()));
    }

    public String endMatch(EndMatchRequest request) {
        int ID = request.getId();
        int moves = 0;
        String loser = request.getLoser();
        String winner = request.getWinner();

        Match end = null;
        if (serverData.getMatches().size() == 0) {
            return OCCodec.encode(new FailureResponse("There are no matches available"));
        }
        for (Match match : serverData.getMatches()) {
            if (match.getMatchID() == ID) {
                end = match;
                break;
            }
        }
        if (end == null) {
            return OCCodec.encode(new FailureResponse("there is no match with ID " + ID));
        }
        if (end.isAcknowledgeEnd()) {
            moves = end.getBoard().getMoves().size();
            UserProfile user = serverData.getProfile(winner);
            user.increment("gamesWon");
            user = serverData.getProfile(loser);
            user.increment("gamesLost");
            serverData.addToArchive(end.endMatch(loser, winner, moves));
            serverData.removeMatch(end);
            return OCCodec.encode(new EndMatchSuccessResponse(true, String.valueOf(serverData.getArchive().size())));
        }
        else {
            end.setAcknowledgeEnd(true);
            return OCCodec.encode(new EndMatchSuccessResponse(true, null));
        }
    }

    // Helper method to grab an invite between users
    public Invite lookForInvite(String inviter, String invitee, Mailbox mail, boolean sent){
        if (sent) {
            for (Invite invite : mail.getSent()) {
                if ((invite.getInviter().equalsIgnoreCase(inviter) && invite.getInvitee().equalsIgnoreCase(invitee)) ||
                        (invite.getInviter().equalsIgnoreCase(invitee) && invite.getInvitee().equalsIgnoreCase(inviter)))
                    return invite;
            }
        }else {
            for (Invite invite : mail.getReceived()) {
                if ((invite.getInviter().equalsIgnoreCase(inviter) && invite.getInvitee().equalsIgnoreCase(invitee)) ||
                        (invite.getInviter().equalsIgnoreCase(invitee) && invite.getInvitee().equalsIgnoreCase(inviter)))
                    return invite;
            }
        }
        return null;
    }

    // Helper method to grab a match between users
    public Match lookForMatch(String player1, String player2){
        for (Match match : serverData.getMatches()){
            if ((match.getProfile1().equalsIgnoreCase(player1) && match.getProfile2().equalsIgnoreCase(player2)) ||
                    (match.getProfile1().equalsIgnoreCase(player2) && match.getProfile2().equalsIgnoreCase(player1)))
                return match;
        }
        return null;
    }

    private String getGameRecords(GetGameRecordsRequest request) {
        String user = request.getUser();

        if (!serverData.profileExists(user)){
            // target user doesn't exist
            System.out.println("Target user doesn't exist");
            return OCCodec.encode(new FailureResponse("target user doesn't exist"));
        }

        List<GameRecordEntry> entries = new ArrayList<GameRecordEntry>();
        int countForUser = 0;
        for( GameRecord record : serverData.getArchive()){
            ArrayList<String> players = record.getPlayers();

            if(user.equals(players.get(0)) || user.equals(players.get(1)))
            {
                String opponent;
                if(user.equals(players.get(0)))
                {
                    opponent = players.get(1);
                }
                else
                {
                    opponent = players.get(0);
                }

                String result;
                if(record.isDraw())
                {
                    result = "tie";
                }
                else
                {
                    result = record.getWinner();
                }

                entries.add(new GameRecordEntry(opponent, result, record.getNumMoves()));
                countForUser++;
            }
        }

        return OCCodec.encode(new GameRecordsSuccessResponse(true, countForUser, entries));
    }

    private String checkCheckmate(CheckCheckmateRequest request) {
        int ID = request.getId();

        Match match = null;
        if (serverData.getMatches().size() == 0){
            return OCCodec.encode(new FailureResponse("There are no matches available"));
        }
        for (Match m : serverData.getMatches()){
            if (m.getMatchID() == ID) {
                match = m;
                break;
            }
        }
        if (match == null){
            return OCCodec.encode(new FailureResponse("there is no match with ID " + ID));
        }
        String p1 = match.getProfile1();
        String p2 = match.getProfile2();
        boolean inCheckmate = match.checkCheckmate();

        if (inCheckmate) {
            String loser;
            String winner;
            if (p1.equals(match.getBoard().getTurn().getCurrentTurnPlayer())) {
                loser = p1;
                winner = p2;
            }
            else {
                loser = p2;
                winner = p1;
            }
            return OCCodec.encode(new CheckmateSuccessResponse(true, true, loser, winner));
        }
        else {
            return OCCodec.encode(new CheckmateSuccessResponse(true, false, null, null));
        }
    }

    private String checkForfeit(CheckForfeitRequest request) {
        int ID = request.getId();

        Match match = null;
        if (serverData.getMatches().size() == 0){
            return OCCodec.encode(new FailureResponse("There are no matches available"));
        }
        for (Match m : serverData.getMatches()){
            if (m.getMatchID() == ID) {
                match = m;
                break;
            }
        }
        if (match == null){
            return OCCodec.encode(new FailureResponse("there is no match with ID " + ID));
        }

        return OCCodec.encode(new ForfeitSuccessResponse(true, match.isAcknowledgeEnd()));
    }
}
