# Commands Reference

Omega Chess uses a client-server architecture. The desktop client communicates with the server via XER XML messages over a TCP socket connection (see `omega-chess.asn` for the formal schema and `OCMessageFactory` for the Java codec). Below is a reference of all supported server requests.

For full request/response message templates with XER XML examples, see [protocol.md](protocol.md).

## Account Commands

### Register

**Description:** Registers a new user account.
**Parameters:** `email`, `nickname`, `password`
**Example:**
```xml
<RegisterRequest><email>user@example.com</email><nickname>player1</nickname><password>secret</password></RegisterRequest>
```

### Unregister

**Description:** Removes a registered user account.
**Parameters:** `nickname`
**Example:**
```xml
<UnregisterRequest><nickname>player1</nickname></UnregisterRequest>
```

### Login

**Description:** Authenticates a user.
**Parameters:** `nickname`, `password`
**Example:**
```xml
<LoginRequest><nickname>player1</nickname><password>secret</password></LoginRequest>
```

### Get Profile Data

**Description:** Retrieves a user's profile statistics (wins, losses, ties).
**Parameters:** `nickname`
**Example:**
```xml
<GetProfileDataRequest><nickname>player1</nickname></GetProfileDataRequest>
```

## Invitation Commands

### Send Invite

**Description:** Sends a game invitation to another user.
**Parameters:** `inviter`, `invitee`
**Example:**
```xml
<SendInviteRequest><inviter>player1</inviter><invitee>player2</invitee></SendInviteRequest>
```

### Get Sent Invites

**Description:** Returns all invitations sent by a user.
**Parameters:** `user`
**Example:**
```xml
<GetInvitesSentRequest><user>player1</user></GetInvitesSentRequest>
```

### Get Received Invites

**Description:** Returns all invitations received by a user.
**Parameters:** `user`
**Example:**
```xml
<GetInvitesReceivedRequest><user>player1</user></GetInvitesReceivedRequest>
```

### Invite Response

**Description:** Accepts or declines an invitation. If accepted, a new match is created.
**Parameters:** `response` (`accept` or `decline`), `inviter`, `invitee`
**Example:**
```xml
<InviteResponseRequest><response>accept</response><inviter>player1</inviter><invitee>player2</invitee></InviteResponseRequest>
```

## Match Commands

### Get Board Data

**Description:** Returns the current board state (piece positions) for a match.
**Parameters:** `id` (match ID)
**Example:**
```xml
<GetBoardDataRequest><id>1</id></GetBoardDataRequest>
```

### Get Legal Moves

**Description:** Returns the legal moves for a piece at a given board position.
**Parameters:** `matchID`, `row` (0–11), `column` (0–11)
**Example:**
```xml
<GetLegalMovesRequest><matchID>1</matchID><row>3</row><column>4</column></GetLegalMovesRequest>
```

### Match Move

**Description:** Executes a move on the board.
**Parameters:** `matchID`, `fromRow`, `fromColumn`, `toRow`, `toColumn`
**Example:**
```xml
<MatchMoveRequest><matchID>1</matchID><fromRow>1</fromRow><fromColumn>3</fromColumn><toRow>3</toRow><toColumn>3</toColumn></MatchMoveRequest>
```

### Get In-Progress Matches

**Description:** Returns all active matches for a user.
**Parameters:** `nickname`
**Example:**
```xml
<GetInProgressMatchesRequest><nickname>player1</nickname></GetInProgressMatchesRequest>
```

### Get Turn

**Description:** Returns whose turn it is and the turn colour.
**Parameters:** `id` (match ID)
**Example:**
```xml
<GetTurnRequest><id>1</id></GetTurnRequest>
```

### Check Checkmate

**Description:** Checks if the current turn player is in checkmate.
**Parameters:** `id` (match ID)
**Example:**
```xml
<CheckCheckmateRequest><id>1</id></CheckCheckmateRequest>
```

### Check Forfeit

**Description:** Checks if the other player has forfeited the match.
**Parameters:** `id` (match ID)
**Example:**
```xml
<CheckForfeitRequest><id>1</id></CheckForfeitRequest>
```

### End Match

**Description:** Ends a match and records the result.
**Parameters:** `id` (match ID), `winner`, `loser`
**Example:**
```xml
<EndMatchRequest><id>1</id><winner>player1</winner><loser>player2</loser></EndMatchRequest>
```

## Notification Commands

### Get Notifications

**Description:** Returns a user's notifications.
**Parameters:** `nickname`
**Example:**
```xml
<GetNotificationsRequest><nickname>player1</nickname></GetNotificationsRequest>
```

## Record Commands

### Get Game Records

**Description:** Returns the game records of a player, including opponents, results, and move counts.
**Parameters:** `user`
**Example:**
```xml
<GetGameRecordsRequest><user>player1</user></GetGameRecordsRequest>
```
