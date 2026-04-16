# Omega Chess

## Description

Omega Chess is a Java-based application for playing [Omega Chess](https://en.wikipedia.org/wiki/Omega_Chess), a variant of traditional chess played on a 10×10 board with four additional corner squares and two new piece types — the Champion and the Wizard. The project features a desktop client built with [libGDX](https://libgdx.com/) and a multi-threaded server that manages user accounts, matchmaking, and game state.

## Installation

### Desktop Client

1. Clone the repository: `git clone https://github.com/dmccoystephenson/Omega-Chess.git`
2. Build the project: `./gradlew build`
3. Run the desktop client: `./gradlew desktop:run`

### Server

1. Build the project: `./gradlew build`
2. Run the server: `./gradlew :server:run`

### Server (Docker)

1. Copy the sample environment file: `cp sample.env .env`
2. Start the server: `docker compose up -d`
3. Connect the desktop client: `./gradlew desktop:run --args='true'`

See [Configuration Guide](CONFIG.md#docker) for details and environment variable reference.

## Usage

### Documentation

- [User Guide](USER_GUIDE.md) – Getting started and common scenarios
- [Commands Reference](COMMANDS.md) – Server protocol and API reference
- [Configuration Guide](CONFIG.md) – Build and configuration options
- [Server Protocol](protocol.md) – Detailed server request/response specification

### Additional Resources

- [Development Manual](deliverables/devManual.md)

## Support

### Experiencing a bug?

Please fill out a bug report [here](https://github.com/dmccoystephenson/Omega-Chess/issues/new).

- [Known Bugs](https://github.com/dmccoystephenson/Omega-Chess/issues?q=is%3Aissue+is%3Aopen+label%3Abug)

## Contributing

- [CONTRIBUTING.md](CONTRIBUTING.md)

## Testing

### Unit Tests

Linux:

    ./gradlew clean test

Windows:

    .\gradlew.bat clean test

If you see `BUILD SUCCESSFUL`, the tests have passed.

## Development

### Prerequisites

- Java JDK 8 or 11
- [IntelliJ IDEA](https://www.jetbrains.com/idea/) (recommended) or another Java IDE

### Setup

1. Clone the repository: `git clone https://github.com/dmccoystephenson/Omega-Chess.git`
2. Open the project in your IDE.
3. Build the project: `./gradlew build`

### Running Locally

1. Start the server: run `OCMultiServer.main()` from `server/src/main/java/com/omegaChess/server/`.
2. Run the desktop client: run `DesktopLauncher.main()` from `desktop/src/com/csc14/runtimeterrors/game/desktop/`.
3. Pass `true` as a program argument to the desktop launcher to connect to the local server.

## Authors and Acknowledgement

### Developers

| Name | Main Contributions |
|------|--------------------|
| Daniel Stephenson | Project maintainer |
| CS414 Runtime Terrors team | Original development |

## Project Status

This project is in maintenance mode.
