package com.omegaChess.protocol;

import com.sun.jna.Library;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

/**
 * JNA binding to the native {@code libasn1omega} shared library built from
 * asn1c-generated C sources plus {@code shim.c}.
 *
 * <p>The native library provides two conversion functions:
 * <ul>
 *   <li>{@code ocmsg_xer_to_uper} – XER XML → UPER binary</li>
 *   <li>{@code ocmsg_uper_to_xer} – UPER binary → XER XML</li>
 * </ul>
 *
 * <p>This class loads the native library lazily and exposes a boolean
 * {@link #isAvailable()} check so that callers (e.g. {@link OCCodec})
 * can fall back to the pure-Java {@link OCUperCodec} when the native
 * library is not present.
 */
final class NativeAsn1Codec {

    /**
     * JNA interface mapping the C symbols exported by libasn1omega.
     */
    interface Asn1OmegaLib extends Library {
        Pointer ocmsg_xer_to_uper(String pduType, Pointer xerXml, int xerLen, IntByReference outLen);
        Pointer ocmsg_uper_to_xer(String pduType, Pointer uperBytes, int uperLen, IntByReference outLen);
        void ocmsg_free(Pointer ptr);
    }

    private static volatile Asn1OmegaLib lib;
    private static volatile boolean loaded;
    private static volatile boolean available;

    private NativeAsn1Codec() { }

    /**
     * Attempt to load the native library.  Called once; subsequent calls
     * return immediately.
     */
    private static void ensureLoaded() {
        if (loaded) return;
        synchronized (NativeAsn1Codec.class) {
            if (loaded) return;
            try {
                lib = Native.load("asn1omega", Asn1OmegaLib.class);
                available = true;
            } catch (UnsatisfiedLinkError e) {
                available = false;
            }
            loaded = true;
        }
    }

    /**
     * @return {@code true} if the native library was loaded successfully.
     */
    static boolean isAvailable() {
        ensureLoaded();
        return available;
    }

    /**
     * Encode XER XML to UPER binary using the native asn1c codec.
     *
     * @param pduType ASN.1 type name (e.g. "LoginRequest")
     * @param xerXml  XER XML string
     * @return UPER-encoded byte array, or {@code null} on failure
     */
    static byte[] xerToUper(String pduType, String xerXml) {
        ensureLoaded();
        if (!available) return null;

        byte[] xmlBytes = xerXml.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        Memory xmlMem = new Memory(xmlBytes.length);
        xmlMem.write(0, xmlBytes, 0, xmlBytes.length);

        IntByReference outLen = new IntByReference(0);
        Pointer result = lib.ocmsg_xer_to_uper(pduType, xmlMem, xmlBytes.length, outLen);
        if (result == null) return null;

        try {
            int len = outLen.getValue();
            return result.getByteArray(0, len);
        } finally {
            lib.ocmsg_free(result);
        }
    }

    /**
     * Decode UPER binary to XER XML using the native asn1c codec.
     *
     * @param pduType   ASN.1 type name (e.g. "LoginRequest")
     * @param uperBytes UPER-encoded byte array
     * @return XER XML string, or {@code null} on failure
     */
    static String uperToXer(String pduType, byte[] uperBytes) {
        ensureLoaded();
        if (!available) return null;

        Memory mem = new Memory(uperBytes.length);
        mem.write(0, uperBytes, 0, uperBytes.length);

        IntByReference outLen = new IntByReference(0);
        Pointer result = lib.ocmsg_uper_to_xer(pduType, mem, uperBytes.length, outLen);
        if (result == null) return null;

        try {
            int len = outLen.getValue();
            byte[] xmlBytes = result.getByteArray(0, len);
            return new String(xmlBytes, java.nio.charset.StandardCharsets.UTF_8);
        } finally {
            lib.ocmsg_free(result);
        }
    }
}
