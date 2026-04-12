# Copilot Instructions

This repository follows the DPC (Dans Plugins Community) conventions defined at
https://github.com/Dans-Plugins/dpc-conventions. Read those conventions before
making any changes.

## Technology Stack

- Language: Java
- Build tool: Gradle (Groovy DSL)
- Graphics framework: libGDX
- Test framework: JUnit 5

## Project Structure

- `core/src/` – Shared game logic, UI screens, and libGDX assets
- `desktop/src/` – Desktop launcher (LWJGL backend)
- `server/src/main/java/` – Multi-threaded game server (matchmaking, protocol, game state)
- `server/src/test/java/` – Unit tests for server-side logic and chess pieces
- `core/assets/` – Game assets (textures, skins)

## Coding Conventions

- Follow the existing package structure when adding new classes.
- Server-side chess pieces extend `ChessPiece` in `com.omegaChess.pieces`.
- Board logic is in `com.omegaChess.board`.
- Server protocol handling is in `com.omegaChess.server.OCProtocol`.
- Client-server communication uses comma-delimited `key=value` messages via `OCMessage`.

## Contribution Workflow

- Branch from `develop` for all changes.
- Open a pull request against `develop`, not `main`.
- Reference the related GitHub issue in every pull request description.
