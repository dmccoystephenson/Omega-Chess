package com.omegaChess.protocol;

import java.util.Arrays;
import java.util.Base64;

/**
 * Codec for encoding/decoding Omega Chess protocol messages.
 *
 * <p>Wire format: {@code [1-byte type tag] [UPER field payload]}
 * Base64-encoded for TCP text-line transport (println/readLine framing).
 *
 * <p>When the native {@code libasn1omega} shared library is available
 * (built from asn1c-generated C sources via {@code asn1/Makefile}),
 * the codec delegates UPER encoding/decoding to the native library
 * through JNA ({@link NativeAsn1Codec}).  When the native library is
 * not present, the pure-Java {@link OCUperCodec} is used as a fallback.
 */
public final class OCCodec {

    private OCCodec() {
        // utility class
    }

    /**
     * Encode a typed message POJO to a Base64-encoded string
     * suitable for TCP text-line transport.
     *
     * @param message a typed protocol message POJO
     * @return Base64-encoded wire string
     */
    public static String encode(Object message) {
        int tag = OCUperCodec.tagOf(message);
        byte[] fieldBytes;

        if (NativeAsn1Codec.isAvailable()) {
            // Native path: POJO → XER XML → native UPER encode
            String typeName = OCUperCodec.typeNameOf(tag);
            String xer = OCMessageFactory.toXml(message);
            fieldBytes = NativeAsn1Codec.xerToUper(typeName, xer);
            if (fieldBytes == null) {
                // Native encode failed — fall back to Java
                fieldBytes = OCUperCodec.encodeFields(message);
            }
        } else {
            // Java fallback: pure-Java UPER encode
            fieldBytes = OCUperCodec.encodeFields(message);
        }

        // Wire format: [1-byte tag] [field payload]
        byte[] wire = new byte[1 + fieldBytes.length];
        wire[0] = (byte) tag;
        System.arraycopy(fieldBytes, 0, wire, 1, fieldBytes.length);
        return Base64.getEncoder().encodeToString(wire);
    }

    /**
     * Decode a Base64-encoded wire string back to a typed message POJO.
     *
     * @param encoded Base64-encoded wire string received from the network
     * @return the decoded typed protocol message POJO
     */
    public static Object decode(String encoded) {
        byte[] wire = Base64.getDecoder().decode(encoded);
        int tag = wire[0] & 0xFF;
        byte[] fieldBytes = Arrays.copyOfRange(wire, 1, wire.length);

        if (NativeAsn1Codec.isAvailable()) {
            // Native path: UPER bytes → native XER decode → fromXml
            String typeName = OCUperCodec.typeNameOf(tag);
            String xer = NativeAsn1Codec.uperToXer(typeName, fieldBytes);
            if (xer != null) {
                return OCMessageFactory.fromXml(xer);
            }
            // Native decode failed — fall back to Java
        }

        // Java fallback: pure-Java UPER decode
        return OCUperCodec.decodeFields(tag, fieldBytes);
    }

    /**
     * @return {@code true} if the native asn1c codec is loaded and active.
     */
    public static boolean isNativeAvailable() {
        return NativeAsn1Codec.isAvailable();
    }
}
