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
| `ios` | iOS launcher (RoboVM backend) |
| `tests` | Test module for project verification and automated checks |
