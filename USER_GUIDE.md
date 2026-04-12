# User Guide

## Prerequisites

- Java JDK 8 or 11 installed on your system
- The Omega Chess project built from source (see [README](README.md#installation))

## First Steps

After building the project, you need to start the server and then launch the desktop client.

### Starting the Server

Run the server JAR or execute `OCMultiServer.main()` from your IDE. The server listens for client connections and manages all game state, user accounts, and matchmaking.

### Launching the Desktop Client

Run the desktop client using `./gradlew desktop:run` or execute `DesktopLauncher.main()` from your IDE.

- Pass `true` as a program argument to connect to a local server.
- Omit the program argument to connect to the production server.

## Common Scenarios

### Registering an Account

1. Launch the desktop client.
2. On the main screen, navigate to the **Register** option.
3. Enter your email, nickname, and password.
4. Submit the registration form.

### Logging In

1. Launch the desktop client.
2. Enter your nickname and password on the login screen.
3. Click **Login**.

### Starting a Game

1. After logging in, navigate to the **Lobby**.
2. Send an invite to another registered user.
3. Wait for the other player to accept the invitation.
4. Once accepted, the match begins automatically.

### Playing a Match

1. Open the match from the **Resume** screen or from a notification.
2. Click on a piece to see its legal moves highlighted on the board.
3. Click on a highlighted square to move the piece.
4. Turns alternate between players.

### Viewing Your Profile

1. Navigate to the **Profile** screen.
2. View your game statistics including wins, losses, and ties.

### Checking Notifications

1. Navigate to the **Mailbox** screen.
2. View invitations and game notifications.

## Omega Chess Rules

Omega Chess is played on a 10×10 playable board with four additional corner squares (104 playable squares total). The implementation represents this using a 12×12 grid that includes off-board/blank cells. In addition to the standard chess pieces, two new piece types are introduced:

- **Champion** – Can move one or two squares orthogonally, or exactly two squares diagonally (jumping over pieces).
- **Wizard** – Can move one square diagonally, or jump to a square that is one square orthogonal and two squares diagonal (an offset "L" shape, different from the knight).

Standard chess rules apply for all other pieces, including castling, en passant, and pawn promotion.
