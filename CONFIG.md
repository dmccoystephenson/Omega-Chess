# Configuration Guide

Omega Chess is configured primarily through Gradle build files and command-line arguments. This document describes the available configuration options.

## Build Configuration

Build settings are defined in `build.gradle` and `gradle.properties`.

### gradle.properties

#### org.gradle.daemon

**Type:** boolean
**Default:** `true`
**Description:** Enables the Gradle daemon for faster builds.

```properties
org.gradle.daemon=true
```

#### org.gradle.jvmargs

**Type:** string
**Default:** `-Xms128m -Xmx1500m`
**Description:** JVM arguments for the Gradle daemon. Adjust memory settings if you encounter out-of-memory errors during the build.

```properties
org.gradle.jvmargs=-Xms128m -Xmx1500m
```

#### org.gradle.configureondemand

**Type:** boolean
**Default:** `false`
**Description:** When enabled, Gradle only configures projects that are relevant to the requested tasks.

```properties
org.gradle.configureondemand=false
```

## Desktop Client Configuration

### Server Connection

The desktop client accepts an optional command-line argument to control which server it connects to.

**Argument:** First program argument passed to `DesktopLauncher.main()`
**Type:** string (`true`)
**Default:** argument omitted (connects to the production server)
**Description:** Pass `true` to connect to a local server running on `localhost`. To connect to the production server, omit the argument.

```
# Connect to local server
./gradlew desktop:run --args='true'

# Connect to production server
./gradlew desktop:run
```

## Project Modules

The project is composed of the following Gradle modules, configured in `settings.gradle`:

| Module | Description |
|--------|-------------|
| `core` | Shared game logic and libGDX screens |
| `desktop` | Desktop launcher (LWJGL backend) |
| `server` | Multi-threaded game server |
| `protocol` | Typed protocol message POJOs, UPER codec, and JNA native binding |
| `ios` | iOS launcher (RoboVM backend) |
| `tests` | Test module for project verification and automated checks |

## Building the ASN.1 Native Codec

The protocol is formally defined in `omega-chess.asn` at the repository root. The
`protocol` module includes both a pure-Java UPER codec (`OCUperCodec`) and a JNA
binding to the native C codec (`libasn1omega`) built from asn1c-generated sources.

At runtime, `OCCodec` attempts to load `libasn1omega` via JNA. When the native
library is available, encoding and decoding are delegated to asn1c's UPER
implementation through `NativeAsn1Codec`. When the library is not found, the
codec falls back transparently to the pure-Java `OCUperCodec`.

An XER XML codec (`OCMessageFactory`) is also available for debugging/logging
purposes and is used as the intermediate format for the native codec path
(POJO → XER XML → native UPER encode, and reverse for decode).

### Prerequisites

- gcc
- make
- [asn1c](https://github.com/mouse07410/asn1c) — only needed to regenerate C
  sources (pre-generated sources are checked in under `asn1/generated/`)

### Build Steps

```bash
# Build the shared library from the checked-in generated sources
cd asn1
make

# The shared library (libasn1omega.so / .dylib / .dll) is now in asn1/
```

To regenerate the C sources after modifying `omega-chess.asn`:

```bash
# Requires asn1c on PATH
cd asn1
make generate
make
```

### Using the Native Library

Place `libasn1omega.so` (or `.dylib` / `.dll`) on the JNA library search path
(e.g., `java.library.path`, `LD_LIBRARY_PATH`, or the working directory).
`OCCodec` will automatically detect and use it. Verify with:

```java
boolean nativeActive = OCCodec.isNativeAvailable();
```

## Docker

A `Dockerfile` and `compose.yml` are provided to build and run the server in a container. The image uses a multi-stage build: the first stage compiles the server JAR and the native ASN.1 codec (`libasn1omega.so`), and the second stage produces a minimal runtime image.

### Quick Start

```bash
# 1. Copy the sample environment file
cp sample.env .env

# 2. (Optional) Edit .env to change the host port
#    SERVER_PORT=8484

# 3. Start the server
docker compose up -d

# 4. View logs
docker compose logs -f server

# 5. Stop the server
docker compose down
```

### Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `SERVER_PORT` | `8484` | TCP port exposed on the Docker host |

See `sample.env` for the template.

### Data Persistence

Server data (user profiles, matches, game records) is stored in a Docker named volume `server-data` mounted at `/app/server-data`. The volume survives container restarts and rebuilds. To reset all data, remove the volume:

```bash
docker compose down -v
```

### Building Manually

```bash
# Build the image without Compose
docker build -t omega-chess-server .

# Run the container
docker run -d -p 8484:8484 -v omega-chess-data:/app/server-data omega-chess-server
```

### Connecting the Desktop Client

Pass `true` as a program argument to the desktop launcher to connect to a local server (the containerized server is accessible on `localhost`):

```bash
./gradlew desktop:run --args='true'
```
