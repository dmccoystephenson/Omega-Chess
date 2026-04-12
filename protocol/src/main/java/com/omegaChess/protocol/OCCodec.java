package com.omegaChess.protocol;

/**
 * JNA bindings to libasn1omega.
 *
 * Each pair of methods (encode / decode) corresponds to one top-level
 * ASN.1 PDU defined in omega-chess.asn.
 *
 * Encoding always produces XER (XML Encoding Rules) bytes so that wire
 * messages remain human-readable during development; swap -oxer for -ouper
 * in the Makefile to switch to compact Unaligned PER for production.
 *
 * NOTE: This interface is a placeholder for future native codec integration.
 * The current implementation uses pure-Java XER serialization via
 * {@link OCMessageFactory}. When the native library (libasn1omega) is
 * built from the asn1c-generated C sources, load it via JNA and delegate
 * encoding/decoding to these native methods for better performance and
 * strict ASN.1 compliance.
 *
 * To enable native codec:
 * 1. Build asn1c and generate C sources (see CONFIG.md)
 * 2. Build libasn1omega.so via asn1/Makefile
 * 3. Add JNA dependency to build.gradle
 * 4. Uncomment the JNA loading below and implement the bridge
 *
 * Immutable POJO — Java records require JDK 16+; this project targets JDK 7/8.
 */
public final class OCCodec {

    private OCCodec() {
        // utility class
    }

    // ---------------------------------------------------------------
    // When JNA is available, uncomment the following:
    //
    // import com.sun.jna.Library;
    // import com.sun.jna.Native;
    // import com.sun.jna.Pointer;
    //
    // public interface NativeCodec extends Library {
    //     NativeCodec INSTANCE = Native.load("asn1omega", NativeCodec.class);
    //
    //     Pointer ocmsg_encode(String pduType, byte[] xmlBytes, int xmlLen);
    //     byte[]  ocmsg_decode(String pduType, byte[] perBytes,  int perLen);
    //     void    ocmsg_free(Pointer p);
    // }
    // ---------------------------------------------------------------

    /**
     * Encode a typed message object to its XER XML wire representation.
     * Currently delegates to the pure-Java {@link OCMessageFactory}.
     *
     * @param message a typed protocol message POJO
     * @return single-line XER XML string
     */
    public static String encode(Object message) {
        return OCMessageFactory.toXml(message);
    }

    /**
     * Decode an XER XML wire string into a typed message object.
     * Currently delegates to the pure-Java {@link OCMessageFactory}.
     *
     * @param xml XER XML string received from the wire
     * @return the decoded typed protocol message POJO
     */
    public static Object decode(String xml) {
        return OCMessageFactory.fromXml(xml);
    }
}
