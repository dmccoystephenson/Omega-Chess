package com.omegaChess.protocol;

import com.omegaChess.protocol.messages.*;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * XER XML codec for the Omega Chess protocol.
 * <p>
 * Serializes / deserializes typed message POJOs to and from single-line XML
 * strings suitable for TCP println/readLine framing.
 */
public final class OCMessageFactory {

    private OCMessageFactory() { }

    // -----------------------------------------------------------------------
    //  Serialization helpers
    // -----------------------------------------------------------------------

    private static Document newDocument() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.newDocument();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create XML document", e);
        }
    }

    private static void addText(Document doc, Element parent, String tag, String value) {
        if (value == null) return;
        Element el = doc.createElement(tag);
        el.setTextContent(value);
        parent.appendChild(el);
    }

    private static void addBool(Document doc, Element parent, String tag, boolean value) {
        addText(doc, parent, tag, Boolean.toString(value));
    }

    private static void addInt(Document doc, Element parent, String tag, int value) {
        addText(doc, parent, tag, Integer.toString(value));
    }

    private static String serialize(Document doc) {
        try {
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.setOutputProperty(OutputKeys.INDENT, "no");
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
            // Strip any stray newlines the transformer may emit
            return writer.toString().replace("\n", "").replace("\r", "");
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize XML", e);
        }
    }

    // -----------------------------------------------------------------------
    //  Deserialization helpers
    // -----------------------------------------------------------------------

    private static Document parse(String xml) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(new InputSource(new StringReader(xml)));
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse XML: " + xml, e);
        }
    }

    private static String getText(Element parent, String tag) {
        Element child = directChild(parent, tag);
        return child != null ? child.getTextContent() : null;
    }

    private static boolean getBool(Element parent, String tag) {
        String v = getText(parent, tag);
        return v != null && Boolean.parseBoolean(v);
    }

    private static int getInt(Element parent, String tag) {
        String v = getText(parent, tag);
        return v != null ? Integer.parseInt(v) : 0;
    }

    /** Return direct child elements with the given tag name. */
    private static List<Element> directChildren(Element parent, String tag) {
        List<Element> result = new ArrayList<Element>();
        NodeList children = parent.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node n = children.item(i);
            if (n.getNodeType() == Node.ELEMENT_NODE && n.getNodeName().equals(tag)) {
                result.add((Element) n);
            }
        }
        return result;
    }

    /** Return the first direct child element with the given tag name, or null. */
    private static Element directChild(Element parent, String tag) {
        List<Element> children = directChildren(parent, tag);
        return children.isEmpty() ? null : children.get(0);
    }

    // -----------------------------------------------------------------------
    //  toXml
    // -----------------------------------------------------------------------

    public static String toXml(Object message) {
        if (message == null) throw new IllegalArgumentException("message must not be null");

        Document doc = newDocument();
        Element root;

        // ---- Requests ----

        if (message instanceof SquareRequest) {
            SquareRequest m = (SquareRequest) message;
            root = doc.createElement("SquareRequest");
            addInt(doc, root, "number", m.getNumber());

        } else if (message instanceof RegisterRequest) {
            RegisterRequest m = (RegisterRequest) message;
            root = doc.createElement("RegisterRequest");
            addText(doc, root, "email", m.getEmail());
            addText(doc, root, "nickname", m.getNickname());
            addText(doc, root, "password", m.getPassword());

        } else if (message instanceof UnregisterRequest) {
            UnregisterRequest m = (UnregisterRequest) message;
            root = doc.createElement("UnregisterRequest");
            addText(doc, root, "nickname", m.getNickname());

        } else if (message instanceof LoginRequest) {
            LoginRequest m = (LoginRequest) message;
            root = doc.createElement("LoginRequest");
            addText(doc, root, "nickname", m.getNickname());
            addText(doc, root, "password", m.getPassword());

        } else if (message instanceof GetProfileDataRequest) {
            GetProfileDataRequest m = (GetProfileDataRequest) message;
            root = doc.createElement("GetProfileDataRequest");
            addText(doc, root, "nickname", m.getNickname());

        } else if (message instanceof SendInviteRequest) {
            SendInviteRequest m = (SendInviteRequest) message;
            root = doc.createElement("SendInviteRequest");
            addText(doc, root, "inviter", m.getInviter());
            addText(doc, root, "invitee", m.getInvitee());

        } else if (message instanceof GetInvitesSentRequest) {
            GetInvitesSentRequest m = (GetInvitesSentRequest) message;
            root = doc.createElement("GetInvitesSentRequest");
            addText(doc, root, "user", m.getUser());

        } else if (message instanceof GetInvitesReceivedRequest) {
            GetInvitesReceivedRequest m = (GetInvitesReceivedRequest) message;
            root = doc.createElement("GetInvitesReceivedRequest");
            addText(doc, root, "user", m.getUser());

        } else if (message instanceof GetNotificationsRequest) {
            GetNotificationsRequest m = (GetNotificationsRequest) message;
            root = doc.createElement("GetNotificationsRequest");
            addText(doc, root, "nickname", m.getNickname());

        } else if (message instanceof InviteResponseRequest) {
            InviteResponseRequest m = (InviteResponseRequest) message;
            root = doc.createElement("InviteResponseRequest");
            addText(doc, root, "response", m.getResponse());
            addText(doc, root, "inviter", m.getInviter());
            addText(doc, root, "invitee", m.getInvitee());

        } else if (message instanceof GetBoardDataRequest) {
            GetBoardDataRequest m = (GetBoardDataRequest) message;
            root = doc.createElement("GetBoardDataRequest");
            addInt(doc, root, "id", m.getId());

        } else if (message instanceof GetLegalMovesRequest) {
            GetLegalMovesRequest m = (GetLegalMovesRequest) message;
            root = doc.createElement("GetLegalMovesRequest");
            addInt(doc, root, "matchID", m.getMatchID());
            addInt(doc, root, "row", m.getRow());
            addInt(doc, root, "column", m.getColumn());

        } else if (message instanceof MatchMoveRequest) {
            MatchMoveRequest m = (MatchMoveRequest) message;
            root = doc.createElement("MatchMoveRequest");
            addInt(doc, root, "matchID", m.getMatchID());
            addInt(doc, root, "fromRow", m.getFromRow());
            addInt(doc, root, "fromColumn", m.getFromColumn());
            addInt(doc, root, "toRow", m.getToRow());
            addInt(doc, root, "toColumn", m.getToColumn());

        } else if (message instanceof GetInProgressMatchesRequest) {
            GetInProgressMatchesRequest m = (GetInProgressMatchesRequest) message;
            root = doc.createElement("GetInProgressMatchesRequest");
            addText(doc, root, "nickname", m.getNickname());

        } else if (message instanceof GetTurnRequest) {
            GetTurnRequest m = (GetTurnRequest) message;
            root = doc.createElement("GetTurnRequest");
            addInt(doc, root, "id", m.getId());

        } else if (message instanceof GetGameRecordsRequest) {
            GetGameRecordsRequest m = (GetGameRecordsRequest) message;
            root = doc.createElement("GetGameRecordsRequest");
            addText(doc, root, "user", m.getUser());

        } else if (message instanceof EndMatchRequest) {
            EndMatchRequest m = (EndMatchRequest) message;
            root = doc.createElement("EndMatchRequest");
            addInt(doc, root, "id", m.getId());
            addText(doc, root, "winner", m.getWinner());
            addText(doc, root, "loser", m.getLoser());

        } else if (message instanceof CheckCheckmateRequest) {
            CheckCheckmateRequest m = (CheckCheckmateRequest) message;
            root = doc.createElement("CheckCheckmateRequest");
            addInt(doc, root, "id", m.getId());

        } else if (message instanceof CheckForfeitRequest) {
            CheckForfeitRequest m = (CheckForfeitRequest) message;
            root = doc.createElement("CheckForfeitRequest");
            addInt(doc, root, "id", m.getId());

        // ---- Responses ----

        } else if (message instanceof FailureResponse) {
            // Check FailureResponse before SimpleSuccessResponse
            FailureResponse m = (FailureResponse) message;
            root = doc.createElement("FailureResponse");
            addBool(doc, root, "success", m.isSuccess());
            addText(doc, root, "reason", m.getReason());

        } else if (message instanceof SquareSuccessResponse) {
            SquareSuccessResponse m = (SquareSuccessResponse) message;
            root = doc.createElement("SquareSuccessResponse");
            addBool(doc, root, "success", m.isSuccess());
            addText(doc, root, "answer", m.getAnswer());

        } else if (message instanceof ProfileDataSuccessResponse) {
            ProfileDataSuccessResponse m = (ProfileDataSuccessResponse) message;
            root = doc.createElement("ProfileDataSuccessResponse");
            addBool(doc, root, "success", m.isSuccess());
            addText(doc, root, "nickname", m.getNickname());
            addInt(doc, root, "gamesWon", m.getGamesWon());
            addInt(doc, root, "gamesLost", m.getGamesLost());
            addInt(doc, root, "gamesTied", m.getGamesTied());

        } else if (message instanceof InviteListSuccessResponse) {
            InviteListSuccessResponse m = (InviteListSuccessResponse) message;
            root = doc.createElement("InviteListSuccessResponse");
            addBool(doc, root, "success", m.isSuccess());
            addInt(doc, root, "amount", m.getAmount());
            addInt(doc, root, "totalCount", m.getTotalCount());
            addInt(doc, root, "maxNicknameLength", m.getMaxNicknameLength());
            Element invites = doc.createElement("invites");
            for (InviteRecord rec : m.getInvites()) {
                Element entry = doc.createElement("InviteRecord");
                addText(doc, entry, "inviter", rec.getInviter());
                addText(doc, entry, "invitee", rec.getInvitee());
                addBool(doc, entry, "accepted", rec.isAccepted());
                addBool(doc, entry, "declined", rec.isDeclined());
                invites.appendChild(entry);
            }
            root.appendChild(invites);

        } else if (message instanceof NotificationsSuccessResponse) {
            NotificationsSuccessResponse m = (NotificationsSuccessResponse) message;
            root = doc.createElement("NotificationsSuccessResponse");
            addBool(doc, root, "success", m.isSuccess());
            addInt(doc, root, "count", m.getCount());
            Element notifications = doc.createElement("notifications");
            for (NotificationRecord rec : m.getNotifications()) {
                Element entry = doc.createElement("NotificationRecord");
                addText(doc, entry, "event", rec.getEvent());
                addText(doc, entry, "message", rec.getMessage());
                addText(doc, entry, "dateString", rec.getDateString());
                notifications.appendChild(entry);
            }
            root.appendChild(notifications);

        } else if (message instanceof InviteResponseSuccessResponse) {
            InviteResponseSuccessResponse m = (InviteResponseSuccessResponse) message;
            root = doc.createElement("InviteResponseSuccessResponse");
            addBool(doc, root, "success", m.isSuccess());
            addText(doc, root, "matchID", m.getMatchID());

        } else if (message instanceof BoardDataSuccessResponse) {
            BoardDataSuccessResponse m = (BoardDataSuccessResponse) message;
            root = doc.createElement("BoardDataSuccessResponse");
            addBool(doc, root, "success", m.isSuccess());
            Element pieces = doc.createElement("pieces");
            for (PieceEntry pe : m.getPieces()) {
                Element entry = doc.createElement("PieceEntry");
                addText(doc, entry, "position", pe.getPosition());
                addText(doc, entry, "piece", pe.getPiece());
                pieces.appendChild(entry);
            }
            root.appendChild(pieces);

        } else if (message instanceof LegalMovesSuccessResponse) {
            LegalMovesSuccessResponse m = (LegalMovesSuccessResponse) message;
            root = doc.createElement("LegalMovesSuccessResponse");
            addBool(doc, root, "success", m.isSuccess());
            addText(doc, root, "legalMoves", m.getLegalMoves());
            addBool(doc, root, "enPassant", m.isEnPassant());

        } else if (message instanceof InProgressMatchesSuccessResponse) {
            InProgressMatchesSuccessResponse m = (InProgressMatchesSuccessResponse) message;
            root = doc.createElement("InProgressMatchesSuccessResponse");
            addBool(doc, root, "success", m.isSuccess());
            addInt(doc, root, "count", m.getCount());
            Element matches = doc.createElement("matches");
            for (MatchSummary ms : m.getMatches()) {
                Element entry = doc.createElement("MatchSummary");
                addText(doc, entry, "opponentNickname", ms.getOpponentNickname());
                addInt(doc, entry, "matchID", ms.getMatchID());
                addInt(doc, entry, "playerIndex", ms.getPlayerIndex());
                matches.appendChild(entry);
            }
            root.appendChild(matches);

        } else if (message instanceof TurnSuccessResponse) {
            TurnSuccessResponse m = (TurnSuccessResponse) message;
            root = doc.createElement("TurnSuccessResponse");
            addBool(doc, root, "success", m.isSuccess());
            addText(doc, root, "user", m.getUser());
            addText(doc, root, "color", m.getColor());

        } else if (message instanceof GameRecordsSuccessResponse) {
            GameRecordsSuccessResponse m = (GameRecordsSuccessResponse) message;
            root = doc.createElement("GameRecordsSuccessResponse");
            addBool(doc, root, "success", m.isSuccess());
            addInt(doc, root, "number", m.getNumber());
            Element records = doc.createElement("records");
            for (GameRecordEntry gre : m.getRecords()) {
                Element entry = doc.createElement("GameRecordEntry");
                addText(doc, entry, "opponentNickname", gre.getOpponentNickname());
                addText(doc, entry, "result", gre.getResult());
                addInt(doc, entry, "moves", gre.getMoves());
                records.appendChild(entry);
            }
            root.appendChild(records);

        } else if (message instanceof EndMatchSuccessResponse) {
            EndMatchSuccessResponse m = (EndMatchSuccessResponse) message;
            root = doc.createElement("EndMatchSuccessResponse");
            addBool(doc, root, "success", m.isSuccess());
            addText(doc, root, "archiveID", m.getArchiveID());

        } else if (message instanceof CheckmateSuccessResponse) {
            CheckmateSuccessResponse m = (CheckmateSuccessResponse) message;
            root = doc.createElement("CheckmateSuccessResponse");
            addBool(doc, root, "success", m.isSuccess());
            addBool(doc, root, "checkmate", m.isCheckmate());
            addText(doc, root, "loser", m.getLoser());
            addText(doc, root, "winner", m.getWinner());

        } else if (message instanceof ForfeitSuccessResponse) {
            ForfeitSuccessResponse m = (ForfeitSuccessResponse) message;
            root = doc.createElement("ForfeitSuccessResponse");
            addBool(doc, root, "success", m.isSuccess());
            addBool(doc, root, "forfeit", m.isForfeit());

        } else if (message instanceof SimpleSuccessResponse) {
            SimpleSuccessResponse m = (SimpleSuccessResponse) message;
            root = doc.createElement("SimpleSuccessResponse");
            addBool(doc, root, "success", m.isSuccess());

        } else {
            throw new IllegalArgumentException("Unsupported message type: " + message.getClass().getName());
        }

        doc.appendChild(root);
        return serialize(doc);
    }

    // -----------------------------------------------------------------------
    //  fromXml
    // -----------------------------------------------------------------------

    public static Object fromXml(String xml) {
        if (xml == null || xml.isEmpty()) {
            throw new IllegalArgumentException("xml must not be null or empty");
        }

        Document doc = parse(xml);
        Element root = doc.getDocumentElement();
        String type = root.getTagName();

        switch (type) {

            // ---- Requests ----

            case "SquareRequest":
                return new SquareRequest(getInt(root, "number"));

            case "RegisterRequest":
                return new RegisterRequest(
                        getText(root, "email"),
                        getText(root, "nickname"),
                        getText(root, "password"));

            case "UnregisterRequest":
                return new UnregisterRequest(getText(root, "nickname"));

            case "LoginRequest":
                return new LoginRequest(
                        getText(root, "nickname"),
                        getText(root, "password"));

            case "GetProfileDataRequest":
                return new GetProfileDataRequest(getText(root, "nickname"));

            case "SendInviteRequest":
                return new SendInviteRequest(
                        getText(root, "inviter"),
                        getText(root, "invitee"));

            case "GetInvitesSentRequest":
                return new GetInvitesSentRequest(getText(root, "user"));

            case "GetInvitesReceivedRequest":
                return new GetInvitesReceivedRequest(getText(root, "user"));

            case "GetNotificationsRequest":
                return new GetNotificationsRequest(getText(root, "nickname"));

            case "InviteResponseRequest":
                return new InviteResponseRequest(
                        getText(root, "response"),
                        getText(root, "inviter"),
                        getText(root, "invitee"));

            case "GetBoardDataRequest":
                return new GetBoardDataRequest(getInt(root, "id"));

            case "GetLegalMovesRequest":
                return new GetLegalMovesRequest(
                        getInt(root, "matchID"),
                        getInt(root, "row"),
                        getInt(root, "column"));

            case "MatchMoveRequest":
                return new MatchMoveRequest(
                        getInt(root, "matchID"),
                        getInt(root, "fromRow"),
                        getInt(root, "fromColumn"),
                        getInt(root, "toRow"),
                        getInt(root, "toColumn"));

            case "GetInProgressMatchesRequest":
                return new GetInProgressMatchesRequest(getText(root, "nickname"));

            case "GetTurnRequest":
                return new GetTurnRequest(getInt(root, "id"));

            case "GetGameRecordsRequest":
                return new GetGameRecordsRequest(getText(root, "user"));

            case "EndMatchRequest":
                return new EndMatchRequest(
                        getInt(root, "id"),
                        getText(root, "winner"),
                        getText(root, "loser"));

            case "CheckCheckmateRequest":
                return new CheckCheckmateRequest(getInt(root, "id"));

            case "CheckForfeitRequest":
                return new CheckForfeitRequest(getInt(root, "id"));

            // ---- Responses ----

            case "SimpleSuccessResponse":
                return new SimpleSuccessResponse();

            case "FailureResponse":
                return new FailureResponse(getText(root, "reason"));

            case "SquareSuccessResponse":
                return new SquareSuccessResponse(getText(root, "answer"));

            case "ProfileDataSuccessResponse":
                return new ProfileDataSuccessResponse(
                        getBool(root, "success"),
                        getText(root, "nickname"),
                        getInt(root, "gamesWon"),
                        getInt(root, "gamesLost"),
                        getInt(root, "gamesTied"));

            case "InviteListSuccessResponse": {
                List<InviteRecord> invites = new ArrayList<InviteRecord>();
                Element invitesEl = directChild(root, "invites");
                if (invitesEl != null) {
                    for (Element e : directChildren(invitesEl, "InviteRecord")) {
                        invites.add(new InviteRecord(
                                getText(e, "inviter"),
                                getText(e, "invitee"),
                                getBool(e, "accepted"),
                                getBool(e, "declined")));
                    }
                }
                return new InviteListSuccessResponse(
                        getBool(root, "success"),
                        getInt(root, "amount"),
                        getInt(root, "totalCount"),
                        getInt(root, "maxNicknameLength"),
                        invites);
            }

            case "NotificationsSuccessResponse": {
                List<NotificationRecord> notifications = new ArrayList<NotificationRecord>();
                Element notificationsEl = directChild(root, "notifications");
                if (notificationsEl != null) {
                    for (Element e : directChildren(notificationsEl, "NotificationRecord")) {
                        notifications.add(new NotificationRecord(
                                getText(e, "event"),
                                getText(e, "message"),
                                getText(e, "dateString")));
                    }
                }
                return new NotificationsSuccessResponse(
                        getBool(root, "success"),
                        getInt(root, "count"),
                        notifications);
            }

            case "InviteResponseSuccessResponse":
                return new InviteResponseSuccessResponse(
                        getBool(root, "success"),
                        getText(root, "matchID"));

            case "BoardDataSuccessResponse": {
                List<PieceEntry> pieces = new ArrayList<PieceEntry>();
                Element piecesEl = directChild(root, "pieces");
                if (piecesEl != null) {
                    for (Element e : directChildren(piecesEl, "PieceEntry")) {
                        pieces.add(new PieceEntry(
                                getText(e, "position"),
                                getText(e, "piece")));
                    }
                }
                return new BoardDataSuccessResponse(
                        getBool(root, "success"),
                        pieces);
            }

            case "LegalMovesSuccessResponse":
                return new LegalMovesSuccessResponse(
                        getBool(root, "success"),
                        getText(root, "legalMoves"),
                        getBool(root, "enPassant"));

            case "InProgressMatchesSuccessResponse": {
                List<MatchSummary> matches = new ArrayList<MatchSummary>();
                Element matchesEl = directChild(root, "matches");
                if (matchesEl != null) {
                    for (Element e : directChildren(matchesEl, "MatchSummary")) {
                        matches.add(new MatchSummary(
                                getText(e, "opponentNickname"),
                                getInt(e, "matchID"),
                                getInt(e, "playerIndex")));
                    }
                }
                return new InProgressMatchesSuccessResponse(
                        getBool(root, "success"),
                        getInt(root, "count"),
                        matches);
            }

            case "TurnSuccessResponse":
                return new TurnSuccessResponse(
                        getBool(root, "success"),
                        getText(root, "user"),
                        getText(root, "color"));

            case "GameRecordsSuccessResponse": {
                List<GameRecordEntry> records = new ArrayList<GameRecordEntry>();
                Element recordsEl = directChild(root, "records");
                if (recordsEl != null) {
                    for (Element e : directChildren(recordsEl, "GameRecordEntry")) {
                        records.add(new GameRecordEntry(
                                getText(e, "opponentNickname"),
                                getText(e, "result"),
                                getInt(e, "moves")));
                    }
                }
                return new GameRecordsSuccessResponse(
                        getBool(root, "success"),
                        getInt(root, "number"),
                        records);
            }

            case "EndMatchSuccessResponse":
                return new EndMatchSuccessResponse(
                        getBool(root, "success"),
                        getText(root, "archiveID"));

            case "CheckmateSuccessResponse":
                return new CheckmateSuccessResponse(
                        getBool(root, "success"),
                        getBool(root, "checkmate"),
                        getText(root, "loser"),
                        getText(root, "winner"));

            case "ForfeitSuccessResponse":
                return new ForfeitSuccessResponse(
                        getBool(root, "success"),
                        getBool(root, "forfeit"));

            default:
                throw new IllegalArgumentException("Unknown message type: " + type);
        }
    }
}
