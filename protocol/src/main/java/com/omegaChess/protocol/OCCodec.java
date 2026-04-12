package com.omegaChess.protocol;

import java.util.Base64;

/**
 * Codec for encoding/decoding Omega Chess protocol messages using
 * UPER (Unaligned Packed Encoding Rules) as defined in omega-chess.asn.
 *
 * Messages are UPER-encoded to binary, then Base64-encoded for
 * TCP text-line transport (println/readLine framing).
 */
public final class OCCodec {

    private OCCodec() {
        // utility class
    }

    /**
     * Encode a typed message POJO to a Base64-encoded UPER string
     * suitable for TCP text-line transport.
     *
     * @param message a typed protocol message POJO
     * @return Base64-encoded UPER string
     */
    public static String encode(Object message) {
        byte[] uperBytes = OCUperCodec.encode(message);
        return Base64.getEncoder().encodeToString(uperBytes);
    }

    /**
     * Decode a Base64-encoded UPER string back to a typed message POJO.
     *
     * @param encoded Base64-encoded UPER string received from the wire
     * @return the decoded typed protocol message POJO
     */
    public static Object decode(String encoded) {
        byte[] uperBytes = Base64.getDecoder().decode(encoded);
        return OCUperCodec.decode(uperBytes);
    }
}
