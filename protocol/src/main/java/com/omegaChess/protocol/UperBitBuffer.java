package com.omegaChess.protocol;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/**
 * Bit-level buffer for UPER encoding and decoding.
 *
 * UPER (Unaligned PER) packs data at the bit level with no alignment padding.
 * This buffer provides methods to write and read constrained/unconstrained
 * integers, booleans, and length-determinant-prefixed octet strings.
 */
public final class UperBitBuffer {

    // ----- Writing state -----
    private ByteArrayOutputStream bos;
    private int currentByte;
    private int bitIndex; // 0-7, number of bits written in currentByte

    // ----- Reading state -----
    private byte[] data;
    private int readBitPos;

    /** Create a buffer for writing. */
    public UperBitBuffer() {
        bos = new ByteArrayOutputStream();
        currentByte = 0;
        bitIndex = 0;
    }

    /** Create a buffer for reading from existing bytes. */
    public UperBitBuffer(byte[] data) {
        this.data = Arrays.copyOf(data, data.length);
        this.readBitPos = 0;
    }

    // ===================== Writing methods =====================

    /** Write a single bit. */
    public void writeBit(boolean bit) {
        if (bit) {
            currentByte |= (1 << (7 - bitIndex));
        }
        bitIndex++;
        if (bitIndex == 8) {
            bos.write(currentByte);
            currentByte = 0;
            bitIndex = 0;
        }
    }

    /**
     * Write {@code numBits} from {@code value}, MSB first.
     * Only the lowest {@code numBits} bits of {@code value} are used.
     */
    public void writeBits(long value, int numBits) {
        for (int i = numBits - 1; i >= 0; i--) {
            writeBit(((value >>> i) & 1) == 1);
        }
    }

    /** Write a boolean as a single bit. */
    public void writeBoolean(boolean value) {
        writeBit(value);
    }

    /**
     * Write a constrained whole number (ASN.1 UPER 12.2.2).
     * Range = max - min + 1. Number of bits = ceil(log2(range)).
     * Value is offset by min before encoding.
     */
    public void writeConstrainedInt(int value, int min, int max) {
        long range = (long) max - (long) min + 1;
        if (range <= 0) {
            throw new IllegalArgumentException("Invalid range: min=" + min + " max=" + max);
        }
        if (value < min || value > max) {
            throw new IllegalArgumentException(
                    "Value " + value + " out of range [" + min + ".." + max + "]");
        }
        int bitsNeeded = bitsForRange(range);
        if (bitsNeeded > 0) {
            writeBits(value - min, bitsNeeded);
        }
    }

    /**
     * Write a semi-constrained integer (non-negative, no upper bound).
     * Uses a 1-byte length determinant (number of octets for the value)
     * followed by the value in that many octets.
     */
    public void writeSemiConstrainedInt(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("Semi-constrained int must be non-negative: " + value);
        }
        int octets = octetsNeeded(value);
        writeBits(octets, 8); // length determinant: 1 byte
        writeBits(value, octets * 8);
    }

    /**
     * Write a UTF-8 string with unconstrained length.
     * Format: 16-bit length prefix (number of UTF-8 octets) + UTF-8 bytes.
     */
    public void writeString(String value) {
        byte[] utf8 = toUtf8(value);
        writeBits(utf8.length, 16); // 16-bit length prefix
        for (byte b : utf8) {
            writeBits(b & 0xFF, 8);
        }
    }

    /**
     * Write a nullable string (OPTIONAL in ASN.1).
     * Writes a presence bit (1=present, 0=absent), then the string if present.
     */
    public void writeOptionalString(String value) {
        if (value != null) {
            writeBit(true);
            writeString(value);
        } else {
            writeBit(false);
        }
    }

    /** Flush any remaining bits (with zero padding) and return the byte array. */
    public byte[] toByteArray() {
        if (bitIndex > 0) {
            bos.write(currentByte);
            currentByte = 0;
            bitIndex = 0;
        }
        return bos.toByteArray();
    }

    // ===================== Reading methods =====================

    /** Read a single bit. */
    public boolean readBit() {
        if (readBitPos >= data.length * 8) {
            throw new RuntimeException("UPER decode: read past end of buffer");
        }
        int byteIdx = readBitPos / 8;
        int bitIdx = 7 - (readBitPos % 8);
        readBitPos++;
        return ((data[byteIdx] >>> bitIdx) & 1) == 1;
    }

    /** Read {@code numBits} bits and return as a long, MSB first. */
    public long readBits(int numBits) {
        long result = 0;
        for (int i = 0; i < numBits; i++) {
            result = (result << 1) | (readBit() ? 1 : 0);
        }
        return result;
    }

    /** Read a boolean (single bit). */
    public boolean readBoolean() {
        return readBit();
    }

    /** Read a constrained whole number (ASN.1 UPER 12.2.2). */
    public int readConstrainedInt(int min, int max) {
        long range = (long) max - (long) min + 1;
        int bitsNeeded = bitsForRange(range);
        if (bitsNeeded == 0) {
            return min;
        }
        long offset = readBits(bitsNeeded);
        int value = (int) (offset + min);
        if (value < min || value > max) {
            throw new RuntimeException(
                    "UPER decode: constrained int " + value + " out of range [" + min + ".." + max + "]");
        }
        return value;
    }

    /** Read a semi-constrained integer. */
    public int readSemiConstrainedInt() {
        int octets = (int) readBits(8);
        if (octets == 0) {
            return 0;
        }
        return (int) readBits(octets * 8);
    }

    /** Read a UTF-8 string with 16-bit length prefix. */
    public String readString() {
        int len = (int) readBits(16);
        byte[] utf8 = new byte[len];
        for (int i = 0; i < len; i++) {
            utf8[i] = (byte) readBits(8);
        }
        return fromUtf8(utf8);
    }

    /** Read a nullable (OPTIONAL) string. */
    public String readOptionalString() {
        if (readBit()) {
            return readString();
        }
        return null;
    }

    // ===================== Helpers =====================

    /** Compute the minimum number of bits needed to represent values in [0, range-1]. */
    private static int bitsForRange(long range) {
        if (range <= 1) {
            return 0;
        }
        int bits = 0;
        long r = range - 1;
        while (r > 0) {
            bits++;
            r >>>= 1;
        }
        return bits;
    }

    /** Compute the minimum number of octets needed for a non-negative int. */
    private static int octetsNeeded(int value) {
        if (value == 0) {
            return 1;
        }
        if (value <= 0xFF) {
            return 1;
        }
        if (value <= 0xFFFF) {
            return 2;
        }
        if (value <= 0xFFFFFF) {
            return 3;
        }
        return 4;
    }

    private static byte[] toUtf8(String s) {
        try {
            return s.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UTF-8 not supported", e);
        }
    }

    private static String fromUtf8(byte[] bytes) {
        try {
            return new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UTF-8 not supported", e);
        }
    }
}
