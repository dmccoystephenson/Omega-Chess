# Supported Server Requests

All messages are encoded using UPER (Unaligned Packed Encoding Rules) as defined by
the ASN.1 schema in `omega-chess.asn`. Each message is framed as a 1-byte type tag
followed by the UPER-encoded field payload, then Base64-encoded for TCP text-line
transport via `println()`/`readLine()`.

When the native `libasn1omega` shared library is available (built from asn1c-generated
C sources via `asn1/Makefile`), `OCCodec` delegates UPER encoding/decoding to the
native library through JNA. Otherwise, the pure-Java `OCUperCodec` is used as a
fallback. Both paths produce the same wire format.

**Design decision:** The `process` string field from the original key-value protocol
has been removed. Message type is now implicit in the CHOICE tag, making it redundant.

The XER XML representations below are shown for documentation purposes; the actual
wire format is compact UPER binary (Base64-encoded).

---

## Square (test request)
- This request is intended to be used for testing purposes, usually to verify that communication has been established.

Request:
```xml
<SquareRequest><number>10</number></SquareRequest>
```

Success Response:
```xml
<SquareSuccessResponse><success>true</success><answer>Square of 10 is 100</answer></SquareSuccessResponse>
```

Failure Response:
```xml
<FailureResponse><success>false</success><reason>Wrong input!</reason></FailureResponse>
```

## Register
- This request registers a new user using their nickname, password and email.

Request:
```xml
<RegisterRequest><email>example@gmail.com</email><nickname>examplenick</nickname><password>examplepass</password></RegisterRequest>
```

Success Response:
```xml
<SimpleSuccessResponse><success>true</success></SimpleSuccessResponse>
```

Failure Response:
```xml
<FailureResponse><success>false</success><reason>nickname/email was taken</reason></FailureResponse>
```

## Unregister
- This request unregisters a user using their nickname.

Request:
```xml
<UnregisterRequest><nickname>examplenick</nickname></UnregisterRequest>
```

Success Response:
```xml
<SimpleSuccessResponse><success>true</success></SimpleSuccessResponse>
```

Failure Response:
```xml
<FailureResponse><success>false</success><reason>nickname wasn't found</reason></FailureResponse>
```

## Login
- This request logs in a user using their nickname and password.

Request:
```xml
<LoginRequest><nickname>examplenick</nickname><password>examplepass</password></LoginRequest>
```

Success Response:
```xml
<SimpleSuccessResponse><success>true</success></SimpleSuccessResponse>
```

Failure Response:
```xml
<FailureResponse><success>false</success><reason>wrong password</reason></FailureResponse>
```

## Get Profile Data
- This request returns a user's profile data.

Request:
```xml
<GetProfileDataRequest><nickname>examplenick</nickname></GetProfileDataRequest>
```

Success Response:
```xml
<ProfileDataSuccessResponse><success>true</success><nickname>examplenick</nickname><gamesWon>5</gamesWon><gamesLost>3</gamesLost><gamesTied>1</gamesTied></ProfileDataSuccessResponse>
```

Failure Response:
```xml
<FailureResponse><success>false</success><reason>nickname wasn't found</reason></FailureResponse>
```

## Send Invite
- This request sends an invitation to a user.

Request:
```xml
<SendInviteRequest><inviter>examplenick1</inviter><invitee>examplenick2</invitee></SendInviteRequest>
```

Success Response:
```xml
<SimpleSuccessResponse><success>true</success></SimpleSuccessResponse>
```

Failure Response:
```xml
<FailureResponse><success>false</success><reason>input user doesn't exist</reason></FailureResponse>
```

## Get Sent Invites
- This request returns a user's sent invitations.

Request:
```xml
<GetInvitesSentRequest><user>nickname</user></GetInvitesSentRequest>
```

Success Response:
```xml
<InviteListSuccessResponse><success>true</success><amount>2</amount><totalCount>2</totalCount><maxNicknameLength>10</maxNicknameLength><invites><InviteRecord><inviter>player1</inviter><invitee>player2</invitee><accepted>false</accepted><declined>false</declined></InviteRecord><InviteRecord><inviter>player1</inviter><invitee>player3</invitee><accepted>false</accepted><declined>false</declined></InviteRecord></invites></InviteListSuccessResponse>
```

Failure Response:
```xml
<FailureResponse><success>false</success><reason>target user doesn't exist</reason></FailureResponse>
```

## Get Received Invites
- This request returns a user's received invitations.

Request:
```xml
<GetInvitesReceivedRequest><user>nickname</user></GetInvitesReceivedRequest>
```

Success Response: Same format as Get Sent Invites.

Failure Response:
```xml
<FailureResponse><success>false</success><reason>target user doesn't exist</reason></FailureResponse>
```

## Get Notifications
- This request returns a user's notifications.

Request:
```xml
<GetNotificationsRequest><nickname>examplenick</nickname></GetNotificationsRequest>
```

Success Response:
```xml
<NotificationsSuccessResponse><success>true</success><count>2</count><notifications><NotificationRecord><event>NEW_MATCH</event><message>A match has started!</message><dateString>2024-01-15</dateString></NotificationRecord><NotificationRecord><event>MATCH_ENDED</event><message>The match has ended.</message><dateString>2024-01-16</dateString></NotificationRecord></notifications></NotificationsSuccessResponse>
```

Failure Response:
```xml
<FailureResponse><success>false</success><reason>target user doesn't exist</reason></FailureResponse>
```

## Invite Response
- This request accepts/declines an invitation.

Request:
```xml
<InviteResponseRequest><response>accept</response><inviter>nickname1</inviter><invitee>nickname2</invitee></InviteResponseRequest>
```

Success Response (accept):
```xml
<InviteResponseSuccessResponse><success>true</success><matchID>42</matchID></InviteResponseSuccessResponse>
```

Success Response (decline):
```xml
<InviteResponseSuccessResponse><success>true</success></InviteResponseSuccessResponse>
```

## Get Board Data
- This request returns the data for what pieces are in what spaces on the board for the respective match ID.

Request:
```xml
<GetBoardDataRequest><id>42</id></GetBoardDataRequest>
```

Success Response:
```xml
<BoardDataSuccessResponse><success>true</success><pieces><PieceEntry><position>a1</position><piece>WR</piece></PieceEntry><PieceEntry><position>b1</position><piece>WN</piece></PieceEntry></pieces></BoardDataSuccessResponse>
```

Failure Response:
```xml
<FailureResponse><success>false</success><reason>No match found that has ID=42</reason></FailureResponse>
```

## Get Legal Moves
- This request returns a piece's legal moves.

Request:
```xml
<GetLegalMovesRequest><matchID>42</matchID><row>2</row><column>1</column></GetLegalMovesRequest>
```

Success Response:
```xml
<LegalMovesSuccessResponse><success>true</success><legalMoves>/a3/a4/a5/</legalMoves><enPassant>false</enPassant></LegalMovesSuccessResponse>
```

Failure Response:
```xml
<FailureResponse><success>false</success><reason>no piece at specified position</reason></FailureResponse>
```

## Match Move
- This request sends a move to be made on the server.

Request:
```xml
<MatchMoveRequest><matchID>42</matchID><fromRow>2</fromRow><fromColumn>1</fromColumn><toRow>4</toRow><toColumn>1</toColumn></MatchMoveRequest>
```

Success Response:
```xml
<SimpleSuccessResponse><success>true</success></SimpleSuccessResponse>
```

Failure Response:
```xml
<FailureResponse><success>false</success><reason>invalid move</reason></FailureResponse>
```

## Get In-Progress Matches
- This request returns data for the matches a user is currently in.

Request:
```xml
<GetInProgressMatchesRequest><nickname>exampleNickname</nickname></GetInProgressMatchesRequest>
```

Success Response:
```xml
<InProgressMatchesSuccessResponse><success>true</success><count>2</count><matches><MatchSummary><opponentNickname>opponent1</opponentNickname><matchID>42</matchID><playerIndex>1</playerIndex></MatchSummary><MatchSummary><opponentNickname>opponent2</opponentNickname><matchID>43</matchID><playerIndex>2</playerIndex></MatchSummary></matches></InProgressMatchesSuccessResponse>
```

## Get Turn
- This request returns the name of the player whose turn it is, along with the turn color.

Request:
```xml
<GetTurnRequest><id>42</id></GetTurnRequest>
```

Success Response:
```xml
<TurnSuccessResponse><success>true</success><user>nickname</user><color>White</color></TurnSuccessResponse>
```

Failure Response:
```xml
<FailureResponse><success>false</success><reason>No match found that has ID=42</reason></FailureResponse>
```

## Get Game Records
- This request returns the game records of a player, including who the game was with, the result and how many moves.

Request:
```xml
<GetGameRecordsRequest><user>nickname</user></GetGameRecordsRequest>
```

Success Response:
```xml
<GameRecordsSuccessResponse><success>true</success><number>2</number><records><GameRecordEntry><opponentNickname>opponent1</opponentNickname><result>nickname</result><moves>45</moves></GameRecordEntry><GameRecordEntry><opponentNickname>opponent2</opponentNickname><result>tie</result><moves>60</moves></GameRecordEntry></records></GameRecordsSuccessResponse>
```

Failure Response:
```xml
<FailureResponse><success>false</success><reason>target user doesn't exist</reason></FailureResponse>
```

## End Match
- This request marks the match as ready for completion if it is the first time a match calls end and then ends the match and creates the game record on the second.

Request:
```xml
<EndMatchRequest><id>42</id><winner>winningPlayer</winner><loser>losingPlayer</loser></EndMatchRequest>
```

Success Response (first call — marks for completion):
```xml
<EndMatchSuccessResponse><success>true</success></EndMatchSuccessResponse>
```

Success Response (second call — archives the match):
```xml
<EndMatchSuccessResponse><success>true</success><archiveID>1</archiveID></EndMatchSuccessResponse>
```

Failure Response:
```xml
<FailureResponse><success>false</success><reason>there is no match with ID 42</reason></FailureResponse>
```

## Check Checkmate
- This request checks if the current turn player is in checkmate.

Request:
```xml
<CheckCheckmateRequest><id>42</id></CheckCheckmateRequest>
```

Success Response (no checkmate):
```xml
<CheckmateSuccessResponse><success>true</success><checkmate>false</checkmate></CheckmateSuccessResponse>
```

Success Response (checkmate):
```xml
<CheckmateSuccessResponse><success>true</success><checkmate>true</checkmate><loser>losingPlayer</loser><winner>winningPlayer</winner></CheckmateSuccessResponse>
```

Failure Response:
```xml
<FailureResponse><success>false</success><reason>there is no match with ID 42</reason></FailureResponse>
```

## Check Forfeit
- This request checks if the other player has forfeit the match.

Request:
```xml
<CheckForfeitRequest><id>42</id></CheckForfeitRequest>
```

Success Response:
```xml
<ForfeitSuccessResponse><success>true</success><forfeit>false</forfeit></ForfeitSuccessResponse>
```

Failure Response:
```xml
<FailureResponse><success>false</success><reason>there is no match with ID 42</reason></FailureResponse>
```
 