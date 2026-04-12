# Commands Reference

Omega Chess uses a client-server architecture. The desktop client communicates with the server via JSON-style messages over a socket connection. Below is a reference of all supported server requests.

For full request/response message templates, see [protocol.md](protocol.md).

## Account Commands

### Register

**Description:** Registers a new user account.
**Parameters:** `email`, `nickname`, `password`
**Example:** `{ "process": "register", "email": "user@example.com", "nickname": "player1", "password": "secret" }`

### Unregister

**Description:** Removes a registered user account.
**Parameters:** `nickname`
**Example:** `{ "process": "unregister", "nickname": "player1" }`

### Login

**Description:** Authenticates a user.
**Parameters:** `nickname`, `password`
**Example:** `{ "process": "login", "nickname": "player1", "password": "secret" }`

### Get Profile Data

**Description:** Retrieves a user's profile statistics (wins, losses, ties).
**Parameters:** `nickname`
**Example:** `{ "process": "get profile data", "nickname": "player1" }`

## Invitation Commands

### Send Invite

**Description:** Sends a game invitation to another user.
**Parameters:** `inviter`, `invitee`
**Example:** `{ "process": "invite", "inviter": "player1", "invitee": "player2" }`

### Get Sent Invites

**Description:** Returns all invitations sent by a user.
**Parameters:** `user`
**Example:** `{ "process": "invites sent", "user": "player1" }`

### Get Received Invites

**Description:** Returns all invitations received by a user.
**Parameters:** `user`
**Example:** `{ "process": "invites received", "user": "player1" }`

### Invite Response

**Description:** Accepts or declines an invitation. If accepted, a new match is created.
**Parameters:** `response` (`accept` or `decline`), `inviter`, `invitee`
**Example:** `{ "process": "invite response", "response": "accept", "inviter": "player1", "invitee": "player2" }`

## Match Commands

### Get Board Data

**Description:** Returns the current board state (piece positions) for a match.
**Parameters:** `ID` (match ID)
**Example:** `{ "process": "get board data", "ID": "1" }`

### Get Legal Moves

**Description:** Returns the legal moves for a piece at a given board position.
**Parameters:** `matchID`, `row` (0–11), `column` (0–11)
**Example:** `{ "process": "get legal moves", "matchID": "1", "row": "3", "column": "4" }`

### Match Move

**Description:** Executes a move on the board.
**Parameters:** `matchID`, `fromRow`, `fromColumn`, `toRow`, `toColumn`
**Example:** `{ "process": "match move", "matchID": "1", "fromRow": "1", "fromColumn": "3", "toRow": "3", "toColumn": "3" }`

### Get In-Progress Matches

**Description:** Returns all active matches for a user.
**Parameters:** `nickname`
**Example:** `{ "process": "get in-progress matches", "nickname": "player1" }`

### Get Turn

**Description:** Returns whose turn it is and the turn colour.
**Parameters:** `ID` (match ID)
**Example:** `{ "process": "get turn", "ID": "1" }`

### Check Checkmate

**Description:** Checks if the current turn player is in checkmate.
**Parameters:** `ID` (match ID)
**Example:** `{ "process": "checkmate check", "ID": "1" }`

### Check Forfeit

**Description:** Checks if the other player has forfeited the match.
**Parameters:** `ID` (match ID)
**Example:** `{ "process": "forfeit check", "ID": "1" }`

### End Match

**Description:** Ends a match and records the result.
**Parameters:** `ID` (match ID), `winner`, `loser`
**Example:** `{ "process": "end match", "ID": "1", "winner": "player1", "loser": "player2" }`

## Notification Commands

### Get Notifications

**Description:** Returns a user's notifications.
**Parameters:** `nickname`
**Example:** `{ "process": "get notifications", "nickname": "player1" }`

## Record Commands

### Get Game Records

**Description:** Returns the game records of a player, including opponents, results, and move counts.
**Parameters:** `user`
**Example:** `{ "process": "get game records", "user": "player1" }`
