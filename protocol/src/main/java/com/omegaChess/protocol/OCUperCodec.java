package com.omegaChess.protocol;

import java.util.ArrayList;
import java.util.List;

import com.omegaChess.protocol.messages.BoardDataSuccessResponse;
import com.omegaChess.protocol.messages.CheckCheckmateRequest;
import com.omegaChess.protocol.messages.CheckForfeitRequest;
import com.omegaChess.protocol.messages.CheckmateSuccessResponse;
import com.omegaChess.protocol.messages.EndMatchRequest;
import com.omegaChess.protocol.messages.EndMatchSuccessResponse;
import com.omegaChess.protocol.messages.FailureResponse;
import com.omegaChess.protocol.messages.ForfeitSuccessResponse;
import com.omegaChess.protocol.messages.GameRecordEntry;
import com.omegaChess.protocol.messages.GameRecordsSuccessResponse;
import com.omegaChess.protocol.messages.GetBoardDataRequest;
import com.omegaChess.protocol.messages.GetGameRecordsRequest;
import com.omegaChess.protocol.messages.GetInProgressMatchesRequest;
import com.omegaChess.protocol.messages.GetInvitesReceivedRequest;
import com.omegaChess.protocol.messages.GetInvitesSentRequest;
import com.omegaChess.protocol.messages.GetLegalMovesRequest;
import com.omegaChess.protocol.messages.GetNotificationsRequest;
import com.omegaChess.protocol.messages.GetProfileDataRequest;
import com.omegaChess.protocol.messages.GetTurnRequest;
import com.omegaChess.protocol.messages.InProgressMatchesSuccessResponse;
import com.omegaChess.protocol.messages.InviteListSuccessResponse;
import com.omegaChess.protocol.messages.InviteRecord;
import com.omegaChess.protocol.messages.InviteResponseRequest;
import com.omegaChess.protocol.messages.InviteResponseSuccessResponse;
import com.omegaChess.protocol.messages.LegalMovesSuccessResponse;
import com.omegaChess.protocol.messages.LoginRequest;
import com.omegaChess.protocol.messages.MatchMoveRequest;
import com.omegaChess.protocol.messages.MatchSummary;
import com.omegaChess.protocol.messages.NotificationRecord;
import com.omegaChess.protocol.messages.NotificationsSuccessResponse;
import com.omegaChess.protocol.messages.PieceEntry;
import com.omegaChess.protocol.messages.ProfileDataSuccessResponse;
import com.omegaChess.protocol.messages.RegisterRequest;
import com.omegaChess.protocol.messages.SendInviteRequest;
import com.omegaChess.protocol.messages.SimpleSuccessResponse;
import com.omegaChess.protocol.messages.SquareRequest;
import com.omegaChess.protocol.messages.SquareSuccessResponse;
import com.omegaChess.protocol.messages.TurnSuccessResponse;
import com.omegaChess.protocol.messages.UnregisterRequest;

/**
 * UPER (Unaligned Packed Encoding Rules) codec for all Omega Chess
 * protocol message types as defined in omega-chess.asn.
 *
 * <p>Each message type is assigned a numeric tag (CHOICE index 0..33).
 * The encoding format is: 6-bit tag + fields in schema order.
 * OPTIONAL fields are preceded by a 1-bit presence flag.
 * The {@code success} boolean is implicit (always true for success
 * responses, always false for {@link FailureResponse}).
 */
public final class OCUperCodec {

    // Tag constants — requests 0..18, responses 19..33
    private static final int TAG_SQUARE_REQUEST = 0;
    private static final int TAG_REGISTER_REQUEST = 1;
    private static final int TAG_UNREGISTER_REQUEST = 2;
    private static final int TAG_LOGIN_REQUEST = 3;
    private static final int TAG_GET_PROFILE_DATA_REQUEST = 4;
    private static final int TAG_SEND_INVITE_REQUEST = 5;
    private static final int TAG_GET_INVITES_SENT_REQUEST = 6;
    private static final int TAG_GET_INVITES_RECEIVED_REQUEST = 7;
    private static final int TAG_GET_NOTIFICATIONS_REQUEST = 8;
    private static final int TAG_INVITE_RESPONSE_REQUEST = 9;
    private static final int TAG_GET_BOARD_DATA_REQUEST = 10;
    private static final int TAG_GET_LEGAL_MOVES_REQUEST = 11;
    private static final int TAG_MATCH_MOVE_REQUEST = 12;
    private static final int TAG_GET_IN_PROGRESS_MATCHES_REQUEST = 13;
    private static final int TAG_GET_TURN_REQUEST = 14;
    private static final int TAG_GET_GAME_RECORDS_REQUEST = 15;
    private static final int TAG_END_MATCH_REQUEST = 16;
    private static final int TAG_CHECK_CHECKMATE_REQUEST = 17;
    private static final int TAG_CHECK_FORFEIT_REQUEST = 18;

    private static final int TAG_SIMPLE_SUCCESS_RESPONSE = 19;
    private static final int TAG_FAILURE_RESPONSE = 20;
    private static final int TAG_SQUARE_SUCCESS_RESPONSE = 21;
    private static final int TAG_PROFILE_DATA_SUCCESS_RESPONSE = 22;
    private static final int TAG_INVITE_LIST_SUCCESS_RESPONSE = 23;
    private static final int TAG_NOTIFICATIONS_SUCCESS_RESPONSE = 24;
    private static final int TAG_INVITE_RESPONSE_SUCCESS_RESPONSE = 25;
    private static final int TAG_BOARD_DATA_SUCCESS_RESPONSE = 26;
    private static final int TAG_LEGAL_MOVES_SUCCESS_RESPONSE = 27;
    private static final int TAG_IN_PROGRESS_MATCHES_SUCCESS_RESPONSE = 28;
    private static final int TAG_TURN_SUCCESS_RESPONSE = 29;
    private static final int TAG_GAME_RECORDS_SUCCESS_RESPONSE = 30;
    private static final int TAG_END_MATCH_SUCCESS_RESPONSE = 31;
    private static final int TAG_CHECKMATE_SUCCESS_RESPONSE = 32;
    private static final int TAG_FORFEIT_SUCCESS_RESPONSE = 33;

    private static final int TAG_MIN = 0;
    private static final int TAG_MAX = 33;

    private OCUperCodec() { }

    // ======================== Encode ========================

    /**
     * Encode a typed message POJO to UPER binary.
     *
     * @param message a protocol message POJO
     * @return UPER-encoded byte array
     * @throws RuntimeException if the message type is unknown
     */
    public static byte[] encode(Object message) {
        UperBitBuffer buf = new UperBitBuffer();

        // ----- Requests -----
        if (message instanceof SquareRequest) {
            buf.writeConstrainedInt(TAG_SQUARE_REQUEST, TAG_MIN, TAG_MAX);
            SquareRequest m = (SquareRequest) message;
            buf.writeSemiConstrainedInt(m.getNumber());

        } else if (message instanceof RegisterRequest) {
            buf.writeConstrainedInt(TAG_REGISTER_REQUEST, TAG_MIN, TAG_MAX);
            RegisterRequest m = (RegisterRequest) message;
            buf.writeString(m.getEmail());
            buf.writeString(m.getNickname());
            buf.writeString(m.getPassword());

        } else if (message instanceof UnregisterRequest) {
            buf.writeConstrainedInt(TAG_UNREGISTER_REQUEST, TAG_MIN, TAG_MAX);
            UnregisterRequest m = (UnregisterRequest) message;
            buf.writeString(m.getNickname());

        } else if (message instanceof LoginRequest) {
            buf.writeConstrainedInt(TAG_LOGIN_REQUEST, TAG_MIN, TAG_MAX);
            LoginRequest m = (LoginRequest) message;
            buf.writeString(m.getNickname());
            buf.writeString(m.getPassword());

        } else if (message instanceof GetProfileDataRequest) {
            buf.writeConstrainedInt(TAG_GET_PROFILE_DATA_REQUEST, TAG_MIN, TAG_MAX);
            GetProfileDataRequest m = (GetProfileDataRequest) message;
            buf.writeString(m.getNickname());

        } else if (message instanceof SendInviteRequest) {
            buf.writeConstrainedInt(TAG_SEND_INVITE_REQUEST, TAG_MIN, TAG_MAX);
            SendInviteRequest m = (SendInviteRequest) message;
            buf.writeString(m.getInviter());
            buf.writeString(m.getInvitee());

        } else if (message instanceof GetInvitesSentRequest) {
            buf.writeConstrainedInt(TAG_GET_INVITES_SENT_REQUEST, TAG_MIN, TAG_MAX);
            GetInvitesSentRequest m = (GetInvitesSentRequest) message;
            buf.writeString(m.getUser());

        } else if (message instanceof GetInvitesReceivedRequest) {
            buf.writeConstrainedInt(TAG_GET_INVITES_RECEIVED_REQUEST, TAG_MIN, TAG_MAX);
            GetInvitesReceivedRequest m = (GetInvitesReceivedRequest) message;
            buf.writeString(m.getUser());

        } else if (message instanceof GetNotificationsRequest) {
            buf.writeConstrainedInt(TAG_GET_NOTIFICATIONS_REQUEST, TAG_MIN, TAG_MAX);
            GetNotificationsRequest m = (GetNotificationsRequest) message;
            buf.writeString(m.getNickname());

        } else if (message instanceof InviteResponseRequest) {
            buf.writeConstrainedInt(TAG_INVITE_RESPONSE_REQUEST, TAG_MIN, TAG_MAX);
            InviteResponseRequest m = (InviteResponseRequest) message;
            buf.writeString(m.getResponse());
            buf.writeString(m.getInviter());
            buf.writeString(m.getInvitee());

        } else if (message instanceof GetBoardDataRequest) {
            buf.writeConstrainedInt(TAG_GET_BOARD_DATA_REQUEST, TAG_MIN, TAG_MAX);
            GetBoardDataRequest m = (GetBoardDataRequest) message;
            buf.writeSemiConstrainedInt(m.getId());

        } else if (message instanceof GetLegalMovesRequest) {
            buf.writeConstrainedInt(TAG_GET_LEGAL_MOVES_REQUEST, TAG_MIN, TAG_MAX);
            GetLegalMovesRequest m = (GetLegalMovesRequest) message;
            buf.writeSemiConstrainedInt(m.getMatchID());
            buf.writeSemiConstrainedInt(m.getRow());
            buf.writeSemiConstrainedInt(m.getColumn());

        } else if (message instanceof MatchMoveRequest) {
            buf.writeConstrainedInt(TAG_MATCH_MOVE_REQUEST, TAG_MIN, TAG_MAX);
            MatchMoveRequest m = (MatchMoveRequest) message;
            buf.writeSemiConstrainedInt(m.getMatchID());
            buf.writeSemiConstrainedInt(m.getFromRow());
            buf.writeSemiConstrainedInt(m.getFromColumn());
            buf.writeSemiConstrainedInt(m.getToRow());
            buf.writeSemiConstrainedInt(m.getToColumn());

        } else if (message instanceof GetInProgressMatchesRequest) {
            buf.writeConstrainedInt(TAG_GET_IN_PROGRESS_MATCHES_REQUEST, TAG_MIN, TAG_MAX);
            GetInProgressMatchesRequest m = (GetInProgressMatchesRequest) message;
            buf.writeString(m.getNickname());

        } else if (message instanceof GetTurnRequest) {
            buf.writeConstrainedInt(TAG_GET_TURN_REQUEST, TAG_MIN, TAG_MAX);
            GetTurnRequest m = (GetTurnRequest) message;
            buf.writeSemiConstrainedInt(m.getId());

        } else if (message instanceof GetGameRecordsRequest) {
            buf.writeConstrainedInt(TAG_GET_GAME_RECORDS_REQUEST, TAG_MIN, TAG_MAX);
            GetGameRecordsRequest m = (GetGameRecordsRequest) message;
            buf.writeString(m.getUser());

        } else if (message instanceof EndMatchRequest) {
            buf.writeConstrainedInt(TAG_END_MATCH_REQUEST, TAG_MIN, TAG_MAX);
            EndMatchRequest m = (EndMatchRequest) message;
            buf.writeSemiConstrainedInt(m.getId());
            buf.writeString(m.getWinner());
            buf.writeString(m.getLoser());

        } else if (message instanceof CheckCheckmateRequest) {
            buf.writeConstrainedInt(TAG_CHECK_CHECKMATE_REQUEST, TAG_MIN, TAG_MAX);
            CheckCheckmateRequest m = (CheckCheckmateRequest) message;
            buf.writeSemiConstrainedInt(m.getId());

        } else if (message instanceof CheckForfeitRequest) {
            buf.writeConstrainedInt(TAG_CHECK_FORFEIT_REQUEST, TAG_MIN, TAG_MAX);
            CheckForfeitRequest m = (CheckForfeitRequest) message;
            buf.writeSemiConstrainedInt(m.getId());

        // ----- Responses -----
        } else if (message instanceof SimpleSuccessResponse) {
            buf.writeConstrainedInt(TAG_SIMPLE_SUCCESS_RESPONSE, TAG_MIN, TAG_MAX);
            // success is implicit (always true) — no fields to write

        } else if (message instanceof FailureResponse) {
            buf.writeConstrainedInt(TAG_FAILURE_RESPONSE, TAG_MIN, TAG_MAX);
            FailureResponse m = (FailureResponse) message;
            buf.writeString(m.getReason());

        } else if (message instanceof SquareSuccessResponse) {
            buf.writeConstrainedInt(TAG_SQUARE_SUCCESS_RESPONSE, TAG_MIN, TAG_MAX);
            SquareSuccessResponse m = (SquareSuccessResponse) message;
            buf.writeString(m.getAnswer());

        } else if (message instanceof ProfileDataSuccessResponse) {
            buf.writeConstrainedInt(TAG_PROFILE_DATA_SUCCESS_RESPONSE, TAG_MIN, TAG_MAX);
            ProfileDataSuccessResponse m = (ProfileDataSuccessResponse) message;
            buf.writeString(m.getNickname());
            buf.writeSemiConstrainedInt(m.getGamesWon());
            buf.writeSemiConstrainedInt(m.getGamesLost());
            buf.writeSemiConstrainedInt(m.getGamesTied());

        } else if (message instanceof InviteListSuccessResponse) {
            buf.writeConstrainedInt(TAG_INVITE_LIST_SUCCESS_RESPONSE, TAG_MIN, TAG_MAX);
            InviteListSuccessResponse m = (InviteListSuccessResponse) message;
            buf.writeSemiConstrainedInt(m.getAmount());
            buf.writeSemiConstrainedInt(m.getTotalCount());
            buf.writeSemiConstrainedInt(m.getMaxNicknameLength());
            List<InviteRecord> invites = m.getInvites();
            buf.writeSemiConstrainedInt(invites.size());
            for (InviteRecord r : invites) {
                buf.writeString(r.getInviter());
                buf.writeString(r.getInvitee());
                buf.writeBoolean(r.isAccepted());
                buf.writeBoolean(r.isDeclined());
            }

        } else if (message instanceof NotificationsSuccessResponse) {
            buf.writeConstrainedInt(TAG_NOTIFICATIONS_SUCCESS_RESPONSE, TAG_MIN, TAG_MAX);
            NotificationsSuccessResponse m = (NotificationsSuccessResponse) message;
            buf.writeSemiConstrainedInt(m.getCount());
            List<NotificationRecord> notes = m.getNotifications();
            buf.writeSemiConstrainedInt(notes.size());
            for (NotificationRecord r : notes) {
                buf.writeString(r.getEvent());
                buf.writeString(r.getMessage());
                buf.writeString(r.getDateString());
            }

        } else if (message instanceof InviteResponseSuccessResponse) {
            buf.writeConstrainedInt(TAG_INVITE_RESPONSE_SUCCESS_RESPONSE, TAG_MIN, TAG_MAX);
            InviteResponseSuccessResponse m = (InviteResponseSuccessResponse) message;
            buf.writeOptionalString(m.getMatchID());

        } else if (message instanceof BoardDataSuccessResponse) {
            buf.writeConstrainedInt(TAG_BOARD_DATA_SUCCESS_RESPONSE, TAG_MIN, TAG_MAX);
            BoardDataSuccessResponse m = (BoardDataSuccessResponse) message;
            List<PieceEntry> pieces = m.getPieces();
            buf.writeSemiConstrainedInt(pieces.size());
            for (PieceEntry p : pieces) {
                buf.writeString(p.getPosition());
                buf.writeString(p.getPiece());
            }

        } else if (message instanceof LegalMovesSuccessResponse) {
            buf.writeConstrainedInt(TAG_LEGAL_MOVES_SUCCESS_RESPONSE, TAG_MIN, TAG_MAX);
            LegalMovesSuccessResponse m = (LegalMovesSuccessResponse) message;
            buf.writeString(m.getLegalMoves());
            buf.writeBoolean(m.isEnPassant());

        } else if (message instanceof InProgressMatchesSuccessResponse) {
            buf.writeConstrainedInt(TAG_IN_PROGRESS_MATCHES_SUCCESS_RESPONSE, TAG_MIN, TAG_MAX);
            InProgressMatchesSuccessResponse m = (InProgressMatchesSuccessResponse) message;
            buf.writeSemiConstrainedInt(m.getCount());
            List<MatchSummary> matches = m.getMatches();
            buf.writeSemiConstrainedInt(matches.size());
            for (MatchSummary s : matches) {
                buf.writeString(s.getOpponentNickname());
                buf.writeSemiConstrainedInt(s.getMatchID());
                buf.writeSemiConstrainedInt(s.getPlayerIndex());
            }

        } else if (message instanceof TurnSuccessResponse) {
            buf.writeConstrainedInt(TAG_TURN_SUCCESS_RESPONSE, TAG_MIN, TAG_MAX);
            TurnSuccessResponse m = (TurnSuccessResponse) message;
            buf.writeString(m.getUser());
            buf.writeString(m.getColor());

        } else if (message instanceof GameRecordsSuccessResponse) {
            buf.writeConstrainedInt(TAG_GAME_RECORDS_SUCCESS_RESPONSE, TAG_MIN, TAG_MAX);
            GameRecordsSuccessResponse m = (GameRecordsSuccessResponse) message;
            buf.writeSemiConstrainedInt(m.getNumber());
            List<GameRecordEntry> records = m.getRecords();
            buf.writeSemiConstrainedInt(records.size());
            for (GameRecordEntry r : records) {
                buf.writeString(r.getOpponentNickname());
                buf.writeString(r.getResult());
                buf.writeSemiConstrainedInt(r.getMoves());
            }

        } else if (message instanceof EndMatchSuccessResponse) {
            buf.writeConstrainedInt(TAG_END_MATCH_SUCCESS_RESPONSE, TAG_MIN, TAG_MAX);
            EndMatchSuccessResponse m = (EndMatchSuccessResponse) message;
            buf.writeOptionalString(m.getArchiveID());

        } else if (message instanceof CheckmateSuccessResponse) {
            buf.writeConstrainedInt(TAG_CHECKMATE_SUCCESS_RESPONSE, TAG_MIN, TAG_MAX);
            CheckmateSuccessResponse m = (CheckmateSuccessResponse) message;
            buf.writeBoolean(m.isCheckmate());
            buf.writeOptionalString(m.getLoser());
            buf.writeOptionalString(m.getWinner());

        } else if (message instanceof ForfeitSuccessResponse) {
            buf.writeConstrainedInt(TAG_FORFEIT_SUCCESS_RESPONSE, TAG_MIN, TAG_MAX);
            ForfeitSuccessResponse m = (ForfeitSuccessResponse) message;
            buf.writeBoolean(m.isForfeit());

        } else {
            throw new RuntimeException("Unknown message type: " + message.getClass().getName());
        }

        return buf.toByteArray();
    }

    // ======================== Decode ========================

    /**
     * Decode UPER binary back to a typed message POJO.
     *
     * @param data UPER-encoded byte array
     * @return the decoded protocol message POJO
     * @throws RuntimeException if the data is malformed or the tag is unknown
     */
    public static Object decode(byte[] data) {
        UperBitBuffer buf = new UperBitBuffer(data);
        int tag = buf.readConstrainedInt(TAG_MIN, TAG_MAX);

        switch (tag) {
            // ----- Requests -----
            case TAG_SQUARE_REQUEST:
                return new SquareRequest(buf.readSemiConstrainedInt());

            case TAG_REGISTER_REQUEST:
                return new RegisterRequest(buf.readString(), buf.readString(), buf.readString());

            case TAG_UNREGISTER_REQUEST:
                return new UnregisterRequest(buf.readString());

            case TAG_LOGIN_REQUEST:
                return new LoginRequest(buf.readString(), buf.readString());

            case TAG_GET_PROFILE_DATA_REQUEST:
                return new GetProfileDataRequest(buf.readString());

            case TAG_SEND_INVITE_REQUEST:
                return new SendInviteRequest(buf.readString(), buf.readString());

            case TAG_GET_INVITES_SENT_REQUEST:
                return new GetInvitesSentRequest(buf.readString());

            case TAG_GET_INVITES_RECEIVED_REQUEST:
                return new GetInvitesReceivedRequest(buf.readString());

            case TAG_GET_NOTIFICATIONS_REQUEST:
                return new GetNotificationsRequest(buf.readString());

            case TAG_INVITE_RESPONSE_REQUEST:
                return new InviteResponseRequest(buf.readString(), buf.readString(), buf.readString());

            case TAG_GET_BOARD_DATA_REQUEST:
                return new GetBoardDataRequest(buf.readSemiConstrainedInt());

            case TAG_GET_LEGAL_MOVES_REQUEST:
                return new GetLegalMovesRequest(
                        buf.readSemiConstrainedInt(),
                        buf.readSemiConstrainedInt(),
                        buf.readSemiConstrainedInt());

            case TAG_MATCH_MOVE_REQUEST:
                return new MatchMoveRequest(
                        buf.readSemiConstrainedInt(),
                        buf.readSemiConstrainedInt(),
                        buf.readSemiConstrainedInt(),
                        buf.readSemiConstrainedInt(),
                        buf.readSemiConstrainedInt());

            case TAG_GET_IN_PROGRESS_MATCHES_REQUEST:
                return new GetInProgressMatchesRequest(buf.readString());

            case TAG_GET_TURN_REQUEST:
                return new GetTurnRequest(buf.readSemiConstrainedInt());

            case TAG_GET_GAME_RECORDS_REQUEST:
                return new GetGameRecordsRequest(buf.readString());

            case TAG_END_MATCH_REQUEST: {
                int id = buf.readSemiConstrainedInt();
                String winner = buf.readString();
                String loser = buf.readString();
                return new EndMatchRequest(id, winner, loser);
            }

            case TAG_CHECK_CHECKMATE_REQUEST:
                return new CheckCheckmateRequest(buf.readSemiConstrainedInt());

            case TAG_CHECK_FORFEIT_REQUEST:
                return new CheckForfeitRequest(buf.readSemiConstrainedInt());

            // ----- Responses -----
            case TAG_SIMPLE_SUCCESS_RESPONSE:
                return new SimpleSuccessResponse();

            case TAG_FAILURE_RESPONSE:
                return new FailureResponse(buf.readString());

            case TAG_SQUARE_SUCCESS_RESPONSE:
                return new SquareSuccessResponse(buf.readString());

            case TAG_PROFILE_DATA_SUCCESS_RESPONSE: {
                String nickname = buf.readString();
                int gamesWon = buf.readSemiConstrainedInt();
                int gamesLost = buf.readSemiConstrainedInt();
                int gamesTied = buf.readSemiConstrainedInt();
                return new ProfileDataSuccessResponse(true, nickname, gamesWon, gamesLost, gamesTied);
            }

            case TAG_INVITE_LIST_SUCCESS_RESPONSE: {
                int amount = buf.readSemiConstrainedInt();
                int totalCount = buf.readSemiConstrainedInt();
                int maxNicknameLength = buf.readSemiConstrainedInt();
                int listLen = buf.readSemiConstrainedInt();
                List<InviteRecord> invites = new ArrayList<InviteRecord>();
                for (int i = 0; i < listLen; i++) {
                    String inviter = buf.readString();
                    String invitee = buf.readString();
                    boolean accepted = buf.readBoolean();
                    boolean declined = buf.readBoolean();
                    invites.add(new InviteRecord(inviter, invitee, accepted, declined));
                }
                return new InviteListSuccessResponse(true, amount, totalCount, maxNicknameLength, invites);
            }

            case TAG_NOTIFICATIONS_SUCCESS_RESPONSE: {
                int count = buf.readSemiConstrainedInt();
                int listLen = buf.readSemiConstrainedInt();
                List<NotificationRecord> notes = new ArrayList<NotificationRecord>();
                for (int i = 0; i < listLen; i++) {
                    String event = buf.readString();
                    String msg = buf.readString();
                    String dateString = buf.readString();
                    notes.add(new NotificationRecord(event, msg, dateString));
                }
                return new NotificationsSuccessResponse(true, count, notes);
            }

            case TAG_INVITE_RESPONSE_SUCCESS_RESPONSE: {
                String matchID = buf.readOptionalString();
                return new InviteResponseSuccessResponse(true, matchID);
            }

            case TAG_BOARD_DATA_SUCCESS_RESPONSE: {
                int listLen = buf.readSemiConstrainedInt();
                List<PieceEntry> pieces = new ArrayList<PieceEntry>();
                for (int i = 0; i < listLen; i++) {
                    String position = buf.readString();
                    String piece = buf.readString();
                    pieces.add(new PieceEntry(position, piece));
                }
                return new BoardDataSuccessResponse(true, pieces);
            }

            case TAG_LEGAL_MOVES_SUCCESS_RESPONSE: {
                String legalMoves = buf.readString();
                boolean enPassant = buf.readBoolean();
                return new LegalMovesSuccessResponse(true, legalMoves, enPassant);
            }

            case TAG_IN_PROGRESS_MATCHES_SUCCESS_RESPONSE: {
                int count = buf.readSemiConstrainedInt();
                int listLen = buf.readSemiConstrainedInt();
                List<MatchSummary> matches = new ArrayList<MatchSummary>();
                for (int i = 0; i < listLen; i++) {
                    String opponentNickname = buf.readString();
                    int matchID = buf.readSemiConstrainedInt();
                    int playerIndex = buf.readSemiConstrainedInt();
                    matches.add(new MatchSummary(opponentNickname, matchID, playerIndex));
                }
                return new InProgressMatchesSuccessResponse(true, count, matches);
            }

            case TAG_TURN_SUCCESS_RESPONSE: {
                String user = buf.readString();
                String color = buf.readString();
                return new TurnSuccessResponse(true, user, color);
            }

            case TAG_GAME_RECORDS_SUCCESS_RESPONSE: {
                int number = buf.readSemiConstrainedInt();
                int listLen = buf.readSemiConstrainedInt();
                List<GameRecordEntry> records = new ArrayList<GameRecordEntry>();
                for (int i = 0; i < listLen; i++) {
                    String opponentNickname = buf.readString();
                    String result = buf.readString();
                    int moves = buf.readSemiConstrainedInt();
                    records.add(new GameRecordEntry(opponentNickname, result, moves));
                }
                return new GameRecordsSuccessResponse(true, number, records);
            }

            case TAG_END_MATCH_SUCCESS_RESPONSE: {
                String archiveID = buf.readOptionalString();
                return new EndMatchSuccessResponse(true, archiveID);
            }

            case TAG_CHECKMATE_SUCCESS_RESPONSE: {
                boolean checkmate = buf.readBoolean();
                String loser = buf.readOptionalString();
                String winner = buf.readOptionalString();
                return new CheckmateSuccessResponse(true, checkmate, loser, winner);
            }

            case TAG_FORFEIT_SUCCESS_RESPONSE:
                return new ForfeitSuccessResponse(true, buf.readBoolean());

            default:
                throw new RuntimeException("UPER decode: unknown tag " + tag);
        }
    }
}
