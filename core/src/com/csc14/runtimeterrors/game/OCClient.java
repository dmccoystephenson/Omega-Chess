package com.csc14.runtimeterrors.game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;

import com.omegaChess.protocol.OCMessageFactory;
import com.omegaChess.protocol.Result;
import com.omegaChess.protocol.messages.*;

public class OCClient {

    private static final String serverHostName = "34.71.50.54"; // in order to test on production, OCMultiServer.java must be running on the server host
    private static final String localHostName = "localhost"; // set this to your hostname when testing locally


    private final PrintWriter out;
    private final BufferedReader in;

    public OCClient(boolean useLocalArg) throws IOException {
        String hostName;
        if(useLocalArg)
        {
            hostName = localHostName;
        }
        else
        {
            hostName = serverHostName;
        }

        int portNumber = 8484;
        Socket socket = new Socket(hostName, portNumber);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    private <S> Result<S> sendRequestAndReceive(Object request, Class<S> successType) {
        String xml = OCMessageFactory.toXml(request);
        out.println(xml);

        String responseXml;
        try {
            responseXml = in.readLine();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail(new FailureResponse("Communication error"));
        }

        Object response = OCMessageFactory.fromXml(responseXml);
        if (response instanceof FailureResponse) {
            return Result.fail((FailureResponse) response);
        }
        return Result.ok(successType.cast(response));
    }

    private <S> boolean printResult(Result<S> result) {
        if (result.isSuccess()) {
            System.out.println("Success!");
            return true;
        }
        else {
            System.out.println(result.getReason());
            return false;
        }
    }

    // example request
    public String sendSquareRequest(String number) {

        System.out.println("Sending square request!");

        SquareRequest request = new SquareRequest(Integer.parseInt(number));

        Result<SquareSuccessResponse> result = sendRequestAndReceive(request, SquareSuccessResponse.class);

        if (result.isSuccess()) {
            return result.getSuccess().getAnswer();
        }
        else {
            return result.getReason();
        }

    }

    // register request
    public boolean sendRegisterRequest(String email, String nickname, String password) {

        System.out.println("Sending register request for " + nickname + "!");

        RegisterRequest request = new RegisterRequest(email, nickname, password);

        Result<SimpleSuccessResponse> result = sendRequestAndReceive(request, SimpleSuccessResponse.class);

        return printResult(result);
    }

    // unregister request
    public boolean sendUnregisterRequest(String nickname) {
        System.out.println("Sending unregister request for " + nickname + "!");

        UnregisterRequest request = new UnregisterRequest(nickname);

        Result<SimpleSuccessResponse> result = sendRequestAndReceive(request, SimpleSuccessResponse.class);

        return printResult(result);
    }

    // login request
    public Result<SimpleSuccessResponse> sendLoginRequest(String nickname, String password) {
        System.out.println("Sending login request for " + nickname + "!");

        LoginRequest request = new LoginRequest(nickname, password);

        Result<SimpleSuccessResponse> result = sendRequestAndReceive(request, SimpleSuccessResponse.class);

        printResult(result);

        return result;
    }

    // get profile data request
    public Result<ProfileDataSuccessResponse> sendGetProfileDataRequest(String nickname) {
        System.out.println("Sending get profile data request for " + nickname + "!");

        GetProfileDataRequest request = new GetProfileDataRequest(nickname);

        Result<ProfileDataSuccessResponse> result = sendRequestAndReceive(request, ProfileDataSuccessResponse.class);

        printResult(result);

        return result;
    }

    // send invite request
    public Result<SimpleSuccessResponse> sendInviteRequest(String inviter, String invitee){
        System.out.println("Sending invite request from " + inviter + " to " + invitee + "!");

        SendInviteRequest request = new SendInviteRequest(inviter.toLowerCase(), invitee.toLowerCase());

        Result<SimpleSuccessResponse> result = sendRequestAndReceive(request, SimpleSuccessResponse.class);

        printResult(result);

        return result;

    }

    // get sent invites request
    public Result<InviteListSuccessResponse> getSentInvites(String user){
        System.out.println("Sending request to get sent invites from mailbox!");

        GetInvitesSentRequest request = new GetInvitesSentRequest(user);

        // Send and receive results
        Result<InviteListSuccessResponse> result = sendRequestAndReceive(request, InviteListSuccessResponse.class);

        printResult(result);

        return result;
    }

    // get received invites request
    public Result<InviteListSuccessResponse> getReceivedInvites(String user){
        System.out.println("Sending request to get received invites from mailbox!");

        GetInvitesReceivedRequest request = new GetInvitesReceivedRequest(user);

        // Send and receive results
        Result<InviteListSuccessResponse> result = sendRequestAndReceive(request, InviteListSuccessResponse.class);

        printResult(result);

        return result;
    }

    // get notifications request
    public Result<NotificationsSuccessResponse> getNotifications(String nickname) {
        System.out.println("Sending request to get notifications from mailbox for user: " + nickname);

        GetNotificationsRequest request = new GetNotificationsRequest(nickname);

        // Send and receive results
        Result<NotificationsSuccessResponse> result = sendRequestAndReceive(request, NotificationsSuccessResponse.class);

        printResult(result);

        return result;
    }


    // get legal moves request
    public Result<LegalMovesSuccessResponse> getLegalMoves(int matchID, int[] position) {
        System.out.println("Sending request to get legal moves for matchID: " + matchID + " and piece at position: " + position[0] + "," + position[1]);

        GetLegalMovesRequest request = new GetLegalMovesRequest(matchID, position[0], position[1]);

        // Send and receive results
        Result<LegalMovesSuccessResponse> result = sendRequestAndReceive(request, LegalMovesSuccessResponse.class);

        printResult(result);

        return result;
    }

    // Accept an invite from another user
    public Result<InviteResponseSuccessResponse> acceptInvite(String user, String inviter){
        System.out.println("Accepting an invitation from " + inviter);

        InviteResponseRequest request = new InviteResponseRequest("accept", inviter, user);

        Result<InviteResponseSuccessResponse> result = sendRequestAndReceive(request, InviteResponseSuccessResponse.class);

        printResult(result);

        return result;
    }

    // Decline an invite from another user
    public Result<SimpleSuccessResponse> declineInvite(String user, String inviter){
        System.out.println("Accepting an invitation from " + inviter);

        InviteResponseRequest request = new InviteResponseRequest("decline", inviter, user);

        Result<SimpleSuccessResponse> result = sendRequestAndReceive(request, SimpleSuccessResponse.class);

        printResult(result);

        return result;
    }

    // Get board data from server for a match
    public Result<BoardDataSuccessResponse> getBoardData(int ID){
        //System.out.println("Getting board data for match with ID="+ID);

        GetBoardDataRequest request = new GetBoardDataRequest(ID);

        Result<BoardDataSuccessResponse> result = sendRequestAndReceive(request, BoardDataSuccessResponse.class);

        //printResult(result);

        return result;
    }

    // send move to be made on the server
    public Result<SimpleSuccessResponse> matchMove(int matchID, int[] fromPosition, int[] toPosition){
        System.out.println("Sending move from "+ Arrays.toString(fromPosition) +" to "+ Arrays.toString(toPosition) +" to the server");

        MatchMoveRequest request = new MatchMoveRequest(matchID, fromPosition[0], fromPosition[1], toPosition[0], toPosition[1]);

        // receive message
        Result<SimpleSuccessResponse> result = sendRequestAndReceive(request, SimpleSuccessResponse.class);

        printResult(result);

        return result;
    }

    // request the matches a user can resume
    public Result<InProgressMatchesSuccessResponse> getResumeMatches(String nickname) {
        System.out.println("Sending get in-progress matches request for " + nickname);

        GetInProgressMatchesRequest request = new GetInProgressMatchesRequest(nickname);

        // receive message
        Result<InProgressMatchesSuccessResponse> result = sendRequestAndReceive(request, InProgressMatchesSuccessResponse.class);

        printResult(result);

        return result;
    }

    // Get current turn
    public Result<TurnSuccessResponse> getTurn(int ID){

        GetTurnRequest request = new GetTurnRequest(ID);

        // Received message
        Result<TurnSuccessResponse> result = sendRequestAndReceive(request, TurnSuccessResponse.class);

        printResult(result);

        return result;
    }

    // End match
    public Result<EndMatchSuccessResponse> endMatch(int ID, String winner, String loser){
        EndMatchRequest request = new EndMatchRequest(ID, winner, loser);

        Result<EndMatchSuccessResponse> result = sendRequestAndReceive(request, EndMatchSuccessResponse.class);

        printResult(result);

        return result;
    }

    // get game records
    public Result<GameRecordsSuccessResponse> getGameRecords(String nickname){
        GetGameRecordsRequest request = new GetGameRecordsRequest(nickname);

        Result<GameRecordsSuccessResponse> result = sendRequestAndReceive(request, GameRecordsSuccessResponse.class);

        printResult(result);

        return result;
    }

    // request checkmate check
    public Result<CheckmateSuccessResponse> getCheckmate(int matchID) {
        CheckCheckmateRequest request = new CheckCheckmateRequest(matchID);

        Result<CheckmateSuccessResponse> result = sendRequestAndReceive(request, CheckmateSuccessResponse.class);

        printResult(result);

        return result;
    }

    // request forfeit check
    public Result<ForfeitSuccessResponse> getForfeit(int matchID) {
        CheckForfeitRequest request = new CheckForfeitRequest(matchID);

        Result<ForfeitSuccessResponse> result = sendRequestAndReceive(request, ForfeitSuccessResponse.class);

        printResult(result);

        return result;
    }
}
