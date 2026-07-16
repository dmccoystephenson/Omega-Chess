# ---- Build stage ----
FROM eclipse-temurin:11-jdk AS build

RUN apt-get update && \
    apt-get install -y --no-install-recommends gcc make libc6-dev && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Copy Gradle wrapper and build config first (layer caching)
COPY gradlew settings.gradle build.gradle gradle.properties ./
COPY gradle/ gradle/

# Copy source modules
COPY protocol/ protocol/
COPY server/ server/
COPY core/ core/
COPY desktop/ desktop/
COPY ios/ ios/

# Copy ASN.1 schema and native codec sources
COPY omega-chess.asn ./
COPY asn1/ asn1/

RUN chmod +x gradlew

# Build the native ASN.1 codec
RUN cd asn1 && make

# Build the server runtime distribution (skip tests — they run in CI).
# installDist produces a lib/ directory containing the server JAR plus
# all runtime dependencies (protocol, JNA, etc.) with stable paths.
RUN ./gradlew :server:installDist --no-daemon

# ---- Runtime stage ----
FROM eclipse-temurin:11-jre

WORKDIR /app

# Copy all runtime JARs from the Gradle install distribution
COPY --from=build /app/server/build/install/server/lib/ /app/lib/

# Copy the native codec library
COPY --from=build /app/asn1/libasn1omega.so /app/lib/libasn1omega.so

ENV LD_LIBRARY_PATH=/app/lib

# Server data is persisted here — mount a volume for persistence
VOLUME /app/server-data

EXPOSE 8484

ENTRYPOINT ["java", "-cp", "/app/lib/*", "com.omegaChess.server.OCMultiServer"]
