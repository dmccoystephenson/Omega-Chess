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
| `protocol` | Typed protocol message POJOs and XER XML codec |
| `ios` | iOS launcher (RoboVM backend) |
| `tests` | Test module for project verification and automated checks |

## Building the ASN.1 Codec (Optional)

The protocol is formally defined in `omega-chess.asn` at the repository root. A
pure-Java XER XML codec (`OCMessageFactory`) is included in the `protocol` module
and is used at runtime. The steps below are only required if you wish to build the
optional native C codec (`libasn1omega`) for production PER encoding.

### Prerequisites

- gcc
- make
- autoconf, automake, libtool
- [asn1c (mouse07410 fork)](https://github.com/mouse07410/asn1c)

### Build Steps

```bash
# 1. Clone and install the asn1c compiler
git clone https://github.com/mouse07410/asn1c.git
cd asn1c && autoreconf -iv && ./configure && make && sudo make install
cd ..

# 2. Generate C sources from the ASN.1 schema
cd asn1
make generate

# 3. Build the shared library
make

# 4. The shared library (libasn1omega.so / .dylib / .dll) is now in asn1/
```

Once built, update `OCCodec.java` to load the native library via JNA instead
of delegating to the pure-Java `OCMessageFactory`.
